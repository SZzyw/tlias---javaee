package com.way_ne.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.way_ne.mapper.ClazzMapper;
import com.way_ne.pojo.Clazz;
import com.way_ne.pojo.ClazzQueryParam;
import com.way_ne.pojo.PageResult;
import com.way_ne.service.ClazzService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class ClazzServiceImpl implements ClazzService {
    @Autowired
    private ClazzMapper clazzMapper;
    @Override
    public PageResult<Clazz> findAll(ClazzQueryParam clazzQueryParam) {
        PageHelper.startPage(clazzQueryParam.getPage(), clazzQueryParam.getPageSize());
        List<Clazz> list = clazzMapper.findAll(clazzQueryParam);
        Page<Clazz> page=(Page<Clazz>) list;
        LocalDate today = LocalDate.now();
        for(Clazz it : list){
            // 使用 LocalDate 的比较方法
            if(!today.isBefore(it.getEndDate())) {
                // 班级已结束，可以设置状态
                it.setStatus("已结课");
            } else if(today.isBefore(it.getBeginDate())) {
                // 班级未开始
                it.setStatus("未开班");
            } else {
                // 班级正在进行中
                it.setStatus("在读中");
            }
        }
        return new PageResult<Clazz>(page.getTotal(),page.getResult());
    }

    @Override
    public void deleteById(Integer ids) {
        clazzMapper.deleteById(ids);
    }

    @Override
    public void addClazz(Clazz clazz) {
        clazz.setCreateTime(LocalDateTime.now());
        clazz.setUpdateTime(LocalDateTime.now());
        clazzMapper.addClazz(clazz);
    }

    @Override
    public Clazz getById(Integer ids) {
        return clazzMapper.getById(ids);
    }

    @Override
    public void updateClazz(Clazz clazz) {
        clazz.setUpdateTime(LocalDateTime.now());
        clazzMapper.updateClazz(clazz);
    }

    @Override
    public List<Clazz> list() {
        return clazzMapper.list();
    }
}
