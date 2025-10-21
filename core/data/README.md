# Core-Data 模块

## 概述

`core-data` 模块是应用的数据层，负责实现 `core-domain` 模块定义的接口，并处理所有数据的来源和存储。

它封装了数据操作的复杂性，为上层（业务逻辑层）提供统一、干净的数据接口。

## 设计原则

- **实现 Domain 接口**: 提供 `core-domain` 中定义的 `Repository` 接口的具体实现。
- **数据来源抽象**: 管理多个数据源（如网络、数据库、内存缓存），并决定何时从哪个来源获取数据。
- **数据转换**: 负责将网络数据模型（DTOs）或数据库实体转换为 `core-domain` 中定义的业务实体（Entities）。

## 关键组件

- **Repository Implementations (仓库实现)**: `core-domain` 中 `Repository` 接口的具体实现类。例如，`UserRepositoryImpl` 会实现 `UserRepository` 接口，并内部调用 `ApiService` 或 `UserDao` 来获取数据。

- **Data Sources (数据源)**: 
  - **Remote Data Source**: 通常是 Retrofit/Ktor 的 `ApiService`，负责从网络 API 获取数据。
  - **Local Data Source**: 通常是 Room/SQLDelight 的 `DAO`，负责从本地数据库读写数据。

- **Data Models (DTOs / Database Entities)**: 用于网络或数据库操作的数据模型，与 `core-domain` 的业务实体相分离。

- **Mappers (映射器)**: 用于在不同的数据模型之间进行转换的工具类或扩展函数。

## 模块依赖关系

- **依赖**: 依赖 `core-domain` 模块来获取接口和业务实体的定义。
- **被依赖**: 被所有需要访问数据的功能模块（如 `app`, `tv`）所依赖。
