package com.sched.api.dto.response;

public record AIPredictionResponse(
        String productName,
        Integer stockQuantity,
        Integer prediction7Days,
        Integer prediction15Days,
        Integer prediction30Days,
        Integer recommendedRestock,
        String alert,
        String modelUsed,
        Double modelMAE
) {
}
