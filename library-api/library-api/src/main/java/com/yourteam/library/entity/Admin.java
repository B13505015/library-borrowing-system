package com.yourteam.library.entity;

public class Admin {

    // 管理員主鍵 ID（Primary Key）
    private int adminId;

    // 管理員登入帳號
    private String username;

    // 管理員登入密碼
    private String password;

    // 管理員姓名
    private String name;

    // 管理員帳號狀態，例如 ACTIVE、DISABLED
    private String status;

    // 無參數建構子（No-args constructor）
    public Admin() {
    }

    // 全欄位建構子（All-args constructor）
    public Admin(int adminId, String username, String password, String name, String status) {
        this.adminId = adminId;
        this.username = username;
        this.password = password;
        this.name = name;
        this.status = status;
    }

    // Getter / Setter methods

    public int getAdminId() {
        return adminId;
    }

    public void setAdminId(int adminId) {
        this.adminId = adminId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}