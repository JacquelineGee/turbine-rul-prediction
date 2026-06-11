package com.example.demo.service.impl;

import com.example.demo.mapper.OperationLogMapper;
import com.example.demo.pojo.LoginTime;
import com.example.demo.pojo.Model;
import com.example.demo.pojo.OperationLog;
import com.example.demo.service.OperationLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
@Service
public class OperationLogServiceImpl implements OperationLogService {
    @Autowired
    OperationLogMapper operationLogMapper;

    @Override
    public void saveLog(OperationLog log) {
        operationLogMapper.insert(log);
    }

    @Override
    @Async("logTaskExecutor")
    public void asyncSaveLog(OperationLog log) {
        operationLogMapper.insert(log);
    }

    @Override
    public List<OperationLog> getLogList(OperationLog params) {


        return operationLogMapper.getLogList(params);
    }

    @Override
    public List<Model> ModelUseTimes() {
        return operationLogMapper.ModelUseTimes();
    }

    @Override
    public List<LoginTime> LoginTimes() {
        return operationLogMapper.LoginTimes();
    }

    @Override
    public Integer UserNum() {
        return operationLogMapper.UserNum();
    }

    @Override
    public String NewestUser() {
        return operationLogMapper.NewestUser();
    }
}
