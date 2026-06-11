package com.example.demo.mapper;

import com.example.demo.pojo.ChatMessage;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface ChatMessageMapper {

    @Insert("INSERT INTO chat_message(conversation_id, role, content, timestamp) " +
            "VALUES(#{conversationId}, #{role}, #{content}, #{timestamp})")
    void insertMessage(ChatMessage message);

    @Select("SELECT * FROM chat_message WHERE conversation_id = #{conversationId} ORDER BY timestamp ASC")
    List<ChatMessage> findByConversationId(Long conversationId);

    @Delete("DELETE FROM chat_message WHERE conversation_id = #{conversationId}")
    void deleteByConversationId(Long conversationId);
}
