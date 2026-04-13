package com.cs210.project.authentication;

import com.cs210.project.domain.account.Account;
import com.cs210.project.domain.account.AccountRole;
import com.cs210.project.domain.account.AccountStatus;
import com.cs210.project.infrastructure.persistence.AppDatabase;
import io.ebean.Database;

import java.time.LocalDate;
import java.util.Locale;
import java.util.regex.Pattern;

public class AuthenticationService {

    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$");
    private static final int MIN_PASSWORD_LENGTH = 6;

    public AuthResult login(String rawEmail, String rawPassword) {
        String email = normalizeEmail(rawEmail);
        String password = sanitize(rawPassword);

        if (email.isBlank() || password.isBlank()) {
            return AuthResult.failure("Please enter both email and password.");
        }

        try {
            Account account = findByEmail(email);
            if (account == null || !account.matchesPassword(password)) {
                return AuthResult.failure("Invalid email or password.");
            }

            if (!account.isActive()) {
                return AuthResult.failure("This account is not active. Current status: " + account.getStatusLabel());
            }

            return AuthResult.success("Login successful.", account);
        } catch (Exception exception) {
            exception.printStackTrace();
            return AuthResult.failure(databaseUnavailableMessage());
        }
    }

    public AuthResult register(RegistrationRequest request) {
        String fullName = sanitize(request.fullName());
        String email = normalizeEmail(request.email());
        String phone = sanitize(request.phone());
        String password = sanitize(request.password());
        String confirmPassword = sanitize(request.confirmPassword());

        if (fullName.isBlank() || email.isBlank() || password.isBlank() || confirmPassword.isBlank()) {
            return AuthResult.failure("Full name, email, password, and confirm password are required.");
        }

        if (!EMAIL_PATTERN.matcher(email).matches()) {
            return AuthResult.failure("Please use a valid email address.");
        }

        if (password.length() < MIN_PASSWORD_LENGTH) {
            return AuthResult.failure("Password must be at least 6 characters long.");
        }

        if (!password.equals(confirmPassword)) {
            return AuthResult.failure("Password and confirm password do not match.");
        }

        try {
            if (findByEmail(email) != null) {
                return AuthResult.failure("That email is already registered.");
            }

            Account account = new Account();
            account.setRole(AccountRole.MEMBER);
            account.setFullName(fullName);
            account.setEmail(email);
            account.setPhone(phone.isBlank() ? null : phone);
            account.setStatus(AccountStatus.ACTIVE);
            account.setDateJoined(LocalDate.now());
            account.setPlainPassword(password);

            database().save(account);
            return AuthResult.success("Registration successful.", account);
        } catch (Exception exception) {
            exception.printStackTrace();
            return AuthResult.failure(databaseUnavailableMessage());
        }
    }

    private Account findByEmail(String email) {
        return database()
                .find(Account.class)
                .where()
                .ieq("email", email)
                .setMaxRows(1)
                .findOne();
    }

    private Database database() {
        return AppDatabase.getDatabase();
    }

    private String normalizeEmail(String value) {
        return sanitize(value).toLowerCase(Locale.ROOT);
    }

    private String sanitize(String value) {
        return value == null ? "" : value.trim();
    }

    private String databaseUnavailableMessage() {
        return "Database connection failed. Set db.url, db.user, and db.password before using authentication.";
    }
}
