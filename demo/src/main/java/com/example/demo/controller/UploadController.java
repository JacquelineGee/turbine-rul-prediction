package com.example.demo.controller;

import com.example.demo.pojo.Result;
import com.example.demo.service.PredictionService;
import com.example.demo.utils.AliOSSUtils;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Slf4j
@RestController
public class    UploadController {
    /*@PostMapping("/upload")
    public Result upload(String username, Integer age, MultipartFile image) throws IOException {
        log.info("文件上传：{},{},{}",username,age,image);
        String originalFilename = image.getOriginalFilename();
        int index = originalFilename.lastIndexOf(".");
        String extname = originalFilename.substring(index);
        String newFileName= UUID.randomUUID().toString()+extname;
        log.info("新文件名：{}",newFileName);
        image.transferTo(new File("E://images//"+newFileName));
        return Result.success();

    }*/
    @Autowired
    private AliOSSUtils aliOSSUtils;
    @Autowired
    private PredictionService predictionService;
    @ApiOperation("上传用户头像")
    @PostMapping("/upload")
    public Result upload(@RequestParam("image") MultipartFile image) throws IOException {
        log.info("文件上传：{}",image.getOriginalFilename());
        String url = aliOSSUtils.upload(image);
        log.info("文件上传完成，文件访问的url:{}",url);
        return Result.success(url);
    }
    @ApiOperation("上传数据集")
    @PostMapping("/upload1")
    public Result upload1(@RequestParam("dataset") MultipartFile dataset) throws IOException {
        log.info("文件上传：{}",dataset.getOriginalFilename());
        String url = aliOSSUtils.upload(dataset);
        log.info("文件上传完成，文件访问的url:{}",url);
        return Result.success(url);
    }
    @ApiOperation("上传预测报告")
    @PostMapping("/upload-pdf")
    public Result uploadPdf(
            @RequestParam("file") MultipartFile file,
            @RequestParam("userId") String userId,
            @RequestParam("filename") String filename) throws IOException {
        log.info("PDF文件上传：{}, userId: {},文件名：{}", file.getOriginalFilename(), userId,filename);
        String url = aliOSSUtils.upload(file);
        log.info("PDF文件上传完成，文件访问的url: {}, userId: {},文件名：{}", url, userId,filename);
        predictionService.savePdf(url,userId,filename);
        return Result.success(url);
    }
}
