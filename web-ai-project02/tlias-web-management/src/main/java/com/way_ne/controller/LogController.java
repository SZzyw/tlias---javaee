package com.way_ne.controller;

import com.way_ne.pojo.OperateLog;
import com.way_ne.pojo.Result;
import com.way_ne.security.RequiresPermission;
import com.way_ne.service.OperateLogService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/log")
public class LogController {
    @Autowired
    private OperateLogService operateLogService;

    @RequiresPermission("log:view")
    @GetMapping
    public Result list() {
        log.info("查询操作日志");
        List<OperateLog> list = operateLogService.list();
        return Result.success(list);
    }
}
