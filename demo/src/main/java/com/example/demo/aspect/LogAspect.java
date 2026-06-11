package com.example.demo.aspect;

import com.example.demo.pojo.OperationLog;
import com.example.demo.service.OperationLogService;
import com.example.demo.utils.HttpContextUtils;
import com.example.demo.utils.IPUtils;
import com.example.demo.utils.JwtUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;
import java.util.Date;

@Aspect
@Component
@Slf4j
public class LogAspect {
    @Autowired
    private OperationLogService operationLogService;

    @Pointcut("@annotation(com.example.demo.annotation.OperLog)")
    public void operLogPointCut() {}

    @Pointcut("execution(* com.example.demo.controller..*.*(..)) && !execution(* com.example.demo.controller.ULoginController.login(..)) && !execution(* com.example.demo.controller.UController.addUser(..))")
    public void controllerPointCut() {}

    @Around("operLogPointCut() || controllerPointCut()")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
        long beginTime = System.currentTimeMillis();

        // 执行方法
        Object result = joinPoint.proceed();

        // 执行时长(毫秒)
        long time = System.currentTimeMillis() - beginTime;

        // 保存日志
        saveOperationLog(joinPoint, time, result, null);

        return result;
    }

    private void saveOperationLog(ProceedingJoinPoint joinPoint, long time, Object result, Exception e) {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();

        OperationLog operationLog = new OperationLog();

        // 获取请求信息
        HttpServletRequest request = HttpContextUtils.getHttpServletRequest();
        if (request != null) {
            String jwtToken = request.getHeader("Authorization");
            if (jwtToken != null && jwtToken.startsWith("Bearer ")) {
                try {
                    jwtToken = jwtToken.substring(7);
                    operationLog.setUserId(JwtUtils.getUserId(jwtToken));
                    operationLog.setUsername(JwtUtils.getUsername(jwtToken));
                } catch (Exception ex) {
                    log.warn("解析JWT失败，记录为匿名操作", ex);
                    operationLog.setUserId("anonymous");
                    operationLog.setUsername("anonymous");
                }
            } else {
                log.info("没有");
                operationLog.setUserId("1");
                operationLog.setUsername("anonymous");
            }
            operationLog.setIp(IPUtils.getIpAddr(request));
            operationLog.setMethod(request.getMethod() + " " + request.getRequestURI());
        }


        // 设置操作时间
        operationLog.setOperationTime(new Date());
        operationLog.setExecutionTime(time);

        // 设置操作状态
        if (e != null) {
            operationLog.setStatus(0);
            operationLog.setErrorMsg(e.getMessage());
        } else {
            operationLog.setStatus(1);
        }

        // 设置操作用户
        // 这里需要根据你的认证系统获取当前用户
        // operationLog.setUserId(currentUserId);
        // operationLog.setUsername(currentUsername);

        // 获取操作描述
        com.example.demo.annotation.OperLog operLog = method.getAnnotation(
                com.example.demo.annotation.OperLog.class);
        if (operLog != null) {
            operationLog.setOperation(operLog.operation());
            operationLog.setModule(operLog.module());
        } else {
            operationLog.setOperation(method.getName());
            operationLog.setModule(joinPoint.getTarget().getClass().getName());
        }

        // 获取请求参数
        Object[] args = joinPoint.getArgs();
        try {
            StringBuilder params = new StringBuilder();
            for (Object arg : args) {
                if (arg != null && !(arg instanceof MultipartFile)) {
                    params.append(new ObjectMapper().writeValueAsString(arg)).append(" ");
                }
            }
            operationLog.setParams(params.toString());
        } catch (Exception ex) {
            operationLog.setParams("参数解析失败");
        }

        // 异步保存日志
        operationLogService.asyncSaveLog(operationLog);
    }
}
