package com.example.demo.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RulDataArrays {
    private Long id;
    private String labels;
    private String predictions;
    public RulDataArrays(String labels, String predictions) {
        this.labels = labels;
        this.predictions = predictions;
    }
}
