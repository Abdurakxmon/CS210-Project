package com.cs210.project.domain.account;

public enum AccountRole {
    MEMBER(1, "Member"),
    RECEPTIONIST(2, "Receptionist"),
    WORKER(3, "Worker"),
    SUPER_ADMIN(4, "Super Admin");


    private final int code;
    private final String displayName;

    AccountRole(int code, String displayName) {
        this.code = code;
        this.displayName = displayName;
    }

    public int getCode() {
        return code;
    }

    public static AccountRole fromCode(int code) {
        for (AccountRole role : values()) {
            if (role.code == code) return role;
        }
        throw new IllegalArgumentException("Unknown role code: " + code);
    }

    public String getDisplayName() {
        return displayName;
    }
}