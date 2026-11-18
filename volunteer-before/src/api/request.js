import axios from 'axios'
import { useAuthStore } from '../store/auth'
import router from '../router'

const request = axios.create({
  baseURL: 'http://localhost:8080/api',
  timeout: 10000
})

request.interceptors.request.use(config => {
  const token = localStorage.getItem('token')
  if (token) config.headers.Authorization = `Bearer ${token}`
  return config
})

request.interceptors.response.use(
  res => {
    if (res.data.code !== 200) throw new Error(res.data.message)
    return res.data.data
  },
  err => {
    if (err.response?.status === 401) {
      useAuthStore().logout()
      router.push('/login')
    }
    return Promise.reject(new Error(err.response?.data?.message || err.message || '请求失败'))
  }
)

export default request