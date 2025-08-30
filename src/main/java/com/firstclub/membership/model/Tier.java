package com.firstclub.membership.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.Set;

@Entity
@Table(name = "tier")
@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class Tier {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String code; // SILVER, GOLD, PLATINUM

    private Integer priority;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "tier_benefit",
            joinColumns = @JoinColumn(name = "tier_id"),
            inverseJoinColumns = @JoinColumn(name = "benefit_id"))
    private Set<Benefit> benefits;

    private Boolean active = true;
}