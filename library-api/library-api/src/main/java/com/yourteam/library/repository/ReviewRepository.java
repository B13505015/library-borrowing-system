package com.yourteam.library.repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import com.yourteam.library.config.DBConnection;
import library_api.dto.AdminReviewResponse;

public class ReviewRepository {
    public boolean addReview(int userId, int bookId, int rating, String comment) {
        String sql = "INSERT INTO reviews (user_id, book_id, rating, comment) VALUES (?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            pstmt.setInt(2, bookId);
            pstmt.setInt(3, rating);
            pstmt.setString(4, comment);
            return pstmt.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public List<String> findReviewsByBookId(int bookId) {
        List<String> list = new ArrayList<>();
        String sql = "SELECT rating, comment, created_at FROM reviews WHERE book_id = ? ORDER BY created_at DESC";
        try (Connection conn = DBConnection.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, bookId);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                list.add("★" + rs.getInt("rating") + " - " + rs.getString("comment") + " (" + rs.getTimestamp("created_at") + ")");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    public List<AdminReviewResponse> findAllReviews(String keyword) {
        List<AdminReviewResponse> list = new ArrayList<>();
        String normalizedKeyword = keyword == null ? "" : keyword.trim().toLowerCase();
        String likeKeyword = "%" + normalizedKeyword + "%";
        String sql = "SELECT r.review_id, r.book_id, b.title AS book_title, "
                + "r.user_id, u.student_no, u.name AS user_name, "
                + "r.rating, r.comment, r.created_at "
                + "FROM reviews r "
                + "JOIN books b ON b.book_id = r.book_id "
                + "JOIN users u ON u.user_id = r.user_id "
                + "WHERE ? = '' "
                + "OR LOWER(b.title) LIKE ? "
                + "OR LOWER(u.student_no) LIKE ? "
                + "OR LOWER(u.name) LIKE ? "
                + "OR LOWER(COALESCE(r.comment, '')) LIKE ? "
                + "ORDER BY r.created_at DESC";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, normalizedKeyword);
            pstmt.setString(2, likeKeyword);
            pstmt.setString(3, likeKeyword);
            pstmt.setString(4, likeKeyword);
            pstmt.setString(5, likeKeyword);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                list.add(new AdminReviewResponse(
                        rs.getInt("review_id"),
                        rs.getInt("book_id"),
                        rs.getString("book_title"),
                        rs.getInt("user_id"),
                        rs.getString("student_no"),
                        rs.getString("user_name"),
                        rs.getInt("rating"),
                        rs.getString("comment"),
                        rs.getTimestamp("created_at") == null
                                ? null
                                : rs.getTimestamp("created_at").toLocalDateTime().toString()
                ));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    public boolean deleteReview(int reviewId) {
        String sql = "DELETE FROM reviews WHERE review_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, reviewId);
            return pstmt.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}
