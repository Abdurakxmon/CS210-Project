package com.cs210.project.domain.account;

public enum AccountRole {
    MEMBER("member", "Member"),
    RECEPTIONIST("receptionist", "Receptionist"),
    WORKER("worker", "Worker"),
    SUPER_ADMIN("super_admin", "Super Admin");

    private final String databaseValue;
    private final String displayName;

    AccountRole(String databaseValue, String displayName) {
        this.databaseValue = databaseValue;
        this.displayName = displayName;
    }

    public String getDatabaseValue() {
        return databaseValue;
    }

    public String getDisplayName() {
        return displayName;
    }

    public static AccountRole fromDatabaseValue(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }

        String normalized = value.trim().toLowerCase();
        if ("admin".equals(normalized)) {
            return SUPER_ADMIN;
        }

        for (AccountRole role : values()) {
            if (role.databaseValue.equals(normalized)) {
                return role;
            }
        }

        throw new IllegalArgumentException("Unknown account role: " + value);
    }

    @Override
    public String toString() {
        return displayName;
    }
}
