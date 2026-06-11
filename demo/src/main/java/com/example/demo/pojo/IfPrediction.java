package com.example.demo.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class IfPrediction {
    private Integer predictId;
    private String userId;
    private String predictModel;
    private String updateTimeStart;
    private String updateTimeEnd;
}
