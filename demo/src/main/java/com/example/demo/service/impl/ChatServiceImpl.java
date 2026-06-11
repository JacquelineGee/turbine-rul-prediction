package com.example.demo.service.impl;

import com.example.demo.mapper.ChatMessageMapper;
import com.example.demo.mapper.ConversationMapper;
import com.example.demo.pojo.ChatMessage;
import com.example.demo.pojo.Conversation;
import com.example.demo.service.ChatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class ChatServiceImpl implements ChatService {
    @Autowired
    ConversationMapper conversationMapper;
    @Autowired
    ChatMessageMapper chatMessageMapper;

    @Transactional
    @Override
    public Conversation createConversation(String userId, String firstMessage) {
        Conversation conversation = new Conversation();
        conversation.setUserId(userId);
        conversation.setTitle(firstMessage.substring(0, Math.min(firstMessage.length(), 30)));
        conversation.setCreatedAt(LocalDateTime.now());
        conversationMapper.insertConversation(conversation);
        return conversation;
    }
    @Override
    public List<Conversation> getConversationsByUserId(String userId) {
        List<Conversation> conversations = conversationMapper.findByUserId(userId);
        conversations.forEach(conv -> {
            List<ChatMessage> messages = chatMessageMapper.findByConversationId(conv.getId());
            conv.setMessages(messages);
        });
        return conversations;
    }
    @Override
    public Conversation getConversationById(Long id) {
        Conversation conversation = conversationMapper.findById(id);
        if (conversation != null) {
            List<ChatMessage> messages = chatMessageMapper.findByConversationId(id);
            // 确保 messages 不为 null
            conversation.setMessages(messages != null ? messages : new ArrayList<>());
        }
        return conversation;
    }
    @Override
    @Transactional
    public void addMessageToConversation(Long conversationId, String role, String content) {
        ChatMessage message = new ChatMessage();
        message.setConversationId(conversationId);
        message.setRole(role);
        message.setContent(content);
        message.setTimestamp(LocalDateTime.now());
        chatMessageMapper.insertMessage(message);
    }
    @Override
    @Transactional
    public void deleteConversation(Long id) {
        chatMessageMapper.deleteByConversationId(id);
        conversationMapper.deleteById(id);
    }
    @Override
    @Transactional
    public void clearUserConversations(String userId) {
        List<Conversation> conversations = conversationMapper.findByUserId(userId);
        conversations.forEach(conv -> {
            chatMessageMapper.deleteByConversationId(conv.getId());
        });
        conversationMapper.deleteByUserId(userId);
    }
}
