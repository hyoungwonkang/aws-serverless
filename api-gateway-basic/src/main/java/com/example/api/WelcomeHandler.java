package com.example.api;

import java.util.HashMap;
import java.util.Map;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;

public class WelcomeHandler implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {

    @Override
    public APIGatewayProxyResponseEvent handleRequest(APIGatewayProxyRequestEvent input, Context context) {
        // 1. 로거 설정
        LambdaLogger logger = context.getLogger();
        logger.log("----http 요청 도착----");

        // 2. Query String 파싱 (예: ?name=Jimin)
        // input.getQueryStringParameters()는 쿼리스트링이 없으면 null일 수 있으므로 주의
        Map<String, String> queryParams = input.getQueryStringParameters();
        String name = "Guest";
        if (queryParams != null && queryParams.containsKey("name")) {
            name = queryParams.get("name");
        }

        // 3. Header 파싱 (예: Header에 "lang": "ko"가 있는지 확인)
        // Http 헤더는 대소문자를 구분하지 않는 경우가 많지만, Lambda 이벤트에서는 넘어온 그대로 확인
        Map<String, String> headers = input.getHeaders();
        String lang = "en"; // 기본값 영어
        String userAgent = "Unknown";

        if (headers != null) {
            if (headers.containsKey("lang")) {
                lang = headers.get("lang");
            }
            if (headers.containsKey("User-Agent")) {
                userAgent = headers.get("User-Agent");
            }
        }

        // 4. 언어에 따른 인사말 생성 (ko, en, fr 중 하나로 가정)
        String message;
        if ("ko".equalsIgnoreCase(lang)) {
            message = String.format("안녕하세요, %s님! 당신의 브라우저는 %s군요.", name, userAgent);
        } else if ("fr".equalsIgnoreCase(lang)) {
            message = String.format("Bonjour, %s! Votre navigateur est %s.", name, userAgent);
        } else {
            message = String.format("Hello, %s! Your browser is %s.", name, userAgent);
        }

        // 5. 응답 객체 생성 (Status Code 200 + Body)
        APIGatewayProxyResponseEvent response = new APIGatewayProxyResponseEvent();
        response.setStatusCode(200);
        response.setBody(message);

        // 6. 응답 헤더 설정 (CORS 처럼 브라우저가 알아야 할 정보 등)
        Map<String, String> responseHeaders = new HashMap<>();
        responseHeaders.put("Content-Type", "text/plain; charset=utf-8");
        response.setHeaders(responseHeaders);
        return response;
    }
    
}
