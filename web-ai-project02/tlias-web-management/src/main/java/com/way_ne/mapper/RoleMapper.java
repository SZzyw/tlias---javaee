package com.way_ne.mapper;

import com.way_ne.pojo.Role;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface RoleMapper {
    List<Role> list();

    Role getById(Integer id);

    Role getByCode(String code);

    void insert(Role role);

    void updateById(Role role);

    void deleteByIds(List<Integer> ids);
}
