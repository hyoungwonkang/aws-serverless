package com.example.lounge;

import java.util.Map;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;

public class LoungeHandler implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent>{

    @Override
    public APIGatewayProxyResponseEvent handleRequest(APIGatewayProxyRequestEvent input, Context context) {
        
        // Authorizer에서 생성한 IAM Policy 문서 꺼내기
        Map<String, Object> policy =  input.getRequestContext().getAuthorizer();
        
        // principalId 꺼내기 (사용자 확인)
        String user = "Unknown";
        if (policy != null) {
            user = policy.get("principalId").toString();
        }

        return new APIGatewayProxyResponseEvent()
            .withStatusCode(200)
            .withBody(user + "님! 라운지에 오신걸 환영합니다.");
    }
}
