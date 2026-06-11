package com.example.demo.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Power {
    Integer powerId;
    String powerName;
    Integer faPowerId;
    Date updtime;

}
