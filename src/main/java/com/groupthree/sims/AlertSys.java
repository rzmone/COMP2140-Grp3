package com.groupthree.sims;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
public class AlertSys
{
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
    public static Alert createAlert(AlertType type,
                             AlertSeverity severity,
                             String message,
                             String relatedEntity,
                             Double thresholdValue,
                             Double actualValue) {

        Alert alert = new Alert
        (
                type,
                severity,
                message,
                relatedEntity,
                thresholdValue,
                actualValue,
                LocalDateTime.now()
        );

        int id = Database.insert("alerts", toMap(alert));
        alert.setId(id);

        return alert;
    }

    private static Map<String, Object> toMap(Alert alert)
    {
        Map<String, Object> map = new HashMap<>();

        // Include ID only if it's already assigned
        if(alert.getId() > -1)
            map.put("id", alert.getId());

        map.put("type", alert.getType().toString());
        map.put("severity", alert.getSeverity().toString());
        map.put("message", alert.getMessage());
        map.put("relatedEntity", alert.getRelatedEntity());
        map.put("thresholdValue", alert.getThresholdValue());
        map.put("actualValue", alert.getActualValue());
        map.put("createdAt", java.sql.Timestamp.valueOf(alert.getCreatedAt()));
        map.put("acknowledged", alert.isAcknowledged());
        map.put("acknowledgedBy", alert.getAcknowledgedBy() != null ? alert.getAcknowledgedBy().getId() : null);
        map.put("acknowledgedAt", alert.getAcknowledgedAt() != null ? java.sql.Timestamp.valueOf(alert.getAcknowledgedAt()) : null);
        
        return map;
    }

    private static Alert fromMap(Map<String, Object> map)
    {
        Alert alert = new Alert
        (
                (int) map.get("id"),
                AlertType.valueOf((String) map.get("type")),
                AlertSeverity.valueOf((String) map.get("severity")),
                (String) map.get("message"),
                (String) map.get("relatedEntity"),
                (Double) map.get("thresholdValue"),
                (Double) map.get("actualValue"),
                ((java.sql.Timestamp) map.get("createdAt")).toLocalDateTime()
        );

        alert.setAcknowledged((Boolean) map.get("acknowledged"));
        alert.setAcknowledgedBy(
                map.get("acknowledgedBy") != null ?
                        SecuritySys.findUserById((int) map.get("acknowledgedBy")) : null
        );
        alert.setAcknowledgedAt(
                map.get("acknowledgedAt") != null ?
                        ((java.sql.Timestamp) map.get("acknowledgedAt")).toLocalDateTime() : null
        );

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
    public static Alert raiseLowStockAlert(Stock stock)
    {
        String message = String.format(
                "Item %s has low stock. Current quantity: %d, Threshold: %d",
                stock.getName() + ", ID: " + stock.getId(), stock.getStockLevel(), stock.getMinimumStockLevel()
        );

        return createAlert
        (
                AlertType.LOW_STOCK,
                AlertSeverity.WARNING,
                message,
                String.valueOf(stock.getId()),
                (double) stock.getMinimumStockLevel(),
                (double) stock.getStockLevel()
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
    public static Alert raiseDefectThresholdAlert(String batchId,
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
    public static void checkLowStockThreshold(int stockId)
    {
        Stock stock = InventorySys.findStockById(stockId);
        if (stock.getStockLevel() <= stock.getMinimumStockLevel())
        {
            raiseLowStockAlert(stock);
        }
    }

    /* ===========================================================
       ALERT QUERY OPERATIONS
       =========================================================== */

    /**
     * @return all alerts, including acknowledged and historical alerts
     */
    public static List<Alert> getAllAlerts()
    {
        List<Map<String, Object>> rows = Database.selectAll("alerts");
        List<Alert> alerts = new ArrayList<>();

        for (Map<String, Object> row : rows) {
            alerts.add(fromMap(row));
        }
        return alerts;
    }

    /**
     * @return alerts that have not been acknowledged by any user
     */
    public static List<Alert> getUnacknowledgedAlerts() {
        List<Alert> result = new ArrayList<>();
        for (Alert a : getAllAlerts()) {
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
    public static List<Alert> getActiveAlerts() {
        return getUnacknowledgedAlerts();
    }

    /**
     * @return user-friendly formatted alert messages for display in AdminGUI
     */
    public static List<String> getActiveAlertMessages() {
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
    public static Alert findAlertById(int id) {
        Map<String, Object> row = Database.select("SELECT * FROM alerts WHERE id = ?").get(0);
        return row != null ? fromMap(row) : null;
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
    public static boolean acknowledgeAlert(int alertId, User user)
    {
        Alert alert = findAlertById(alertId);
        if (alert == null) return false;

        alert.setAcknowledged(true);
        alert.setAcknowledgedBy(user);
        alert.setAcknowledgedAt(LocalDateTime.now());
        return true;
    }
}
