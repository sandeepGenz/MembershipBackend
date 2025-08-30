package com.firstclub.membership.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.Set;

@Entity
@Table(name = "membership_plan")
@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class MembershipPlan {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String code; // MONTHLY, QUARTERLY, YEARLY

    private Integer durationDays;

    /** price in minor units (paise/cents) for safety */
    private Long amountMinor;

    private String currency;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "plan_benefit",
            joinColumns = @JoinColumn(name = "plan_id"),
            inverseJoinColumns = @JoinColumn(name = "benefit_id"))
    private Set<Benefit> benefits;
}