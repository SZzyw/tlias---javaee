package com.way_ne.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 职位数据统计
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class JobOption {
    private List jobList;//职位列表
    private List dataList;//数据列表
}
