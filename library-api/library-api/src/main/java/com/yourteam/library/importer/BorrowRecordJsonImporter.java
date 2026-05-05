package com.yourteam.library.importer;

import java.io.InputStream;
import com.yourteam.library.util.RelativeDateParser;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

public class BorrowRecordJsonImporter {

    private static final String DB_URL = System.getenv().getOrDefault("LIB_DB_URL", "jdbc:mysql://localhost:3306/library_system");
    private static final String DB_USER = System.getenv().getOrDefault("LIB_DB_USER", "root");
    private static final String DB_PASSWORD = System.getenv().getOrDefault("LIB_DB_PASSWORD", "0000");

    public void importBorrowRecordsJson() {
        String insertSql = "INSERT INTO borrow_records (user_id, book_id, borrow_date, due_date, return_date, borrow_days, created_at) VALUES (?, ?, ?, ?, ?, ?, ?)";
        String resetBookStatusSql = "UPDATE books SET status = 'AVAILABLE'";
        String markBorrowedBookStatusSql = "UPDATE books SET status = 'BORROWED' WHERE book_id = ?";
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
                Statement resetBookStatusStmt = conn.createStatement();
                PreparedStatement stmt = conn.prepareStatement(insertSql);
                PreparedStatement borrowedBookStmt = conn.prepareStatement(markBorrowedBookStatusSql)) {

               resetBookStatusStmt.executeUpdate(resetBookStatusSql);

            ObjectMapper objectMapper = new ObjectMapper();
            InputStream inputStream = getClass().getClassLoader().getResourceAsStream("Borrow_records.json");
            if (inputStream == null) throw new RuntimeException("找不到 Borrow_records.json");

            List<Map<String, Object>> recordList = objectMapper.readValue(inputStream, new TypeReference<List<Map<String, Object>>>() {});
            int successCount = 0;

            for (Map<String, Object> recordMap : recordList) {
                stmt.setInt(1, ((Number) recordMap.get("user_id")).intValue());
                stmt.setInt(2, ((Number) recordMap.get("book_id")).intValue());
                stmt.setObject(3, parseDate(recordMap.get("borrow_date"), formatter));
                stmt.setObject(4, parseDate(recordMap.get("due_date"), formatter));
                stmt.setObject(5, parseDate(recordMap.get("return_date"), formatter));
                stmt.setInt(6, ((Number) recordMap.get("borrow_days")).intValue());
                stmt.setObject(7, parseDate(recordMap.get("created_at"), formatter));
                stmt.executeUpdate();
                
                if (parseDate(recordMap.get("return_date"), formatter) == null) {
                    borrowedBookStmt.setInt(1, ((Number) recordMap.get("book_id")).intValue());
                    borrowedBookStmt.executeUpdate();
                }             
                
                successCount++;
            }

            System.out.println("Borrow_records.json 匯入完成，筆數: " + successCount);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private LocalDateTime parseDate(Object value, DateTimeFormatter formatter) {
        if (value == null) {
            return null;
        }

        String text = value.toString().trim();

        // 處理空字串或 "null"
        if (text.isEmpty() || text.equalsIgnoreCase("null")) {
            return null;
        }

        // 處理像 "-45 days"、"7 days"、"+10 days" 這種相對日期
        if (text.toLowerCase().matches("[-+]?\\d+\\s+days?")) {
            return RelativeDateParser.parseRelativeDate(text);
        }

        // 處理一般日期格式，例如 "2026-05-05 12:30:00"
        return LocalDateTime.parse(text, formatter);
    }
}
