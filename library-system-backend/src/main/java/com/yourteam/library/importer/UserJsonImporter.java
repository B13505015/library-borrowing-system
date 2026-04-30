package com.yourteam.library.importer;

import java.io.InputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.yourteam.library.entity.User;
import com.yourteam.library.repository.UserRepository;

public class UserJsonImporter {

    // 建立 UserRepository 物件，負責把資料寫進資料庫
    private UserRepository userRepository;

    // 建構子（constructor）
    public UserJsonImporter() {
        this.userRepository = new UserRepository();
    }

    // 匯入 Users.json 到 users 資料表
    public void importUsersJson() {
        try {
            // 建立 Jackson 的 ObjectMapper，用來解析 JSON
            ObjectMapper objectMapper = new ObjectMapper();

            // 從 resources 資料夾讀取 Users.json
            InputStream inputStream = getClass().getClassLoader().getResourceAsStream("Users.json");

            // 如果找不到檔案，直接丟出例外
            if (inputStream == null) {
                throw new RuntimeException("找不到 Users.json，請確認檔案是否放在 src/main/resources");
            }

            // 將 JSON 解析成 List<Map<String, Object>>
            List<Map<String, Object>> userList = objectMapper.readValue(
                inputStream,
                new TypeReference<List<Map<String, Object>>>() {}
            );

            // 時間格式：對應 JSON 裡的 created_at，例如 2026-01-15 14:22:37
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

            // 計數器：統計成功匯入幾筆
            int successCount = 0;

            // 一筆一筆處理 JSON 資料
            for (Map<String, Object> userMap : userList) {
                // 建立 User 物件
                User user = new User();

                // 將 JSON 欄位資料塞進 User 物件
                user.setStudentNo((String) userMap.get("student_no"));
                user.setName((String) userMap.get("name"));
                user.setPassword((String) userMap.get("password"));
                user.setRoleLevel((String) userMap.get("role_level"));
                user.setStatus((String) userMap.get("status"));

                // 解析 created_at 字串成 LocalDateTime
                String createdAtStr = (String) userMap.get("created_at");
                LocalDateTime createdAt = LocalDateTime.parse(createdAtStr, formatter);

                // 呼叫 repository 寫進資料庫
                boolean success = userRepository.insertUser(user, createdAt);

                if (success) {
                    successCount++;
                }
            }

            // 印出匯入結果
            System.out.println("Users.json 匯入完成");
            System.out.println("成功匯入筆數: " + successCount);

        } catch (Exception e) {
            // 發生錯誤時印出錯誤訊息
            e.printStackTrace();
        }
    }
}