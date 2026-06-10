<template>
  <div class="dashboard">
    <!-- 1. 核心指标区 -->
    <section class="metrics">
      <IndicatorCard label="总比赛量" :value="overview.totalMatches ?? 0" :trend="overview.matchGrowth" />
      <IndicatorCard label="活跃玩家" :value="overview.activePlayers ?? 0" :trend="overview.playerGrowth" />
      <IndicatorCard
        label="全区平均段位"
        :value="overview.avgRankName || '—'"
        :trend="overview.rankGrowth"
      />
      <IndicatorCard label="实时在线人数" :value="overview.onlineNow ?? 0" suffix=" 人" />
    </section>

    <!-- 2. 主体图表区：左 65% + 右 35% -->
    <section class="main">
      <!-- 左侧主区域 -->
      <div class="left-area">
        <!-- 左上大卡片：BP 胜率 -->
        <ChartCard title="版本英雄 BP 胜率分析" class="bp-card">
          <BaseChart :option="bpOption" height="100%" />
        </ChartCard>

        <!-- 左下双列 -->
        <div class="left-bottom">
          <ChartCard title="24 小时英雄热度趋势" class="hot-card">
            <BaseChart :option="hotOption" height="100%" />
          </ChartCard>
          <ChartCard title="全区段位人数分布" class="rank-card">
            <BaseChart :option="rankOption" height="100%" />
          </ChartCard>
        </div>
      </div>

      <!-- 右侧副区域 -->
      <div class="right-area">
        <ChartCard title="版本 T0 热门英雄 TOP 5" class="t0-card">
          <div class="t0-list">
            <div v-for="(c, i) in topChampions" :key="c.championId" class="t0-row">
              <span class="rank" :class="`rank-${i + 1}`">{{ i + 1 }}</span>
              <el-avatar :size="32" :src="c.iconUrl" class="avatar">
                {{ (c.nameCn || c.name || '?').charAt(0) }}
              </el-avatar>
              <span class="name">{{ c.nameCn || c.name }}</span>
              <span class="role-tag">{{ roleLabel(c.role) }}</span>
              <span class="rate num">{{ ((c.pickRate ?? 0) * 100).toFixed(1) }}%</span>
            </div>
            <el-empty v-if="!topChampions.length" :image-size="60" description="暂无数据" />
          </div>
        </ChartCard>

        <ChartCard title="实时上分榜单" class="rank-list-card">
          <div class="rank-list">
            <div v-for="p in topRanks" :key="p.rankNo" class="rank-row">
              <span class="rank" :class="`rank-${p.rankNo}`">{{ p.rankNo }}</span>
              <span class="name">{{ p.summonerName }}</span>
              <span class="tier" :class="`tier-${p.tier}`">{{ tierLabel(p.tier) }} {{ p.rankName }}</span>
              <span class="lp num">{{ p.leaguePoints }} LP</span>
            </div>
            <el-empty v-if="!topRanks.length" :image-size="60" description="暂无数据" />
          </div>
        </ChartCard>

        <ChartCard title="实时滚动赛事" class="realtime-card">
          <div class="realtime-track" ref="trackEl">
            <div v-for="m in realtimeMatches" :key="m.matchId" class="rt-row">
              <span class="rt-id">{{ m.matchId }}</span>
              <span class="rt-blue">{{ m.blueTeamName }}</span>
              <span class="rt-score num">
                <em :class="m.blueScore > m.redScore ? 'win' : ''">{{ m.blueScore }}</em>
                <i>:</i>
                <em :class="m.redScore > m.blueScore ? 'win' : ''">{{ m.redScore }}</em>
              </span>
              <span class="rt-red">{{ m.redTeamName }}</span>
              <span class="rt-status">{{ m.status === 'LIVE' ? '直播中' : '已结束' }}</span>
            </div>
          </div>
        </ChartCard>
      </div>
    </section>

    <!-- 3. 底部公告区 -->
    <footer class="footer">
      <div class="left">
        <el-icon><Bell /></el-icon>
        <span>当前版本 {{ app.currentPatch }} 数据更新于 {{ updateTime }} ｜ 数据源：Riot 官方 API + 自研 Spark 数据仓库</span>
      </div>
      <div class="right">
        <span>游戏：{{ app.currentGame }}</span>
        <span class="sep">·</span>
        <span>共 {{ topChampions.length }} 个英雄 · {{ realtimeMatches.length }} 场赛事</span>
      </div>
    </footer>
  </div>
