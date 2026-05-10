//package com.way_ne.interceptor;
//
//import com.way_ne.utils.JwtUtils;
//import jakarta.servlet.*;
//import jakarta.servlet.annotation.WebFilter;
//import jakarta.servlet.http.HttpServletRequest;
//import jakarta.servlet.http.HttpServletResponse;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.stereotype.Component;
//import org.springframework.web.servlet.HandlerInterceptor;
//
//import java.io.IOException;
//
//@Slf4j
//@Component
//public class TokenInterceptor implements HandlerInterceptor {
//    @Override
//    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws IOException, ServletException {
//        //3．获取请求头中的token
//        String jwt = request.getHeader("token");
//
//        //4．判断token是否存在，如果不存在，说明用户没有登录，返回错误信息（响应401状态码）
//        if(jwt==null||jwt.equals("")){
//            log.info("令牌非法，返回401");
//            response.setStatus(401);
//            return false;
//        }
//
//        //5．如果token存在，校验令牌，如果校验失败 -> 返回错误信息（响应401状态码）
//        try {
//            JwtUtils.parseToken(jwt);
//        } catch (Exception e) {
//            log.info("令牌非法，返回401");
//            response.setStatus(401);
//            return false;
//        }
//
//        //6．校验通过，放行
//        log.info("令牌合法，放行");
//        return true;
//    }
//}
