package com.yourteam.library;

import com.yourteam.library.service.AuthService;

public class TestAuthService {

    public static void main(String[] args) {
        // 建立 AuthService 物件
        AuthService authService = new AuthService();

        // 測試一般使用者登入
        String userResult = authService.loginUser("B12345678", "1234");
        System.out.println("一般使用者登入結果: " + userResult);

        // 測試管理員登入
        String adminResult = authService.loginAdmin("admin", "0000");
        System.out.println("管理員登入結果: " + adminResult);
    }
}