package com.example.demo.mapper;

import com.example.demo.pojo.Conversation;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface ConversationMapper {
    @Insert("INSERT INTO conversation(user_id, title, created_at) VALUES(#{userId}, #{title}, #{createdAt})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    void insertConversation(Conversation conversation);

    @Select("SELECT * FROM conversation WHERE user_id = #{userId} ORDER BY created_at DESC")
    List<Conversation> findByUserId(String userId);

    @Select("SELECT * FROM conversation WHERE id = #{id}")
    Conversation findById(Long id);

    @Delete("DELETE FROM conversation WHERE id = #{id}")
    void deleteById(Long id);

    @Delete("DELETE FROM conversation WHERE user_id = #{userId}")
    void deleteByUserId(String userId);
}
