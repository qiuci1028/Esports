<template>
  <div class="indicator-card" :class="{ 'with-trend': trend !== undefined }">
    <div class="label">{{ label }}</div>
    <div class="value num">{{ formatted }}</div>
    <div v-if="trend !== undefined" class="trend" :class="trendClass">
      <el-icon><CaretTop v-if="trend >= 0" /><CaretBottom v-else /></el-icon>
      <span>{{ Math.abs((trend * 100)).toFixed(1) }}%</span>
      <span class="trend-tip">较昨日</span>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import { CaretTop, CaretBottom } from '@element-plus/icons-vue'

const props = withDefaults(
  defineProps<{
    label: string
    value: number | string
    /** 小数位 */
    decimals?: number
    /** 千分位 */
    thousand?: boolean
    /** 后缀 */
    suffix?: string
    /** 环比，0.05 表示 +5% */
    trend?: number
  }>(),
  { decimals: 0, thousand: true, suffix: '' }
)

const formatted = computed(() => {
  if (typeof props.value === 'string') return props.value
  if (typeof props.value !== 'number') return String(props.value ?? '0')
  let v = props.value
  if (props.thousand) v = Math.round(v)
  const s = v.toFixed(props.decimals)
  const parts = s.split('.')
  parts[0] = parts[0].replace(/\B(?=(\d{3})+(?!\d))/g, ',')
  return parts.join('.') + props.suffix
})

const trendClass = computed(() => (props.trend === undefined ? '' : props.trend >= 0 ? 'up' : 'down'))
</script>

<style scoped lang="scss">
.indicator-card {
  background: $color-bg-card;
  border: 1px solid $color-border;
  border-radius: $radius-card;
  padding: 18px 20px 14px;
  box-shadow: $shadow-card;
  display: flex;
  flex-direction: column;
  gap: 6px;
  min-height: 96px;
  position: relative;
  overflow: hidden;

  &::after {
    content: '';
    position: absolute;
    top: 0; right: 0;
    width: 60px; height: 60px;
    background: linear-gradient(135deg, transparent 50%, rgba($color-primary, 0.06) 50%);
    pointer-events: none;
  }

  .label {
    font-size: $font-size-caption;
    color: $color-text-secondary;
    line-height: 1.2;
  }

  .value {
    font-size: $font-size-data-large;
    font-weight: 700;
    color: $color-primary;
    line-height: 1.2;
  }

  .trend {
    display: flex;
    align-items: center;
    gap: 4px;
    font-size: $font-size-caption;
    color: $color-text-secondary;

    &.up { color: $color-success; }
    &.down { color: $color-danger; }

    .trend-tip { color: $color-text-secondary; }
  }
}
</style>
