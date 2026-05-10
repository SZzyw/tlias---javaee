package com.way_ne.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.way_ne.mapper.EmpExprMapper;
import com.way_ne.mapper.EmpMapper;
import com.way_ne.pojo.*;
import com.way_ne.service.EmpService;
import com.way_ne.utils.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class EmpServiceImpl implements EmpService {
    @Autowired
    EmpMapper empMapper;
    @Autowired
    EmpExprMapper empExprMapper;
    @Override
    public PageResult<Emp> page(EmpQueryParam empQueryParam) {
        PageHelper.startPage(empQueryParam.getPage(),empQueryParam.getPageSize());//页码,每页记录数
        List<Emp> empList=empMapper.list(empQueryParam);
        Page<Emp> p=(Page<Emp>)empList;//强转
        return new PageResult<Emp>(p.getTotal(),p.getResult());//获取总记录数，获取当前页数据
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void save(Emp emp) {
        //员工的基本信息
        emp.setCreateTime(LocalDateTime.now());
        emp.setUpdateTime(LocalDateTime.now());
        empMapper.insert(emp);
        //员工的职位信息
        List<EmpExpr> emprList=emp.getExprList();
        if(emprList!=null&&emprList.size()>0){
            for(EmpExpr empExpr:emprList){
                empExpr.setEmpId(emp.getId());
            }
            empExprMapper.insertBatch(emprList);
        }
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void delete(List<Integer> ids){
        //删除员工
        empMapper.deleteById(ids);
        //删除职位信息
        empExprMapper.deleteByEmpIds(ids);
    }

    @Override
    public Emp getInfo(Integer id) {
        return empMapper.getById(id);
    }
    @Transactional(rollbackFor = Exception.class)
    @Override
    public void update(Emp emp) {
        //修改员工信息
        emp.setUpdateTime(LocalDateTime.now());
        empMapper.updateById(emp);
        //修改职位信息
        //先删除
        empExprMapper.deleteByEmpIds(Arrays.asList(emp.getId()));
        //再添加
        List<EmpExpr> emprList=emp.getExprList();
        if(emprList!=null&&emprList.size()>0){
            for(EmpExpr empExpr:emprList){
                empExpr.setEmpId(emp.getId());
            }
            empExprMapper.insertBatch(emprList);
        }
    }

    @Override
    public LoginInfo login(Emp emp) {
        Emp e=empMapper.selectByUsernameAndPassword(emp);
        if(e!=null){
            Map<String,Object> claims=new HashMap<>();
            claims.put("id",e.getId());
            claims.put("username",e.getUsername());
            String token= JwtUtils.generateToken(claims);
            return new LoginInfo(e.getId(),e.getUsername(),e.getName(),token);
        }
        return null;
    }

    @Override
    public List<Emp> getlist() {
        return empMapper.getlist();
    }
}
