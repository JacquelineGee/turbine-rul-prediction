package com.example.demo.service.impl;

import com.example.demo.mapper.UMapper;
import com.example.demo.pojo.*;
import com.example.demo.service.UService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UServiceimpl implements UService {
    @Autowired
    UMapper uMapper;
    @Override
    public List<U> selectAllu() {
        return uMapper.selectAllu();
    }

    @Override
    public U login(U u) {
        return uMapper.getUByPswAndID(u);
    }



    @Override
    public void upu(Uup user) {
        uMapper.upu(user);
    }

    @Override
    public U selectById(String id) {
        return uMapper.selectById(id);
    }

    @Override
    public void addUser(U u) {
        uMapper.addUser(u);
    }

    @Override
    public List<U> ifUser(U u) {
        return uMapper.ifUser(u);
    }

    @Override
    public void deleteByIds(List<String> list) {
        String userIdsStr = String.join(",", list);
        uMapper.deleteByIds(userIdsStr);
    }

    @Override
    public List<Power> userPower(Integer id) {
        return uMapper.userPower(id);
    }

    @Override
    public U verify(String id, Integer op) {
        return uMapper.verify(id,op);
    }

    @Override
    public Integer version(String userId) {
        return uMapper.version(userId);
    }

    @Override
    public void deleteById(String id) {
        uMapper.deleteById(id);
    }
}
