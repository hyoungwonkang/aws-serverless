package com.example.jwt;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayCustomAuthorizerEvent;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

public class JwtAuthorizerHandler  implements RequestHandler<APIGatewayCustomAuthorizerEvent, Map<String, Object>>  {

    private static final String SECRET_KEY = "this-is-my-super-secret-key-for-jwt-example";

    @Override
    public Map<String, Object> handleRequest(APIGatewayCustomAuthorizerEvent input, Context context) {
        
        String token = input.getAuthorizationToken();
        String methodArn = input.getMethodArn();
        
        // 1. "Bearer " 접두사 제거
        if (token != null && token.startsWith("Bearer")) {
            token = token.substring(7);
        }
        try {
            // 2. 평문 문자열을 그대로 바이트 배열로 변환하여 키 생성
            Key key = Keys.hmacShaKeyFor(SECRET_KEY.getBytes(StandardCharsets.UTF_8));

            // 3. 파싱 및 검증 (여기서 서명이 틀리면 예외 발생)
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();

            String userId = claims.getSubject();  // 토큰에 담긴 ID 추출
            context.getLogger().log("Verified User: " + userId);

            return generateIamPolicy(userId, "Allow", methodArn);
        } catch (Exception e) {
            context.getLogger().log("Token verification failed: " + e.getMessage());
            return generateIamPolicy("anonymous", "Deny", methodArn);
        }
    }

    // IAM Policy JSON 생성
    private Map<String, Object> generateIamPolicy(String principalId, String effect, String resource) {
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

        return authResponse;
    }
}
