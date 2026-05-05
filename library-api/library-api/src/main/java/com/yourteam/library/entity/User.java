package com.yourteam.library.entity;

public class User {

    // 使用者主鍵 ID（Primary Key）
    private int userId;

    // 學號 / 使用者登入帳號
    private String studentNo;

    // 使用者姓名
    private String name;

    // 使用者密碼
    private String password;

    // 使用者等級，例如 NORMAL、VIP
    private String roleLevel;

    // 帳號狀態，例如 ACTIVE、SUSPENDED
    private String status;

    // 無參數建構子（No-args constructor）
    public User() {
    }

    // 全欄位建構子（All-args constructor）
    public User(int userId, String studentNo, String name, String password, String roleLevel, String status) {
        this.userId = userId;
        this.studentNo = studentNo;
        this.name = name;
        this.password = password;
        this.roleLevel = roleLevel;
        this.status = status;
    }

    // Getter / Setter methods

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getStudentNo() {
        return studentNo;
    }

    public void setStudentNo(String studentNo) {
        this.studentNo = studentNo;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRoleLevel() {
        return roleLevel;
    }

    public void setRoleLevel(String roleLevel) {
        this.roleLevel = roleLevel;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}