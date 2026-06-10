/**
 * 后端统一返回结构
 */
export interface ApiResult<T> {
  code: number
  msg: string
  data: T
  ts?: number
}

/** 分页结果 */
export interface PageResult<T> {
  total: number
  pageNum: number
  pageSize: number
  list: T[]
}

/** 用户信息 */
export interface UserInfo {
  id: number
  username: string
  nickname: string
  role: string
  avatar?: string
}

/** 登录响应 */
export interface LoginResponse {
  token: string
  expireHours: number
  user: UserInfo
}

/** 玩家列表项 */
export interface PlayerVO {
  id: number
  game: string
  puuid: string
  summonerName: string
  region: string
  level: number
  tier?: string
  rankName?: string
  leaguePoints?: number
  wins?: number
  losses?: number
  winRate?: number
  profileIconUrl?: string
  lastActive?: string
}

/** 玩家基础信息 */
export interface PlayerBasic {
  id: number
  game: string
  puuid: string
  summonerName: string
  region: string
  level: number
  profileIconUrl?: string
  lastActive?: string
}

/** 段位 */
export interface RankVO {
  tier: string
  rankTier: string
  leaguePoints: number
  wins: number
  losses: number
}

/** 段位历史 */
export interface RankHistoryVO {
  tier: string
  rankTier: string
  leaguePoints: number
  snapshotDate: string
}

/** KDA */
export interface KdaVO {
  matchId: string
  championId: string
  kills: number
  deaths: number
  assists: number
  win: number
}

/** 英雄熟练度 */
export interface MasteryVO {
  championId: string
  championName: string
  masteryPoints: number
  masteryLevel: number
}

/** 活跃时段 */
export interface ActiveHourVO {
  hour: number
  gameCount: number
}

/** 玩家详情聚合 */
export interface PlayerDetailDTO {
  basic: PlayerBasic
  currentRank?: RankVO
  rankHistory: RankHistoryVO[]
  kdaList: KdaVO[]
  topMastery: MasteryVO[]
  activeHour: ActiveHourVO[]
  winRate?: number
  mainRole?: string
  heroPoolSize?: number
}

/** 大屏核心指标 */
export interface OverviewDTO {
  totalMatches: number
  matchGrowth: number
  activePlayers: number
  playerGrowth: number
  avgRankLevel: number
  avgRankName: string
  rankGrowth: number
  onlineNow: number
}

/** 段位分布 */
export interface RankDistributionDTO {
  tier: string
  count: number
  percentage: number
}

/** 上分榜 */
export interface TopRankPlayerVO {
  rankNo: number
  summonerName: string
  tier: string
  rankName: string
  leaguePoints: number
  wins: number
  losses: number
  profileIconUrl?: string
}

/** 实时赛事 */
export interface RealtimeMatchVO {
  matchId: string
  game: string
  blueTeamName: string
  redTeamName: string
  blueScore: number
  redScore: number
  status: string
  startTime: string
}

/** 英雄统计 */
export interface ChampionStatsVO {
  championId: string
  name: string
  nameCn: string
  role: string
  iconUrl?: string
  games: number
  wins: number
  pickRate: number
  banRate: number
  winRate: number
  patchCode: string
}

/** 英雄热度趋势 */
export interface ChampionHotVO {
  date: string
  championId: string
  name: string
  nameCn: string
  pickRate: number
  winRate: number
}

/** 比赛列表 */
export interface MatchListVO {
  matchId: string
  game: string
  gameMode: string
  mapName: string
  patchCode: string
  durationSec: number
  creationTime: string
  winningTeam: string
}

/** 比赛详情-队员 */
export interface TeamMemberVO {
  puuid: string
  summonerName: string
  teamSide: string
  championId: string
  championName: string
  championIcon?: string
  rolePosition: string
  kills: number
  deaths: number
  assists: number
  goldEarned: number
  damageDealt: number
  visionScore: number
  win: number
}

/** 比赛详情-队伍汇总 */
export interface TeamSummaryVO {
  totalKills: number
  totalDeaths: number
  totalAssists: number
  totalGold: number
  totalDamage: number
  totalVision: number
}

/** 比赛详情 */
export interface MatchDetailDTO {
  basic: {
    matchId: string
    game: string
    gameMode: string
    mapName: string
    patchCode: string
    durationSec: number
    creationTime: string
    winningTeam: string
  }
  blueTeam: TeamMemberVO[]
  redTeam: TeamMemberVO[]
  blueSummary: TeamSummaryVO
  redSummary: TeamSummaryVO
}
