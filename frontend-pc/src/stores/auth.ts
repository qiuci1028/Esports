import { defineStore } from 'pinia'
import { authApi } from '@/api/auth'
import { setToken, clearToken, getToken } from '@/utils/request'
import type { UserInfo } from '@/types/api'

export const useAuthStore = defineStore('auth', {
  state: () => ({
    token: getToken(),
    user: null as UserInfo | null
  }),
  persist: {
    key: 'esports_auth',
    storage: localStorage,
    pick: ['user']
  },
  actions: {
    async login(username: string, password: string) {
      const resp = await authApi.login(username, password)
      this.token = resp.token
      this.user = resp.user
      setToken(resp.token)
      return resp
    },
    async fetchMe() {
      if (!this.token) return null
      try {
        this.user = await authApi.me()
        return this.user
      } catch {
        this.logout()
        return null
      }
    },
    logout() {
      this.token = ''
      this.user = null
      clearToken()
    },
    hasRole(role: string) {
      return this.user?.role === role
    }
  }
})
