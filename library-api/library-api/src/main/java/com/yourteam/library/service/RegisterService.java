package com.yourteam.library.service;

import java.time.LocalDateTime;

import com.yourteam.library.entity.User;
import com.yourteam.library.repository.UserRepository;

public class RegisterService {

    private UserRepository userRepository;

    public RegisterService() {
        this.userRepository = new UserRepository();
    }

    // 回傳值：
    // STUDENT_EXISTS -> 學號已存在
    // REGISTER_SUCCESS -> 註冊成功
    // REGISTER_FAILED -> 註冊失敗
    public String registerUser(String studentNo, String name, String password, String level) {

        User existingUser = userRepository.findByStudentNo(studentNo);
        if (existingUser != null) {
            return "STUDENT_EXISTS";
        }

        User user = new User();
        user.setStudentNo(studentNo);
        user.setName(name);
        user.setPassword(password);
        
        if (!"VIP".equalsIgnoreCase(level)) {
            level = "NORMAL";
        }
        user.setRoleLevel(level.toUpperCase());
        user.setStatus("ACTIVE");

        boolean success = userRepository.insertUser(user, LocalDateTime.now());

        if (success) {
            return "REGISTER_SUCCESS";
        }

        return "REGISTER_FAILED";
    }
}