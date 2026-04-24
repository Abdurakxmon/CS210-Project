package com.cs210.project.controllers;

import com.cs210.project.models.Account;

import java.time.LocalDate;
import java.util.List;
import java.util.Locale;
import java.util.regex.Pattern;

public class AccountController {

    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$");
    private static final int MIN_PASSWORD_LENGTH = 6;

    public List<Account> findAllAccounts() {
        return Account.findAll();
    }

    public OperationResult saveAccount(
            Long accountId,
            String fullName,
            String email,
            String phone,
            Account.Role role,
            Account.Status status,
            String password
    ) {
        boolean creating = accountId == null;

        String cleanFullName = sanitize(fullName);
        String cleanEmail = normalizeEmail(email);
        String cleanPhone = sanitize(phone);
        String cleanPassword = sanitize(password);

        if (cleanFullName.isBlank() || cleanEmail.isBlank()) {
            return OperationResult.failure("Full name and email are required.");
        }

        if (!EMAIL_PATTERN.matcher(cleanEmail).matches()) {
            return OperationResult.failure("Please use a valid email address.");
        }

        if (role == null) {
            return OperationResult.failure("Please choose a role.");
        }

        if (status == null) {
            return OperationResult.failure("Please choose a status.");
        }

        if (creating && cleanPassword.isBlank()) {
            return OperationResult.failure("Password is required for a new account.");
        }

        if (!cleanPassword.isBlank() && cleanPassword.length() < MIN_PASSWORD_LENGTH) {
            return OperationResult.failure("Password must be at least 6 characters long.");
        }

        Account existingWithEmail = Account.findByEmail(cleanEmail);
        if (existingWithEmail != null && (accountId == null || !existingWithEmail.getId().equals(accountId))) {
            return OperationResult.failure("That email is already registered.");
        }

        Account account = creating ? new Account() : Account.findById(accountId);
        if (account == null) {
            return OperationResult.failure("Selected account could not be found.");
        }

        account.setFullName(cleanFullName);
        account.setEmail(cleanEmail);
        account.setPhone(cleanPhone.isBlank() ? null : cleanPhone);
        account.setRole(role);
        account.setStatus(status);
        if (creating && account.getDateJoined() == null) {
            account.setDateJoined(LocalDate.now());
        }
        if (!cleanPassword.isBlank()) {
            account.setPlainPassword(cleanPassword);
        }

        if (creating) {
            account.save();
        } else {
            account.update();
        }

        return OperationResult.success(
                creating ? "Account created successfully." : "Account updated successfully.",
                account
        );
    }

    public OperationResult deleteAccount(Long accountId, Long signedInUserId) {
        if (accountId == null) {
            return OperationResult.failure("Choose an account to delete.");
        }

        if (accountId.equals(signedInUserId)) {
            return OperationResult.failure("You cannot delete the account you are currently using.");
        }

        Account account = Account.findById(accountId);
        if (account == null) {
            return OperationResult.failure("Selected account could not be found.");
        }

        account.delete();
        return OperationResult.success("Account deleted successfully.", account);
    }

    private String normalizeEmail(String value) {
        return sanitize(value).toLowerCase(Locale.ROOT);
    }

    private String sanitize(String value) {
        return value == null ? "" : value.trim();
    }

    public record OperationResult(boolean success, String message, Account account) {
        public static OperationResult success(String message, Account account) {
            return new OperationResult(true, message, account);
        }

        public static OperationResult failure(String message) {
            return new OperationResult(false, message, null);
        }
    }
}
