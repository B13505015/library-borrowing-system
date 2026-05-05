package com.yourteam.library.importer;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

public class UserJsonImporter {

    private static final String DB_URL = System.getenv().getOrDefault("LIB_DB_URL", "jdbc:mysql://localhost:3306/library_db");
    private static final String DB_USER = System.getenv().getOrDefault("LIB_DB_USER", "root");
    private static final String DB_PASSWORD = System.getenv().getOrDefault("LIB_DB_PASSWORD", "");

    public void importUsersJson() {
        String sql = "INSERT INTO users (student_no, name, password, role_level, created_at, status) VALUES (?, ?, ?, ?, ?, ?)";
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            ObjectMapper objectMapper = new ObjectMapper();
            InputStream inputStream = getClass().getClassLoader().getResourceAsStream("importer/Users.json");
            if (inputStream == null) throw new RuntimeException("找不到 Users.json");

            List<Map<String, Object>> userList = objectMapper.readValue(inputStream, new TypeReference<List<Map<String, Object>>>() {});
            int successCount = 0;

            for (Map<String, Object> userMap : userList) {
                stmt.setString(1, (String) userMap.get("student_no"));
                stmt.setString(2, (String) userMap.get("name"));
                stmt.setString(3, (String) userMap.get("password"));
                stmt.setString(4, (String) userMap.get("role_level"));

                String createdAtStr = (String) userMap.get("created_at");
                LocalDateTime createdAt = LocalDateTime.parse(createdAtStr, formatter);
                stmt.setObject(5, createdAt);

                stmt.setString(6, (String) userMap.get("status"));
                stmt.executeUpdate();
                successCount++;
            }

            System.out.println("Users.json 匯入完成，筆數: " + successCount);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
