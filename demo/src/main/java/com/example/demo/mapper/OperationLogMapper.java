package com.example.demo.mapper;

import com.example.demo.pojo.LoginTime;
import com.example.demo.pojo.Model;
import com.example.demo.pojo.OperationLog;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

@Mapper
public interface OperationLogMapper {
    int insert(OperationLog log);

    List<OperationLog> selectLogList(Map<String, Object> params);

    int countLogList(Map<String, Object> params);


    List<Model> ModelUseTimes();

    List<LoginTime> LoginTimes();

    List<OperationLog> getLogList(OperationLog params);

    Integer UserNum();

    String NewestUser();
}
