package com.firstclub.membership.service;

import com.firstclub.membership.model.Cohort;
import com.firstclub.membership.model.Tier;
import com.firstclub.membership.model.UserAccount;
import com.firstclub.membership.repository.TierRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class RuleEvaluationService {

    private final TierRepository tierRepo;

    // Hardcoded thresholds in minor units & orders; can be externalized to YAML/DB
    private final Map<String, Threshold> thresholds = Map.of(
            "PLATINUM", new Threshold(10, 15000L),
            "GOLD", new Threshold(5, 5000L),
            "SILVER", new Threshold(0, 0L)
    );

    // cohort boosts: each cohort can bump tier by X steps (higher priority)
    private final Map<String, Integer> cohortBoosts = Map.of(
            "HNI", 1,
            "STUDENT", 0
    );

    public Tier evaluate(UserAccount user, int ordersInMonth, long totalValueMinor) {
        List<Tier> ordered = tierRepo.findByActiveTrueOrderByPriorityDesc();
        int boost = 0;
        if (user != null && user.getCohorts() != null) {
            for (Cohort c : user.getCohorts()) {
                boost += cohortBoosts.getOrDefault(c.getCode(), 0);
            }
        }

        for (Tier t : ordered) {
            Threshold th = thresholds.getOrDefault(t.getCode(), new Threshold(0, 0L));
            if (ordersInMonth >= th.minOrders && totalValueMinor >= th.minValue) {
                if (boost <= 0) return t;
                int idx = ordered.indexOf(t);
                int bumped = Math.max(0, idx - boost);
                return ordered.get(bumped);
            }
        }
        // fallback lowest
        return ordered.isEmpty() ? null : ordered.get(ordered.size() - 1);
    }

    private record Threshold(int minOrders, long minValue) {}
}