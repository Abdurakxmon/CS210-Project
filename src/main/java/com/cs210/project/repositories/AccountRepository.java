package com.cs210.project.repositories;

import com.cs210.project.config.DatabaseConnection;
import com.cs210.project.constants.Enums.AccountStatus;
import com.cs210.project.constants.Enums.RoleType;
import com.cs210.project.models.Account;
import com.cs210.project.models.Person;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AccountRepository {

    public List<Account> findAll() {
        List<Account> accounts = new ArrayList<>();
        String sql = "SELECT a.*, p.name, p.email, p.phone FROM accounts a " +
                     "JOIN persons p ON a.person_id = p.id";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                accounts.add(mapResultSetToAccount(rs));
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return accounts;
    }

    public void updateRole(int accountId, RoleType role) {
        String sql = "UPDATE accounts SET role_type = ? WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, role.getValue());
            pstmt.setInt(2, accountId);
            pstmt.executeUpdate();
        } catch (SQLException e) { e.printStackTrace(); }
    }

    public void updateStatus(int accountId, AccountStatus status) {
        String sql = "UPDATE accounts SET status = ? WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, status.getValue());
            pstmt.setInt(2, accountId);
            pstmt.executeUpdate();
        } catch (SQLException e) { e.printStackTrace(); }
    }

    public void resetPassword(int accountId, String newPassword) {
        String sql = "UPDATE accounts SET password_hash = ? WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, newPassword);
            pstmt.setInt(2, accountId);
            pstmt.executeUpdate();
        } catch (SQLException e) { e.printStackTrace(); }
    }

    public boolean create(Person p, String username, String password, RoleType role, AccountStatus status) {
        Connection conn = null;
        try {
            conn = DatabaseConnection.getConnection();
            conn.setAutoCommit(false);

            String sqlPerson = "INSERT INTO persons (name, email, phone) VALUES (?, ?, ?)";
            PreparedStatement pstmtPerson = conn.prepareStatement(sqlPerson, Statement.RETURN_GENERATED_KEYS);
            pstmtPerson.setString(1, p.getName());
            pstmtPerson.setString(2, p.getEmail());
            pstmtPerson.setString(3, p.getPhone());
            pstmtPerson.executeUpdate();
            
            int personId;
            try (ResultSet rs = pstmtPerson.getGeneratedKeys()) {
                if (rs.next()) personId = rs.getInt(1); else throw new SQLException("Person ID failed");
            }

            String sqlAccount = "INSERT INTO accounts (person_id, username, password_hash, status, role_type) VALUES (?, ?, ?, ?, ?)";
            PreparedStatement pstmtAccount = conn.prepareStatement(sqlAccount);
            pstmtAccount.setInt(1, personId);
            pstmtAccount.setString(2, username);
            pstmtAccount.setString(3, password);
            pstmtAccount.setInt(4, status.getValue());
            pstmtAccount.setInt(5, role.getValue());
            pstmtAccount.executeUpdate();

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

    public void update(Account a) {
        String sqlAccount = "UPDATE accounts SET username = ?, status = ?, role_type = ? WHERE id = ?";
        String sqlPerson = "UPDATE persons SET name = ?, email = ?, phone = ? WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection()) {
            conn.setAutoCommit(false);
            try (PreparedStatement pstmtAccount = conn.prepareStatement(sqlAccount);
                 PreparedStatement pstmtPerson = conn.prepareStatement(sqlPerson)) {
                
                pstmtAccount.setString(1, a.getUsername());
                pstmtAccount.setInt(2, a.getStatus().getValue());
                pstmtAccount.setInt(3, a.getRoleType().getValue());
                pstmtAccount.setInt(4, a.getId());
                pstmtAccount.executeUpdate();

                pstmtPerson.setString(1, a.getPerson().getName());
                pstmtPerson.setString(2, a.getPerson().getEmail());
                pstmtPerson.setString(3, a.getPerson().getPhone());
                pstmtPerson.setInt(4, a.getPersonId());
                pstmtPerson.executeUpdate();

                conn.commit();
            } catch (SQLException e) { conn.rollback(); throw e; }
        } catch (SQLException e) { e.printStackTrace(); }
    }

    public void deactivate(int accountId) {
        String sql = "UPDATE accounts SET is_active = FALSE WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, accountId);
            pstmt.executeUpdate();
        } catch (SQLException e) { e.printStackTrace(); }
    }

    public void reactivate(int accountId) {
        String sql = "UPDATE accounts SET is_active = TRUE WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, accountId);
            pstmt.executeUpdate();
        } catch (SQLException e) { e.printStackTrace(); }
    }

    private Account mapResultSetToAccount(ResultSet rs) throws SQLException {
        Account account = new Account();
        account.setId(rs.getInt("id"));
        account.setPersonId(rs.getInt("person_id"));
        account.setUsername(rs.getString("username"));
        account.setPasswordHash(rs.getString("password_hash"));
        account.setStatus(AccountStatus.fromInt(rs.getInt("status")));
        account.setRoleType(RoleType.fromInt(rs.getInt("role_type")));
        account.setActive(rs.getBoolean("is_active"));
        
        Person person = new Person();
        person.setId(rs.getInt("person_id"));
        person.setName(rs.getString("name"));
        person.setEmail(rs.getString("email"));
        person.setPhone(rs.getString("phone"));
        account.setPerson(person);
        return account;
    }
}
