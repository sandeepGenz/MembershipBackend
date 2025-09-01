package com.firstclub.membership.controller;

import com.firstclub.membership.model.Tier;
import com.firstclub.membership.model.UserAccount;
import com.firstclub.membership.repository.UserAccountRepository;
import com.firstclub.membership.service.RuleEvaluationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/tiers")
@RequiredArgsConstructor
public class TierController {
    private final RuleEvaluationService ruleService;
    private final UserAccountRepository userRepo;

    /**
     * Evaluate recommended tier using orders and total value along with user cohorts if available.
     */
    @GetMapping("/evaluate")
    public ResponseEntity<String> evaluate(@RequestParam String userId,
                                           @RequestParam int orders,
                                           @RequestParam long valueMinor) {
        UserAccount user = userRepo.findByExternalUserId(userId).orElse(UserAccount.builder().externalUserId(userId).build());
        Tier t = ruleService.evaluate(user, orders, valueMinor);
        return ResponseEntity.ok(t != null ? t.getCode() : null);
    }
}