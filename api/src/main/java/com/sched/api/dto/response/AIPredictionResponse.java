package com.sched.api.dto.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AIPredictionResponse {
    private String productName;
    private Integer stockQuantity;
    private Integer prediction7Days;
    private Integer prediction15Days;
    private Integer prediction30Days;
    private Integer recommendedRestock;
    private String alert;
    private String modelUsed;
    private Double modelMAE;
}