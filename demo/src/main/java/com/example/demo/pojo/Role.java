package com.example.demo.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Role {
    Integer RoleId;
    String RoleName;
    String powers;
    Date updtime;
    Integer version;
}
