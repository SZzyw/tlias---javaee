# 接口、前后端联调检查与修复文档

## 1. 目标

- 全量盘点本项目前端、后端与聊天接入的接口。
- 执行可落地的后端、前端、联调测试。
- 记录发现的问题、复现方式、修复状态与回归结果。
- 明确 `openchatwidget` 的 URL、密钥、模型配置修改位置。

## 2. 项目结构

### 2.1 主项目

- 前端：`vue-tlias-management`
- Java 后端：`web-ai-project02/tlias-web-management`
- 聊天服务：`web-ai-project02/chat-agent`

### 2.2 非检查对象

- `openchatwidget` 仓库本体不作为本次业务修复对象。
- 但其在本项目中的接入层、配置入口、代理路径属于检查范围。

## 3. 前后端调用关系

### 3.1 前端统一请求入口

- 文件：`vue-tlias-management/src/utils/request.js`
- 业务接口 `baseURL`：`/api`

### 3.2 Vite 代理

- 文件：`vue-tlias-management/vite.config.js`
- `/api` -> `http://localhost:8080`，并去掉 `/api` 前缀
- `/api/chat` -> `http://localhost:8787`
- `/head` -> `http://localhost:8080`

### 3.3 聊天组件挂载点

- 文件：`vue-tlias-management/src/views/layout/index.vue`
- 标签：`<open-chat-widget url="/api/chat"></open-chat-widget>`

### 3.4 聊天组件桥接层

- 文件：`vue-tlias-management/src/widget-bridge.js`
- 作用：把自定义元素 `open-chat-widget` 映射到 `@openchatwidget/sdk`

## 4. 后端接口盘点

### 4.1 登录与验证码

- `GET /captcha`
- `POST /login`

### 4.2 部门

- `GET /depts`
- `POST /depts`
- `GET /depts/{id}`
- `PUT /depts`
- `DELETE /depts?id=`

### 4.3 员工

- `GET /emps`
- `POST /emps`
- `GET /emps/{id}`
- `PUT /emps`
- `DELETE /emps?ids=`
- `GET /emps/list`
- `PUT /emps/password`

### 4.4 班级

- `GET /clazzs`
- `POST /clazzs`
- `GET /clazzs/{id}`
- `PUT /clazzs`
- `DELETE /clazzs/{id}`
- `GET /clazzs/list`

### 4.5 学员

- `GET /students`
- `POST /students`
- `GET /students/{id}`
- `PUT /students`
- `DELETE /students/{ids}`
- `PUT /students/violation/{id}/{score}`

### 4.6 角色与权限

- `GET /roles`
- `POST /roles`
- `GET /roles/{id}`
- `PUT /roles`
- `DELETE /roles?ids=`
- `GET /roles/{id}/permissions`
- `PUT /roles/{id}/permissions`
- `GET /permissions`

### 4.7 报表与首页

- `GET /report/dashboard`
- `GET /report/empJobData`
- `GET /report/empGenderData`
- `GET /report/empEntryTrend`
- `GET /report/studentDegreeData`
- `GET /report/studentCountData`
- `GET /report/studentEntryTrend`
- `GET /report/violationRank`
- `GET /report/exportEmp`
- `GET /report/exportStudent`

### 4.8 日志与上传

- `GET /log`
- `POST /upload`

### 4.9 聊天

- `POST /api/chat`

## 5. 前端页面盘点

- 登录页：`src/views/login/index.vue`
- 首页：`src/views/index/index.vue`
- 部门页：`src/views/dept/index.vue`
- 员工页：`src/views/emp/index.vue`
- 班级页：`src/views/clazz/index.vue`
- 学员页：`src/views/stu/index.vue`
- 角色页：`src/views/role/index.vue`
- 日志页：`src/views/log/index.vue`
- 员工统计：`src/views/report/emp/index.vue`
- 学员统计：`src/views/report/stu/index.vue`

## 6. 测试计划

### 6.1 后端测试

- 执行 `mvn test`
- 执行后端启动验证
- 验证核心接口是否可访问
- 验证登录鉴权、权限拦截、导出接口、上传接口

### 6.2 前端测试

- 执行 `npm run build`
- 验证各页面编译是否通过
- 验证聊天组件接入层是否能正常打包

### 6.3 联调测试

- 启动 Java 后端、聊天服务、前端
- 验证登录页验证码与登录流程
- 验证首页、部门、员工、班级、学员、角色、日志、统计页的核心读取流程
- 验证聊天接口代理与组件调用链

