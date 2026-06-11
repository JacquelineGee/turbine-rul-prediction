# demo — Spring Boot 后端服务

燃气轮机 RUL 预测系统的后端 RESTful API 服务。采用 Controller-Service-Mapper 分层架构，处理用户认证、数据 CRUD、文件上传、AI 对话代理等业务逻辑。

## 技术栈

| 类别 | 技术 |
|---|---|
| 框架 | Spring Boot 2.5 |
| 语言 | Java（编译 JDK 9+，运行 1.8） |
| 构建 | Maven |
| ORM | MyBatis-Plus 3.4 + PageHelper 1.4 |
| 数据库 | MySQL 5.7 |
| 缓存 | Redis 7.0（Lettuce 客户端） |
| 认证 | JWT（jjwt 0.9.1） |
| API 文档 | Swagger 2（springfox 2.9） |
| 文件存储 | Aliyun OSS 3.15 |
| 工具 | Lombok, Fastjson, Gson, Apache POI, Commons Lang3, Commons Codec |

## 项目结构

```
demo/
├── pom.xml
└── src/main/
    ├── java/com/example/demo/
    │   ├── DemoApplication.java              # Spring Boot 启动类
    │   ├── config/
    │   │   ├── CorsConfig.java               # CORS 跨域
    │   │   ├── SwaggerConfig.java            # Swagger 文档
    │   │   └── AsyncConfig.java              # 异步任务
    │   ├── controller/                       # 控制器层（路由 + 参数校验）
    │   │   ├── ULoginController.java         # 登录 / 注册
    │   │   ├── UController.java              # 用户管理
    │   │   ├── GetUserController.java        # 用户信息查询
    │   │   ├── RoleController.java           # 角色管理
    │   │   ├── DatasetController.java        # 数据集管理
    │   │   ├── PredictionController.java     # 预测数据管理
    │   │   ├── ChatController.java           # DeepSeek AI 对话代理
    │   │   ├── UploadController.java         # 文件上传（OSS）
    │   │   └── OperationLogController.java   # 操作日志
    │   ├── service/                          # 业务接口
    │   │   └── impl/                         # 业务实现
    │   ├── mapper/                           # MyBatis Mapper 接口
    │   ├── pojo/                             # 实体类 / DTO
    │   └── utils/
    │       ├── JwtUtils.java                 # JWT 生成与校验
    │       ├── AliOSSUtils.java              # OSS 上传工具
    │       ├── HttpContextUtils.java         # HTTP 上下文
    │       └── IPUtils.java                  # IP 获取
    └── resources/
        ├── application.yml                   # 主配置
        └── mapper/                           # MyBatis XML 映射
```

## 分层架构

```
Controller 层 → 路由分发、参数校验、权限验证
    ↓
Service 层   → 业务逻辑编排、事务管理、数据校验
    ↓
Mapper 层    → 数据持久化、SQL 执行（MyBatis-Plus）
    ↓
MySQL / Redis
```

## API 概览

Swagger 文档：http://localhost:8080/swagger-ui.html

### 认证模块
| 端点 | 方法 | 说明 | 需认证 |
|---|---|---|---|
| `/login` | POST | 用户登录，返回 JWT | — |
| `/register` | POST | 用户注册 | — |

### 用户管理
| 端点 | 方法 | 说明 | 需认证 |
|---|---|---|---|
| `/upu` | POST | 更新用户信息 | ✓ |
| `/deleteById` | POST | 删除用户 | ✓ |

### 角色权限管理
| 端点 | 方法 | 说明 |
|---|---|---|
| `/roles` | GET | 获取角色列表 |
| `/roles/**` | POST/PUT/DELETE | 角色 CRUD |
| `/cas` | GET | 获取功能权限列表 |

### 数据集管理
| 端点 | 方法 | 说明 |
|---|---|---|
| `/dataSet` | GET | 获取数据集列表 |
| `/addDataset` | POST | 新增数据集 |
| `/updDataset` | POST | 更新数据集 |
| `/deleteById1/{id}` | DELETE | 删除数据集 |
| `/selectDataset` | POST | 条件查询数据集 |
| `/upload1` | POST | 上传数据集文件 |

