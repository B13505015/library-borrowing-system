package com.yourteam.library.service;

import java.util.List;

import com.yourteam.library.entity.User;
import com.yourteam.library.repository.UserRepository;

public class UserService {

    private UserRepository userRepository;

    public UserService() {
        this.userRepository = new UserRepository();
    }

    public List<User> getAllUsers() {
        return userRepository.findAllUsers();
    }

    public List<User> searchUsers(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return userRepository.findAllUsers();
        }
        return userRepository.searchUsersByKeyword(keyword);
    }

    public boolean suspendUser(String studentNo) {
        User user = userRepository.findByStudentNo(studentNo);
        if (user == null) {
            return false;
        }
        return userRepository.updateUserStatus(user.getUserId(), "SUSPENDED");
    }

    public boolean activateUser(String studentNo) {
        User user = userRepository.findByStudentNo(studentNo);
        if (user == null) {
            return false;
        }
        return userRepository.updateUserStatus(user.getUserId(), "ACTIVE");
    }
}