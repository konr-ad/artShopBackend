package com.artshop.backend.services;

import com.artshop.backend.api.dto.AddressDto;
import com.artshop.backend.api.dto.CreateOrderRequest;
import com.artshop.backend.models.entity.Address;
import com.artshop.backend.models.entity.Customer;
import com.artshop.backend.models.payu.Order;
import com.artshop.backend.repositories.CustomerRepository;
import com.artshop.backend.repositories.OrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderService {

    @Value("${payu.client-id}")   private String clientId;
    @Value("${payu.client-secret}") private String clientSecret;
    @Value("${payu.auth-url}")    private String authUrl;
    @Value("${payu.order-url}")   private String orderUrl;
    @Value("${payu.continue-url}") private String continueUrl;
    @Value("${payu.notify-url}")   private String notifyUrl;

    private final OrderRepository orderRepository;
    private final CustomerRepository customerRepository;
    private final RestTemplate restTemplate;

    // NOWE: przetwarzanie z CreateOrderRequest
    public Order processOrder(CreateOrderRequest req, String clientIp) {
        // 1) Customer (re-use by e-mail)
        var email = Optional.ofNullable(req.contactEmail())
                .orElseGet(() -> req.buyer() != null ? req.buyer().email() : null);

        if (email == null || email.isBlank()) {
            throw new IllegalArgumentException("Missing contactEmail/buyer.email");
        }

        Customer customer = customerRepository.findByEmail(email)
                .orElseGet(() -> {
                    var c = new Customer();
                    c.setEmail(email);
                    if (req.buyer() != null) {
                        c.setFirstName(req.buyer().firstName());
                        c.setLastName(req.buyer().lastName());
                    }
                    return customerRepository.save(c);
                });

        // 2) Encja Order
        var order = new com.artshop.backend.models.payu.Order();
        order.setCustomer(customer);
        order.setCurrencyCode(Optional.ofNullable(req.currencyCode()).orElse("PLN"));
        order.setExtOrderId(req.extOrderId());
        order.setContactEmail(email);

        if (req.shippingAddress() == null) {
            throw new IllegalArgumentException("shippingAddress is required");
        }
        order.setShippingAddress(toAddress(req.shippingAddress()));

        // 3) Pozycje + suma (po stronie backendu!)
        if (req.products() == null || req.products().isEmpty()) {
            throw new IllegalArgumentException("Order has no products");
        }
        BigDecimal total = BigDecimal.ZERO;
        for (var p : req.products()) {
            if (p.paintingId() == null) {
                throw new IllegalArgumentException("Item.paintingId is required");
            }
            if (p.paintingType() == null || p.paintingType().isBlank()) {
                throw new IllegalArgumentException("Item.paintingType is required");
            }

            var it = new com.artshop.backend.models.payu.OrderItem();
            it.setOrder(order);
            it.setPaintingId(p.paintingId());
            it.setPaintingNameSnapshot(p.name());

            // mapowanie String -> enum (case-insensitive, z czytelnym błędem)
            try {
                it.setPaintingTypeSnapshot(
                        com.artshop.backend.enums.EPaintingType.valueOf(p.paintingType().toUpperCase())
                );
            } catch (IllegalArgumentException ex) {
                throw new IllegalArgumentException("Unsupported paintingType: " + p.paintingType());
            }

            it.setUnitPriceAtPurchase(p.unitPrice());           // PLN
            it.setQuantity(Math.max(1, p.quantity()));

            order.getItems().add(it);
            total = total.add(p.unitPrice().multiply(java.math.BigDecimal.valueOf(it.getQuantity())));
        }
        order.setTotalAmount(total);

        // 4) Zapisz (status NEW)
        order = orderRepository.save(order);

        // 5) PayU
        String token = getAuthToken();
        Map<String, Object> payuResp = createOrderInPayU(order, token, clientIp);

        order.setPayuOrderId((String) payuResp.get("orderId"));
        order.setRedirectUri((String) payuResp.get("redirectUri"));
        // order.setPaymentStatus(EPaymentStatus.PENDING);

        return orderRepository.save(order);
    }

    // Token: x-www-form-urlencoded
    public String getAuthToken() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String,String> form = new LinkedMultiValueMap<>();
        form.add("grant_type", "client_credentials");
        form.add("client_id", clientId);
        form.add("client_secret", clientSecret);

        var resp = restTemplate.postForEntity(authUrl, new HttpEntity<>(form, headers), Map.class);
        var body = Objects.requireNonNull(resp.getBody());
        return Objects.toString(body.get("access_token"), null);
    }

    @SuppressWarnings("unchecked")
    public Map<String, Object> createOrderInPayU(
            com.artshop.backend.models.payu.Order order,
            String token,
            String clientIp
    ) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(token);

        Map<String, Object> body = new LinkedHashMap<>();
        body.put("continueUrl", continueUrl);
        body.put("notifyUrl",   notifyUrl);
        body.put("customerIp",  Optional.ofNullable(clientIp).orElse("127.0.0.1"));
        body.put("merchantPosId", clientId); // sandbox: to samo co clientId

        body.put("description", Optional.ofNullable(order.getItems())
                .orElse(List.of())
                .stream().map(com.artshop.backend.models.payu.OrderItem::getPaintingNameSnapshot)
                .collect(Collectors.joining(" + ")));

        body.put("currencyCode", order.getCurrencyCode());
        body.put("totalAmount", order.getTotalAmount().movePointRight(2).toBigInteger().toString()); // grosze
        if (order.getExtOrderId() != null) body.put("extOrderId", order.getExtOrderId());

        Map<String, String> buyer = new LinkedHashMap<>();
        buyer.put("email", order.getContactEmail());
        if (order.getCustomer() != null) {
            buyer.put("firstName", Optional.ofNullable(order.getCustomer().getFirstName()).orElse(""));
            buyer.put("lastName",  Optional.ofNullable(order.getCustomer().getLastName()).orElse(""));
        }
        body.put("buyer", buyer);

        List<Map<String, String>> products = new ArrayList<>();
        for (var it : order.getItems()) {
            Map<String, String> p = new LinkedHashMap<>();
            p.put("name", it.getPaintingNameSnapshot());
            p.put("unitPrice", it.getUnitPriceAtPurchase().movePointRight(2).toBigInteger().toString()); // grosze
            p.put("quantity", String.valueOf(it.getQuantity()));
            products.add(p);
        }
        body.put("products", products);

        var resp = restTemplate.postForEntity(orderUrl, new HttpEntity<>(body, headers), Map.class);
        return resp.getBody();
    }

    private static Address toAddress(AddressDto dto) {
        Address a = new Address();
        a.setStreet(dto.street());
        a.setApartmentNumber(dto.apartmentNumber());
        a.setCity(dto.city());
        a.setState(dto.state());
        a.setZip(dto.zip());
        a.setCountry(dto.country());
        return a;
    }

    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }
}