package com.example.api;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.fasterxml.jackson.databind.ObjectMapper;

public class LocationHandler implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {

    @Override
    public APIGatewayProxyResponseEvent handleRequest(APIGatewayProxyRequestEvent input, Context context) {
        // 1. 로거 설정
        LambdaLogger logger = context.getLogger();
        logger.log("----http 요청 도착----");

        // 2. Query String 파싱 (예: ?name=Jimin)
        // input.getQueryStringParameters()는 쿼리스트링이 없으면 null일 수 있으므로 주의
        Map<String, String> queryParams = input.getQueryStringParameters();
        String name = "Kang";
        if (queryParams != null && queryParams.containsKey("name")) {
            name = queryParams.get("name");
        }
        String city = "Seoul"; 
        if (queryParams != null && queryParams.containsKey("city")) {
            city = queryParams.get("city");
        }

        // Http 헤더는 대소문자를 구분하지 않는 경우가 많지만, Lambda 이벤트에서는 넘어온 그대로 확인
        Map<String, String> headers = input.getHeaders();
        if (headers != null) {
            if (headers.containsKey("name")) {
                name = headers.get("name");
            }
            if (headers.containsKey("city")) {
                city = headers.get("city");
            }
        }

        String nation = System.getenv().getOrDefault("NATION", "Korea");
        String message = String.format("Hello, %s! Welcome to %s, %s.", name, city, nation);
        String timeStamp = Instant.now().toString();

        Map<String, String> responseBody = new HashMap<>();
        responseBody.put("name", name);
        responseBody.put("city", city);
        responseBody.put("message", message);
        responseBody.put("timestamp", timeStamp);
        
        String jsonBody;
        try {
            jsonBody = new ObjectMapper().writeValueAsString(responseBody);
        } catch (Exception e) {
            logger.log("JSON 변환 오류: " + e.getMessage());
            jsonBody = "{\"error\":\"Failed to generate JSON response\"}";
        }

        APIGatewayProxyResponseEvent response = new APIGatewayProxyResponseEvent();
        response.setStatusCode(200);
        response.setBody(jsonBody);

        Map<String, String> responseHeaders = new HashMap<>();
        responseHeaders.put("Content-Type", "application/json; charset=utf-8");
        responseHeaders.put("X-Custom-Header", "custom value");
        response.setHeaders(responseHeaders);
        return response;
    }
    
}
