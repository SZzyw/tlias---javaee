package com.way_ne.controller;

import com.way_ne.pojo.Result;
import com.way_ne.utils.LoginCaptchaStore;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class CaptchaController {
    private final LoginCaptchaStore loginCaptchaStore;

    @GetMapping("/captcha")
    public Result captcha() throws Exception {
        return Result.success(loginCaptchaStore.create());
    }
}