</template>

<script setup lang="ts">
import { onMounted, onBeforeUnmount, ref, computed, reactive } from 'vue'
import { Bell } from '@element-plus/icons-vue'
import dayjs from 'dayjs'
import IndicatorCard from '@/components/common/IndicatorCard.vue'
import ChartCard from '@/components/common/ChartCard.vue'
import BaseChart from '@/components/chart/BaseChart.vue'
import { dashboardApi } from '@/api/dashboard'
import { useAppStore } from '@/stores/app'
import type {
  OverviewDTO,
  ChampionStatsVO,
  ChampionHotVO,
  RankDistributionDTO,
  TopRankPlayerVO,
  RealtimeMatchVO
} from '@/types/api'

const app = useAppStore()

const overview = reactive<OverviewDTO>({
  totalMatches: 0,
  matchGrowth: 0,
  activePlayers: 0,
  playerGrowth: 0,
  avgRankLevel: 0,
  avgRankName: '—',
  rankGrowth: 0,
  onlineNow: 0
})

const topChampions = ref<ChampionStatsVO[]>([])
const hotTrend = ref<ChampionHotVO[]>([])
const rankDist = ref<RankDistributionDTO[]>([])
const topRanks = ref<TopRankPlayerVO[]>([])
const realtimeMatches = ref<RealtimeMatchVO[]>([])

const updateTime = ref(dayjs().format('YYYY-MM-DD HH:mm:ss'))
let timer: number | null = null

async function load() {
  const g = app.currentGame
  const p = app.currentPatch
  try {
    const [ov, tc, ht, rd, tr, rt] = await Promise.all([
      dashboardApi.overview(g),
      dashboardApi.topChampions(g, p, 5),
      dashboardApi.hotTrend(g, p, 5),
      dashboardApi.rankDistribution(g),
      dashboardApi.topRank(g, 10),
      dashboardApi.realtimeMatches(g, 10)
    ])
    Object.assign(overview, ov)
    topChampions.value = tc || []
    hotTrend.value = ht || []
    rankDist.value = rd || []
    topRanks.value = tr || []
    realtimeMatches.value = rt || []
    updateTime.value = dayjs().format('YYYY-MM-DD HH:mm:ss')
  } catch (e) {
    console.error('Dashboard load error', e)
  }
}

onMounted(() => {
  load()
  timer = window.setInterval(load, 60_000) // 每分钟刷一次
})
onBeforeUnmount(() => { if (timer) clearInterval(timer) })

// ===== 图表 options =====

// BP 胜率横向柱状图
const bpOption = computed(() => {
  const data = topChampions.value.slice(0, 8).reverse()
  return {
    grid: { top: 20, right: 40, bottom: 20, left: 80 },
    tooltip: { trigger: 'axis', axisPointer: { type: 'shadow' } },
    xAxis: {
      type: 'value',
      axisLine: { lineStyle: { color: '#334155' } },
      axisLabel: { color: '#94A3B8' },
      splitLine: { lineStyle: { color: 'rgba(51,65,85,0.5)' } }
    },
    yAxis: {
      type: 'category',
      data: data.map((c) => c.nameCn || c.name),
      axisLine: { lineStyle: { color: '#334155' } },
      axisLabel: { color: '#F8FAFC', fontSize: 12 }
    },
    series: [
      {
        name: '选用率',
        type: 'bar',
        data: data.map((c) => Number(((c.pickRate ?? 0) * 100).toFixed(1))),
        itemStyle: {
          color: {
            type: 'linear', x: 0, y: 0, x2: 1, y2: 0,
            colorStops: [
              { offset: 0, color: '#1677FF' },
              { offset: 1, color: '#4096FF' }
            ]
          },
          borderRadius: [0, 4, 4, 0]
        },
        barWidth: 14,
        label: { show: true, position: 'right', color: '#94A3B8', formatter: '{c}%', fontSize: 11 }
      }
    ]
  }
})

