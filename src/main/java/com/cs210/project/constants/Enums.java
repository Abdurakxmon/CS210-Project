package com.cs210.project.constants;

public class Enums {

    public enum VehicleType {
        CAR(1, "Car"), TRUCK(2, "Truck"), SUV(3, "SUV"), VAN(4, "Van"), MOTORCYCLE(5, "Motorcycle");
        private final int value;
        private final String label;
        VehicleType(int v, String l) { this.value = v; this.label = l; }
        public int getValue() { return value; }
        public String getLabel() { return label; }
        public static VehicleType fromInt(int v) {
            for (VehicleType t : values()) if (t.value == v) return t;
            return CAR;
        }
    }

    public enum CarType {
        ECONOMY(1, "Economy"), COMPACT(2, "Compact"), INTERMEDIATE(3, "Intermediate"), 
        STANDARD(4, "Standard"), FULL_SIZE(5, "Full Size"), PREMIUM(6, "Premium"), LUXURY(7, "Luxury");
        private final int value;
        private final String label;
        CarType(int v, String l) { this.value = v; this.label = l; }
        public int getValue() { return value; }
        public String getLabel() { return label; }
        public static CarType fromInt(int v) {
            for (CarType t : values()) if (t.value == v) return t;
            return ECONOMY;
        }
    }

    public enum ReservationStatus {
        WAITING(1, "Waiting"), PENDING(2, "Pending"), CONFIRMED(3, "Confirmed"), 
        COMPLETED(4, "Completed"), CANCELLED(5, "Cancelled"), NONE(6, "None"),
        WAITING_FOR_INSPECTION(7, "Waiting for Inspection"), OVERDUE(8, "Overdue");
        private final int value;
        private final String label;
        ReservationStatus(int v, String l) { this.value = v; this.label = l; }
        public int getValue() { return value; }
        public String getLabel() { return label; }
        public static ReservationStatus fromInt(int v) {
            for (ReservationStatus s : values()) if (s.value == v) return s;
            return NONE;
        }
    }

    public enum AccountStatus {
        ACTIVE(1, "Active"), CLOSED(2, "Closed"), CANCELLED(3, "Cancelled"), BLACKLISTED(4, "Blacklisted"), NONE(5, "None");
        private final int value;
        private final String label;
        AccountStatus(int v, String l) { this.value = v; this.label = l; }
        public int getValue() { return value; }
        public String getLabel() { return label; }
        public static AccountStatus fromInt(int v) {
            for (AccountStatus s : values()) if (s.value == v) return s;
            return NONE;
        }
    }

    public enum PaymentStatus {
        UNPAID(1, "Unpaid"), PENDING(2, "Pending"), COMPLETED(3, "Completed"), 
        FAILED(4, "Failed"), DECLINED(5, "Declined"), CANCELLED(6, "Cancelled"), 
        ABANDONED(7, "Abandoned"), SETTLING(8, "Settling"), SETTLED(9, "Settled"), REFUNDED(10, "Refunded");
        private final int value;
        private final String label;
        PaymentStatus(int v, String l) { this.value = v; this.label = l; }
        public int getValue() { return value; }
        public String getLabel() { return label; }
        public static PaymentStatus fromInt(int v) {
            for (PaymentStatus s : values()) if (s.value == v) return s;
            return UNPAID;
        }
    }

    public enum BillItemType {
        BASE_CHARGE(1, "Base Charge"), INSURANCE(2, "Insurance"), EQUIPMENT(3, "Equipment"),
        CANCELLATION_FEE(4, "Cancellation Fee"),
        LATE_FEE(5, "Late Fee"), DAMAGE_FEE(6, "Damage Fee"),
        FUEL_FEE(7, "Fuel Fee"), OTHER(8, "Other");
        private final int value;
        private final String label;
        BillItemType(int v, String l) { this.value = v; this.label = l; }
        public int getValue() { return value; }
        public String getLabel() { return label; }
        public static BillItemType fromInt(int v) {
            for (BillItemType t : values()) if (t.value == v) return t;
            return OTHER;
        }
    }

    public enum VehicleLogType {
        ACCIDENT(1, "Accident"), FUELING(2, "Fueling"), CLEANING_SERVICE(3, "Cleaning Service"), 
        OIL_CHANGE(4, "Oil Change"), REPAIR(5, "Repair"), OTHER(6, "Other");
        private final int value;
        private final String label;
        VehicleLogType(int v, String l) { this.value = v; this.label = l; }
        public int getValue() { return value; }
        public String getLabel() { return label; }
        public static VehicleLogType fromInt(int v) {
            for (VehicleLogType t : values()) if (t.value == v) return t;
            return OTHER;
        }
    }

