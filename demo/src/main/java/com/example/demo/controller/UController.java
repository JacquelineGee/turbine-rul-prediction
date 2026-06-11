package com.example.demo.controller;

import com.example.demo.pojo.*;
import com.example.demo.service.OperationLogService;
import com.example.demo.service.UService;
import com.example.demo.utils.JwtUtils;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@Slf4j
@RestController
//@CrossOrigin(origins="*")
public class UController {
    @Autowired
    private UService uService;
    @Autowired
    OperationLogService operationLogService;
    @ApiOperation("获取所有用户")
    @GetMapping("/users")
    public Result selectAllu(){
        List<U> us=uService.selectAllu();
        return Result.success(us);
    }
    @ApiOperation("根据ID查询用户")
    @PostMapping("/user/{id}")
    public Result getUById(@PathVariable String id)
    {
        U u=uService.selectById(id);
        log.info("查询到用户名字，{}",u.getUserName());
        return Result.success(u);
    }
    @ApiOperation("添加用户")
    @PostMapping("/addUser")
    public Result addUser(@RequestBody U u)
    {
        Map<String, Object> Claims=new HashMap<>();
        Claims.put("id",u.getUserId());
        Claims.put("username",u.getUserName());
        String jwt = JwtUtils.generateJwt(Claims);
        u.setJwt(jwt);
        OperationLog logg = new OperationLog();
        logg.setUserId(u.getUserId());
        logg.setUsername(u.getUserName());
        logg.setOperation("用户注册");
        logg.setModule("认证模块");
        logg.setStatus(1);
        logg.setMethod("/addUser");
        logg.setOperationTime(new Date());
        operationLogService.asyncSaveLog(logg);
        uService.addUser(u);

        return Result.success();
    }
    @ApiOperation("条件查询用户")
    @PostMapping("/ifUser")
    public Result ifUser(@RequestBody U u)
    {
        List<U> us=uService.ifUser(u);
        return Result.success(us);
    }
    @ApiOperation("根据ID删除用户")
    @PostMapping("/deleteById")
    public  Result deleteById(@RequestParam String Id)
    {
        uService.deleteById(Id);
        return Result.success();
    }
    @ApiOperation("批量删除用户")
    @PostMapping("/deleteByIds")
    public Result deleteByIds(@RequestBody List<String> list)
    {
        uService.deleteByIds(list);
        return Result.success();
    }
    @ApiOperation("查询用户权限")
    @PostMapping("/userPower/{id}")
    public Result userPower(@PathVariable Integer id)
    {
        List<Power> powers=uService.userPower(id);
        log.info("查询用户{}的权限",id);
        return Result.success(powers);
    }
    @ApiOperation("验证用户权限")
    @PostMapping("/verify")
    public Result verify(@RequestParam String id,@RequestParam Integer op)
    {
        U us=uService.verify(id,op);
        if(us!=null)
        {
            log.info("查询到用户{}",us);
            return Result.success();
        }

        else
            return Result.error("-1");
    }
    @ApiOperation("更新用户信息")
    @PostMapping("/upu")
    public Result UpdateUser(@RequestBody Uup uup)
    {
            uService.upu(uup);
            return Result.success();
    }
}
