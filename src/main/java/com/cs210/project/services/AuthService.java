package com.cs210.project.services;

import com.cs210.project.config.Session;
import com.cs210.project.models.Account;
import com.cs210.project.models.Member;
import com.cs210.project.models.Person;
import com.cs210.project.repositories.MemberRepository;

import java.time.LocalDateTime;

public class AuthService {
    private final MemberRepository memberRepo = new MemberRepository();

    public boolean login(String username, String password) {
        Account account = memberRepo.login(username, password);
        if (account != null) {
            Member member = memberRepo.findByAccountId(account.getId());
            Session.login(account, member);
            return true;
        }
        return false;
    }

    public boolean register(String name, String email, String username, String password, String license, LocalDateTime expiry) {
        Person p = new Person();
        p.setName(name);
        p.setEmail(email);
        return memberRepo.register(p, username, password, license, expiry);
    }

    public void logout() {
        Session.logout();
    }
}
