# Task 1 运行与操作

这是 Kuikly 行情原型：12 支示例股票 → 个股详情 → 行情证据联动卡。行情和 AI 解读均为确定性历史 Mock，没有联网行情或真实模型调用。

## 启动

从仓库根目录运行 PowerShell：

```powershell
.\scripts\bootstrap-cli.ps1
.\scripts\doctor.ps1
.\scripts\verify.ps1
.\scripts\run-h5.ps1 -Production
```

最后一条以前台进程提供 [本地演示](http://127.0.0.1:18761/)，关闭该终端或 Ctrl+C 停止。可用 `-Port 18762` 更换端口。`run-h5.ps1` 不加 Production 时启动原开发服务器；生产验收使用上面的明确入口。

脚本只设置当前进程环境。JDK、Node、Gradle、SDK、npm、临时文件及浏览器缓存都在 `.cache/`；不使用 conda base、不全局安装、不用 setx。已经存在的 Edge 可被测试工具调用；其测试 profile 放在工作区，未安装新的系统浏览器。不要手工绕过 `use-cli-env.ps1` 调用全局包管理器。

## 推荐操作

1. 从列表打开 A，查看 11.20 元及当日 +0.15 元 / +1.36%。
2. 点击“中途回落”，核对 08-24 至 08-27 的 11.50 → 10.80、-6.09%。两图同时标出这一区间。
3. 点击 08-25 的 K 线或量柱，查看开高低收和当日 -1.74%；也可用“检视起点/终点”和“前一日/后一日”精确选择。从“区间内 · 中途回落”返回完整解释，核对端点计算式。
4. 点击“量能观察”，核对五个样本共 600 万股 / 5 = 均量 120 万股，再核对 180 / 120 = 1.50 倍。点击任一样本行查看当天，再从相关依据返回比较。
5. 在“演示数据”切换量能缺失，09-02 缺量使比较不可用；日期和价格仍可检视。切回完整行情恢复倍数。
6. 返回原列表位置，打开 B，核对整体 -4.00%、局部反弹 +5.49%、量能 0.80 倍。也可体验空行情、首次失败后重试、暂无解读、无效引用与长文。

图上只支持点按检视；纵向拖动交给页面滚动。不提供缩放、平移或拖动十字光标。

浏览器后退→前进会恢复实际快照和所选日期/证据；刷新保留同一上下文。地址栏同步最小参数，可在新标签页打开复现。切换数据与日期不会不断增加历史条目。演示选项在详情底部，长列表会按需渲染，滚动到底部即可切换。

## 承接入口

- `/?entity=MOCK_A&date=2026-08-25`：恢复指定日期。
- `/?entity=MOCK_B&evidence=E2`：恢复局部反弹依据。
- `/?entity=UNKNOWN`：未知实体。
- `/?entity=MOCK_B&snapshot=unavailable`：保留股票及原快照错误。

这些是导航合同的演示入口；Task 2 聊天页面尚未实现。

## 验证与文件

- [正式测试报告](REVIEWS/TASK1-TESTS.md)：实际构建、行为、缺口及评分映射。
- [证据目录](evidence/task1/README.md)：截图、录像、日志与产物哈希。
- [模块合同](interfaces/QuoteEvidenceLens.md)：数据、状态、反查与 caller 边界。
- [学习复盘](learning/TASK1-LEARNING.md)：项目讲述和面试准备。

共同逻辑单测在 `shared/src/commonTest`，由 `:shared:testDebugUnitTest` 执行；H5 行为另测。`verify.ps1` 还检查 JS 和 Android 的页面注册表，防止出现“编译通过但首页未注册”的包。Android Debug APK 在 `androidApp/build/outputs/apk/debug/`；构建成功与设备运行是两个结果。

浏览器回归脚本由 Playwright CLI 的 `run-code` 执行，不是普通 Node 程序：

```powershell
. .\scripts\use-cli-env.ps1
$taskCli = rg --files --hidden --no-ignore .cache/npm/_npx -g playwright-cli.js | Select-Object -First 1
# 在工作区 Playwright 配置中使用 390x844、hasTouch=true、isMobile=true，profile/outputDir 也放 .cache。
# 先用 CLI 的 open 启动该配置的本地浏览器会话 task1，并打开本地演示地址。
& .cache/node/node.exe $taskCli --raw -s=task1 run-code (Get-Content scripts/test-task1-h5.js -Raw)
& .cache/node/node.exe $taskCli --raw -s=task1 run-code (Get-Content scripts/test-task1-touch.js -Raw)
& .cache/node/node.exe $taskCli --raw -s=task1 run-code (Get-Content scripts/test-task1-deepening.js -Raw)
# 另建 hasTouch=false 的桌面会话 task1desktop 后：
& .cache/node/node.exe $taskCli --raw -s=task1desktop run-code (Get-Content scripts/test-task1-mouse.js -Raw)
```

必须检查输出 `TASK1_H5_PASS` / `TASK1_TOUCH_PASS` / `TASK1_DEEPENING_PASS` / `TASK1_MOUSE_PASS`，不能仅看外层 PowerShell 的退出码。桌面鼠标另用 hasTouch=false 的 profile 验收。脚本会实际操作浏览器并覆盖工作区内同名截图；不会调用外部服务。

下一步：按推荐路径体验原型，使用学习复盘自测；Android 设备和 Task 2 保持独立验收。
