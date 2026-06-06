import { createRouter, createWebHistory } from 'vue-router'

import IndexView from '@/views/index/index.vue'
import ClazzView from '@/views/clazz/index.vue'
import DeptView from '@/views/dept/index.vue'
import EmpView from '@/views/emp/index.vue'
import LogView from '@/views/log/index.vue'
import RoleView from '@/views/role/index.vue'
import StuView from '@/views/stu/index.vue'
import EmpReportView from '@/views/report/emp/index.vue'
import StuReportView from '@/views/report/stu/index.vue'
import LayoutView from '@/views/layout/index.vue'
import LoginView from '@/views/login/index.vue'
import { clearAuth, getLoginInfo, getToken, hasPermission } from '@/utils/auth'
import { getFirstAccessiblePath } from '@/utils/menu'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
     path: '/',
     name: '',
     component: LayoutView,
     redirect: '/index',
     children: [
      {path: 'index', name: 'index', component: IndexView, meta: { permission: 'dashboard:view' }},
      {path: 'clazz', name: 'clazz', component: ClazzView, meta: { permission: 'clazz:view' }},
      {path: 'stu', name: 'stu', component: StuView, meta: { permission: 'student:view' }},
      {path: 'dept', name: 'dept', component: DeptView, meta: { permission: 'dept:view' }},
      {path: 'emp', name: 'emp', component: EmpView, meta: { permission: 'emp:view' }},
      {path: 'role', name: 'role', component: RoleView, meta: { permission: 'role:view' }},
      {path: 'log', name: 'log', component: LogView, meta: { permission: 'log:view' }},
      {path: 'empReport', name: 'empReport', component: EmpReportView, meta: { permission: 'report:emp' }},
      {path: 'stuReport', name: 'stuReport', component: StuReportView, meta: { permission: 'report:stu' }},
     ]
    },
    {path: '/login', name: 'login', component: LoginView}
  ]
})

router.beforeEach((to) => {
  const token = getToken()
  const loginInfo = getLoginInfo()

  if (to.path !== '/login' && !token) {
    return '/login'
  }

  if (to.path === '/login' && token && loginInfo) {
    return getFirstAccessiblePath(loginInfo.permissions)
  }

  if (token && !loginInfo) {
    clearAuth()
    return '/login'
  }

  const permission = to.meta?.permission
  if (permission && !hasPermission(permission)) {
    return getFirstAccessiblePath(loginInfo?.permissions || [])
  }
})

export default router
