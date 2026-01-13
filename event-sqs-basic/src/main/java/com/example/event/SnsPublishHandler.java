import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;

import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.sns.SnsClient;

public class SnsPublishHandler implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {

  // SNS 클라이언트 생성 (호출할때마다 만들지 말고, 재사용하기)
  private final SnsClient snsClient = SnsClient.builder()
    .region(Region.AP_NORTHEAST_2)
    .build();

  @Override
  public APIGatewayProxyResponseEvent handleRequest(APIGatewayProxyRequestEvent input, Context context) {
    // Topic Arn (환경 변수에서 가져옴)
    String topicArn = System.getenv("TOPIC_ARN");

    // 보낼 메시지 (요청 본문으로 텍스트 정보를 받아 옴)
    String message = input.getBody();
    if (message == null || message.isBlank()) {
      message = "Default Message";
    }
    return null;
  }
}
