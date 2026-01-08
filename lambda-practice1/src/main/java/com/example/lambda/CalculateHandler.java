package com.example.lambda;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;

public class CalculateHandler implements RequestHandler<CalcRequest, String> {

    @Override
    public String handleRequest(CalcRequest input, Context context) {
        // Context를 통한 로거 활용 (로그 기록은 AWS 클라우드와치 통해서 확인 가능)
        context.getLogger().log("Input:" + input);

        // 환경 변수 (AWS Console에서 직접 세팅)
        String calculatorName = System.getenv("CALCULATOR_NAME");
        if (calculatorName == null) {
            calculatorName = "MyCalc";
        }
        
        // 입력 값 처리
        String num1Str = input.getNum1();
        String num2Str = input.getNum2();
        String operation = input.getOperation();

        // 비즈니스 로직
        Double num1 = Double.parseDouble(num1Str);
        Double num2 = Double.parseDouble(num2Str);
        Double operationResult = 0D;
        
        switch (operation) {
            case "+":
                operationResult = num1 + num2;
                break;
            case "-":
                operationResult = num1 - num2;
                break;
            case "*":
                operationResult = num1 * num2;
                break;
            case "/":
                operationResult = num1 / num2;
                break;
        
            default:
                break;
        }

        String result = String.format("[%s] 결과: %s %s %s = %.2f",
            calculatorName, num1Str, operation, num2Str, operationResult);

        // 람다의 실행 결과 반환
        return result;
    }
}
