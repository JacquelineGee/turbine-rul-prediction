package com.example.demo.controller;

import com.example.demo.pojo.LoginTime;
import com.example.demo.pojo.Model;
import com.example.demo.pojo.OperationLog;
import com.example.demo.pojo.Result;
import com.example.demo.service.OperationLogService;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/system/log")
public class OperationLogController {

    private final OperationLogService operationLogService;

    public OperationLogController(OperationLogService operationLogService) {
        this.operationLogService = operationLogService;
    }
    @ApiOperation("保存日志")
    @PostMapping
    public Result<Void> save(@RequestBody OperationLog operationLog) {
        operationLogService.asyncSaveLog(operationLog);
        return Result.success();
    }
    @ApiOperation("日志列表")
    @PostMapping("/list")
    public Result<List<OperationLog>> list(@RequestBody OperationLog op) {
        List<OperationLog> result = operationLogService.getLogList(op);
        return Result.success(result);
    }

    @PostMapping("/modelUseTimes")
    @ApiOperation("获取三个模型各自使用次数")
    public Result<List<Model>> ModelUseTimes(){
        List<Model> result = operationLogService.ModelUseTimes();
        return Result.success(result);
    }

    @PostMapping("/LoginTimes")
    @ApiOperation("获取近七天内登录次数")
    public Result<List<LoginTime>> LoginTimes(){
        List<LoginTime> result = operationLogService.LoginTimes();
        return Result.success(result);
    }

    @PostMapping("/UserNum")
    @ApiOperation("统计所有用户数量")
    public Result<Integer> UserNum(){
        Integer result = operationLogService.UserNum();
        return Result.success(result);
    }

    @PostMapping("/NewestUser")
    @ApiOperation("查询最新用户")
    public Result<String> NewestUser(){
        String result = operationLogService.NewestUser();
        return Result.success(result);
    }
}