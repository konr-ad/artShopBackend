package com.artShop.artShop.services;

import com.artShop.artShop.models.payu.Order;
import com.artShop.artShop.models.payu.OrderItem;
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

    private static final Logger logger = LoggerFactory.getLogger(OrderService.class);

    @Autowired
    public OrderService(OrderRepository orderRepository, RestTemplate restTemplate) {
        this.orderRepository = orderRepository;
        this.restTemplate = restTemplate;
    }

    public Order processOrder(Order order) {
        // Save the order to the database first
        Order savedOrder = saveOrder(order);

        // Get the authorization token
        String token = getAuthToken();

        // Create order in PayU
        Map<String, Object> response = createOrderInPayU(savedOrder, token);

        // Extract and update necessary information from the response
        savedOrder.setPayuOrderId((String) response.get("orderId"));
        savedOrder.setRedirectUri((String) response.get("redirectUri"));
        savedOrder.setPaymentStatus((String) ((Map) response.get("status")).get("statusCode"));

        // Save updated order to the database
        saveOrder(savedOrder);

        return savedOrder;
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

        ResponseEntity<Map> response = restTemplate.postForEntity(orderUrl, request, Map.class);
        return response.getBody();
    }


    public Order saveOrder(Order order) {
        Order savedOrder = new Order();
        savedOrder.setEmail(order.getEmail());
        savedOrder.setFirstName(order.getFirstName());
        savedOrder.setLastName(order.getLastName());
        savedOrder.setCountry(order.getCountry());
        savedOrder.setState(order.getState());
        savedOrder.setAddress(order.getAddress());
        savedOrder.setApartmentNumber(order.getApartmentNumber());
        savedOrder.setCity(order.getCity());
        savedOrder.setZip(order.getZip());
        savedOrder.setDescription(order.getDescription());
        savedOrder.setCurrencyCode(order.getCurrencyCode());
        savedOrder.setTotalAmount(order.getTotalAmount());
        savedOrder.setItems(order.getItems());

        return orderRepository.save(order);
    }
}
