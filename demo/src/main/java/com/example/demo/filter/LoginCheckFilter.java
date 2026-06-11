package com.example.demo.filter;

import com.alibaba.fastjson.JSONObject;
import com.example.demo.pojo.Result;
import com.example.demo.utils.JwtUtils;
import com.github.pagehelper.util.StringUtil;
import com.mysql.cj.util.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.jdbc.Null;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Slf4j
//@WebFilter(urlPatterns = "/*")
public class LoginCheckFilter implements Filter {

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest req=(HttpServletRequest) servletRequest;
        HttpServletResponse resp=(HttpServletResponse) servletResponse;
        String url = req.getRequestURI().toString();
        log.info("请求的url：{}",url);
        if(url.contains("login"))
        {
            log.info("登录操作，放行");
            filterChain.doFilter(servletRequest,servletResponse);
        }
        String jwt = req.getHeader("Authorization");
        if(StringUtil.isEmpty(jwt))
        {
            log.info("请求头token为空");
            Result error=Result.error("not_login");
            String NotLogin = JSONObject.toJSONString(error);
//            resp.getWriter().write(NotLogin);
            return;
        }
        try {
            JwtUtils.parseJWT(jwt);
        } catch (Exception e) {
            e.printStackTrace();
            log.info("解析令牌失败");
            Result error=Result.error("not_login");
            String NotLogin = JSONObject.toJSONString(error);
            resp.getWriter().write(NotLogin);
            return;
        }
        log.info("令牌合法，放行");
        filterChain.doFilter(servletRequest,servletResponse);
    }
}
