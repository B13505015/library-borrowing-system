package com.yourteam.library.repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import com.yourteam.library.config.DBConnection;
import com.yourteam.library.entity.BorrowRecord;

public class BorrowRecordRepository {

    // 查詢所有借閱紀錄
    public List<BorrowRecord> findAllRecords() {
        // 建立 List，用來存放所有借閱紀錄
        List<BorrowRecord> recordList = new ArrayList<>();

        // SQL：查詢 borrow_records 資料表全部資料
        String sql = "SELECT * FROM borrow_records ORDER BY borrow_date DESC";

        try (
            // 建立資料庫連線
            Connection conn = DBConnection.getConnection();

            // 建立 PreparedStatement
            PreparedStatement pstmt = conn.prepareStatement(sql)
        ) {
            // 執行查詢
            ResultSet rs = pstmt.executeQuery();

            // 一筆一筆讀取結果
            while (rs.next()) {
                BorrowRecord record = new BorrowRecord();

                // 基本欄位
                record.setRecordId(rs.getInt("record_id"));
                record.setUserId(rs.getInt("user_id"));
                record.setBookId(rs.getInt("book_id"));
                record.setBorrowDays(rs.getInt("borrow_days"));

                // borrow_date 轉成 LocalDateTime
                Timestamp borrowTimestamp = rs.getTimestamp("borrow_date");
                if (borrowTimestamp != null) {
                    record.setBorrowDate(borrowTimestamp.toLocalDateTime());
                }

                // due_date 轉成 LocalDateTime
                Timestamp dueTimestamp = rs.getTimestamp("due_date");
                if (dueTimestamp != null) {
                    record.setDueDate(dueTimestamp.toLocalDateTime());
                }

                // return_date 可能是 null，所以要先判斷
                Timestamp returnTimestamp = rs.getTimestamp("return_date");
                if (returnTimestamp != null) {
                    record.setReturnDate(returnTimestamp.toLocalDateTime());
                }

                // created_at 轉成 LocalDateTime
                Timestamp createdTimestamp = rs.getTimestamp("created_at");
                if (createdTimestamp != null) {
                    record.setCreatedAt(createdTimestamp.toLocalDateTime());
                }

                // 加入 List
                recordList.add(record);
            }

        } catch (Exception e) {
            // 先直接印出錯誤訊息
            e.printStackTrace();
        }

        // 回傳所有借閱紀錄
        return recordList;
    }

    // 根據 user_id 查詢某個使用者的借閱紀錄
    public List<BorrowRecord> findRecordsByUserId(int userId) {
        // 建立 List，用來存放該使用者的借閱紀錄
        List<BorrowRecord> recordList = new ArrayList<>();

        // SQL：查詢指定 user_id 的借閱紀錄
        String sql = "SELECT * FROM borrow_records WHERE user_id = ?";

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

            // 一筆一筆讀取結果
            while (rs.next()) {
                BorrowRecord record = new BorrowRecord();

                // 基本欄位
                record.setRecordId(rs.getInt("record_id"));
                record.setUserId(rs.getInt("user_id"));
                record.setBookId(rs.getInt("book_id"));
                record.setBorrowDays(rs.getInt("borrow_days"));

                // borrow_date 轉成 LocalDateTime
                Timestamp borrowTimestamp = rs.getTimestamp("borrow_date");
                if (borrowTimestamp != null) {
                    record.setBorrowDate(borrowTimestamp.toLocalDateTime());
                }

                // due_date 轉成 LocalDateTime
                Timestamp dueTimestamp = rs.getTimestamp("due_date");
                if (dueTimestamp != null) {
                    record.setDueDate(dueTimestamp.toLocalDateTime());
                }

                // return_date 可能是 null，所以要先判斷
                Timestamp returnTimestamp = rs.getTimestamp("return_date");
                if (returnTimestamp != null) {
                    record.setReturnDate(returnTimestamp.toLocalDateTime());
                }

                // created_at 轉成 LocalDateTime
                Timestamp createdTimestamp = rs.getTimestamp("created_at");
                if (createdTimestamp != null) {
                    record.setCreatedAt(createdTimestamp.toLocalDateTime());
                }

                // 加入 List
                recordList.add(record);
            }

        } catch (Exception e) {
            // 先直接印出錯誤訊息
            e.printStackTrace();
        }

