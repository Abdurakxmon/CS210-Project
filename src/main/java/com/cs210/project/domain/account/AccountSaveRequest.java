package com.cs210.project.domain.account;

public record AccountSaveRequest(
        Long id,
        String fullName,
        String email,
        String phone,
        AccountRole role,
        AccountStatus status,
        String password
) {
}
