<template>
  <article class="card">
    <div class="a-cover" :style="cover">
      <span class="cat-tag"><i class="bi" :class="style.icon"></i>{{ a.category }}</span>
      <span class="state-tag" :class="{ full: isFull }">{{ stateText }}</span>
      <i class="bi ghost" :class="style.icon"></i>
    </div>
    <div class="a-body">
      <h3 class="a-title">{{ a.title }}</h3>
      <div class="a-meta">
        <div class="row"><i class="bi bi-geo-alt"></i><span class="ellipsis">{{ a.location }}</span></div>
        <div class="row"><i class="bi bi-calendar3"></i><span>{{ range }}</span></div>
        <div class="row"><i class="bi bi-hourglass-split"></i><span>可认定 {{ a.hours }} 小时工时</span></div>
      </div>

      <div class="progress-row">
        <span>已报名 {{ a.signedCount }}/{{ a.maxCount }}</span>
        <span>余位 {{ remain }}</span>
      </div>
      <div class="progress"><span :style="{ width: percent + '%' }"></span></div>

      <div class="a-actions">
        <router-link :to="`/activity/${a.id}`" class="btn btn-ghost btn-sm">
          查看详情
        </router-link>
        <button class="btn btn-primary btn-sm" :disabled="isFull || ended" @click="$emit('signup', a)">
          {{ ended ? '已结束' : isFull ? '已满员' : '立即报名' }}
        </button>
      </div>
    </div>
  </article>
</template>

<script setup>
import { computed } from 'vue'
import { coverStyle, styleOf, timeRange, isEnded, remainOf, percentOf } from '../utils/category'

const props = defineProps({ a: { type: Object, required: true } })
defineEmits(['signup'])

const style = computed(() => styleOf(props.a.category))
const cover = computed(() => coverStyle(props.a.category))
const range = computed(() => timeRange(props.a.startTime, props.a.endTime))
const ended = computed(() => isEnded(props.a))
const isFull = computed(() => remainOf(props.a) <= 0)
const remain = computed(() => remainOf(props.a))
const percent = computed(() => percentOf(props.a))
const stateText = computed(() => (ended.value ? '已结束' : isFull.value ? '已满员' : '招募中'))
</script>
