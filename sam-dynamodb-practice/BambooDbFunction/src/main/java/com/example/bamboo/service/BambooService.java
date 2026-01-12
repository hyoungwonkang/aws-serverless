package com.example.bamboo.service;

import java.time.LocalDateTime;
import java.util.UUID;

import com.example.bamboo.entity.Bamboo;
import com.example.bamboo.repository.BambooRepository;

public class BambooService {
    
    private final BambooRepository repository;

    public BambooService(BambooRepository repository) {
        this.repository = repository;
    }

    public Bamboo createPost(Bamboo post, String writerId) {
        post.setPostId(UUID.randomUUID().toString());
        post.setWriterId(writerId);
        post.setCreatedAt(LocalDateTime.now().toString());
        
        repository.save(post);
        return post;
    }

    public Bamboo getPost(String postId) {
        Bamboo post = repository.findById(postId);
        if (post == null) {
            throw new IllegalArgumentException("Post not found");
        }
        return post;
    }

    public void updatePost(String postId, String userId, String newTitle, String newContent) {
        Bamboo post = getPost(postId);

        validateOwner(post, userId);
    
        post.setTitle(newTitle);
        post.setContent(newContent);

        repository.save(post);
    }

    public void deletePost(String postId, String userId) {
        Bamboo post = getPost(postId);

        validateOwner(post, userId);

        repository.deleteById(postId);
    }

    // [Helper] 작성자 검증 로직
    private void validateOwner(Bamboo post, String userId) {
        if (post.getWriterId() == null || !post.getWriterId().equals(userId)) {
        throw new SecurityException("Forbidden: You are not the owner.");
        }
    }
}
