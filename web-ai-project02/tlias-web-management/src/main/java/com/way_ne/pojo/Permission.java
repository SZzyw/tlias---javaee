package com.way_ne.pojo;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class Permission {
    private Integer id;
    private String code;
    private String name;
    private String groupName;
    private String type;
    private String path;
    private Integer sortOrder;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
