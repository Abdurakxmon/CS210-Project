package com.cs210.project.repositories;

import com.cs210.project.config.DatabaseConnection;
import com.cs210.project.constants.Enums.BillItemType;
import com.cs210.project.models.Bill;
import com.cs210.project.models.BillItem;

import java.sql.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class BillRepository {

    public Bill findByReservationId(int resId) {
        String sql = "SELECT * FROM bills WHERE reservation_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, resId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    Bill bill = new Bill();
                    bill.setId(rs.getInt("id"));
                    bill.setReservationId(rs.getInt("reservation_id"));
                    bill.setTotalAmount(rs.getBigDecimal("total_amount"));
                    bill.setItems(findItemsByBillId(bill.getId()));
                    return bill;
                }
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return null;
    }

    public void create(Bill bill) {
        String sql = "INSERT INTO bills (reservation_id, total_amount) VALUES (?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setInt(1, bill.getReservationId());
            pstmt.setBigDecimal(2, bill.getTotalAmount() != null ? bill.getTotalAmount() : BigDecimal.ZERO);
            pstmt.executeUpdate();
            try (ResultSet rs = pstmt.getGeneratedKeys()) {
                if (rs.next()) bill.setId(rs.getInt(1));
            }
        } catch (SQLException e) { e.printStackTrace(); }
    }

    public void updateTotal(int billId) {
        String sql = "UPDATE bills b SET total_amount = (SELECT SUM(amount) FROM bill_items WHERE bill_id = ?) WHERE b.id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, billId);
            pstmt.setInt(2, billId);
            pstmt.executeUpdate();
        } catch (SQLException e) { e.printStackTrace(); }
    }

    public void createBill(int resId, BigDecimal baseAmount) {
        String sqlBill = "INSERT INTO bills (reservation_id, total_amount) VALUES (?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sqlBill, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setInt(1, resId);
            pstmt.setBigDecimal(2, baseAmount);
            pstmt.executeUpdate();
            
            int billId;
            try (ResultSet rs = pstmt.getGeneratedKeys()) {
                if (rs.next()) billId = rs.getInt(1); else return;
            }
            
            addItem(billId, BillItemType.BASE_CHARGE, baseAmount, "Base Rental Charge");
        } catch (SQLException e) { e.printStackTrace(); }
    }

    public void addItem(int billId, BillItemType type, BigDecimal amount, String serviceName) {
        String sqlItem = "INSERT INTO bill_items (bill_id, item_type, amount, service_name) VALUES (?, ?, ?, ?)";
        String sqlUpdateTotal = "UPDATE bills SET total_amount = total_amount + ? WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection()) {
            conn.setAutoCommit(false);
            try (PreparedStatement pstmtItem = conn.prepareStatement(sqlItem);
                 PreparedStatement pstmtUpdate = conn.prepareStatement(sqlUpdateTotal)) {
                
                pstmtItem.setInt(1, billId);
                pstmtItem.setInt(2, type.getValue());
                pstmtItem.setBigDecimal(3, amount);
                pstmtItem.setString(4, serviceName);
                pstmtItem.executeUpdate();
                
                pstmtUpdate.setBigDecimal(1, amount);
                pstmtUpdate.setInt(2, billId);
                pstmtUpdate.executeUpdate();
                
                conn.commit();
            } catch (SQLException e) { conn.rollback(); throw e; }
        } catch (SQLException e) { e.printStackTrace(); }
    }

    private List<BillItem> findItemsByBillId(int billId) {
        List<BillItem> items = new ArrayList<>();
        String sql = "SELECT * FROM bill_items WHERE bill_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, billId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    BillItem item = new BillItem();
                    item.setId(rs.getInt("id"));
                    item.setBillId(rs.getInt("bill_id"));
                    item.setItemType(BillItemType.fromInt(rs.getInt("item_type")));
                    item.setAmount(rs.getBigDecimal("amount"));
                    item.setServiceName(rs.getString("service_name"));
                    items.add(item);
                }
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return items;
    }
}
