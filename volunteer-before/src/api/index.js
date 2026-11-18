import request from './request'

export default {
  // 认证
  login: (data) => request.post('/auth/login', data),
  register: (data) => request.post('/auth/register', data),

  // 个人资料
  getProfile: () => request.get('/profile'),
  updateProfile: (data) => request.put('/profile', data),

  // 活动（公开）
  getActivities: (params) => request.get('/activities', { params }),
  getActivity: (id) => request.get(`/activities/${id}`),
  getOverview: () => request.get('/stats/overview'),

  // 报名 / 工时（本人）
  signup: (activityId) => request.post('/registrations', { activityId }),
  cancelSignup: (id) => request.delete(`/registrations/${id}`),
  getMyRegistrations: () => request.get('/registrations/my'),
  getMyHours: () => request.get('/attendance/my'),

  // 管理端：看板
  getDashboard: () => request.get('/admin/dashboard'),

  // 管理端：活动
  getAllActivities: () => request.get('/admin/activities'),
  createActivity: (data) => request.post('/admin/activities', data),
  updateActivity: (id, data) => request.put(`/admin/activities/${id}`, data),
  changeActivityStatus: (id, status) => request.patch(`/admin/activities/${id}/status`, { status }),
  deleteActivity: (id) => request.delete(`/admin/activities/${id}`),

  // 管理端：报名审核与工时
  getRegistrations: (status) => request.get('/admin/registrations', { params: { status } }),
  auditRegistration: (id, status) => request.post(`/admin/registrations/${id}/audit`, { status }),
  markAbsent: (id) => request.post(`/admin/registrations/${id}/absent`),
  issueHours: (registrationId, hours) => request.post('/admin/attendance', { registrationId, hours }),

  // 管理端：用户与角色（仅 admin）
  getUsers: () => request.get('/admin/users'),
  updateUser: (id, data) => request.patch(`/admin/users/${id}`, data),

  // 管理端：管理员认证（仅 admin）
  getPendingAdmins: () => request.get('/admin/pending-admins'),
  approveAdmin: (id) => request.post(`/admin/pending-admins/${id}/approve`),
  rejectAdmin: (id) => request.post(`/admin/pending-admins/${id}/reject`)
}
