# Punctual Life Platform（准时达生活平台）技术说明文档

## 1. 项目概述

准时达生活平台是一个基于 **Spring Cloud 微服务架构** 的外卖点餐平台，包含管理后台 Web 端（Vue.js 3 + Vite）和微信小程序客户端两大前端。后端采用 Maven 多模块管理，支持服务注册发现、动态路由、分布式缓存、文件上传、微信支付、业务报表及数据分析等功能。

- **项目名称**：sky-take-out2
- **Group ID**：com.sky
- **版本**：1.0-SNAPSHOT
- **Java 版本**：17
- **构建工具**：Maven（多模块）

---

## 2. 技术栈

### 2.1 后端框架

| 技术 | 版本 | 用途 |
|---|---|---|
| Spring Boot | 2.7.3 | 应用基础框架 |
| Spring Cloud | 2021.0.3 | 微服务治理（Gateway、OpenFeign、LoadBalancer） |
| Spring Cloud Alibaba | 2021.0.4.0 | Nacos 服务注册与配置中心 |
| MyBatis Spring Boot | 2.2.0 | ORM 持久层框架 |
| Alibaba Druid | 1.2.1 | 数据库连接池 |
| PageHelper | 1.3.0 | MyBatis 分页插件 |
| Knife4j | 3.0.2 | API 文档（Swagger UI） |

### 2.2 中间件与基础设施

| 组件 | 版本/地址 | 用途 |
|---|---|---|
| Nacos | 192.168.233.145:8848 | 服务注册发现 + 配置管理 |
| Redis | 192.168.233.145:6380 (db 2) | 分布式缓存 |
| MySQL | 127.0.0.1:3306 (sky_take_out) | 关系型数据库 |

### 2.3 第三方集成

| 功能 | 技术方案 |
|---|---|
| 文件存储 | 阿里云 OSS (aliyun-oss-spring-boot-starter) |
| 微信登录 | 微信小程序 code2session 接口 |
| 微信支付 | wechatpay-apache-httpclient 0.4.8 |
| 认证鉴权 | JJWT 0.9.1（HS256 对称加密） |
| 报表导出 | Apache POI 3.16（Excel 导出） |
| 实时推送 | WebSocket（订单状态通知） |
| 切面编程 | AspectJ（自动填充审计字段） |

### 2.4 前端

| 端 | 技术栈 |
|---|---|
| 管理后台 | Vue.js 3 + Vite |
| 客户端 | 微信小程序原生开发 |

---

## 3. 系统架构

### 3.1 模块总览

```
punctual-life-platform (parent pom)
├── gateway-service       # API 网关（单入口，鉴权，动态路由）
├── sky-pojo              # 共享实体类（Entity、DTO、VO）
├── takeout-common        # 公共工具（拦截器、上下文、OSS、微信、异常）
├── takeout-api           # Feign 远程调用接口
├── common-service        # 文件上传服务（阿里云 OSS）
├── employee-service      # 员工/管理员服务
├── category-service      # 菜品/套餐分类服务
├── dish-service          # 菜品管理服务
├── setmeal-service       # 套餐管理服务
├── order-service         # 订单服务（含 WebSocket 推送）
├── shop-service          # 店铺状态 + 购物车服务
├── user-service          # 微信用户 + 地址簿服务
├── report-service        # 业务报表 + Excel 导出服务
├── ai-service            # 业务数据分析服务
└── demo/                 # 示例模块（demo1, demo2, demo3）
```

### 3.2 服务端口与 Nacos 注册

| 服务模块 | 端口 | Nacos 服务名 |
|---|---|---|
| gateway-service | 8080 | gateway-service |
| category-service | 8081 | category-service |
| dish-service | 8082 | dish-service |
| setmeal-service | 8083 | setmeal-service |
| order-service | 8084 | order-service |
| user-service | 8085 | user-service |
| employee-service | 8086 | employee-service |
| shop-service | 8087 | shop-service |
| common-service | 8088 | common-service |
| ai-service | 8089 | ai-service |
| report-service | 8090 | report-service |

### 3.3 请求数据流

