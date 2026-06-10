# Riot 电竞大数据分析平台 - 后端

> Spring Boot 3.2 + Java 17 + MyBatis-Plus 3.5 + MySQL 8 + Knife4j + JWT

## 目录

- [快速开始](#快速开始)
- [项目结构](#项目结构)
- [配置说明](#配置说明)
- [数据库初始化](#数据库初始化)
- [默认账号](#默认账号)
- [接口文档](#接口文档)
- [模块清单](#模块清单)
- [常见问题](#常见问题)

---

## 快速开始

### 1. 环境要求

| 工具 | 版本 | 说明 |
| --- | --- | --- |
| JDK | 17+ | 项目使用 Java 17 语法 |
| Maven | 3.8+ | 或使用项目自带的 `mvnw.cmd` 包装器 |
| MySQL | 8.0+ | utf8mb4 字符集 |

### 2. 初始化数据库

打开 MySQL 命令行或客户端，执行：

```bash
mysql -u root -p < src/main/resources/db/01_schema.sql
mysql -u root -p esports < src/main/resources/db/02_seed_data.sql
```

或者用 Navicat / DataGrip 等工具直接执行这两个 SQL 文件。

### 3. 配置环境变量

复制 `.env.example` 为 `.env`，按本机情况修改：

```env
MYSQL_HOST=127.0.0.1
MYSQL_PORT=3306
MYSQL_DB=esports
MYSQL_USER=root
MYSQL_PASSWORD=zjb070124@     # ← 改成本机密码
JWT_SECRET=建议用 openssl rand -base64 32 生成
RIOT_API_KEY=RGAPI-xxx
```

> `.env` 已被 `.gitignore` 排除，**不会**提交到代码仓库。

### 4. 启动

#### Windows（PowerShell / CMD）

```cmd
:: 临时把 .env 加载到当前 shell（PowerShell）
Get-Content .env | ForEach-Object { if($_ -match '^([^=]+)=(.*)$') { [Environment]::SetEnvironmentVariable($matches[1], $matches[2], 'Process') } }

:: 启动
mvnw.cmd spring-boot:run
```

或者在 IDE（如 IntelliJ IDEA）中：
1. 打开 `EsportsApplication.java`
2. 在启动配置里勾选 `Environment variables` → `Load from file` → 选 `.env`
3. 点运行

#### macOS / Linux

```bash
set -a; source .env; set +a
./mvnw spring-boot:run
```

### 5. 验证

```bash
curl http://localhost:8080/api/hello
```

预期返回：
```json
{
  "code": 0,
  "msg": "ok",
  "data": {"service":"esports-bigdata-backend","version":"1.0.0","ts":"2026-06-10 ..."}
}
```

---

## 项目结构

```
backend/
├── pom.xml                                # Maven 配置
├── .env / .env.example                    # 环境变量（密码等敏感信息）
├── mvnw.cmd                               # Maven 包装器（Windows）
├── README.md
└── src/main/
    ├── java/com/esports/bigdata/
    │   ├── EsportsApplication.java        # 启动入口
    │   ├── common/                        # 公共组件
    │   │   ├── Result.java                # 统一返回结构
    │   │   ├── ResultCode.java            # 错误码枚举
    │   │   ├── exception/                 # 全局异常处理
    │   │   └── page/                      # 分页
    │   ├── config/                        # 配置类（CORS/MyBatis-Plus/Knife4j/MVC）
    │   ├── security/                      # JWT 工具与拦截器
    │   └── module/
    │       ├── auth/                      # 鉴权（登录/当前用户）
    │       ├── dashboard/                 # 数据大屏
    │       ├── player/                    # 玩家
    │       ├── champion/                  # 英雄/特工
    │       └── match/                     # 比赛
    └── resources/
        ├── application.yml                # 主配置
        └── db/
            ├── 01_schema.sql              # 表结构
            └── 02_seed_data.sql           # 种子数据
```

---

## 配置说明

所有配置项都从环境变量读取（`.env` 文件），未设置时回退默认值。

| 配置项 | 环境变量 | 默认值 | 说明 |
| --- | --- | --- | --- |
| 服务端口 | `SERVER_PORT` | `8080` | |
| MySQL 主机 | `MYSQL_HOST` | `127.0.0.1` | |
| MySQL 端口 | `MYSQL_PORT` | `3306` | |
| 数据库名 | `MYSQL_DB` | `esports` | |
| 用户名 | `MYSQL_USER` | `root` | |
| 密码 | `MYSQL_PASSWORD` | **必填** | 真实密码请写到 `.env` |
| JWT 密钥 | `JWT_SECRET` | dev 默认 | 生产用 `openssl rand -base64 32` |
| Token 有效期 | `JWT_EXPIRE_HOURS` | `72` | 小时 |
| Riot API Key | `RIOT_API_KEY` | `RGAPI-...` | 必填，去 [Riot Developer Portal](https://developer.riotgames.com/) 申请 |

---

## 数据库初始化

### 库结构（共 12 张表）

| 类别 | 表 | 用途 |
| --- | --- | --- |
| 用户/订阅 | `tb_user`, `tb_subscribe` | 登录、微信订阅 |
| 维度 | `dim_patch`, `dim_player`, `dim_champion`, `dim_agent`, `dim_comp` | 静态维度 |
| 事实 | `fact_match`, `fact_match_player`, `fact_ranking_snapshot` | 业务事实 |
| ADS | `ads_champion_stats_d`, `ads_player_profile` | 应用层宽表 |

### 种子数据量

- 用户：2 条（admin/demo）
- 英雄：40 个
- 特工：20 个
- 阵容：8 套
- 玩家：14 个
- 段位快照：100 条（10 玩家 × 10 天）
- 比赛：20 场
- 比赛-玩家事实：200 条
- 英雄统计：100 条

---

## 默认账号

种子数据已生成两个测试账号，**密码统一为 `123456`**（BCrypt 哈希）：

| 用户名 | 密码 | 角色 |
| --- | --- | --- |
| `admin` | `123456` | ADMIN |
| `demo`  | `123456` | USER |

```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"123456"}'
```

返回中的 `token` 字段即 JWT，后续请求加 Header：`X-Token: Bearer <token>`

---

## 接口文档

启动后访问 Knife4j 文档：

```
http://localhost:8080/api/doc.html
```

文档包含 5 大模块、所有 RESTful 接口、请求/响应示例、在线调试。

---

## 模块清单

| 模块 | 路径前缀 | 关键接口 |
| --- | --- | --- |
| 鉴权 | `/auth` | `POST /auth/login` `GET /auth/me` |
| 数据大屏 | `/dashboard` | `GET /dashboard/overview` `GET /dashboard/rank-distribution` `GET /dashboard/top-rank` `GET /dashboard/realtime-matches` `GET /dashboard/top-champions` |
| 玩家 | `/player` | `GET /player/page` `GET /player/detail` `GET /player/search` |
| 英雄/特工 | `/champion` | `GET /champion/list` `GET /champion/top` `GET /champion/hot-trend` `GET /champion/detail/{id}` |
| 比赛 | `/match` | `GET /match/page` `GET /match/detail/{matchId}` |
| 健康检查 | `/hello` | `GET /hello` |

---

## 常见问题

### Q1: 启动报 `Access denied for user 'root'@'localhost'`
检查 `.env` 中 `MYSQL_PASSWORD` 是否正确，注意密码中如有 `@` 需用普通字符串即可（YAML 自动处理）。

### Q2: Knife4j 404
确认访问的是 `http://localhost:8080/api/doc.html`（带 `/api` 前缀，因为 `server.servlet.context-path=/api`）。

### Q3: `mvnw.cmd` 报 JAVA_HOME 找不到
```cmd
set JAVA_HOME=C:\Program Files\Java\jdk-17
mvnw.cmd spring-boot:run
```

### Q4: Maven 下载依赖很慢
项目根目录新建 `~/.m2/settings.xml`，配置阿里云镜像：
```xml
<settings>
  <mirrors>
    <mirror>
      <id>aliyun</id>
      <url>https://maven.aliyun.com/repository/public</url>
      <mirrorOf>*</mirrorOf>
    </mirror>
  </mirrors>
</settings>
```

### Q5: 我用 JDK 8
本项目用了 Java 17 语法（如 `var`、`switch` 表达式）。如果必须用 JDK 8，需要：
1. 降低 Spring Boot 版本到 `2.7.x`
2. 改写部分代码（去掉 `var`，改用 `String.format`）
3. 替换 `cn.hutool.crypto.digest.BCrypt`（Hutool 5.7+ 支持 JDK 8）

建议**升级到 JDK 17**（Oracle / Eclipse Temurin / Microsoft Build of OpenJDK 都有），一次到位。
