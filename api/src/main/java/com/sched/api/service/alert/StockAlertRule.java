package com.sched.api.service.alert;

import com.sched.api.dto.response.AlertResponse;

import java.util.List;

/**
 * Strategy (GoF, behavioral) for evaluating a single kind of stock alert.
 * New alert kinds are added by creating a new implementation — no existing
 * code is modified (Open/Closed Principle).
 */
public interface StockAlertRule {

    /** The alert kind this rule answers for. */
    AlertType type();

    /** Evaluates the rule for the given company and returns the triggered alerts. */
    List<AlertResponse> evaluate(Long companyId);
}
