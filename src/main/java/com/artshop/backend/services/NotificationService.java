package com.artshop.backend.services;

import com.artshop.backend.enums.EPaymentStatus;
import com.artshop.backend.models.payu.Order;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final JavaMailSender mailSender;
    private final SpringTemplateEngine templateEngine;

    @Value("${app.frontend.origin}")
    private String frontendOrigins;

    @Value("${spring.mail.username}")
    private String fromAddress;

    @Value("${app.mail.toAddress1}")
    private String toAddress1;

    @Value("${app.mail.toAddress2}")
    private String toAddress2;

    public void sendNewOrderAdminEmail(Order order) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(
                    message, MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED,
                    StandardCharsets.UTF_8.name());

            helper.setFrom(fromAddress);
            helper.setTo(new String[]{
                    toAddress1, toAddress2
            });
            helper.setSubject("Nowe zamówienie – " + order.getExtOrderId());

            String createdAt = formatLocalDateTime(order.getCreatedAt());
            String frontendAddress = getFrontendAddress(frontendOrigins);

            Context context = new Context();
            context.setVariable("extOrderId", order.getExtOrderId());
            context.setVariable("totalAmount", order.getTotalAmount().toString());
            context.setVariable("itemCount", order.getItems().size());
            context.setVariable("orderCreateDate", createdAt);
            context.setVariable("adminOrderUrl", frontendAddress + "/login");
            context.setVariable("paymentStatus", EPaymentStatus.COMPLETED.getDescriptionPL());

            String html = templateEngine.process("new-order-admin", context);
            helper.setText(html, true);

            mailSender.send(message);
        } catch (Exception e) {
        }
    }

    public void sendPaymentConfirmationCustomerEmail(Order order) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(
                    message, MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED,
                    StandardCharsets.UTF_8.name());

            helper.setFrom(fromAddress);
            helper.setTo(new String[]{
                    order.getCustomer().getEmail()
            });
            helper.setSubject("Nowe zamówienie – " + order.getExtOrderId());

            String createdAt = formatLocalDateTime(order.getCreatedAt());

            Context context = new Context();
            context.setVariable("customerName", order.getCustomer().getFirstName());
            context.setVariable("items", order.getItems());
            context.setVariable("extOrderId", order.getExtOrderId());
            context.setVariable("totalAmount", order.getTotalAmount().toString());
            context.setVariable("itemCount", order.getItems().size());
            context.setVariable("orderCreateDate", createdAt);

            String html = templateEngine.process("payment-confirmation-customerPL", context);
            helper.setText(html, true);

            mailSender.send(message);
        } catch (Exception e) {
            // loguj błąd, ewentualnie re-try lub zapis w bazie
            // np. log.error("Failed to send admin new order email", e);
        }
    }

    private String getFrontendAddress(String frontendOrigins) {
        return Arrays.stream(frontendOrigins.split(","))
                .map(String::trim).filter(s -> !s.isBlank() && s.contains("https"))
                .findFirst()
                .orElseThrow(null);
    }

    private String formatLocalDateTime(Instant createdAtInstant) {
        LocalDateTime createdAt = LocalDateTime.ofInstant(
                createdAtInstant,
                ZoneId.of("Europe/Warsaw"));
        return createdAt.format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss"));
    }
}