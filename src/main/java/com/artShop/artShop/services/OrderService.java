package com.artShop.artShop.services;

import com.artShop.artShop.enums.EPaintingType;
import com.artShop.artShop.models.Customer;
import com.artShop.artShop.models.Painting;
import com.artShop.artShop.models.payu.Order;
import com.artShop.artShop.repositories.CustomerRepository;
import com.artShop.artShop.repositories.OrderRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    private static final Logger logger = LoggerFactory.getLogger(OrderService.class);

    @Autowired
    public OrderService(OrderRepository orderRepository, RestTemplate restTemplate, CustomerRepository customerRepository) {
        this.orderRepository = orderRepository;
        this.restTemplate = restTemplate;
        this.customerRepository = customerRepository;
    }

    public Order processOrder(Order order) {
        Customer customer = order.getCustomer();
        if (customer != null && (customer.getId() == null || !customerRepository.existsById(customer.getId()))) {
            Customer existingCustomer = customerRepository.findByEmail(customer.getEmail());
            if (existingCustomer != null) {
                customer = existingCustomer;
            } else {
                customer = customerRepository.save(customer);
            }
        }
        order.setCustomer(customer);

//        if (order.getPaintingIds() != null && !order.getPaintingIds().isEmpty()) {
//            List<Painting> paintings = paintingRepository.findAllById(order.getPaintingIds());
//            paintings.forEach(painting -> painting.setOrder(order));
//            order.setPaintings(paintings);
//        }

        // Save the order
        Order savedOrder = orderRepository.save(order);

        // Continue with PayU integration
        String token = getAuthToken();
        Map<String, Object> response = createOrderInPayU(savedOrder, token);

        // Extract and update necessary information from the response
        savedOrder.setPayuOrderId((String) response.get("orderId"));
        savedOrder.setRedirectUri((String) response.get("redirectUri"));
        savedOrder.setPaymentStatus((String) ((Map) response.get("status")).get("statusCode")); //to nie jest paymentStatus
        logger.info("response.get(\"status\"): {}", response.get("status"));
        logger.info("response.get(\"status\"): {}", response.get("statusCode"));
        // Save updated order to the database
        return orderRepository.save(savedOrder);
    }

    public String getAuthToken() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("grant_type", "client_credentials");
        body.add("client_id", clientId);
        body.add("client_secret", clientSecret);

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(body, headers);

        ResponseEntity<Map> response = restTemplate.postForEntity(authUrl, request, Map.class);

        return response.getBody().get("access_token").toString();
    }

    public Map<String, Object> createOrderInPayU(Order order, String token) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(token);

        Map<String, Object> body = new HashMap<>();
        body.put("continueUrl", "http://25.53.71.208:4200/thankyou");
        body.put("notifyUrl", "http://25.53.71.208:4200/notify");
        body.put("customerIp", "127.0.0.1");
        body.put("merchantPosId", "482430");
        body.put("description", order.getDescription());
        body.put("currencyCode", order.getCurrencyCode());
        body.put("totalAmount", order.getTotalAmount());
        body.put("extOrderId", order.getExtOrderId());

        Map<String, String> buyer = new HashMap<>();
        buyer.put("email", order.getCustomer().getEmail());
        buyer.put("firstName", order.getCustomer().getFirstName());
        buyer.put("lastName", order.getCustomer().getLastName());
        body.put("buyer", buyer);

        List<Map<String, String>> products = new ArrayList<>();
        if (order.getPaintings() != null) {
            for (Painting painting : order.getPaintings()) {
                String quantity = "1";
                Map<String, String> product = new HashMap<>();
                product.put("name", painting.getName());
                product.put("unitPrice", String.valueOf((int) Math.round(painting.getPrice() * 100)));
                if (painting.getType().equals(EPaintingType.PRINT)) {
                    quantity = painting.getQuantity();
                }
                product.put("quantity", quantity);
                products.add(product);
            }
        }
        body.put("products", products);

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);

        ResponseEntity<Map> response = restTemplate.postForEntity(orderUrl, request, Map.class);
        return response.getBody();
    }

    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }
}
