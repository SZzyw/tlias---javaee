package com.way_ne.controller;

import com.way_ne.mapper.EmpMapper;
import com.way_ne.pojo.Emp;
import com.way_ne.pojo.EmpQueryParam;
import com.way_ne.pojo.PageResult;
import com.way_ne.pojo.Result;
import com.way_ne.security.RequiresPermission;
import com.way_ne.service.EmpService;
import com.way_ne.utils.JwtUtils;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 员工管理
 */
@RestController
@Slf4j
@RequestMapping("/emps")
public class EmpController {
    /**
     *查询员工信息
     */
    @Autowired
    EmpService empService;

    @Autowired
    private HttpServletRequest request;

    @RequiresPermission("emp:view")
    @GetMapping
    public Result page(EmpQueryParam empQueryParam){
        log.info("查询员工信息{}",empQueryParam);
        PageResult<Emp> list = empService.page(empQueryParam);
        return Result.success(list);
    }
    /**
     * 新增员工
     */
    @RequiresPermission("emp:edit")
    @PostMapping
    public Result add(@RequestBody Emp emp){
        log.info("新增员工{}",emp);
        empService.save(emp);
        return Result.success();
    }
    /**
     * 删除员工
     */
    @RequiresPermission("emp:edit")
    @DeleteMapping
    public Result delete(@RequestParam List<Integer> ids){
        log.info("删除员工{}",ids);
        empService.delete(ids);
        return Result.success();
    }

    @RequiresPermission("emp:view")
    @GetMapping("/{id}")
    public Result getInfo(@PathVariable Integer id){
        log.info("根据id查询员工信息{}",id);
        Emp emp=empService.getInfo(id);
        return Result.success(emp);
    }

    @RequiresPermission("emp:edit")
    @PutMapping
    public Result update(@RequestBody Emp emp){
        log.info("修改员工信息{}",emp);
        empService.update(emp);
        return Result.success();
    }

    @RequiresPermission({"clazz:view", "emp:view"})
    @GetMapping("/list")
    public Result getlist(){
        log.info("查询所有员工信息");
        List<Emp> list=empService.getlist();
        return Result.success(list);
    }

    @PutMapping("/password")
    public Result changePassword(@RequestBody Map<String, String> params) {
        String token = request.getHeader("token");
        if (token == null || token.isEmpty()) {
            return Result.error("未登录");
        }
        var claims = JwtUtils.parseToken(token);
        Integer id = (Integer) claims.get("id");
        String oldPassword = params.get("oldPassword");
        String newPassword = params.get("newPassword");
        try {
            empService.changePassword(id, oldPassword, newPassword);
            return Result.success();
        } catch (RuntimeException e) {
            return Result.error(e.getMessage());
        }
    }
}
