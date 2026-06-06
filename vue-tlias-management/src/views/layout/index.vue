<script setup>
import { computed, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import request from '@/utils/request'
import { clearAuth, getLoginInfo } from '@/utils/auth'
import { filterMenusByPermissions } from '@/utils/menu'

const router = useRouter()
const route = useRoute()
const passwordDialogVisible = ref(false)
const passwordForm = ref({ oldPassword: '', newPassword: '', confirmPassword: '' })
const passwordFormRef = ref()
const loginInfo = computed(() => getLoginInfo())
const menus = computed(() => filterMenusByPermissions(loginInfo.value?.permissions || []))

const pageDescriptions = {
  '/index': '集中查看员工、学员、班级与违纪数据，让教学管理与运营状态在同一页上完成总览。',
  '/clazz': '管理班级信息、排课周期、班主任与学科归属，保持班级生命周期清晰可控。',
  '/stu': '统一维护学员档案、班级归属与违纪信息，便于日常跟踪与统计分析。',
  '/dept': '维护部门结构与组织名称，为员工、角色与统计维度提供基础组织数据。',
  '/emp': '管理员工档案、头像、角色、部门与工作经历，支撑后台组织协同。',
  '/role': '按角色维度分配权限，让系统访问范围与管理职责保持一致。',
  '/log': '查看关键操作日志，快速回溯重要行为与后台使用记录。',
  '/empReport': '聚焦员工统计分析，支持岗位、性别与入职趋势等多维度查看。',
  '/stuReport': '聚焦学员统计分析，支持学历、班级人数、入学趋势与违纪排行查看。'
}

const flattenMenus = (items) =>
  items.flatMap((item) => (item.type === 'item' ? [item] : item.children || []))

const currentMenu = computed(() => flattenMenus(menus.value).find((item) => item.path === route.path))
const currentPageTitle = computed(() => currentMenu.value?.title || '教育管理系统')
const currentPageDescription = computed(
  () => pageDescriptions[route.path] || '保持教学、人员、权限与统计信息在统一后台中协同管理。'
)
const userTags = computed(() => {
  const tags = []
  if (loginInfo.value?.roleName) tags.push(loginInfo.value.roleName)
  if (loginInfo.value?.permissions?.length) tags.push(`${loginInfo.value.permissions.length} 项权限`)
  return tags
})

const logout = () => {
  clearAuth()
  ElMessage.success('已退出登录')
  router.push('/login')
}

const openPasswordDialog = () => {
  passwordForm.value = { oldPassword: '', newPassword: '', confirmPassword: '' }
  if (passwordFormRef.value) passwordFormRef.value.resetFields()
  passwordDialogVisible.value = true
}

const changePassword = async () => {
  if (!passwordFormRef.value) return
  passwordFormRef.value.validate(async (valid) => {
    if (!valid) return
    if (passwordForm.value.newPassword !== passwordForm.value.confirmPassword) {
      ElMessage.error('两次输入的密码不一致')
      return
    }
    const result = await request.put('/emps/password', {
      oldPassword: passwordForm.value.oldPassword,
      newPassword: passwordForm.value.newPassword
    })
    if (result.code) {
      ElMessage.success('修改密码成功，请重新登录')
      passwordDialogVisible.value = false
      logout()
    } else {
      ElMessage.error(result.msg)
    }
  })
}

const passwordRules = {
  oldPassword: [{ required: true, message: '请输入原密码', trigger: 'blur' }],
  newPassword: [
    { required: true, message: '请输入新密码', trigger: 'blur' },
    { min: 6, max: 20, message: '密码长度6-20位', trigger: 'blur' }
  ],
  confirmPassword: [{ required: true, message: '请确认新密码', trigger: 'blur' }]
}
</script>

<template>
  <div class="layout-shell">
    <el-container class="shell-frame">
      <el-aside width="260px" class="shell-aside">
        <div class="brand-panel">
          <span class="brand-badge">TLIAS</span>
          <h1 class="brand-title">教育管理系统</h1>
          <p class="brand-subtitle">Teaching & Learning Information Administration System</p>
        </div>

        <el-scrollbar class="menu-scroll">
          <el-menu router :default-active="route.path" class="shell-menu">
            <template v-for="item in menus" :key="item.type === 'item' ? item.path : item.index">
              <el-menu-item v-if="item.type === 'item'" :index="item.path">
                <el-icon><component :is="item.icon" /></el-icon>
                <span>{{ item.title }}</span>
              </el-menu-item>
              <el-sub-menu v-else :index="item.index">
                <template #title>
                  <el-icon><component :is="item.icon" /></el-icon>
                  <span>{{ item.title }}</span>
                </template>
                <el-menu-item v-for="child in item.children" :key="child.path" :index="child.path">
                  <el-icon><component :is="child.icon" /></el-icon>
                  <span>{{ child.title }}</span>
                </el-menu-item>
              </el-sub-menu>
            </template>
          </el-menu>
        </el-scrollbar>
      </el-aside>

      <el-container class="shell-main">
        <el-header class="shell-header">
          <div class="header-copy">
            <p class="header-kicker">智慧教务后台</p>
            <h2>{{ currentPageTitle }}</h2>
            <p>{{ currentPageDescription }}</p>
          </div>

          <div class="header-side">
            <div class="user-panel">
              <div class="user-avatar">{{ (loginInfo?.name || 'U').slice(0, 1) }}</div>
              <div class="user-meta">
                <span class="user-name">{{ loginInfo?.name || '未登录用户' }}</span>
                <div class="user-tags">
                  <span v-for="tag in userTags" :key="tag" class="user-tag">{{ tag }}</span>
                </div>
              </div>
            </div>

            <div class="header-buttons">
              <el-button type="primary" plain @click="openPasswordDialog">
                <el-icon><EditPen /></el-icon> 修改密码
              </el-button>
              <el-button @click="logout">
                <el-icon><SwitchButton /></el-icon> 退出登录
              </el-button>
            </div>
          </div>
        </el-header>

        <el-main class="shell-content">
          <router-view></router-view>
        </el-main>
      </el-container>
    </el-container>

    <open-chat-widget url="/api/chat"></open-chat-widget>
  </div>

  <el-dialog v-model="passwordDialogVisible" title="修改密码" width="450">
    <el-form :model="passwordForm" :rules="passwordRules" ref="passwordFormRef" label-width="100px">
      <el-form-item label="原密码" prop="oldPassword">
        <el-input type="password" v-model="passwordForm.oldPassword" placeholder="请输入原密码" show-password />
      </el-form-item>
      <el-form-item label="新密码" prop="newPassword">
        <el-input type="password" v-model="passwordForm.newPassword" placeholder="请输入新密码" show-password />
      </el-form-item>
      <el-form-item label="确认密码" prop="confirmPassword">
        <el-input type="password" v-model="passwordForm.confirmPassword" placeholder="请再次输入新密码" show-password />
      </el-form-item>
    </el-form>
    <template #footer>
      <el-button @click="passwordDialogVisible = false">取消</el-button>
      <el-button type="primary" @click="changePassword">确定</el-button>
    </template>
  </el-dialog>
</template>

<style scoped>
.layout-shell {
  min-height: 100vh;
  padding: 18px;
}

.shell-frame {
  min-height: calc(100vh - 36px);
  overflow: hidden;
  border: 1px solid rgba(124, 85, 53, 0.14);
  border-radius: 34px;
  background: rgba(255, 251, 246, 0.46);
  box-shadow: 0 30px 80px rgba(65, 43, 24, 0.12);
  backdrop-filter: blur(18px);
}

.shell-aside {
  display: flex;
  flex-direction: column;
  padding: 20px 18px;
  border-right: 1px solid rgba(124, 85, 53, 0.12);
  background:
    linear-gradient(180deg, rgba(70, 44, 25, 0.98), rgba(95, 64, 42, 0.94)),
    linear-gradient(180deg, rgba(255, 255, 255, 0.04), rgba(255, 255, 255, 0.02));
}

.brand-panel {
  padding: 20px 16px 22px;
  border: 1px solid rgba(255, 255, 255, 0.08);
  border-radius: 24px;
  color: #fff8f1;
  background: rgba(255, 255, 255, 0.06);
}

.brand-badge {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  padding: 6px 10px;
  border-radius: 999px;
  font-size: 12px;
  letter-spacing: 0.18em;
  text-transform: uppercase;
  color: rgba(255, 247, 236, 0.84);
  background: rgba(255, 255, 255, 0.1);
}

.brand-title {
  margin: 16px 0 10px;
  font-size: 30px;
  line-height: 1.12;
  font-family: 'STZhongsong', 'KaiTi', serif;
}

.brand-subtitle {
  margin: 0;
  font-size: 13px;
  line-height: 1.7;
  color: rgba(255, 247, 236, 0.72);
}

.menu-scroll {
  margin-top: 18px;
  flex: 1;
}

:deep(.shell-menu) {
  border-right: none;
  background: transparent;
  --el-menu-bg-color: transparent;
  --el-menu-text-color: rgba(255, 246, 235, 0.78);
  --el-menu-hover-bg-color: rgba(255, 255, 255, 0.08);
  --el-menu-active-color: #fff8f0;
}

:deep(.shell-menu .el-menu-item),
:deep(.shell-menu .el-sub-menu__title) {
  height: 48px;
  margin-bottom: 8px;
  border-radius: 16px;
}

:deep(.shell-menu .el-menu-item.is-active) {
  background: linear-gradient(135deg, rgba(196, 150, 98, 0.86), rgba(255, 228, 188, 0.38));
}

:deep(.shell-menu .el-sub-menu .el-menu-item) {
  min-width: 0;
  margin-top: 6px;
  margin-bottom: 0;
  padding-left: 42px !important;
  color: rgba(255, 246, 235, 0.72);
}

:deep(.shell-menu .el-sub-menu .el-menu-item.is-active) {
  color: #fffdf8;
  background: rgba(255, 255, 255, 0.12);
}

.shell-main {
  background: linear-gradient(180deg, rgba(255, 250, 244, 0.6), rgba(248, 241, 233, 0.8));
}

.shell-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 18px;
  height: auto;
  padding: 24px 28px 10px;
}

