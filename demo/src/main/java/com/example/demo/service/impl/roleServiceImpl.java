package com.example.demo.service.impl;

import com.example.demo.mapper.UMapper;
import com.example.demo.mapper.roleMapper;
import com.example.demo.pojo.Power;
import com.example.demo.pojo.Role;
import com.example.demo.service.roleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class roleServiceImpl implements roleService {
    @Autowired
    roleMapper roleMapper;
    @Override
    public List<Role> roles() {
        return roleMapper.roles();
    }

    @Override
    public List<Role> roleById(String id) {
        return roleMapper.roleById(id);
    }

    @Override
    public void roleUpd(String userId, List<Integer> roleIds) {
        roleMapper.roleUpd(userId,roleIds.toString());
    }

    @Override
    public List<Power> allPower() {
        return roleMapper.allPower();
    }

    @Override
    public List<Power> powerById(String id) {
        return roleMapper.powerById(id);
    }

    @Override
    public void updRolePower(Integer id, List<Integer> powers) {

        String powerIds = String.join(",", powers.stream().map(String::valueOf).collect(Collectors.toList()));
        roleMapper.updRolePower(id, powerIds);
    }

    @Override
    public List<Role> queryRole(Integer id, String roleName) {
        return roleMapper.quertRole(id,roleName);
    }

    @Override
    public void addPower(Power power) {
        roleMapper.addPower(power);
    }

    @Override
    public void deleteByIds(List<String> list) {
        String roleIdsStr = String.join(",", list);
        roleMapper.deleteByIds(roleIdsStr);
    }

    @Override
    public void addRole(Role role) {
        roleMapper.addRole(role);
    }

    @Override
    public Role roleById1(Integer id) {
        return roleMapper.roleById1(id);
    }

    @Override
    public void updRole(Role role) {
        roleMapper.updRole(role);
    }

    @Override
    public void deletePower(Integer id) {
        roleMapper.deletePower(id);
    }

    @Override
    public Power powerById1(Integer id) {
        return roleMapper.powerById1(id);
    }

    @Override
    public void updPower(Power power) {
        roleMapper.updPower(power);
    }
}
