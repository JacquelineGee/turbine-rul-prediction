package com.example.demo.service;

import com.example.demo.pojo.IfPrediction;
import com.example.demo.pojo.Prediction;

import java.util.List;

public interface PredictionService {
    void savePredictions(List<Double> labels,List<Double> losses, List<Double> predictions,String modelId,String userId,Integer datasetId);

    List<Prediction> allPredictions();

    void deleteById(Integer id);

    Prediction searchById(Integer id);

    List<Prediction> ifSelect(IfPrediction data);

    void savePdf(String url, String userId, String filename);

    List<Prediction> PredictionByUserId(String userId);
}
