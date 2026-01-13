package com.example.event;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.S3Event;
import com.amazonaws.services.lambda.runtime.events.models.s3.S3EventNotification.S3Entity;
import com.amazonaws.services.lambda.runtime.events.models.s3.S3EventNotification.S3EventNotificationRecord;

public class DeleteTextFileHandler implements RequestHandler<S3Event, String> {

    @Override
    public String handleRequest(S3Event input, Context context) {

        for (S3EventNotificationRecord record : input.getRecords()) {
            // 삭제된 파일의 정보 추출
            S3Entity entity = record.getS3();
            String bucketName = entity.getBucket().getName();
            String key = entity.getObject().getUrlDecodedKey();

            String message = String.format("[ALERT] File Deleted! Bucket: %s, File: %s", bucketName, key);
            context.getLogger().log(message);
        }
        return "DeleteTextFileHandler 실행됨";
    }

    
}
