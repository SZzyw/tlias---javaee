package com.way_ne.pojo;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class OperateLog {
    private Integer id;
    private String operator;
    private String operation;
    private String method;
    private String params;
    private Long costTime;
    private String ip;
    private LocalDateTime createTime;
}
