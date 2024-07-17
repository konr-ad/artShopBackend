package com.artShop.artShop.services;

import com.artShop.artShop.models.payu.Order;
import com.artShop.artShop.models.payu.OrderItem;
import com.artShop.artShop.repositories.OrderRepository;
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

    @Autowired
    public OrderService(OrderRepository orderRepository, RestTemplate restTemplate) {
        this.orderRepository = orderRepository;
        this.restTemplate = restTemplate;
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

    public ResponseEntity<Map> createOrderInPayU(Order order, String token) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(token);

        Map<String, Object> body = new HashMap<>();
        body.put("continueUrl", "http://www.bialkowskaismail.pl");
        body.put("notifyUrl", "http://25.53.71.208:4200/notify");
        body.put("customerIp", "127.0.0.1");
        body.put("merchantPosId", "482430");
        body.put("description", order.getDescription());
        body.put("currencyCode", order.getCurrencyCode());
        body.put("totalAmount", order.getTotalAmount());
        body.put("extOrderId", order.getExtOrderId());

        Map<String, String> buyer = new HashMap<>();
        buyer.put("email", order.getEmail());
        buyer.put("firstName", order.getFirstName());
        buyer.put("lastName", order.getLastName());
        body.put("buyer", buyer);

        List<Map<String, String>> products = new ArrayList<>();
        if (order.getItems() != null) {
            for (OrderItem item : order.getItems()) {
                Map<String, String> product = new HashMap<>();
                product.put("name", item.getName());
                product.put("unitPrice", item.getUnitPrice());
                product.put("quantity", item.getQuantity());
                products.add(product);
            }
        }
        body.put("products", products);

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);

        return restTemplate.postForEntity(orderUrl, request, Map.class);
    }


    public Order processOrder(Order order) {
        // Save the order to the database first
        Order savedOrder = saveOrder(order);

        // Get the authorization token
        String token = getAuthToken();

        // Create order in PayU
        ResponseEntity<Map> responseEntity = createOrderInPayU(savedOrder, token);

        // Handle response as needed, e.g., update order status based on PayU response

        // Return the saved order
        return savedOrder;
    }

    public Order saveOrder(Order order) {
        return orderRepository.save(order);
    }
}
