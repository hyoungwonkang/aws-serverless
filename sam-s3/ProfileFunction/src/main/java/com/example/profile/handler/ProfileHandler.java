package com.example.profile.handler;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.example.profile.model.ProfileRequest;
import com.fasterxml.jackson.databind.ObjectMapper;

import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.PresignedPutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest;

public class ProfileHandler implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {
    
    private final ObjectMapper objectMapper = new ObjectMapper();

    private final Region SEOUL_REGION = Region.AP_NORTHEAST_2;
    private final String BUCKET_NAME = System.getenv("BUCKET_NAME");

    private final S3Presigner presigner = S3Presigner.builder()
        .region(SEOUL_REGION)
        .build();

    @Override
    public APIGatewayProxyResponseEvent handleRequest(APIGatewayProxyRequestEvent input, Context context) {

        // response는 try 블록, catch 블록 모두 사용
        APIGatewayProxyResponseEvent response = new APIGatewayProxyResponseEvent();
        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "application/json");
        response.setHeaders(headers);
        
        try {
            // 요청 바디 가져옴
            // input.getBody()가 null이면 Jackson은 예외를 던질 수 있으므로 주의해야 합니다.
            String body = input.getBody();
            if (body == null || body.isEmpty()) {
                response.setStatusCode(400);
                response.setBody("{\"message\": \"Empty request body\"}");
                return response;
            }

            // 요청 바디 파싱 (String JSON -> Java Object)
            ProfileRequest requestBody = objectMapper.readValue(body, ProfileRequest.class);
            if (requestBody.userId() == null || requestBody.filename() == null) {
                response.setStatusCode(400);
                response.setBody("{\"message\": \"userId and filename are required\"}");
                return response;
            }

            // S3 Key 생성 (폴더처럼 구조화)
            String objectKey = requestBody.userId() + "/" + requestBody.filename();

            // Presigned URL 요청 생성
            PutObjectRequest objectRequest = PutObjectRequest.builder()
                .bucket(BUCKET_NAME)
                .key(objectKey)
                .contentType("image/jpeg")  // 화면이 없는 관계로 실습 편의 상 JPG 이미지로 고정
                .build();

            PutObjectPresignRequest presignRequest = PutObjectPresignRequest.builder()
                .signatureDuration(Duration.ofMinutes(10))   // 10분간 유효
                .putObjectRequest(objectRequest)
                .build();

            // URL 발급
            PresignedPutObjectRequest presignedRequest = presigner.presignPutObject(presignRequest);
            String uploadUrl = presignedRequest.url().toString();

            // 응답 생성
            Map<String, String> responseBody = new HashMap<>();
            responseBody.put("uploadUrl", uploadUrl);
            responseBody.put("objectKey", objectKey);
            response.setStatusCode(200);

            // 응답
            response.setBody(objectMapper.writeValueAsString(responseBody));
            
        } catch (Exception e) {
            context.getLogger().log("Error: " + e.getMessage());
            // Jackson 파싱 에러나 AWS SDK 에러 모두 여기서 잡힙니다.
            response.setStatusCode(500);
            response.setBody("{\"message\": \"Internal Server Error: " + e.getMessage() + "\"}");
        }
        return response;
    }
}
