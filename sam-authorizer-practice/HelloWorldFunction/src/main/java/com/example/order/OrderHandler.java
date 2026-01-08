package com.example.order;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;

public class OrderHandler implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {
    private List<Map<Integer, String>> orders = new ArrayList<>();
    
    // 생성자에서 목 데이터 초기화
    public OrderHandler() {
        orders.add(Map.of(100, "MacBook Pro"));
        orders.add(Map.of(200, "Apple Watch"));
        orders.add(Map.of(300, "iPad A"));
    }

    @Override
    public APIGatewayProxyResponseEvent handleRequest(APIGatewayProxyRequestEvent input, Context context) {
        
        Map<String, String> pathParameters = input.getPathParameters();
        String orderId = pathParameters != null ? pathParameters.get(id)
        
        String user = "Unknown";
        String orderId = ;
        if (policy != null) {
            user = policy.get("pricipalId").toString();
        }

        return null;
    }

    

}
