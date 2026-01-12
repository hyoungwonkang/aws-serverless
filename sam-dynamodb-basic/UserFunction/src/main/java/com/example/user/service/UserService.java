package com.example.user.service;

import java.util.UUID;

import com.example.user.entity.User;
import com.example.user.repository.UserRepository;

public class UserService {
    private final UserRepository repository;

    // 생성자 주입
    public UserService(UserRepository repository) {
        this.repository = repository;
    }

    // 사용자 등록
    public User createUser(User user) {
        // 간단한 예외 로직
        if (user.getAge() < 0) {
            throw new IllegalArgumentException("Age cannot be negative");
        }
        // PK는 UUID로 생성
        String userId = UUID.randomUUID().toString();
        user.setUserId(userId);

        repository.save(user);

        return user;
    }

    // 사용자 조회
    public User getUser(String userId) {
        User foundUser = repository.findById(userId);
        if (foundUser == null) {
            throw new RuntimeException("User not found. User ID: " + userId);
        }
        
        return foundUser;
    }
}
