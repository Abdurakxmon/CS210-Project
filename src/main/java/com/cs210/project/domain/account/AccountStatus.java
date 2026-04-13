package com.cs210.project.domain.account;

public enum AccountStatus {
    ACTIVE("active", "Active"),
    CLOSED("closed", "Closed"),
    CANCELED("canceled", "Canceled"),
    BLACKLISTED("blacklisted", "Blacklisted");

    private final String databaseValue;
    private final String displayName;

    AccountStatus(String databaseValue, String displayName) {
        this.databaseValue = databaseValue;
        this.displayName = displayName;
    }

    public String getDatabaseValue() {
        return databaseValue;
    }

    public String getDisplayName() {
        return displayName;
    }

    public static AccountStatus fromDatabaseValue(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }

        String normalized = value.trim().toLowerCase();
        for (AccountStatus status : values()) {
            if (status.databaseValue.equals(normalized)) {
                return status;
            }
        }

        throw new IllegalArgumentException("Unknown account status: " + value);
    }

    @Override
    public String toString() {
        return displayName;
    }
}
