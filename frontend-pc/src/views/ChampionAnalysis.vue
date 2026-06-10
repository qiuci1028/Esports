<template>
  <div class="champion-page">
    <!-- 1. 筛选栏 -->
    <section class="filter-bar biz-card">
      <span class="label">游戏：</span>
      <el-select v-model="filters.game" style="width: 140px" @change="onGameChange">
        <el-option label="英雄联盟 LOL" value="LOL" />
        <el-option label="无畏契约 VAL" value="VALORANT" />
        <el-option label="云顶之弈 TFT" value="TFT" />
      </el-select>
      <span class="label">模式：</span>
      <el-select v-model="filters.gameMode" style="width: 120px" clearable>
        <el-option label="经典" value="CLASSIC" />
        <el-option label="排位" value="RANKED" />
        <el-option label="竞技" value="COMPETITIVE" />
      </el-select>
      <span class="label">版本：</span>
      <el-select v-model="filters.patch" style="width: 120px">
        <el-option v-for="p in patches" :key="p" :value="p" :label="p" />
      </el-select>
      <span class="label">位置：</span>
      <el-select v-model="filters.role" style="width: 120px" clearable>
        <el-option v-for="r in roleOpts" :key="r.value" :value="r.value" :label="r.label" />
      </el-select>
      <el-button type="primary" :icon="Refresh" @click="load">刷新</el-button>
    </section>

    <!-- 2. 汇总指标 -->
    <section class="metrics">
      <IndicatorCard label="平均出场率" :value="metrics.avgPick" suffix="%" :decimals="2" :thousand="false" />
      <IndicatorCard label="综合胜率" :value="metrics.avgWin" suffix="%" :decimals="2" :thousand="false" />
      <IndicatorCard label="平均禁用率" :value="metrics.avgBan" suffix="%" :decimals="2" :thousand="false" />
      <IndicatorCard label="热门英雄数" :value="metrics.popularCount" />
    </section>

    <!-- 3. 三栏图表区 -->
    <section class="charts">
      <ChartCard title="全英雄 BP 胜率排行 TOP 15" class="c1">
        <BaseChart :option="rankOption" height="100%" />
      </ChartCard>
      <ChartCard title="英雄属性分布（雷达）" class="c2">
        <BaseChart :option="radarOption" height="100%" />
      </ChartCard>
      <ChartCard title="每日热度变化" class="c3">
        <BaseChart :option="trendOption" height="100%" />
      </ChartCard>
    </section>

    <!-- 4. 详情列表 -->
    <section class="table-card biz-card">
      <div class="head">
        <span class="title"><i class="title-bar" /> 英雄详细数据</span>
        <span class="subtitle">共 {{ filteredList.length }} 条</span>
      </div>
      <el-table :data="filteredList" stripe :max-height="420" :default-sort="{ prop: 'pickRate', order: 'descending' }">
        <el-table-column type="index" label="#" width="60" align="center" />
        <el-table-column label="英雄" min-width="160">
          <template #default="{ row }">
            <span class="champ-cell">
              <el-avatar :size="24" :src="row.iconUrl">
                {{ (row.nameCn || row.name || '?').charAt(0) }}
              </el-avatar>
              <span>{{ row.nameCn || row.name }}</span>
            </span>
          </template>
        </el-table-column>
        <el-table-column prop="role" label="位置" width="80" align="center">
          <template #default="{ row }">
            <el-tag size="small" effect="plain">{{ roleLabel(row.role) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="games" label="场次" width="90" align="center" sortable />
        <el-table-column prop="pickRate" label="出场率" width="120" align="center" sortable>
          <template #default="{ row }">
            <div class="rate-bar">
              <div class="bar"><div class="fill pick" :style="{ width: pct(row.pickRate) + '%' }" /></div>
              <span class="num">{{ pct(row.pickRate) }}%</span>
            </div>
          </template>
        </el-table-column>
        <el-table-column prop="banRate" label="禁用率" width="120" align="center" sortable>
          <template #default="{ row }">
            <div class="rate-bar">
              <div class="bar"><div class="fill ban" :style="{ width: pct(row.banRate) + '%' }" /></div>
              <span class="num">{{ pct(row.banRate) }}%</span>
            </div>
          </template>
        </el-table-column>
        <el-table-column prop="winRate" label="胜率" width="100" align="center" sortable>
          <template #default="{ row }">
            <span :class="winrateClass(row.winRate)">{{ pct(row.winRate) }}%</span>
          </template>
        </el-table-column>
        <el-table-column prop="wins" label="胜场" width="80" align="center" />
        <el-table-column label="操作" width="100" align="center">
          <template #default="{ row }">
            <el-button link type="primary" size="small" @click="onDetailRow(row)">详情</el-button>
          </template>
        </el-table-column>
      </el-table>
    </section>

    <!-- 详情弹窗 -->
    <el-dialog v-model="detailVisible" :title="`英雄详情 - ${currentChamp?.nameCn || currentChamp?.name}`" width="500px">
      <div v-if="currentChamp" class="detail-body">
        <p>位置：<strong>{{ roleLabel(currentChamp.role) }}</strong></p>
        <p>版本：<strong>{{ currentChamp.patchCode }}</strong></p>
        <p>场次：<strong>{{ currentChamp.games }}</strong>（{{ currentChamp.wins }} 胜）</p>
        <p>出场率：<strong class="text-brand">{{ pct(currentChamp.pickRate) }}%</strong></p>
        <p>禁用率：<strong class="text-danger">{{ pct(currentChamp.banRate) }}%</strong></p>
        <p>胜率：<strong :class="winrateClass(currentChamp.winRate)">{{ pct(currentChamp.winRate) }}%</strong></p>
      </div>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, computed, onMounted, watch } from 'vue'
import { Refresh } from '@element-plus/icons-vue'
import IndicatorCard from '@/components/common/IndicatorCard.vue'
import ChartCard from '@/components/common/ChartCard.vue'
import BaseChart from '@/components/chart/BaseChart.vue'
import { championApi } from '@/api/champion'
import type { ChampionStatsVO, ChampionHotVO } from '@/types/api'
import { useAppStore } from '@/stores/app'

const app = useAppStore()

const filters = reactive({
  game: 'LOL',
  gameMode: '',
  patch: '14.10',
  role: ''
})

const patches = computed(() => {
  if (filters.game === 'LOL') return ['14.10', '14.09', '14.08', '14.07']
  if (filters.game === 'VALORANT') return ['9.0', '8.11']
  return ['S10', 'S9.5']
})

const roleOpts = [
  { value: 'top', label: '上单' },
  { value: 'jungle', label: '打野' },
  { value: 'mid', label: '中单' },
  { value: 'adc', label: 'ADC' },
  { value: 'support', label: '辅助' }
]

const ROLE_MAP: Record<string, string> = {
  top: '上单', jungle: '打野', mid: '中单', adc: 'ADC', support: '辅助'
}
const roleLabel = (r?: string) => (r ? ROLE_MAP[r] || r : '—')
const pct = (v?: number) => ((v ?? 0) * 100).toFixed(1)
const winrateClass = (v?: number) => {
  const w = v ?? 0
  return w >= 0.52 ? 'text-success' : w <= 0.48 ? 'text-danger' : 'text-warning'
}

const list = ref<ChampionStatsVO[]>([])
const hotTrend = ref<ChampionHotVO[]>([])
const loading = ref(false)

async function load() {
  loading.value = true
  try {
    const [a, b] = await Promise.all([
      championApi.list({ game: filters.game, patch: filters.patch, role: filters.role, limit: 50 }),
      championApi.hotTrend(filters.game, filters.patch, 5)
    ])
    list.value = a || []
    hotTrend.value = b || []
  } finally {
    loading.value = false
  }
}

function onGameChange(g: string) {
  filters.patch = g === 'LOL' ? '14.10' : g === 'VALORANT' ? '9.0' : 'S10'
  load()
}

watch(() => [filters.patch, filters.role], load)

const filteredList = computed(() => list.value)

const metrics = computed(() => {
  if (!list.value.length) return { avgPick: 0, avgWin: 0, avgBan: 0, popularCount: 0 }
  const sum = list.value.reduce(
    (acc, c) => ({
      pick: acc.pick + (c.pickRate ?? 0),
      win: acc.win + (c.winRate ?? 0),
      ban: acc.ban + (c.banRate ?? 0)
    }),
    { pick: 0, win: 0, ban: 0 }
  )
  return {
    avgPick: Number(((sum.pick / list.value.length) * 100).toFixed(2)),
    avgWin: Number(((sum.win / list.value.length) * 100).toFixed(2)),
    avgBan: Number(((sum.ban / list.value.length) * 100).toFixed(2)),
    popularCount: list.value.filter((c) => (c.pickRate ?? 0) >= 0.1).length
  }
})

// ===== Charts =====
const rankOption = computed(() => {
  const top = list.value.slice().sort((a, b) => (b.pickRate ?? 0) - (a.pickRate ?? 0)).slice(0, 15).reverse()
  return {
    grid: { top: 10, right: 30, bottom: 10, left: 90 },
    tooltip: { trigger: 'axis', axisPointer: { type: 'shadow' } },
    xAxis: { type: 'value', axisLabel: { color: '#94A3B8', formatter: '{value}%' }, splitLine: { lineStyle: { color: 'rgba(51,65,85,0.5)' } } },
    yAxis: {
      type: 'category',
      data: top.map((c) => c.nameCn || c.name),
      axisLabel: { color: '#F8FAFC', fontSize: 11 }
    },
    series: [
      {
        type: 'bar',
        data: top.map((c) => Number(((c.pickRate ?? 0) * 100).toFixed(1))),
        itemStyle: { color: { type: 'linear', x: 0, y: 0, x2: 1, y2: 0, colorStops: [{ offset: 0, color: '#1677FF' }, { offset: 1, color: '#4096FF' }] }, borderRadius: [0, 4, 4, 0] },
        barWidth: 12
      }
    ]
  }
})

const radarOption = computed(() => {
  // 取前 6 维指标：出场率、胜率、禁用率、场次、胜场、热门度
  const top = list.value.slice(0, 6)
  const indicators = [
    { name: '出场率', max: 50 },
    { name: '胜率', max: 60 },
    { name: '禁用率', max: 30 },
    { name: '场次', max: 300 },
    { name: '胜场', max: 200 },
    { name: '热度', max: 100 }
  ]
  return {
    tooltip: {},
    legend: { textStyle: { color: '#94A3B8', fontSize: 11 }, top: 0, itemWidth: 12, itemHeight: 8 },
    radar: {
      indicator: indicators,
      shape: 'polygon',
      splitNumber: 4,
      axisName: { color: '#F8FAFC', fontSize: 11 },
      splitLine: { lineStyle: { color: 'rgba(51,65,85,0.5)' } },
      splitArea: { areaStyle: { color: ['rgba(22,119,255,0.02)', 'rgba(22,119,255,0.05)'] } }
    },
    series: [
      {
        type: 'radar',
        data: top.map((c) => ({
          name: c.nameCn || c.name,
          value: [
            Number(((c.pickRate ?? 0) * 100).toFixed(1)),
            Number(((c.winRate ?? 0) * 100).toFixed(1)),
            Number(((c.banRate ?? 0) * 100).toFixed(1)),
            c.games ?? 0,
            c.wins ?? 0,
            Math.min(100, (c.pickRate ?? 0) * 100 * 3)
          ],
          areaStyle: { opacity: 0.15 }
        }))
      }
    ],
    color: ['#1677FF', '#10B981', '#F59E0B', '#EF4444', '#A78BFA', '#06B6D4']
  }
})

const trendOption = computed(() => {
  const byDate: Record<string, ChampionHotVO[]> = {}
  hotTrend.value.forEach((h) => {
    if (!byDate[h.date]) byDate[h.date] = []
    byDate[h.date].push(h)
  })
  const dates = Object.keys(byDate).sort()
  const names = Array.from(new Set(hotTrend.value.map((h) => h.nameCn || h.name)))
  return {
    grid: { top: 30, right: 16, bottom: 30, left: 36 },
    tooltip: { trigger: 'axis' },
    legend: { top: 0, textStyle: { color: '#94A3B8', fontSize: 11 } },
    xAxis: { type: 'category', data: dates.map((d) => d.slice(5)), axisLabel: { color: '#94A3B8' } },
    yAxis: { type: 'value', axisLabel: { color: '#94A3B8', formatter: '{value}%' }, splitLine: { lineStyle: { color: 'rgba(51,65,85,0.5)' } } },
    series: names.map((n) => ({
      name: n,
      type: 'line',
      smooth: true,
      data: dates.map((d) => {
        const row = byDate[d].find((r) => (r.nameCn || r.name) === n)
        return row ? Number(((row.pickRate ?? 0) * 100).toFixed(1)) : 0
      })
    })),
    color: ['#1677FF', '#10B981', '#F59E0B', '#EF4444', '#A78BFA']
  }
})

// 详情弹窗
const detailVisible = ref(false)
const currentChamp = ref<ChampionStatsVO | null>(null)
function onDetail(c: ChampionStatsVO) {
  currentChamp.value = c
  detailVisible.value = true
}
function onDetailRow(row: unknown) {
  onDetail(row as ChampionStatsVO)
}

onMounted(() => {
  // 同步 app store
  app.setGame('LOL')
  app.setPatch(filters.patch)
  load()
})
</script>

<style scoped lang="scss">
.champion-page {
  display: flex;
  flex-direction: column;
  gap: $space-card;
}
.filter-bar {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 12px 20px;
  .label { font-size: 13px; color: $color-text-secondary; white-space: nowrap; }
}
.metrics {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: $space-card;
}
.charts {
  display: grid;
  grid-template-columns: 3fr 2fr 2fr;
  gap: $space-card;
  height: 360px;
  > * { min-height: 0; }
}
.table-card {
  padding: 16px 20px;
  .head {
    display: flex;
    align-items: center;
    justify-content: space-between;
    margin-bottom: 12px;
  }
  .title {
    font-size: 20px;
    font-weight: 700;
    color: $color-text-primary;
    display: flex;
    align-items: center;
    gap: 8px;
  }
  .title-bar { display: inline-block; width: 4px; height: 16px; background: $color-primary; border-radius: 2px; }
  .subtitle { color: $color-text-secondary; font-size: 12px; }
}
.champ-cell { display: flex; align-items: center; gap: 8px; }
.rate-bar {
  display: flex;
  align-items: center;
  gap: 8px;
  .bar { flex: 1; height: 6px; background: rgba(255,255,255,0.05); border-radius: 3px; overflow: hidden; }
  .fill { height: 100%; border-radius: 3px; transition: width 0.3s; }
  .fill.pick { background: linear-gradient(90deg, #1677FF, #4096FF); }
  .fill.ban  { background: linear-gradient(90deg, #EF4444, #F87171); }
  .num { width: 50px; text-align: right; font-family: $font-family-num; }
}
.detail-body {
  p { margin: 8px 0; font-size: 14px; color: $color-text-secondary; }
  strong { color: $color-text-primary; margin-left: 8px; }
}
</style>
