package com.way_ne.service.impl;

import com.way_ne.mapper.DeptMapper;
import com.way_ne.pojo.Dept;
import com.way_ne.service.DeptService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.security.cert.Extension;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class DeptServicImpl implements DeptService {
    @Autowired
    private DeptMapper deptMapper;
    @Override
    public List<Dept> findAll() {
        return deptMapper.findAll();
    }
    @Override
    public void deleteById(Integer deptId) {
        deptMapper.deleteById(deptId);
    }
    @Override
    public void add(Dept dept) {
        dept.setCreateTime(LocalDateTime.now());
        dept.setUpdateTime(LocalDateTime.now());
        deptMapper.add(dept);
    }
    @Override
    public Dept getById(Integer id) {
        return deptMapper.getId(id);
    }
    @Override
    public void update(Dept dept) {
        dept.setUpdateTime(LocalDateTime.now());
        deptMapper.update(dept);
    }
}
