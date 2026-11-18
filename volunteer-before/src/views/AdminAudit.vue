<template>
  <div class="section-title">
    <div>
      <h2>报名审核</h2>
      <div class="sub">审核志愿者报名，并为已完成服务的志愿者认定工时</div>
    </div>
    <div class="tabs">
      <button
        v-for="t in tabs"
        :key="t.key"
        class="tab"
        :class="{ active: tab === t.key }"
        @click="switchTab(t.key)"
      >
        {{ t.label }}<span v-if="t.key === 'pending' && pendingCount"> {{ pendingCount }}</span>
      </button>
    </div>
  </div>

  <div
    v-if="err"
    class="panel"
    style="display: flex; gap: 12px; align-items: center; padding: 16px 20px; margin-bottom: 16px"
  >
    <i class="bi bi-exclamation-triangle-fill" style="color: #b91c1c"></i>
    <span style="flex: 1; color: #b91c1c; font-size: 13.5px">{{ err }}</span>
    <button class="btn btn-ghost btn-sm" @click="loadAll">重试</button>
  </div>

  <div class="panel">
    <div class="table-wrap">
      <table class="table">
        <thead>
          <tr>
            <th style="width: 18%">志愿者</th>
            <th style="width: 28%">报名活动</th>
            <th>报名时间</th>
            <th>状态</th>
            <th>工时</th>
            <th style="text-align: right">操作</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="r in rows" :key="r.id">
            <td>
              <div class="flex center gap-8">
                <span class="avatar">{{ (r.username || '?').slice(0, 1).toUpperCase() }}</span>
                <div>
                  <div class="strong">{{ r.username }}</div>
                  <div class="muted" style="font-size: 12px">
                    {{ r.phone || '—' }}<span v-if="r.skills"> · {{ r.skills }}</span>
                  </div>
                </div>
              </div>
            </td>
            <td>
              <div>{{ r.activityTitle }}</div>
              <div class="muted" style="font-size: 12.5px">{{ fmtTime(r.startTime) }}</div>
            </td>
            <td class="muted nowrap">{{ fmtTime(r.signupTime) }}</td>
            <td>
              <span class="pill" :class="statusOf(r.status).cls">{{ statusOf(r.status).text }}</span>
            </td>
            <td class="nowrap">
              <span v-if="r.issued" class="pill green"><i class="bi bi-check2"></i> 已认定</span>
              <span v-else class="muted">—</span>
            </td>
            <td>
              <div class="flex gap-8" style="justify-content: flex-end">
                <template v-if="r.status === 'pending'">
                  <button class="btn btn-soft btn-sm" @click="audit(r, 'approved')">
                    <i class="bi bi-check2"></i> 通过
                  </button>
                  <button class="btn btn-danger btn-sm" @click="audit(r, 'rejected')">
                    <i class="bi bi-x-lg"></i> 驳回
                  </button>
                </template>
                <button
                  v-else-if="r.status === 'approved' && !r.issued"
                  class="btn btn-primary btn-sm"
                  @click="issue(r)"
                >
                  <i class="bi bi-clock"></i> 认定工时
                </button>
                <span v-else class="muted" style="font-size: 12.5px">已处理</span>
              </div>
            </td>
          </tr>
          <tr v-if="!rows.length">
            <td colspan="6" class="muted" style="text-align: center; padding: 40px">
              当前没有需要处理的记录
            </td>
          </tr>
        </tbody>
      </table>
    </div>
  </div>
</template>

<script setup>
import { onMounted, ref } from 'vue'
import api from '../api'
import { fmtTime, STATUS_TEXT } from '../utils/category'
import { toast } from '../utils/toast'

const tabs = [
  { key: 'pending', label: '待审核' },
  { key: 'pending-hours', label: '待认定工时' },
  { key: 'all', label: '全部记录' }
]

const tab = ref('pending')
const all = ref([])
const rows = ref([])
const pendingCount = ref(0)
const err = ref('')

const statusOf = (s) => STATUS_TEXT[s] || { text: s, cls: 'grey' }

async function loadAll() {
  err.value = ''
  try {
    const data = (await api.getRegistrations('all')) || []
    all.value = Array.isArray(data) ? data : []
    pendingCount.value = all.value.filter((r) => r.status === 'pending').length
    apply()
  } catch (e) {
    err.value = e.message
    toast.error(e.message)
    rows.value = []
  }
}

function apply() {
  if (tab.value === 'pending') {
    rows.value = all.value.filter((r) => r.status === 'pending')
  } else if (tab.value === 'pending-hours') {
    rows.value = all.value.filter((r) => r.status === 'approved' && !r.issued)
  } else {
    rows.value = all.value
  }
}

function switchTab(k) {
  tab.value = k
  apply()
}

async function audit(r, status) {
  const tip = status === 'approved' ? `通过 ${r.username} 报名「${r.activityTitle}」？` : `驳回 ${r.username} 的报名？名额将释放。`
  if (!window.confirm(tip)) return
  try {
    await api.auditRegistration(r.id, status)
    toast.success(status === 'approved' ? '已通过' : '已驳回')
    await loadAll()
  } catch (e) {
    toast.error(e.message)
  }
}

async function issue(r) {
  const h = window.prompt(`为 ${r.username} 认定「${r.activityTitle}」的服务工时（小时）：`, '4')
  if (h === null) return
  const hours = Number(h)
  if (!hours || hours <= 0) return toast.error('工时必须为正数')
  try {
    await api.issueHours(r.id, hours)
    toast.success(`已认定 ${hours} 小时工时`)
    await loadAll()
  } catch (e) {
    toast.error(e.message)
  }
}

onMounted(loadAll)
</script>
