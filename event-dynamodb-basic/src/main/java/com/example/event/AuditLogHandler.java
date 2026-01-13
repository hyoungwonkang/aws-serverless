package com.example.event;

import java.util.Map;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.DynamodbEvent;
import com.amazonaws.services.lambda.runtime.events.DynamodbEvent.DynamodbStreamRecord;
import com.amazonaws.services.lambda.runtime.events.models.dynamodb.AttributeValue;

public class AuditLogHandler implements RequestHandler<DynamodbEvent, String> {

    @Override
    public String handleRequest(DynamodbEvent input, Context context) {
        for (DynamodbStreamRecord record : input.getRecords()) {
            // 이벤트 이름 (INSERT, MODIFY, DELETE)
            String eventName = record.getEventName();

            // UPDATE 감지
            if (eventName.equals("MODIFY")) {
                // 변경 전 후 데이터 가져오기
                Map<String, AttributeValue> oldImage =record.getDynamodb().getOldImage();
                Map<String, AttributeValue> newImage =record.getDynamodb().getNewImage();

                // 가격(price) 변경 여부 감지
                String oldPrice = oldImage.containsKey("price") ? oldImage.get("price").getN() : "0";
                String newPrice = newImage.containsKey("price") ? newImage.get("price").getN() : "0";
            
                // 가격(price) 변하면, 로그 남기기 (별도 서버 또는 S3 활용 권장)
                if (!oldPrice.equals(newPrice)) {
                    String productId = newImage.get("id").getS();
                    String logMessage = String.format(
                        "[AUDIT WARNING] Price Changed! ProductId: %s | Old: %s -> New: %s",
                        productId, oldPrice, newPrice);
                    context.getLogger().log(logMessage);
                }
            }
        }
        return "Successfully processed " + input.getRecords().size() + " records.";
    }
}
