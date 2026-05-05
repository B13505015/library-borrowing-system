package com.yourteam.library.repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import com.yourteam.library.config.DBConnection;
import com.yourteam.library.entity.Admin;

public class AdminRepository {

    // 根據管理員帳號 username 查詢管理員資料
    public Admin findByUsername(String username) {

        // admins 表目前沒有 name / status 欄位
        // 所以這裡用 username AS name、'ACTIVE' AS status 補出查詢結果欄位
        String sql = """
            SELECT 
                admin_id,
                username,
                password,
                username AS name,
                'ACTIVE' AS status
            FROM admins
            WHERE username = ?
        """;

        try (
            Connection conn = DBConnection.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql)
        ) {
            pstmt.setString(1, username);

            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                Admin admin = new Admin();

                admin.setAdminId(rs.getInt("admin_id"));
                admin.setUsername(rs.getString("username"));
                admin.setPassword(rs.getString("password"));
                admin.setStatus(rs.getString("status"));

                return admin;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }
}