import request from './request'

export default {
  login: (data) => request.post('/auth/login', data),
  register: (data) => request.post('/auth/register', data),
  getActivities: () => request.get('/activities'),
  getActivity: (id) => request.get(`/activities/${id}`),
  signup: (activityId) => request.post('/registrations', { activityId }),
  getMyRegistrations: () => request.get('/registrations/my'),
  getMyHours: () => request.get('/attendance/my'),
  getDashboard: () => request.get('/admin/dashboard')
}