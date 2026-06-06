package com.way_ne.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Component
public class SecuritySchemaInitializer implements CommandLineRunner {
    private final JdbcTemplate jdbcTemplate;

    public SecuritySchemaInitializer(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void run(String... args) {
        createSchema();
        seedPermissions();
        seedRoles();
        seedRolePermissions();
        migrateEmployeeRoles();
    }

    private void createSchema() {
        jdbcTemplate.execute("""
                create table if not exists sys_role (
                  id int unsigned not null auto_increment comment 'ID,主键',
                  name varchar(30) not null comment '角色名称',
                  code varchar(30) not null comment '角色编码',
                  description varchar(255) default null comment '角色描述',
                  create_time datetime default null comment '创建时间',
                  update_time datetime default null comment '修改时间',
                  primary key (id),
                  unique key uk_role_name (name),
                  unique key uk_role_code (code)
                ) engine=InnoDB default charset=utf8mb4 collate=utf8mb4_0900_ai_ci comment='角色表'
                """);
        jdbcTemplate.execute("""
                create table if not exists sys_permission (
                  id int unsigned not null auto_increment comment 'ID,主键',
                  code varchar(50) not null comment '权限编码',
                  name varchar(50) not null comment '权限名称',
                  group_name varchar(50) default null comment '权限分组',
                  type varchar(20) default null comment '权限类型',
                  path varchar(100) default null comment '关联路径',
                  sort_order int default 0 comment '排序',
                  create_time datetime default null comment '创建时间',
                  update_time datetime default null comment '修改时间',
                  primary key (id),
                  unique key uk_permission_code (code)
                ) engine=InnoDB default charset=utf8mb4 collate=utf8mb4_0900_ai_ci comment='权限表'
                """);
        jdbcTemplate.execute("""
                create table if not exists sys_role_permission (
                  role_id int unsigned not null comment '角色ID',
                  permission_id int unsigned not null comment '权限ID',
                  primary key (role_id, permission_id)
                ) engine=InnoDB default charset=utf8mb4 collate=utf8mb4_0900_ai_ci comment='角色权限关联表'
                """);
        Integer roleIdColumnCount = jdbcTemplate.queryForObject("""
                select count(*)
                from information_schema.columns
                where table_schema = database()
                  and table_name = 'emp'
                  and column_name = 'role_id'
                """, Integer.class);
        if (roleIdColumnCount != null && roleIdColumnCount == 0) {
            jdbcTemplate.execute("""
                    alter table emp add column role_id int unsigned default null comment '角色ID'
                    """);
        }
    }

    private void seedPermissions() {
        LocalDateTime now = LocalDateTime.now();
        List<Object[]> permissions = List.of(
                row("dashboard:view", "首页概览", "首页", "menu", "/index", 10, now),
                row("clazz:view", "班级管理", "班级学员管理", "menu", "/clazz", 20, now),
                row("clazz:edit", "班级编辑", "班级学员管理", "button", null, 21, now),
                row("student:view", "学员管理", "班级学员管理", "menu", "/stu", 30, now),
                row("student:edit", "学员编辑", "班级学员管理", "button", null, 31, now),
                row("student:violation", "学员违纪处理", "班级学员管理", "button", null, 32, now),
                row("dept:view", "部门管理", "系统信息管理", "menu", "/dept", 40, now),
                row("dept:edit", "部门编辑", "系统信息管理", "button", null, 41, now),
                row("emp:view", "员工管理", "系统信息管理", "menu", "/emp", 50, now),
                row("emp:edit", "员工编辑", "系统信息管理", "button", null, 51, now),
                row("role:view", "角色管理", "系统信息管理", "menu", "/role", 60, now),
                row("role:edit", "角色编辑", "系统信息管理", "button", null, 61, now),
                row("report:emp", "员工报表", "数据统计管理", "menu", "/empReport", 70, now),
                row("report:stu", "学员报表", "数据统计管理", "menu", "/stuReport", 80, now),
                row("log:view", "操作日志", "数据统计管理", "menu", "/log", 90, now)
        );
        for (Object[] row : permissions) {
            jdbcTemplate.update("""
                    insert into sys_permission(code, name, group_name, type, path, sort_order, create_time, update_time)
                    values (?, ?, ?, ?, ?, ?, ?, ?)
                    on duplicate key update
                    name = values(name),
                    group_name = values(group_name),
                    type = values(type),
                    path = values(path),
                    sort_order = values(sort_order),
                    update_time = values(update_time)
                    """, row);
        }
    }

    private void seedRoles() {
        LocalDateTime now = LocalDateTime.now();
        insertRole("ADMIN", "管理员", "系统管理员", now);
        insertRole("MASTER", "班主任", "班级与学员管理", now);
        insertRole("TEACHER", "讲师", "教学岗位", now);
        insertRole("STUDENT_AFFAIRS", "学工", "学员事务管理", now);
    }

    private void insertRole(String code, String name, String description, LocalDateTime now) {
        jdbcTemplate.update("""
                insert into sys_role(code, name, description, create_time, update_time)
                values (?, ?, ?, ?, ?)
                on duplicate key update
                name = values(name),
                description = values(description),
                update_time = values(update_time)
                """, code, name, description, now, now);
    }

    private void seedRolePermissions() {
        grantAll("ADMIN");
        grant("MASTER", "dashboard:view", "clazz:view", "clazz:edit", "student:view", "student:edit", "student:violation", "report:stu");
        grant("TEACHER", "dashboard:view", "clazz:view", "student:view", "report:emp", "report:stu");
        grant("STUDENT_AFFAIRS", "dashboard:view", "clazz:view", "student:view", "student:edit", "student:violation", "report:stu", "log:view");
    }

    private void grantAll(String roleCode) {
        jdbcTemplate.update("""
                insert ignore into sys_role_permission(role_id, permission_id)
                select r.id, p.id from sys_role r join sys_permission p where r.code = ?
                """, roleCode);
    }

    private void grant(String roleCode, String... permissionCodes) {
        for (String permissionCode : permissionCodes) {
            jdbcTemplate.update("""
                    insert ignore into sys_role_permission(role_id, permission_id)
                    select r.id, p.id from sys_role r join sys_permission p
                    where r.code = ? and p.code = ?
                    """, roleCode, permissionCode);
        }
    }

    private void migrateEmployeeRoles() {
        jdbcTemplate.update("""
                update emp set role_id = (select id from sys_role where code = 'MASTER' limit 1)
                where role_id is null and job = 1
                """);
        jdbcTemplate.update("""
                update emp set role_id = (select id from sys_role where code = 'TEACHER' limit 1)
                where role_id is null and job = 2
                """);
        jdbcTemplate.update("""
                update emp set role_id = (select id from sys_role where code = 'STUDENT_AFFAIRS' limit 1)
                where role_id is null and job = 3
                """);
        jdbcTemplate.update("""
                update emp set role_id = (select id from sys_role where code = 'ADMIN' limit 1)
                where role_id is null
                """);
    }

    private Object[] row(String code, String name, String groupName, String type, String path, Integer sortOrder, LocalDateTime now) {
        return new Object[]{code, name, groupName, type, path, sortOrder, now, now};
    }
}
