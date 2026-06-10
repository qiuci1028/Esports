package com.esports.bigdata.module.dashboard.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.esports.bigdata.module.champion.entity.Champion;
import com.esports.bigdata.module.champion.entity.ChampionStats;
import com.esports.bigdata.module.champion.mapper.ChampionMapper;
import com.esports.bigdata.module.champion.mapper.ChampionStatsMapper;
import com.esports.bigdata.module.dashboard.dto.*;
import com.esports.bigdata.module.dashboard.dto.DashboardConstants;
import com.esports.bigdata.module.match.entity.Match;
import com.esports.bigdata.module.match.entity.MatchPlayer;
import com.esports.bigdata.module.match.mapper.MatchMapper;
import com.esports.bigdata.module.match.mapper.MatchPlayerMapper;
import com.esports.bigdata.module.player.entity.Player;
import com.esports.bigdata.module.player.entity.RankingSnapshot;
import com.esports.bigdata.module.player.mapper.PlayerMapper;
import com.esports.bigdata.module.player.mapper.RankingSnapshotMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DashboardService {

    private final MatchMapper matchMapper;
    private final MatchPlayerMapper matchPlayerMapper;
    private final PlayerMapper playerMapper;
    private final RankingSnapshotMapper rankMapper;
    private final ChampionStatsMapper championStatsMapper;
    private final ChampionMapper championMapper;

    /** 段位 → 等级数值映射，用于计算平均段位 */
    private static final Map<String, Integer> TIER_LEVEL = new LinkedHashMap<>();
    static {
        TIER_LEVEL.put("IRON", 1);
        TIER_LEVEL.put("BRONZE", 2);
        TIER_LEVEL.put("SILVER", 3);
        TIER_LEVEL.put("GOLD", 4);
        TIER_LEVEL.put("PLATINUM", 5);
        TIER_LEVEL.put("EMERALD", 6);
        TIER_LEVEL.put("DIAMOND", 7);
        TIER_LEVEL.put("MASTER", 8);
        TIER_LEVEL.put("GRANDMASTER", 9);
        TIER_LEVEL.put("CHALLENGER", 10);
    }
    private static final Map<Integer, String> LEVEL_TO_NAME = Map.of(
            1, "黑铁", 2, "青铜", 3, "白银", 4, "黄金", 5, "铂金",
            6, "翡翠", 7, "钻石", 8, "大师", 9, "宗师", 10, "王者");

    public OverviewDTO overview(String game) {
        OverviewDTO dto = new OverviewDTO();
        String g = game == null || game.isBlank() ? "LOL" : game;

        // 总比赛量
        Long totalMatches = matchMapper.selectCount(
                new LambdaQueryWrapper<Match>().eq(Match::getGame, g));
        dto.setTotalMatches(totalMatches);
        dto.setMatchGrowth(new BigDecimal("0.082").setScale(3, RoundingMode.HALF_UP));

        // 活跃玩家
        Long activePlayers = playerMapper.selectCount(
                new LambdaQueryWrapper<Player>().eq(Player::getGame, g));
        dto.setActivePlayers(activePlayers);
        dto.setPlayerGrowth(new BigDecimal("0.156").setScale(3, RoundingMode.HALF_UP));

        // 平均段位
        List<RankingSnapshot> latestRanks = rankMapper.selectList(
                new LambdaQueryWrapper<RankingSnapshot>()
                        .eq(RankingSnapshot::getGame, g)
                        .orderByDesc(RankingSnapshot::getSnapshotDate));
        if (!latestRanks.isEmpty()) {
            // 取每个 puuid 最新一条
            Map<String, RankingSnapshot> byPlayer = new LinkedHashMap<>();
            for (RankingSnapshot r : latestRanks) {
                byPlayer.putIfAbsent(r.getPuuid(), r);
            }
            double avgLevel = byPlayer.values().stream()
                    .filter(r -> r.getTier() != null && TIER_LEVEL.containsKey(r.getTier()))
                    .mapToInt(r -> TIER_LEVEL.get(r.getTier()))
                    .average().orElse(4.0);
            int level = (int) Math.round(avgLevel);
            dto.setAvgRankLevel(BigDecimal.valueOf(avgLevel).setScale(2, RoundingMode.HALF_UP));
            dto.setAvgRankName(LEVEL_TO_NAME.getOrDefault(level, "黄金"));
        } else {
            dto.setAvgRankLevel(BigDecimal.valueOf(4.20));
            dto.setAvgRankName("黄金");
        }
        dto.setRankGrowth(new BigDecimal("0.021").setScale(3, RoundingMode.HALF_UP));

        // 实时在线（演示：总玩家数 8%）
        dto.setOnlineNow((int) (activePlayers * 0.08));
        return dto;
    }

    public List<RankDistributionDTO> rankDistribution(String game) {
        String g = game == null || game.isBlank() ? "LOL" : game;
        List<RankingSnapshot> ranks = rankMapper.selectList(
                new LambdaQueryWrapper<RankingSnapshot>().eq(RankingSnapshot::getGame, g));
        // 每个玩家只保留最新
        Map<String, RankingSnapshot> byPlayer = new LinkedHashMap<>();
        for (RankingSnapshot r : ranks) {
            byPlayer.putIfAbsent(r.getPuuid(), r);
        }
        Map<String, Long> cnt = byPlayer.values().stream()
                .filter(r -> r.getTier() != null)
                .collect(Collectors.groupingBy(RankingSnapshot::getTier, Collectors.counting()));
        long total = cnt.values().stream().mapToLong(Long::longValue).sum();

        List<RankDistributionDTO> out = new ArrayList<>();
        for (Map.Entry<String, Integer> e : TIER_LEVEL.entrySet()) {
            long c = cnt.getOrDefault(e.getKey(), 0L);
            RankDistributionDTO d = new RankDistributionDTO();
            d.setTier(e.getKey());
            d.setCount((int) c);
            d.setPercentage(total == 0 ? 0.0 : BigDecimal.valueOf(c * 100.0 / total)
                    .setScale(2, RoundingMode.HALF_UP).doubleValue());
            out.add(d);
        }
        return out;
    }

    public List<TopRankPlayerVO> topRank(String game, int topN) {
        String g = game == null || game.isBlank() ? "LOL" : game;
        List<RankingSnapshot> ranks = rankMapper.selectList(
                new LambdaQueryWrapper<RankingSnapshot>()
                        .eq(RankingSnapshot::getGame, g)
                        .orderByDesc(RankingSnapshot::getSnapshotDate)
                        .orderByDesc(RankingSnapshot::getLeaguePoints));
        Map<String, RankingSnapshot> byPlayer = new LinkedHashMap<>();
        for (RankingSnapshot r : ranks) {
            byPlayer.putIfAbsent(r.getPuuid(), r);
        }
        List<RankingSnapshot> top = byPlayer.values().stream()
                .sorted(Comparator.comparingInt(RankingSnapshot::getLeaguePoints).reversed())
                .limit(topN)
                .toList();
        Set<String> puuids = top.stream().map(RankingSnapshot::getPuuid).collect(Collectors.toSet());
        Map<String, Player> pMap = puuids.isEmpty() ? Map.of() :
                playerMapper.selectList(
                        new LambdaQueryWrapper<Player>().in(Player::getPuuid, puuids))
                        .stream().collect(Collectors.toMap(Player::getPuuid, p -> p));

        List<TopRankPlayerVO> out = new ArrayList<>();
        for (int i = 0; i < top.size(); i++) {
            RankingSnapshot r = top.get(i);
            TopRankPlayerVO vo = new TopRankPlayerVO();
            vo.setRankNo(i + 1);
            Player p = pMap.get(r.getPuuid());
            vo.setSummonerName(p == null ? r.getPuuid() : p.getSummonerName());
            vo.setTier(r.getTier());
            vo.setRankName(r.getRankTier());
            vo.setLeaguePoints(r.getLeaguePoints());
            vo.setWins(r.getWins());
            vo.setLosses(r.getLosses());
            vo.setProfileIconUrl(p == null ? null : p.getProfileIconUrl());
            out.add(vo);
        }
        return out;
    }

    public List<RealtimeMatchVO> realtimeMatches(String game, int limit) {
        String g = game == null || game.isBlank() ? "LOL" : game;
        List<Match> matches = matchMapper.selectList(
                new LambdaQueryWrapper<Match>()
                        .eq(Match::getGame, g)
                        .orderByDesc(Match::getCreationTime)
                        .last("LIMIT " + Math.min(limit, 50)));

        Set<String> matchIds = matches.stream().map(Match::getMatchId).collect(Collectors.toSet());
        List<MatchPlayer> players = matchIds.isEmpty() ? List.of() :
                matchPlayerMapper.selectList(
                        new LambdaQueryWrapper<MatchPlayer>().in(MatchPlayer::getMatchId, matchIds));

        // 聚合每场两边名称
        Map<String, List<MatchPlayer>> byMatch = players.stream()
                .collect(Collectors.groupingBy(MatchPlayer::getMatchId));

        List<RealtimeMatchVO> out = new ArrayList<>();
        for (Match m : matches) {
            List<MatchPlayer> ps = byMatch.getOrDefault(m.getMatchId(), List.of());
            String blueNames = ps.stream()
                    .filter(p -> "BLUE".equalsIgnoreCase(p.getTeamSide()))
                    .map(p -> p.getPuuid() == null ? "蓝方" : p.getPuuid().substring(0, 4))
                    .collect(Collectors.joining("·"));
            String redNames = ps.stream()
                    .filter(p -> "RED".equalsIgnoreCase(p.getTeamSide()))
                    .map(p -> p.getPuuid() == null ? "红方" : p.getPuuid().substring(0, 4))
                    .collect(Collectors.joining("·"));
            int blueKills = ps.stream()
                    .filter(p -> "BLUE".equalsIgnoreCase(p.getTeamSide()))
                    .mapToInt(MatchPlayer::getKills).sum();
            int redKills = ps.stream()
                    .filter(p -> "RED".equalsIgnoreCase(p.getTeamSide()))
                    .mapToInt(MatchPlayer::getKills).sum();

            RealtimeMatchVO vo = new RealtimeMatchVO();
            vo.setMatchId(m.getMatchId());
            vo.setGame(m.getGame());
            vo.setBlueTeamName(blueNames.isEmpty() ? "BLUE" : blueNames);
            vo.setRedTeamName(redNames.isEmpty() ? "RED" : redNames);
            vo.setBlueScore(blueKills);
            vo.setRedScore(redKills);
            vo.setStatus("FINISHED");
            vo.setStartTime(m.getCreationTime() == null ? null : m.getCreationTime().toString());
            out.add(vo);
        }
        return out;
    }

    public List<ChampionStats> topChampionsByPatch(String game, String patch, int topN) {
        String g = game == null || game.isBlank() ? "LOL" : game;
        String p = patch == null || patch.isBlank() ? DashboardConstants.CURRENT_PATCH : patch;
        return championStatsMapper.selectList(
                new LambdaQueryWrapper<ChampionStats>()
                        .eq(ChampionStats::getGame, g)
                        .eq(ChampionStats::getPatchCode, p)
                        .orderByDesc(ChampionStats::getPickRate)
                        .last("LIMIT " + topN));
    }

    public List<ChampionHotVO> hotTrend(String game, String patch, int topN) {
        String g = game == null || game.isBlank() ? "LOL" : game;
        String p = patch == null || patch.isBlank() ? DashboardConstants.CURRENT_PATCH : patch;
        // 取当前版本所有英雄统计，按 pickRate 排序截取 TOP N
        List<ChampionStats> rows = championStatsMapper.selectList(
                new LambdaQueryWrapper<ChampionStats>()
                        .eq(ChampionStats::getGame, g)
                        .eq(ChampionStats::getPatchCode, p)
                        .orderByDesc(ChampionStats::getPickRate)
                        .last("LIMIT " + Math.max(topN * 4, 20)));
        // 加载英雄名映射
        Map<String, Champion> champMap = championMapper.selectList(
                new LambdaQueryWrapper<Champion>())
                .stream().collect(Collectors.toMap(Champion::getChampionId, c -> c, (a, b) -> a));
        // 按天构造 7 天趋势数据（演示用，模拟热度变化）
        List<ChampionHotVO> out = new ArrayList<>();
        LocalDate today = LocalDate.now();
        for (ChampionStats r : rows.stream().limit(topN).toList()) {
            Champion champ = champMap.get(r.getChampionId());
            for (int d = 6; d >= 0; d--) {
                LocalDate dt = today.minusDays(d);
                ChampionHotVO vo = new ChampionHotVO();
                vo.setDate(dt.toString());
                vo.setChampionId(r.getChampionId());
                vo.setName(champ == null ? r.getChampionId() : champ.getName());
                vo.setNameCn(champ == null ? r.getChampionId() : champ.getNameCn());
                double wave = Math.sin(d * 0.6 + r.getChampionId().hashCode() % 7) * 0.05;
                double basePick = r.getPickRate() == null ? 0.10 : r.getPickRate().doubleValue();
                double baseWin = r.getWinRate() == null ? 0.50 : r.getWinRate().doubleValue();
                vo.setPickRate(BigDecimal.valueOf(Math.max(0.01, basePick + wave))
                        .setScale(4, RoundingMode.HALF_UP));
                vo.setWinRate(BigDecimal.valueOf(Math.min(0.99, Math.max(0.30, baseWin + wave / 2)))
                        .setScale(4, RoundingMode.HALF_UP));
                out.add(vo);
            }
        }
        return out;
    }
}
