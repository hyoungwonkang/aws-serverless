package com.example.lambda;

import java.util.Map;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;

public class GreetingHandler implements RequestHandler<Map<String, String>, String> {

    @Override
    public String handleRequest(Map<String, String> input, Context context) {
        // Context를 통한 로거 활용 (로그 기록은 AWS 클라우드와치 통해서 확인 가능)
        context.getLogger().log("Input:" + input);

        // 환경 변수 (AWS Console에서 직접 세팅)
        String greetingPrefix = System.getenv("GREETING_PREFIX");
        if (greetingPrefix == null) {
            greetingPrefix = "Hello";
        }
        
        // 입력 값 처리
        String name = input.getOrDefault("name", "Guest");

        // 비즈니스 로직
        String result = String.format("%s, %s", greetingPrefix, name);

        // 람다의 실행 결과 반환
        return result;
    }
}
