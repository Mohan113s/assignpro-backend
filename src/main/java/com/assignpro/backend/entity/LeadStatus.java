package com.assignpro.backend.entity;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

/**
 * Lead statuses matching Flutter frontend's AppConstants.leadStatuses.
 * Uses @JsonValue/@JsonCreator for seamless Flutter ↔ Backend JSON mapping.
 */
public enum LeadStatus {

    NEW("New"),
    PENDING("Pending"),
    FOLLOW_UP("Follow Up"),
    INTERESTED("Interested"),
    NOT_INTERESTED("Not Interested"),
    BUSY("Busy"),
    NO_RESPONSE("No Response"),
    WRONG_NUMBER("Wrong Number"),
    COMPLETED("Completed"),
    CONTACTED("Contacted"),
    NEGOTIATION("Negotiation"),
    WON("Won"),
    LOST("Lost");

    private final String displayName;

    LeadStatus(String displayName) {
        this.displayName = displayName;
    }

    @JsonValue
    public String getDisplayName() {
        return displayName;
    }

    /**
     * Parses status from Flutter's display name or Java enum name.
     * Handles both "Follow Up" (from Flutter) and "FOLLOW_UP" (from DB).
     */
    @JsonCreator
    public static LeadStatus fromValue(String value) {
        if (value == null || value.isBlank())
            return PENDING;
        // Try display name match first
        for (LeadStatus status : values()) {
            if (status.displayName.equalsIgnoreCase(value)) {
                return status;
            }
        }
        // Try enum name match (e.g., "FOLLOW_UP")
        try {
            return LeadStatus.valueOf(value.toUpperCase().replace(" ", "_"));
        } catch (IllegalArgumentException e) {
            return PENDING; // Safe default
        }
    }
}