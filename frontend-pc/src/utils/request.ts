import axios, { type AxiosInstance, type AxiosRequestConfig, type AxiosResponse } from 'axios'
import { ElMessage } from 'element-plus'
import type { ApiResult } from '@/types/api'

const TOKEN_KEY = 'esports_token'

export const getToken = () => localStorage.getItem(TOKEN_KEY) || ''
export const setToken = (t: string) => localStorage.setItem(TOKEN_KEY, t)
export const clearToken = () => localStorage.removeItem(TOKEN_KEY)

const service: AxiosInstance = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL || '/api',
  timeout: 15000,
  withCredentials: false
})

service.interceptors.request.use((cfg) => {
  const token = getToken()
  if (token) {
    cfg.headers['X-Token'] = `Bearer ${token}`
  }
  return cfg
})

service.interceptors.response.use(
  (resp: AxiosResponse<ApiResult<unknown>>) => {
    const body = resp.data
    if (body && typeof body === 'object' && 'code' in body) {
      if (body.code === 0) return resp
      ElMessage.error(body.msg || `请求失败（${body.code}）`)
      return Promise.reject(body)
    }
    return resp
  },
  (err) => {
    const status = err?.response?.status
    const msg = err?.response?.data?.msg || err.message || '网络异常'
    if (status === 401) {
      clearToken()
      ElMessage.warning('登录已失效，请重新登录')
      setTimeout(() => (window.location.href = '/login'), 500)
    } else {
      ElMessage.error(msg)
    }
    return Promise.reject(err)
  }
)

export function get<T>(url: string, params?: Record<string, unknown>, cfg?: AxiosRequestConfig) {
  return service.get<unknown, AxiosResponse<ApiResult<T>>>(url, { params, ...cfg })
    .then((r) => r.data.data)
}

export function post<T>(url: string, data?: unknown, cfg?: AxiosRequestConfig) {
  return service.post<unknown, AxiosResponse<ApiResult<T>>>(url, data, cfg)
    .then((r) => r.data.data)
}

export default service
