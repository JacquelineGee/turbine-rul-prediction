package com.example.demo.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class IfDataset {
    private String datasetName;
    private String uploadUserId;
    private String updateTimeStart;
    private String updateTimeEnd;
}
