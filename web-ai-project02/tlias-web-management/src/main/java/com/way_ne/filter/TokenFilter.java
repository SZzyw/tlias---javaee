package com.way_ne.filter;

import com.way_ne.utils.JwtUtils;
import io.jsonwebtoken.Claims;
import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;

@Slf4j
@WebFilter(urlPatterns = "/*")
public class TokenFilter implements Filter {
    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;//请求数据
        HttpServletResponse response = (HttpServletResponse) servletResponse;//响应数据
        //1.获取请求路径
        String requestpath = request.getRequestURI();// /api/emp
        //String requestpath = request.getRequestURL();// http://localhost:8080/api/emp

        //2．判断是否是登录请求，如果路径中包含 /login，说明是登录操作，放行
        if("OPTIONS".equalsIgnoreCase(request.getMethod())
                || requestpath.contains("/login")
                || requestpath.startsWith("/head/")
                || requestpath.startsWith("/captcha")
                || requestpath.startsWith("/error")){
            log.info("登录操作，放行");
            filterChain.doFilter(request,response);
            return;
        }

        //3．获取请求头中的token
        String jwt = request.getHeader("token");

        //4．判断token是否存在，如果不存在，说明用户没有登录，返回错误信息（响应401状态码）
        if(jwt==null||jwt.equals("")){
            log.info("令牌非法，返回401");
            response.setStatus(401);
            return;
        }

        //5．如果token存在，校验令牌，如果校验失败 -> 返回错误信息（响应401状态码）
        try {
            Claims claims = JwtUtils.parseToken(jwt);
            request.setAttribute("claims", claims);
        } catch (Exception e) {
            log.info("令牌非法，返回401");
            response.setStatus(401);
            return;
        }

        //6．校验通过，放行
        log.info("令牌合法，放行");
        filterChain.doFilter(request,response);
    }
}
