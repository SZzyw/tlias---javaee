package com.way_ne.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CaptchaInfo {
    private String captchaId;
    private String imageBase64;
}
