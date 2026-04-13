package com.cs210.project.authentication;

import com.cs210.project.domain.account.Account;

public record AuthResult(boolean success, String message, Account account) {

    public static AuthResult success(String message, Account account) {
        return new AuthResult(true, message, account);
    }

    public static AuthResult failure(String message) {
        return new AuthResult(false, message, null);
    }
}
