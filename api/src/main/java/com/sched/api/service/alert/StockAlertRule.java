package com.sched.api.service.alert;

import com.sched.api.dto.response.AlertResponse;

import java.util.List;

public interface StockAlertRule {
    AlertType type();

    List<AlertResponse> evaluate(Long companyId);
}
