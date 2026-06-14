package com.yourteam.library.repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
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
        repairInvalidNotifiedReservations(bookId);
        String availabilitySql = "SELECT 1 FROM books b WHERE b.book_id = ? AND b.status = 'AVAILABLE' "
                + "AND NOT EXISTS (SELECT 1 FROM borrow_records br WHERE br.book_id = b.book_id AND br.return_date IS NULL) "
                + "AND NOT EXISTS (SELECT 1 FROM reservations r WHERE r.book_id = b.book_id AND r.status = 'NOTIFIED')";
        String selectSql = "SELECT reservation_id FROM reservations WHERE book_id = ? AND status = 'WAITING' ORDER BY queue_priority DESC, created_at ASC LIMIT 1";
        String updateSql = "UPDATE reservations SET status = 'NOTIFIED', notified_at = ?, expires_at = ? WHERE reservation_id = ?";
        try (Connection conn = DBConnection.getConnection()) {
            try (PreparedStatement availability = conn.prepareStatement(availabilitySql)) {
                availability.setInt(1, bookId);
                if (!availability.executeQuery().next()) return false;
            }

            try (PreparedStatement select = conn.prepareStatement(selectSql)) {
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
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public void repairInvalidNotifiedReservations(int bookId) {
        String sql = "UPDATE reservations r JOIN books b ON b.book_id = r.book_id "
                + "SET r.status = 'WAITING', r.notified_at = NULL, r.expires_at = NULL "
                + "WHERE r.book_id = ? AND r.status = 'NOTIFIED' "
                + "AND (b.status <> 'AVAILABLE' OR EXISTS ("
                + "SELECT 1 FROM borrow_records br WHERE br.book_id = r.book_id AND br.return_date IS NULL))";
        try (Connection conn = DBConnection.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, bookId);
            pstmt.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
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

    public String findActiveReservationStatus(int userId, int bookId) {
        expireNotificationsAndNotifyNext();
        String sql = "SELECT r.status FROM reservations r "
                + "WHERE r.book_id = ? AND r.user_id = ? AND r.status IN ('WAITING','NOTIFIED') "
                + "ORDER BY CASE r.status WHEN 'NOTIFIED' THEN 0 ELSE 1 END, r.created_at ASC LIMIT 1";
        try (Connection conn = DBConnection.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, bookId);
            pstmt.setInt(2, userId);
            ResultSet rs = pstmt.executeQuery();
            return rs.next() ? rs.getString("status") : null;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public Integer findActiveReservationId(int userId, int bookId) {
        String sql = "SELECT reservation_id FROM reservations WHERE book_id = ? AND user_id = ? "
                + "AND status IN ('WAITING','NOTIFIED') "
                + "ORDER BY CASE status WHEN 'NOTIFIED' THEN 0 ELSE 1 END, created_at ASC LIMIT 1";
        try (Connection conn = DBConnection.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, bookId);
            pstmt.setInt(2, userId);
            ResultSet rs = pstmt.executeQuery();
            return rs.next() ? rs.getInt("reservation_id") : null;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean cancelReservation(int reservationId, int userId) {
        String sql = "UPDATE reservations SET status = 'CANCELLED' "
                + "WHERE reservation_id = ? AND user_id = ? AND status = 'WAITING'";
        try (Connection conn = DBConnection.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, reservationId);
            pstmt.setInt(2, userId);
            return pstmt.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public String fulfillNotifiedReservation(int reservationId, int userId, int borrowDays) {
        String selectSql = "SELECT r.book_id, r.status, r.expires_at, b.status AS book_status "
                + "FROM reservations r JOIN books b ON b.book_id = r.book_id "
                + "WHERE r.reservation_id = ? AND r.user_id = ? FOR UPDATE";
        String insertSql = "INSERT INTO borrow_records "
                + "(user_id, book_id, borrow_date, due_date, return_date, borrow_days, created_at) "
                + "VALUES (?, ?, ?, ?, NULL, ?, ?)";
        String updateBookSql = "UPDATE books SET status = 'BORROWED' WHERE book_id = ? AND status = 'AVAILABLE'";
        String updateReservationSql = "UPDATE reservations SET status = 'FULFILLED' "
                + "WHERE reservation_id = ? AND user_id = ? AND status = 'NOTIFIED'";

        try (Connection conn = DBConnection.getConnection()) {
            conn.setAutoCommit(false);
            try {
                int bookId;
                try (PreparedStatement select = conn.prepareStatement(selectSql)) {
                    select.setInt(1, reservationId);
                    select.setInt(2, userId);
                    ResultSet rs = select.executeQuery();
                    if (!rs.next()) {
                        conn.rollback();
                        return "RESERVATION_STATUS_CHANGED";
                    }
                    bookId = rs.getInt("book_id");
                    if (!"NOTIFIED".equalsIgnoreCase(rs.getString("status"))) {
                        conn.rollback();
                        return "RESERVATION_STATUS_CHANGED";
                    }
                    Timestamp expiresAt = rs.getTimestamp("expires_at");
                    if (expiresAt != null && expiresAt.toLocalDateTime().isBefore(LocalDateTime.now())) {
                        try (PreparedStatement expire = conn.prepareStatement(
                                "UPDATE reservations SET status = 'EXPIRED' WHERE reservation_id = ? AND status = 'NOTIFIED'")) {
                            expire.setInt(1, reservationId);
                            expire.executeUpdate();
                        }
                        conn.commit();
                        notifyNextReservation(bookId);
                        return "RESERVATION_EXPIRED";
                    }
                    if (!"AVAILABLE".equalsIgnoreCase(rs.getString("book_status"))) {
                        conn.rollback();
                        return "BOOK_NOT_AVAILABLE";
                    }
                }

                LocalDateTime now = LocalDateTime.now();
                try (PreparedStatement insert = conn.prepareStatement(insertSql)) {
                    insert.setInt(1, userId);
                    insert.setInt(2, bookId);
                    insert.setTimestamp(3, Timestamp.valueOf(now));
                    insert.setTimestamp(4, Timestamp.valueOf(now.plusDays(borrowDays)));
                    insert.setInt(5, borrowDays);
                    insert.setTimestamp(6, Timestamp.valueOf(now));
                    if (insert.executeUpdate() != 1) throw new IllegalStateException("借閱紀錄新增失敗");
                }

                try (PreparedStatement updateBook = conn.prepareStatement(updateBookSql)) {
                    updateBook.setInt(1, bookId);
                    if (updateBook.executeUpdate() != 1) throw new IllegalStateException("書籍狀態更新失敗");
                }

                try (PreparedStatement updateReservation = conn.prepareStatement(updateReservationSql)) {
                    updateReservation.setInt(1, reservationId);
                    updateReservation.setInt(2, userId);
                    if (updateReservation.executeUpdate() != 1) throw new IllegalStateException("預約狀態更新失敗");
                }

                conn.commit();
                return "BORROW_SUCCESS";
            } catch (Exception e) {
                conn.rollback();
                e.printStackTrace();
            } finally {
                conn.setAutoCommit(true);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "RESERVATION_STATUS_CHANGED";
    }

    public void expireNotificationsAndNotifyNext() {
        java.util.List<Integer> notifiedBookIds = new java.util.ArrayList<>();
        java.util.List<Integer> bookIds = new java.util.ArrayList<>();
        String notifiedBooksSql = "SELECT DISTINCT book_id FROM reservations WHERE status = 'NOTIFIED'";
        String selectSql = "SELECT DISTINCT book_id FROM reservations "
                + "WHERE status = 'NOTIFIED' AND expires_at IS NOT NULL AND expires_at < NOW()";
        String updateSql = "UPDATE reservations SET status = 'EXPIRED' "
                + "WHERE status = 'NOTIFIED' AND expires_at IS NOT NULL AND expires_at < NOW()";
        try (Connection conn = DBConnection.getConnection()) {
            try (PreparedStatement notifiedBooks = conn.prepareStatement(notifiedBooksSql);
                 ResultSet rs = notifiedBooks.executeQuery()) {
                while (rs.next()) notifiedBookIds.add(rs.getInt("book_id"));
            }
            for (int bookId : notifiedBookIds) repairInvalidNotifiedReservations(bookId);

            try (PreparedStatement select = conn.prepareStatement(selectSql);
                 ResultSet rs = select.executeQuery()) {
                while (rs.next()) bookIds.add(rs.getInt("book_id"));
            }
            try (PreparedStatement update = conn.prepareStatement(updateSql)) {
                update.executeUpdate();
            }
            for (int bookId : bookIds) notifyNextReservation(bookId);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public java.util.List<library_api.dto.MyReservationResponse> findMyActiveReservations(int userId) {
        java.util.List<Integer> activeBookIds = new java.util.ArrayList<>();
        String activeBooksSql = "SELECT DISTINCT book_id FROM reservations "
                + "WHERE user_id = ? AND status IN ('WAITING','NOTIFIED')";
        try (Connection conn = DBConnection.getConnection(); PreparedStatement pstmt = conn.prepareStatement(activeBooksSql)) {
            pstmt.setInt(1, userId);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) activeBookIds.add(rs.getInt("book_id"));
        } catch (Exception e) {
            e.printStackTrace();
        }
        for (int bookId : activeBookIds) repairInvalidNotifiedReservations(bookId);
        expireNotificationsAndNotifyNext();
        java.util.List<library_api.dto.MyReservationResponse> list = new java.util.ArrayList<>();
        String sql = "SELECT r.reservation_id, r.book_id, COALESCE(r.reservation_title, b.title) AS title, r.status, r.queue_priority, r.created_at, r.notified_at, r.expires_at, "
                + "CASE WHEN r.status = 'NOTIFIED' AND b.status = 'AVAILABLE' AND NOT EXISTS ("
                + "SELECT 1 FROM borrow_records br WHERE br.book_id = r.book_id AND br.return_date IS NULL"
                + ") THEN TRUE ELSE FALSE END AS can_borrow_notified "
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
                        rs.getTimestamp("expires_at") == null ? null : rs.getTimestamp("expires_at").toLocalDateTime().toString(),
                        rs.getBoolean("can_borrow_notified")
                ));
            }
        } catch (Exception e) { e.printStackTrace(); }
        return list;
    }
}
