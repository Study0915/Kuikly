# Contributing

## 环境

- JDK 21
- 项目 Gradle Wrapper
- `GRADLE_USER_HOME=$PWD/.cache/gradle`
- npm 缓存 `$PWD/.cache/npm`

## 工作流

1. 从 `main` 建立 `feature/<topic>` 或 `bugfix/<topic>` 分支。
2. 先在 `docs/` 记录功能边界和验证方式。
3. 保持计算逻辑与 Canvas/宿主解耦，并补齐边界测试。
4. 执行 `scripts/verify.ps1`。
5. 提交信息遵循 Angular Convention。

不得提交密钥、本机 SDK 路径、构建缓存或运行产物。平台支持声明必须附实际证据。
