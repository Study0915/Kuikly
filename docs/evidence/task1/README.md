# Task 1 验收证据

2026-09-07，业务提交 `f6669fa`。全部为历史 Mock；[测试裁决](../../REVIEWS/TASK1-TESTS.md)。

## 已入 Git 的截图

| 文件 | 场景 |
|---|---|
| [首页 390](t1-home-390.png) | 名称、代码、最新价、当日额/幅 |
| [详情 390](t1-detail-390.png) | A 字段、Mock 摘要、时效 |
| [回落双图 390](t1-e2-390.png) | 08-24 至 08-27 的对应区间 |
| [单日事实 320](t1-facts-320.png) | 08-25 OHLC、-1.74% 与相关依据 |
| [缺量事实](t1-missing-volume.png) | 09-02 缺量仍可检视 |
| [B 反查顺序](t1-b-related.png) | 局部→样本→整体 |
| [长文 320](t1-long-text-320.png) | 完整换行显示 |

## 本地录像及构建产物

大体积产物和包含本机路径的原始日志保存在忽略目录，不直接进入 Git。不是已发布的提交包。

| 产物 | 相对路径 | 字节 / SHA-256 |
|---|---|---|
| 实际浏览器录像 | `.cache/task1-evidence/task1-demo.webm` | 2,540,373 / `BFD43D9638293C22F0E8F030CFB0ADC4E9EE63E8A9820B206B944B060C5FF881` |
| H5 production JS | `h5App/build/kotlin-webpack/js/productionExecutable/h5App.js` | 448,515 / `7E38205BD1C640428ADE9AFC782620C6CB42B9FF1D6B7138E917AE6398ED854C` |
| Android Debug APK | `androidApp/build/outputs/apk/debug/androidApp-debug.apk` | 6,518,735 / `56AE11434DADE7198A33C549D7ED313DA0B61FC861D0942973A144608EDA7C32` |

录像为 VP8、390×844、25fps、90.20 秒，无配音。实际路径覆盖列表滚动、A 详情、区间→日期→已有依据、量能与缺量恢复、B 反弹、失败重试与返回；用 `scripts/demo-task1-h5.js` 操作真实页面录制，非动画模拟。已成功解码第 5/35/65 秒画面并目检，抽帧与日志留在 `.cache/task1-evidence/browser` / `raw`。

录像首次因缺少 FFmpeg 未生成文件；安装后清理了 CLI 的失败录像状态并重新录制，最终以实际文件、哈希及解码结果验收，不把工具给出的空链接算作成功。

## 运行时与原始报告

- `.cache/task1-evidence/raw/t1-verify-final.log`：最终 JS/H5、21 单测、Android 构建，含页面注册检查。
- `.cache/task1-evidence/raw/t1-h5-result.txt`：59 项功能检查。
- `.cache/task1-evidence/raw/t1-touch-result.txt`：7 项真实触摸检查。
- `.cache/task1-evidence/raw/t1-mouse-result.txt`：鼠标点选/拖动、可见 Loading 与返回取消晚结果。
- C1 两实例技术夹具及错误复现见 [探针记录](../../REVIEWS/TASK1-PROBES.md)；夹具已移除，不能据此声称完成聊天页面。

本轮复用工作区 JDK/Node/SDK/Playwright；仅为录像新增 FFmpeg `n7.0.1-playwright-build-1011`（包内 `COPYING.LGPLv2.1`），以及其 Windows 检查工具 Winldd v1007。通过 Playwright 官方 CDN 获取，均在 `.cache/browsers/`；安装日志 `.cache/task1-evidence/raw/ffmpeg-install.log`。没有新增业务依赖、全局安装或 base 环境修改。

下一步：按 [运行说明](../../TASK1-RUN.md) 体验 Demo；设备运行、用户学习自测和 Task 2 仍分别记录。
