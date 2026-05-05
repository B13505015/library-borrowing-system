package com.yourteam.library.importer;

public class DataImportRunner {
    public static void main(String[] args) {
        UserJsonImporter userImporter = new UserJsonImporter();
        BookJsonImporter bookImporter = new BookJsonImporter();
        BorrowRecordJsonImporter borrowRecordImporter = new BorrowRecordJsonImporter();

        System.out.println("開始匯入 Users.json ...");
        userImporter.importUsersJson();

        System.out.println("開始匯入 Books.json ...");
        bookImporter.importBooksJson();

        System.out.println("開始匯入 Borrow_records.json ...");
        borrowRecordImporter.importBorrowRecordsJson();

        System.out.println("全部資料匯入完成。");
    }
}
