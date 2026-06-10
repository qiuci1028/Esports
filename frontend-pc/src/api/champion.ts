import { get } from '@/utils/request'
import type { ChampionStatsVO, ChampionHotVO } from '@/types/api'

export const championApi = {
  list: (params: { game?: string; patch?: string; role?: string; limit?: number } = {}) =>
    get<ChampionStatsVO[]>('/champion/list', params),
  top: (game = 'LOL', patch?: string, topN = 5) =>
    get<ChampionStatsVO[]>('/champion/top', { game, patch, topN }),
  hotTrend: (game = 'LOL', patch?: string, topN = 5) =>
    get<ChampionHotVO[]>('/champion/hot-trend', { game, patch, topN }),
  detail: (championId: string, game = 'LOL', patch?: string) =>
    get<ChampionStatsVO>(`/champion/detail/${championId}`, { game, patch })
}
