package com.cs210.project.domain.account;

public enum AccountStatus {
    ACTIVE(1, "Active"),
    CLOSED(2, "Closed"),
    CANCELED(3, "Cenceled"),
    BLACKLISTED(4, "Blacklisted");


    private final int code;
    private final String displayName;

    AccountStatus(int code, String displayName) {
        this.code = code;
        this.displayName = displayName;
    }

    public int getCode() {
        return code;
    }

    public static AccountStatus fromCode(int code) {
        for (AccountStatus status : values()) {
            if (status.code == code) return status;
        }
        throw new IllegalArgumentException("Unknown status code: " + code);
    }

    public String getDisplayName() {
        return displayName;
    }
}