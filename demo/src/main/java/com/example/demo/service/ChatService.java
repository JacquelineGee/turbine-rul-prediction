package com.example.demo.service;

import com.example.demo.pojo.Conversation;

import java.util.List;

public interface ChatService {
    public void clearUserConversations(String userId);
    public void deleteConversation(Long id);
    public void addMessageToConversation(Long conversationId, String role, String content);
    public Conversation getConversationById(Long id);
    public List<Conversation> getConversationsByUserId(String userId);
    public Conversation createConversation(String userId, String firstMessage);
}