```
微信小程序 / 管理后台(Vue3)
        │
        ▼
┌─────────────────────────────────┐
│  gateway-service (8080)         │
│  - AuthGlobalFilter JWT 鉴权   │
│  - 从 Nacos 拉取动态路由配置    │
│  - 转发请求至目标服务           │
└─────────────────────────────────┘
        │
        ▼
┌─────────────────────────────────┐
│  服务内拦截器                    │
│  JwtTokenAdminInterceptor       │
│  JwtTokenUserInterceptor        │
│  - 从 Header 提取用户/员工 ID   │
│  - 存入 BaseContext (ThreadLocal)│
└─────────────────────────────────┘
        │
        ▼
┌─────────────────────────────────┐
│  Controller                     │
│  - 接收 DTO 参数                │
│  - 调用 Service 层              │
└─────────────────────────────────┘
        │
        ▼
┌─────────────────────────────────┐
│  Service 层                     │
│  - 业务逻辑                     │
│  - Redis 缓存读写               │
│  - Feign 远程调用               │
│  - MyBatis Mapper 数据库操作    │
└─────────────────────────────────┘
        │
        ▼
┌───────────────────┐  ┌──────────────────┐
│  MySQL (sky_take_out)│  │  Redis (db=2)     │
└───────────────────┘  └──────────────────┘
```

### 3.4 配置分层架构

每个微服务采用三层配置：

1. **bootstrap.yml** — 配置 Nacos 服务器地址、命名空间、共享配置文件（shared-jdbc.yaml, shared-redis.yaml, shared-logback.yaml）
2. **application.yml** — 服务名、端口、Swagger 路径匹配等
3. **application-dev.yml** — 开发环境具体参数（数据源、Redis、JWT 密钥、微信配置）

通过 Nacos 配置中心实现多服务共享 JDBC、Redis、日志配置，避免重复维护。

---

## 4. 核心模块详解

### 4.1 网关服务（gateway-service）

整个系统的唯一入口，基于 **Spring Cloud Gateway** 的 Reactive 编程模型。

| 组件 | 说明 |
|---|---|
| `AuthGlobalFilter` | 全局过滤器（Order=0），对所有请求进行 JWT 鉴权，白名单路径包括登录/登出 |
| `DynamicRouteLoader` | 监听 Nacos 配置 `gateway-routes.json`，动态注册/更新路由定义 |
| `JwtUtil` | JWT 令牌创建与解析（HS256），支持管理端和用户端两套密钥 |

**鉴权流程**：
- 放行路径：`/admin/employee/login`、`/admin/employee/logout`、`/user/user/login`
- 其他路径：从请求头获取 `adminToken` 或 `userToken`，校验 JWT，提取用户/员工 ID，写入下游请求头（`admin-info` / `user-info`）
- 校验失败返回 401

### 4.2 公共模块（takeout-common）

所有微服务依赖的共享库。

| 类 | 说明 |
|---|---|
| `Result<T>` | 统一响应体：code（1=成功 / 0=失败）、msg、data |
| `PageResult` | 分页响应体：total、records |
| `BaseContext` | ThreadLocal 存储当前请求的用户/员工 ID |
| `JwtTokenAdminInterceptor` | 管理端拦截器，从请求头提取 empId 存入 BaseContext |
| `JwtTokenUserInterceptor` | 用户端拦截器，从请求头提取 userId 存入 BaseContext |
| `AutoFillAspect` | AOP 切面，自动填充 createTime/updateTime/createUser/updateUser |
| `JacksonObjectMapper` | Jackson 日期格式化配置 |
| `AliOssUtil` | 阿里云 OSS 文件上传工具 |
| `WeChatPayUtil` | 微信支付集成工具 |

**异常类**：`AccountLockedException`、`AccountNotFoundException`、`PasswordErrorException`、`ShoppingCartBusinessException`、`OrderBusinessException`、`LoginFailedException` 等。

### 4.3 远程调用模块（takeout-api）

基于 **Spring Cloud OpenFeign** 的声明式远程调用接口，配合 `DefaultFeignConfig` 设置全日志级别和 `RequestInterceptor` 转发当前用户 ID。

