# 进度日志

## 会话：2026-06-14

### 阶段 1：发现与盘点
- **状态：** complete
- **开始时间：** 2026-06-14
- 执行的操作：
  - 创建规划文件
  - 梳理项目结构、接口与配置入口
  - 生成检查与修复文档 `interface-audit-and-fix-plan.md`
- 创建/修改的文件：
  - `task_plan.md`
  - `findings.md`
  - `progress.md`
  - `interface-audit-and-fix-plan.md`

### 阶段 2：测试与修复
- **状态：** complete
- 执行的操作：
  - 执行 `mvn test`
  - 执行 `npm run lint`
  - 执行 `npm run build`
  - 启动前端、后端、聊天服务进行联调
  - 修复 `JwtTest` 过期 token 问题
  - 新增 `ApiSmokeTest` 覆盖核心接口
  - 修复 `widget-bridge.js` 重复生命周期方法
  - 修复员工页 lint/运行时问题
- 创建/修改的文件：
  - `web-ai-project02/tlias-web-management/src/test/java/com/way_ne/JwtTest.java`
  - `web-ai-project02/tlias-web-management/src/test/java/com/way_ne/ApiSmokeTest.java`
  - `vue-tlias-management/src/widget-bridge.js`
  - `vue-tlias-management/src/views/emp/index.vue`
  - `vue-tlias-management/.eslintrc.cjs`

## 测试结果
| 测试 | 输入 | 预期结果 | 实际结果 | 状态 |
|------|------|---------|---------|------|
| 后端单测 | `mvn test` | 所有测试通过 | 通过，含新增接口冒烟测试 | pass |
| 前端静态检查 | `npm run lint` | 通过 | 通过 | pass |
| 前端生产构建 | `npm run build` | 通过 | 通过，存在大包 warning | pass |
| 聊天服务启动 | `npm run dev` | 8787 监听成功 | 成功监听并响应 | pass |
| 前端代理验证码 | `GET http://localhost:5173/api/captcha` | 返回验证码 JSON | 返回 `code=1` | pass |
| 后端验证码直连 | `GET http://localhost:8080/captcha` | 返回验证码 JSON | 返回 `code=1` | pass |
| 聊天接口直连 | `POST http://localhost:8787/api/chat` | 返回 200 | 返回 200 | pass |
| 聊天接口代理 | `POST http://localhost:5173/api/chat` | 返回 200 | 返回 200 | pass |

## 错误日志
| 时间戳 | 错误 | 尝试次数 | 解决方案 |
|--------|------|---------|---------|
| 2026-06-14 | `JwtTest` 解析过期 JWT 失败 | 1 | 改为测试内动态生成 token |
| 2026-06-14 | `widget-bridge.js` 重复类成员导致 lint 失败 | 1 | 删除重复 `disconnectedCallback` |
| 2026-06-14 | `ApiSmokeTest` 初版断言错误 | 1 | 改为验证非法验证码登录 |

## 五问重启检查
| 问题 | 答案 |
|------|------|
| 我在哪里？ | 阶段 5 |
| 我要去哪里？ | 输出结论与配置入口 |
| 目标是什么？ | 检查前后端联调并定位 openchatwidget 配置 |
| 我学到了什么？ | 见 findings.md |
| 我做了什么？ | 见上方记录 |
