<template>
  <router-link to="/activities" class="back-link">
    <i class="bi bi-arrow-left"></i> 返回活动广场
  </router-link>

  <template v-if="a">
    <section class="detail-head" :style="headStyle">
      <span class="cat-tag"><i class="bi" :class="style.icon"></i>{{ a.category }}</span>
      <h1>{{ a.title }}</h1>
      <div class="detail-meta">
        <span><i class="bi bi-geo-alt"></i>{{ a.location }}</span>
        <span><i class="bi bi-calendar3"></i>{{ range }}</span>
        <span><i class="bi bi-hourglass-split"></i>{{ a.hours }} 小时工时</span>
        <span><i class="bi bi-people"></i>{{ a.signedCount }}/{{ a.maxCount }} 人已报名</span>
      </div>
      <i class="bi ghost" :class="style.icon"></i>
    </section>

    <div class="detail-grid">
      <div class="panel">
        <div class="panel-head">
          <h3>活动介绍</h3>
          <span class="sub">发布人：{{ a.organizerName || '活动组织方' }}</span>
        </div>
        <div class="prose">{{ a.description || '组织者暂未填写详细说明，可在报名后与负责人联系。' }}</div>

        <div class="section-title" style="margin-bottom: 8px">
          <h2 style="font-size: 16px"><i class="bi bi-info-circle" style="color: var(--brand)"></i> 报名须知</h2>
        </div>
        <div class="prose" style="color: var(--muted); font-size: 14px">
          1. 报名后状态为「待审核」，组织者会尽快处理，通过后请按时间到场签到；
          2. 服务结束后由组织者在 24 小时内认定工时，可在「我的工时」查看；
          3. 如需取消，请在开始前于「我的报名」中撤回，把名额留给其他志愿者；
          4. 连续爽约会影响信用分，信用分低于 80 分将限制报名热门活动。
        </div>
      </div>

      <aside class="panel side-card">
        <div class="panel-head">
          <h3>报名信息</h3>
          <span class="pill" :class="statusCls">{{ stateText }}</span>
        </div>

        <div class="side-num">
          <span class="big">{{ remain }}</span>
          <span class="small">个剩余名额</span>
        </div>
        <div class="progress"><span :style="{ width: percent + '%' }"></span></div>
        <div class="progress-row">
          <span>已报名 {{ a.signedCount }} 人</span>
          <span>上限 {{ a.maxCount }} 人</span>
        </div>

        <div style="margin: 14px 0 18px">
          <div class="kv"><span class="k">活动时间</span><span class="v">{{ range }}</span></div>
          <div class="kv"><span class="k">服务地点</span><span class="v" style="max-width: 190px; text-align: right">{{ a.location }}</span></div>
          <div class="kv"><span class="k">认定工时</span><span class="v">{{ a.hours }} 小时</span></div>
          <div class="kv"><span class="k">报名状态</span><span class="v">{{ myStatusText }}</span></div>
        </div>

        <button
          v-if="!mine"
          class="btn btn-primary btn-block"
          :disabled="ended || isFull || submitting"
          @click="signup"
        >
          <i class="bi bi-hand-index-thumb"></i>
          {{ ended ? '活动已结束' : isFull ? '名额已满' : submitting ? '提交中…' : '立即报名' }}
        </button>
        <button v-else class="btn btn-soft btn-block" disabled>
          <i class="bi bi-check2-circle"></i> 你已报名该活动
        </button>
        <div class="hint" style="text-align: center">报名后可在「我的报名」中查看审核进度</div>
      </aside>
    </div>
  </template>

  <div v-else class="panel" style="padding: 60px; text-align: center">
    <i class="bi bi-hourglass" style="font-size: 30px; color: var(--muted)"></i>
    <p class="muted">加载中…</p>
  </div>
</template>

<script setup>
import { computed, onMounted, ref } from 'vue'
import { useRoute } from 'vue-router'
import api from '../api'
import { detailHeadStyle, styleOf, timeRange, isEnded, remainOf, percentOf } from '../utils/category'
import { toast } from '../utils/toast'
import { useAuthStore } from '../store/auth'

const route = useRoute()
const auth = useAuthStore()
const a = ref(null)
const mine = ref(null)
const submitting = ref(false)

const style = computed(() => styleOf(a.value?.category))
const headStyle = computed(() => detailHeadStyle(a.value?.category))
const range = computed(() => timeRange(a.value?.startTime, a.value?.endTime))
const ended = computed(() => isEnded(a.value))
const remain = computed(() => remainOf(a.value))
const isFull = computed(() => remain.value <= 0)
const percent = computed(() => percentOf(a.value))

const stateText = computed(() =>
  ended.value ? '已结束' : isFull.value ? '已满员' : a.value?.status === 'draft' ? '未发布' : '招募中'
)
const statusCls = computed(() =>
  ended.value ? 'grey' : isFull.value ? 'rose' : 'green'
)
const myStatusText = computed(() => {
  if (!mine.value) return '未报名'
  return { pending: '待审核', approved: '已通过', rejected: '已驳回' }[mine.value.status] || mine.value.status
})

async function load() {
  const id = route.params.id
  if (!id || id === 'undefined' || id === 'null') {
    toast.error('活动参数缺失，已返回活动广场')
    a.value = null
    return
  }
  try {
    a.value = await api.getActivity(id)
    if (auth.role === 'volunteer') {
      const regs = await api.getMyRegistrations()
      mine.value = (regs || []).find((r) => Number(r.activityId) === Number(id) && r.status !== 'rejected')
    }
  } catch (e) {
    toast.error(e.message)
  }
}

async function signup() {
  submitting.value = true
  try {
    await api.signup(a.value.id)
    toast.success('报名成功，等待组织者审核')
    await load()
  } catch (e) {
    toast.error(e.message)
  } finally {
    submitting.value = false
  }
}

onMounted(load)
</script>
