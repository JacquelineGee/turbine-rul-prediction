package com.example.demo.controller;

import com.example.demo.annotation.OperLog;
import com.example.demo.pojo.*;
import com.example.demo.service.DatasetService;
import com.example.demo.service.PredictionService;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Slf4j
@RestController
public class PredictionController {

    @Autowired
    private PredictionService predictionService;
//    @OperLog(operation = "燃气轮机寿命预测", module = "预测模块")
    @PostMapping("/save-prediction")
    @ApiOperation("保存预测数据")
    public Result savePredictData(@RequestBody PredictSaveRequest data)
    {
        log.info(data.getLabels().toString());
        log.info(data.getModelId());
        predictionService.savePredictions(data.getLabels(),data.getLosses(),data.getPredictions(),data.getModelId(),data.getUserId(),data.getDatasetId());
        return Result.success();
    }

    @GetMapping("/predictions")
    @ApiOperation("获取所有预测记录")
    public Result allPredictions()
    {
        List<Prediction> predictions=predictionService.allPredictions();
        return Result.success(predictions);
    }
    @PostMapping("/IfSelectPrediction")
    @ApiOperation("条件查询预测记录")
    public Result Ifselect(@RequestBody IfPrediction data)
    {
        log.info("查询到{}",data.getPredictId());
        List<Prediction> predictions=predictionService.ifSelect(data);
        return Result.success(predictions);
    }
    @DeleteMapping("/deleteById2/{id}")
    @ApiOperation("根据id删除数据集")
    public Result deleteById(@PathVariable Integer id)
    {
        predictionService.deleteById(id);
        return Result.success();
    }

    @PostMapping("/prediction/{id}")
    @ApiOperation("根据id查询记录")
    public Result searchById(@PathVariable Integer id)
    {
        log.info("请求到了{}",id);
        Prediction data=predictionService.searchById(id);
        return Result.success(data);
    }

    @PostMapping("/predictionByUserId/{userId}")
    @ApiOperation("根据userid查询记录")
    public Result PredictionByUserId(@PathVariable String userId)
    {
        List<Prediction> data=predictionService.PredictionByUserId(userId);
        return Result.success(data);
    }
}
