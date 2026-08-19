# KuiklyChart API

## 数据

```kotlin
data class ChartPoint(val label: String, val value: Float)

data class ChartSeries(
    val name: String,
    val points: List<ChartPoint>,
    val color: Color,
)

data class ChartSelection(
    val seriesIndex: Int,
    val pointIndex: Int,
    val label: String,
    val value: Float,
)
```

非有限值会被忽略。原始 `pointIndex` 在选择结果中保留，便于调用方关联业务数据。

## 折线图

```kotlin
LineChart {
    attr {
        data(series)
        showAxis(true)
        showGrid(true)
        showDots(true)
        showTooltip(true)
        theme(ChartTheme.FINANCE_DARK)
    }
    event {
        onPointSelected { selection -> println(selection) }
    }
}
```

## 柱状图

```kotlin
BarChart {
    attr {
        data(series)
        barColors(colors)
        showAxis(true)
        showGrid(true)
        showValueLabels(true)
        cornerRadius(4f)
    }
    event {
        onPointSelected { selection -> println(selection) }
    }
}
```

## 计算 API

- `ChartMath.computeRange`：安全数值范围。
- `ChartMath.ticks`：稳定的等距刻度。
- `ChartMath.layout`：把有效点转换为绘图区坐标。
- `ChartMath.nearestPoint`：点击/拖动命中测试。
- `ChartMath.sampleLabelIndices`：超长横轴标签抽样。

## 交互

- 点击选择最近点并显示 Tooltip。
- 横向拖动持续更新最近点与十字辅助线。
- 空数据组件显示占位文案，不触发选择回调。

## K 线与成交量（v0.2）

```kotlin
CandleChart {
    attr {
        data(candles)
        showVolume(true)
        showAxis(true)
        showGrid(true)
        visibleCount(48)
        theme(ChartTheme.FINANCE_DARK)
    }
    event {
        onCandleSelected { index, candle -> println(candle) }
    }
}
```

`CandleMath` 独立负责有效 OHLC 范围、可视窗口、缩放和拖动边界；Canvas 只负责渲染。
窗口算法会忽略非有限 OHLC/成交量，始终将起止索引限制在输入范围内。
