package com.groupthree.sims;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * AlertSys manages the creation, storage, retrieval, and acknowledgment
 * of system alerts. Alerts represent important system events such as:
 *
 *  • Low stock conditions
 *  • Defect rate violations
 *  • Security or production-related warnings
 *
 * This class provides:
 *  - Factory methods for generating specific alert types
 *  - Centralized creation logic
 *  - Query utilities for UIs (AdminGUI)
 *  - Acknowledgment handling
 */
public class AlertSys {

    /** Internal list storing active and historical alerts */
    private final List<Alert> alerts = new ArrayList<>();

    /** Auto-incrementing identifier for new alerts */
    private long nextAlertId = 1;


    /* ===========================================================
       ALERT CREATION
       =========================================================== */

    /**
     * Creates and registers a new alert with the provided metadata.
     *
     * @param type            alert category/type
     * @param severity        severity level of the alert
     * @param message         main alert message
     * @param relatedEntity   reference entity (e.g., itemCode, batchId)
     * @param thresholdValue  expected threshold limit (nullable)
     * @param actualValue     actual measured value (nullable)
     * @return the created Alert instance
     */
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


    /* ===========================================================
       PREDEFINED ALERT FACTORIES
       =========================================================== */

    /**
     * Generates a low-stock warning alert for an item.
     *
     * @param itemCode    unique identifier of the item
     * @param currentQty  current available quantity
     * @param threshold   minimum acceptable quantity
     * @return created Alert
     */
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

    /**
     * Generates an alert when a production batch exceeds the allowed defect rate.
     *
     * @param batchId     identifier of the batch
     * @param defectRate  detected defect percentage
     * @param threshold   allowable defect limit
     * @return created Alert
     */
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

    /**
     * Convenience method that checks if an item is below the required stock level
     * and automatically generates an alert when necessary.
     */
    public void checkLowStockThreshold(String itemCode,
                                       int currentQty,
                                       int threshold) {

        if (currentQty <= threshold) {
            raiseLowStockAlert(itemCode, currentQty, threshold);
        }
    }


    /* ===========================================================
       ALERT QUERY OPERATIONS
       =========================================================== */

    /**
     * @return all alerts, including acknowledged and historical alerts
     */
    public List<Alert> getAllAlerts() {
        return new ArrayList<>(alerts);
    }

    /**
     * @return alerts that have not been acknowledged by any user
     */
    public List<Alert> getUnacknowledgedAlerts() {
        List<Alert> result = new ArrayList<>();
        for (Alert a : alerts) {
            if (!a.isAcknowledged()) {
                result.add(a);
            }
        }
        return result;
    }

    /**
     * Defines "active" alerts as those not yet acknowledged.
     *
     * @return active alerts
     */
    public List<Alert> getActiveAlerts() {
        return getUnacknowledgedAlerts();
    }

    /**
     * @return user-friendly formatted alert messages for display in AdminGUI
     */
    public List<String> getActiveAlertMessages() {
        List<String> lines = new ArrayList<>();
        for (Alert a : getActiveAlerts()) {
            lines.add(a.toString());
        }
        return lines;
    }

    /**
     * Finds an alert by its unique ID.
     *
     * @return matching Alert or null if none found
     */
    public Alert findAlertById(long id) {
        for (Alert a : alerts) {
            if (a.getId() == id) {
                return a;
            }
        }
        return null;
    }


    /* ===========================================================
       ALERT ACKNOWLEDGMENT
       =========================================================== */

    /**
     * Marks an alert as acknowledged by a specific user.
     *
     * @param alertId ID of the alert to acknowledge
     * @param user    the user acknowledging the alert
     * @return true if alert exists and was acknowledged, false otherwise
     */
    public boolean acknowledgeAlert(long alertId, User user) {
        Alert alert = findAlertById(alertId);
        if (alert == null) return false;

        alert.setAcknowledged(true);
        alert.setAcknowledgedBy(user);
        alert.setAcknowledgedAt(LocalDateTime.now());
        return true;
    }
}
