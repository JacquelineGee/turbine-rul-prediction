package com.example.demo.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Uup {
    private String UserId;
    private String UserName;
    private String gender;
    private String UserPassword;
    private String image;
    private Date updtime;
    private String phone;
    private String email;
    private Integer version;
}
