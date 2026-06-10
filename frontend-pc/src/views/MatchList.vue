<template>
  <div class="match-list">
    <section class="filter biz-card">
      <el-select v-model="filters.game" style="width: 140px" @change="load">
        <el-option label="LOL" value="LOL" />
        <el-option label="VALORANT" value="VALORANT" />
        <el-option label="TFT" value="TFT" />
      </el-select>
      <el-select v-model="filters.gameMode" clearable placeholder="模式" style="width: 120px" @change="load">
        <el-option label="经典" value="CLASSIC" />
        <el-option label="排位" value="RANKED" />
        <el-option label="竞技" value="COMPETITIVE" />
      </el-select>
      <el-input v-model="filters.patch" placeholder="版本（可空）" style="width: 140px" clearable @change="load" />
      <el-button type="primary" :icon="Refresh" @click="load">刷新</el-button>
    </section>

    <section class="list biz-card">
      <el-table :data="list" stripe v-loading="loading" :max-height="640">
        <el-table-column prop="matchId" label="比赛 ID" min-width="140" />
        <el-table-column label="游戏" width="100" align="center">
          <template #default="{ row }">
            <el-tag size="small" effect="dark" :type="row.game === 'LOL' ? 'primary' : row.game === 'VALORANT' ? 'success' : 'warning'">
              {{ row.game }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="gameMode" label="模式" width="100" align="center" />
        <el-table-column prop="mapName" label="地图" width="160" />
        <el-table-column prop="patchCode" label="版本" width="100" align="center" />
        <el-table-column prop="durationSec" label="时长" width="120" align="center">
          <template #default="{ row }">
            <span class="num">{{ formatDuration(row.durationSec) }}</span>
          </template>
        </el-table-column>
        <el-table-column label="结果" width="100" align="center">
          <template #default="{ row }">
            <span :class="`result-tag result-${row.winningTeam}`">
              {{ row.winningTeam === 'BLUE' ? '蓝方胜' : row.winningTeam === 'RED' ? '红方胜' : '平局' }}
            </span>
          </template>
        </el-table-column>
        <el-table-column prop="creationTime" label="开始时间" min-width="180" />
        <el-table-column label="操作" width="100" align="center">
          <template #default="{ row }">
            <el-button link type="primary" size="small" @click="goDetail(row.matchId)">详情</el-button>
          </template>
        </el-table-column>
      </el-table>

      <div class="pagination">
        <el-pagination
          v-model:current-page="filters.pageNum"
          v-model:page-size="filters.pageSize"
          :page-sizes="[10, 20, 50]"
          :total="total"
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="load"
          @current-change="load"
        />
      </div>
    </section>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { Refresh } from '@element-plus/icons-vue'
import { matchApi } from '@/api/match'
import type { MatchListVO } from '@/types/api'

const router = useRouter()
const filters = reactive({
  game: 'LOL',
  gameMode: '',
  patch: '',
  pageNum: 1,
  pageSize: 10
})

const list = ref<MatchListVO[]>([])
const total = ref(0)
const loading = ref(false)

async function load() {
  loading.value = true
  try {
    const r = await matchApi.page(filters)
    list.value = r.list
    total.value = r.total
  } finally {
    loading.value = false
  }
}

function goDetail(id: string) {
  router.push(`/match/${id}`)
}

function formatDuration(sec?: number) {
  if (!sec) return '—'
  const m = Math.floor(sec / 60)
  const s = sec % 60
  return `${m}:${s.toString().padStart(2, '0')}`
}

onMounted(load)
</script>

<style scoped lang="scss">
.match-list { display: flex; flex-direction: column; gap: $space-card; }
.filter {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 12px 20px;
}
.list { padding: 16px 20px; }
.pagination { display: flex; justify-content: flex-end; margin-top: 12px; }
.result-tag {
  display: inline-block;
  padding: 2px 8px;
  border-radius: 4px;
  font-size: 12px;
  font-weight: 700;
}
.result-BLUE { background: rgba(22, 119, 255, 0.2); color: #4096FF; }
.result-RED { background: rgba(239, 68, 68, 0.2); color: #F87171; }
.result-NONE { background: rgba(148, 163, 184, 0.2); color: #94A3B8; }
</style>
