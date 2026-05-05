package com.yourteam.library.repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import com.yourteam.library.config.DBConnection;

import library_api.dto.PopularBookResponse;
import com.yourteam.library.entity.Book;

public class BookRepository {

    // 查詢所有書籍資料
    public List<Book> findAllBooks() {
        // 建立一個 List，用來存放查到的所有 Book 物件
        List<Book> bookList = new ArrayList<>();

        // SQL：查詢 books 資料表全部資料
        String sql = "SELECT * FROM books WHERE status <> 'REMOVED'";

        try (
            // 建立資料庫連線
            Connection conn = DBConnection.getConnection();

            // 建立 PreparedStatement
            PreparedStatement pstmt = conn.prepareStatement(sql)
        ) {
            // 執行查詢
            ResultSet rs = pstmt.executeQuery();

            // 一筆一筆讀取查詢結果
            while (rs.next()) {
                Book book = new Book();

                // 將資料表欄位塞進 Book 物件
                book.setBookId(rs.getInt("book_id"));
                book.setTitle(rs.getString("title"));
                book.setPublisher(rs.getString("publisher"));
                book.setPublishYear(rs.getInt("publish_year"));
                book.setEdition(rs.getString("edition"));
                book.setFormat(rs.getString("format"));
                book.setSource(rs.getString("source"));
                book.setNote(rs.getString("note"));
                book.setStatus(rs.getString("status"));

                // 加入 List
                bookList.add(book);
            }

        } catch (Exception e) {
            // 先直接印錯誤訊息
            e.printStackTrace();
        }

        // 回傳所有書籍清單
        return bookList;
    }

    // 根據 book_id 查詢單一本書
    public Book findByBookId(int bookId) {

        // SQL：查詢指定 book_id 的書籍
    	String sql = "SELECT * FROM books WHERE book_id = ?";

        try (
            // 建立資料庫連線
            Connection conn = DBConnection.getConnection();

            // 建立 PreparedStatement
            PreparedStatement pstmt = conn.prepareStatement(sql)
        ) {
            // 將 ? 替換成實際傳入的 bookId
            pstmt.setInt(1, bookId);

            // 執行查詢
            ResultSet rs = pstmt.executeQuery();

            // 如果查到資料，就建立 Book 物件回傳
            if (rs.next()) {
                Book book = new Book();

                book.setBookId(rs.getInt("book_id"));
                book.setTitle(rs.getString("title"));
                book.setPublisher(rs.getString("publisher"));
                book.setPublishYear(rs.getInt("publish_year"));
                book.setEdition(rs.getString("edition"));
                book.setFormat(rs.getString("format"));
                book.setSource(rs.getString("source"));
                book.setNote(rs.getString("note"));
                book.setStatus(rs.getString("status"));

                return book;
            }

        } catch (Exception e) {
            // 先直接印錯誤訊息
            e.printStackTrace();
        }

        // 查不到時回傳 null
        return null;
    }
    

