package com.firstclub.membership.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.Set;

@Entity
@Table(name = "user_account")
@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class UserAccount {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String externalUserId;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "user_cohort",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "cohort_id"))
    private Set<Cohort> cohorts;
}