package com.yourteam.library.repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.yourteam.library.config.DBConnection;
import com.yourteam.library.service.PenaltyService;

import library_api.dto.AdminPenaltyResponse;
import library_api.dto.PenaltyResponse;

public class PenaltyRepository {
    public List<PenaltyResponse> findUserPenaltySummaries(int userId) {
        List<PenaltyResponse> penalties = new ArrayList<>();
        String sql = "SELECT br.record_id, br.book_id, b.title AS book_title, "
                + "br.borrow_date, br.due_date, br.return_date, "
                + "p.penalty_id, p.amount, p.status "
                + "FROM borrow_records br "
                + "JOIN books b ON b.book_id = br.book_id "
                + "LEFT JOIN user_penalties p ON p.borrow_record_id = br.record_id "
                + "AND p.penalty_type = 'FINE' "
                + "AND p.penalty_id = (SELECT MAX(p2.penalty_id) FROM user_penalties p2 "
                + "WHERE p2.borrow_record_id = br.record_id AND p2.penalty_type = 'FINE') "
                + "WHERE br.user_id = ? "
                + "AND (p.penalty_id IS NOT NULL OR br.due_date < COALESCE(br.return_date, CURRENT_TIMESTAMP)) "
                + "ORDER BY br.borrow_date DESC";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                LocalDateTime dueDate = toDateTime(rs.getTimestamp("due_date"));
                LocalDateTime returnDate = toDateTime(rs.getTimestamp("return_date"));
                boolean settled = returnDate != null;
                long overdueDays = PenaltyService.calculateOverdueDays(
                        dueDate,
                        settled ? returnDate : LocalDateTime.now()
                );
                Integer penaltyId = (Integer) rs.getObject("penalty_id");
                String status = rs.getString("status");
                double amount = settled && penaltyId != null
                        ? rs.getDouble("amount")
                        : PenaltyService.calculateFine(overdueDays);
                penalties.add(new PenaltyResponse(
                        penaltyId,
                        rs.getInt("record_id"),
                        rs.getInt("book_id"),
                        rs.getString("book_title"),
                        toString(rs.getTimestamp("borrow_date")),
                        toString(rs.getTimestamp("due_date")),
                        toString(rs.getTimestamp("return_date")),
                        overdueDays,
                        amount,
                        status,
                        settled,
                        settled && penaltyId != null && "OPEN".equals(status)
                ));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return penalties;
    }

