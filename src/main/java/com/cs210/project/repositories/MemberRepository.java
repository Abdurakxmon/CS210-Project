package com.cs210.project.repositories;

import com.cs210.project.config.DatabaseConnection;
import com.cs210.project.constants.Enums.AccountStatus;
import com.cs210.project.constants.Enums.RoleType;
import com.cs210.project.models.Account;
import com.cs210.project.models.Member;
import com.cs210.project.models.Person;
import org.mindrot.jbcrypt.BCrypt;

import java.time.LocalDate;
import java.time.Period;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MemberRepository {
    private static final int MINIMUM_AGE = 18;
    private static final String PASSWORD_RULE = "^(?=.*[A-Z])(?=.*\\d).{8,}$";

    public Account login(String username, String password) {
        String sql = "SELECT a.*, p.name, p.email, p.phone FROM accounts a " +
                     "JOIN persons p ON a.person_id = p.id " +
                     "WHERE a.username = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
             pstmt.setString(1, username);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    String storedPassword = rs.getString("password_hash");
                    if (!passwordMatches(password, storedPassword)) return null;

                    // Smooth migration path for legacy plaintext passwords stored in older datasets.
                    if (!isBcryptHash(storedPassword)) {
                        upgradePasswordHash(conn, rs.getInt("id"), password);
                    }

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

    private boolean passwordMatches(String rawPassword, String storedPassword) {
        if (rawPassword == null || storedPassword == null || storedPassword.isBlank()) return false;

        if (isBcryptHash(storedPassword)) {
            try {
                String normalizedHash = normalizeBcryptVersion(storedPassword);
                return BCrypt.checkpw(rawPassword, normalizedHash);
            } catch (IllegalArgumentException e) {
                // Corrupt/unsupported hash format should fail login instead of crashing UI thread.
                return false;
            }
        }

        // Legacy fallback for old plaintext records.
        return rawPassword.equals(storedPassword);
    }

    private boolean isBcryptHash(String hash) {
        return hash.startsWith("$2a$") || hash.startsWith("$2b$") || hash.startsWith("$2y$");
    }

    private String normalizeBcryptVersion(String hash) {
        if (hash.startsWith("$2b$") || hash.startsWith("$2y$")) {
            return "$2a$" + hash.substring(4);
        }
        return hash;
    }

    private void upgradePasswordHash(Connection conn, int accountId, String rawPassword) {
        String updateSql = "UPDATE accounts SET password_hash = ? WHERE id = ?";
        try (PreparedStatement updateStmt = conn.prepareStatement(updateSql)) {
            updateStmt.setString(1, BCrypt.hashpw(rawPassword, BCrypt.gensalt()));
            updateStmt.setInt(2, accountId);
            updateStmt.executeUpdate();
        } catch (SQLException e) {
            // Login already succeeded; do not block user for best-effort migration.
            System.err.println("Password hash upgrade failed for account_id=" + accountId + ": " + e.getMessage());
        }
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
            if (password == null || !password.matches(PASSWORD_RULE)) {
                throw new SQLException("Password must be at least 8 characters, include 1 uppercase letter and 1 number.");
            }
            if (p == null || p.getBirthDate() == null || Period.between(p.getBirthDate(), LocalDate.now()).getYears() < MINIMUM_AGE) {
                throw new SQLException("Customer must be at least " + MINIMUM_AGE + " years old.");
            }
            if (expiry == null || !expiry.toLocalDate().isAfter(LocalDate.now())) {
                throw new SQLException("Driver license expiry must be a future date.");
            }

            conn = DatabaseConnection.getConnection();
            conn.setAutoCommit(false);

            // 0. Check duplicates
            String checkSql = "SELECT (SELECT COUNT(*) FROM accounts WHERE username = ?) as user_exists, " +
                              "(SELECT COUNT(*) FROM members WHERE driver_license_number = ?) as license_exists";
            try (PreparedStatement checkStmt = conn.prepareStatement(checkSql)) {
                checkStmt.setString(1, username);
                checkStmt.setString(2, license);
                try (ResultSet rs = checkStmt.executeQuery()) {
                    if (rs.next()) {
                        if (rs.getInt("user_exists") > 0) throw new SQLException("Username already exists.");
                        if (rs.getInt("license_exists") > 0) throw new SQLException("Driver license already registered.");
                    }
                }
            }

            // 1. Insert Person (support old schemas that do not yet have birth_date)
            boolean hasBirthDate = hasColumn(conn, "persons", "birth_date");
            String sqlPerson = hasBirthDate
                    ? "INSERT INTO persons (name, email, phone, street_address, city, state, zipcode, country, birth_date) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)"
                    : "INSERT INTO persons (name, email, phone, street_address, city, state, zipcode, country) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
            PreparedStatement pstmtPerson = conn.prepareStatement(sqlPerson, Statement.RETURN_GENERATED_KEYS);
            pstmtPerson.setString(1, p.getName());
            pstmtPerson.setString(2, p.getEmail());
            pstmtPerson.setString(3, p.getPhone());
            pstmtPerson.setString(4, p.getStreetAddress());
            pstmtPerson.setString(5, p.getCity());
            pstmtPerson.setString(6, p.getState());
            pstmtPerson.setString(7, p.getZipcode());
            pstmtPerson.setString(8, p.getCountry());
            if (hasBirthDate) {
                if (p.getBirthDate() != null) pstmtPerson.setDate(9, Date.valueOf(p.getBirthDate())); else pstmtPerson.setNull(9, Types.DATE);
            }
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
            
            // Hash password with BCrypt
            String hashed = BCrypt.hashpw(password, BCrypt.gensalt());
            pstmtAccount.setString(3, hashed);
            
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
            System.err.println("Registration DB Error: " + e.getMessage());
            if (conn != null) try { conn.rollback(); } catch (SQLException ex) { ex.printStackTrace(); }
            return false;
        } finally {
            if (conn != null) try { conn.close(); } catch (SQLException e) { e.printStackTrace(); }
        }
    }

    private boolean hasColumn(Connection conn, String tableName, String columnName) throws SQLException {
        DatabaseMetaData meta = conn.getMetaData();
        String catalog = conn.getCatalog();
        try (ResultSet rs = meta.getColumns(catalog, null, tableName, columnName)) {
            return rs.next();
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
                list.add(m);
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }
}
