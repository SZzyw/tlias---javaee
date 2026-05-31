package com.way_ne.service;

import com.way_ne.pojo.*;

import java.util.List;

public interface EmpService {
    /**
     * 查询员工信息
     * @return
     */
    PageResult<Emp> page(EmpQueryParam empQueryParam);

    /**
     * 新增员工
     * @param emp
     */
    void save(Emp emp);

    /**
     * 删除员工
     * @param ids
     */
    void delete(List<Integer> ids);

    /**
     * 根据id查询员工
     * @param id
     * @return
     */
    Emp getInfo(Integer id);

    /**
     * 修改员工
     * @param emp
     */
    void update(Emp emp);

    /**
     * 员工登录
     * @param emp
     * @return
     */
    LoginInfo login(Emp emp);

    /**
     * 修改密码
     */
    void changePassword(Integer id, String oldPassword, String newPassword);

    /**
     * 获取员工列表
     * @return
     */
    List<Emp> getlist();
}
