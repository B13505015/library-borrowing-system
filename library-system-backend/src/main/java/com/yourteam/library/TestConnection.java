package com.yourteam.library;

import java.sql.Connection;

import com.yourteam.library.config.DBConnection;

public class TestConnection {

    public static void main(String[] args) {
        try (
            // 嘗試取得資料庫連線
            Connection conn = DBConnection.getConnection()
        ) {
            // 如果成功，就印出成功訊息
            System.out.println("Database connected successfully.");
        } catch (Exception e) {
            // 如果失敗，就印出錯誤訊息
            e.printStackTrace();
        }
    }
}