package com.firstclub.membership.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.firstclub.membership.dto.BenefitsDTO;
import com.firstclub.membership.model.*;
import com.firstclub.membership.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SubscriptionService {
    private final SubscriptionRepository subRepo;
    private final MembershipPlanRepository planRepo;
    private final TierRepository tierRepo;
    private final UserAccountRepository userRepo;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Transactional
    public Subscription subscribe(String externalUserId, String planCode, String tierCode, String idempotencyKey) {
        if (idempotencyKey != null) {
            Optional<Subscription> previous = subRepo.findByIdempotencyKey(idempotencyKey);
            if (previous.isPresent()) return previous.get();
        }

        UserAccount user = userRepo.findByExternalUserId(externalUserId)
                .orElseGet(() -> userRepo.save(UserAccount.builder().externalUserId(externalUserId).build()));

        MembershipPlan plan = planRepo.findByCode(planCode)
                .orElseThrow(() -> new IllegalArgumentException("Unknown plan " + planCode));
        Tier tier = tierRepo.findByCode(tierCode)
                .orElseThrow(() -> new IllegalArgumentException("Unknown tier " + tierCode));

        Instant now = Instant.now();
        Subscription s = Subscription.builder()
                .user(user)
                .plan(plan)
                .tier(tier)
                .status(SubscriptionStatus.ACTIVE)
                .startAt(now)
                .endAt(now.plus(Duration.ofDays(plan.getDurationDays())))
                .idempotencyKey(idempotencyKey)
                .build();

        Subscription saved = subRepo.save(s);
        return saved;
    }

    @Transactional
    public Subscription changeTier(Long subId, String newTierCode) {
        Subscription s = subRepo.findByIdForUpdate(subId)
                .orElseThrow(() -> new IllegalArgumentException("Subscription not found: " + subId));
        if (s.getStatus() != SubscriptionStatus.ACTIVE) throw new IllegalStateException("Subscription not active");
        Tier t = tierRepo.findByCode(newTierCode)
                .orElseThrow(() -> new IllegalArgumentException("Unknown tier: " + newTierCode));
        s.setTier(t);
        return subRepo.save(s);
    }

    @Transactional
    public void cancel(Long subId) {
        Subscription s = subRepo.findById(subId).orElseThrow(() -> new IllegalArgumentException("Not found"));
        s.setStatus(SubscriptionStatus.CANCELED);
        subRepo.save(s);
    }

    public Optional<Subscription> current(String externalUserId) {
        return subRepo.findActiveByUserExternalId(externalUserId, Instant.now());
    }

    /**
     * Produce a merged BenefitsDTO for a subscription by combining plan & tier benefits.
     */
    public BenefitsDTO getBenefitsForSubscription(Subscription s) {
        List<Benefit> merged = new ArrayList<>();
        if (s.getPlan() != null && s.getPlan().getBenefits() != null) merged.addAll(s.getPlan().getBenefits());
        if (s.getTier() != null && s.getTier().getBenefits() != null) merged.addAll(s.getTier().getBenefits());

        boolean freeDelivery = false;
        Integer maxDiscount = null;
        List<String> coupons = new ArrayList<>();
        Map<String, Object> raw = new HashMap<>();

        for (Benefit b : merged) {
            try {
                JsonNode node = b.getDetails() == null || b.getDetails().isBlank() ? null : objectMapper.readTree(b.getDetails());
                if ("FREE_DELIVERY".equalsIgnoreCase(b.getName())) {
                    if (node == null || node.path("eligible").asBoolean(true)) freeDelivery = true;
                } else if ("EXTRA_DISCOUNT".equalsIgnoreCase(b.getName())) {
                    if (node != null && node.has("percent")) {
                        int p = node.path("percent").asInt();
                        if (maxDiscount == null || p > maxDiscount) maxDiscount = p;
                    }
                } else if ("EXCLUSIVE_DEALS".equalsIgnoreCase(b.getName())) {
                    if (node != null && node.has("coupons")) {
                        node.path("coupons").forEach(n -> coupons.add(n.asText()));
                    }
                } else if ("PRIORITY_SUPPORT".equalsIgnoreCase(b.getName())) {
                    raw.put("prioritySupport", true);
                }
                raw.put(b.getName(), node);
            } catch (Exception ex) {
                // ignore parse errors for robustness; log in real app
            }
        }

        return BenefitsDTO.builder()
                .freeDelivery(freeDelivery)
                .discountPercent(maxDiscount)
                .coupons(coupons)
                .raw(raw)
                .build();
    }

    public SubscriptionRepository getSubscriptionRepository() {
        return subRepo;
    }

    public BenefitsDTO getBenefitsForUser(String externalUserId) {
        Optional<Subscription> opt = current(externalUserId);
        if (opt.isEmpty()) return BenefitsDTO.builder().freeDelivery(false).discountPercent(null).coupons(Collections.emptyList()).raw(Collections.emptyMap()).build();
        return getBenefitsForSubscription(opt.get());
    }
}