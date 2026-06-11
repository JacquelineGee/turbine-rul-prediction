package com.example.demo.controller;

import com.example.demo.pojo.ChatMessage;
import com.example.demo.pojo.Conversation;
import com.example.demo.service.ChatService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.ResponseExtractor;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/api/chat")
public class ChatController {
    @Value("${deepseek.api.key}")
    private String apiKey;
    private final ChatService chatService;
    private final ObjectMapper objectMapper;
    private final String deepSeekApiUrl = "https://api.deepseek.com/chat/completions";

    public ChatController(ChatService chatService, ObjectMapper objectMapper) {
        this.chatService = chatService;
        this.objectMapper = objectMapper;
    }
    @ApiOperation("临时对话")
    @PostMapping("/messageTemp")
    public SseEmitter sendMessageTemp(
            @RequestBody ChatRequest chatRequest,
            @RequestHeader("X-User-ID") String userId) {

        SseEmitter emitter = new SseEmitter(0L);

        new Thread(() -> {
            try {
                log.info("Received message from user {}: {}", userId, chatRequest.getMessage());

                Conversation conversation;
                List<ChatMessage> messagesToSend = new ArrayList<>();

                // Always include the current user message
                ChatMessage userMessage = new ChatMessage("user", chatRequest.getMessage());
                messagesToSend.add(userMessage);


                    // Create new conversation
                    //conversation = chatService.createConversation(userId, chatRequest.getMessage());


                // Save user message to database
                //chatService.addMessageToConversation(conversation.getId(), userMessage.getRole(), userMessage.getContent());

                HttpHeaders headers = new HttpHeaders();
                headers.set("Authorization", "Bearer " + apiKey);
                headers.setContentType(MediaType.APPLICATION_JSON);

                // Build request body with all messages
                String requestBody = buildRequestBody(messagesToSend);
                log.debug("Request body to DeepSeek: {}", requestBody);

                HttpEntity<String> entity = new HttpEntity<>(requestBody, headers);
                RestTemplate restTemplate = new RestTemplate();

                ResponseExtractor<Void> responseExtractor = response -> {
                    try (BufferedReader reader = new BufferedReader(new InputStreamReader(response.getBody()))) {
                        String line;
                        StringBuilder assistantResponse = new StringBuilder();
                        while ((line = reader.readLine()) != null) {
                            line = line.trim();
                            if (line.startsWith("data:")) {
                                String json = line.substring(5).trim();
                                log.info("Deepseek数据流{}",json);
                                if (!json.isEmpty() && !json.equals("[DONE]")) {
                                    emitter.send(SseEmitter.event().data(json));

                                    if (json.contains("\"delta\":{\"content\":\"")) {
                                        String content = json.split("\"delta\":\\{\"content\":\"")[1]
                                                .split("\"")[0];
                                        assistantResponse.append(content);
                                    }
                                }
                            }
                        }



                        emitter.complete();
                    } catch (Exception e) {
                        log.error("Error reading stream data: {}", e.getMessage(), e);
                        emitter.completeWithError(e);
                    }
                    return null;
                };

                restTemplate.execute(deepSeekApiUrl, HttpMethod.POST, request -> {
                    request.getHeaders().addAll(headers);
                    request.getBody().write(requestBody.getBytes());
                }, responseExtractor);

            } catch (Exception e) {
                log.error("Failed to call DeepSeek API: {}", e.getMessage(), e);
                emitter.completeWithError(e);
            }
        }).start();

        return emitter;
    }
    @ApiOperation("发送对话")
    @PostMapping("/message")
    public SseEmitter sendMessage(
            @RequestBody ChatRequest chatRequest,
            @RequestHeader("X-User-ID") String userId,
            @RequestParam(required = false) Long conversationId) {

        SseEmitter emitter = new SseEmitter(0L);

        new Thread(() -> {
            try {
                log.info("Received message from user {}: {}", userId, chatRequest.getMessage());

                Conversation conversation;
                List<ChatMessage> messagesToSend = new ArrayList<>();

                // Always include the current user message
                ChatMessage userMessage = new ChatMessage("user", chatRequest.getMessage());
                messagesToSend.add(userMessage);

                if (conversationId != null && conversationId != -1) {
                    // Continue existing conversation
                    conversation = chatService.getConversationById(conversationId);
                    if (conversation == null) {
                        throw new RuntimeException("Conversation not found");
                    }
                    // Prepend existing messages (excluding the current one)
                    messagesToSend.addAll(0, conversation.getMessages());
                } else {
                    // Create new conversation
                    conversation = chatService.createConversation(userId, chatRequest.getMessage());
                }

                // Save user message to database
                chatService.addMessageToConversation(conversation.getId(), userMessage.getRole(), userMessage.getContent());

                HttpHeaders headers = new HttpHeaders();
                headers.set("Authorization", "Bearer " + apiKey);
                headers.setContentType(MediaType.APPLICATION_JSON);

                // Build request body with all messages
                String requestBody = buildRequestBody(messagesToSend);
                log.debug("Request body to DeepSeek: {}", requestBody);

                HttpEntity<String> entity = new HttpEntity<>(requestBody, headers);
                RestTemplate restTemplate = new RestTemplate();

                ResponseExtractor<Void> responseExtractor = response -> {
                    try (BufferedReader reader = new BufferedReader(new InputStreamReader(response.getBody()))) {
                        String line;
                        StringBuilder assistantResponse = new StringBuilder();
                        while ((line = reader.readLine()) != null) {
                            line = line.trim();
                            if (line.startsWith("data:")) {
                                String json = line.substring(5).trim();
                                log.info("Deepseek数据流{}",json);
                                if (!json.isEmpty() && !json.equals("[DONE]")) {
                                    emitter.send(SseEmitter.event().data(json));

                                    if (json.contains("\"delta\":{\"content\":\"")) {
                                        String content = json.split("\"delta\":\\{\"content\":\"")[1]
                                                .split("\"")[0];
                                        assistantResponse.append(content);
                                    }
                                }
                            }
                        }

                        // Save AI response to database
                        if (assistantResponse.length() > 0 && conversationId != null) {
                            chatService.addMessageToConversation(
                                    conversation.getId(),
                                    "assistant",
                                    assistantResponse.toString()
                            );
                        }

                        emitter.complete();
                    } catch (Exception e) {
                        log.error("Error reading stream data: {}", e.getMessage(), e);
                        emitter.completeWithError(e);
                    }
                    return null;
                };

                restTemplate.execute(deepSeekApiUrl, HttpMethod.POST, request -> {
                    request.getHeaders().addAll(headers);
                    request.getBody().write(requestBody.getBytes());
                }, responseExtractor);

            } catch (Exception e) {
                log.error("Failed to call DeepSeek API: {}", e.getMessage(), e);
                emitter.completeWithError(e);
            }
        }).start();

        return emitter;
    }
    @ApiOperation("获取用户所有对话")
    @GetMapping("/conversations")
    public List<ConversationDto> getConversations(@RequestHeader("X-User-ID") String userId) {
        List<Conversation> conversations = chatService.getConversationsByUserId(userId);
        return conversations.stream().map(conv -> new ConversationDto(
                conv.getId(),
                conv.getTitle(),
                conv.getCreatedAt(),
                conv.getMessages().size()
        )).collect(Collectors.toList());
    }

