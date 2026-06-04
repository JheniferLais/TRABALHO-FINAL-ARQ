package com.sched.api.service.alert;

/**
 * Identifies each stock alert strategy so the {@code AlertService} can
 * dispatch to the right {@link StockAlertRule} without {@code if/else} chains.
 */
public enum AlertType {
    EXPIRING,
    LOW_STOCK
}
