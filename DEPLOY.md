# 后端公网部署说明

将牙科诊所后端 API 部署到公网后，你的 Appsmith 前端（https://jmoney.appsmith.com）即可通过公网 URL 调用接口。

---

## 方式一：Render（推荐，免费）

[Render](https://render.com) 提供免费档 Web 服务，适合演示与前后端联调。

### 步骤

1. **注册 / 登录**  
   打开 [https://render.com](https://render.com)，用 GitHub 登录。

2. **用 GitHub 仓库部署**  
   - 在 Render 控制台点击 **New** → **Web Service**。  
   - 连接你的 GitHub，选择 **MIE350_DentalCilinic_WebApplication** 仓库。  
   - 若已有 `render.yaml`，Render 会识别并提示用 Blueprint 部署，选 **Apply** 即可。  
   - 若未识别，则手动设置：
     - **Name**: `dental-clinic-api`（或任意名称）
     - **Region**: 选离你近的（如 Singapore）
     - **Root Directory**: 留空（使用仓库根目录）
     - **Runtime**: **Docker**
     - **Dockerfile Path**: `backend/Dockerfile`
     - **Docker Context**: `backend`
     - **Instance Type**: 选 Free

3. **创建并部署**  
   点击 **Create Web Service**，等待构建与部署完成（约 3–5 分钟）。

4. **获取公网地址**  
   部署成功后，在服务详情页会看到类似：  
   `https://dental-clinic-api-xxxx.onrender.com`  
   这就是你的 **后端 API 基础地址**。

5. **在 Appsmith 中填写**  
   - 在 Appsmith 的 API / 数据源里，Base URL 填：  
     `https://dental-clinic-api-xxxx.onrender.com`  
   - 接口路径保持为：`/api/...`（例如 `/api/auth/login`、`/api/patients`）。

**注意**：Render 免费实例约 15 分钟无访问会休眠，首次访问可能需等待几十秒唤醒。

---

## 方式二：Railway

[Railway](https://railway.app) 同样支持 Docker，部署流程类似。

1. 登录 [railway.app](https://railway.app)，用 GitHub 登录。  
2. **New Project** → **Deploy from GitHub repo**，选择本仓库。  
3. 在项目设置中：
   - **Root Directory**: 设为 `backend`
   - **Build**: 选择 **Dockerfile**
   - **Start Command**: 留空（镜像内已包含 `java -jar app.jar`）
4. 在 **Variables** 中可添加 `PORT=8080`（多数情况下 Railway 会自动注入）。  
5. 在 **Settings** → **Networking** 中生成 **Public URL**，即你的公网 API 地址。  
6. 在 Appsmith 里把该 URL 填为 API Base URL，路径仍为 `/api/...`。

---

## 方式三：本地临时公网（ngrok）

仅用于本地调试、不部署到云时，可用 ngrok 把本机 8080 暴露到公网。

1. 安装 [ngrok](https://ngrok.com/download)。  
2. 本地启动后端：
   ```bash
   cd backend
   ./mvnw spring-boot:run
   ```
3. 新开终端执行：
   ```bash
   ngrok http 8080
   ```
4. 终端中会显示类似 `https://xxxx.ngrok-free.app` 的地址，在 Appsmith 里用该地址作为 Base URL。  
5. 本机关闭后端或 ngrok 后，该地址会失效。

---

## CORS 与前端

后端已允许以下来源跨域访问 `/api/**`：

- `https://jmoney.appsmith.com`
- `https://app.appsmith.com`
- 以及本地开发地址（如 localhost:5173）

若你使用其他前端域名，需在 `backend/src/main/java/com/dentalclinic/WebConfig.java` 的 `allowedOrigins` 中追加该域名并重新部署。

---

## 数据说明

当前使用 **H2 内存数据库**，数据不落盘。每次服务重启（包括 Render 休眠后唤醒）后，数据会恢复为 `data.sql` 中的初始数据。  
测试账号：

- 用户名: `receptionist`，密码: `dental123`
- 用户名: `dentist`，密码: `dental456`
- 用户名: `admin`，密码: `dental789`

若后续需要持久化，可改为使用 Render/Railway 提供的 PostgreSQL 等数据库，再修改 `application.properties` 和依赖即可。
