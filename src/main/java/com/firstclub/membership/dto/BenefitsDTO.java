package com.firstclub.membership.dto;

import lombok.Builder;
import lombok.Value;

import java.util.List;
import java.util.Map;

@Value
@Builder
public class BenefitsDTO {
    boolean freeDelivery;
    Integer discountPercent; // aggregate / highest percent
    List<String> coupons;
    Map<String, Object> raw; // merged raw benefit details for advanced use
}