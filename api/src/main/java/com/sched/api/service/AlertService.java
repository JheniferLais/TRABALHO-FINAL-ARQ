package com.sched.api.service;

import com.sched.api.domain.User;
import com.sched.api.dto.response.AlertResponse;
import com.sched.api.service.alert.AlertType;
import com.sched.api.service.alert.StockAlertRule;
import com.sched.api.utils.SecurityUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class AlertService {

    private final Map<AlertType, StockAlertRule> rulesByType;

    public AlertService(List<StockAlertRule> rules) {
        this.rulesByType = rules.stream()
                .collect(Collectors.toMap(StockAlertRule::type, Function.identity()));
    }

    public List<AlertResponse> getProductsExpiringInNext30Days() {
        return evaluate(AlertType.EXPIRING);
    }

    public List<AlertResponse> getProductsWithLowStock() {
        return evaluate(AlertType.LOW_STOCK);
    }

    private List<AlertResponse> evaluate(AlertType type) {
        User authUser = SecurityUtils.getAuthenticatedUser();
        Long companyId = authUser.getCompany().getId();

        return rulesByType.get(type).evaluate(companyId);
    }
}
