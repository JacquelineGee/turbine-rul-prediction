package com.example.demo.service;

import com.example.demo.pojo.*;

import java.util.List;

public interface UService {
    List<U> selectAllu();

    U login(U u);



    void upu(Uup user);

    U selectById(String id);

    void addUser(U u);

    List<U> ifUser(U u);

    void deleteByIds(List<String> list);

    List<Power> userPower(Integer id);

    U verify(String id, Integer op);

    Integer version(String userId);

    void deleteById(String id);
}
