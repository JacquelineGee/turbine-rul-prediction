package com.example.demo.controller;

import com.example.demo.pojo.OperationLog;
import com.example.demo.pojo.Result;
import com.example.demo.pojo.U;
import com.example.demo.service.OperationLogService;
import com.example.demo.service.UService;
import com.example.demo.utils.JwtUtils;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.formula.functions.Now;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static java.time.LocalTime.now;

//@CrossOrigin(origins="*")
//@CrossOrigin(origins="http://localhost:7000/",methods={RequestMethod.GET, RequestMethod.POST,RequestMethod.OPTIONS},allowCredentials="true")
@Slf4j
@RestController
public class ULoginController {
    @Autowired
    UService uService;
    @Autowired
    OperationLogService operationLogService;
    @ApiOperation("用户登录")
    @PostMapping("/log")
    public Result login(@RequestBody U u){
        U e=uService.login(u);
        if(e!=null){
            Map<String, Object> Claims=new HashMap<>();
            Claims.put("id",e.getUserId());
            Claims.put("username",e.getUserName());
            String jwt = JwtUtils.generateJwt(Claims);
            e.setJwt(jwt);
            OperationLog log = new OperationLog();
            log.setUserId(e.getUserId());
            log.setUsername(e.getUserName());
            log.setOperation("用户登录");
            log.setModule("认证模块");
            log.setStatus(1);
            log.setMethod("login");
            log.setOperationTime(new Date());
            operationLogService.asyncSaveLog(log);
            return Result.success(e);
        }
        return Result.error("用户名或密码错误");
    }

}
