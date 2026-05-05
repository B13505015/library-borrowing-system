package com.yourteam.library.repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.Map;

import com.yourteam.library.config.DBConnection;

public class LoanPolicyRepository {
    public Map<String, Object> getPolicy(String roleLevel) {
        String sql = "SELECT role_level, max_active_loans, overdue_fine_per_day, reservation_priority, fine_grace_days FROM loan_policies WHERE role_level = ?";
        try (Connection conn = DBConnection.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, roleLevel.toUpperCase());
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                Map<String, Object> m = new HashMap<>();
                m.put("roleLevel", rs.getString("role_level"));
                m.put("maxActiveLoans", rs.getInt("max_active_loans"));
                m.put("overdueFinePerDay", rs.getDouble("overdue_fine_per_day"));
                m.put("reservationPriority", rs.getInt("reservation_priority"));
                m.put("fineGraceDays", rs.getInt("fine_grace_days"));
                return m;
            }
        } catch (Exception e) { e.printStackTrace(); }
        return null;
    }

    public boolean updateFinePolicy(String roleLevel, double finePerDay, int graceDays) {
        String sql = "UPDATE loan_policies SET overdue_fine_per_day = ?, fine_grace_days = ? WHERE role_level = ?";
        try (Connection conn = DBConnection.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setDouble(1, finePerDay);
            pstmt.setInt(2, graceDays);
            pstmt.setString(3, roleLevel.toUpperCase());
            return pstmt.executeUpdate() > 0;
        } catch (Exception e) { e.printStackTrace(); }
        return false;
    }
}
