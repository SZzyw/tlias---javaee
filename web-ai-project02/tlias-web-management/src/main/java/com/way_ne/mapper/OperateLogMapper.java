package com.way_ne.mapper;

import com.way_ne.pojo.OperateLog;
import com.way_ne.pojo.PageResult;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface OperateLogMapper {
    void insert(OperateLog operateLog);
    List<OperateLog> list();
}
