<template>
  <div class="empty">
    <i class="bi" :class="icon"></i>
    <div class="t">{{ title }}</div>
    <div>{{ desc }}</div>
    <slot />
  </div>
</template>

<script setup>
defineProps({
  icon: { type: String, default: 'bi-inbox' },
  title: { type: String, default: '暂无数据' },
  desc: { type: String, default: '' }
})
</script>
