import { get } from '@/utils/request'
import type { PageResult, PlayerDetailDTO, PlayerVO } from '@/types/api'

export const playerApi = {
  page: (params: {
    game?: string
    region?: string
    summonerName?: string
    pageNum?: number
    pageSize?: number
  }) => get<PageResult<PlayerVO>>('/player/page', params),
  detail: (puuid: string, game?: string) =>
    get<PlayerDetailDTO>('/player/detail', { puuid, game }),
  search: (keyword: string, limit = 10) =>
    get<PlayerVO[]>('/player/search', { keyword, limit })
}
