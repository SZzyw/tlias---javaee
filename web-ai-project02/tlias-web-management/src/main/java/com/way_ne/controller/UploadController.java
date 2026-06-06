package com.way_ne.controller;

import com.way_ne.pojo.Result;
import com.way_ne.utils.LocalHeadStorage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

/**
 * 文件上传
 */
@Slf4j
@RestController
public class UploadController {
    @Autowired
    private LocalHeadStorage localHeadStorage;

    @PostMapping("/upload")
    public Result upload(MultipartFile file) throws IOException {
        log.info("文件上传，文件名{}",file.getOriginalFilename());
        String url = localHeadStorage.store(file);
        log.info("文件上传完成，文件访问地址{}",url);
        return Result.success(url);
    }
}
