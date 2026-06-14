package com.yourteam.library.repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;

import com.yourteam.library.config.DBConnection;

public class BookIsbnRepository {

    // 新增一個 ISBN 到 book_isbns 資料表
    public boolean insertIsbn(int bookId, String isbn) {

        // SQL：新增 ISBN 資料
        String sql = "INSERT INTO book_isbns (book_id, isbn) VALUES (?, ?)";

        try (
            // 建立資料庫連線
            Connection conn = DBConnection.getConnection();

            // 建立 PreparedStatement
            PreparedStatement pstmt = conn.prepareStatement(sql)
        ) {
            // 設定 SQL 參數
            pstmt.setInt(1, bookId);
            pstmt.setString(2, isbn);

            // 執行新增
            int affectedRows = pstmt.executeUpdate();

            // 如果有新增成功，回傳 true
            return affectedRows > 0;

        } catch (Exception e) {
            // 發生錯誤時印出訊息
            e.printStackTrace();
        }

        // 新增失敗時回傳 false
        return false;
    }

    public List<String> findIsbnsByBookId(int bookId) {
        List<String> isbns = new ArrayList<>();
        String sql = "SELECT isbn FROM book_isbns WHERE book_id = ? ORDER BY isbn_id";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, bookId);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) isbns.add(rs.getString("isbn"));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return isbns;
    }

    public boolean replaceIsbns(int bookId, List<String> isbns) {
        String deleteSql = "DELETE FROM book_isbns WHERE book_id = ?";
        String insertSql = "INSERT INTO book_isbns (book_id, isbn) VALUES (?, ?)";
        LinkedHashSet<String> normalized = new LinkedHashSet<>();
        if (isbns != null) {
            for (String isbn : isbns) {
                if (isbn != null && !isbn.trim().isEmpty()) normalized.add(isbn.trim());
            }
        }

        try (Connection conn = DBConnection.getConnection()) {
            conn.setAutoCommit(false);
            try {
                try (PreparedStatement delete = conn.prepareStatement(deleteSql)) {
                    delete.setInt(1, bookId);
                    delete.executeUpdate();
                }
                try (PreparedStatement insert = conn.prepareStatement(insertSql)) {
                    for (String isbn : normalized) {
                        insert.setInt(1, bookId);
                        insert.setString(2, isbn);
                        insert.addBatch();
                    }
                    if (!normalized.isEmpty()) insert.executeBatch();
                }
                conn.commit();
                return true;
            } catch (Exception e) {
                conn.rollback();
                e.printStackTrace();
            } finally {
                conn.setAutoCommit(true);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}
