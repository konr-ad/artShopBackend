package com.artshop.backend.services;

import com.artshop.backend.exception.EntityNotFoundException;
import com.artshop.backend.models.entity.Customer;
import com.artshop.backend.models.payu.Order;
import com.artshop.backend.models.payu.OrderItem;
import com.artshop.backend.repositories.CustomerRepository;
import com.artshop.backend.repositories.OrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.util.*;

@Slf4j
@RequiredArgsConstructor
@Service
public class OrderService {

    @Value("${payu.client-id}")
    private String clientId;

    @Value("${payu.client-secret}")
    private String clientSecret;

    @Value("${payu.auth-url}")
    private String authUrl;

    @Value("${payu.order-url}")
    private String orderUrl;

    private final OrderRepository orderRepository;
    private final RestTemplate restTemplate;
    private final CustomerRepository customerRepository;


    /**
     * Zostaje Twój flow:
     * - ogarniamy klienta
     * - przypinamy pozycje do zamówienia
     * - zapis do DB
     * - token PayU -> create order -> update redirectUri/payuOrderId
     */
    public Order processOrder(Order order) {
        // 1) Customer reuse albo utworzenie
        Customer customer = order.getCustomer();
        if (customer != null) {
            if (customer.getId() == null || !customerRepository.existsById(customer.getId())) {
                Optional<Customer> existing = customerRepository.findByEmail(customer.getEmail());
                if (existing.isPresent()) {
                    customer = existing.get();
                } else {
                    customer = customerRepository.save(customer);
                }
            }
        } else {
            // jeśli przychodzi tylko email kontaktowy, a nie obiekt customer – możesz dodać prostą logikę
            if (order.getContactEmail() != null && !order.getContactEmail().isBlank()) {
                customer = customerRepository.findFirstByEmailOrderByIdDesc(order.getContactEmail())
                        .orElseGet(() -> {
                            Customer c = new Customer();
                            c.setEmail(order.getContactEmail());
                            return customerRepository.save(c);
                        });
            } else {
                throw new EntityNotFoundException("Missing customer or contactEmail");
            }
        }
        order.setCustomer(customer);

        // 2) Przypnij pozycje do zamówienia (nowy model: OrderItem)
        if (order.getItems() != null && !order.getItems().isEmpty()) {
            for (OrderItem it : order.getItems()) {
                it.setOrder(order);
                if (it.getQuantity() <= 0) it.setQuantity(1);
            }
        } else {
            throw new IllegalArgumentException("Order has no items");
        }

        // 3) Ustal sumę jeśli nie ustawiona (BigDecimal PLN)
        if (order.getTotalAmount() == null) {
            BigDecimal total = order.getItems().stream()
                    .map(OrderItem::lineTotal)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            order.setTotalAmount(total);
        }

        // 4) Zapisz zamówienie (status NEW/PENDING ustawiasz wg swojego enum)
        Order savedOrder = orderRepository.save(order);

        // 5) PayU
        String token = getAuthToken();
        Map<String, Object> response = createOrderInPayU(savedOrder, token);

        // 6) Update pól z odpowiedzi PayU
        savedOrder.setPayuOrderId((String) response.get("orderId"));
        savedOrder.setRedirectUri((String) response.get("redirectUri"));
        // jeśli chcesz mapować status: savedOrder.setPaymentStatus(EPaymentStatus.PENDING);

        log.info("PayU response status: {}", response.get("status"));

        return orderRepository.save(savedOrder);
    }

    // --- poniżej Twoje metody integracji PayU, zachowane w duchu 1:1 ---

    public String getAuthToken() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        Map<String, String> body = new LinkedHashMap<>();
        body.put("grant_type", "client_credentials");
        body.put("client_id", clientId);
        body.put("client_secret", clientSecret);

        HttpEntity<Map<String, String>> request = new HttpEntity<>(body, headers);
        ResponseEntity<Map> response = restTemplate.postForEntity(authUrl, request, Map.class);
        return Objects.requireNonNull(response.getBody()).get("access_token").toString();
    }

    @SuppressWarnings("unchecked")
    public Map<String, Object> createOrderInPayU(Order order, String token) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(token);

        Map<String, Object> body = new LinkedHashMap<>();
        // TODO: przenieś do konfiguracji/profili
        body.put("continueUrl", "http://localhost:4200/thankyou");
        body.put("notifyUrl",   "http://localhost:4200/notify");
        body.put("customerIp", "127.0.0.1");
        body.put("merchantPosId", "482430");

        body.put("description", "testowyOpisDoZMiany");//order.getDescription());
        body.put("currencyCode", order.getCurrencyCode());
        // PayU wymaga stringa w groszach:
        body.put("totalAmount", order.getTotalAmount().movePointRight(2).toBigInteger().toString());
        if (order.getExtOrderId() != null) body.put("extOrderId", order.getExtOrderId());

        Map<String, String> buyer = new LinkedHashMap<>();
        // preferuj contactEmail (dodałeś do Order)
        String email = order.getContactEmail() != null ? order.getContactEmail()
                : (order.getCustomer() != null ? order.getCustomer().getEmail() : "");
        buyer.put("email", email);
        if (order.getCustomer() != null) {
            buyer.put("firstName", Optional.ofNullable(order.getCustomer().getFirstName()).orElse(""));
            buyer.put("lastName",  Optional.ofNullable(order.getCustomer().getLastName()).orElse(""));
        }
        body.put("buyer", buyer);

        List<Map<String, String>> products = new ArrayList<>();
        for (OrderItem it : order.getItems()) {
            Map<String, String> product = new LinkedHashMap<>();
            product.put("name", it.getPaintingNameSnapshot());
            product.put("unitPrice", it.getUnitPriceAtPurchase().movePointRight(2).toBigInteger().toString());
            product.put("quantity", String.valueOf(it.getQuantity()));
            products.add(product);
        }
        body.put("products", products);

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);
        ResponseEntity<Map> response = restTemplate.postForEntity(orderUrl, request, Map.class);
        return response.getBody();
    }
}