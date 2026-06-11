package com.example.demo.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Prediction implements Serializable {
    private static final long serialVersionUID = 1L;

    private Integer predictId;
    private String modelName;
    private Date predictTime;
    private String labels;
    private String losses;
    private String predictions;
    private String userId;
    private String userName;
    private String datasetName;

    public List<Double> getLabelsAsList() {
        if (labels == null || labels.isEmpty()) {
            return null;
        }
        return Arrays.stream(labels.split(","))
                .map(Double::parseDouble)
                .collect(Collectors.toList());
    }

    // 将 predictions 字符串转换为 Double 列表
    public List<Double> getPredictionsAsList() {
        if (predictions == null || predictions.isEmpty()) {
            return null;
        }
        return Arrays.stream(predictions.split(","))
                .map(Double::parseDouble)
                .collect(Collectors.toList());
    }

    public List<Double> getLossesAsList() {
        if (losses == null || losses.isEmpty()) {
            return null;
        }
        return Arrays.stream(losses.split(","))
                .map(Double::parseDouble)
                .collect(Collectors.toList());
    }
}