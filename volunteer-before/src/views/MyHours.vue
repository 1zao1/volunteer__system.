<template>
  <div class="section-title">
    <div>
      <h2>我的工时</h2>
      <div class="sub">工时由组织者在服务结束后认定，累计可用于评优与证明开具</div>
    </div>
    <router-link to="/activities" class="btn btn-soft btn-sm">
      <i class="bi bi-clock"></i> 参加更多活动
    </router-link>
  </div>

  <section class="big-hero">
    <div>
      <div class="k">累 计 服 务 时 长</div>
      <div class="v">{{ total }}<small>小时</small></div>
      <div class="k" style="margin-top: 6px">
        <i class="bi bi-patch-check"></i>
        {{ level }}
      </div>
    </div>
    <div class="side-stats">
      <div>
        <div class="n">{{ list.length }}</div>
        <div class="l">服务次数</div>
      </div>
      <div>
        <div class="n">{{ activities }}</div>
        <div class="l">参与活动</div>
      </div>
      <div>
        <div class="n">{{ avg }}</div>
        <div class="l">平均单次（小时）</div>
      </div>
    </div>
  </section>

  <div class="panel mt-24">
    <div class="panel-head">
      <h3>服务记录</h3>
      <span class="sub">按时间倒序，共 {{ list.length }} 条</span>
    </div>

    <div v-if="list.length">
      <div v-for="x in list" :key="x.id" class="tl">
        <div class="tl-date">{{ fmtDate(x.date) }}</div>
        <div>
          <div class="tl-title">{{ x.activityTitle }}</div>
          <div class="tl-sub">
            <span v-if="x.category"><i class="bi bi-tag"></i> {{ x.category }}</span>
            <span style="margin-left: 10px"><i class="bi bi-geo-alt"></i> {{ x.location || '—' }}</span>
          </div>
        </div>
        <div class="tl-hours">{{ x.hours }}<small>h</small></div>
      </div>
    </div>

    <EmptyState
      v-else-if="!loading"
      icon="bi-clock-history"
      title="还没有工时记录"
      desc="参加活动并完成服务后，组织者会为你认定工时。"
    />
    <div v-else class="skeleton" style="height: 140px"></div>
  </div>
</template>

<script setup>
import { computed, onMounted, ref } from 'vue'
import api from '../api'
import EmptyState from '../components/EmptyState.vue'
import { fmtDate } from '../utils/category'
import { toast } from '../utils/toast'

const list = ref([])
const loading = ref(true)

const total = computed(() => list.value.reduce((s, x) => s + (Number(x.hours) || 0), 0))
const activities = computed(() => new Set(list.value.map((x) => x.activityTitle)).size)
const avg = computed(() => (list.value.length ? (total.value / list.value.length).toFixed(1) : 0))

const level = computed(() => {
  const h = total.value
  if (h >= 100) return '五星志愿者 · 累计超 100 小时'
  if (h >= 50) return '四星志愿者 · 累计 50 小时以上'
  if (h >= 20) return '三星志愿者 · 累计 20 小时以上'
  if (h >= 10) return '二星志愿者 · 累计 10 小时以上'
  if (h > 0) return '一星志愿者 · 已开始记录服务时长'
  return '尚未开始记录，先报名一个活动吧'
})

async function load() {
  loading.value = true
  try {
    list.value = (await api.getMyHours()) || []
  } catch (e) {
    toast.error(e.message)
  } finally {
    loading.value = false
  }
}

onMounted(load)
</script>
