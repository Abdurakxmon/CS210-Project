package com.cs210.project.config;

import com.cs210.project.models.Account;
import com.cs210.project.models.Member;

/**
 * Session class to track the currently logged-in user and their role.
 */
public class Session {
    private static Account currentAccount;
    private static Member currentMember;

    public static void login(Account account, Member member) {
        currentAccount = account;
        currentMember = member;
    }

    public static void logout() {
        currentAccount = null;
        currentMember = null;
    }

    public static Account getAccount() {
        return currentAccount;
    }

    public static Member getMember() {
        return currentMember;
    }

    public static boolean isLoggedIn() {
        return currentAccount != null;
    }

    public static boolean isMember() {
        return isLoggedIn() && currentAccount.getRoleType() == com.cs210.project.constants.Enums.RoleType.MEMBER;
    }

    public static boolean isReceptionist() {
        return isLoggedIn() && currentAccount.getRoleType() == com.cs210.project.constants.Enums.RoleType.RECEPTIONIST;
    }

    public static boolean isSuperAdmin() {
        return isLoggedIn() && currentAccount.getRoleType() == com.cs210.project.constants.Enums.RoleType.SUPER_ADMIN;
    }
}
