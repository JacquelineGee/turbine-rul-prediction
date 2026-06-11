package com.example.demo.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Course
{
    private String id;
    private String name;
    private String type;
    private String pno;
    private int credit;
}
