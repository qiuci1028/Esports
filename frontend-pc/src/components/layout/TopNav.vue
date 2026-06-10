<template>
  <header class="top-nav">
    <div class="left">
      <div class="logo">
        <span class="logo-icon">R</span>
        <span class="logo-text">Riot 电竞大数据分析平台</span>
      </div>
    </div>

    <nav class="center">
      <RouterLink
        v-for="tab in gameTabs"
        :key="tab.key"
        :to="tab.to"
        class="game-tab"
        :class="{ active: app.currentGame === tab.key }"
        @click="app.setGame(tab.key)"
      >
        {{ tab.label }}
      </RouterLink>
    </nav>

    <div class="right">
      <el-select
        v-model="app.currentPatch"
        size="default"
        class="patch-select"
        @change="onPatchChange"
      >
        <el-option
          v-for="p in patches"
          :key="p"
          :value="p"
          :label="`版本 ${p}`"
        />
      </el-select>

      <el-button :icon="Refresh" circle @click="onRefresh" />

      <div class="status">
        <span class="dot" />
        <span class="status-text">{{ statusText }}</span>
      </div>
    </div>
  </header>
</template>

<script setup lang="ts">
import { computed, ref, onMounted, onBeforeUnmount } from 'vue'
import { RouterLink, useRouter } from 'vue-router'
import { Refresh } from '@element-plus/icons-vue'
import { useAppStore, type GameKey } from '@/stores/app'
import { useAuthStore } from '@/stores/auth'

const app = useAppStore()
const auth = useAuthStore()
const router = useRouter()

const gameTabs: { key: GameKey; label: string; to: string }[] = [
  { key: 'LOL', label: '英雄联盟', to: '/' },
  { key: 'VALORANT', label: '无畏契约', to: '/champion' },
  { key: 'TFT', label: '云顶之弈', to: '/champion' }
]

const patches = computed(() => {
  if (app.currentGame === 'LOL') return ['14.10', '14.09', '14.08', '14.07']
  if (app.currentGame === 'VALORANT') return ['9.0', '8.11', '8.10']
  return ['S10', 'S9.5']
})

const now = ref(new Date())
let timer: number | null = null
onMounted(() => { timer = window.setInterval(() => (now.value = new Date()), 1000) })
onBeforeUnmount(() => { if (timer) clearInterval(timer) })

const statusText = computed(() => {
  const t = now.value.toLocaleTimeString('zh-CN', { hour12: false })
  return `${t}  系统运行中  ${auth.user ? auth.user.nickname : '游客'}`
})

function onPatchChange(v: string) { app.setPatch(v) }
function onRefresh() {
  app.refresh()
  router.go(0) // 简化：刷新当前页
}
</script>

<style scoped lang="scss">
.top-nav {
  height: $height-nav;
  background: $color-bg-card;
  border-bottom: 1px solid $color-border;
  display: flex;
  align-items: center;
  padding: 0 $space-page-x;
  position: sticky;
  top: 0;
  z-index: 100;
  box-shadow: 0 2px 8px rgba(0,0,0,0.3);

  .left, .right { display: flex; align-items: center; gap: 16px; }
  .center { flex: 1; display: flex; justify-content: center; gap: 8px; }

  .logo {
    display: flex;
    align-items: center;
    gap: 10px;

    .logo-icon {
      display: inline-flex;
      align-items: center;
      justify-content: center;
      width: 32px; height: 32px;
      background: linear-gradient(135deg, $color-primary, $color-primary-light);
      color: #fff;
      font-weight: 800;
      font-size: 18px;
      border-radius: 6px;
    }
    .logo-text {
      font-size: 18px;
      font-weight: 700;
      color: $color-text-primary;
    }
  }

  .game-tab {
    padding: 6px 20px;
    font-size: 16px;
    color: $color-text-secondary;
    border-radius: 18px;
    transition: all 0.2s;
    cursor: pointer;
    text-decoration: none;

    &:hover { color: $color-text-primary; background: rgba($color-primary, 0.08); }
    &.active {
      color: #fff;
      background: linear-gradient(135deg, $color-primary, $color-primary-light);
      box-shadow: 0 2px 8px rgba($color-primary, 0.4);
    }
  }

  .patch-select { width: 130px; }

  .status {
    display: flex;
    align-items: center;
    gap: 6px;
    font-size: $font-size-caption;
    color: $color-text-secondary;
    .dot {
      width: 8px; height: 8px;
      border-radius: 50%;
      background: $color-success;
      box-shadow: 0 0 6px $color-success;
      animation: pulse 1.5s infinite;
    }
  }
}

@keyframes pulse {
  0%, 100% { opacity: 1; }
  50% { opacity: 0.4; }
}
</style>