| Feign 接口 | 目标服务 | 核心方法 |
|---|---|---|
| DishClient | dish-service | 按 ID 查询、按状态列表查询 |
| SetmealClient | setmeal-service | 按菜品 ID 查套餐、按 ID 查询、按状态列表查询 |
| OrderClient | order-service | 订单统计、营业额统计、销量 Top10 |
| UserClient | user-service | 用户查询、地址簿查询、用户统计 |
| ReportClient | report-service | 营业数据、各类统计报表、Top10 |
| ShopClient | shop-service | 购物车列表、清空购物车 |

### 4.4 业务服务

#### 员工管理（employee-service）
- 管理员登录/登出（生成 JWT）
- 员工 CRUD、分页查询
- 员工状态启用/禁用、密码修改

#### 分类管理（category-service）
- 菜品/套餐分类的增删改查
- 分类状态启停
- 管理端和用户端双接口

#### 菜品管理（dish-service）
- 菜品 CRUD、口味管理
- 菜品起售/停售
- Redis 缓存菜品列表（按分类缓存，变更时清除）

#### 套餐管理（setmeal-service）
- 套餐 CRUD、套餐菜品关联
- Spring Cache 注解驱动缓存（@Cacheable、@CacheEvict）

#### 订单管理（order-service）
- 用户下单、支付、取消、催单、再来一单
- 管理员接单、拒单、派送、完成
- 订单统计、营业额统计、销量 Top10
- WebSocket 实时推送订单状态

#### 店铺与购物车（shop-service）
- 店铺营业状态管理（Redis 存储）
- 购物车增删改查

#### 用户服务（user-service）
- 微信小程序登录（code → openid → JWT）
- 地址簿管理（默认地址设置）
- 用户统计

#### 报表服务（report-service）
- 营业额/用户/订单统计报表
- 销量 Top10 排行
- Excel 导出（Apache POI）

### 4.5 AI 分析服务（ai-service）

基于规则的业务数据分析引擎，非 LLM 调用：

1. **数据采集**：通过 `ReportClient` Feign 调用获取营业额、用户、订单、Top10 数据
2. **趋势分析**：比较前后半段时间的平均值，判断 UP/DOWN/STABLE
3. **风险识别**：检测无订单、低完成率、低新用户、营业额下降、低客单价、高销售集中度等风险
4. **建议生成**：针对识别的风险匹配可操作建议
5. **热销分析**：Top1 ≥ 50% 为"高集中度"，Top3 ≥ 70% 为"中集中度"

支持按 DAY/WEEK/MONTH 维度进行概要分析，以及自定义时间范围分析（最长 31 天）。

---

## 5. 数据库设计概要

数据库名：`sky_take_out`

| 表名 | 说明 | 核心字段 |
|---|---|---|
| employee | 员工表 | id, username, name, password(MD5), phone, sex, idNumber, status |
| category | 分类表 | id, type(1:菜品/2:套餐), name, sort, status |
| dish | 菜品表 | id, name, category_id, price, image, description, status |
| dish_flavor | 菜品口味表 | id, dish_id, name, value |
| setmeal | 套餐表 | id, category_id, name, price, status, description, image |
| setmeal_dish | 套餐菜品关联表 | id, setmeal_id, dish_id, copies |
| orders | 订单表 | id, number, status(1-6), user_id, amount, order_time, checkout_time |
| order_detail | 订单明细表 | id, order_id, dish_id, setmeal_id, name, number, amount |
| shopping_cart | 购物车表 | id, name, user_id, dish_id, setmeal_id, dish_flavor, number, amount |
| user | 微信用户表 | id, openid, name, phone, sex, avatar |
| address_book | 地址簿表 | id, user_id, consignee, phone, province, city, detail, is_default |

---

## 6. 缓存策略

| 数据类型 | 缓存方式 | 失效策略 |
|---|---|---|
| 菜品列表 | `dish_{categoryId}` Redis Key | 菜品增删改时手动清除 |
| 套餐列表 | `@Cacheable("setmealCache")` | `@CacheEvict` 在变更时清除 |
| 店铺状态 | `SHOP_STATUS` Redis Key | 直接读写，设置过期时间 |
| 用户登录态 | JJWT 自包含 Token，2 小时过期 | 无需服务端存储 |

