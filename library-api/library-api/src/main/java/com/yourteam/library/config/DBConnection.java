package com.yourteam.library.config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {

    private static String getenvOrDefault(String key, String defaultValue) {
        String value = System.getenv(key);
        return value == null || value.isBlank() ? defaultValue : value;
    }

    // Zeabur / 部署環境可透過環境變數覆蓋，未設定時回退本機預設值
    private static final String DB_HOST = getenvOrDefault("DB_HOST", "127.0.0.1");
    private static final String DB_PORT = getenvOrDefault("DB_PORT", "3306");
    private static final String DB_NAME = getenvOrDefault("DB_NAME", "library_system");
    private static final String DB_USER = getenvOrDefault("DB_USER", "root");
    private static final String DB_PASSWORD = getenvOrDefault("DB_PASSWORD", "0000");

    // 將時區改成 Asia/Taipei，避免時間差 8 小時
    private static final String URL = String.format(
            "jdbc:mysql://%s:%s/%s?useSSL=false&serverTimezone=Asia/Taipei",
            DB_HOST,
            DB_PORT,
            DB_NAME
    );

    // 提供其他 class 呼叫的連線方法
    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, DB_USER, DB_PASSWORD);
    }
}
