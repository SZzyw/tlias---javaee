package com.way_ne.pojo;

import lombok.Data;

import java.util.List;

@Data
public class RolePermissionRequest {
    private List<Integer> permissionIds;
}
