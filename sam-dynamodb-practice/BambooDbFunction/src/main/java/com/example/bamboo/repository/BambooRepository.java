package com.example.bamboo.repository;

import com.example.bamboo.entity.Bamboo;

import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;

public class BambooRepository {
    private final DynamoDbTable<Bamboo> table;

    public BambooRepository(DynamoDbEnhancedClient enhancedClient) {
        // 환경 변수에서 테이블 이름 가져오기 (기본값 설정)
        String tableName = System.getenv("TABLE_NAME");
        if (tableName == null) {
            tableName = "BambooTable"; // 로컬 테스트용 안전장치
        }

        // 테이블 객체 매핑
        this.table = enhancedClient.table(tableName, TableSchema.fromBean(Bamboo.class));
    }

    public void save(Bamboo bamboo) {
        table.putItem(bamboo);
    }

    public Bamboo findById(String postId) {
        Key key = Key.builder()
            .partitionValue(postId)
            .build();

        return table.getItem(key);
    }

    public void deleteById(String postId) {
        Key key = Key.builder()
            .partitionValue(postId)
            .build();

        table.deleteItem(key);
    }
}