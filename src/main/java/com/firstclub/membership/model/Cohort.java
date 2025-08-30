package com.firstclub.membership.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "cohort")
@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class Cohort {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String code; // e.g. HNI, STUDENT
}