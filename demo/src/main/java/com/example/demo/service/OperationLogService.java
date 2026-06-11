package com.example.demo.service;

import com.example.demo.pojo.LoginTime;
import com.example.demo.pojo.Model;
import com.example.demo.pojo.OperationLog;

import java.util.List;
import java.util.Map;

public interface OperationLogService {
    void saveLog(OperationLog log);

    void asyncSaveLog(OperationLog log);

    List<OperationLog> getLogList(OperationLog params);

    List<Model> ModelUseTimes();

    List<LoginTime> LoginTimes();

    Integer UserNum();

    String NewestUser();
}
