package com.cs210.project.domain.account;

public record AccountOperationResult(boolean success, String message, Account account) {

    public static AccountOperationResult success(String message, Account account) {
        return new AccountOperationResult(true, message, account);
    }

    public static AccountOperationResult failure(String message) {
        return new AccountOperationResult(false, message, null);
    }
}