### 预测数据
| 端点 | 方法 | 说明 |
|---|---|---|
| `/predictions` | GET | 获取全部预测 |
| `/predictionByUserId/{id}` | POST | 按用户查预测 |
| `/prediction/{id}` | POST | 获取预测详情 |
| `/save-prediction` | POST | 保存预测结果 |
| `/deleteById2/{id}` | DELETE | 删除预测 |
| `/IfSelectPrediction` | POST | 条件查询预测 |

### AI 对话
| 端点 | 方法 | 说明 |
|---|---|---|
| `/api/chat/conversations` | GET | 获取对话列表 |
| `/api/chat/conversation/{id}` | GET/DELETE | 获取/删除对话 |
| `/api/chat/message` | POST | 发送消息（SSE 流式） |
| `/api/chat/messageTemp` | POST | 临时消息 |

### 文件上传
| 端点 | 方法 | 说明 |
|---|---|---|
| `/upload` | POST | 头像上传至 OSS |
| `/upload-pdf` | POST | PDF 上传 |

### 系统日志
| 端点 | 方法 | 说明 |
|---|---|---|
| `/api/system/log/NewestUser` | POST | 新增用户统计 |
| `/api/system/log/UserNum` | POST | 用户总数 |
| `/api/system/log/modelUseTimes` | POST | 模型使用次数 |
| `/api/system/log/LoginTimes` | POST | 登录次数趋势 |

## 本地配置

项目提供了配置模板文件 `application.example.yml`，首次使用时复制并填入本地配置：

```bash
cp src/main/resources/application.example.yml src/main/resources/application.yml
```

然后编辑 `application.yml`，将数据库和 Redis 改为本地地址：

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/powersystem?...
    username: root
    password: 本地MySQL密码
  redis:
    host: localhost
    port: 6379
    password:
```

同理填入你的 DeepSeek API Key 和 Aliyun OSS 凭据。

> **注意**：`application.yml` 已加入 `.gitignore`，不会被提交到仓库。请勿将真实凭据写入 `application.example.yml`。

## 数据库

数据库名：`powersystem`，包含以下 12 张表：

| 表名 | 说明 | 关键字段 |
|---|---|---|
| `user` | 用户表 | id, username, nickname, phone, email, password, avatar, gender |
| `role` | 角色表 | id, role_name, role_desc |
| `function` | 权限表 | id, function_name, function_desc |
| `user_role` | 用户-角色关联 | user_id, role_id |
| `role_function` | 角色-权限关联 | role_id, function_id |
| `dataset` | 数据集表 | dataset_id, dataset_name, file_path, user_id, upload_time |
| `model_library` | 模型库表 | model_id, model_name, model_type, model_path |
| `prediction` | 预测记录表 | predict_id, user_id, dataset_id, model_id, predict_time |
| `prediction_report` | 预测报表表 | report_id, predict_id, report_content |
| `conversation` | 对话记录表 | conversation_id, user_id, title, create_time |
| `chat_message` | 对话消息表 | message_id, conversation_id, role, content, timestamp |
| `operation_log` | 操作日志表 | log_id, user_id, username, operation, ip, time |

## 存储过程

项目中定义了以下存储过程，用于批量操作和权限管理：

| 存储过程 | 功能 |
|---|---|
| `delete_roles_by_ids` | 批量删除角色（逗号分隔 ID 字符串） |
| `delete_users_by_ids` | 批量删除用户（逗号分隔 ID 字符串） |
| `insert_user_roles` | 更新用户角色（JSON 数组参数） |
| `update_role_powers` | 更新角色权限（逗号分隔 ID 字符串） |

## 认证流程

```
① 前端 POST /login { username, password }
② 后端验证 → 生成 JWT → 返回 token + 权限标志
③ 前端存储 token（localStorage） + 权限（sessionStorage）
④ 后续请求 Axios 拦截器自动附加 Authorization: Bearer <token>
⑤ 后端 LoginCheckFilter 校验 token 有效性
```

## 启动

```bash
cd demo
mvn clean install -DskipTests
mvn spring-boot:run     # → http://localhost:8080
```

启动前需确保 MySQL 数据库 `powersystem` 已创建，Redis 已启动。
