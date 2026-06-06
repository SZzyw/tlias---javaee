package com.way_ne.controller;

import com.way_ne.pojo.LoginRequest;
import com.way_ne.pojo.LoginInfo;
import com.way_ne.pojo.Result;
import com.way_ne.service.EmpService;
import com.way_ne.utils.LoginCaptchaStore;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/login")
public class LoginController {
    @Autowired
    private EmpService empService;
    @Autowired
    private LoginCaptchaStore loginCaptchaStore;
    /**
     * 登录
     */
    @PostMapping
    public Result login(@RequestBody LoginRequest loginRequest) {
        log.info("登录信息：{}", loginRequest.getUsername());
        if (!loginCaptchaStore.validate(loginRequest.getCaptchaId(), loginRequest.getCaptchaCode())) {
            return Result.error("验证码错误或已过期");
        }
        LoginInfo info = empService.login(loginRequest);
        if(info!= null)
            return Result.success(info);
        else
            return Result.error("用户名或密码错误");
    }
}
