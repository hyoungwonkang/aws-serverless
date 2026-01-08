package com.example.order;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayCustomAuthorizerEvent;

public class SecurityHandler implements RequestHandler<APIGatewayCustomAuthorizerEvent, Map<String, Object>> {

    @Override
    public Map<String, Object> handleRequest(APIGatewayCustomAuthorizerEvent input, Context context) {
        String token = input.getAuthorizationToken();
        String methodArn = input.getMethodArn();
        context.getLogger().log("Authorization Header: " + token);

        if("my-secret-key".equals(token)) {
            return generateIAMPolicy("user-001", "Allow", methodArn);
        } else {
            return generateIAMPolicy("user-001", "Deny", methodArn);
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