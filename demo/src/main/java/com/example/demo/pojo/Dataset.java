package com.example.demo.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Dataset {
    private Integer datasetId;
    private String datasetName;
    private String datasetUrl;
    private Date updateTime;
    private String uploadUserId;
    private String userName;
}
