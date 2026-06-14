import request from "@/utils/request";

export const queryAllApi = () => request.get("/roles");

export const queryByIdApi = (id) => request.get(`/roles/${id}`);

export const addApi = (role) => request.post("/roles", role);

export const updateApi = (role) => request.put("/roles", role);

export const deleteApi = (ids) => request.delete("/roles", { params: { ids } });

export const queryPermissionListApi = () => request.get("/permissions");

export const queryRolePermissionIdsApi = (id) =>
  request.get(`/roles/${id}/permissions`);

export const saveRolePermissionsApi = (id, permissionIds) =>
  request.put(`/roles/${id}/permissions`, { permissionIds });
