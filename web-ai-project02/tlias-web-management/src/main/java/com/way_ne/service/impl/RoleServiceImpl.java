package com.way_ne.service.impl;

import com.way_ne.mapper.EmpMapper;
import com.way_ne.mapper.PermissionMapper;
import com.way_ne.mapper.RoleMapper;
import com.way_ne.pojo.Permission;
import com.way_ne.pojo.Role;
import com.way_ne.service.RoleService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

@Service
public class RoleServiceImpl implements RoleService {
    private final RoleMapper roleMapper;
    private final PermissionMapper permissionMapper;
    private final EmpMapper empMapper;

    public RoleServiceImpl(RoleMapper roleMapper, PermissionMapper permissionMapper, EmpMapper empMapper) {
        this.roleMapper = roleMapper;
        this.permissionMapper = permissionMapper;
        this.empMapper = empMapper;
    }

    @Override
    public List<Role> list() {
        return roleMapper.list();
    }

    @Override
    public Role getById(Integer id) {
        Role role = roleMapper.getById(id);
        if (role != null) {
            role.setPermissionIds(permissionMapper.listPermissionIdsByRoleId(id));
        }
        return role;
    }

    @Override
    public void save(Role role) {
        role.setCreateTime(LocalDateTime.now());
        role.setUpdateTime(LocalDateTime.now());
        roleMapper.insert(role);
    }

    @Override
    public void update(Role role) {
        role.setUpdateTime(LocalDateTime.now());
        roleMapper.updateById(role);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void delete(List<Integer> ids) {
        if (ids == null || ids.isEmpty()) {
            return;
        }
        if (empMapper.countByRoleIds(ids) > 0) {
            throw new RuntimeException("存在员工正在使用该角色，无法删除");
        }
        for (Integer id : ids) {
            permissionMapper.deleteRolePermissions(id);
        }
        roleMapper.deleteByIds(ids);
    }

    @Override
    public List<Permission> listPermissions() {
        return permissionMapper.listAll();
    }

    @Override
    public List<Integer> getPermissionIds(Integer roleId) {
        return permissionMapper.listPermissionIdsByRoleId(roleId);
    }

    @Override
    public List<String> getPermissionCodes(Integer roleId) {
        return permissionMapper.listCodesByRoleId(roleId);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void assignPermissions(Integer roleId, List<Integer> permissionIds) {
        permissionMapper.deleteRolePermissions(roleId);
        if (permissionIds == null) {
            permissionIds = Collections.emptyList();
        }
        if (!permissionIds.isEmpty()) {
            permissionMapper.insertRolePermissions(roleId, permissionIds);
        }
    }
}
