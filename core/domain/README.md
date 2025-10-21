# Core-Domain 模块

## 概述

`core-domain` 模块是应用业务逻辑的核心，它定义了应用的“做什么”，但不关心“如何做”。

该模块是纯 Kotlin 模块，不依赖任何 Android 框架。这确保了业务逻辑的独立性、可测试性和可移植性。

## 设计原则

- **独立性**: 不包含任何与 UI 或数据源实现相关的代码。
- **纯净性**: 只包含纯 Kotlin/Java 代码和业务规则。
- **依赖倒置**: 定义数据操作的接口（Repositories），但具体实现由 `core-data` 模块提供。

## 关键组件

- **Entities (实体)**: 代表应用核心数据结构的纯数据类。例如，`User`, `Product` 等。

- **Use Cases (用例) / Interactors**: 封装了单一、具体的业务操作。例如，`GetUserUseCase` 负责获取用户信息，它会调用 `UserRepository` 接口来完成任务。

- **Repository Interfaces (仓库接口)**: 定义了数据层的契约。例如，`UserRepository` 接口定义了获取和更新用户数据的方法，但其具体实现（从网络还是本地数据库获取）是在 `core-data` 模块中完成的。

## 模块依赖关系

- **被依赖**: `core-data` 模块和所有功能模块（如 `app`, `tv`）都依赖此模块。
- **不依赖**: 此模块不依赖项目中的任何其他模块。
