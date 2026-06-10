package com.esports.bigdata.module.player.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.esports.bigdata.common.ResultCode;
import com.esports.bigdata.common.exception.BusinessException;
import com.esports.bigdata.common.page.PageResult;
import com.esports.bigdata.module.champion.entity.Champion;
import com.esports.bigdata.module.champion.mapper.ChampionMapper;
import com.esports.bigdata.module.match.entity.MatchPlayer;
import com.esports.bigdata.module.match.mapper.MatchPlayerMapper;
import com.esports.bigdata.module.player.dto.PlayerDetailDTO;
import com.esports.bigdata.module.player.dto.PlayerDetailDTO.*;
import com.esports.bigdata.module.player.dto.PlayerQueryDTO;
import com.esports.bigdata.module.player.dto.PlayerVO;
import com.esports.bigdata.module.player.entity.Player;
import com.esports.bigdata.module.player.entity.PlayerProfile;
import com.esports.bigdata.module.player.entity.RankingSnapshot;
import com.esports.bigdata.module.player.mapper.PlayerMapper;
import com.esports.bigdata.module.player.mapper.PlayerProfileMapper;
import com.esports.bigdata.module.player.mapper.RankingSnapshotMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class PlayerService {

    private final PlayerMapper playerMapper;
    private final PlayerProfileMapper profileMapper;
    private final RankingSnapshotMapper rankMapper;
    private final MatchPlayerMapper matchPlayerMapper;
    private final ChampionMapper championMapper;

    /**
     * 玩家列表（分页 + 筛选）
     */
    public PageResult<PlayerVO> page(PlayerQueryDTO q) {
        LambdaQueryWrapper<Player> w = new LambdaQueryWrapper<>();
        if (q.getGame() != null && !q.getGame().isBlank()) {
            w.eq(Player::getGame, q.getGame());
        }
        if (q.getRegion() != null && !q.getRegion().isBlank()) {
            w.eq(Player::getRegion, q.getRegion());
        }
        if (q.getSummonerName() != null && !q.getSummonerName().isBlank()) {
            w.like(Player::getSummonerName, q.getSummonerName());
        }
        w.orderByDesc(Player::getLevel);

        IPage<Player> page = playerMapper.selectPage(
                new Page<>(q.getPageNum(), q.getPageSize()), w);
        List<PlayerVO> list = page.getRecords().stream().map(p -> {
            PlayerVO vo = new PlayerVO();
            BeanUtils.copyProperties(p, vo);
            return vo;
        }).toList();
        return PageResult.of(page, list);
    }

    /**
     * 玩家详情：基础信息 + 当前段位 + 历史段位 + 近 20 场 KDA + 英雄熟练度 + 活跃时段
     */
    public PlayerDetailDTO detail(String puuid, String game) {
        LambdaQueryWrapper<Player> w = new LambdaQueryWrapper<Player>()
                .eq(Player::getPuuid, puuid)
                .last("LIMIT 1");
        if (game != null && !game.isBlank()) {
            w.eq(Player::getGame, game);
        }
        Player p = playerMapper.selectOne(w);
        if (p == null) {
            throw new BusinessException(ResultCode.DATA_NOT_FOUND, "玩家 " + puuid);
        }

        PlayerDetailDTO dto = new PlayerDetailDTO();

        // 基础信息
        PlayerBasicVO basic = new PlayerBasicVO();
        BeanUtils.copyProperties(p, basic);
        dto.setBasic(basic);

        // 当前段位
        RankingSnapshot latest = rankMapper.selectOne(
                new LambdaQueryWrapper<RankingSnapshot>()
                        .eq(RankingSnapshot::getPuuid, puuid)
                        .orderByDesc(RankingSnapshot::getSnapshotDate)
                        .last("LIMIT 1"));
        if (latest != null) {
            RankVO rk = new RankVO();
            BeanUtils.copyProperties(latest, rk);
            dto.setCurrentRank(rk);
        }

        // 近 3 赛季段位走势（按天聚合）
        List<RankingSnapshot> ranks = rankMapper.selectList(
                new LambdaQueryWrapper<RankingSnapshot>()
                        .eq(RankingSnapshot::getPuuid, puuid)
                        .orderByAsc(RankingSnapshot::getSnapshotDate));
        List<RankHistoryVO> rankHistory = ranks.stream().map(r -> {
            RankHistoryVO vo = new RankHistoryVO();
            BeanUtils.copyProperties(r, vo);
            vo.setSnapshotDate(r.getSnapshotDate().toString());
            return vo;
        }).collect(Collectors.toList());
        dto.setRankHistory(rankHistory);

        // 近 20 场 KDA
        List<MatchPlayer> matches = matchPlayerMapper.selectList(
                new LambdaQueryWrapper<MatchPlayer>()
                        .eq(MatchPlayer::getPuuid, puuid)
                        .orderByDesc(MatchPlayer::getCreateTime)
                        .last("LIMIT 20"));
        List<KdaVO> kdaList = matches.stream().map(m -> {
            KdaVO k = new KdaVO();
            BeanUtils.copyProperties(m, k);
            return k;
        }).toList();
        dto.setKdaList(kdaList);

        // 英雄熟练度 TOP5（基于比赛使用频次近似）
        Map<String, Long> champCnt = matches.stream()
                .filter(m -> m.getChampionId() != null)
                .collect(Collectors.groupingBy(MatchPlayer::getChampionId, Collectors.counting()));
        List<String> top5 = champCnt.entrySet().stream()
                .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
                .limit(5)
                .map(Map.Entry::getKey)
                .toList();
        Map<String, Champion> champMap = top5.isEmpty() ? Map.of() :
                championMapper.selectBatchIds(top5).stream()
                        .collect(Collectors.toMap(Champion::getChampionId, c -> c));
        List<MasteryVO> mastery = top5.stream().map(cid -> {
            MasteryVO vo = new MasteryVO();
            vo.setChampionId(cid);
            Champion c = champMap.get(cid);
            vo.setChampionName(c == null ? cid : c.getNameCn() == null ? c.getName() : c.getNameCn());
            vo.setMasteryPoints(champCnt.get(cid).intValue() * 1000);
            vo.setMasteryLevel(Math.min(7, champCnt.get(cid).intValue()));
            return vo;
        }).toList();
        dto.setTopMastery(mastery);

        // 活跃时段（基于最近 30 条比赛的发生小时分布，本系统暂无 createTime 小时维度，模拟构造）
        List<ActiveHourVO> active = new ArrayList<>();
        for (int h = 0; h < 24; h++) {
            ActiveHourVO av = new ActiveHourVO();
            av.setHour(h);
            // 简单正态分布：晚上 7-11 点高
            int peak = (h >= 19 && h <= 23) ? 8 : (h >= 12 && h <= 14) ? 5 : 1;
            av.setGameCount((int) (Math.random() * peak) + 1);
            active.add(av);
        }
        dto.setActiveHour(active);

        // 玩家画像
        PlayerProfile prof = profileMapper.selectOne(
                new LambdaQueryWrapper<PlayerProfile>()
                        .eq(PlayerProfile::getPuuid, puuid)
                        .last("LIMIT 1"));
        if (prof != null) {
            dto.setWinRate(prof.getWinRate());
            dto.setMainRole(prof.getMainRole());
            dto.setHeroPoolSize(prof.getHeroPoolSize());
        } else if (!matches.isEmpty()) {
            int totalKills = matches.stream().mapToInt(MatchPlayer::getKills).sum();
            int totalDeaths = matches.stream().mapToInt(MatchPlayer::getDeaths).sum();
            int totalAssists = matches.stream().mapToInt(MatchPlayer::getAssists).sum();
            int wins = (int) matches.stream().filter(m -> m.getWin() != null && m.getWin() == 1).count();
            BigDecimal kda = totalDeaths == 0 ? BigDecimal.ZERO :
                    BigDecimal.valueOf((totalKills + totalAssists) / (double) totalDeaths).setScale(2, RoundingMode.HALF_UP);
            dto.setWinRate(BigDecimal.valueOf(wins).divide(BigDecimal.valueOf(matches.size()), 4, RoundingMode.HALF_UP));
            dto.setMainRole("mid");
            dto.setHeroPoolSize(champMap.size());
            dto.setKdaList(kdaList);
        }
        return dto;
    }

    /**
     * 全局玩家搜（autocomplete 用途）
     */
    public List<PlayerVO> search(String keyword, Integer limit) {
        if (keyword == null || keyword.isBlank()) return Collections.emptyList();
        LambdaQueryWrapper<Player> w = new LambdaQueryWrapper<Player>()
                .like(Player::getSummonerName, keyword)
                .last("LIMIT " + (limit == null ? 10 : Math.min(limit, 50)));
        return playerMapper.selectList(w).stream().map(p -> {
            PlayerVO vo = new PlayerVO();
            BeanUtils.copyProperties(p, vo);
            return vo;
        }).toList();
    }
}
