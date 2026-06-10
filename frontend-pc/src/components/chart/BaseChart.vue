<template>
  <div ref="chartEl" :style="{ width: '100%', height: height }" />
</template>

<script setup lang="ts">
import { onMounted, onBeforeUnmount, ref, watch, nextTick } from 'vue'
import * as echarts from 'echarts/core'
import {
  BarChart, LineChart, PieChart, RadarChart, ScatterChart, HeatmapChart, SankeyChart
} from 'echarts/charts'
import {
  TitleComponent, TooltipComponent, GridComponent, LegendComponent,
  DataZoomComponent, ToolboxComponent, MarkLineComponent, MarkAreaComponent
} from 'echarts/components'
import { CanvasRenderer } from 'echarts/renderers'

// 按需注册
echarts.use([
  BarChart, LineChart, PieChart, RadarChart, ScatterChart, HeatmapChart, SankeyChart,
  TitleComponent, TooltipComponent, GridComponent, LegendComponent,
  DataZoomComponent, ToolboxComponent, MarkLineComponent, MarkAreaComponent,
  CanvasRenderer
])

interface Props {
  option: Record<string, unknown>
  height?: string
  /** 自动 resize 监听 */
  autoResize?: boolean
}

const props = withDefaults(defineProps<Props>(), {
  height: '320px',
  autoResize: true
})

const chartEl = ref<HTMLDivElement>()
let chart: echarts.ECharts | null = null
let ro: ResizeObserver | null = null

function init() {
  if (!chartEl.value) return
  chart = echarts.init(chartEl.value, undefined, { renderer: 'canvas' })
  chart.setOption(props.option)
}

onMounted(() => {
  nextTick(init)
  if (props.autoResize && chartEl.value) {
    ro = new ResizeObserver(() => chart?.resize())
    ro.observe(chartEl.value)
  }
})

onBeforeUnmount(() => {
  ro?.disconnect()
  chart?.dispose()
  chart = null
})

watch(
  () => props.option,
  (opt) => chart?.setOption(opt, true),
  { deep: true }
)
</script>
