<template>
  <div class="toast-wrap">
    <div v-for="t in toastState.items" :key="t.id" class="toast" :class="t.type">
      <i :class="t.type === 'error' ? 'bi bi-exclamation-circle' : 'bi bi-check2-circle'"></i>
      <span>{{ t.message }}</span>
    </div>
  </div>
</template>

<script setup>
import { toastState } from '../utils/toast'
</script>
