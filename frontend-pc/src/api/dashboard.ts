import { get } from '@/utils/request'
import type {
  OverviewDTO,
  RankDistributionDTO,
  TopRankPlayerVO,
  RealtimeMatchVO,
  ChampionStatsVO,
  ChampionHotVO
} from '@/types/api'

export const dashboardApi = {
  overview: (game = 'LOL') => get<OverviewDTO>('/dashboard/overview', { game }),
  rankDistribution: (game = 'LOL') => get<RankDistributionDTO[]>('/dashboard/rank-distribution', { game }),
  topRank: (game = 'LOL', topN = 10) => get<TopRankPlayerVO[]>('/dashboard/top-rank', { game, topN }),
  realtimeMatches: (game = 'LOL', limit = 10) => get<RealtimeMatchVO[]>('/dashboard/realtime-matches', { game, limit }),
  topChampions: (game = 'LOL', patch?: string, topN = 5) =>
    get<ChampionStatsVO[]>('/dashboard/top-champions', { game, patch, topN }),
  hotTrend: (game = 'LOL', patch?: string, topN = 5) =>
    get<ChampionHotVO[]>('/dashboard/hot-trend', { game, patch, topN })
}
