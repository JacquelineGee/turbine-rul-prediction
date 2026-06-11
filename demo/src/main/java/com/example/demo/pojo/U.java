package com.example.demo.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class U {
    private String userId;
    private String userName;
    private String gender;
    private String userPassword;
    private String jwt;
    private String image;
    private String roles;
    private String phone;
    private String email;
    private List<Power> powers;
    private Date updtime;
    private Integer version;
}
