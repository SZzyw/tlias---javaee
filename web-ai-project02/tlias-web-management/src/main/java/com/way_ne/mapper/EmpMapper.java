package com.way_ne.mapper;

import com.way_ne.pojo.Emp;
import com.way_ne.pojo.EmpQueryParam;
import org.apache.ibatis.annotations.MapKey;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

/**
 * 员工信息
 */
@Mapper
public interface EmpMapper {
    /**
     * 查询员工信息
     */
    public List<Emp> list(EmpQueryParam empQueryParam);

    /**
     * 新增员工
     */
    //@Options(useGeneratedKeys = true, keyProperty = "id") // 获取到生成的主键 -- 主键返回
    void insert(Emp emp);

    /**
     * 删除员工
     */
    void deleteById(List<Integer> ids);

    /**
     * 根据id查询员工
     */
    Emp getById(Integer id);

    /**
     * 修改员工
     */
    void updateById(Emp emp);

    /**
     * 统计员工信息
     */
    @MapKey("pos")
    List<Map<String, Object>> countEmpJobData();

    /**
     * 统计员工性别信息
     */
    @MapKey("name")
    List<Map<String, Object>> countEmpGenderData();

    /**
     * 员工登录
     */
    Emp selectByUsernameAndPassword(Emp emp);

    Emp selectByUsername(String username);

    void updatePassword(Integer id, String password);

    List<Map<String, Object>> countEmpEntryTrend();

    List<Map<String, Object>> selectAllPasswords();

    /**
     * 查询所有员工信息
     */
    List<Emp> getlist();

    Integer countByRoleIds(List<Integer> ids);
}
