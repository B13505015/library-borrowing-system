package com.yourteam.library.repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import com.yourteam.library.config.DBConnection;

public class FavoriteRepository {
    public boolean addFavorite(int userId, int bookId) {
        String sql = "INSERT INTO favorites (user_id, book_id) VALUES (?, ?)";
        try (Connection conn = DBConnection.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            pstmt.setInt(2, bookId);
            return pstmt.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean removeFavorite(int userId, int bookId) {
        String sql = "DELETE FROM favorites WHERE user_id = ? AND book_id = ?";
        try (Connection conn = DBConnection.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            pstmt.setInt(2, bookId);
            return pstmt.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public List<Integer> getFavoriteBookIds(int userId) {
        List<Integer> list = new ArrayList<>();
        String sql = "SELECT book_id FROM favorites WHERE user_id = ? ORDER BY created_at DESC";
        try (Connection conn = DBConnection.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) list.add(rs.getInt("book_id"));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }
}
