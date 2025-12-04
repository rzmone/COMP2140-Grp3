package com.groupthree.sims;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class AlertSys {

    private final List<Alert> alerts = new ArrayList<>();
    private long nextAlertId = 1;

    // -------------- Core alert operations --------------

    public Alert createAlert(AlertType type,
                             AlertSeverity severity,
                             String message,
                             String relatedEntity,
                             Double thresholdValue,
                             Double actualValue) {

        Alert alert = new Alert(
                nextAlertId++,
                type,
                severity,
                message,
                relatedEntity,
                thresholdValue,
                actualValue,
                LocalDateTime.now()
        );
        alerts.add(alert);
        return alert;
    }

    public Alert raiseLowStockAlert(String itemCode,
                                    int currentQty,
                                    int threshold) {
        String message = String.format(
                "Item %s has low stock. Current quantity: %d, Threshold: %d",
                itemCode, currentQty, threshold
        );

        return createAlert(
                AlertType.LOW_STOCK,
                AlertSeverity.WARNING,
                message,
                itemCode,
                (double) threshold,
                (double) currentQty
        );
    }

    public Alert raiseDefectThresholdAlert(String batchId,
                                           double defectRate,
                                           double threshold) {
        String message = String.format(
                "Batch %s defect rate %.2f exceeded threshold %.2f",
                batchId, defectRate, threshold
        );

        return createAlert(
                AlertType.DEFECT_THRESHOLD,
                AlertSeverity.CRITICAL,
                message,
                batchId,
                threshold,
                defectRate
        );
    }

    public void checkLowStockThreshold(String itemCode,
                                       int currentQty,
                                       int threshold) {
        if (currentQty <= threshold) {
            raiseLowStockAlert(itemCode, currentQty, threshold);
        }
    }

    // -------------- Management for AdminGUI --------------

    public List<Alert> getAllAlerts() {
        return new ArrayList<>(alerts);
    }

    public List<Alert> getUnacknowledgedAlerts() {
        List<Alert> result = new ArrayList<>();
        for (Alert a : alerts) {
            if (!a.isAcknowledged()) result.add(a);
        }
        return result;
    }

    /**
     * "Active" = unacknowledged alerts.
     */
    public List<Alert> getActiveAlerts() {
        return getUnacknowledgedAlerts();
    }

    /**
     * Helper that returns formatted strings, so AdminGUI can just append them.
     */
    public List<String> getActiveAlertMessages() {
        List<String> lines = new ArrayList<>();
        for (Alert a : getActiveAlerts()) {
            lines.add(a.toString());
        }
        return lines;
    }

    public Alert findAlertById(long id) {
        for (Alert a : alerts) {
            if (a.getId() == id) return a;
        }
        return null;
    }

    public boolean acknowledgeAlert(long alertId, User user) {
        Alert alert = findAlertById(alertId);
        if (alert == null) return false;
        alert.setAcknowledged(true);
        alert.setAcknowledgedBy(user);
        alert.setAcknowledgedAt(LocalDateTime.now());
        return true;
    }
}
