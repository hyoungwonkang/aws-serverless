package com.example.jwt;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;

public class JwtLambdaHandler implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {

    @Override
  public APIGatewayProxyResponseEvent handleRequest(APIGatewayProxyRequestEvent input, Context context) {
    // Authorizer가 넘겨준 User ID 추출
    String userId = "unknown";
    if (input.getRequestContext().getAuthorizer() != null) {
      userId = (String) input.getRequestContext().getAuthorizer().get("principalId");
    }

    // 응답 결과
    String body = String.format("{\"사용자 ID\": \"%s\"}", userId);
    return new APIGatewayProxyResponseEvent()
        .withStatusCode(200)
        .withBody(body);
  }
}
