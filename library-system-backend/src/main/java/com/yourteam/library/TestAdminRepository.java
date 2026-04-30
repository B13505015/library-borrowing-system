package com.yourteam.library;

import com.yourteam.library.entity.Admin;
import com.yourteam.library.repository.AdminRepository;

public class TestAdminRepository {

    public static void main(String[] args) {
        // 建立 AdminRepository 物件
        AdminRepository adminRepository = new AdminRepository();

        // 用管理員帳號查詢管理員
        Admin admin = adminRepository.findByUsername("admin");

        // 判斷有沒有查到資料
        if (admin != null) {
            System.out.println("查詢成功");
            System.out.println("adminId: " + admin.getAdminId());
            System.out.println("username: " + admin.getUsername());
            System.out.println("password: " + admin.getPassword());
            System.out.println("name: " + admin.getName());
            System.out.println("status: " + admin.getStatus());
        } else {
            System.out.println("查無資料");
        }
    }
}