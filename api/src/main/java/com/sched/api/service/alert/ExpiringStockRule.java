package com.sched.api.service.alert;

import com.sched.api.dto.response.AlertResponse;
import com.sched.api.repository.AlertRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Flags stock batches whose expiration date falls within the next 30 days.
 */
@Component
@RequiredArgsConstructor
public class ExpiringStockRule implements StockAlertRule {

    private static final int HORIZON_DAYS = 30;

    private final AlertRepository alertRepository;

    @Override
    public AlertType type() {
        return AlertType.EXPIRING;
    }

    @Override
    @Transactional(readOnly = true)
    public List<AlertResponse> evaluate(Long companyId) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime horizon = now.plusDays(HORIZON_DAYS);

        return alertRepository
                .findByExpirationDateBetweenAndProduct_Company_IdAndProduct_DeletedFalse(now, horizon, companyId)
                .stream()
                .map(AlertResponse::new)
                .toList();
    }
}
