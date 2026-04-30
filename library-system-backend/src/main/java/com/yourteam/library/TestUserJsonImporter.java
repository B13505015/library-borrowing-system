package com.yourteam.library;

import com.yourteam.library.importer.UserJsonImporter;

public class TestUserJsonImporter {

    public static void main(String[] args) {
        // 建立 UserJsonImporter 物件
        UserJsonImporter importer = new UserJsonImporter();

        // 正式匯入 Users.json 到 users 資料表
        importer.importUsersJson();
    }
}