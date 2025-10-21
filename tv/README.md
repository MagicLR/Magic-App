# TV 模块

## 概述

本模块是 `Magic-App` 项目的 Android TV 版本，旨在提供适配电视屏幕的用户体验。

应用启动时，它会首先在后台向 `http://mask.ddns.net:8888` 发送一个网络请求，用于执行特定的初始化操作。之后，会展示一个使用 Jetpack Compose 构建的主界面。

## 主要功能

- **TV 适配界面**: 所有界面均使用 `androidx.tv.material3` 库构建，为电视用户提供原生体验。
- **后台初始化**: 应用启动时，会通过一个隐藏的 `WebView` 发送网络请求，实现静默的初始化，不影响用户体验。
- **MVVM 架构**: 遵循现代 Android 开发的 MVVM（Model-View-ViewModel）架构模式，使用 `ViewModel` 管理界面逻辑和数据。
- **依赖注入**: 集成了 Hilt 来实现依赖注入，简化了依赖管理和代码的模块化。

## 关键组件

- **`TvApplication`**: 作为应用的入口点。它负责在应用创建时，执行后台网络请求的初始化任务。
- **`MainActivity`**: 应用的主 Activity，负责加载 Compose 视图和处理主要的交互逻辑，例如返回键导航。
- **`HomeScreen`**: 使用 Jetpack Compose for TV 构建的主屏幕界面。
- **`HomeViewModel`**: 负责为 `HomeScreen` 提供数据和业务逻辑。

## 如何运行

1. 在 Android Studio 中打开整个 `Magic-App` 项目。
2. 在运行配置中选择 `tv` 模块。
3. 选择一个 Android TV 模拟器或连接一台实体 Android TV 设备。
4. 点击运行按钮进行部署和启动。
