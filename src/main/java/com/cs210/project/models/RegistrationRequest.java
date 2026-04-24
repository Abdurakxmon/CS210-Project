package com.cs210.project.models;

public record RegistrationRequest(
        String fullName,
        String email,
        String phone,
        String password,
        String confirmPassword
) {
}
