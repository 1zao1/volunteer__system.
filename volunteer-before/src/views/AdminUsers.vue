<template>
  <div class="section-title">
    <div>
      <h2>用户管理</h2>
      <div class="sub">管理全站用户与角色：管理员专属，组织者与志愿者可见范围受限于自己参与的业务</div>
    </div>
    <div class="flex gap-8">
      <div class="search" style="min-width: 240px">
        <i class="bi bi-search"></i>
        <input v-model.trim="keyword" placeholder="搜索用户名 / 手机号" />
      </div>
      <button class="btn btn-ghost btn-sm" @click="load">
        <i class="bi bi-arrow-clockwise"></i> 刷新
      </button>
    </div>
  </div>

  <div class="stats-grid" style="grid-template-columns: repeat(4, 1fr)">
    <div class="stat">
      <div class="icon green"><i class="bi bi-people"></i></div>
      <div>
        <div class="num">{{ counts.volunteer }}</div>
        <div class="label">志愿者</div>
      </div>
    </div>
    <div class="stat">
      <div class="icon sky"><i class="bi bi-megaphone"></i></div>
      <div>
        <div class="num">{{ counts.organizer }}</div>
        <div class="label">组织者</div>
      </div>
    </div>
    <div class="stat">
      <div class="icon amber"><i class="bi bi-shield-check"></i></div>
      <div>
        <div class="num">{{ counts.admin }}</div>
        <div class="label">管理员</div>
      </div>
    </div>
    <div class="stat">
      <div class="icon rose"><i class="bi bi-exclamation-triangle"></i></div>
      <div>
        <div class="num">{{ counts.lowCredit }}</div>
        <div class="label">信用分低于 90</div>
      </div>
    </div>
  </div>

  <div class="panel mt-24">
    <div class="table-wrap">
      <table class="table">
        <thead>
          <tr>
            <th style="width: 22%">用户名</th>
            <th>手机号</th>
            <th>技能标签</th>
            <th style="width: 14%">角色</th>
            <th style="width: 12%">信用分</th>
            <th style="width: 12%">注册时间</th>
            <th style="text-align: right">操作</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="u in filtered" :key="u.id">
            <td>
              <div class="flex center gap-8">
                <span class="avatar">{{ (u.username || '?').slice(0, 1).toUpperCase() }}</span>
                <span class="strong">{{ u.username }}</span>
              </div>
            </td>
            <td class="muted">{{ u.phone || '—' }}</td>
            <td class="muted" style="font-size: 12.5px">{{ u.skills || '未填写' }}</td>
            <td>
              <span class="pill" :class="roleCls(u.role)">{{ roleText(u.role) }}</span>
            </td>
            <td>
              <span :class="u.creditScore < 90 ? 'credit-warn' : ''">{{ u.creditScore ?? '—' }}</span>
            </td>
            <td class="muted nowrap">{{ u.createTime || '—' }}</td>
            <td>
              <div class="flex gap-8" style="justify-content: flex-end">
                <button class="btn btn-ghost btn-sm" @click="edit(u)">
                  <i class="bi bi-pencil"></i> 编辑
                </button>
              </div>
            </td>
          </tr>
          <tr v-if="err">
            <td colspan="7" style="text-align: center; padding: 30px; color: #b91c1c">
              {{ err }}
            </td>
          </tr>
          <tr v-else-if="!filtered.length">
            <td colspan="7" class="muted" style="text-align: center; padding: 40px">没有匹配的用户</td>
          </tr>
        </tbody>
      </table>
    </div>
  </div>

  <div v-if="cur" class="modal-mask" @click.self="cur = null">
    <div class="modal" style="max-width: 460px">
      <div class="modal-head">
        <h3>编辑用户 · {{ cur.username }}</h3>
        <button class="btn-plain" @click="cur = null"><i class="bi bi-x-lg"></i></button>
      </div>
      <div>
        <div style="margin-bottom: 16px">
          <label class="strong" style="font-size: 13px">角色</label>
          <select v-model="form.role" class="select" style="margin-top: 6px">
            <option value="volunteer">志愿者</option>
            <option value="organizer">组织者</option>
            <option value="admin">管理员</option>
          </select>
          <div class="hint">调整角色后，该用户重新登录即可获得对应菜单与权限。</div>
        </div>
        <div>
          <label class="strong" style="font-size: 13px">信用分</label>
          <input v-model.number="form.creditScore" type="number" min="0" max="100" class="input" style="margin-top: 6px" />
          <div class="hint">0–100，低于 80 分时前端会限制其报名热门活动。</div>
        </div>
      </div>
      <div class="modal-foot">
        <button class="btn btn-ghost" @click="cur = null">取消</button>
        <button class="btn btn-primary" :disabled="saving" @click="save">
          {{ saving ? '保存中…' : '保存' }}
        </button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { computed, onMounted, ref } from 'vue'
import api from '../api'
import { toast } from '../utils/toast'

const users = ref([])
const keyword = ref('')
const err = ref('')
const cur = ref(null)
const form = ref({ role: '', creditScore: 100 })
const saving = ref(false)

const filtered = computed(() =>
  users.value.filter(
    (u) =>
      !keyword.value ||
      (u.username || '').includes(keyword.value) ||
      (u.phone || '').includes(keyword.value)
  )
)

const counts = computed(() => {
  const c = { volunteer: 0, organizer: 0, admin: 0, lowCredit: 0 }
  users.value.forEach((u) => {
    if (c[u.role] !== undefined) c[u.role]++
    if ((u.creditScore ?? 100) < 90) c.lowCredit++
  })
  return c
})

const roleText = (r) => ({ volunteer: '志愿者', organizer: '组织者', admin: '管理员' }[r] || r)
const roleCls = (r) => ({ volunteer: 'green', organizer: 'amber', admin: 'sky' }[r] || 'grey')

async function load() {
  err.value = ''
  try {
    const data = await api.getUsers()
    users.value = Array.isArray(data) ? data : []
  } catch (e) {
    err.value = e.message
    toast.error(e.message)
  }
}

function edit(u) {
  cur.value = u
  form.value = { role: u.role, creditScore: u.creditScore ?? 100 }
}

async function save() {
  saving.value = true
  try {
    await api.updateUser(cur.value.id, form.value)
    toast.success('已保存')
    cur.value = null
    await load()
  } catch (e) {
    toast.error(e.message)
  } finally {
    saving.value = false
  }
}

onMounted(load)
</script>

<style scoped>
.credit-warn {
  color: #b45309;
  font-weight: 600;
}
</style>
