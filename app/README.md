# App 模块

## 概述

本模块是 `Magic-App` 项目的核心，提供了手机和平板设备的用户界面和主要功能。

应用采用单 Activity 架构，所有界面均使用 Jetpack Compose 构建，确保了现代化且响应迅速的用户体验。

## 主要功能

- **现代化 UI**: 完全使用 Jetpack Compose 构建，提供了丰富的动画和自适应布局。
- **底部导航**: 应用包含一个底部导航栏，用于在“主页”和“发现”等主要功能区之间切换。此导航栏在特定页面会自动隐藏，以提供更沉浸的浏览体验。
- **MVVM 架构**: 遵循官方推荐的 MVVM（Model-View-ViewModel）架构，实现了界面与业务逻辑的分离。
- **依赖注入**: 集成了 Hilt，用于管理整个应用的依赖关系，简化了开发和测试。
- **Jetpack Navigation**: 使用 Navigation Compose 组件管理应用内的页面跳转和导航栈。

## 关键组件

- **`MagicApp`**: 应用的 Application 类，使用 `@HiltAndroidApp` 注解，是 Hilt 注入的入口点。
- **`MainActivity`**: 项目中唯一的 Activity，作为所有 Composable 界面的容器。
- **`AppNavHost`**: 管理所有导航目标（Destinations）的导航主机，负责处理页面路由。
- **`AppBottomNavigationBar`**: 自定义的底部导航栏组件，实现了主要功能区的切换逻辑和动画效果。

## 如何运行

1. 在 Android Studio 中打开 `Magic-App` 项目。
2. 在运行配置中选择 `app` 模块。
3. 选择一个手机或平板模拟器，或连接一台实体 Android 设备。
4. 点击运行按钮进行部署和启动。
