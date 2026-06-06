package com.way_ne.service;

import com.way_ne.pojo.Permission;
import com.way_ne.pojo.Role;

import java.util.List;

public interface RoleService {
    List<Role> list();

    Role getById(Integer id);

    void save(Role role);

    void update(Role role);

    void delete(List<Integer> ids);

    List<Permission> listPermissions();

    List<Integer> getPermissionIds(Integer roleId);

    List<String> getPermissionCodes(Integer roleId);

    void assignPermissions(Integer roleId, List<Integer> permissionIds);
}
