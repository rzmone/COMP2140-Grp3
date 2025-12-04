package com.groupthree.sims;

import java.time.LocalDateTime;

/**
 * Represents a system-generated event or warning that notifies users
 * about abnormal conditions, threshold violations, or important system activity.
 *
 * Alerts may include:
 *  - Inventory shortages or Overstock warnings
 *  - Production issues
 *  - Security or login events
 *  - Equipment failures
 *
 * Each alert contains metadata describing severity, type, and values related
 * to the condition being reported. Alerts may also be acknowledged by users.
 */
public class Alert {

    /** Unique identifier for the alert entry */
    private long id;

    /** Category/type of alert (e.g., INVENTORY, SECURITY, SYSTEM, PRODUCTION) */
    private AlertType type;

    /** Severity level indicating urgency and required action */
    private AlertSeverity severity;

    /** Human-readable message describing the alert condition */
    private String message;

    /** Optional reference to a related subsystem or business entity (e.g., itemCode, batchId) */
    private String relatedEntity;

    /** Expected threshold limit for this alert (if applicable) */
    private Double thresholdValue;

    /** Actual measured or detected value that triggered the alert */
    private Double actualValue;

    /** Timestamp when the alert was generated */
    private LocalDateTime createdAt;

    /** Indicates whether the alert has been acknowledged by a user */
    private boolean acknowledged;

    /** User who acknowledged the alert (if any) */
    private User acknowledgedBy;

    /** Timestamp when the alert was acknowledged */
    private LocalDateTime acknowledgedAt;


    /**
     * Constructs a new Alert instance containing all required alert metadata.
     *
     * @param id              unique identifier
     * @param type            type/category of alert
     * @param severity        importance or urgency level
     * @param message         primary alert message
     * @param relatedEntity   optional related entity ID or reference
     * @param thresholdValue  expected threshold that was violated (nullable)
     * @param actualValue     actual measured value (nullable)
     * @param createdAt       timestamp when the alert was generated
     */
    public Alert(long id,
                 AlertType type,
                 AlertSeverity severity,
                 String message,
                 String relatedEntity,
                 Double thresholdValue,
                 Double actualValue,
                 LocalDateTime createdAt) {

        this.id = id;
        this.type = type;
        this.severity = severity;
        this.message = message;
        this.relatedEntity = relatedEntity;
        this.thresholdValue = thresholdValue;
        this.actualValue = actualValue;
        this.createdAt = createdAt;

        // Alerts start unacknowledged
        this.acknowledged = false;
    }


    /* ===========================================================
       Getters and Setters
       =========================================================== */

    public long getId() { return id; }
    public void setId(long id) { this.id = id; }

    public AlertType getType() { return type; }
    public void setType(AlertType type) { this.type = type; }

    public AlertSeverity getSeverity() { return severity; }
    public void setSeverity(AlertSeverity severity) { this.severity = severity; }

    public String getMessage() { return message; }

    public String getRelatedEntity() { return relatedEntity; }
    public void setRelatedEntity(String relatedEntity) { this.relatedEntity = relatedEntity; }

    public Double getThresholdValue() { return thresholdValue; }
    public void setThresholdValue(Double thresholdValue) { this.thresholdValue = thresholdValue; }

    public Double getActualValue() { return actualValue; }
    public void setActualValue(Double actualValue) { this.actualValue = actualValue; }

    public LocalDateTime getCreatedAt() { return createdAt; }

    public boolean isAcknowledged() { return acknowledged; }
    public void setAcknowledged(boolean acknowledged) { this.acknowledged = acknowledged; }

    public User getAcknowledgedBy() { return acknowledgedBy; }
    public void setAcknowledgedBy(User acknowledgedBy) { this.acknowledgedBy = acknowledgedBy; }

    public LocalDateTime getAcknowledgedAt() { return acknowledgedAt; }
    public void setAcknowledgedAt(LocalDateTime acknowledgedAt) { this.acknowledgedAt = acknowledgedAt; }


    /**
     * @return a simple formatted representation showing severity, type, and message
     */
    @Override
    public String toString() {
        String prefix = "[" + severity + "] (" + type + ") ";
        return prefix + message;
    }
}
