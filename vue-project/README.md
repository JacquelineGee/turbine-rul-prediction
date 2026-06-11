# vue-project — 用户端前端

燃气轮机 RUL 预测平台的用户端 Web 应用。提供轮机知识科普、3D 交互式模型、数据集上传、RUL 预测与可视化、AI 智能分析等功能。

## 技术栈

| 类别 | 技术 |
|---|---|
| 框架 | Vue 3.5 (Composition API, MVVM 模式) |
| 构建 | Vite 6 + TypeScript |
| UI | Element Plus 2.9 + @element-plus/icons-vue |
| 状态管理 | Pinia 2.3 |
| 路由 | Vue Router 4.5（history 模式） |
| HTTP | Axios 1.7（JWT 拦截器） |
| 图表 | ECharts 5.6 + vue-echarts 7.0 |
| 3D 渲染 | Three.js 0.174（WebGL） |
| 动画 | GSAP 3.12 + Lenis 平滑滚动 |
| Markdown | marked 15 + highlight.js |
| PDF | html2pdf.js 0.10 |

## 项目结构

```
vue-project/
├── index.html                         # 入口 HTML
├── vite.config.js                     # Vite 配置（代理、@ 别名、GLB 资源支持）
├── package.json
└── src/
    ├── main.js                        # 应用入口
    ├── App.vue                        # 根组件
    ├── router/index.js                # 18 条路由 + JWT + 权限守卫
    ├── store/
    │   ├── user.ts                    # 用户信息 Store
    │   └── userStore.ts              # 登录状态管理
    ├── utils/axios.js                # Axios 实例（baseURL: localhost:8080）
    ├── components/                    # 公共组件
    │   ├── NavMenu.vue               # 顶部导航
    │   ├── ShowCard.vue              # 展示卡片
    │   ├── HoverCard.vue             # 悬停卡片
    │   ├── ImageCropper.vue          # 头像裁剪
    │   └── icons/                    # SVG 图标
    ├── views/
    │   ├── page/                     # 页面级视图
    │   │   ├── HomePage.vue              # 首页（轮播 + 产品卡片）
    │   │   ├── LoginPage.vue             # 登录 / 注册
    │   │   ├── DataGetPage.vue           # 数据集上传管理
    │   │   ├── predict.vue               # 预测结果表格
    │   │   ├── predictionDetail.vue      # 预测详情（ECharts 可视化）
    │   │   ├── AIChatPage.vue            # DeepSeek AI 对话
    │   │   ├── ModelPage.vue             # 模型选择
    │   │   ├── TurbineStructure.vue      # 3D 发动机结构
    │   │   ├── ServicePage.vue           # 产品服务介绍
    │   │   ├── UserInfoPage.vue          # 个人信息管理
    │   │   ├── carousel/
    │   │   │   ├── Turbine.vue           # 轮机介绍
    │   │   │   ├── Reason.vue            # 故障原因
    │   │   │   ├── Methods.vue           # 预测方法说明
    │   │   │   └── BaseOnDL.vue          # 深度学习详解
    │   │   └── model/
    │   │       ├── model1.vue            # CNN+LSTM 模型预测
    │   │       ├── model2.vue            # CNN+GRU 模型预测
    │   │       └── model3.vue            # Wiener+LSTM 混合模型预测
    │   └── component/                # 视图级组件
    │       ├── GLBViewer.vue         # Three.js 3D 查看器
    │       ├── EChart.vue            # ECharts 封装
    │       ├── DeepSeekApiComponent.vue  # AI 对话组件
    │       ├── GetPDF.vue            # PDF 报告导出
    │       └── NavMenu.vue           # 导航菜单
    └── assets/                       # 静态资源（图片、GLB/GLTF 3D 模型）
```

## 路由一览

| 路径 | 页面 | 需登录 | 所需权限 |
|---|---|---|---|
| `/` | 首页 | — | — |
| `/login` | 登录 / 注册 | — | — |
| `/about` | 关于 | — | — |
| `/model` | 模型选择 | — | — |
| `/structure` | 3D 轮机结构 | — | — |
| `/TurbineDetail` | 3D 详细模型 | — | — |
| `/turbine` | 轮机介绍 | — | — |
| `/reason` | 故障原因 | — | — |
| `/methods` | 预测方法 | — | — |
| `/BaseOnDL` | 深度学习方法 | — | — |
| `/service` | 产品服务 | — | — |
| `/data-collecting` | 数据集管理 | ✓ | `dataset` |
| `/data-show` | 预测数据表格 | ✓ | `HPd` |
| `/predictionDetail/:id` | 预测详情 | ✓ | — |
| `/model1` | CNN+LSTM 模型 | ✓ | `model1` |
| `/model2` | CNN+GRU 模型 | ✓ | `model2` |
| `/model3` | Wiener+LSTM | ✓ | `model3` |
| `/AI-Chat` | AI 对话 | ✓ | — |
| `/user-info` | 个人信息 | ✓ | — |

权限标志由后端登录接口返回，前端存入 `sessionStorage`（`dataset`, `HPd`, `model1`, `model2`, `model3`）。

## 启动

```bash
cd vue-project
npm install
npm run dev        # 开发模式 → http://localhost:537
npm run build      # 生产构建 → dist/
npm run preview    # 预览生产构建
```

## 依赖的后端服务

| 服务 | 地址 | 用途 |
|---|---|---|
| Spring Boot | `http://localhost:8080` | 用户认证、数据 CRUD、文件上传 |
| Flask ML | `http://localhost:5000` | 模型推理（`/load-dataset`、`/get-rul-data`） |

> Axios 实例 `src/utils/axios.js` 中 `baseURL` 配置为 `http://localhost:8080/`。部分页面（AIChatPage、DataGetPage、model1/2/3 等）直接硬编码了后端地址。部署到非本地环境时需统一修改。

## 开发代理

`vite.config.js` 中配置代理：

```js
server: {
  proxy: {
    '/api': {
      target: 'http://localhost:8080',
      changeOrigin: true,
      rewrite: (path) => path.replace(/^\/api/, ''),
    },
  },
}
```

## 3D 模型说明

3D 燃气轮机模型通过 SolidWorks → Blender → Three.js 流水线制作。模型文件（`.glb`/`.gltf`）位于 `src/assets/`，Vite 已配置 `assetsInclude: ['**/*.glb']`。通过 Three.js 的 `GLTFLoader` 加载渲染，支持：

- 360° 旋转、缩放、平移
- 爆炸图：零部件按装配关系分离展示
- 动画过渡：GSAP 驱动的平滑相机运动

## 注意事项

- 页面中的图片资源托管于 Aliyun OSS（`vue-project-image.oss-cn-shanghai.aliyuncs.com`），纯本地开发时需下载至本地 `src/assets/` 并替换引用
- 模型预测功能（model1/2/3）依赖 Flask ML 服务先启动
- AI 对话功能依赖后端代理 DeepSeek API，需有效 API Key
