package com.yourteam.library.repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import com.yourteam.library.config.DBConnection;

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
}