    public List<AdminPenaltyResponse> findAllPenalties(String keyword, String status) {
        List<AdminPenaltyResponse> penalties = new ArrayList<>();
        String normalizedKeyword = keyword == null ? "" : keyword.trim().toLowerCase();
        String normalizedStatus = normalizeDatabaseStatus(status);
        String likeKeyword = "%" + normalizedKeyword + "%";
        String sql = "SELECT p.penalty_id, p.borrow_record_id, p.user_id, "
                + "u.student_no, u.name AS user_name, br.book_id, b.title AS book_title, "
                + "br.borrow_date, br.due_date, br.return_date, p.amount, p.status "
                + "FROM user_penalties p "
                + "JOIN users u ON u.user_id = p.user_id "
                + "JOIN borrow_records br ON br.record_id = p.borrow_record_id "
                + "JOIN books b ON b.book_id = br.book_id "
                + "WHERE p.penalty_type = 'FINE' "
                + "AND (? = '' OR LOWER(u.student_no) LIKE ? OR LOWER(u.name) LIKE ? OR LOWER(b.title) LIKE ?) "
                + "AND (? = '' OR p.status = ?) "
                + "ORDER BY p.created_at DESC";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, normalizedKeyword);
            pstmt.setString(2, likeKeyword);
            pstmt.setString(3, likeKeyword);
            pstmt.setString(4, likeKeyword);
            pstmt.setString(5, normalizedStatus);
            pstmt.setString(6, normalizedStatus);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                LocalDateTime dueDate = toDateTime(rs.getTimestamp("due_date"));
                LocalDateTime returnDate = toDateTime(rs.getTimestamp("return_date"));
                long overdueDays = PenaltyService.calculateOverdueDays(
                        dueDate,
                        returnDate == null ? LocalDateTime.now() : returnDate
                );
                penalties.add(new AdminPenaltyResponse(
                        rs.getInt("penalty_id"),
                        rs.getInt("borrow_record_id"),
                        rs.getInt("user_id"),
                        rs.getString("student_no"),
                        rs.getString("user_name"),
                        rs.getInt("book_id"),
                        rs.getString("book_title"),
                        toString(rs.getTimestamp("borrow_date")),
                        toString(rs.getTimestamp("due_date")),
                        toString(rs.getTimestamp("return_date")),
                        overdueDays,
                        rs.getDouble("amount"),
                        rs.getString("status")
                ));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return penalties;
    }

    public AdminPenaltyResponse findPenaltyById(int penaltyId) {
        String sql = "SELECT p.penalty_id, p.borrow_record_id, p.user_id, "
                + "u.student_no, u.name AS user_name, br.book_id, b.title AS book_title, "
                + "br.borrow_date, br.due_date, br.return_date, p.amount, p.status "
                + "FROM user_penalties p "
                + "JOIN users u ON u.user_id = p.user_id "
                + "JOIN borrow_records br ON br.record_id = p.borrow_record_id "
                + "JOIN books b ON b.book_id = br.book_id "
                + "WHERE p.penalty_id = ? AND p.penalty_type = 'FINE'";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, penaltyId);
            ResultSet rs = pstmt.executeQuery();
            if (!rs.next()) return null;
            LocalDateTime dueDate = toDateTime(rs.getTimestamp("due_date"));
            LocalDateTime returnDate = toDateTime(rs.getTimestamp("return_date"));
            return new AdminPenaltyResponse(
                    rs.getInt("penalty_id"),
                    rs.getInt("borrow_record_id"),
                    rs.getInt("user_id"),
                    rs.getString("student_no"),
                    rs.getString("user_name"),
                    rs.getInt("book_id"),
                    rs.getString("book_title"),
                    toString(rs.getTimestamp("borrow_date")),
                    toString(rs.getTimestamp("due_date")),
                    toString(rs.getTimestamp("return_date")),
                    PenaltyService.calculateOverdueDays(
                            dueDate,
                            returnDate == null ? LocalDateTime.now() : returnDate
                    ),
                    rs.getDouble("amount"),
                    rs.getString("status")
            );
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public Integer findFinePenaltyIdByBorrowRecordId(int recordId) {
        String sql = "SELECT penalty_id FROM user_penalties "
                + "WHERE borrow_record_id = ? AND penalty_type = 'FINE' "
                + "ORDER BY penalty_id DESC LIMIT 1";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, recordId);
            ResultSet rs = pstmt.executeQuery();
            return rs.next() ? rs.getInt("penalty_id") : null;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public boolean upsertFineForBorrowRecord(int userId, int recordId, double amount, String reason) {
        String selectSql = "SELECT penalty_id, status FROM user_penalties "
                + "WHERE borrow_record_id = ? AND penalty_type = 'FINE' "
                + "ORDER BY penalty_id DESC LIMIT 1 FOR UPDATE";
        String insertSql = "INSERT INTO user_penalties "
                + "(user_id, borrow_record_id, penalty_type, amount, reason, status) "
                + "VALUES (?, ?, 'FINE', ?, ?, 'OPEN')";
        String updateSql = "UPDATE user_penalties SET amount = ?, reason = ? "
                + "WHERE penalty_id = ? AND status = 'OPEN'";

        try (Connection conn = DBConnection.getConnection()) {
            conn.setAutoCommit(false);
            try (PreparedStatement select = conn.prepareStatement(selectSql)) {
                select.setInt(1, recordId);
                ResultSet rs = select.executeQuery();
                boolean success;
                if (rs.next()) {
                    if (!"OPEN".equals(rs.getString("status"))) {
                        conn.commit();
                        return true;
                    }
                    try (PreparedStatement update = conn.prepareStatement(updateSql)) {
                        update.setDouble(1, amount);
                        update.setString(2, reason);
                        update.setInt(3, rs.getInt("penalty_id"));
                        success = update.executeUpdate() > 0;
                    }
                } else {
                    try (PreparedStatement insert = conn.prepareStatement(insertSql)) {
                        insert.setInt(1, userId);
                        insert.setInt(2, recordId);
                        insert.setDouble(3, amount);
                        insert.setString(4, reason);
                        success = insert.executeUpdate() > 0;
                    }
                }
                if (success) {
                    conn.commit();
                } else {
                    conn.rollback();
                }
                return success;
            } catch (Exception e) {
                conn.rollback();
                throw e;
            } finally {
                conn.setAutoCommit(true);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public String payPenalty(int penaltyId, String studentId) {
        String selectSql = "SELECT p.status, br.return_date "
                + "FROM user_penalties p "
                + "JOIN users u ON u.user_id = p.user_id "
                + "LEFT JOIN borrow_records br ON br.record_id = p.borrow_record_id "
                + "WHERE p.penalty_id = ? AND p.penalty_type = 'FINE' AND u.student_no = ?";
        String updateSql = "UPDATE user_penalties SET status = 'PAID' "
                + "WHERE penalty_id = ? AND status = 'OPEN'";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement select = conn.prepareStatement(selectSql)) {
            select.setInt(1, penaltyId);
            select.setString(2, studentId);
            ResultSet rs = select.executeQuery();
            if (!rs.next()) return "NOT_FOUND";
            String status = rs.getString("status");
            if ("PAID".equals(status)) return "ALREADY_PAID";
            if ("WAIVED".equals(status)) return "WAIVED";
            if (rs.getTimestamp("return_date") == null) return "NOT_SETTLED";
            try (PreparedStatement update = conn.prepareStatement(updateSql)) {
                update.setInt(1, penaltyId);
                return update.executeUpdate() > 0 ? "PAID" : "FAILED";
            }
        } catch (Exception e) {
            e.printStackTrace();
            return "FAILED";
        }
    }

    public String adminUpdatePenaltyStatus(int penaltyId, String targetStatus) {
        if (!"PAID".equals(targetStatus) && !"WAIVED".equals(targetStatus)) {
            return "INVALID_STATUS";
        }
        AdminPenaltyResponse penalty = findPenaltyById(penaltyId);
        if (penalty == null) return "NOT_FOUND";
        if (!"OPEN".equals(penalty.getStatus())) return "INVALID_TRANSITION";
        String sql = "UPDATE user_penalties SET status = ? "
                + "WHERE penalty_id = ? AND penalty_type = 'FINE' AND status = 'OPEN'";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, targetStatus);
            pstmt.setInt(2, penaltyId);
            return pstmt.executeUpdate() > 0 ? "UPDATED" : "FAILED";
        } catch (Exception e) {
            e.printStackTrace();
            return "FAILED";
        }
    }

    private String normalizeDatabaseStatus(String status) {
        if (status == null || status.isBlank() || "ALL".equalsIgnoreCase(status)) return "";
        return "UNPAID".equalsIgnoreCase(status) ? "OPEN" : status.toUpperCase();
    }

    private LocalDateTime toDateTime(Timestamp timestamp) {
        return timestamp == null ? null : timestamp.toLocalDateTime();
    }

    private String toString(Timestamp timestamp) {
        return timestamp == null ? null : timestamp.toLocalDateTime().toString();
    }
}
