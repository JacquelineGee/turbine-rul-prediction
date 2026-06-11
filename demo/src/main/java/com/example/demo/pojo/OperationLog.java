package com.example.demo.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class OperationLog {
    private Long id;
    private String userId;
    private String username;
    private String operation;
    private String method;
    private String params;
    private String ip;
    private Integer status;
    private String errorMsg;
    private Date operationTime;
    private Long executionTime;
    private String module;
    private Date startTime;
    private Date endTime;
}
