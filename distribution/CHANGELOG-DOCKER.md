# Nacos Docker 镜像修改说明

## 1. PostgreSQL 数据源插件支持

在官方 Nacos 基础上新增 PostgreSQL 数据源插件，支持使用 PostgreSQL 作为外部存储。

### 新增/修改的文件

| 路径 | 说明 |
|------|------|
| `plugin/datasource/.../impl/postgres/*.java` | 10 个 PostgreSQL 专用 Mapper 实现（ConfigInfo、ConfigInfoBeta、ConfigInfoGray、ConfigInfoTag、ConfigTagsRelation、GroupCapacity、HistoryConfigInfo、TenantCapacity、TenantInfo） |
| `plugin/datasource/.../enums/postgres/TrustedPostgresFunctionEnum.java` | PostgreSQL 内置函数映射（如 NOW → CURRENT_TIMESTAMP） |
| `plugin/datasource/.../constants/DataSourceConstant.java` | 新增 `POSTGRESQL = "postgresql"` 常量 |
| `plugin/datasource/pom.xml` | 新增 `org.postgresql:postgresql:42.7.3` 依赖 |
| `plugin/datasource/.../META-INF/services/...Mapper` | SPI 注册 9 个 PostgreSQL Mapper |
| `persistence/.../PersistenceConstant.java` | 新增 `POSTGRESQL = "postgresql"` 常量 |
| `plugin-default-impl/.../PostgresqlPageHandlerAdapter.java` | PostgreSQL 分页适配器（OFFSET FETCH NEXT） |
| `plugin-default-impl/.../PageHandlerAdapterFactory.java` | 注册 PostgreSQL 分页适配器 |
| `config/.../ExternalHistoryConfigInfoPersistServiceImpl.java` | 兼容 PostgreSQL 的 SQL 语法 |
| `distribution/conf/postgres-schema.sql` | PostgreSQL 建表 DDL（12 张表） |

### 使用方法

`application.properties` 中配置：

```properties
spring.sql.init.platform=postgresql
db.num=1
db.url.0=jdbc:postgresql://<host>:5432/<db>?characterEncoding=utf8&connectTimeout=1000&socketTimeout=3000&autoReconnect=true&useUnicode=true&useSSL=false&serverTimezone=UTC
db.user.0=<user>
db.password.0=<password>
db.pool.config.driverClassName=org.postgresql.Driver
```

首次启动前需执行 `distribution/conf/postgres-schema.sql` 初始化表结构。

---

## 2. Docker 镜像优化（K8s 友好）

### 2.1 非 root 用户支持（`runAsUser: 1501`）

```dockerfile
# Dockerfile
RUN mkdir -p logs data
    && chmod -R 777 /home/nacos
```

整个 `/home/nacos` 目录设置为 777 权限，任意 UID 均可读写，适配 K8s `securityContext.runAsUser` 配置。

### 2.2 日志路径可配置（LOG_HOME）

```bash
# docker-startup.sh
: "${LOG_HOME:=${BASE_DIR}/logs}"          # 默认 ${BASE_DIR}/logs，可通过环境变量覆盖
JAVA_OPT="${JAVA_OPT} -Dnacos.logs.path=${LOG_HOME}"  # 传给 logback
```

通过 `LOG_HOME` 环境变量指定日志输出目录，不设置时沿用默认值 `${BASE_DIR}/logs`。

K8s 部署示例：

```yaml
env:
  - name: LOG_HOME
    value: "/home/appxddg/logs"           # 日志写到此处
volumeMounts:
  - mountPath: /home/appxddg/logs
    name: log-home
```

### 2.3 GC 日志

容器环境下 GC 日志不写文件（避免权限问题），默认走 JVM stderr。

### 2.4 Heap Dump 路径

```bash
-XX:HeapDumpPath=${LOG_HOME}/java_heapdump.hprof
```

堆转储文件跟随 `LOG_HOME` 目录，便于统一收集。

---

## 3. 构建与部署

### 构建镜像

```bash
# Maven 打包（生成 distribution/target/nacos-server-{version}.tar.gz）
mvn clean install -DskipTests -Prelease-nacos

# 构建 Docker 镜像
cd distribution
docker build -t nacos-postgres:{version} .
```

### 保存与导入

```bash
# 导出
docker save nacos-postgres:{version} | gzip > nacos-postgres-{version}-docker-image.tar.gz

# 导入
docker load < nacos-postgres-{version}-docker-image.tar.gz
```

### 版本对应关系

| 分支 | Nacos 版本 | PostgreSQL 插件 | Docker Tag |
|------|-----------|----------------|------------|
| `2.5.1-postgresql` | 2.5.1 | ✅ | — |
| `2.5.2-postgresql` | 2.5.2 | ✅ | `nacos-postgres:2.5.2` |
| `2.5.3-postgresql` | 2.5.3 | ✅ | `nacos-postgres:2.5.3` |
