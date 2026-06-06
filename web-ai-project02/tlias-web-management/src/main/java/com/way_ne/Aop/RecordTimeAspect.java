package com.way_ne.Aop;

import com.way_ne.mapper.OperateLogMapper;
import com.way_ne.pojo.OperateLog;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Arrays;

@Slf4j
@Aspect
@Component
public class RecordTimeAspect {
    @Autowired
    private OperateLogMapper operateLogMapper;

    @Autowired
    private HttpServletRequest request;

    @Around("execution(* com.way_ne.service.impl.*.*(..)) && !execution(* com.way_ne.service.impl.OperateLogServiceImpl.*(..))")
    public Object recordTime(ProceedingJoinPoint pjp) throws Throwable {
        long begin = System.currentTimeMillis();
        Object result = pjp.proceed();
        long end = System.currentTimeMillis();
        long costTime = end - begin;

        String methodName = pjp.getSignature().getDeclaringTypeName() + "." + pjp.getSignature().getName();
        String params = Arrays.toString(pjp.getArgs());
        String ip = request.getRemoteAddr();
        String operation = request.getMethod() + " " + request.getRequestURI();

        OperateLog operateLog = new OperateLog();
        operateLog.setOperator(getCurrentUsername());
        operateLog.setOperation(operation);
        operateLog.setMethod(methodName);
        operateLog.setParams(params.length() > 500 ? params.substring(0, 500) : params);
        operateLog.setCostTime(costTime);
        operateLog.setIp(ip);
        operateLog.setCreateTime(LocalDateTime.now());

        try {
            operateLogMapper.insert(operateLog);
        } catch (Exception e) {
            log.warn("保存操作日志失败", e);
        }

        log.info("方法 {} 执行耗时：{}ms", pjp.getSignature(), costTime);
        return result;
    }

    private String getCurrentUsername() {
        try {
            String authHeader = request.getHeader("token");
            if (authHeader != null && !authHeader.isEmpty()) {
                var claims = com.way_ne.utils.JwtUtils.parseToken(authHeader);
                return (String) claims.get("username");
            }
        } catch (Exception e) {
            // ignore
        }
        return "unknown";
    }
}
