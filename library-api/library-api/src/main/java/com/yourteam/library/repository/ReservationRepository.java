package com.yourteam.library.repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDateTime;

import com.yourteam.library.config.DBConnection;

public class ReservationRepository {
    public boolean createReservation(int userId, int bookId, int priority) {
        String activeBorrowSql = "SELECT 1 FROM borrow_records WHERE user_id = ? AND book_id = ? AND return_date IS NULL LIMIT 1";
        String existsSql = "SELECT 1 FROM reservations r JOIN books b ON b.book_id = r.book_id "
                + "WHERE b.title = (SELECT title FROM books WHERE book_id = ?) "
                + "AND r.user_id = ? AND r.status IN ('WAITING','NOTIFIED') LIMIT 1";
        String insertSql = "INSERT INTO reservations (user_id, book_id, reservation_title, queue_priority) "
                + "SELECT ?, b.book_id, b.title, ? "
                + "FROM books b "
                + "WHERE b.book_id = ?";

        try (Connection conn = DBConnection.getConnection()) {
            try (PreparedStatement activeBorrowStmt = conn.prepareStatement(activeBorrowSql)) {
                activeBorrowStmt.setInt(1, userId);
                activeBorrowStmt.setInt(2, bookId);
                ResultSet borrowRs = activeBorrowStmt.executeQuery();
                if (borrowRs.next()) {
                    return false;
                }
            }

            try (PreparedStatement existsStmt = conn.prepareStatement(existsSql)) {
                existsStmt.setInt(1, bookId);
                existsStmt.setInt(2, userId);
                ResultSet existsRs = existsStmt.executeQuery();
                if (existsRs.next()) {
                    return false;
                }
            }

            try (PreparedStatement insertStmt = conn.prepareStatement(insertSql)) {
                insertStmt.setInt(1, userId);
                insertStmt.setInt(2, priority);
                insertStmt.setInt(3, bookId);
                return insertStmt.executeUpdate() > 0;
            }
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


    public int countWaitingReservations(int bookId) {
        String sql = "SELECT COUNT(DISTINCT r.user_id) AS c FROM reservations r JOIN books b ON b.book_id = r.book_id WHERE b.title = (SELECT title FROM books WHERE book_id = ?) AND r.status = 'WAITING'";
        try (Connection conn = DBConnection.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, bookId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) return rs.getInt("c");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    public Integer findUserQueuePosition(int userId, int bookId) {
        String sql = "SELECT r.user_id FROM reservations r JOIN books b ON b.book_id = r.book_id WHERE b.title = (SELECT title FROM books WHERE book_id = ?) AND r.status = 'WAITING' GROUP BY r.user_id ORDER BY MAX(r.queue_priority) DESC, MIN(r.created_at) ASC";
        try (Connection conn = DBConnection.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, bookId);
            ResultSet rs = pstmt.executeQuery();
            int position = 0;
            while (rs.next()) {
                position++;
                if (rs.getInt("user_id") == userId) {
                    return position;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public java.util.List<String> findNotifiedReservationMessages(int userId) {
        java.util.List<String> msgs = new java.util.ArrayList<>();
        String sql = "SELECT DISTINCT b.title, r.expires_at FROM reservations r JOIN books b ON b.book_id = r.book_id "
                + "WHERE r.user_id = ? AND r.status = 'NOTIFIED' AND (r.expires_at IS NULL OR r.expires_at >= NOW()) ORDER BY r.notified_at DESC";
        try (Connection conn = DBConnection.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                String title = rs.getString("title");
                java.sql.Timestamp expires = rs.getTimestamp("expires_at");
                msgs.add("《" + title + "》可借用了" + (expires == null ? "" : "，請於 " + expires.toLocalDateTime().toLocalDate() + " 前借閱"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return msgs;
    }

    public boolean hasActiveReservationByTitle(int userId, int bookId) {
        String sql = "SELECT 1 FROM reservations r JOIN books b ON b.book_id = r.book_id "
                + "WHERE b.title = (SELECT title FROM books WHERE book_id = ?) "
                + "AND r.user_id = ? AND r.status IN ('WAITING','NOTIFIED') LIMIT 1";
        try (Connection conn = DBConnection.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, bookId);
            pstmt.setInt(2, userId);
            ResultSet rs = pstmt.executeQuery();
            return rs.next();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }


    public java.util.List<library_api.dto.MyReservationResponse> findMyActiveReservations(int userId) {
        java.util.List<library_api.dto.MyReservationResponse> list = new java.util.ArrayList<>();
        String sql = "SELECT r.reservation_id, r.book_id, COALESCE(r.reservation_title, b.title) AS title, r.status, r.queue_priority, r.created_at, r.notified_at, r.expires_at "
                + "FROM reservations r JOIN books b ON b.book_id = r.book_id "
                + "WHERE r.user_id = ? AND r.status IN ('WAITING','NOTIFIED') ORDER BY r.created_at DESC";
        try (Connection conn = DBConnection.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                int bookId = rs.getInt("book_id");
                Integer qPos = "WAITING".equalsIgnoreCase(rs.getString("status")) ? findUserQueuePosition(userId, bookId) : null;
                list.add(new library_api.dto.MyReservationResponse(
                        rs.getInt("reservation_id"),
                        bookId,
                        rs.getString("title"),
                        rs.getString("status"),
                        qPos,
                        rs.getInt("queue_priority"),
                        rs.getTimestamp("created_at") == null ? null : rs.getTimestamp("created_at").toLocalDateTime().toString(),
                        rs.getTimestamp("notified_at") == null ? null : rs.getTimestamp("notified_at").toLocalDateTime().toString(),
                        rs.getTimestamp("expires_at") == null ? null : rs.getTimestamp("expires_at").toLocalDateTime().toString()
                ));
            }
        } catch (Exception e) { e.printStackTrace(); }
        return list;
    }
}
