package com.example.demo.filter;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
//import javax.xml.ws.WebFault;
import java.io.IOException;
//@WebFilter(urlPatterns = "/*")
public class DemoFilter implements Filter {
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        Filter.super.init(filterConfig);
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        System.out.println("拦截到了请求");
    }

    @Override
    public void destroy() {
        Filter.super.destroy();
    }
}
