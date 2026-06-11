package com.example.demo.service.impl;

import com.example.demo.mapper.PredictionMapper;
import com.example.demo.pojo.IfPrediction;
import com.example.demo.pojo.Prediction;
import com.example.demo.service.PredictionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class PredictionServiceImpl implements PredictionService {

    @Autowired
    private PredictionMapper predictionMapper;
    @Autowired
    private PredictionServiceImpl self;
    @Override
    public void savePredictions(List<Double> labels, List<Double> losses, List<Double> predictions, String modelId, String userId, Integer datasetId) {
        // 1️⃣ 处理 `labels`
        String labelsStr = labels.stream()
                .map(Object::toString)
                .collect(Collectors.joining(","));

        // 2️⃣ 处理 `predictions`
        String predictionsStr = predictions.stream()
                .map(Object::toString)
                .collect(Collectors.joining(","));

        // 3️⃣ 处理 `losses`
        String lossesStr = losses.stream()
                .map(Object::toString)
                .collect(Collectors.joining(","));

        predictionMapper.savePrediction(labelsStr, predictionsStr, lossesStr, modelId, userId, datasetId);
        predictionMapper.modelUse(modelId);
        // 保存数据后，清除相关缓存，因为数据有更新
        self.clearAllPredictionsCache();
    }

    @Override
    @Cacheable(value = "allPredictionsCache", unless = "#result == null")
    public List<Prediction> allPredictions() {
        return predictionMapper.allPredictions();
    }

    @Override
    @CacheEvict(value = "allPredictionsCache", allEntries = true)
    public void deleteById(Integer id) {
        predictionMapper.deleteById(id);
    }

    @Override
    @Cacheable(value = "predictionByIdCache", key = "#id", unless = "#result == null")
    public Prediction searchById(Integer id) {
        return predictionMapper.searchById(id);
    }

    @Override
    @Cacheable(value = "ifSelectCache", key = "#data.toString()", unless = "#result == null")
    public List<Prediction> ifSelect(IfPrediction data) {
        return predictionMapper.ifSelect(data);
    }

    @Override
    public void savePdf(String url, String userId, String filename) {
        predictionMapper.savePdf(url,userId,filename);
    }

    @Override
    public List<Prediction> PredictionByUserId(String userId) {
        return predictionMapper.PredictionByUserId(userId);
    }

    /**
     * 清除所有预测数据的缓存
     */
    @Scheduled(fixedRate = 300000)
    @CacheEvict(value = "allPredictionsCache", allEntries = true)
    public void clearAllPredictionsCache() {
        // 此方法仅用于清除缓存，无实际业务逻辑
        log.info("【缓存清理】定时任务已执行，时间: {}", LocalDateTime.now());
    }
}