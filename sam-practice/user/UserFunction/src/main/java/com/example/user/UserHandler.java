package com.example.user;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;

public class UserHandler implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {

  // DB 역할을 할 가짜 데이터(Mock Data) 준비
  // 메모리에 리스트를 미리 만들어 둡니다. (실제로는 DB에서 가져와야 할 데이터)
  private final List<Map<Integer, String>> users = new ArrayList<>();

  public UserHandler() {
    // 생성자에서 데이터 초기화
    users.add(Map.of(100, "Junior Dev"));
    users.add(Map.of(200, "Senior Dev"));
    users.add(Map.of(300, "Project Manager"));
  }

  @Override
  public APIGatewayProxyResponseEvent handleRequest(APIGatewayProxyRequestEvent input, Context context) {
    // Path Parameter 추출
    // /users/{id} 에서 {id} 부분의 값을 가져옵니다.
    Map<String, String> pathParameters = input.getPathParameters();
    String strId = pathParameters != null ? pathParameters.get("id") : null;

    context.getLogger().log("Requested ID: " + strId);

    // 응답용 헤더 설정 (JSON)
    Map<String, String> headers = new HashMap<>();
    headers.put("Content-Type", "application/json");

    // 유효성 검사 및 데이터 탐색
    try {
      if (strId == null) {
        return error(400, "Invalid Request: ID is missing", headers);
      }

      int targetId = Integer.parseInt(strId);
      String foundName = null;

      // 리스트를 순회하며 ID 찾기 (DB의 WHERE 절 역할)
      for (Map<Integer, String> user : users) {
        if (user.containsKey(targetId)) {
          foundName = user.get(targetId);
          break; // 찾았으면 반복 종료
        }
      }

      // 결과에 따른 응답 분기
      if (foundName != null) {
        // 200 OK: 찾았을 때
        String responseBody = String.format("{ \"id\": \"%d\", \"name\": \"%s\" }", targetId, foundName);
        return ok(responseBody, headers);
      } else {
        // 404 Not Found: 데이터가 없을 때
        return error(404, "User not found", headers);
      }

    } catch (NumberFormatException e) {
      // ID가 숫자가 아닐 경우 ("abc" 등) 처리
      return error(400, "ID must be a number", headers);
    }
  }

  // 정상 응답 객체 생성
  private APIGatewayProxyResponseEvent ok(String body, Map<String, String> headers) {
    return new APIGatewayProxyResponseEvent()
      .withStatusCode(200)
      .withHeaders(headers)
      .withBody(body);
  }

  // 에러 응답 객체 생성
  private APIGatewayProxyResponseEvent error(int statusCode, String body, Map<String, String> headers) {
  return new APIGatewayProxyResponseEvent()
    .withStatusCode(statusCode)
    .withHeaders(headers)
    .withBody(String.format("{\"error\": \"%s\"}", body));
  }
}
