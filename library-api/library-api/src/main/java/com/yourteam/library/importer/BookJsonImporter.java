package com.yourteam.library.importer;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

public class BookJsonImporter {
    private static final String DB_URL = System.getenv().getOrDefault("LIB_DB_URL", "jdbc:mysql://localhost:3306/library_system");
    private static final String DB_USER = System.getenv().getOrDefault("LIB_DB_USER", "root");
    private static final String DB_PASSWORD = System.getenv().getOrDefault("LIB_DB_PASSWORD", "");

    public void importBooksJson() {
        String bookSql = "INSERT INTO books (title, authors, subjects, publisher, publish_year, edition, format_desc, source, note, status) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        String isbnSql = "INSERT INTO book_isbns (book_id, isbn) VALUES (?, ?)";

        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement bookStmt = conn.prepareStatement(bookSql, PreparedStatement.RETURN_GENERATED_KEYS);
             PreparedStatement isbnStmt = conn.prepareStatement(isbnSql)) {

            ObjectMapper objectMapper = new ObjectMapper();
            InputStream inputStream = getClass().getClassLoader().getResourceAsStream("importer/Books.json");
            if (inputStream == null) throw new RuntimeException("找不到 Books.json");

            List<Map<String, Object>> bookList = objectMapper.readValue(inputStream, new TypeReference<List<Map<String, Object>>>() {});
            int successCount = 0;

            for (Map<String, Object> bookMap : bookList) {
                bookStmt.setString(1, (String) bookMap.get("題名"));
                bookStmt.setString(2, joinArray(bookMap.get("作者")));
                bookStmt.setString(3, joinArray(bookMap.get("主題")));
                bookStmt.setString(4, (String) bookMap.get("出版者"));
                bookStmt.setString(5, objectToString(bookMap.get("出版年")));
                bookStmt.setString(6, (String) bookMap.get("版本"));
                bookStmt.setString(7, (String) bookMap.get("格式"));
                bookStmt.setString(8, (String) bookMap.get("資料來源"));
                bookStmt.setString(9, (String) bookMap.get("附註"));
                bookStmt.setString(10, "AVAILABLE");
                bookStmt.executeUpdate();

                int bookId = -1;
                try (var keys = bookStmt.getGeneratedKeys()) {
                    if (keys.next()) bookId = keys.getInt(1);
                }
                if (bookId == -1) continue;

                Object isbnsObj = bookMap.get("識別號");
                if (isbnsObj instanceof List<?>) {
                    for (Object isbnObj : (List<?>) isbnsObj) {
                        if (isbnObj != null) {
                            isbnStmt.setInt(1, bookId);
                            isbnStmt.setString(2, isbnObj.toString());
                            isbnStmt.executeUpdate();
                        }
                    }
                }
                successCount++;
            }

            System.out.println("Books.json 匯入完成，筆數: " + successCount);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String joinArray(Object value) {
        if (!(value instanceof List<?> list)) return null;
        return list.stream().map(Object::toString).collect(Collectors.joining(", "));
    }

    private String objectToString(Object value) {
        return value == null ? null : value.toString();
    }
}
