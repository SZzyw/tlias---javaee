<script setup>
import { computed, onMounted, ref } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  queryAllApi,
  queryByIdApi,
  addApi,
  updateApi,
  deleteApi,
  queryPermissionListApi,
  queryRolePermissionIdsApi,
  saveRolePermissionsApi
} from '@/api/role'
import { hasPermission } from '@/utils/auth'

const canRoleEdit = hasPermission('role:edit')
const roles = ref([])
const permissions = ref([])
const dialogVisible = ref(false)
const permissionDialogVisible = ref(false)
const formTitle = ref('新增角色')
const currentRoleId = ref(null)
const checkedPermissionIds = ref([])
const roleFormRef = ref()
const role = ref({ name: '', code: '', description: '' })

const permissionGroups = computed(() => {
  const groups = {}
  permissions.value.forEach((item) => {
    const groupName = item.groupName || '未分组'
    if (!groups[groupName]) groups[groupName] = []
    groups[groupName].push(item)
  })
  return groups
})

const loadRoles = async () => {
  const result = await queryAllApi()
  if (result.code) roles.value = result.data
}

const loadPermissions = async () => {
  const result = await queryPermissionListApi()
  if (result.code) permissions.value = result.data
}

onMounted(async () => {
  await Promise.all([loadRoles(), loadPermissions()])
})

const openAddDialog = () => {
  formTitle.value = '新增角色'
  role.value = { name: '', code: '', description: '' }
  dialogVisible.value = true
  roleFormRef.value?.resetFields()
}

const openEditDialog = async (id) => {
  const result = await queryByIdApi(id)
  if (result.code) {
    formTitle.value = '编辑角色'
    role.value = result.data
    dialogVisible.value = true
  }
}

const save = async () => {
  const result = role.value.id ? await updateApi(role.value) : await addApi(role.value)
  if (result.code) {
    ElMessage.success('保存成功')
    dialogVisible.value = false
    loadRoles()
  } else {
    ElMessage.error(result.msg)
  }
}

const remove = async (id) => {
  ElMessageBox.confirm('确认删除该角色吗？', '提示', {
    confirmButtonText: '确认',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    const result = await deleteApi([id])
    if (result.code) {
      ElMessage.success('删除成功')
      loadRoles()
    } else {
      ElMessage.error(result.msg)
    }
  }).catch(() => {})
}

const openPermissionDialog = async (item) => {
  currentRoleId.value = item.id
  const result = await queryRolePermissionIdsApi(item.id)
  if (result.code) {
    checkedPermissionIds.value = result.data || []
    permissionDialogVisible.value = true
  }
}

const savePermissions = async () => {
  const result = await saveRolePermissionsApi(currentRoleId.value, checkedPermissionIds.value)
  if (result.code) {
    ElMessage.success('权限分配成功')
    permissionDialogVisible.value = false
    loadRoles()
  } else {
    ElMessage.error(result.msg)
  }
}
</script>

<template>
  <div class="page-shell">
    <section class="page-header">
      <div class="page-header-copy">
        <p class="page-eyebrow">Authorization</p>
        <h1 class="page-title">角色管理</h1>
        <p class="page-description">按职责维护角色、权限与人员绑定范围，确保后台访问能力与组织分工一致。</p>
      </div>
      <div class="page-actions" v-if="canRoleEdit">
        <el-button type="primary" @click="openAddDialog">+ 新增角色</el-button>
      </div>
    </section>

    <section class="page-table-card">
      <div class="table-panel-header">
        <div>
          <h3 class="page-section-title">角色列表</h3>
          <p class="page-section-subtitle">查看角色编码、描述和绑定员工数量，快速进入权限配置。</p>
        </div>
      </div>
      <el-table :data="roles" border style="width: 100%">
        <el-table-column type="index" label="序号" width="80" align="center" />
        <el-table-column prop="name" label="角色名称" width="140" align="center" />
        <el-table-column prop="code" label="角色编码" width="180" align="center" />
        <el-table-column prop="description" label="角色描述" min-width="220" />
        <el-table-column prop="empCount" label="绑定员工数" width="120" align="center" />
        <el-table-column prop="updateTime" label="最后操作时间" width="180" align="center" />
        <el-table-column label="操作" width="260" align="center">
          <template #default="scope">
            <el-button size="small" @click="openPermissionDialog(scope.row)">
              <el-icon><Key /></el-icon> 权限
            </el-button>
            <el-button v-if="canRoleEdit" type="primary" size="small" @click="openEditDialog(scope.row.id)">
              <el-icon><EditPen /></el-icon> 编辑
            </el-button>
            <el-button v-if="canRoleEdit" type="danger" size="small" @click="remove(scope.row.id)">
              <el-icon><Delete /></el-icon> 删除
            </el-button>
          </template>
        </el-table-column>
      </el-table>
    </section>
  </div>

  <el-dialog v-model="dialogVisible" :title="formTitle" width="500">
    <el-form :model="role" ref="roleFormRef" label-width="90px">
      <el-form-item label="角色名称">
        <el-input v-model="role.name" placeholder="请输入角色名称" />
      </el-form-item>
      <el-form-item label="角色编码">
        <el-input v-model="role.code" placeholder="例如 ADMIN" />
      </el-form-item>
      <el-form-item label="角色描述">
        <el-input v-model="role.description" type="textarea" :rows="3" placeholder="请输入角色描述" />
      </el-form-item>
    </el-form>
    <template #footer>
      <el-button @click="dialogVisible = false">取消</el-button>
      <el-button type="primary" @click="save">保存</el-button>
    </template>
  </el-dialog>

  <el-dialog v-model="permissionDialogVisible" title="分配权限" width="700">
    <div class="permission-groups">
      <div class="permission-group" v-for="(groupItems, groupName) in permissionGroups" :key="groupName">
        <div class="group-title">{{ groupName }}</div>
        <el-checkbox-group v-model="checkedPermissionIds">
          <el-checkbox v-for="item in groupItems" :key="item.id" :label="item.id">
            {{ item.name }} <span class="permission-code">({{ item.code }})</span>
          </el-checkbox>
        </el-checkbox-group>
      </div>
    </div>
    <template #footer>
      <el-button @click="permissionDialogVisible = false">取消</el-button>
      <el-button type="primary" @click="savePermissions">保存</el-button>
    </template>
  </el-dialog>
</template>

<style scoped>
.permission-groups {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.permission-group {
  border: 1px solid var(--el-border-color);
  border-radius: 6px;
  padding: 12px 14px;
}

.group-title {
  font-weight: 600;
  margin-bottom: 10px;
}

.permission-code {
  color: var(--el-text-color-secondary);
}
</style>
