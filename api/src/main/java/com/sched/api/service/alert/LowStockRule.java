package com.sched.api.service.alert;

import com.sched.api.dto.response.AlertResponse;
import com.sched.api.repository.AlertRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Flags stock batches whose remaining quantity is at or below the
 * replenishment threshold.
 */
@Component
@RequiredArgsConstructor
public class LowStockRule implements StockAlertRule {

    private static final int LOW_STOCK_THRESHOLD = 20;

    private final AlertRepository alertRepository;

    @Override
    public AlertType type() {
        return AlertType.LOW_STOCK;
    }

    @Override
    @Transactional(readOnly = true)
    public List<AlertResponse> evaluate(Long companyId) {
        return alertRepository
                .findByQuantityLessThanEqualAndProduct_Company_IdAndProduct_DeletedFalse(LOW_STOCK_THRESHOLD, companyId)
                .stream()
                .map(AlertResponse::new)
                .toList();
    }
}
