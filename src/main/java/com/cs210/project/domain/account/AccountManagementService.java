package com.cs210.project.domain.account;

import com.cs210.project.infrastructure.persistence.AppDatabase;
import io.ebean.Database;

import java.time.LocalDate;
import java.util.List;
import java.util.Locale;
import java.util.regex.Pattern;

public class AccountManagementService {

    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$");
    private static final int MIN_PASSWORD_LENGTH = 6;

    public List<Account> findAllAccounts() {
        return database()
                .find(Account.class)
                .orderBy("id asc")
                .findList();
    }

    public AccountOperationResult saveAccount(AccountSaveRequest request) {
        String fullName = sanitize(request.fullName());
        String email = normalizeEmail(request.email());
        String phone = sanitize(request.phone());
        String password = sanitize(request.password());

        if (fullName.isBlank() || email.isBlank()) {
            return AccountOperationResult.failure("Full name and email are required.");
        }

        if (!EMAIL_PATTERN.matcher(email).matches()) {
            return AccountOperationResult.failure("Please use a valid email address.");
        }

        if (request.role() == null) {
            return AccountOperationResult.failure("Please choose a role.");
        }

        if (request.status() == null) {
            return AccountOperationResult.failure("Please choose a status.");
        }

        boolean creating = request.id() == null;
        if (creating && password.isBlank()) {
            return AccountOperationResult.failure("Password is required for a new account.");
        }

        if (!password.isBlank() && password.length() < MIN_PASSWORD_LENGTH) {
            return AccountOperationResult.failure("Password must be at least 6 characters long.");
        }

        Account existingWithEmail = database()
                .find(Account.class)
                .where()
                .ieq("email", email)
                .setMaxRows(1)
                .findOne();

        if (existingWithEmail != null && !existingWithEmail.getId().equals(request.id())) {
            return AccountOperationResult.failure("That email is already registered.");
        }

        Account account = creating ? new Account() : database().find(Account.class, request.id());
        if (account == null) {
            return AccountOperationResult.failure("Selected account could not be found.");
        }

        account.setFullName(fullName);
        account.setEmail(email);
        account.setPhone(phone.isBlank() ? null : phone);
        account.setRole(request.role());
        account.setStatus(request.status());

        if (creating && account.getDateJoined() == null) {
            account.setDateJoined(LocalDate.now());
        }

        if (!password.isBlank()) {
            account.setPlainPassword(password);
        }

        database().save(account);
        return AccountOperationResult.success(
                creating ? "Account created successfully." : "Account updated successfully.",
                account
        );
    }

    public AccountOperationResult deleteAccount(Long accountId, Long signedInUserId) {
        if (accountId == null) {
            return AccountOperationResult.failure("Choose an account to delete.");
        }

        if (accountId.equals(signedInUserId)) {
            return AccountOperationResult.failure("You cannot delete the account you are currently using.");
        }

        Account account = database().find(Account.class, accountId);
        if (account == null) {
            return AccountOperationResult.failure("Selected account could not be found.");
        }

        database().delete(account);
        return AccountOperationResult.success("Account deleted successfully.", account);
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
}
