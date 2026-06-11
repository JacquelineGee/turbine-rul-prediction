package com.example.demo.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Conversation {
    private Long id;
    private String userId;
    private String title;
    private LocalDateTime createdAt;
    private List<ChatMessage> messages = new ArrayList<>(); // 初始化为空列表
}