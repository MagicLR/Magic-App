# Magic-App

Magic-App 是一个基于 **MVVM + Clean Architecture** 架构构建的 Android 应用。  
项目采用多模块设计，核心逻辑与 UI 分离，便于扩展、测试和复用。

---

## ✨ 特性（规划中）
- 基于 **Jetpack Compose** 的现代化 UI
- 使用 **Hilt** 进行依赖注入
- 使用 **Retrofit + OkHttp** 进行网络请求
- 使用 **Room** 进行本地数据持久化
- 多模块结构：
    - `core/domain`：业务模型与接口
    - `core/data`：数据实现（远程 + 本地）
    - `app`：UI 与 ViewModel
    - `tv`: TV 版 UI 与 ViewModel
---

## 📂 模块结构

- **domain 层**
    - 定义业务模型（如 `User`, `SshConfig`）
    - 定义 Repository 接口
    - 提供用例（UseCase）类，封装业务逻辑

- **data 层**
    - Retrofit Service（远程数据源）
    - Room DAO（本地数据源）
    - Repository 实现类（组合远程与本地）

- **app 层**
    - 使用 Hilt 注入 `domain` 定义的接口实现
    - 提供 ViewModel
    - 使用 Compose 构建界面

- **tv 层**
    - TV 版 UI 与 ViewModel

---

## 🛠 技术栈

- **语言**: Kotlin
- **UI**: Jetpack Compose, Material3
- **依赖注入**: Hilt (Dagger)
- **网络**: Retrofit, OkHttp
- **持久化**: Room
- **协程**: Kotlin Coroutines, Flow
- **架构**: MVVM + Clean Architecture
- **测试**: JUnit, Mockito/Mockk

---

## 🚀 快速开始

### 环境要求
- Android Studio Ladybug+ (或更高)
- Kotlin 1.9+
- Gradle 8+
- JDK 17（推荐使用 Android Studio 内置 JDK）

### 构建步骤
```bash
# 克隆项目
git clone https://github.com/MaskLR/Magic-App.git
cd Magic-App

# 构建 Debug 版本
./gradlew assembleDebug