.header-copy {
  max-width: 720px;
}

.header-kicker {
  margin: 0 0 8px;
  font-size: 13px;
  letter-spacing: 0.16em;
  text-transform: uppercase;
  color: #8a735f;
}

.header-copy h2 {
  margin: 0;
  font-size: 32px;
  color: #2f241d;
}

.header-copy p:last-child {
  margin: 10px 0 0;
  font-size: 14px;
  line-height: 1.8;
  color: #766759;
}

.header-side {
  display: flex;
  flex-direction: column;
  align-items: flex-end;
  gap: 12px;
}

.user-panel {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 10px 14px;
  border: 1px solid rgba(124, 85, 53, 0.12);
  border-radius: 18px;
  background: rgba(255, 255, 255, 0.72);
}

.user-avatar {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 44px;
  height: 44px;
  border-radius: 50%;
  font-size: 18px;
  font-weight: 700;
  color: #fff9f2;
  background: linear-gradient(135deg, #7c5535, #b78658);
}

.user-meta {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.user-name {
  font-size: 15px;
  font-weight: 700;
  color: #2f241d;
}

.user-tags {
  display: flex;
  flex-wrap: wrap;
  gap: 6px;
}

.user-tag {
  padding: 4px 8px;
  border-radius: 999px;
  font-size: 12px;
  color: #725640;
  background: rgba(124, 85, 53, 0.08);
}

.header-buttons {
  display: flex;
  gap: 10px;
  flex-wrap: wrap;
}

.shell-content {
  padding: 12px 28px 28px;
}

@media (max-width: 1100px) {
  .shell-header {
    flex-direction: column;
    align-items: flex-start;
  }

  .header-side {
    width: 100%;
    align-items: flex-start;
  }
}

@media (max-width: 900px) {
  .layout-shell {
    padding: 10px;
  }

  .shell-frame {
    min-height: calc(100vh - 20px);
    border-radius: 26px;
  }

  .shell-content,
  .shell-header {
    padding-left: 18px;
    padding-right: 18px;
  }
}

@media (max-width: 768px) {
  .shell-frame {
    flex-direction: column;
  }

  .shell-aside {
    width: 100% !important;
    border-right: none;
    border-bottom: 1px solid rgba(124, 85, 53, 0.12);
  }

  .brand-title {
    font-size: 24px;
  }

  .header-copy h2 {
    font-size: 26px;
  }

  .header-buttons {
    width: 100%;
  }

  .header-buttons .el-button {
    flex: 1;
    min-width: 140px;
  }
}
</style>
