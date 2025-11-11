//package com.artshop.backend.services;
//
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.stereotype.Service;
//
//import java.nio.charset.StandardCharsets;
//import java.security.MessageDigest;
//import java.util.regex.Matcher;
//import java.util.regex.Pattern;
//
//@Slf4j
//@Service
//public class PayuSignatureVerifier {
//
//    private final String secondKey;
//
//    public PayuSignatureVerifier(@Value("${payu.secondKey}") String secondKey) {
//        this.secondKey = secondKey;
//    }
//
//    /**
//     * Weryfikacja podpisu nagłówka OpenPayu-Signature zgodnie z dokumentacją.
//     *
//     * Nagłówek wygląda np:
//     *   OpenPayu-Signature: sender=checkout;signature=d47d8a771d558c29285887febddd9327;algorithm=MD5;content=DOCUMENT
//     *
//     * Proces:
//     * 1. Pobierz incomingSignature z nagłówka.
//     * 2. Sprawdź algorytm (np. MD5) — dokumentacja PayU na dziś mówi MD5. :contentReference[oaicite:2]{index=2}
//     * 3. Oblicz expectedSignature = hash(body + secondKey).
//     * 4. Porównaj expectedSignature z incomingSignature w sposób bezpieczny (np. MessageDigest.isEqual).
//     *
//     * @param signatureHeader wartość nagłówka OpenPayu-Signature
//     * @param body            surowe ciało żądania (JSON string)
//     * @return true jeśli podpis poprawny
//     */
//    public boolean verify(String signatureHeader, String body) {
//        if (signatureHeader == null || body == null) {
//            return false;
//        }
//
//        // Parsowanie nagłówka
//        Pattern p = Pattern.compile("sender=([^;]+);signature=([^;]+);algorithm=([^;]+);content=([^;]+)");
//        Matcher m = p.matcher(signatureHeader.trim());
//        if (!m.matches()) {
//            return false;
//        }
//        String sender    = m.group(1);
//        String incomingSignature = m.group(2);
//        String algorithm = m.group(3);
//        String content   = m.group(4);
//
//        // Sprawdzenie content — dokumentacja mówi „DOCUMENT” (nie zawsze używane do obliczeń)
//        if (!"DOCUMENT".equalsIgnoreCase(content)) {
//            return false;
//        }
//
//        // Aktualnie dokumentacja PayU mówi, że algorytm to MD5 — jeśli inny, można dodać obsługę SHA-256. :contentReference[oaicite:3]{index=3}
//        if (!"MD5".equalsIgnoreCase(algorithm)) {
//            log.warn("Unexpected signature algorithm from PayU: {}", algorithm);
//            // Możesz ew. wspierać inne algorytmy
//            return false;
//        }
//
//        // Obliczenie expectedSignature
//        String toHash = body + secondKey;
//        String expectedSignature = md5Hex(toHash);
//
//        // Bezpieczne porównanie
//        return MessageDigest.isEqual(expectedSignature.getBytes(StandardCharsets.UTF_8),
//                incomingSignature.getBytes(StandardCharsets.UTF_8));
//    }
//
//    private String md5Hex(String input) {
//        try {
//            MessageDigest md = MessageDigest.getInstance("MD5");
//            byte[] digest = md.digest(input.getBytes(StandardCharsets.UTF_8));
//            return bytesToHexLowercase(digest);
//        } catch (Exception e) {
//            throw new RuntimeException("Failed to compute MD5 for PayU signature check", e);
//        }
//    }
//
//    private String bytesToHexLowercase(byte[] bytes) {
//        StringBuilder sb = new StringBuilder(bytes.length * 2);
//        for (byte b : bytes) {
//            sb.append(Character.forDigit((b >> 4) & 0xF, 16));
//            sb.append(Character.forDigit((b & 0xF), 16));
//        }
//        return sb.toString();
//    }
//}