    // 根據 bookId 更新書籍狀態
    public boolean updateBookStatus(int bookId, String status) {

        // SQL：更新指定書籍的狀態
        String sql = "UPDATE books SET status = ? WHERE book_id = ?";

        try (
            // 建立資料庫連線
            Connection conn = DBConnection.getConnection();

            // 建立 PreparedStatement
            PreparedStatement pstmt = conn.prepareStatement(sql)
        ) {
            // 設定 SQL 參數
            pstmt.setString(1, status);
            pstmt.setInt(2, bookId);

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
    
    
    // 新增一本書到 books 資料表
    public int insertBook(Book book, java.time.LocalDateTime createdAt) {

        // SQL：新增 books 資料
        String sql = "INSERT INTO books (title, publisher, publish_year, edition, format, source, note, status, created_at) "
                   + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (
            // 建立資料庫連線
            Connection conn = DBConnection.getConnection();

            // 建立 PreparedStatement，並要求回傳自動產生的主鍵 book_id
            PreparedStatement pstmt = conn.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)
        ) {
            // 設定 SQL 參數
            pstmt.setString(1, book.getTitle());
            pstmt.setString(2, book.getPublisher());
            pstmt.setInt(3, book.getPublishYear());
            pstmt.setString(4, book.getEdition());
            pstmt.setString(5, book.getFormat());
            pstmt.setString(6, book.getSource());
            pstmt.setString(7, book.getNote());
            pstmt.setString(8, book.getStatus());
            pstmt.setTimestamp(9, java.sql.Timestamp.valueOf(createdAt));

            // 執行新增
            int affectedRows = pstmt.executeUpdate();

            // 如果新增成功，就取得自動產生的 book_id
            if (affectedRows > 0) {
                ResultSet generatedKeys = pstmt.getGeneratedKeys();

                if (generatedKeys.next()) {
                    return generatedKeys.getInt(1);
                }
            }

        } catch (Exception e) {
            // 發生錯誤時印出訊息
            e.printStackTrace();
        }

        // 新增失敗時回傳 -1
        return -1;
    }
    
    
    // 根據關鍵字搜尋書籍
    // 目前先搜尋書名、出版社、版本、格式、資料來源、附註
    public List<Book> searchBooksByKeyword(String keyword) {
        // 建立 List，用來存放搜尋結果
        List<Book> bookList = new ArrayList<>();

        // SQL：使用 LIKE 做模糊搜尋
        String sql = "SELECT * FROM books "
                + "WHERE status <> 'REMOVED' AND ("
                + "title LIKE ? "
                + "OR publisher LIKE ? "
                + "OR edition LIKE ? "
                + "OR format LIKE ? "
                + "OR source LIKE ? "
                + "OR note LIKE ?)";

        try (
            // 建立資料庫連線
            Connection conn = DBConnection.getConnection();

            // 建立 PreparedStatement
            PreparedStatement pstmt = conn.prepareStatement(sql)
        ) {
            // 建立模糊搜尋字串，例如 %Java%
            String searchValue = "%" + keyword + "%";

            // 將六個 ? 都設成同一個搜尋值
            pstmt.setString(1, searchValue);
            pstmt.setString(2, searchValue);
            pstmt.setString(3, searchValue);
            pstmt.setString(4, searchValue);
            pstmt.setString(5, searchValue);
            pstmt.setString(6, searchValue);

            // 執行查詢
            ResultSet rs = pstmt.executeQuery();

            // 一筆一筆讀取查詢結果
            while (rs.next()) {
                Book book = new Book();

                book.setBookId(rs.getInt("book_id"));
                book.setTitle(rs.getString("title"));
                book.setPublisher(rs.getString("publisher"));
                book.setPublishYear(rs.getInt("publish_year"));
                book.setEdition(rs.getString("edition"));
                book.setFormat(rs.getString("format"));
                book.setSource(rs.getString("source"));
                book.setNote(rs.getString("note"));
                book.setStatus(rs.getString("status"));

                // 加入搜尋結果
                bookList.add(book);
            }

        } catch (Exception e) {
            // 發生錯誤時印出訊息
            e.printStackTrace();
        }

        // 回傳搜尋結果
        return bookList;
    }
    
 // 根據 bookId 更新書籍資料
    public boolean updateBook(Book book) {

        String sql = "UPDATE books SET title = ?, publisher = ?, publish_year = ?, edition = ?, format = ?, source = ?, note = ? "
                   + "WHERE book_id = ?";

        try (
            Connection conn = DBConnection.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql)
        ) {
            pstmt.setString(1, book.getTitle());
            pstmt.setString(2, book.getPublisher());
            pstmt.setInt(3, book.getPublishYear());
            pstmt.setString(4, book.getEdition());
            pstmt.setString(5, book.getFormat());
            pstmt.setString(6, book.getSource());
            pstmt.setString(7, book.getNote());
            pstmt.setInt(8, book.getBookId());

            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;

        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }
    
 // 下架書籍：將 status 改為 REMOVED
    public boolean removeBook(int bookId) {
        String sql = "UPDATE books SET status = 'REMOVED' WHERE book_id = ?";

        try (
            Connection conn = DBConnection.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql)
        ) {
            pstmt.setInt(1, bookId);

            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;

        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    public int countAllBooks() {
        String sql = "SELECT COUNT(*) AS c FROM books";
        try (Connection conn = DBConnection.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) return rs.getInt("c");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    public int countBorrowedBooks() {
        String sql = "SELECT COUNT(*) AS c FROM books WHERE status = 'BORROWED'";
        try (Connection conn = DBConnection.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) return rs.getInt("c");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

public List<PopularBookResponse> findPopularBooks(String sortBy, int limit) {
        List<PopularBookResponse> list = new ArrayList<>();
        String orderBy = "borrow_count DESC, avg_rating DESC";
        if ("rating".equalsIgnoreCase(sortBy)) {
            orderBy = "avg_rating DESC, review_count DESC, borrow_count DESC";
        }
        String sql = "SELECT b.book_id, b.title, COUNT(DISTINCT br.record_id) AS borrow_count, "
                + "COALESCE(AVG(r.rating), 0) AS avg_rating, COUNT(DISTINCT r.review_id) AS review_count "
                + "FROM books b "
                + "LEFT JOIN borrow_records br ON br.book_id = b.book_id "
                + "LEFT JOIN reviews r ON r.book_id = b.book_id "
                + "WHERE b.status <> 'REMOVED' "
                + "GROUP BY b.book_id, b.title "
                + "ORDER BY " + orderBy + " LIMIT ?";

        try (Connection conn = DBConnection.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, limit);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                list.add(new PopularBookResponse(
                        rs.getInt("book_id"),
                        rs.getString("title"),
                        rs.getInt("borrow_count"),
                        rs.getDouble("avg_rating"),
                        rs.getInt("review_count")
                ));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }
}