    @ApiOperation("根据对话ID获取对话详情")
    @GetMapping("/conversation/{id}")
    public Conversation getConversation(@PathVariable Long id) {
        return chatService.getConversationById(id);
    }
    @ApiOperation("根据对话ID删除对话")
    @DeleteMapping("/conversation/{id}")
    public ResponseEntity<String> deleteConversation(@PathVariable Long id) {
        chatService.deleteConversation(id);
        return ResponseEntity.ok("Conversation deleted");
    }
    @ApiOperation("清空历史对话记录")
    @PostMapping("/clear")
    public ResponseEntity<String> clearChatHistory(@RequestHeader("X-User-ID") String userId) {
        chatService.clearUserConversations(userId);
        return ResponseEntity.ok("All conversation history cleared");
    }

    private String buildRequestBody(List<ChatMessage> chatHistory) throws JsonProcessingException {
        if (chatHistory == null || chatHistory.isEmpty()) {
            throw new IllegalArgumentException("Message history cannot be empty");
        }

        List<Map<String, String>> messages = chatHistory.stream()
                .map(msg -> Map.of("role", msg.getRole(), "content", msg.getContent()))
                .collect(Collectors.toList());

        Map<String, Object> requestMap = new HashMap<>();
        requestMap.put("model", "deepseek-chat");
        requestMap.put("messages", messages);
        requestMap.put("stream", true);

        return objectMapper.writeValueAsString(requestMap);
    }

    // DTO classes
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ChatRequest {
        private String message;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ConversationDto {
        private Long id;
        private String title;
        private LocalDateTime createdAt;
        private int messageCount;
    }
}