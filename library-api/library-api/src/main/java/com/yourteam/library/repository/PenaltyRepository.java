package com.yourteam.library.repository;

import java.sql.Connection;
import java.sql.PreparedStatement;

import com.yourteam.library.config.DBConnection;

public class PenaltyRepository {
    public boolean createFine(int userId, int recordId, double amount, String reason) {
        String sql = "INSERT INTO user_penalties (user_id, borrow_record_id, penalty_type, amount, reason) VALUES (?, ?, 'FINE', ?, ?)";
        try (Connection conn = DBConnection.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            pstmt.setInt(2, recordId);
            pstmt.setDouble(3, amount);
            pstmt.setString(4, reason);
            return pstmt.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}
