package com.way_ne.mapper;

import com.way_ne.pojo.Permission;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface PermissionMapper {
    List<Permission> listAll();

    List<String> listCodesByRoleId(Integer roleId);

    List<Integer> listPermissionIdsByRoleId(Integer roleId);

    void deleteRolePermissions(Integer roleId);

    void insertRolePermissions(@Param("roleId") Integer roleId, @Param("permissionIds") List<Integer> permissionIds);
}
