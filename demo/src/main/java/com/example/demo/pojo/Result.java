package com.example.demo.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Result<T> {  // 添加泛型参数T
    private Integer code;
    private String msg;
    private T data;      // 使用泛型类型

    // 成功响应（无数据）
    public static <T> Result<T> success() {
        return new Result<>(1, "success", null);
    }

    // 成功响应（带数据）
    public static <T> Result<T> success(T data) {
        return new Result<>(1, "success", data);
    }

    // 失败响应
    public static <T> Result<T> error(String msg) {
        return new Result<>(0, msg, null);
    }
}