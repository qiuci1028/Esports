import { get } from '@/utils/request'
import type { MatchDetailDTO, MatchListVO, PageResult } from '@/types/api'

export const matchApi = {
  page: (params: {
    game?: string
    gameMode?: string
    patch?: string
    pageNum?: number
    pageSize?: number
  }) => get<PageResult<MatchListVO>>('/match/page', params),
  detail: (matchId: string) => get<MatchDetailDTO>(`/match/detail/${matchId}`)
}
