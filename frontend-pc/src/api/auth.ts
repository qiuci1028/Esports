import { post, get } from '@/utils/request'
import type { LoginResponse, UserInfo } from '@/types/api'

export const authApi = {
  login: (username: string, password: string) =>
    post<LoginResponse>('/auth/login', { username, password }),
  me: () => get<UserInfo>('/auth/me'),
  hello: () => get<{ service: string; version: string; ts: string }>('/hello')
}
