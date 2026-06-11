# vue-project-demo — 管理端前端

燃气轮机 RUL 预测系统的后台管理面板。提供用户管理、角色权限分配、数据集管理、预测记录管理和操作日志审计功能。

## 技术栈

| 类别 | 技术 |
|---|---|
| 框架 | Vue 2.6 |
| 构建 | Vue CLI 5 |
| UI | Element UI 2.15 |
| 路由 | Vue Router 3.5 |
| HTTP | Axios（baseURL: `localhost:8080`） |
| 图表 | ECharts 5.6 |

## 项目结构

```
vue-project-demo/
├── vue.config.js              # Vue CLI 配置（devServer 端口 7000）
├── package.json
└── src/
    ├── main.js                # 入口：注册 Vue / ElementUI / Axios / 全局组件
    ├── App.vue                # 根组件
    ├── router/index.js        # 路由 + JWT 守卫 + 角色权限校验
    ├── components/
    │   ├── AsideView.vue      # 侧边栏导航
    │   └── HeaderView.vue     # 顶部栏
    ├── views/
    │   ├── LoginView.vue          # 登录
    │   ├── HomeView.vue           # 仪表盘（新增用户、登录次数、模型使用统计）
    │   ├── AboutView.vue          # 关于
    │   ├── DatasetView.vue        # 数据集管理（上传、编辑、删除）
    │   ├── PredictDataView.vue    # 预测数据管理（查看、删除）
    │   ├── LogView.vue            # 操作日志审计
    │   └── power/                 # 权限管理模块
    │       ├── UserView.vue       # 用户 CRUD + 头像上传
    │       ├── RoleView.vue       # 角色 CRUD + 权限分配
    │       ├── FunctionView.vue   # 功能权限定义
    │       └── OpView.vue         # 操作管理
    └── assets/
        ├── logo.png
        └── style/global.css
```

## 路由一览

| 路径 | 页面 | 需 admin 角色 |
|---|---|---|
| `/login` | 登录 | — |
| `/home` | 仪表盘 | ✓ |
| `/about` | 关于 | ✓ |
| `/dataset` | 数据集管理 | ✓ |
| `/predict` | 预测数据管理 | ✓ |
| `/log` | 操作日志 | ✓ |
| `/power/user` | 用户管理 | ✓ |
| `/power/role` | 角色管理 | ✓ |
| `/power/function` | 权限管理 | ✓ |
| `/power/op` | 操作管理 | ✓ |

路由守卫校验 JWT token 和 admin 角色。

## 功能说明

| 模块 | 功能 |
|---|---|
| 仪表盘 | 新增用户数、用户总数、模型使用统计（饼图）、登录次数趋势（折线图） |
| 用户管理 | 查询、新增、编辑、删除用户，分配角色，上传头像 |
| 角色管理 | 查询、新增、编辑、删除角色，为角色分配功能权限 |
| 权限管理 | 功能权限的新增、编辑、删除 |
| 数据集管理 | 数据集上传（支持文件上传至后端）、查询、编辑、删除 |
| 预测数据 | 历史预测记录的查看与删除 |
| 操作日志 | 只读查询系统操作日志，支持多条件筛选 |

## 启动

```bash
cd vue-project-demo
npm install
npm run serve     # 开发模式 → http://localhost:7000
npm run build     # 生产构建 → dist/
npm run lint      # 代码检查
```

## 后端依赖

Axios 实例在 `src/main.js` 中配置 `axios.defaults.baseURL='http://localhost:8080'`，所有 API 请求发往 Spring Boot 后端。启动前需确保后端已运行。

部分页面（DatasetView、PredictDataView、UserView）使用了硬编码的 `http://127.0.0.1:8080` 地址，部署时需统一修改。

## 与用户端的关系

管理端与用户端（vue-project）共享同一 Spring Boot 后端，通过 JWT 中的角色信息区分管理员与普通用户。管理端路由均需要 admin 角色才能访问。
