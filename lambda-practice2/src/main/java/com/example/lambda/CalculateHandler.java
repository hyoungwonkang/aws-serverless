package com.example.lambda;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;

public class CalculateHandler implements RequestHandler<UserInfoRequest, String> {

    @Override
    public String handleRequest(UserInfoRequest input, Context context) {
        // Context를 통한 로거 활용 (로그 기록은 AWS 클라우드와치 통해서 확인 가능)
        context.getLogger().log("Input:" + input);

        // 환경 변수 (AWS Console에서 직접 세팅)
        String gender = System.getenv("GENDER");
        if (gender == null) {
            gender = "default-gender";
        }
        
        // 입력 값 처리
        String name = input.getName();
        String birthdayStr = input.getBirthday();
        
        // 비즈니스 로직
        Integer birthday = Integer.parseInt(birthdayStr);

        String result = String.format("{\"name\":\"%s\", \"gender\":\"%s\", \"birthday\":%d}", name, gender, birthday);

        // 람다의 실행 결과 반환
        return result;
    }
}
