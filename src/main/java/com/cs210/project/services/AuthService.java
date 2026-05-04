package com.cs210.project.services;

import com.cs210.project.config.Session;
import com.cs210.project.models.Account;
import com.cs210.project.models.Member;
import com.cs210.project.models.Person;
import com.cs210.project.repositories.MemberRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;

public class AuthService {
    private final MemberRepository memberRepo = new MemberRepository();
    private static final int MINIMUM_AGE = 18;
    private static final String PASSWORD_RULE = "^(?=.*[A-Z])(?=.*\\d).{8,}$";

    public boolean login(String username, String password) {
        Account account = memberRepo.login(username, password);
        if (account != null) {
            Member member = memberRepo.findByAccountId(account.getId());
            Session.login(account, member);
            return true;
        }
        return false;
    }

    public boolean register(String name, String email, String phone, String streetAddress, String city, String state,
                            String zipcode, java.time.LocalDate birthDate, String username, String password,
                            String license, LocalDateTime expiry) {
        validateRegistrationData(birthDate, password, expiry);

        Person p = new Person();
        p.setName(name);
        p.setEmail(email);
        p.setPhone(phone);
        p.setStreetAddress(streetAddress);
        p.setCity(city);
        p.setState(state);
        p.setZipcode(zipcode);
        p.setBirthDate(birthDate);
        return memberRepo.register(p, username, password, license, expiry);
    }

    private void validateRegistrationData(LocalDate birthDate, String password, LocalDateTime licenseExpiry) {
        if (password == null || !password.matches(PASSWORD_RULE)) {
            throw new IllegalArgumentException("Password must be at least 8 characters, include 1 uppercase letter and 1 number.");
        }
        if (birthDate == null || Period.between(birthDate, LocalDate.now()).getYears() < MINIMUM_AGE) {
            throw new IllegalArgumentException("You must be at least " + MINIMUM_AGE + " years old.");
        }
        if (licenseExpiry == null || !licenseExpiry.toLocalDate().isAfter(LocalDate.now())) {
            throw new IllegalArgumentException("License expiry must be a future date.");
        }
    }
}
