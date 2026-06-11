package com.example.demo.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PredictSaveRequest {
    private List<Double> labels;
    private List<Double> predictions;
    private List<Double> losses;
    private String userId;
    private String modelId;
    private Integer datasetId;
}
