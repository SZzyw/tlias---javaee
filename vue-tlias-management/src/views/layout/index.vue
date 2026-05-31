<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import request from '@/utils/request'

const router = useRouter()
const passwordDialogVisible = ref(false)
const passwordForm = ref({ oldPassword: '', newPassword: '', confirmPassword: '' })
const passwordFormRef = ref()

const logout = () => {
  localStorage.removeItem('token')
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
  <div class="common-layout">
    <el-container>
      <el-header class="header">
        <span class="title">教育管理系统</span>
        <span class="right_tool">
          <el-button link type="warning" style="color: white;" @click="openPasswordDialog">
            <el-icon><EditPen /></el-icon> 修改密码
          </el-button>
          <span style="color: white;">&nbsp;&nbsp;&nbsp;|&nbsp;&nbsp;&nbsp;</span>
          <el-button link type="warning" style="color: white;" @click="logout">
            <el-icon><SwitchButton /></el-icon> 退出登录
          </el-button>
        </span>
      </el-header>

      <el-container>
        <el-aside width="200px" class="aside">
          <el-menu router>
            <el-menu-item index="/index">
              <el-icon><Promotion /></el-icon> 首页
            </el-menu-item>
            <el-sub-menu index="/manage">
              <template #title>
                <el-icon><Menu /></el-icon> 班级学员管理
              </template>
              <el-menu-item index="/clazz">
                <el-icon><HomeFilled /></el-icon>班级管理
              </el-menu-item>
              <el-menu-item index="/stu">
                <el-icon><UserFilled /></el-icon>学员管理
              </el-menu-item>
            </el-sub-menu>
            <el-sub-menu index="/system">
              <template #title>
                <el-icon><Tools /></el-icon>系统信息管理
              </template>
              <el-menu-item index="/dept">
                <el-icon><HelpFilled /></el-icon>部门管理
              </el-menu-item>
              <el-menu-item index="/emp">
                <el-icon><Avatar /></el-icon>员工管理
              </el-menu-item>
            </el-sub-menu>
            <el-sub-menu index="/report">
              <template #title>
                <el-icon><Histogram /></el-icon>数据统计管理
              </template>
              <el-menu-item index="/empReport">
                <el-icon><InfoFilled /></el-icon>员工信息统计
              </el-menu-item>
              <el-menu-item index="/stuReport">
                <el-icon><Share /></el-icon>学员信息统计
              </el-menu-item>
              <el-menu-item index="/log">
                <el-icon><Document /></el-icon>日志信息统计
              </el-menu-item>
            </el-sub-menu>
          </el-menu>
        </el-aside>

        <el-main>
          <router-view></router-view>
        </el-main>
      </el-container>

      <open-chat-widget url="/api/chat"></open-chat-widget>

    </el-container>
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
.header {
  background-image: linear-gradient(to right, #00547d, #007fa4, #00aaa0, #00d072, #a8eb12);
}
.title {
  color: white;
  font-size: 40px;
  font-family: 楷体;
  line-height: 60px;
  font-weight: bolder;
}
.right_tool{
  float: right;
  line-height: 60px;
}
a {
  color: white;
  text-decoration: none;
}
.aside {
  width: 220px;
  border-right: 1px solid #ccc;
  height: 730px;
}
</style>
