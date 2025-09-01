package com.firstclub.membership.service;

import com.firstclub.membership.model.MembershipPlan;
import com.firstclub.membership.repository.MembershipPlanRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PlanService {
    private final MembershipPlanRepository repo;

    public List<MembershipPlan> listAll() {
        return repo.findAll();
    }
}