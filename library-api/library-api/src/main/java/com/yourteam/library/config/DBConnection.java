package com.yourteam.library.config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {

    // 從環境變數讀取資料庫設定
    // 如果沒有環境變數，就使用本機預設值，方便你在自己電腦測試
    private static final String DB_HOST = System.getenv().getOrDefault("DB_HOST", "127.0.0.1");
    private static final String DB_PORT = System.getenv().getOrDefault("DB_PORT", "3306");
    private static final String DB_NAME = System.getenv().getOrDefault("DB_NAME", "library_system");
    private static final String DB_USER = System.getenv().getOrDefault("DB_USER", "root");
    private static final String DB_PASSWORD = System.getenv().getOrDefault("DB_PASSWORD", "0000");

    private static final String URL =
            "jdbc:mysql://" + DB_HOST + ":" + DB_PORT + "/" + DB_NAME
            + "?useSSL=false"
            + "&serverTimezone=Asia/Taipei"
            + "&allowPublicKeyRetrieval=true"
            + "&characterEncoding=utf8";

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, DB_USER, DB_PASSWORD);
    }
}
