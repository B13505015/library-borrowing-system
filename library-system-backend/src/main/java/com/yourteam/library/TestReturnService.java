package com.yourteam.library;

import com.yourteam.library.service.ReturnService;

public class TestReturnService {

    public static void main(String[] args) {
        // 建立 ReturnService 物件
        ReturnService returnService = new ReturnService();

        // 測試還書
        // 這裡先假設 record_id = 1
        String result = returnService.returnBook(4);

        // 印出還書結果
        System.out.println("還書結果: " + result);
    }
}