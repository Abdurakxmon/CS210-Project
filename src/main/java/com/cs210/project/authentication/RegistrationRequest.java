package com.cs210.project.authentication;

public record RegistrationRequest(
        String fullName,
        String email,
        String phone,
        String password,
        String confirmPassword
) {
}
