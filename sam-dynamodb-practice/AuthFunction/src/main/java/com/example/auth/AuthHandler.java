package com.example.auth;

import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayCustomAuthorizerEvent;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

public class AuthHandler implements RequestHandler<APIGatewayCustomAuthorizerEvent, Map<String, Object>> {

    // template.yaml 에서 가져오는 환경 변수
    private static final String SECRET_KEY = System.getenv("SECRET_KEY");

    @Override
    public Map<String, Object> handleRequest(APIGatewayCustomAuthorizerEvent input, Context context) {
        // JWT
        String token = input.getAuthorizationToken();
        if (token == null || !token.startsWith("Bearer ")) {
            throw new RuntimeException("401 Unauthorized. No Token");
        }

        // 허용할 경로 (마지막 슬래시 유무, 쿼리 스트링 유무 등으로 주소를 다르게 인식하는 경우 대비)
        String methodArn = input.getMethodArn();        // arn:aws:..//Prod/posts
        methodArn = methodArn.split("/", 2)[0] + "/*";  // arn:aws:.../Prod/*

        // "Bearer "제거
        token = token.substring(7);

        try {
            // JWT 검증 & 파싱 (잘못된 JWT는 여기서 오류 발생)
            Claims claims = Jwts.parserBuilder()
                .setSigningKey(Keys.hmacShaKeyFor(SECRET_KEY.getBytes(StandardCharsets.UTF_8)))
                .build()
                .parseClaimsJws(token)
                .getBody();
            String userId = claims.getSubject();

            // 유효한 JWT는 Allow IAM Policy 반환
            return generateIAMPolicy(userId, "Allow", methodArn);
        } catch (Exception e) {
            context.getLogger().log("Token Validation Failed: " + e.getMessage());
            throw new RuntimeException("401 Unauthorized. Invaild Token");
        }
    }

    // IAM Policy JSON 생성
    private Map<String, Object> generateIAMPolicy(String principalId, String effect, String resource) {
        // principalId
        Map<String, Object> authResponse = new HashMap<>();
        authResponse.put("principalId", principalId);
        
        // policyDocument (IAM Policy)
        Map<String, Object> policyDocument = new HashMap<>();
        policyDocument.put("Version", "2012-10-17");
        Map<String, Object> statement = new HashMap<>();
        statement.put("Action", "execute-api:Invoke");
        statement.put("Effect", effect);
        statement.put("Resource", resource);
        policyDocument.put("Statement", Collections.singletonList(statement));
        authResponse.put("policyDocument", policyDocument);

        // context: 비즈니스 로직에 다른 정보를 넘기고 싶다면
        Map<String, Object> context = new HashMap<>();
        context.put("userId", principalId);
        authResponse.put("context", context);

        return authResponse;
    }
}