// 24h 热度趋势
const hotOption = computed(() => {
  const byDate: Record<string, ChampionHotVO[]> = {}
  hotTrend.value.forEach((h) => {
    if (!byDate[h.date]) byDate[h.date] = []
    byDate[h.date].push(h)
  })
  const dates = Object.keys(byDate).sort()
  const top5Names = Array.from(
    new Set(hotTrend.value.map((h) => h.nameCn || h.name))
  ).slice(0, 5)
  const series = top5Names.map((name) => ({
    name,
    type: 'line' as const,
    smooth: true,
    symbol: 'circle' as const,
    symbolSize: 6,
    data: dates.map((d) => {
      const row = byDate[d].find((r) => (r.nameCn || r.name) === name)
      return row ? Number(((row.pickRate ?? 0) * 100).toFixed(1)) : 0
    })
  }))
  return {
    grid: { top: 30, right: 16, bottom: 30, left: 36 },
    tooltip: { trigger: 'axis' },
    legend: { textStyle: { color: '#94A3B8', fontSize: 11 }, top: 0, itemWidth: 14, itemHeight: 8 },
    xAxis: {
      type: 'category',
      data: dates.map((d) => d.slice(5)),
      axisLine: { lineStyle: { color: '#334155' } },
      axisLabel: { color: '#94A3B8', fontSize: 11 }
    },
    yAxis: {
      type: 'value',
      axisLabel: { color: '#94A3B8', fontSize: 11, formatter: '{value}%' },
      splitLine: { lineStyle: { color: 'rgba(51,65,85,0.5)' } }
    },
    series,
    color: ['#1677FF', '#10B981', '#F59E0B', '#EF4444', '#A78BFA']
  }
})

// 段位饼图
const rankOption = computed(() => {
  const data = rankDist.value
    .filter((r) => r.count > 0)
    .map((r) => ({ name: tierLabel(r.tier), value: r.count }))
  return {
    tooltip: { trigger: 'item', formatter: '{b}: {c} ({d}%)' },
    legend: {
      type: 'scroll',
      orient: 'vertical',
      right: 10,
      top: 'middle',
      textStyle: { color: '#94A3B8', fontSize: 11 }
    },
    series: [
      {
        name: '段位分布',
        type: 'pie',
        radius: ['45%', '72%'],
        center: ['38%', '50%'],
        avoidLabelOverlap: true,
        itemStyle: { borderRadius: 4, borderColor: '#1E293B', borderWidth: 2 },
        label: { show: true, color: '#F8FAFC', fontSize: 11, formatter: '{b}\n{d}%' },
        labelLine: { lineStyle: { color: '#334155' } },
        data
      }
    ],
    color: ['#94A3B8', '#A16207', '#94A3B8', '#F59E0B', '#1677FF', '#10B981', '#A78BFA', '#F472B6', '#EF4444', '#FACC15']
  }
})

// ===== 工具 =====
const TIER_MAP: Record<string, string> = {
  IRON: '黑铁', BRONZE: '青铜', SILVER: '白银', GOLD: '黄金',
  PLATINUM: '铂金', EMERALD: '翡翠', DIAMOND: '钻石',
  MASTER: '大师', GRANDMASTER: '宗师', CHALLENGER: '王者'
}
const ROLE_MAP: Record<string, string> = {
  top: '上单', jungle: '打野', mid: '中单', adc: 'ADC', support: '辅助'
}
const tierLabel = (t: string) => TIER_MAP[t?.toUpperCase()] || t
const roleLabel = (r: string) => ROLE_MAP[r] || r
</script>

