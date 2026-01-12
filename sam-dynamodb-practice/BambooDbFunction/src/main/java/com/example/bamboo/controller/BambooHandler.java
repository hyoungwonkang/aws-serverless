package com.example.bamboo.controller;

import java.util.Map;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.example.bamboo.entity.Bamboo;
import com.example.bamboo.repository.BambooRepository;
import com.example.bamboo.service.BambooService;
import com.fasterxml.jackson.databind.ObjectMapper;

import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.http.urlconnection.UrlConnectionHttpClient;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;

/**
 * Lambda Handler
 */
public class BambooHandler implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {

  private static final ObjectMapper objectMapper = new ObjectMapper();
  private final BambooService bambooService;
  
  public BambooHandler() {
    // DB 클라이언트 생성
    DynamoDbClient ddb = DynamoDbClient.builder()
	    .httpClient(UrlConnectionHttpClient.create())  // Cold Start 완화 (무거운 Netty 대신 가벼운 클라이언트 사용)
      .region(Region.AP_NORTHEAST_2)
      .build();
    DynamoDbEnhancedClient enhancedClient = DynamoDbEnhancedClient.builder()
      .dynamoDbClient(ddb)
      .build();
    
    // 레포지토리 & 서비스
    BambooRepository repository = new BambooRepository(enhancedClient);
    this.bambooService = new BambooService(repository);
  }

  @Override
  public APIGatewayProxyResponseEvent handleRequest(APIGatewayProxyRequestEvent input, Context context) {
    String method = input.getHttpMethod();

    try {
      switch (method) {
        case "POST": return createPost(input);
        case "GET": return readPost(input);
        case "PUT": return updatePost(input);
        case "DELETE": return deletePost(input);
        default: return response(405, "Method Now Allowed");
      }
    } catch (IllegalArgumentException e) { // 404 Not Found 등
      return response(404, e.getMessage());
    } catch (SecurityException e) { // 403 Forbidden
      return response(403, e.getMessage());
    } catch (Exception e) {
      context.getLogger().log("Error: " + e.getMessage());
      return response(500, "Internal Server Error");
    }
  }

  private APIGatewayProxyResponseEvent createPost(APIGatewayProxyRequestEvent input) throws Exception {
    Bamboo requestDto = objectMapper.readValue(input.getBody(), Bamboo.class);
    String userId = getUserIdFromToken(input);
    Bamboo savedPost = bambooService.createPost(requestDto, userId);
    return response(201, "Post created. ID: " + savedPost.getPostId());
  }

  private APIGatewayProxyResponseEvent readPost(APIGatewayProxyRequestEvent input) throws Exception {
    String postId = input.getPathParameters().get("postId");
    Bamboo post = bambooService.getPost(postId);
    return response(200, objectMapper.writeValueAsString(post));
  }

  private APIGatewayProxyResponseEvent updatePost(APIGatewayProxyRequestEvent input) throws Exception {
    String postId = input.getPathParameters().get("postId");
    String userId = getUserIdFromToken(input);
    Bamboo requestDto = objectMapper.readValue(input.getBody(), Bamboo.class);
    bambooService.updatePost(postId, userId, requestDto.getTitle(), requestDto.getContent());
    return response(200, "Post updated successfully");
  }

  private APIGatewayProxyResponseEvent deletePost(APIGatewayProxyRequestEvent input) {
    String postId = input.getPathParameters().get("postId");
    String userId = getUserIdFromToken(input);
    bambooService.deletePost(postId, userId);
    return response(200, "Post deleted successfully");
  }

  private String getUserIdFromToken(APIGatewayProxyRequestEvent input) {
    Map<String, Object> authorizer = input.getRequestContext().getAuthorizer();
    return (String) authorizer.get("principalId");
  }

  private APIGatewayProxyResponseEvent response(int statusCode, String body) {
    // body가 JSON 형식이 아니면 JSON으로 감싸주기 위한 간단한 로직 (실무에선 ResponseDTO 사용 권장)
    String finalBody = body.startsWith("{") ? body : 
        (statusCode / 100 == 2 ? String.format("{\"message\": \"%s\"}", body) : String.format("{\"error\": \"%s\"}", body));
        
    return new APIGatewayProxyResponseEvent()
      .withStatusCode(statusCode)
      .withHeaders(Map.of("Content-Type", "application/json"))
      .withBody(finalBody);
  }
}
