package com.yourteam.library.repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDateTime;

import com.yourteam.library.config.DBConnection;

public class ReservationRepository {
    public boolean createReservation(int userId, int bookId, int priority) {
        String sql = "INSERT INTO reservations (user_id, book_id, queue_priority) VALUES (?, ?, ?)";
        try (Connection conn = DBConnection.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            pstmt.setInt(2, bookId);
            pstmt.setInt(3, priority);
            return pstmt.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
    public boolean notifyNextReservation(int bookId) {
        String selectSql = "SELECT reservation_id FROM reservations WHERE book_id = ? AND status = 'WAITING' ORDER BY queue_priority DESC, created_at ASC LIMIT 1";
        String updateSql = "UPDATE reservations SET status = 'NOTIFIED', notified_at = ?, expires_at = ? WHERE reservation_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement select = conn.prepareStatement(selectSql)) {
            select.setInt(1, bookId);
            ResultSet rs = select.executeQuery();
            if (!rs.next()) return false;
            int reservationId = rs.getInt("reservation_id");

            try (PreparedStatement update = conn.prepareStatement(updateSql)) {
                LocalDateTime now = LocalDateTime.now();
                update.setTimestamp(1, java.sql.Timestamp.valueOf(now));
                update.setTimestamp(2, java.sql.Timestamp.valueOf(now.plusDays(2)));
                update.setInt(3, reservationId);
                return update.executeUpdate() > 0;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

}
