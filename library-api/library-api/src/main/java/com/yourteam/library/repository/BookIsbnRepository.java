package com.yourteam.library.repository;

import java.sql.Connection;
import java.sql.PreparedStatement;

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
}