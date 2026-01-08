package com.example.lounge;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayCustomAuthorizerEvent;

public class LoungeAuthorizerHandler implements RequestHandler<APIGatewayCustomAuthorizerEvent, Map<String, Object>> {

    @Override
    public Map<String, Object> handleRequest(APIGatewayCustomAuthorizerEvent input, Context context) {
        // 헤더에 포함된 토큰 꺼내기
        String token = input.getAuthorizationToken();
        String methodArn = input.getMethodArn();
        context.getLogger().log("Authorization Header: " + token);

        // 검증 로직 (GOLD-MEMBER 통과)
        if ("GOLD-MEMBER".equals(token)) {
            // Allow
            // "gold-member"는 principalId가 들어가는 자리. 현재는 없으니 임의값넣음. JWT 토큰에서 subject 추출함.
            return generateIAMPolicy("gold-member", "Allow", methodArn); 
        } else {
            // Deny
            return generateIAMPolicy("gold-member", "Deny", methodArn);
        }
    }

    // IAM Policy 문서를 만들어 반환하는 메서드
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

    return authResponse;
  }
    

}
