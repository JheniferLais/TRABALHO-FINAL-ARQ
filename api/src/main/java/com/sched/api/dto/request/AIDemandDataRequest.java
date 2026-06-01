package com.sched.api.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record AIDemandDataRequest(
        Long productId,

        @NotBlank(message = "productName cannot be empty")
        String productName,

        @NotBlank(message = "category cannot be empty")
        String category,

        @NotNull(message = "price cannot be empty")
        Double price,

        @NotNull(message = "totalSold cannot be empty")
        Integer totalSold,

        @NotNull(message = "month cannot be empty")
        Integer month,

        @NotNull(message = "stockQuantity cannot be empty")
        Long stockQuantity
) {}