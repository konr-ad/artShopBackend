package com.artshop.backend.services;

import com.artshop.backend.enums.EPaymentStatus;
import com.artshop.backend.models.payu.Order;
import com.artshop.backend.models.payu.OrderItem;
import com.artshop.backend.repositories.OrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.*;

/**
 * Serwis obsługujący webhook PayU:
 * - weryfikacja podpisu (stub),
 * - bezpieczne parsowanie payloadu,
 * - idempotencja (po paymentId z 'properties'),
 * - aktualizacja statusu zamówienia,
 * - blokada obrazów dla płatności zakończonych sukcesem.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class PayuService {

    private final OrderRepository orderRepository;
    private final PaintingService paintingService;

    public record NotifyOutcome(HttpStatus status, String message) {
        public static NotifyOutcome ok() {
            return new NotifyOutcome(HttpStatus.OK, "OK");
        }

        public static NotifyOutcome bad(String msg) {
            return new NotifyOutcome(HttpStatus.BAD_REQUEST, msg);
        }

        public static NotifyOutcome forbidden() {
            return new NotifyOutcome(HttpStatus.FORBIDDEN, "Invalid signature");
        }

        public static NotifyOutcome notFound() {
            return new NotifyOutcome(HttpStatus.OK, "Order not found (ignored)");
        }
    }

    @Transactional
    public NotifyOutcome handleNotify(Map<String, Object> payload, String signatureHeader) {
        log.debug("PayU notify payload: {}", payload);

        // 1) (opcjonalnie) weryfikacja podpisu
        // if (!verifySignature(signatureHeader, payload)) { return NotifyOutcome.forbidden(); }

        // 2) Parsowanie i walidacja payloadu
        var parsed = parseNotifyPayload(payload);
        if (!parsed.valid()) {
            log.warn("Invalid notify payload: {}", parsed.reason());
            return NotifyOutcome.bad(parsed.reason());
        }

        // 3) Idempotencja – po PAYMENT_ID/txId (jeśli przechowujesz w Order)
        // Zakładamy, że w encji Order masz np. pole: lastPayuPaymentId
        // i sprawdzasz, czy to zdarzenie już było przetworzone.
        Optional<Order> opt = findOrder(parsed.extOrderId(), parsed.payuOrderId());
        if (opt.isEmpty()) {
            log.warn("Order not found for extOrderId={} / payuOrderId={}", parsed.extOrderId(), parsed.payuOrderId());
            return NotifyOutcome.notFound();
        }

        Order order = opt.get();
        if (order.getPaymentStatus() == EPaymentStatus.COMPLETED) {
            log.info("Duplicate notify received (paymentId={}), ignoring", parsed.paymentId());
            return NotifyOutcome.ok();
        }

        // 4) Aktualizacja stanu i akcje domenowe
        EPaymentStatus newStatus = mapPayuStatus(parsed.status());
        EPaymentStatus oldStatus = order.getPaymentStatus();

        // Tylko gdy rzeczywiście zmieniamy stan
        if (!Objects.equals(oldStatus, newStatus)) {
            order.setPaymentStatus(newStatus);
        }

        // Zablokuj obrazy tylko przy sukcesie (i jeśli wcześniej nie były zablokowane)
        if (newStatus == EPaymentStatus.COMPLETED) {
            List<Long> paintingIds = order.getItems().stream()
                    .map(OrderItem::getPaintingId)
                    .filter(Objects::nonNull)
                    .toList();
            if (!paintingIds.isEmpty()) {
                paintingService.lockPaintings(paintingIds);
            }
        }

        orderRepository.save(order);

        log.info("Order {} updated to {} by PayU notify (extOrderId={}, payuOrderId={}, paymentId={})",
                order.getId(), newStatus, parsed.extOrderId(), parsed.payuOrderId(), parsed.paymentId());

        return NotifyOutcome.ok();
    }

    // ------- helpers -------

    private EPaymentStatus mapPayuStatus(String payuStatus) {
        if (payuStatus == null) return EPaymentStatus.PENDING;
        return switch (payuStatus.toUpperCase(Locale.ROOT)) {
            case "COMPLETED", "SUCCESS" -> EPaymentStatus.COMPLETED;
            case "CANCELED", "CANCELLED", "FAILED", "REJECTED" -> EPaymentStatus.FAILED;
            case "PENDING" -> EPaymentStatus.PENDING;
            default -> EPaymentStatus.PENDING;
        };
    }

    private Optional<Order> findOrder(String extOrderId, String payuOrderId) {
        Optional<Order> opt = Optional.empty();
        if (extOrderId != null && !extOrderId.isBlank()) {
            opt = orderRepository.findByExtOrderId(extOrderId);
        }
        if (opt.isEmpty() && payuOrderId != null && !payuOrderId.isBlank()) {
            opt = orderRepository.findByPayuOrderId(payuOrderId);
        }
        return opt;
    }

    /**
     * Bezpieczne wyciąganie pól z payloadu PayU. Obsługuje:
     * - order.status (preferowane),
     * - fallback do order.transactions[0].status,
     * - properties[].value dla PAYMENT_ID (idempotencja),
     * - orderId i extOrderId.
     */
    private ParsedNotify parseNotifyPayload(Map<String, Object> payload) {
        Object orderObj = payload.get("order");
        if (!(orderObj instanceof Map)) {
            return ParsedNotify.invalid("Missing 'order' object");
        }
        Map<String, Object> order = (Map<String, Object>) orderObj;

        String payuOrderId = asString(order.get("orderId"));
        String extOrderId = asString(order.get("extOrderId"));

        String status = asString(order.get("status"));
        if (status == null) {
            List<Map<String, Object>> tx = (List<Map<String, Object>>) order.get("transactions");
            if (tx != null && !tx.isEmpty()) {
                Object s = tx.get(0).get("status");
                status = asString(s);
            }
        }

        String paymentId = null;
        List<Map<String, Object>> props = (List<Map<String, Object>>) payload.get("properties");
        if (props != null) {
            paymentId = props.stream()
                    .filter(p -> "PAYMENT_ID".equalsIgnoreCase(asString(p.get("name"))))
                    .map(p -> asString(p.get("value")))
                    .filter(Objects::nonNull)
                    .findFirst()
                    .orElse(null);
        }

        if (status == null) {
            return ParsedNotify.invalid("Missing status");
        }
        return ParsedNotify.valid(payuOrderId, extOrderId, status, paymentId);
    }

    private String asString(Object o) {
        return (o instanceof String s) ? s : null;
    }

    /**
     * Dane wyciągnięte z webhooka.
     */
    private record ParsedNotify(String payuOrderId, String extOrderId, String status, String paymentId,
                                boolean valid, String reason) {
        static ParsedNotify valid(String payuOrderId, String extOrderId, String status, String paymentId) {
            return new ParsedNotify(payuOrderId, extOrderId, status, paymentId, true, null);
        }

        static ParsedNotify invalid(String reason) {
            return new ParsedNotify(null, null, null, null, false, reason);
        }
    }

    // Docelowo: weryfikacja nagłówka OpenPayu-Signature (MD5/HMAC SHA-256 zależnie od konfiguracji)
    @SuppressWarnings("unused")
    private boolean verifySignature(String signatureHeader, Map<String, Object> payload) {
        return true;
    }
}

