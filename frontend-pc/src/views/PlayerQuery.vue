<template>
  <div class="player-page">
    <!-- 1. 搜索筛选区 -->
    <section class="search-bar biz-card">
      <el-select v-model="filters.game" placeholder="选择游戏" style="width: 140px">
        <el-option label="英雄联盟 LOL" value="LOL" />
        <el-option label="无畏契约 VALORANT" value="VALORANT" />
        <el-option label="云顶之弈 TFT" value="TFT" />
      </el-select>
      <el-select v-model="filters.region" placeholder="大区" clearable style="width: 120px">
        <el-option v-for="r in regions" :key="r" :value="r" :label="r.toUpperCase()" />
      </el-select>
      <el-input
        v-model="filters.summonerName"
        placeholder="输入召唤师名（模糊搜索）"
        clearable
        style="width: 280px"
        @keyup.enter="onSearch"
      >
        <template #prefix><el-icon><Search /></el-icon></template>
      </el-input>
      <el-button type="primary" :icon="Search" @click="onSearch">查询</el-button>
      <el-button :icon="RefreshLeft" @click="onReset">重置</el-button>
    </section>

    <!-- 2. 玩家基础信息卡 -->
    <section v-if="detail" class="info-card biz-card">
      <div class="left">
        <el-avatar :size="80" :src="detail.basic.profileIconUrl" class="avatar">
          {{ (detail.basic.summonerName || '?').charAt(0) }}
        </el-avatar>
      </div>
      <div class="right">
        <h1 class="player-name">{{ detail.basic.summonerName }}
          <el-tag size="small" type="info" effect="dark">{{ detail.basic.game }}</el-tag>
          <el-tag size="small" effect="dark">{{ detail.basic.region.toUpperCase() }}</el-tag>
          <el-tag size="small" effect="plain">Lv.{{ detail.basic.level }}</el-tag>
        </h1>
        <p class="meta">
          PUUID: <code>{{ detail.basic.puuid }}</code>
          · 最近活跃：{{ detail.basic.lastActive || '—' }}
        </p>
        <p class="rank">
          当前段位：
          <strong :class="`tier-${detail.currentRank?.tier}`" class="tier-text">
            {{ tierLabel(detail.currentRank?.tier) }} {{ detail.currentRank?.rankTier }}
          </strong>
          <span class="lp num">{{ detail.currentRank?.leaguePoints ?? 0 }} LP</span>
          <span class="winrate">综合胜率 <strong>{{ winrateText }}</strong></span>
          <span class="wlt">总场次 <strong>{{ detail.kdaList.length }}</strong></span>
        </p>
        <p class="role">
          主玩位置：<strong>{{ roleLabel(detail.mainRole) }}</strong>
          · 英雄池广度：<strong>{{ detail.heroPoolSize }}</strong>
          · 最活跃时段：<strong>{{ peakHour }}:00</strong>
        </p>
      </div>
    </section>

    <!-- 3. 4 图分析 -->
    <section v-if="detail" class="charts">
      <ChartCard title="英雄熟练度 TOP 5" class="c1">
        <BaseChart :option="masteryOption" height="100%" />
      </ChartCard>
      <ChartCard title="近 10 天段位走势" class="c2">
        <BaseChart :option="rankOption" height="100%" />
      </ChartCard>
      <ChartCard title="近 20 场 KDA" class="c3">
        <BaseChart :option="kdaOption" height="100%" />
      </ChartCard>
      <ChartCard title="活跃时段分布（24h）" class="c4">
        <BaseChart :option="activeOption" height="100%" />
      </ChartCard>
    </section>

    <!-- 4. 比赛记录表 -->
    <section v-if="detail && detail.kdaList.length" class="matches biz-card">
      <div class="matches-head">
        <span class="title">
          <i class="title-bar" /> 近期比赛记录
        </span>
        <span class="subtitle">近 {{ detail.kdaList.length }} 场</span>
      </div>
      <el-table :data="detail.kdaList" stripe style="width: 100%" :max-height="380">
        <el-table-column prop="matchId" label="比赛 ID" min-width="120" />
        <el-table-column label="英雄" min-width="140">
          <template #default="{ row }">
            <span class="champ-cell">
              <el-avatar :size="24" :src="championIcon(row.championId)">
                {{ (row.championId || '?').slice(0, 2) }}
              </el-avatar>
              <span>{{ row.championId }}</span>
            </span>
          </template>
        </el-table-column>
        <el-table-column label="结果" width="80" align="center">
          <template #default="{ row }">
            <el-tag :type="row.win === 1 ? 'success' : 'danger'" effect="dark" size="small">
              {{ row.win === 1 ? '胜利' : '失败' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="KDA" min-width="140" align="center">
          <template #default="{ row }">
            <span class="kda">
              <em class="k">{{ row.kills }}</em> /
              <em class="d">{{ row.deaths }}</em> /
              <em class="a">{{ row.assists }}</em>
            </span>
          </template>
        </el-table-column>
        <el-table-column label="KDA 比" min-width="100" align="center">
          <template #default="{ row }">
            <span :class="kdaClass(row)">{{ kdaRatio(row).toFixed(2) }}</span>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="80" align="center">
          <template #default="{ row }">
            <el-button link type="primary" size="small" @click="goMatch(row.matchId)">详情</el-button>
          </template>
        </el-table-column>
      </el-table>
    </section>

    <el-empty v-if="!detail && searched" description="未找到该玩家，请检查游戏/大区/名称" />
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { Search, RefreshLeft } from '@element-plus/icons-vue'
import ChartCard from '@/components/common/ChartCard.vue'
import BaseChart from '@/components/chart/BaseChart.vue'
import { playerApi } from '@/api/player'
import type { PlayerDetailDTO } from '@/types/api'

const router = useRouter()

const filters = reactive({
  game: 'LOL',
  region: 'kr',
  summonerName: ''
})

const regions = ['kr', 'na1', 'euw1', 'eun1', 'br1', 'jp1', 'ru', 'oce1', 'la1', 'la2', 'tr1']

const detail = ref<PlayerDetailDTO | null>(null)
const searched = ref(false)
const loading = ref(false)

async function onSearch() {
  if (!filters.summonerName.trim()) {
    ElMessage.warning('请输入召唤师名')
    return
  }
  loading.value = true
  searched.value = true
  try {
    const list = await playerApi.search(filters.summonerName.trim(), 20)
    if (!list || list.length === 0) {
      detail.value = null
      return
    }
    // 取第一个同名玩家（实际可让用户选择）
    const p = list.find((x) => x.game === filters.game) || list[0]
    const d = await playerApi.detail(p.puuid, filters.game)
    detail.value = d
  } catch (e) {
    console.error(e)
    detail.value = null
  } finally {
    loading.value = false
  }
}

function onReset() {
  filters.summonerName = ''
  detail.value = null
  searched.value = false
}

function goMatch(id: string) {
  router.push(`/match/${id}`)
}

const winrateText = computed(() => {
  if (!detail.value?.winRate && detail.value?.winRate !== 0) return '—'
  return `${(Number(detail.value.winRate) * 100).toFixed(1)}%`
})

const peakHour = computed(() => {
  const arr = detail.value?.activeHour || []
  if (!arr.length) return '—'
  return arr.reduce((max, x) => (x.gameCount > max.gameCount ? x : max)).hour
})

// === 图表 options ===
const masteryOption = computed(() => {
  const data = detail.value?.topMastery || []
  return {
    grid: { top: 10, right: 30, bottom: 10, left: 90 },
    tooltip: { trigger: 'axis' },
    xAxis: { type: 'value', axisLabel: { color: '#94A3B8' }, splitLine: { lineStyle: { color: 'rgba(51,65,85,0.5)' } } },
    yAxis: {
      type: 'category',
      data: data.map((d) => d.championName).reverse(),
      axisLabel: { color: '#F8FAFC', fontSize: 12 }
    },
    series: [
      {
        type: 'bar',
        data: data.map((d) => d.masteryPoints).reverse(),
        itemStyle: { color: { type: 'linear', x: 0, y: 0, x2: 1, y2: 0, colorStops: [{ offset: 0, color: '#10B981' }, { offset: 1, color: '#34D399' }] }, borderRadius: [0, 4, 4, 0] },
        barWidth: 14,
        label: { show: true, position: 'right', color: '#94A3B8', fontSize: 11, formatter: 'Lv{c}' }
      }
    ]
  }
})

const rankOption = computed(() => {
  const data = (detail.value?.rankHistory || []).map((r) => ({
    date: r.snapshotDate,
    lp: r.leaguePoints,
    tier: r.tier
  }))
  return {
    grid: { top: 20, right: 16, bottom: 30, left: 40 },
    tooltip: { trigger: 'axis' },
    xAxis: {
      type: 'category',
      data: data.map((d) => d.date.slice(5)),
      axisLine: { lineStyle: { color: '#334155' } },
      axisLabel: { color: '#94A3B8' }
    },
    yAxis: {
      type: 'value',
      axisLabel: { color: '#94A3B8' },
      splitLine: { lineStyle: { color: 'rgba(51,65,85,0.5)' } }
    },
    series: [
      {
        type: 'line',
        smooth: true,
        data: data.map((d) => d.lp),
        symbol: 'circle',
        symbolSize: 6,
        lineStyle: { color: '#1677FF', width: 2 },
        itemStyle: { color: '#1677FF' },
        areaStyle: { color: { type: 'linear', x: 0, y: 0, x2: 0, y2: 1, colorStops: [{ offset: 0, color: 'rgba(22,119,255,0.3)' }, { offset: 1, color: 'rgba(22,119,255,0)' }] } }
      }
    ]
  }
})

const kdaOption = computed(() => {
  const list = (detail.value?.kdaList || []).slice(0, 20).reverse()
  return {
    grid: { top: 30, right: 16, bottom: 30, left: 36 },
    tooltip: { trigger: 'axis' },
    legend: { top: 0, textStyle: { color: '#94A3B8' } },
    xAxis: {
      type: 'category',
      data: list.map((_, i) => `#${i + 1}`),
      axisLine: { lineStyle: { color: '#334155' } },
      axisLabel: { color: '#94A3B8' }
    },
    yAxis: { type: 'value', axisLabel: { color: '#94A3B8' }, splitLine: { lineStyle: { color: 'rgba(51,65,85,0.5)' } } },
    series: [
      { name: '击杀', type: 'bar', stack: 'kda', data: list.map((d) => d.kills), itemStyle: { color: '#10B981' } },
      { name: '死亡', type: 'bar', stack: 'kda', data: list.map((d) => -d.deaths), itemStyle: { color: '#EF4444' } },
      { name: '助攻', type: 'bar', data: list.map((d) => d.assists), itemStyle: { color: '#1677FF' } }
    ]
  }
})

const activeOption = computed(() => {
  const data = detail.value?.activeHour || []
  return {
    tooltip: { trigger: 'axis' },
    grid: { top: 20, right: 16, bottom: 30, left: 36 },
    xAxis: {
      type: 'category',
      data: data.map((d) => `${d.hour}时`),
      axisLine: { lineStyle: { color: '#334155' } },
      axisLabel: { color: '#94A3B8', interval: 1 }
    },
    yAxis: { type: 'value', axisLabel: { color: '#94A3B8' }, splitLine: { lineStyle: { color: 'rgba(51,65,85,0.5)' } } },
    series: [
      {
        type: 'bar',
        data: data.map((d) => d.gameCount),
        itemStyle: {
          color: { type: 'linear', x: 0, y: 0, x2: 0, y2: 1, colorStops: [{ offset: 0, color: '#F59E0B' }, { offset: 1, color: 'rgba(245,158,11,0.2)' }] },
          borderRadius: [4, 4, 0, 0]
        },
        barWidth: 10
      }
    ]
  }
})

// === 工具 ===
const TIER_MAP: Record<string, string> = {
  IRON: '黑铁', BRONZE: '青铜', SILVER: '白银', GOLD: '黄金',
  PLATINUM: '铂金', EMERALD: '翡翠', DIAMOND: '钻石',
  MASTER: '大师', GRANDMASTER: '宗师', CHALLENGER: '王者'
}
const ROLE_MAP: Record<string, string> = {
  top: '上单', jungle: '打野', mid: '中单', adc: 'ADC', support: '辅助'
}
const tierLabel = (t?: string) => (t ? TIER_MAP[t.toUpperCase()] || t : '未定级')
const roleLabel = (r?: string) => (r ? ROLE_MAP[r] || r : '—')
const kdaRatio = (row: any) => (row.deaths ? (row.kills + row.assists) / row.deaths : row.kills + row.assists)
const kdaClass = (row: any) => {
  const r = kdaRatio(row)
  return r >= 3 ? 'kda-s' : r >= 2 ? 'kda-a' : r >= 1 ? 'kda-b' : 'kda-c'
}
const championIcon = (id: string) =>
  `https://ddragon.leagueoflegends.com/cdn/14.10.1/img/champion/${id}.png`

onMounted(() => {
  // 首次进入自动查询一个示例玩家
  if (!detail.value) {
    filters.summonerName = 'Faker'
    onSearch()
  }
})
</script>

<style scoped lang="scss">
.player-page {
  display: flex;
  flex-direction: column;
  gap: $space-card;
}

.search-bar {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 16px;
  padding: 16px 24px;
}

.info-card {
  display: flex;
  gap: 24px;
  padding: 24px;
  .left { display: flex; align-items: center; }
  .avatar { background: $color-primary; color: #fff; font-weight: 700; }
  .right { flex: 1; }
  .player-name {
    margin: 0 0 8px 0;
    font-size: 28px;
    font-weight: 700;
    display: flex;
    align-items: center;
    gap: 8px;
    color: $color-text-primary;
  }
  .meta {
    margin: 0 0 8px 0;
    font-size: 12px;
    color: $color-text-secondary;
    code { color: $color-text-primary; font-family: $font-family-num; }
  }
  .rank, .role {
    margin: 4px 0;
    font-size: 14px;
    color: $color-text-secondary;
    strong { color: $color-text-primary; }
    .lp { color: $color-primary; font-weight: 700; margin-left: 12px; }
    .winrate, .wlt { margin-left: 16px; }
  }
  .tier-text {
    font-size: 18px;
    &.tier-CHALLENGER { color: #F59E0B; }
    &.tier-GRANDMASTER { color: #EF4444; }
    &.tier-MASTER { color: #A78BFA; }
    &.tier-DIAMOND { color: #1677FF; }
  }
}

.charts {
  display: grid;
  grid-template-columns: 1fr 1fr;
  grid-template-rows: 320px 320px;
  gap: $space-card;
}

.matches {
  padding: 16px 20px 20px;
  .matches-head {
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
  .champ-cell { display: flex; align-items: center; gap: 8px; }
  .kda {
    font-family: $font-family-num;
    em { font-style: normal; font-weight: 700; }
    em.k { color: #10B981; }
    em.d { color: #EF4444; }
    em.a { color: #1677FF; }
  }
  .kda-s { color: $color-success; font-weight: 700; }
  .kda-a { color: $color-primary; font-weight: 700; }
  .kda-b { color: $color-warning; }
  .kda-c { color: $color-text-secondary; }
}
</style>
