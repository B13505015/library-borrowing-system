package com.yourteam.library.importer;

import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.yourteam.library.entity.Book;
import com.yourteam.library.repository.BookAuthorRepository;
import com.yourteam.library.repository.BookIsbnRepository;
import com.yourteam.library.repository.BookRepository;
import com.yourteam.library.repository.BookSubjectRepository;

public class BookJsonImporter {

    // 建立 repository 物件，負責把資料寫進資料庫
    private BookRepository bookRepository;
    private BookAuthorRepository bookAuthorRepository;
    private BookSubjectRepository bookSubjectRepository;
    private BookIsbnRepository bookIsbnRepository;

    // 建構子（constructor）
    public BookJsonImporter() {
        this.bookRepository = new BookRepository();
        this.bookAuthorRepository = new BookAuthorRepository();
        this.bookSubjectRepository = new BookSubjectRepository();
        this.bookIsbnRepository = new BookIsbnRepository();
    }

    // 匯入 Books.json 到 books / book_authors / book_subjects / book_isbns
    public void importBooksJson() {
        try {
            // 建立 Jackson 的 ObjectMapper，用來解析 JSON
            ObjectMapper objectMapper = new ObjectMapper();

            // 從 resources 資料夾讀取 Books.json
            InputStream inputStream = getClass().getClassLoader().getResourceAsStream("Books.json");

            // 如果找不到檔案，直接丟出例外
            if (inputStream == null) {
                throw new RuntimeException("找不到 Books.json，請確認檔案是否放在 src/main/resources");
            }

            // 將 JSON 解析成 List<Map<String, Object>>
            List<Map<String, Object>> bookList = objectMapper.readValue(
                inputStream,
                new TypeReference<List<Map<String, Object>>>() {}
            );

            // 計數器：統計成功匯入幾本書
            int successCount = 0;

            // 一筆一筆處理 JSON 資料
            for (Map<String, Object> bookMap : bookList) {
                // 建立 Book 物件
                Book book = new Book();

                // 讀取主表欄位
                book.setTitle((String) bookMap.get("題名"));
                book.setPublisher((String) bookMap.get("出版者"));
                book.setEdition((String) bookMap.get("版本"));
                book.setFormat((String) bookMap.get("格式"));
                book.setSource((String) bookMap.get("資料來源"));
                book.setNote((String) bookMap.get("附註"));

                // 新匯入的書預設為 AVAILABLE
                book.setStatus("AVAILABLE");

                // 出版年可能會被解析成 Integer，也可能是其他 Number 類型
                Object publishYearObj = bookMap.get("出版年");
                if (publishYearObj instanceof Number) {
                    book.setPublishYear(((Number) publishYearObj).intValue());
                } else {
                    // 如果不是數字，先給 0，避免程式壞掉
                    book.setPublishYear(0);
                }

                // 建立時間先用現在時間
                LocalDateTime now = LocalDateTime.now();

                // 先把主表資料寫進 books，並取得新產生的 book_id
                int bookId = bookRepository.insertBook(book, now);

                // 如果主表新增失敗，就跳過這筆
                if (bookId == -1) {
                    continue;
                }

                // =========================
                // 匯入作者（作者是陣列）
                // =========================
                Object authorsObj = bookMap.get("作者");
                if (authorsObj instanceof List<?>) {
                    List<?> authors = (List<?>) authorsObj;

                    for (int i = 0; i < authors.size(); i++) {
                        Object authorObj = authors.get(i);

                        if (authorObj != null) {
                            String authorName = authorObj.toString();
                            // 作者順序從 1 開始
                            bookAuthorRepository.insertAuthor(bookId, authorName, i + 1);
                        }
                    }
                }

                // =========================
                // 匯入主題（主題是陣列）
                // =========================
                Object subjectsObj = bookMap.get("主題");
                if (subjectsObj instanceof List<?>) {
                    List<?> subjects = (List<?>) subjectsObj;

                    for (Object subjectObj : subjects) {
                        if (subjectObj != null) {
                            String subjectName = subjectObj.toString();
                            bookSubjectRepository.insertSubject(bookId, subjectName);
                        }
                    }
                }

                // =========================
                // 匯入 ISBN / 識別號（也是陣列）
                // =========================
                Object isbnsObj = bookMap.get("識別號");
                if (isbnsObj instanceof List<?>) {
                    List<?> isbns = (List<?>) isbnsObj;

                    for (Object isbnObj : isbns) {
                        if (isbnObj != null) {
                            String isbn = isbnObj.toString();
                            bookIsbnRepository.insertIsbn(bookId, isbn);
                        }
                    }
                }

                // 成功處理一整本書
                successCount++;
            }

            // 印出匯入結果
            System.out.println("Books.json 匯入完成");
            System.out.println("成功匯入書籍筆數: " + successCount);

        } catch (Exception e) {
            // 發生錯誤時印出錯誤訊息
            e.printStackTrace();
        }
    }
}