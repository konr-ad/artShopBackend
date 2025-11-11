package com.artshop.backend.controllers;

import com.artshop.backend.repositories.OrderRepository;
import com.artshop.backend.services.PaintingService;
import com.artshop.backend.services.PayuService;
import com.artshop.backend.services.PayuService.NotifyOutcome;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/payments/payu")
@RequiredArgsConstructor
@Slf4j
public class PayuController {

    private final PayuService payuService;

    @PostMapping(value = "/notify", consumes = "application/json")
    public ResponseEntity<Void> notify(@RequestBody Map<String, Object> payload,
                                       @RequestHeader(name = "OpenPayu-Signature", required = false) String signature) {
        NotifyOutcome outcome = payuService.handleNotify(payload, signature);
        return ResponseEntity.status(outcome.status()).build();
    }

}