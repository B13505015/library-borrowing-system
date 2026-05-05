package com.yourteam.library.repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import com.yourteam.library.config.DBConnection;
import com.yourteam.library.entity.User;

public class UserRepository {

    // 根據學號（student_no）查詢使用者
    // 之後一般使用者登入功能會先用這個方法找資料
    public User findByStudentNo(String studentNo) {

        // SQL 查詢語法：從 users 資料表找出指定 student_no 的資料
        String sql = "SELECT * FROM users WHERE student_no = ?";

        try (
            // 建立資料庫連線
            Connection conn = DBConnection.getConnection();

            // 建立 PreparedStatement，避免 SQL Injection
            PreparedStatement pstmt = conn.prepareStatement(sql)
        ) {
            // 將 ? 替換成實際傳進來的 studentNo
            pstmt.setString(1, studentNo);

            // 執行查詢
            ResultSet rs = pstmt.executeQuery();

            // 如果查到資料，就把資料組成 User 物件
            if (rs.next()) {
                User user = new User();

                user.setUserId(rs.getInt("user_id"));
                user.setStudentNo(rs.getString("student_no"));
                user.setName(rs.getString("name"));
                user.setPassword(rs.getString("password"));
                user.setRoleLevel(rs.getString("role_level"));
                user.setStatus(rs.getString("status"));

                return user;
            }

        } catch (Exception e) {
            // 先直接印錯誤，之後再改成更正式的例外處理
            e.printStackTrace();
        }

        // 查不到資料時回傳 null
        return null;
    }
    
    // 根據 user_id 查詢使用者
    public User findByUserId(int userId) {

        // SQL：從 users 資料表找出指定 user_id 的資料
        String sql = "SELECT * FROM users WHERE user_id = ?";

        try (
            // 建立資料庫連線
            Connection conn = DBConnection.getConnection();

            // 建立 PreparedStatement
            PreparedStatement pstmt = conn.prepareStatement(sql)
        ) {
            // 將 ? 替換成實際傳進來的 userId
            pstmt.setInt(1, userId);

            // 執行查詢
            ResultSet rs = pstmt.executeQuery();

            // 如果查到資料，就把資料組成 User 物件
            if (rs.next()) {
                User user = new User();

                user.setUserId(rs.getInt("user_id"));
                user.setStudentNo(rs.getString("student_no"));
                user.setName(rs.getString("name"));
                user.setPassword(rs.getString("password"));
                user.setRoleLevel(rs.getString("role_level"));
                user.setStatus(rs.getString("status"));

                return user;
            }

        } catch (Exception e) {
            // 先直接印錯誤訊息
            e.printStackTrace();
        }

        // 查不到資料時回傳 null
        return null;
    }
    
    
    
    
    // 根據 userId 更新使用者狀態
    public boolean updateUserStatus(int userId, String status) {

        // SQL：更新指定使用者的 status
        String sql = "UPDATE users SET status = ? WHERE user_id = ?";

        try (
            // 建立資料庫連線
            Connection conn = DBConnection.getConnection();

            // 建立 PreparedStatement
            PreparedStatement pstmt = conn.prepareStatement(sql)
        ) {
            // 設定 SQL 參數
            pstmt.setString(1, status);
            pstmt.setInt(2, userId);

            // 執行更新
            int affectedRows = pstmt.executeUpdate();

            // 如果有更新到資料，回傳 true
            return affectedRows > 0;

        } catch (Exception e) {
            // 先直接印錯誤訊息
            e.printStackTrace();
        }

        // 更新失敗時回傳 false
        return false;
    }
    
    
    
    // 新增一筆使用者資料到 users 資料表
    public boolean insertUser(User user, java.time.LocalDateTime createdAt) {

        // SQL：新增 users 資料
        String sql = "INSERT INTO users (student_no, name, password, role_level, created_at, status) "
                   + "VALUES (?, ?, ?, ?, ?, ?)";

        try (
            // 建立資料庫連線
            Connection conn = DBConnection.getConnection();

            // 建立 PreparedStatement
            PreparedStatement pstmt = conn.prepareStatement(sql)
        ) {
            // 設定 SQL 參數
            pstmt.setString(1, user.getStudentNo());
            pstmt.setString(2, user.getName());
            pstmt.setString(3, user.getPassword());
            pstmt.setString(4, user.getRoleLevel());
            pstmt.setTimestamp(5, java.sql.Timestamp.valueOf(createdAt));
            pstmt.setString(6, user.getStatus());

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
    
    public java.util.List<User> findAllUsers() {
        java.util.List<User> userList = new java.util.ArrayList<>();

        String sql = "SELECT * FROM users";

        try (
            Connection conn = DBConnection.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql)
        ) {
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                User user = new User();
                user.setUserId(rs.getInt("user_id"));
                user.setStudentNo(rs.getString("student_no"));
                user.setName(rs.getString("name"));
                user.setPassword(rs.getString("password"));
                user.setRoleLevel(rs.getString("role_level"));
                user.setStatus(rs.getString("status"));

                userList.add(user);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return userList;
    }
    
    
    public java.util.List<User> searchUsersByKeyword(String keyword) {
        java.util.List<User> userList = new java.util.ArrayList<>();

        String sql = "SELECT * FROM users WHERE student_no LIKE ? OR name LIKE ?";

        try (
            Connection conn = DBConnection.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql)
        ) {
            String searchValue = "%" + keyword + "%";
            pstmt.setString(1, searchValue);
            pstmt.setString(2, searchValue);

            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                User user = new User();
                user.setUserId(rs.getInt("user_id"));
                user.setStudentNo(rs.getString("student_no"));
                user.setName(rs.getString("name"));
                user.setPassword(rs.getString("password"));
                user.setRoleLevel(rs.getString("role_level"));
                user.setStatus(rs.getString("status"));

                userList.add(user);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return userList;
    }
}