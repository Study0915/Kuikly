# Task 1 技术探针

日期：2026-09-07。基线：0060711；工具隔离提交：b11a87b。

## C1 双图与事件

- 固定 20 日，两图共用坐标；H5 320/390 CSS px 重算宽度，保留日期焦点。
- Edge 桌面鼠标、移动触摸均能从 K 线或成交量选择同一日。
- 真实触摸纵向滑动使父 List 滚动，选择不变；多指、touchcancel、轴标签区域不提交选择。
- 临时双实例容器中，B 点选不改变 A 的区间。此容器仅为技术夹具，不是 Task 2 接入。
- commonTest：几何/手势 5 项，加原空壳合同 1 项，均通过；JS/H5 production、Android APK 构建通过。Android 设备运行未验证。
- 返回桥接随 C3 的实际首页/详情验证；当前探针不据此声明 F03 跨宿主完成。

## 精确版本与兼容修复

依据 KuiklyUI tag `2.4.0`（63cdb10b07065fac9692899dcd8f15bd1f4bc61b）源码：CanvasView、GroupEvent/TouchParams、ScrollerView、Pager，以及 H5 KRView、RichTextProcessor。

1. 混合输入 Windows 主机的 maxTouchPoints 非零时，H5 只绑定 touch。图层增加 click 后备路径；触摸流后 600ms 抑制合成 click，保证一次选择。
2. H5 将多指压成单指参数。宿主在第二指到达时取消第一指流；不阻止父容器正常纵向滚动。
3. 原 H5 普通文本 DOM 测量会把临时 p 节点插回原父容器，响应式换行更新后出现旧文字重叠。红例：`#root p:not([id])` 数量为 1，并经截图确认可见。最小宿主扩展只替换普通文本测量：隐藏副本测量、finally 移除，原正文不被挪动；富文本路径保持框架实现。修复后反复切换证据/日期，副本数量为 0，截图无重叠。

测试第一次使用整个 body 文本比较多指前后状态，受滚动诊断数值异步变化干扰；改为比较该卡事实区后，多指/取消/图外均通过，没有放宽焦点不变的要求。

## 本地证据（忽略目录，不含发布证明）

- `.cache/task1-evidence/raw/c1-build-final.log`：共同逻辑单测、JS/H5、APK。
- `.cache/task1-evidence/raw/c1-text-fix-build.log`：文本修复后的 H5 构建。
- `.cache/task1-evidence/c1-probe.js`：双图点选、宽度、真实触摸滚动、两实例。
- `.cache/task1-evidence/c1-extra.js`：多指、取消、轴区域。
- `.cache/task1-evidence/browser/c1-320-day.png`、`c1-two-instances.png`、`c1-ghost-fixed.png`、`c1-final-390.png`：实际浏览器截图。

下一步：正式模型与首页/详情替换探针，并把这些交互断言迁移到实际联动卡。
