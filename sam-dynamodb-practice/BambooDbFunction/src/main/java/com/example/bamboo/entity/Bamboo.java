package com.example.bamboo.entity;

import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey;

@DynamoDbBean
public class Bamboo {
    private String postId;
    private String writerId;
    private String title;
    private String content;
    private String createdAt;

    public Bamboo() {}  // 기본 생성자
    public Bamboo(String postId, String writerId, String title, String content, String createdAt) {
        this.postId = postId;
        this.writerId = writerId;
        this.title = title;
        this.content = content;
        this.createdAt = createdAt;
    }

    // PK
    @DynamoDbPartitionKey
    public String getPostId() {
        return postId;
    };
    public void setPostId(String postId) {
        this.postId = postId;
    }
    // 그 외 Getter/Setter
    public String getWriterId() {
        return writerId;
    }
    public void setWriterId(String writerId) {
        this.writerId = writerId;
    }
    public String getTitle() {
        return title;
    }
    public void setTitle(String title) {
        this.title = title;
    }
    public String getContent() {
        return content;
    }
    public void setContent(String content) {
        this.content = content;
    }
    public String getCreatedAt() {
        return createdAt;
    }
    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }
}
