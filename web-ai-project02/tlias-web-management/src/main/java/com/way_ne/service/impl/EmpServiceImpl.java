package com.way_ne.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.way_ne.mapper.EmpExprMapper;
import com.way_ne.mapper.EmpMapper;
import com.way_ne.pojo.LoginRequest;
import com.way_ne.pojo.*;
import com.way_ne.service.RoleService;
import com.way_ne.service.EmpService;
import com.way_ne.utils.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
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
    @Autowired
    PasswordEncoder passwordEncoder;
    @Autowired
    RoleService roleService;

    @Override
    public PageResult<Emp> page(EmpQueryParam empQueryParam) {
        PageHelper.startPage(empQueryParam.getPage(), empQueryParam.getPageSize());
        List<Emp> empList=empMapper.list(empQueryParam);
        Page<Emp> p=(Page<Emp>) empList;
        return new PageResult<Emp>(p.getTotal(), p.getResult());
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void save(Emp emp) {
        emp.setPassword(passwordEncoder.encode("123456"));
        emp.setCreateTime(LocalDateTime.now());
        emp.setUpdateTime(LocalDateTime.now());
        empMapper.insert(emp);
        List<EmpExpr> emprList=emp.getExprList();
        if(emprList!=null && emprList.size()>0){
            for(EmpExpr empExpr : emprList){
                empExpr.setEmpId(emp.getId());
            }
            empExprMapper.insertBatch(emprList);
        }
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void delete(List<Integer> ids){
        empMapper.deleteById(ids);
        empExprMapper.deleteByEmpIds(ids);
    }

    @Override
    public Emp getInfo(Integer id) {
        return empMapper.getById(id);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void update(Emp emp) {
        emp.setUpdateTime(LocalDateTime.now());
        empMapper.updateById(emp);
        empExprMapper.deleteByEmpIds(Arrays.asList(emp.getId()));
        List<EmpExpr> emprList=emp.getExprList();
        if(emprList!=null && emprList.size()>0){
            for(EmpExpr empExpr : emprList){
                empExpr.setEmpId(emp.getId());
            }
            empExprMapper.insertBatch(emprList);
        }
    }

    @Override
    public LoginInfo login(LoginRequest loginRequest) {
        Emp e=empMapper.selectByUsername(loginRequest.getUsername());
        if(e!=null && passwordEncoder.matches(loginRequest.getPassword(), e.getPassword())){
            List<String> permissions = roleService.getPermissionCodes(e.getRoleId());
            Map<String,Object> claims=new HashMap<>();
            claims.put("id",e.getId());
            claims.put("username",e.getUsername());
            claims.put("roleId", e.getRoleId());
            claims.put("permissions", String.join(",", permissions));
            String token= JwtUtils.generateToken(claims);
            return new LoginInfo(e.getId(), e.getUsername(), e.getName(), token, e.getRoleId(), e.getRoleCode(), e.getRoleName(), permissions);
        }
        return null;
    }

    @Override
    public void changePassword(Integer id, String oldPassword, String newPassword) {
        Emp emp = empMapper.getById(id);
        if (emp == null) {
            throw new RuntimeException("用户不存在");
        }
        if (!passwordEncoder.matches(oldPassword, emp.getPassword())) {
            throw new RuntimeException("原密码错误");
        }
        empMapper.updatePassword(id, passwordEncoder.encode(newPassword));
    }

    @Override
    public List<Emp> getlist() {
        return empMapper.getlist();
    }
}
