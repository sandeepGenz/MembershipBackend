package com.firstclub.membership.controller;

import com.firstclub.membership.model.MembershipPlan;
import com.firstclub.membership.service.PlanService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/plans")
@RequiredArgsConstructor
public class PlanController {
    private final PlanService planService;

    @GetMapping
    public ResponseEntity<List<MembershipPlan>> list() {
        return ResponseEntity.ok(planService.listAll());
    }
}