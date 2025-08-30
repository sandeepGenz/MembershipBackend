package com.firstclub.membership.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "benefit")
@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class Benefit {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name; // FREE_DELIVERY, EXTRA_DISCOUNT, PRIORITY_SUPPORT, EXCLUSIVE_DEALS

    /**
     * JSON string describing parameters (e.g. {"percent":10,"categories":["ELECTRONICS"]})
     * We'll parse this in services when needed.
     */
    @Column(length = 4000)
    private String details;

    private Boolean configurable = false;
}