package com.example.demo.controller;

import com.example.demo.pojo.Dataset;
import com.example.demo.pojo.IfDataset;
import com.example.demo.pojo.Result;
import com.example.demo.service.DatasetService;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


import java.util.List;


@Slf4j
@RestController
public class DatasetController {
    @Autowired
    private DatasetService datasetService;

    @GetMapping("/dataSet")
    @ApiOperation("获取所有数据集")
    public Result getDataset()
    {
        List<Dataset> datasets=datasetService.getDatasets();
        return Result.success(datasets);
    }
    @PostMapping("/addDataset")
    @ApiOperation("新增数据集")
    public Result addDataset(@RequestBody Dataset dataset)
    {
        datasetService.addDataset(dataset);
        return Result.success();
    }
    @DeleteMapping("/deleteById1/{id}")
    @ApiOperation("根据id删除数据集")
    public Result deleteById(@PathVariable Integer id)
    {
        datasetService.deleteById(id);
        return Result.success();
    }

    @PostMapping("/selectDataset")
    @ApiOperation("根据筛选条件查询符合内容的数据集")
    public Result selectDataset(@RequestBody IfDataset ifDataset)
    {
        List<Dataset> datasets=datasetService.selectDataset(ifDataset);
        return Result.success(datasets);
    }

    @PostMapping("/updDataset")
    @ApiOperation("修改数据集")
    public Result updDataset(@RequestBody Dataset dataset)
    {
        datasetService.updDataset(dataset);
        return Result.success();
    }


}
