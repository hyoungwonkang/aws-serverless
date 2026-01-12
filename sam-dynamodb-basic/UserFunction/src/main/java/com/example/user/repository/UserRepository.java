package com.example.user.repository;


import com.example.user.entity.User;

import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;

public class UserRepository {
    
    // 테이블과 매핑할 객체
    private final DynamoDbTable<User> userTable;

    // 생성 시점에 테이블 매핑 완료
    public UserRepository(DynamoDbEnhancedClient enhancedClient) {
        String tableName = System.getenv("TABLE_NAME"); // 테이블 일므은 환경 변수에서 가져오기
        userTable = enhancedClient.table(tableName, TableSchema.fromBean(User.class));
    }

    // 저장
    public void save(User user) {
        userTable.putItem(user);
    }

    // 단 건 조회
    public User findById(String userId) {
        Key key = Key.builder()
                .partitionValue(userId)
                .build();
        User foundUser = userTable.getItem(key);
        // User foundUser = userTable.getItem(r -> r.key(k -> k.partitionValue(userId)));
        return foundUser;
    }
}
