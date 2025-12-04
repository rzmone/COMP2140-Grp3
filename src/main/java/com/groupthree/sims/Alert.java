package com.groupthree.sims;

import java.time.LocalDateTime;

public class Alert {

    private long id;
    private AlertType type;
    private AlertSeverity severity;
    private String message;
    private String relatedEntity; // e.g. itemCode, batchId, subsystem
    private Double thresholdValue;
    private Double actualValue;
    private LocalDateTime createdAt;

    private boolean acknowledged;
    private User acknowledgedBy;
    private LocalDateTime acknowledgedAt;

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
        this.acknowledged = false;
    }

    // --------- Getters & Setters ---------

    public long getId() { return id; }

    public void setId(long id) { this.id = id; }

    public AlertType getType() { return type; }

    public void setType(AlertType type) { this.type = type; }

    public AlertSeverity getSeverity() { return severity; }

    public void setSeverity(AlertSeverity severity) { this.severity = severity; }

    public String getMessage() { return message; }

    public String getRelatedEntity() { return relatedEntity; }

    public void setRelatedEntity(String relatedEntity) {
        this.relatedEntity = relatedEntity;
    }

    public Double getThresholdValue() { return thresholdValue; }

    public void setThresholdValue(Double thresholdValue) {
        this.thresholdValue = thresholdValue;
    }

    public Double getActualValue() { return actualValue; }

    public void setActualValue(Double actualValue) {
        this.actualValue = actualValue;
    }

    public LocalDateTime getCreatedAt() { return createdAt; }

    public boolean isAcknowledged() { return acknowledged; }

    public void setAcknowledged(boolean acknowledged) {
        this.acknowledged = acknowledged;
    }

    public User getAcknowledgedBy() { return acknowledgedBy; }

    public void setAcknowledgedBy(User acknowledgedBy) {
        this.acknowledgedBy = acknowledgedBy;
    }

    public LocalDateTime getAcknowledgedAt() { return acknowledgedAt; }

    public void setAcknowledgedAt(LocalDateTime acknowledgedAt) {
        this.acknowledgedAt = acknowledgedAt;
    }

    @Override
    public String toString() {
        String prefix = "[" + severity + "] (" + type + ") ";
        return prefix + message;
    }
}

