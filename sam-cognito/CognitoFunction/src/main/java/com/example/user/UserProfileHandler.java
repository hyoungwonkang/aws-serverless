package com.example.user;

import java.util.Map;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;

public class UserProfileHandler implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {

    @Override
    public APIGatewayProxyResponseEvent handleRequest(APIGatewayProxyRequestEvent input, Context context) {
        
        // api gateway의 authorizer가 넘겨준 사용자 정보 가져오기
        Map<String, Object> authorizer = input.getRequestContext().getAuthorizer();
        Map<String, Object> claims = (Map<String, Object>) authorizer.get("claims");
       
        // claims 안 정보
        String email = (String) claims.get("email");
        String sub = (String) claims.get("sub");
        String username = (String) claims.get("cognito:username");

        // 로그
        context.getLogger().log("User Email: " + email);
        
        // 응답 생성
        String outputBody = String.format("{\"message\": \"Hello, %s! AWS validated your token.\", \"email\": \"%s\", \"sub\": \"%s\"}", username, email, sub);
        return new APIGatewayProxyResponseEvent()
            .withStatusCode(200)
            .withHeaders(
                Map.of(
                    "Content-Type", "application/json",
                    "Access-Control-Allow-Origin", "*" // CORS 헤더 (필수)
                )
            )
            .withBody(outputBody);
    }
}
