package com.example.demo.controller;

import com.example.demo.pojo.*;
import com.example.demo.service.roleService;
import io.swagger.annotations.ApiOperation;
import io.swagger.models.auth.In;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
public class RoleController {
    @Autowired
    private roleService roleService;
    @GetMapping("/roles")
    @ApiOperation("查询所有角色信息")
    public Result Roles()
    {
        List<Role> roleList = roleService.roles();
        return Result.success(roleList);
    }
    @PostMapping("/role/{id}")
    @ApiOperation("根据id查询角色信息")
    public Result getUById(@PathVariable String id)
    {
        List<Role> roles = roleService.roleById(id);
        log.info("查询到用户{}的角色，{}",id,roles);
        return Result.success(roles);
    }
    @PostMapping("/user/updateRoles")
    @ApiOperation("更新用户角色信息")
    public Result roleUpd(@RequestBody RoleUpd roleUpd)
    {
        String userId = roleUpd.getUserId();
        List<Integer> roleIds = roleUpd.getRoleIds();

        log.info("更新用户{},{}的权限",userId,roleIds);
        roleService.roleUpd(userId,roleIds);
        return Result.success();
    }

    @GetMapping("/allPower")
    @ApiOperation("查询所有权限")
    public Result allPower()
    {
        List<Power> powers=roleService.allPower();
        return Result.success(powers);
    }
    @PostMapping("/power/{id}")
    @ApiOperation("查询用户所有权限")
    public Result powerById(@PathVariable String id)
    {

        List<Power> powers=roleService.powerById(id);
        log.info("查询到角色{}的权限",id);
        return Result.success(powers);
    }
    @PostMapping("/queryRoles")
    @ApiOperation("查询角色信息")
    public Result queryRole(@RequestBody Role role)
    {
        Integer id=role.getRoleId();
        String roleName=role.getRoleName();
        log.info("角色{}",roleName);
        List<Role> roles = roleService.queryRole(id,roleName);
        return Result.success(roles);
    }
    @PostMapping("/roleDeleteByIds")
    @ApiOperation("删除角色信息")
    public Result deleteByIds(@RequestBody List<String> list)
    {
        roleService.deleteByIds(list);
        return Result.success();
    }
    @PostMapping("/addRole")
    @ApiOperation("添加角色信息")
    public Result addRole(@RequestBody Role role)
    {
        roleService.addRole(role);
        return Result.success();
    }
    @PostMapping("/roleById/{id}")
    @ApiOperation("根据id查询角色信息")
    public Result roleById(@PathVariable Integer id)
    {
        Role role=roleService.roleById1(id);
        return Result.success(role);
    }
    @PostMapping("/addPower")
    @ApiOperation("查询所有权限")
    public Result addPower(@RequestBody Power power)
    {
        roleService.addPower(power);
        return Result.success();
    }
    @PostMapping("/updRole")
    @ApiOperation("更新角色信息")
    public Result updRole(@RequestBody Role role)
    {
        roleService.updRole(role);
        return Result.success();
    }
    @PostMapping("/updateRolePower/{id}")
    @ApiOperation("根据id更新角色权限")
    public Result updateRolePower(@PathVariable Integer id,@RequestBody Pupd pupd)
    {
        List<Integer> powers=pupd.getPowers();
        log.info("查询到角色{}的权限",powers);
        roleService.updRolePower(id,powers);
        return Result.success();
    }
    @PostMapping("/deletePower/{id}")
    @ApiOperation("根据id删除权限")
    public Result deletePower(@PathVariable Integer id)
    {
        roleService.deletePower(id);
        return Result.success();
    }
    @PostMapping("/powerById/{id}")
    @ApiOperation("根据id查询权限信息")
    public Result powerById1(@PathVariable Integer id)
    {
        Power power=roleService.powerById1(id);
        return Result.success(power);
    }
    @PostMapping("/updPower")
    @ApiOperation("更新权限信息")
    public Result updPower(@RequestBody Power power)
    {
        log.info(power.getPowerName());
        roleService.updPower(power);
        return Result.success();
    }

}
