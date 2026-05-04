package com.cs210.project.repositories;

import com.cs210.project.config.DatabaseConnection;
import com.cs210.project.constants.Enums.AccountStatus;
import com.cs210.project.constants.Enums.RoleType;
import com.cs210.project.models.Account;
import com.cs210.project.models.Person;
import org.mindrot.jbcrypt.BCrypt;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AccountRepository {
    private static final String PASSWORD_RULE = "^(?=.*[A-Z])(?=.*\\d).{8,}$";

    public List<Account> findAll() {
        List<Account> accounts = new ArrayList<>();
        String sql = "SELECT a.*, p.name, p.email, p.phone, p.street_address, p.city, p.state, " +
                     "p.zipcode, p.country, p.birth_date FROM accounts a " +
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

    public boolean create(Person p, String username, String password, RoleType role, AccountStatus status) {
        Connection conn = null;
        try {
            if (password == null || !password.matches(PASSWORD_RULE)) {
                throw new SQLException("Password must be at least 8 characters, include 1 uppercase letter and 1 number.");
            }
            conn = DatabaseConnection.getConnection();
            conn.setAutoCommit(false);

            String sqlPerson = "INSERT INTO persons (name, email, phone, street_address, city, state, zipcode, country, birth_date) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
            PreparedStatement pstmtPerson = conn.prepareStatement(sqlPerson, Statement.RETURN_GENERATED_KEYS);
            pstmtPerson.setString(1, p.getName());
            pstmtPerson.setString(2, p.getEmail());
            pstmtPerson.setString(3, p.getPhone());
            pstmtPerson.setString(4, p.getStreetAddress());
            pstmtPerson.setString(5, p.getCity());
            pstmtPerson.setString(6, p.getState());
            pstmtPerson.setString(7, p.getZipcode());
            pstmtPerson.setString(8, p.getCountry());
            pstmtPerson.setDate(9, Date.valueOf(p.getBirthDate()));
            pstmtPerson.executeUpdate();
            
            int personId;
            try (ResultSet rs = pstmtPerson.getGeneratedKeys()) {
                if (rs.next()) personId = rs.getInt(1); else throw new SQLException("Person ID failed");
            }

            String sqlAccount = "INSERT INTO accounts (person_id, username, password_hash, status, role_type) VALUES (?, ?, ?, ?, ?)";
            PreparedStatement pstmtAccount = conn.prepareStatement(sqlAccount);
            pstmtAccount.setInt(1, personId);
            pstmtAccount.setString(2, username);
            pstmtAccount.setString(3, BCrypt.hashpw(password, BCrypt.gensalt()));
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
        try (Connection conn = DatabaseConnection.getConnection()) {
            conn.setAutoCommit(false);
            String sqlPerson = "UPDATE persons SET name = ?, email = ?, phone = ?, street_address = ?, city = ?, state = ?, zipcode = ?, country = ?, birth_date = ? WHERE id = ?";
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
                pstmtPerson.setString(4, a.getPerson().getStreetAddress());
                pstmtPerson.setString(5, a.getPerson().getCity());
                pstmtPerson.setString(6, a.getPerson().getState());
                pstmtPerson.setString(7, a.getPerson().getZipcode());
                pstmtPerson.setString(8, a.getPerson().getCountry());
                pstmtPerson.setDate(9, Date.valueOf(a.getPerson().getBirthDate()));
                pstmtPerson.setInt(10, a.getPersonId());
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
        person.setStreetAddress(rs.getString("street_address"));
        person.setCity(rs.getString("city"));
        person.setState(rs.getString("state"));
        person.setZipcode(rs.getString("zipcode"));
        person.setCountry(rs.getString("country"));
        Date birthDate = rs.getDate("birth_date");
        person.setBirthDate(birthDate == null ? null : birthDate.toLocalDate());
        account.setPerson(person);
        return account;
    }

    private boolean hasColumn(Connection conn, String tableName, String columnName) throws SQLException {
        DatabaseMetaData meta = conn.getMetaData();
        String catalog = conn.getCatalog();
        try (ResultSet rs = meta.getColumns(catalog, null, tableName, columnName)) {
            return rs.next();
        }
    }
}
