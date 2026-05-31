package com.way_ne.service.impl;

import com.way_ne.mapper.OperateLogMapper;
import com.way_ne.pojo.OperateLog;
import com.way_ne.service.OperateLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OperateLogServiceImpl implements OperateLogService {
    @Autowired
    private OperateLogMapper operateLogMapper;

    @Override
    public List<OperateLog> list() {
        return operateLogMapper.list();
    }
}
