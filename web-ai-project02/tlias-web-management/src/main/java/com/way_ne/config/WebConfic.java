//package com.way_ne.config;
//
//import com.way_ne.interceptor.TokenInterceptor;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
//import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
//
//@Configuration//配置类
//public class WebConfic implements WebMvcConfigurer {
//    @Autowired
//    TokenInterceptor tokenInterceptor;
//    @Override
//    public void addInterceptors(InterceptorRegistry registry) {
//        registry.addInterceptor(tokenInterceptor)//添加拦截器
//                .addPathPatterns("/**")//拦截所有请求
//                .excludePathPatterns("/login");//不拦截请求
//    }
//}
