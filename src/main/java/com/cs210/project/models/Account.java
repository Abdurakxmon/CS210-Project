package com.cs210.project.models;

import com.cs210.project.config.AppDatabase;
import com.cs210.project.config.PasswordUtil;
import io.ebean.Database;
import io.ebean.annotation.WhenCreated;
import io.ebean.annotation.WhenModified;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Locale;

@Entity
@Table(name = "accounts")
public class Account {

    @Id
    @GeneratedValue
    private Long id;

    @Column(name = "role", nullable = false)
    private Integer roleCode;

    @Column(name = "full_name", nullable = false, length = 150)
    private String fullName;

    @Column(length = 150, unique = true)
    private String email;

    @Column(length = 30)
    private String phone;

    @Column(name = "password_hash", nullable = false, length = 255)
    private String passwordHash;

    @Column(name = "status", nullable = false)
    private Integer statusCode;

    @Column(name = "driver_license_number", length = 100)
    private String driverLicenseNumber;

    @Column(name = "driver_license_expiry")
    private LocalDate driverLicenseExpiry;

    @Column(name = "date_joined")
    private LocalDate dateJoined;

    @Column(name = "street_address", length = 255)
    private String streetAddress;

    @Column(length = 100)
    private String city;

    @Column(length = 100)
    private String state;

    @Column(name = "zip_code", length = 20)
    private String zipCode;

    @Column(length = 100)
    private String country;

    @WhenCreated
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @WhenModified
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Role getRole() {
        return Role.fromCode(roleCode);
    }

    public void setRole(Role role) {
        this.roleCode = role == null ? null : role.getCode();
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public Status getStatus() {
        return Status.fromCode(statusCode);
    }

    public void setStatus(Status status) {
        this.statusCode = status == null ? null : status.getCode();
    }

    public String getDriverLicenseNumber() {
        return driverLicenseNumber;
    }

    public void setDriverLicenseNumber(String driverLicenseNumber) {
        this.driverLicenseNumber = driverLicenseNumber;
    }

    public LocalDate getDriverLicenseExpiry() {
        return driverLicenseExpiry;
    }

    public void setDriverLicenseExpiry(LocalDate driverLicenseExpiry) {
        this.driverLicenseExpiry = driverLicenseExpiry;
    }

    public LocalDate getDateJoined() {
        return dateJoined;
    }

    public void setDateJoined(LocalDate dateJoined) {
        this.dateJoined = dateJoined;
    }

    public String getStreetAddress() {
        return streetAddress;
    }

    public void setStreetAddress(String streetAddress) {
        this.streetAddress = streetAddress;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getZipCode() {
        return zipCode;
    }

    public void setZipCode(String zipCode) {
        this.zipCode = zipCode;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public void setPlainPassword(String plainPassword) {
        this.passwordHash = PasswordUtil.hashPassword(plainPassword);
    }

    public boolean matchesPassword(String plainPassword) {
        return PasswordUtil.verifyPassword(plainPassword, passwordHash);
    }

    public boolean isActive() {
        return getStatus() == Status.ACTIVE;
    }

    public boolean isSuperAdmin() {
        return getRole() == Role.SUPER_ADMIN;
    }

    public String getDisplayName() {
        return fullName == null || fullName.isBlank() ? "Member" : fullName;
    }

    public String getRoleLabel() {
        Role role = getRole();
        return role == null ? "-" : role.getDisplayName();
    }

    public String getStatusLabel() {
        Status status = getStatus();
        return status == null ? "-" : status.getDisplayName();
    }

    public void save() {
        database().save(this);
    }

    public void update() {
        if (id == null) {
            throw new IllegalStateException("Cannot update an account without an id.");
        }
        database().update(this);
    }

    public void delete() {
        if (id == null) {
            return;
        }
        database().delete(this);
    }

    public static Account findById(Long id) {
        if (id == null) {
            return null;
        }
        return database().find(Account.class, id);
    }

    public static Account findByEmail(String rawEmail) {
        String email = normalizeEmail(rawEmail);
        if (email.isBlank()) {
            return null;
        }
        return database()
                .find(Account.class)
                .where()
                .ieq("email", email)
                .setMaxRows(1)
                .findOne();
    }

    public static List<Account> findAll() {
        return database()
                .find(Account.class)
                .orderBy("id asc")
                .findList();
    }

    private static Database database() {
        return AppDatabase.getDatabase();
    }

    private static String normalizeEmail(String value) {
        return value == null ? "" : value.trim().toLowerCase(Locale.ROOT);
    }

    public enum Role {
        MEMBER(1, "Member"),
        RECEPTIONIST(2, "Receptionist"),
        WORKER(3, "Worker"),
        SUPER_ADMIN(4, "Super Admin");

        private final int code;
        private final String displayName;

        Role(int code, String displayName) {
            this.code = code;
            this.displayName = displayName;
        }

        public int getCode() {
            return code;
        }

        public static Role fromCode(Integer code) {
            if (code == null) {
                return null;
            }
            for (Role role : values()) {
                if (role.code == code) {
                    return role;
                }
            }
            return null;
        }

        public String getDisplayName() {
            return displayName;
        }

        @Override
        public String toString() {
            return displayName;
        }
    }

    public enum Status {
        ACTIVE(1, "Active"),
        CLOSED(2, "Closed"),
        CANCELED(3, "Canceled"),
        BLACKLISTED(4, "Blacklisted");

        private final int code;
        private final String displayName;

        Status(int code, String displayName) {
            this.code = code;
            this.displayName = displayName;
        }

        public int getCode() {
            return code;
        }

        public static Status fromCode(Integer code) {
            if (code == null) {
                return null;
            }
            for (Status status : values()) {
                if (status.code == code) {
                    return status;
                }
            }
            return null;
        }

        public String getDisplayName() {
            return displayName;
        }

        @Override
        public String toString() {
            return displayName;
        }
    }
}
