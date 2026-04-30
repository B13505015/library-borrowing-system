package com.yourteam.library;

import com.yourteam.library.entity.User;
import com.yourteam.library.repository.UserRepository;

public class TestUserRepository {

    public static void main(String[] args) {
        // 建立 UserRepository 物件
        UserRepository userRepository = new UserRepository();

        // 用學號查詢使用者
        User user = userRepository.findByStudentNo("B12345678");

        // 判斷有沒有查到資料
        if (user != null) {
            System.out.println("查詢成功");
            System.out.println("userId: " + user.getUserId());
            System.out.println("studentNo: " + user.getStudentNo());
            System.out.println("name: " + user.getName());
            System.out.println("password: " + user.getPassword());
            System.out.println("roleLevel: " + user.getRoleLevel());
            System.out.println("status: " + user.getStatus());
        } else {
            System.out.println("查無資料");
        }
    }
}