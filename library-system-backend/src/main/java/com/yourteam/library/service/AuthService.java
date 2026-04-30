package com.yourteam.library.service;

import com.yourteam.library.entity.Admin;
import com.yourteam.library.entity.User;
import com.yourteam.library.repository.AdminRepository;
import com.yourteam.library.repository.UserRepository;

public class AuthService {

    // 建立 repository 物件，負責去資料庫查資料
    private UserRepository userRepository;
    private AdminRepository adminRepository;

    // 建構子（constructor）
    public AuthService() {
        this.userRepository = new UserRepository();
        this.adminRepository = new AdminRepository();
    }

    // 一般使用者登入驗證
    // 回傳值說明：
    // "LOGIN_SUCCESS"         -> 登入成功
    // "USER_NOT_FOUND"        -> 找不到帳號
    // "PASSWORD_INCORRECT"    -> 密碼錯誤
    // "USER_SUSPENDED"        -> 帳號被停權
    public String loginUser(String studentNo, String password) {

        // 先依學號查詢使用者
        User user = userRepository.findByStudentNo(studentNo);

        // 如果查不到資料，代表帳號不存在
        if (user == null) {
            return "USER_NOT_FOUND";
        }

        // 如果密碼不正確，回傳密碼錯誤
        if (!user.getPassword().equals(password)) {
            return "PASSWORD_INCORRECT";
        }

        // 如果帳號狀態是 SUSPENDED，表示被停權
        if ("SUSPENDED".equalsIgnoreCase(user.getStatus())) {
            return "USER_SUSPENDED";
        }

        // 都通過就登入成功
        return "LOGIN_SUCCESS";
    }

    // 管理員登入驗證
    // 回傳值說明：
    // "LOGIN_SUCCESS"         -> 登入成功
    // "ADMIN_NOT_FOUND"       -> 找不到管理員帳號
    // "PASSWORD_INCORRECT"    -> 密碼錯誤
    // "ADMIN_DISABLED"        -> 帳號被停用
    public String loginAdmin(String username, String password) {

        // 先依帳號查詢管理員
        Admin admin = adminRepository.findByUsername(username);

        // 如果查不到資料，代表帳號不存在
        if (admin == null) {
            return "ADMIN_NOT_FOUND";
        }

        // 如果密碼不正確，回傳密碼錯誤
        if (!admin.getPassword().equals(password)) {
            return "PASSWORD_INCORRECT";
        }

        // 如果管理員狀態是 DISABLED，表示不能登入
        if ("DISABLED".equalsIgnoreCase(admin.getStatus())) {
            return "ADMIN_DISABLED";
        }

        // 都通過就登入成功
        return "LOGIN_SUCCESS";
    }
}