        // 回傳該使用者的借閱紀錄
        return recordList;
    }
    // 新增一筆借閱紀錄
    public boolean insertBorrowRecord(BorrowRecord record) {

        // SQL：新增 borrow_records 資料
        String sql = "INSERT INTO borrow_records (user_id, book_id, borrow_date, due_date, return_date, borrow_days, created_at) "
                   + "VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (
            // 建立資料庫連線
            Connection conn = DBConnection.getConnection();

            // 建立 PreparedStatement
            PreparedStatement pstmt = conn.prepareStatement(sql)
        ) {
            // 設定基本欄位
            pstmt.setInt(1, record.getUserId());
            pstmt.setInt(2, record.getBookId());
            pstmt.setTimestamp(3, Timestamp.valueOf(record.getBorrowDate()));
            pstmt.setTimestamp(4, Timestamp.valueOf(record.getDueDate()));

            // return_date 可能是 null，要特別處理
            if (record.getReturnDate() != null) {
                pstmt.setTimestamp(5, Timestamp.valueOf(record.getReturnDate()));
            } else {
                pstmt.setTimestamp(5, null);
            }

            pstmt.setInt(6, record.getBorrowDays());
            pstmt.setTimestamp(7, Timestamp.valueOf(record.getCreatedAt()));

            // 執行新增
            int affectedRows = pstmt.executeUpdate();

            // 如果有新增成功，回傳 true
            return affectedRows > 0;

        } catch (Exception e) {
            // 先直接印錯誤訊息
            e.printStackTrace();
        }

        // 新增失敗時回傳 false
        return false;
    }
    
    // 根據 record_id 查詢單一借閱紀錄
    public BorrowRecord findByRecordId(int recordId) {

        // SQL：查詢指定 record_id 的借閱紀錄
        String sql = "SELECT * FROM borrow_records WHERE record_id = ?";

        try (
            // 建立資料庫連線
            Connection conn = DBConnection.getConnection();

            // 建立 PreparedStatement
            PreparedStatement pstmt = conn.prepareStatement(sql)
        ) {
            // 將 ? 替換成實際傳進來的 recordId
            pstmt.setInt(1, recordId);

            // 執行查詢
            ResultSet rs = pstmt.executeQuery();

            // 如果查到資料，就建立 BorrowRecord 物件回傳
            if (rs.next()) {
                BorrowRecord record = new BorrowRecord();

                record.setRecordId(rs.getInt("record_id"));
                record.setUserId(rs.getInt("user_id"));
                record.setBookId(rs.getInt("book_id"));
                record.setBorrowDays(rs.getInt("borrow_days"));

                Timestamp borrowTimestamp = rs.getTimestamp("borrow_date");
                if (borrowTimestamp != null) {
                    record.setBorrowDate(borrowTimestamp.toLocalDateTime());
                }

                Timestamp dueTimestamp = rs.getTimestamp("due_date");
                if (dueTimestamp != null) {
                    record.setDueDate(dueTimestamp.toLocalDateTime());
                }

                Timestamp returnTimestamp = rs.getTimestamp("return_date");
                if (returnTimestamp != null) {
                    record.setReturnDate(returnTimestamp.toLocalDateTime());
                }

                Timestamp createdTimestamp = rs.getTimestamp("created_at");
                if (createdTimestamp != null) {
                    record.setCreatedAt(createdTimestamp.toLocalDateTime());
                }

                return record;
            }

        } catch (Exception e) {
            // 先直接印錯誤訊息
            e.printStackTrace();
        }

        // 查不到資料時回傳 null
        return null;
    }
    
    // 根據 record_id 更新還書時間
    public boolean updateReturnDate(int recordId, java.time.LocalDateTime returnDate) {

        // SQL：更新指定借閱紀錄的 return_date
        String sql = "UPDATE borrow_records SET return_date = ? WHERE record_id = ?";

        try (
            // 建立資料庫連線
            Connection conn = DBConnection.getConnection();

            // 建立 PreparedStatement
            PreparedStatement pstmt = conn.prepareStatement(sql)
        ) {
            // 設定 SQL 參數
            pstmt.setTimestamp(1, Timestamp.valueOf(returnDate));
            pstmt.setInt(2, recordId);

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
    
    public java.util.List<BorrowRecord> findRecordsByBookId(int bookId) {
        java.util.List<BorrowRecord> recordList = new java.util.ArrayList<>();

        String sql = "SELECT * FROM borrow_records WHERE book_id = ? ORDER BY borrow_date DESC";

        try (
            Connection conn = DBConnection.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql)
        ) {
            pstmt.setInt(1, bookId);

            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                BorrowRecord record = new BorrowRecord();

                record.setRecordId(rs.getInt("record_id"));
                record.setUserId(rs.getInt("user_id"));
                record.setBookId(rs.getInt("book_id"));
                record.setBorrowDays(rs.getInt("borrow_days"));

                java.sql.Timestamp borrowTimestamp = rs.getTimestamp("borrow_date");
                if (borrowTimestamp != null) {
                    record.setBorrowDate(borrowTimestamp.toLocalDateTime());
                }

                java.sql.Timestamp dueTimestamp = rs.getTimestamp("due_date");
                if (dueTimestamp != null) {
                    record.setDueDate(dueTimestamp.toLocalDateTime());
                }

                java.sql.Timestamp returnTimestamp = rs.getTimestamp("return_date");
                if (returnTimestamp != null) {
                    record.setReturnDate(returnTimestamp.toLocalDateTime());
                }

                java.sql.Timestamp createdTimestamp = rs.getTimestamp("created_at");
                if (createdTimestamp != null) {
                    record.setCreatedAt(createdTimestamp.toLocalDateTime());
                }

                recordList.add(record);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return recordList;
    }
    public int countActiveBorrowsByUserId(int userId) {
        String sql = "SELECT COUNT(*) AS c FROM borrow_records WHERE user_id = ? AND return_date IS NULL";
        try (Connection conn = DBConnection.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) return rs.getInt("c");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }



    public boolean hasActiveBorrowByUserAndBook(int userId, int bookId) {
        String sql = "SELECT 1 FROM borrow_records WHERE user_id = ? AND book_id = ? AND return_date IS NULL LIMIT 1";
        try (Connection conn = DBConnection.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            pstmt.setInt(2, bookId);
            ResultSet rs = pstmt.executeQuery();
            return rs.next();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}