import { defineStore } from 'pinia'

export type GameKey = 'LOL' | 'VALORANT' | 'TFT'

export const useAppStore = defineStore('app', {
  state: () => ({
    currentGame: 'LOL' as GameKey,
    currentPatch: '14.10',
    refreshAt: Date.now()
  }),
  persist: {
    key: 'esports_app',
    storage: localStorage
  },
  actions: {
    setGame(g: GameKey) {
      this.currentGame = g
      // 切游戏时同步切换版本
      this.currentPatch = g === 'LOL' ? '14.10' : g === 'VALORANT' ? '9.0' : 'S10'
    },
    setPatch(p: string) {
      this.currentPatch = p
    },
    refresh() {
      this.refreshAt = Date.now()
    }
  }
})
