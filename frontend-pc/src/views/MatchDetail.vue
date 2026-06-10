<template>
  <div v-if="detail" class="match-detail">
    <!-- 1. 基础信息 -->
    <section class="header biz-card">
      <div class="meta">
        <h1 class="title">
          <i class="title-bar" />
          比赛 {{ detail.basic.matchId }}
          <el-tag size="small" :type="tagType(detail.basic.game)">{{ detail.basic.game }}</el-tag>
          <el-tag size="small" effect="dark">{{ detail.basic.gameMode }}</el-tag>
        </h1>
        <p class="info">
          <span>地图：<strong>{{ detail.basic.mapName || '—' }}</strong></span>
          <span>版本：<strong>{{ detail.basic.patchCode || '—' }}</strong></span>
          <span>时长：<strong class="num">{{ formatDuration(detail.basic.durationSec) }}</strong></span>
          <span>开始：<strong>{{ detail.basic.creationTime || '—' }}</strong></span>
          <span>胜方：<strong :class="`text-${detail.basic.winningTeam === 'BLUE' ? 'brand' : 'danger'}`">
            {{ detail.basic.winningTeam === 'BLUE' ? '蓝方' : '红方' }}
          </strong></span>
        </p>
      </div>
      <div class="actions">
        <el-button @click="$router.back()">返回列表</el-button>
      </div>
    </section>

    <!-- 2. 双方对比 -->
    <section class="teams">
      <div class="team blue">
        <header class="team-head">
          <span class="team-name">蓝方队伍</span>
          <span class="team-result" :class="detail.basic.winningTeam === 'BLUE' ? 'win' : 'lose'">
            {{ detail.basic.winningTeam === 'BLUE' ? '胜利' : '失败' }}
          </span>
        </header>
        <div class="players">
          <div v-for="p in detail.blueTeam" :key="p.puuid" class="player">
            <el-avatar :size="32" :src="p.championIcon">{{ (p.championName || '?').charAt(0) }}</el-avatar>
            <div class="info">
              <p class="name">{{ p.summonerName }}</p>
              <p class="meta">{{ p.championName }} · {{ p.rolePosition }}</p>
            </div>
            <div class="kda num">
              <em :class="{ k: true }">{{ p.kills }}</em> /
              <em :class="{ d: true }">{{ p.deaths }}</em> /
              <em :class="{ a: true }">{{ p.assists }}</em>
            </div>
          </div>
        </div>
        <footer class="summary">
          <span>总击杀 <strong>{{ detail.blueSummary.totalKills }}</strong></span>
          <span>总死亡 <strong>{{ detail.blueSummary.totalDeaths }}</strong></span>
          <span>总助攻 <strong>{{ detail.blueSummary.totalAssists }}</strong></span>
          <span>总经济 <strong class="num">{{ detail.blueSummary.totalGold.toLocaleString() }}</strong></span>
          <span>总伤害 <strong class="num">{{ detail.blueSummary.totalDamage.toLocaleString() }}</strong></span>
          <span>视野 <strong>{{ detail.blueSummary.totalVision }}</strong></span>
        </footer>
      </div>

      <div class="vs">VS</div>

      <div class="team red">
        <header class="team-head">
          <span class="team-name">红方队伍</span>
          <span class="team-result" :class="detail.basic.winningTeam === 'RED' ? 'win' : 'lose'">
            {{ detail.basic.winningTeam === 'RED' ? '胜利' : '失败' }}
          </span>
        </header>
        <div class="players">
          <div v-for="p in detail.redTeam" :key="p.puuid" class="player">
            <el-avatar :size="32" :src="p.championIcon">{{ (p.championName || '?').charAt(0) }}</el-avatar>
            <div class="info">
              <p class="name">{{ p.summonerName }}</p>
              <p class="meta">{{ p.championName }} · {{ p.rolePosition }}</p>
            </div>
            <div class="kda num">
              <em :class="{ k: true }">{{ p.kills }}</em> /
              <em :class="{ d: true }">{{ p.deaths }}</em> /
              <em :class="{ a: true }">{{ p.assists }}</em>
            </div>
          </div>
        </div>
        <footer class="summary">
          <span>总击杀 <strong>{{ detail.redSummary.totalKills }}</strong></span>
          <span>总死亡 <strong>{{ detail.redSummary.totalDeaths }}</strong></span>
          <span>总助攻 <strong>{{ detail.redSummary.totalAssists }}</strong></span>
          <span>总经济 <strong class="num">{{ detail.redSummary.totalGold.toLocaleString() }}</strong></span>
          <span>总伤害 <strong class="num">{{ detail.redSummary.totalDamage.toLocaleString() }}</strong></span>
          <span>视野 <strong>{{ detail.redSummary.totalVision }}</strong></span>
        </footer>
      </div>
    </section>

    <!-- 3. 对局时序分析 -->
    <section class="timeline biz-card">
      <div class="head">
        <span class="title"><i class="title-bar" /> 对局时序分析</span>
        <span class="subtitle">基于比赛时间线（演示数据）</span>
      </div>
      <BaseChart :option="timelineOption" height="280px" />
    </section>

    <!-- 4. 数据明细表 -->
    <section class="table-card biz-card">
      <div class="head">
        <span class="title"><i class="title-bar" /> 双方数据明细</span>
      </div>
      <el-table :data="flatPlayers" stripe :max-height="400">
        <el-table-column prop="side" label="阵营" width="80" align="center">
          <template #default="{ row }">
            <el-tag size="small" :type="row.side === 'BLUE' ? 'primary' : 'danger'">{{ row.side }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="summonerName" label="召唤师" min-width="120" />
        <el-table-column prop="championName" label="英雄" min-width="120" />
        <el-table-column prop="rolePosition" label="位置" width="80" align="center" />
        <el-table-column label="KDA" width="120" align="center">
          <template #default="{ row }">
            <span class="kda num">
              <em class="k">{{ row.kills }}</em> / <em class="d">{{ row.deaths }}</em> / <em class="a">{{ row.assists }}</em>
            </span>
          </template>
        </el-table-column>
        <el-table-column prop="goldEarned" label="经济" width="120" align="right">
          <template #default="{ row }">
            <span class="num">{{ (row.goldEarned ?? 0).toLocaleString() }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="damageDealt" label="伤害" width="120" align="right">
          <template #default="{ row }">
            <span class="num">{{ (row.damageDealt ?? 0).toLocaleString() }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="visionScore" label="视野" width="80" align="center" />
      </el-table>
    </section>
  </div>
  <el-empty v-else description="加载中或比赛不存在" />
</template>

<script setup lang="ts">
import { ref, computed, onMounted, watch } from 'vue'
import { useRoute } from 'vue-router'
import BaseChart from '@/components/chart/BaseChart.vue'
import { matchApi } from '@/api/match'
import type { MatchDetailDTO, TeamMemberVO } from '@/types/api'

const route = useRoute()
const detail = ref<MatchDetailDTO | null>(null)

async function load() {
  const id = route.params.matchId as string
  if (!id) return
  try {
    detail.value = await matchApi.detail(id)
  } catch (e) {
    console.error(e)
    detail.value = null
  }
}

watch(() => route.params.matchId, load)
onMounted(load)

const flatPlayers = computed<(TeamMemberVO & { side: string })[]>(() => {
  if (!detail.value) return []
  return [
    ...detail.value.blueTeam.map((p) => ({ ...p, side: 'BLUE' })),
    ...detail.value.redTeam.map((p) => ({ ...p, side: 'RED' }))
  ]
})

const tagType = (g?: string) => (g === 'LOL' ? 'primary' : g === 'VALORANT' ? 'success' : 'warning')

function formatDuration(sec?: number) {
  if (!sec) return '—'
  const m = Math.floor(sec / 60)
  const s = sec % 60
  return `${m}:${s.toString().padStart(2, '0')}`
}

// 模拟时序图：双方经济曲线
const timelineOption = computed<Record<string, unknown>>(() => {
  if (!detail.value) return { title: { text: '暂无数据', left: 'center', top: 'middle', textStyle: { color: '#94A3B8' } } }
  const minutes = Math.ceil((detail.value.basic.durationSec || 1800) / 60)
  const x: string[] = []
  for (let i = 0; i <= minutes; i++) x.push(`${i}'`)
  // 模拟经济曲线
  const blue: number[] = []
  const red: number[] = []
  for (let i = 0; i <= minutes; i++) {
    blue.push(Math.round(800 + i * 380 + Math.sin(i / 3) * 50))
    red.push(Math.round(800 + i * 360 + Math.cos(i / 3) * 50))
  }
  return {
    grid: { top: 30, right: 30, bottom: 30, left: 60 },
    tooltip: { trigger: 'axis' },
    legend: { top: 0, textStyle: { color: '#94A3B8' } },
    xAxis: { type: 'category', data: x, axisLine: { lineStyle: { color: '#334155' } }, axisLabel: { color: '#94A3B8' } },
    yAxis: { type: 'value', name: '总经济', nameTextStyle: { color: '#94A3B8' }, axisLabel: { color: '#94A3B8' }, splitLine: { lineStyle: { color: 'rgba(51,65,85,0.5)' } } },
    series: [
      {
        name: '蓝方经济', type: 'line', smooth: true, data: blue,
        lineStyle: { color: '#1677FF', width: 2 }, itemStyle: { color: '#1677FF' },
        areaStyle: { color: { type: 'linear', x: 0, y: 0, x2: 0, y2: 1, colorStops: [{ offset: 0, color: 'rgba(22,119,255,0.3)' }, { offset: 1, color: 'rgba(22,119,255,0)' }] } }
      },
      {
        name: '红方经济', type: 'line', smooth: true, data: red,
        lineStyle: { color: '#EF4444', width: 2 }, itemStyle: { color: '#EF4444' },
        areaStyle: { color: { type: 'linear', x: 0, y: 0, x2: 0, y2: 1, colorStops: [{ offset: 0, color: 'rgba(239,68,68,0.3)' }, { offset: 1, color: 'rgba(239,68,68,0)' }] } }
      }
    ]
  }
})
</script>

<style scoped lang="scss">
.match-detail {
  display: flex;
  flex-direction: column;
  gap: $space-card;
}

.header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 16px 24px;
  .title {
    margin: 0 0 8px;
    font-size: 24px;
    font-weight: 700;
    color: $color-text-primary;
    display: flex;
    align-items: center;
    gap: 8px;
  }
  .title-bar { display: inline-block; width: 4px; height: 18px; background: $color-primary; border-radius: 2px; }
  .info {
    margin: 0;
    display: flex;
    flex-wrap: wrap;
    gap: 18px;
    font-size: 13px;
    color: $color-text-secondary;
    strong { color: $color-text-primary; margin-left: 4px; }
  }
}

.teams {
  display: grid;
  grid-template-columns: 1fr 60px 1fr;
  gap: 0;
  background: $color-bg-card;
  border: 1px solid $color-border;
  border-radius: $radius-card;
  overflow: hidden;

  .team {
    padding: 20px;
    &.blue { background: linear-gradient(135deg, rgba(22,119,255,0.05), transparent); }
    &.red  { background: linear-gradient(225deg, rgba(239,68,68,0.05), transparent); }
  }
  .vs {
    display: flex;
    align-items: center;
    justify-content: center;
    font-size: 28px;
    font-weight: 800;
    color: $color-text-disabled;
    background: rgba(15, 23, 42, 0.4);
  }
  .team-head {
    display: flex;
    align-items: center;
    justify-content: space-between;
    margin-bottom: 12px;
    .team-name { font-size: 20px; font-weight: 700; color: $color-text-primary; }
    .team-result {
      padding: 2px 10px;
      border-radius: 12px;
      font-size: 12px;
      font-weight: 700;
      &.win { background: rgba(16,185,129,0.15); color: $color-success; }
      &.lose { background: rgba(239,68,68,0.15); color: $color-danger; }
    }
  }
  .players { display: flex; flex-direction: column; gap: 8px; }
  .player {
    display: grid;
    grid-template-columns: 32px 1fr 100px;
    align-items: center;
    gap: 12px;
    background: rgba(15, 23, 42, 0.4);
    padding: 8px 12px;
    border-radius: 6px;
    .info { min-width: 0; }
    .name { margin: 0; color: $color-text-primary; font-weight: 500; font-size: 14px; }
    .meta { margin: 2px 0 0; color: $color-text-secondary; font-size: 12px; }
    .kda {
      text-align: right;
      em { font-style: normal; font-weight: 700; }
      em.k { color: $color-success; }
      em.d { color: $color-danger; }
      em.a { color: $color-primary; }
    }
  }
  .summary {
    margin-top: 12px;
    padding-top: 12px;
    border-top: 1px dashed $color-border;
    display: flex;
    flex-wrap: wrap;
    gap: 14px;
    font-size: 12px;
    color: $color-text-secondary;
    strong { color: $color-text-primary; margin-left: 4px; }
  }
}

.timeline, .table-card {
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
.kda {
  em { font-style: normal; font-weight: 700; }
  em.k { color: $color-success; }
  em.d { color: $color-danger; }
  em.a { color: $color-primary; }
}
</style>
