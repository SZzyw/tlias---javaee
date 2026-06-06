package com.way_ne.controller;

import com.way_ne.pojo.Result;
import com.way_ne.security.PermissionCodes;
import com.way_ne.security.RequiresPermission;
import com.way_ne.service.RoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/permissions")
@RequiredArgsConstructor
public class PermissionController {
    private final RoleService roleService;

    @GetMapping
    @RequiresPermission(PermissionCodes.ROLE_VIEW)
    public Result list() {
        return Result.success(roleService.listPermissions());
    }
}
