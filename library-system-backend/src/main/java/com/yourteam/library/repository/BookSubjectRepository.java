package com.yourteam.library.repository;

import java.sql.Connection;
import java.sql.PreparedStatement;

import com.yourteam.library.config.DBConnection;

public class BookSubjectRepository {

    // 新增一個主題到 book_subjects 資料表
    public boolean insertSubject(int bookId, String subjectName) {

        // SQL：新增主題資料
        String sql = "INSERT INTO book_subjects (book_id, subject_name) VALUES (?, ?)";

        try (
            // 建立資料庫連線
            Connection conn = DBConnection.getConnection();

            // 建立 PreparedStatement
            PreparedStatement pstmt = conn.prepareStatement(sql)
        ) {
            // 設定 SQL 參數
            pstmt.setInt(1, bookId);
            pstmt.setString(2, subjectName);

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