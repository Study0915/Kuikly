# Task 1 TESTS

日期：2026-09-07。实施者/验收者：Windows Codex。业务提交：`f6669fa`；分支：`feature/task1-quote-evidence-lens`。

## 裁决

**T1-CODE 完成；T1-TESTS 按计划的平台限制条款受限 VERIFIED。** 共同逻辑、H5 production 运行和 Android Debug 构建通过。Android 设备/模拟器没有运行，不申报该平台交互通过；iOS/鸿蒙及真实 Task 2 caller 未实现或未验证。无已知 Android 运行失败被掩盖成未验证。

本次全部数据和 AI 摘要都是历史 Mock，测试没有连接行情服务或模型。未进行性能基准、真实投资效果验证、发布或提交。

## 执行证据

| 检查 | 结果 | 原始证据（仓库相对路径） |
|---|---|---|
| bootstrap / doctor | CLI_ENV_OK；JDK 17.0.12、Node 24.12.0、npm 11.6.2、Gradle 8.0 | `.cache/task1-evidence/raw/c0-bootstrap.log` |
| 最终 verify | JS/H5 production、Android Debug 均 BUILD SUCCESSFUL；JS/Android 页面注册存在 | `.cache/task1-evidence/raw/t1-verify-final.log` |
| commonTest（JVM 执行） | 21 tests，0 failures，0 errors | `shared/build/test-results/testDebugUnitTest/` |
| H5 完整链路 | TASK1_H5_PASS；59 项检查 | `scripts/test-task1-h5.js`；`.cache/task1-evidence/raw/t1-h5-result.txt` |
| 真实触摸流 | TASK1_TOUCH_PASS；7 项检查 | `scripts/test-task1-touch.js`；`.cache/task1-evidence/raw/t1-touch-result.txt` |
| 桌面鼠标 | 点选通过，移动超过阈值不误选；详情返回的晚到响应单独验证 | `scripts/test-task1-mouse.js`；`.cache/task1-evidence/raw/t1-mouse-result.txt` |
| 视觉 | 实测 320/390 CSS px；关键字段、事实、关联顺序与长文无重叠/截断 | [截图与产物索引](../evidence/task1/README.md) |

单测拆分：EvidenceResolverTest 8、EvidenceEdgeCasesTest 6、MarketPlotLayoutTest 5、FinanceRequestsTest 1、稳定入口合同 1。空 KuiklyChart 的 jsNodeTest SKIPPED 不计作通过的业务单测。业务逻辑不读取设计 JSON 的 expected 数字。

## 计划验收映射

| 项目 | 实际覆盖 | 结论与边界 |
|---|---|---|
| F01–F03 | 12 行字段、A–L 全部逐个打开、逐个恢复滚动位置；H5 后退/前进 | H5 PASS；Android 返回仅源码核对+编译 |
| F04–F05 | A：11.20 / +0.15 / +1.36% / 11.26 / 10.99 / 180 万股；B：9.60 / +0.05 / +0.52% / 9.66 / 9.49 / 80 万股；AI/时效/限制 | H5+事实测试 PASS |
| F06 | 空行情、首次失败重试、未知实体、原快照不可用、已知实体无解读；请求票据拒绝晚结果 | 模型+H5 PASS；不回退默认 A |
| P01–P03 | 320/390 改宽保留日期，双图共用中心；首/中/末日、边界/轴/间隙；依据→日期→依据 | 逻辑+H5 PASS；精确内部边界另有回归测试 |
| P04 | 真实触摸滚动使父 List 变化且不选日，多指/cancel/快速点选；鼠标 click 和拖动 | H5 PASS；移出再移回的最大位移另有纯逻辑测试 |
| P05 | C1 两张独立卡共存、选择和父滚动；之后移除技术入口 | 探针 PASS；不是正式聊天接入 |
| I01–I03 | 三类证据；A -6.09% 与单日 -1.74% 分离；量能目标/样本；B 三种反查角色顺序 | H5+单测 PASS |
| I04–I06 | 仅整体关联提示，无解读仍可看日值；缺量→完整；A/B、文档键、无效恢复焦点 | H5+单测 PASS；同文档双实例另由 C1/单测证明 |
| Q01 | 平价、全零/全缺量、零目标、零均量、不足五日、缺目标量 | 纯逻辑 PASS；H5 代表截图为部分缺量，未把所有边界逐一作为设备画面运行 |
| Q02 | 日期重复/乱序、非法 OHLC/负量、错误窗口长度、重复证据、反序/无效引用 | 单测 PASS；H5 代表错误为无效引用；输入价格类型固定 Int 分，不接收非有限浮点行情 |
| Q03 | 72h 等于/超过/未来，固定 +08:00 时间解析 | 单测 PASS；H5 展示实际时钟下的历史/过期提示 |
| Q04 | 长文完整、两宽、选择有文字、图例单位可见 | H5 视觉 PASS；Android 字体/触摸/布局未运行 |
| Q05 | 完整回归无未捕获异常、无外部请求、无残留测量节点；diff 检查 | H5 PASS；无性能或安全审计认证声明 |

## 修复过的真实问题

1. 普通文本测量留下旧 p 节点：用 H5 文本处理扩展测量隐藏副本，并 finally 移除。红例可见重复文本，绿例副本数 0，实际截图无重叠。
2. 混合输入设备只绑定 touch：提供受抑制的 click 后备；补充宿主鼠标最大位移取消，修复拖动误选。第二指由宿主取消单指流。
3. 精确日槽边界：272px 宽度的第 3 条内部边界被浮点除法归为第 2 槽；改为和槽位相同的边界比较，回归测试通过。
4. 增量构建丢失页面注册：只改非 Page 文件时 KuiklyCoreEntry 无 finance_home，浏览器报未注册且空白。锁定 2.4.0 的 KSP 使用聚合输出但未关联源文件；本仓库关闭 KSP 增量，并在 verify 检查两个 target 的注册。之后再次改非 Page 文件构建，注册仍存在、浏览器通过。开关依据 [KSP 官方增量说明](https://kotlinlang.org/docs/ksp-incremental.html)，未升级编译器或框架。
5. 页面反查入口原先按文档顺序：改为显式优先级后，浏览器检查入口 y 坐标，局部/样本/整体与 resolver 一致。

测试自身曾过早读取异步事实/列表布局；现按明确可见结果等待。首版多指探针也改为比较该卡事实，避免滚动诊断文本干扰。上述等待不放宽数值、焦点或恢复位置的判据。

## 40 / 25 / 25 / 10 的证据边界

| 维度 | 可申报的实现证据 | 不作的推断 |
|---|---|---|
| 功能 40 | 列表→详情→AI、完整字段、恢复与返回 | 不预测实际得分 |
| 工程 25 | 同一 Lens、纯事实/状态/几何、21 单测、请求隔离、聚焦 commit | 一份详情 caller 不冒充两题集成 |
| AI 场景 25 | 证据双向核对、限制说明、缺量失效、无单日解释边界 | Mock 模板不是实时大模型推理 |
| 加分 10 | 本轮不申领 | APK 编译不是额外平台运行 |

下一步：交付学习复盘供用户自测；Android 设备运行须先证明工具配置不写 C 盘，Task 2 仍走独立 PLAN 确认。