---

## 7. JWT 认证体系

系统维护两套独立的 JWT 认证：

| 属性 | 管理端 | 用户端 |
|---|---|---|
| 密钥 | itcast | itheima |
| TTL | 7200000ms (2h) | 7200000ms (2h) |
| 令牌请求头 | adminToken | userToken |
| Claim 键 | empId | userId |
| 生成位置 | employee-service | user-service |
| 校验位置 | gateway-service AuthGlobalFilter | gateway-service AuthGlobalFilter |

---

## 8. 部署架构

```
                    ┌─────────────────┐
                    │   Nacos Server  │ (192.168.233.145:8848)
                    │ 注册中心+配置中心 │
                    └─────────────────┘
                           ▲
                           │ 注册
                           │
┌──────────┐  请求   ┌─────┴──────┐  Feign  ┌──────────────┐
│  客户端   │ ──────▶ │  Gateway   │ ──────▶ │  微服务集群   │
│ (Web/小程序)│        │  (8080)    │         │ 8081 ~ 8090  │
└──────────┘         └────────────┘         └──────┬───────┘
                                                   │
                                          ┌────────┴────────┐
                                          ▼                 ▼
                                   ┌──────────┐     ┌──────────┐
                                   │  MySQL   │     │  Redis   │
                                   │(127.0.0.1)│     │(192.168…)│
                                   └──────────┘     └──────────┘
```

### 启动顺序

1. Nacos Server（注册中心 + 配置中心）
2. MySQL + Redis
3. 网关服务（gateway-service）
4. 各业务微服务（无严格依赖顺序）
5. 前端应用（管理后台 / 微信小程序）

---

## 9. 关键技术决策与注意事项

1. **JWT 无状态设计**：Token 自包含用户信息，网关统一校验，不依赖 Session 或 Redis 存储登录态，方便横向扩展。

2. **动态路由**：网关路由配置存储在 Nacos（`gateway-routes.json`），支持运行时动态变更，无需重启网关。

3. **审计字段自动填充**：通过自定义 `@AutoFill` 注解 + AOP 切面，在 MyBatis 执行 insert/update 时自动填充 createTime、updateTime、createUser、updateUser，避免业务代码中手动设置。

4. **ThreadLocal 传递上下文**：网关鉴权后将用户/员工 ID 写入请求头，各服务的拦截器再将其存入 `BaseContext`（ThreadLocal），实现跨层传递用户身份。

5. **分布式缓存一致性**：菜品和套餐列表的缓存采用旁路缓存模式，写操作时主动清除对应缓存，读操作时缓存未命中则从数据库加载。

6. **Feign 日志配置**：`DefaultFeignConfig` 设置 FULL 级别日志，便于开发调试；RequestInterceptor 将当前用户 ID 通过请求头传递给下游服务。

7. **密码加密**：员工密码使用 MD5 哈希存储（`DigestUtils.md5DigestAsHex()`）。

---

## 10. 项目文件结构

```
Punctual_Life_Platform/
├── pom.xml                          # 父 POM（多模块管理）
├── docs/                            # 文档
├── gateway-service/                 # 网关服务
├── sky-pojo/                        # 实体类模块
├── takeout-common/                  # 公共工具模块
├── takeout-api/                     # Feign 接口模块
├── common-service/                  # 文件上传服务
├── employee-service/                # 员工管理服务
├── category-service/                # 分类管理服务
├── dish-service/                    # 菜品管理服务
├── setmeal-service/                 # 套餐管理服务
├── order-service/                   # 订单服务
├── shop-service/                    # 店铺与购物车服务
├── user-service/                    # 用户服务
├── report-service/                  # 报表服务
├── ai-service/                      # AI 分析服务
├── sky-takeout-admin/               # 管理后台前端（Vue3 + Vite）
└── sky-takeout-miniapp/             # 微信小程序前端
```
