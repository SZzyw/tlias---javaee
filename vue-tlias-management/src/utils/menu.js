export const menuTree = [
  {
    type: "item",
    path: "/index",
    title: "首页",
    icon: "Promotion",
    permission: "dashboard:view",
  },
  {
    type: "group",
    index: "/manage",
    title: "班级学员管理",
    icon: "Menu",
    children: [
      {
        path: "/clazz",
        title: "班级管理",
        icon: "HomeFilled",
        permission: "clazz:view",
      },
      {
        path: "/stu",
        title: "学员管理",
        icon: "UserFilled",
        permission: "student:view",
      },
    ],
  },
  {
    type: "group",
    index: "/system",
    title: "系统信息管理",
    icon: "Tools",
    children: [
      {
        path: "/dept",
        title: "部门管理",
        icon: "HelpFilled",
        permission: "dept:view",
      },
      {
        path: "/emp",
        title: "员工管理",
        icon: "Avatar",
        permission: "emp:view",
      },
      {
        path: "/role",
        title: "角色管理",
        icon: "Lock",
        permission: "role:view",
      },
    ],
  },
  {
    type: "group",
    index: "/report",
    title: "数据统计管理",
    icon: "Histogram",
    children: [
      {
        path: "/empReport",
        title: "员工信息统计",
        icon: "InfoFilled",
        permission: "report:emp",
      },
      {
        path: "/stuReport",
        title: "学员信息统计",
        icon: "Share",
        permission: "report:stu",
      },
      {
        path: "/log",
        title: "日志信息统计",
        icon: "Document",
        permission: "log:view",
      },
    ],
  },
];

export const filterMenusByPermissions = (permissions = []) =>
  menuTree
    .map((item) => {
      if (item.type === "item") {
        return permissions.includes(item.permission) ? item : null;
      }
      const children = item.children.filter((child) =>
        permissions.includes(child.permission)
      );
      return children.length > 0 ? { ...item, children } : null;
    })
    .filter(Boolean);

export const getFirstAccessiblePath = (permissions = []) => {
  for (const item of filterMenusByPermissions(permissions)) {
    if (item.type === "item") return item.path;
    if (item.children?.length) return item.children[0].path;
  }
  return "/login";
};
