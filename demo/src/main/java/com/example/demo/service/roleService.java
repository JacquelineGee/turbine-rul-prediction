package com.example.demo.service;

import com.example.demo.pojo.*;
import io.swagger.models.auth.In;

import java.util.List;

public interface roleService {

    List<Role> roles();

    List<Role> roleById(String id);

    void roleUpd(String userId, List<Integer> roleIds);

    List<Power> allPower();

    List<Power> powerById(String id);

    void updRolePower(Integer id, List<Integer> powers);

    List<Role> queryRole(Integer id, String roleName);

    void addPower(Power power);

    void deleteByIds(List<String> list);

    void addRole(Role role);

    Role roleById1(Integer id);

    void updRole(Role role);

    void deletePower(Integer id);

    Power powerById1(Integer id);

    void updPower(Power power);
}
