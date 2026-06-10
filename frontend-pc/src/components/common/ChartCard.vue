<template>
  <div class="chart-card">
    <div class="card-head">
      <span class="card-title">
        <i class="title-bar" />
        {{ title }}
      </span>
      <div v-if="$slots.extra" class="card-extra">
        <slot name="extra" />
      </div>
    </div>
    <div class="card-body">
      <slot />
    </div>
  </div>
</template>

<script setup lang="ts">
defineProps<{ title: string }>()
</script>

<style scoped lang="scss">
.chart-card {
  background: $color-bg-card;
  border: 1px solid $color-border;
  border-radius: $radius-card;
  box-shadow: $shadow-card;
  display: flex;
  flex-direction: column;
  min-height: 0;

  .card-head {
    display: flex;
    align-items: center;
    justify-content: space-between;
    padding: 12px 20px 8px;
    border-bottom: 1px solid rgba($color-border, 0.5);
  }

  .card-title {
    font-size: $font-size-card-title;
    font-weight: 700;
    color: $color-text-primary;
    line-height: $line-height-title;
    display: flex;
    align-items: center;
    gap: 8px;
  }

  .title-bar {
    display: inline-block;
    width: 4px;
    height: 16px;
    background: $color-primary;
    border-radius: 2px;
  }

  .card-extra {
    font-size: $font-size-caption;
    color: $color-text-secondary;
  }

  .card-body {
    flex: 1;
    padding: 12px 12px 16px;
    min-height: 0;
  }
}
</style>
