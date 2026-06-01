package com.sched.api.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DemandDataRequest {
    private Long productId;

    @NotBlank(message = "productName cannot be empty")
    private String productName;

    @NotBlank(message = "category cannot be empty")
    private String category;

    @NotNull(message = "price cannot be empty")
    private Double price;

    @NotNull(message = "totalSold cannot be empty")
    private Integer totalSold;

    @NotNull(message = "month cannot be empty")
    private Integer month;

    @NotNull(message = "stockQuantity cannot be empty")
    private Long stockQuantity;
}