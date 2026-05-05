package com.yourteam.library.repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import com.yourteam.library.config.DBConnection;
import com.yourteam.library.entity.Admin;

public class AdminRepository {

    // 根據管理員帳號（username）查詢管理員資料
    // 之後管理員登入功能會先用這個方法找資料
    public Admin findByUsername(String username) {

        // SQL 查詢語法：從 admins 資料表找出指定 username 的資料
        String sql = "SELECT * FROM admins WHERE username = ?";

        try (
            // 建立資料庫連線
            Connection conn = DBConnection.getConnection();

            // 建立 PreparedStatement，避免 SQL Injection
            PreparedStatement pstmt = conn.prepareStatement(sql)
        ) {
            // 將 ? 替換成實際傳進來的 username
            pstmt.setString(1, username);

            // 執行查詢
            ResultSet rs = pstmt.executeQuery();

            // 如果查到資料，就把資料組成 Admin 物件
            if (rs.next()) {
                Admin admin = new Admin();

                admin.setAdminId(rs.getInt("admin_id"));
                admin.setUsername(rs.getString("username"));
                admin.setPassword(rs.getString("password"));
                admin.setName(rs.getString("username"));
                admin.setStatus("ACTIVE");

                return admin;
            }

        } catch (Exception e) {
            // 先直接印錯誤，之後再改正式例外處理
            e.printStackTrace();
        }

        // 查不到資料時回傳 null
        return null;
    }
}
