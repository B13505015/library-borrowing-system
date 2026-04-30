package com.yourteam.library.importer;

import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.yourteam.library.entity.BorrowRecord;
import com.yourteam.library.repository.BorrowRecordRepository;
import com.yourteam.library.util.RelativeDateParser;

public class BorrowRecordJsonImporter {

    // 建立 BorrowRecordRepository 物件，負責把資料寫進資料庫
    private BorrowRecordRepository borrowRecordRepository;

    // 建構子（constructor）
    public BorrowRecordJsonImporter() {
        this.borrowRecordRepository = new BorrowRecordRepository();
    }

    // 匯入 Borrow_records.json 到 borrow_records 資料表
    public void importBorrowRecordsJson() {
        try {
            // 建立 Jackson 的 ObjectMapper，用來解析 JSON
            ObjectMapper objectMapper = new ObjectMapper();

            // 從 resources 資料夾讀取 Borrow_records.json
            InputStream inputStream = getClass().getClassLoader().getResourceAsStream("Borrow_records.json");

            // 如果找不到檔案，直接丟出例外
            if (inputStream == null) {
                throw new RuntimeException("找不到 Borrow_records.json，請確認檔案是否放在 src/main/resources");
            }

            // 將 JSON 解析成 List<Map<String, Object>>
            List<Map<String, Object>> recordList = objectMapper.readValue(
                inputStream,
                new TypeReference<List<Map<String, Object>>>() {}
            );

            // 計數器：統計成功匯入幾筆
            int successCount = 0;

            // 一筆一筆處理 JSON 資料
            for (Map<String, Object> recordMap : recordList) {
                BorrowRecord record = new BorrowRecord();

                // user_id、book_id、borrow_days 可能被 Jackson 解析成 Integer 或其他 Number 類型
                Object userIdObj = recordMap.get("user_id");
                Object bookIdObj = recordMap.get("book_id");
                Object borrowDaysObj = recordMap.get("borrow_days");

                if (userIdObj instanceof Number) {
                    record.setUserId(((Number) userIdObj).intValue());
                }

                if (bookIdObj instanceof Number) {
                    record.setBookId(((Number) bookIdObj).intValue());
                }

                if (borrowDaysObj instanceof Number) {
                    record.setBorrowDays(((Number) borrowDaysObj).intValue());
                }

                // 解析相對日期字串
                String borrowDateStr = (String) recordMap.get("borrow_date");
                String dueDateStr = (String) recordMap.get("due_date");
                String returnDateStr = (String) recordMap.get("return_date");
                String createdAtStr = (String) recordMap.get("created_at");

                LocalDateTime borrowDate = RelativeDateParser.parseRelativeDate(borrowDateStr);
                LocalDateTime dueDate = RelativeDateParser.parseRelativeDate(dueDateStr);
                LocalDateTime createdAt = RelativeDateParser.parseRelativeDate(createdAtStr);

                record.setBorrowDate(borrowDate);
                record.setDueDate(dueDate);
                record.setCreatedAt(createdAt);

                // return_date 可能為 null，也可能是相對日期字串
                if (returnDateStr != null) {
                    record.setReturnDate(RelativeDateParser.parseRelativeDate(returnDateStr));
                } else {
                    record.setReturnDate(null);
                }

                // 寫入資料庫
                boolean success = borrowRecordRepository.insertBorrowRecord(record);

                if (success) {
                    successCount++;
                }
            }

            // 印出匯入結果
            System.out.println("Borrow_records.json 匯入完成");
            System.out.println("成功匯入筆數: " + successCount);

        } catch (Exception e) {
            // 發生錯誤時印出錯誤訊息
            e.printStackTrace();
        }
    }
}