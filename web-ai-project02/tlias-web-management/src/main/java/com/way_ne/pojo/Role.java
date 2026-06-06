package com.way_ne.pojo;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class Role {
    private Integer id;
    private String name;
    private String code;
    private String description;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;

    private Integer empCount;
    private List<Integer> permissionIds;
}
