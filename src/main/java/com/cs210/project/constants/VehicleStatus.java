package com.cs210.project.constants;

public enum VehicleStatus {
    AVAILABLE(1),
    RESERVED(2),
    LOANED(3),
    LOST(4),
    BEING_SERVICED(5),
    OTHER(6);

    private final int value;

    VehicleStatus(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public static VehicleStatus fromInt(int value) {
        for (VehicleStatus status : values()) {
            if (status.value == value) return status;
        }
        return OTHER;
    }
}