    public enum RoleType {
        MEMBER(1, "Member"), RECEPTIONIST(2, "Receptionist"), WORKER(3, "Worker"), SUPER_ADMIN(4, "Super Admin");
        private final int value;
        private final String label;
        RoleType(int v, String l) { this.value = v; this.label = l; }
        public int getValue() { return value; }
        public String getLabel() { return label; }
        public static RoleType fromInt(int v) {
            for (RoleType r : values()) if (r.value == v) return r;
            return MEMBER;
        }
    }
    
    public enum InsuranceType {
        BASIC(1, "Additional insurance (deductible)"), PERSONAL(2, "Personal"), BELONGINGS(3, "Belongings");
        private final int value;
        private final String label;
        InsuranceType(int v, String l) { this.value = v; this.label = l; }
        public int getValue() { return value; }
        public String getLabel() { return label; }
        public static InsuranceType fromInt(int v) {
            for (InsuranceType t : values()) if (t.value == v) return t;
            return BASIC;
        }
    }

    public enum EquipmentType {
        NAVIGATION(1, "Navigation"), CHILD_SEAT(2, "Child Seat"), WIFI(3, "Wi-Fi Hotspot"), CAR_FRIDGE(4, "Car fridge");
        private final int value;
        private final String label;
        EquipmentType(int v, String l) { this.value = v; this.label = l; }
        public int getValue() { return value; }
        public String getLabel() { return label; }
        public static EquipmentType fromInt(int v) {
            for (EquipmentType t : values()) if (t.value == v) return t;
            return NAVIGATION;
        }
    }

    public enum PaymentType {
        CREDIT_CARD(1, "Credit Card"), CHECK(2, "Check"), CASH(3, "Cash");
        private final int value;
        private final String label;
        PaymentType(int v, String l) { this.value = v; this.label = l; }
        public int getValue() { return value; }
        public String getLabel() { return label; }
        public static PaymentType fromInt(int v) {
            for (PaymentType t : values()) if (t.value == v) return t;
            return CASH;
        }
    }

    public enum NotificationType {
        RESERVATION_CONFIRMATION(1, "Reservation Confirmation"),
        RESERVATION_REMINDER(2, "Reservation Reminder"),
        CANCELLATION_NOTIFICATION(3, "Cancellation Notification"),
        PICKUP_REMINDER(4, "Pickup Reminder"),
        DUE_DATE_REMINDER(5, "Due Date Reminder"),
        OVERDUE_WARNING(6, "Overdue Warning"),
        RETURN_CONFIRMATION(7, "Return Confirmation"),
        PAYMENT_CONFIRMATION(8, "Payment Confirmation"),
        LATE_FEE_ADDED(9, "Late Fee Added"),
        DAMAGE_FEE_ADDED(10, "Damage Fee Added"),
        FUEL_FEE_ADDED(11, "Fuel Fee Added"),
        SYSTEM(12, "System");
        private final int value;
        private final String label;
        NotificationType(int v, String l) { this.value = v; this.label = l; }
        public int getValue() { return value; }
        public String getLabel() { return label; }
        public static NotificationType fromInt(int v) {
            for (NotificationType t : values()) if (t.value == v) return t;
            return SYSTEM;
        }
    }

    public enum TransmissionType {
        AUTOMATIC(1, "Automatic"), MANUAL(2, "Manual");
        private final int value;
        private final String label;
        TransmissionType(int v, String l) { this.value = v; this.label = l; }
        public int getValue() { return value; }
        public String getLabel() { return label; }
        public static TransmissionType fromInt(int v) {
            for (TransmissionType t : values()) if (t.value == v) return t;
            return AUTOMATIC;
        }
    }

    public enum FuelType {
        PETROL(1, "Benzin"), DIESEL(2, "Propane"), HYBRID(3, "Hybrid"), ELECTRIC(4, "Electric");
        private final int value;
        private final String label;
        FuelType(int v, String l) { this.value = v; this.label = l; }
        public int getValue() { return value; }
        public String getLabel() { return label; }
        public static FuelType fromInt(int v) {
            for (FuelType t : values()) if (t.value == v) return t;
            return PETROL;
        }
    }
}
