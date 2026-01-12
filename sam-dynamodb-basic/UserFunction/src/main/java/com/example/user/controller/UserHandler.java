package com.example.user.controller;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.example.user.entity.User;
import com.example.user.repository.UserRepository;
import com.example.user.service.UserService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import software.amazon.awssdk.core.exception.SdkClientException;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.http.urlconnection.UrlConnectionHttpClient;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.DynamoDbException;

/**
 * Lambda Handler
 * - Controller 역할 수행
 * - HTTP 요청 파싱 -> Service 호출 -> 응답
 */
public class UserHandler implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {

    private final UserService userService;
    private static final ObjectMapper objectMapper = new ObjectMapper();

    // 생성자에서 의존성 직접 조립 (@Autowired 역할 대신)
    public UserHandler() {
        // DB 클라이언트 생성
        DynamoDbClient ddb = DynamoDbClient.builder()
            .httpClient(UrlConnectionHttpClient.create()) // Cold Start 완화
            .region(Region.AP_NORTHEAST_2)
            .build();
        DynamoDbEnhancedClient enhancedClient = DynamoDbEnhancedClient.builder()
        .dynamoDbClient(ddb)
        .build();
        
        // 레포지토리 생성
        UserRepository repository = new UserRepository(enhancedClient);

        // 서비스 생성
        this.userService = new UserService(repository);
    }

    @Override
    public APIGatewayProxyResponseEvent handleRequest(APIGatewayProxyRequestEvent input, Context context) {
        try {
            // 요청 메서드
            String method = input.getHttpMethod();

            // GET 또는 POST로 요청 구분
            switch (method) {
                case "POST":
                    return createUser(input);
                case "GET":
                    return getUser(input, context);
            }

            // GET/POST 외는 예외 처리
            return response(405, "Method Not Allowed");
            
            // 503 Service Unavailable
            // 500 Internal Server Error
            // 404 Not Found
            // 400 Bad Request

        } catch (SdkClientException e) {  // 타임아웃/네트워크 오류
            return response(503, "Service Timeout or Unavailable");
        } catch (DynamoDbException e) {  // DynamoDB 오류
            return response(500, "Database Error");
        } catch (IllegalArgumentException e) {  // 비즈니스 로직 오류
            return response(400, "Error: " + e.getMessage());
        } catch (RuntimeException e) {  // 비즈니스 로직 오류
            return response(404, "Error: " + e.getMessage());
        } catch (Exception e) {  // 기타 오류
            return response(500, "Internal Server Error");
        }
    }

    // 사용자 등록
    private APIGatewayProxyResponseEvent createUser(APIGatewayProxyRequestEvent input) {
        try {
            // Body 파싱
            String body = input.getBody();
            if (body == null || body.isEmpty()) {
                return response(400, "RequestBody is empty");
            }
            
            // JSON to User
            User user = objectMapper.readValue(body, User.class);

            // 서비스 호출
            User newUser = userService.createUser(user);

            // 성공 응답
            return response(201, objectMapper.writeValueAsString(newUser));
        } catch (JsonProcessingException e) {
            // JSON 파싱 예외 (400 Bad Request)
            return response(400, "Invalid JSON format");
        }
    }

    // 사용자 조회
    private APIGatewayProxyResponseEvent getUser(APIGatewayProxyRequestEvent input, Context context) {
    try {
        // 조회할 userId
        String userId = input.getPathParameters().get("userId");
        
        // userId 체크
        if (userId == null || userId.isEmpty()) {
            throw new IllegalArgumentException("Missing User ID");
        }

        // 서비스 호출
        User foundUser = userService.getUser(userId);

        // 응답
        return response(200, objectMapper.writeValueAsString(foundUser));
        
        } catch (JsonProcessingException e) {
            // DB에서 가져온 데이터를 JSON으로 바꾸다가 실패 (500 Internal Server Error)
            return response(500, "Error processing response");
        }
    }

    // 공통 코드: 응답
    private APIGatewayProxyResponseEvent response(Integer status, String body) {
        return new APIGatewayProxyResponseEvent()
            .withStatusCode(status)
            .withBody(body);
    }
}
