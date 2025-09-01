package com.firstclub.membership.controller;

import com.firstclub.membership.dto.BenefitsDTO;
import com.firstclub.membership.model.Subscription;
import com.firstclub.membership.service.SubscriptionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/subscriptions")
@RequiredArgsConstructor
public class SubscriptionController {
    private final SubscriptionService svc;

    /**
     * Subscribe: accepts optional idempotencyKey to avoid duplicate creation.
     * Form fields or JSON body allowed (for simplicity we use request params).
     */
    @PostMapping
    public ResponseEntity<Subscription> subscribe(@RequestParam String userId,
                                                  @RequestParam String planCode,
                                                  @RequestParam String tierCode,
                                                  @RequestParam(required = false) String idempotencyKey) {
        Subscription s = svc.subscribe(userId, planCode, tierCode, idempotencyKey);
        return ResponseEntity.ok(s);
    }

    @GetMapping("/current")
    public ResponseEntity<Subscription> current(@RequestParam String userId) {
        Optional<Subscription> s = svc.current(userId);
        return s.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.noContent().build());
    }

    @PostMapping("/{id}/tier")
    public ResponseEntity<Subscription> changeTier(@PathVariable Long id, @RequestParam String tierCode) {
        Subscription s = svc.changeTier(id, tierCode);
        return ResponseEntity.ok(s);
    }

    @PostMapping("/{id}/cancel")
    public ResponseEntity<Void> cancel(@PathVariable Long id) {
        svc.cancel(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/benefits")
    public ResponseEntity<BenefitsDTO> benefitsForSubscription(@PathVariable Long id) {
        Subscription s = svc.getSubscriptionRepository().findById(id).orElseThrow();
        return ResponseEntity.ok(svc.getBenefitsForSubscription(s));
    }

    @GetMapping("/benefits")
    public ResponseEntity<BenefitsDTO> benefitsForUser(@RequestParam String userId) {
        return ResponseEntity.ok(svc.getBenefitsForUser(userId));
    }
}