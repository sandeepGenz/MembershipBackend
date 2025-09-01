package com.firstclub.membership.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

@Entity
@Table(name = "subscription",
        indexes = {@Index(name = "idx_user", columnList = "user_id")})
@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class Subscription {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false, fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id")
    private UserAccount user;

    @ManyToOne(optional = false, fetch = FetchType.EAGER)
    @JoinColumn(name = "plan_id")
    private MembershipPlan plan;

    @ManyToOne(optional = false, fetch = FetchType.EAGER)
    @JoinColumn(name = "tier_id")
    private Tier tier;

    @Enumerated(EnumType.STRING)
    private SubscriptionStatus status;

    private Instant startAt;
    private Instant endAt;

    @Version
    private Long version;

    @Column(unique = true)
    private String idempotencyKey;
}