## 7. 已确认配置入口

### 7.1 openchatwidget 的 URL

- 页面挂载位置：`vue-tlias-management/src/views/layout/index.vue`
- 当前值：`url="/api/chat"`

### 7.2 openchatwidget 的实际请求 URL 默认值

- 桥接层：`vue-tlias-management/src/widget-bridge.js`
- 默认值：`this.getAttribute('url') || '/api/chat'`

### 7.3 OpenAI 密钥

- 聊天服务：`web-ai-project02/chat-agent/src/agent.ts`
- 读取环境变量：`OPENAI_API_KEY`

### 7.4 OpenAI Base URL

- 聊天服务：`web-ai-project02/chat-agent/src/agent.ts`
- 读取环境变量：`OPENAI_BASE_URL`

### 7.5 调用模型

- 聊天服务：`web-ai-project02/chat-agent/src/agent.ts`
- 读取环境变量：`OPENAI_MODEL`
- 默认值：`gpt-5.5`

## 8. 当前静态检查疑点

### 8.1 前端聊天桥接层重复方法定义

- 文件：`vue-tlias-management/src/widget-bridge.js`
- 现象：`disconnectedCallback` 被定义了两次
- 风险：后定义覆盖前定义，前面的调试或卸载逻辑不可信
- 状态：已修复

### 8.2 需要通过构建与联调进一步确认的问题

- 页面字段与后端返回字段是否完全一致
- 登录权限链在真实账号下的完整页面操作是否全部通过
- 聊天服务在未配置密钥时的报错呈现是否合理

## 9. 测试执行记录

### 9.1 自动化测试

- `mvn test` 通过
- `JwtTest` 已改为动态生成 token
- 新增 `ApiSmokeTest`，已覆盖：
  - `GET /captcha`
  - 非法验证码登录
  - 未授权访问 `/report/dashboard`
  - 携带管理员 token 访问 `depts / emps/list / clazzs/list / students / roles / permissions / report/dashboard / log`
  - `exportEmp / exportStudent` 导出响应头

### 9.2 构建测试

- `vue-tlias-management` 执行 `npm run lint` 通过
- `vue-tlias-management` 执行 `npm run build` 通过
- `chat-agent` 无 `build` 脚本，但 `npm run dev` 可正常启动监听

### 9.3 联调测试

- 前端开发服务、Java 后端、聊天服务均已启动成功
- `GET http://localhost:5173/api/captcha` 返回成功
- `GET http://localhost:8080/captcha` 返回成功
- `POST http://localhost:8787/api/chat` 返回 `200`
- `POST http://localhost:5173/api/chat` 返回 `200`
- `http://localhost:5173/` 在普通脚本请求下可能返回 `404`，但浏览器语义请求与 `curl` 请求返回 `200 HTML`，判断为请求头差异，不是页面故障

## 10. 修复记录

- 修复 `web-ai-project02/tlias-web-management/src/test/java/com/way_ne/JwtTest.java`
  - 去掉过期硬编码 JWT，改为测试内生成并解析
- 新增 `web-ai-project02/tlias-web-management/src/test/java/com/way_ne/ApiSmokeTest.java`
  - 补后端接口与权限冒烟测试
- 修复 `vue-tlias-management/src/widget-bridge.js`
  - 删除重复 `disconnectedCallback`
- 修复 `vue-tlias-management/src/views/emp/index.vue`
  - 去掉未使用参数
  - 为 `v-for` 行补 `:key`
- 调整 `vue-tlias-management/.eslintrc.cjs`
  - 关闭 `vue/multi-word-component-names`，适配当前 `index.vue` 目录约定

## 11. 当前结论

- 后端主要接口从代码和自动化冒烟测试看，读路径与导出路径可用。
- 前端构建、lint、聊天组件接入层已恢复到可回归状态。
- 前后端代理链路可用，聊天接口代理链路可用。
- 本轮没有改动 `openchatwidget` 仓库本体，只改了项目内接入层和测试。

## 12. 剩余风险

- 没有拿到真实验证码值与现成演示账号，因此未完成“浏览器里实际登录后点遍每个页面”的全流程人工回归。
- 聊天接口当前只验证到 HTTP 链路返回 `200`；若环境变量里的 OpenAI 密钥、Base URL 或模型不可用，具体对话内容仍会受外部模型服务影响。
- 前端构建仍有大包 warning，但不影响当前功能。
