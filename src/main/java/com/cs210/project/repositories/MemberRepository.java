package com.cs210.project.repositories;

import com.cs210.project.config.DatabaseConnection;
import com.cs210.project.constants.Enums.AccountStatus;
import com.cs210.project.constants.Enums.RoleType;
import com.cs210.project.models.Account;
import com.cs210.project.models.Member;
import com.cs210.project.models.Person;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MemberRepository {

    public Account login(String username, String password) {
        String sql = "SELECT a.*, p.name, p.email, p.phone FROM accounts a " +
                     "JOIN persons p ON a.person_id = p.id " +
                     "WHERE a.username = ? AND a.password_hash = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, username);
            pstmt.setString(2, password); // Simplified: should be hashed

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    Account account = new Account();
                    account.setId(rs.getInt("id"));
                    account.setPersonId(rs.getInt("person_id"));
                    account.setUsername(rs.getString("username"));
                    account.setStatus(AccountStatus.fromInt(rs.getInt("status")));
                    account.setRoleType(RoleType.fromInt(rs.getInt("role_type")));
                    
                    Person person = new Person();
                    person.setId(rs.getInt("person_id"));
                    person.setName(rs.getString("name"));
                    person.setEmail(rs.getString("email"));
                    person.setPhone(rs.getString("phone"));
                    account.setPerson(person);
                    
                    return account;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public Member findByAccountId(int accountId) {
        String sql = "SELECT * FROM members WHERE account_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, accountId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    Member member = new Member();
                    member.setId(rs.getInt("id"));
                    member.setAccountId(rs.getInt("account_id"));
                    member.setDriverLicenseNumber(rs.getString("driver_license_number"));
                    Timestamp expiry = rs.getTimestamp("driver_license_expiry");
                    if (expiry != null) member.setDriverLicenseExpiry(expiry.toLocalDateTime());
                    return member;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    public boolean register(Person p, String username, String password, String license, java.time.LocalDateTime expiry) {
        Connection conn = null;
        try {
            conn = DatabaseConnection.getConnection();
            conn.setAutoCommit(false);

            // 1. Insert Person
            String sqlPerson = "INSERT INTO persons (name, email, phone, street_address, city, state, zipcode, country) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
            PreparedStatement pstmtPerson = conn.prepareStatement(sqlPerson, Statement.RETURN_GENERATED_KEYS);
            pstmtPerson.setString(1, p.getName());
            pstmtPerson.setString(2, p.getEmail());
            pstmtPerson.setString(3, p.getPhone());
            pstmtPerson.setString(4, p.getStreetAddress());
            pstmtPerson.setString(5, p.getCity());
            pstmtPerson.setString(6, p.getState());
            pstmtPerson.setString(7, p.getZipcode());
            pstmtPerson.setString(8, p.getCountry());
            pstmtPerson.executeUpdate();
            
            int personId;
            try (ResultSet rs = pstmtPerson.getGeneratedKeys()) {
                if (rs.next()) personId = rs.getInt(1); else throw new SQLException("Person ID failed");
            }

            // 2. Insert Account
            String sqlAccount = "INSERT INTO accounts (person_id, username, password_hash, status, role_type) VALUES (?, ?, ?, ?, ?)";
            PreparedStatement pstmtAccount = conn.prepareStatement(sqlAccount, Statement.RETURN_GENERATED_KEYS);
            pstmtAccount.setInt(1, personId);
            pstmtAccount.setString(2, username);
            pstmtAccount.setString(3, password);
            pstmtAccount.setInt(4, AccountStatus.ACTIVE.getValue());
            pstmtAccount.setInt(5, RoleType.MEMBER.getValue());
            pstmtAccount.executeUpdate();
            
            int accountId;
            try (ResultSet rs = pstmtAccount.getGeneratedKeys()) {
                if (rs.next()) accountId = rs.getInt(1); else throw new SQLException("Account ID failed");
            }

            // 3. Insert Member
            String sqlMember = "INSERT INTO members (account_id, driver_license_number, driver_license_expiry) VALUES (?, ?, ?)";
            PreparedStatement pstmtMember = conn.prepareStatement(sqlMember);
            pstmtMember.setInt(1, accountId);
            pstmtMember.setString(2, license);
            pstmtMember.setTimestamp(3, Timestamp.valueOf(expiry));
            pstmtMember.executeUpdate();

            conn.commit();
            return true;
        } catch (SQLException e) {
            if (conn != null) try { conn.rollback(); } catch (SQLException ex) { ex.printStackTrace(); }
            e.printStackTrace();
            return false;
        } finally {
            if (conn != null) try { conn.close(); } catch (SQLException e) { e.printStackTrace(); }
        }
    }

    public List<Member> findAllMembers() {
        List<Member> list = new ArrayList<>();
        String sql = "SELECT m.*, p.name FROM members m " +
                     "JOIN accounts a ON m.account_id = a.id " +
                     "JOIN persons p ON a.person_id = p.id";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Member m = new Member();
                m.setId(rs.getInt("id"));
                m.setAccountId(rs.getInt("account_id"));
                m.setDriverLicenseNumber(rs.getString("driver_license_number"));
                // We'll use a transient field for the name if available, or just use toString
                // For simplicity in the UI, I'll assume Member.toString() or a custom mapper
                list.add(m);
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }
}