<style scoped lang="scss">
.dashboard {
  display: flex;
  flex-direction: column;
  gap: $space-card;
  height: calc(100vh - #{$height-nav} - 32px);
}

.metrics {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: $space-card;
}

.main {
  flex: 1;
  display: grid;
  grid-template-columns: 65fr 35fr;
  gap: $space-card;
  min-height: 0;
}

.left-area {
  display: grid;
  grid-template-rows: 6fr 4fr;
  gap: $space-card;
  min-height: 0;
}
.bp-card { min-height: 0; }
.left-bottom {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: $space-card;
  min-height: 0;
}
.hot-card, .rank-card { min-height: 0; }

.right-area {
  display: grid;
  grid-template-rows: 1fr 1.1fr 1.1fr;
  gap: $space-card;
  min-height: 0;
}
.t0-card, .rank-list-card, .realtime-card { min-height: 0; }

.t0-list, .rank-list {
  height: 100%;
  overflow-y: auto;
  display: flex;
  flex-direction: column;
  gap: 6px;
  padding: 4px 0;
}
.t0-row, .rank-row {
  display: grid;
  grid-template-columns: 28px 32px 1fr 56px 80px;
  align-items: center;
  gap: 10px;
  padding: 6px 8px;
  background: rgba(15, 23, 42, 0.4);
  border-radius: 4px;
  font-size: 13px;
}
.rank-row {
  grid-template-columns: 28px 1fr 90px 80px;
}
.t0-row .avatar { background: $color-bg-page; }
.t0-row .name, .rank-row .name { color: $color-text-primary; font-weight: 500; }
.t0-row .role-tag {
  font-size: 11px;
  color: $color-text-secondary;
  background: rgba(167, 139, 250, 0.15);
  padding: 1px 6px;
  border-radius: 3px;
}
.t0-row .rate { color: $color-primary; font-weight: 700; text-align: right; }

.rank {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  width: 24px; height: 24px;
  border-radius: 50%;
  background: $color-border;
  color: $color-text-secondary;
  font-weight: 700;
  font-size: 12px;
  &.rank-1 { background: linear-gradient(135deg, #FFD700, #FFA500); color: #1A1A1A; }
  &.rank-2 { background: linear-gradient(135deg, #C0C0C0, #808080); color: #1A1A1A; }
  &.rank-3 { background: linear-gradient(135deg, #CD7F32, #8B4513); color: #fff; }
}
.tier {
  font-size: 12px;
  padding: 2px 6px;
  border-radius: 3px;
  background: rgba(255,255,255,0.05);
  &.tier-CHALLENGER { color: #F59E0B; background: rgba(245, 158, 11, 0.1); }
  &.tier-GRANDMASTER { color: #EF4444; background: rgba(239, 68, 68, 0.1); }
  &.tier-MASTER { color: #A78BFA; background: rgba(167, 139, 250, 0.1); }
  &.tier-DIAMOND { color: #1677FF; background: rgba(22, 119, 255, 0.1); }
  &.tier-EMERALD { color: #10B981; background: rgba(16, 185, 129, 0.1); }
  &.tier-PLATINUM { color: #06B6D4; background: rgba(6, 182, 212, 0.1); }
  &.tier-GOLD { color: #FACC15; background: rgba(250, 204, 21, 0.1); }
}
.lp { color: $color-primary; font-weight: 700; text-align: right; }

.realtime-track {
  height: 100%;
  overflow: hidden;
  position: relative;
}
.rt-row {
  display: grid;
  grid-template-columns: 90px 1fr 80px 1fr 60px;
  align-items: center;
  gap: 8px;
  padding: 6px 8px;
  border-bottom: 1px dashed rgba(51, 65, 85, 0.4);
  font-size: 12px;
}
.rt-id { color: $color-text-secondary; font-family: $font-family-num; }
.rt-blue { color: #1677FF; text-align: right; }
.rt-red  { color: #EF4444; text-align: left; }
.rt-score {
  text-align: center;
  em { font-style: normal; padding: 0 4px; color: $color-text-secondary; }
  em.win { color: $color-success; font-weight: 700; }
  i { color: $color-text-secondary; font-style: normal; }
}
.rt-status { color: $color-text-secondary; text-align: right; font-size: 11px; }

.footer {
  height: $height-footer;
  background: $color-bg-card;
  border: 1px solid $color-border;
  border-radius: $radius-card;
  box-shadow: $shadow-card;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 24px;
  font-size: $font-size-caption;
  color: $color-text-secondary;
  .left { display: flex; align-items: center; gap: 8px; }
  .right { display: flex; gap: 12px; }
  .sep { color: $color-border; }
}
</style>
