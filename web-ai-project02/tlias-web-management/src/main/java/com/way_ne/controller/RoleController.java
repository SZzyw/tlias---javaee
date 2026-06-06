package com.way_ne.controller;

import com.way_ne.pojo.Result;
import com.way_ne.pojo.Role;
import com.way_ne.pojo.RolePermissionRequest;
import com.way_ne.security.PermissionCodes;
import com.way_ne.security.RequiresPermission;
import com.way_ne.service.RoleService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/roles")
@RequiredArgsConstructor
public class RoleController {
    private final RoleService roleService;

    @GetMapping
    @RequiresPermission({PermissionCodes.ROLE_VIEW, PermissionCodes.EMP_VIEW})
    public Result list() {
        log.info("查询角色列表");
        return Result.success(roleService.list());
    }

    @GetMapping("/{id}")
    @RequiresPermission(PermissionCodes.ROLE_VIEW)
    public Result getById(@PathVariable Integer id) {
        return Result.success(roleService.getById(id));
    }

    @PostMapping
    @RequiresPermission(PermissionCodes.ROLE_EDIT)
    public Result save(@RequestBody Role role) {
        roleService.save(role);
        return Result.success();
    }

    @PutMapping
    @RequiresPermission(PermissionCodes.ROLE_EDIT)
    public Result update(@RequestBody Role role) {
        roleService.update(role);
        return Result.success();
    }

    @DeleteMapping
    @RequiresPermission(PermissionCodes.ROLE_EDIT)
    public Result delete(@RequestParam List<Integer> ids) {
        roleService.delete(ids);
        return Result.success();
    }

    @GetMapping("/{id}/permissions")
    @RequiresPermission(PermissionCodes.ROLE_VIEW)
    public Result getPermissionIds(@PathVariable Integer id) {
        return Result.success(roleService.getPermissionIds(id));
    }

    @PutMapping("/{id}/permissions")
    @RequiresPermission(PermissionCodes.ROLE_EDIT)
    public Result assignPermissions(@PathVariable Integer id, @RequestBody RolePermissionRequest request) {
        roleService.assignPermissions(id, request.getPermissionIds());
        return Result.success();
    }
}
