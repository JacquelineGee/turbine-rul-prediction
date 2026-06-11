package com.example.demo.controller;

import com.example.demo.pojo.Result;
import com.example.demo.utils.JwtUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
@Slf4j
@RestController
public class GetUserController {
    @GetMapping("/current-user")
    public Result getCurrentUser(@RequestHeader("Authorization") String authHeader) {
        try {
            log.info(authHeader);
            if (authHeader == null) {
                return Result.error("未提供有效的认证信息");
            }

            String jwt = authHeader.substring(7);
            String userId = JwtUtils.getUserId(jwt);
            String username = JwtUtils.getUsername(jwt);

            return Result.success(Map.of(
                    "userId", userId,
                    "username", username
            ));
        } catch (Exception e) {
            log.error("获取用户信息失败", e);
            return Result.error("获取用户信息失败");
        }
    }
}
