package com.yourteam.library;

import com.yourteam.library.importer.BookJsonImporter;

public class TestBookJsonImporter {

    public static void main(String[] args) {
        // 建立 BookJsonImporter 物件
        BookJsonImporter importer = new BookJsonImporter();

        // 正式匯入 Books.json 到資料庫
        importer.importBooksJson();
    }
}