package com.yourteam.library.config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {

    // 資料庫連線網址
    // 將時區改成 Asia/Taipei，避免時間差 8 小時
	private static final String URL = "jdbc:mysql://127.0.0.1:3306/library_system?useSSL=false&serverTimezone=Asia/Taipei";
    // MySQL 帳號
    private static final String USER = "root";

    // MySQL 密碼
    private static final String PASSWORD = "0000";

    // 提供其他 class 呼叫的連線方法
    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}