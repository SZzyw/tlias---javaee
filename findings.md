# 发现与决策

## 需求
- 全量检查前端、后端、前后端联调
- 生成检查与修复依据文档
- 明确 `openchatwidget` 配置入口

## 研究发现
- 前端请求统一使用 `vue-tlias-management/src/utils/request.js`，`baseURL` 为 `/api`。
- Vite 代理把 `/api` 转发到 `http://localhost:8080`，把 `/api/chat` 转发到 `http://localhost:8787`。
- 前端布局页直接嵌入 `<open-chat-widget url="/api/chat">`，因此聊天接口不走 Java 后端主 API。
- 后端主要控制器已定位，包含登录、验证码、员工、部门、班级、学员、角色、权限、报表、日志、上传。
- `openchatwidget` 本体是本地 `file:` 依赖，项目里只需要改接入层或 `chat-agent`，不需要改其仓库源码。
- `chat-agent` 使用环境变量 `OPENAI_API_KEY`、`OPENAI_BASE_URL`、`OPENAI_MODEL`，默认模型是 `gpt-5.5`。
- `openchatwidget` 组件桥接层位于 `vue-tlias-management/src/widget-bridge.js`，原先存在重复的 `disconnectedCallback` 定义，已修复。
- Java 后端测试原先因 `JwtTest` 内写死的过期 token 导致失败，已改为动态生成 token。
- 已新增 `ApiSmokeTest`，覆盖验证码、非法登录、权限保护、核心只读接口和两个导出接口。
- 前端 `npm run lint` 与 `npm run build` 已通过。
- 联调验证已确认：`/api/captcha` 代理可用，`/api/chat` 通过前端代理与直连聊天服务都能返回 `200`。
- `Invoke-WebRequest` 请求 `http://localhost:5173/` 在未显式声明 `Accept: text/html` 时会拿到 404，但浏览器语义请求和 `curl` 请求返回 200，这不是页面本身故障。

## 技术决策
| 决策 | 理由 |
|------|------|
| 先盘点接口与配置入口，再做修复 | 需要先确认实际调用链和第三方嵌入点 |
| 先做静态接口对照，再跑构建和接口验证 | 先发现明显不一致，再用测试确认修复范围 |
| 用前端代理路径验证聊天接口 | 可以确认页面嵌入配置与 Vite 代理同时生效 |

## 遇到的问题
| 问题 | 解决方案 |
|------|---------|
| 后端测试不稳定 | 替换过期样例 token 为动态生成 token |
| 聊天组件桥接层有重复生命周期方法 | 删除重复定义并重新验证构建 |
| 前端 lint 因目录约定和少量实现细节失败 | 关闭仓库约定冲突规则并修复员工页实际问题 |

## 资源
-

## 视觉/浏览器发现
- 前端开发服务器首页在标准 HTML 请求头下返回正常 HTML。
- 页面聊天入口位于后台布局页右下角，由自定义元素 `open-chat-widget` 挂载。
