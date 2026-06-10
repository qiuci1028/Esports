package com.esports.bigdata.module.champion.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.esports.bigdata.common.ResultCode;
import com.esports.bigdata.common.exception.BusinessException;
import com.esports.bigdata.module.champion.dto.ChampionHotVO;
import com.esports.bigdata.module.champion.dto.ChampionStatsVO;
import com.esports.bigdata.module.champion.entity.Champion;
import com.esports.bigdata.module.champion.entity.ChampionStats;
import com.esports.bigdata.module.champion.mapper.ChampionMapper;
import com.esports.bigdata.module.champion.mapper.ChampionStatsMapper;
import com.esports.bigdata.module.dashboard.dto.DashboardConstants;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ChampionService {

    private final ChampionMapper championMapper;
    private final ChampionStatsMapper statsMapper;

    /**
     * 当前版本英雄 BP 胜率排行（按 pick_rate 降序）
     */
    public List<ChampionStatsVO> listByPatch(String game, String patch, String role, Integer limit) {
        LambdaQueryWrapper<ChampionStats> w = new LambdaQueryWrapper<>();
        if (game != null && !game.isBlank()) w.eq(ChampionStats::getGame, game);
        if (patch != null && !patch.isBlank()) w.eq(ChampionStats::getPatchCode, patch);
        if (role != null && !role.isBlank()) w.eq(ChampionStats::getRole, role);
        w.orderByDesc(ChampionStats::getPickRate);
        if (limit != null && limit > 0) w.last("LIMIT " + Math.min(limit, 200));

        List<ChampionStats> rows = statsMapper.selectList(w);
        if (rows.isEmpty()) return Collections.emptyList();

        // 关联 hero 元数据
        List<String> ids = rows.stream().map(ChampionStats::getChampionId).distinct().toList();
        Map<String, Champion> map = championMapper.selectBatchIds(ids).stream()
                .collect(Collectors.toMap(Champion::getChampionId, c -> c));

        return rows.stream().map(s -> {
            ChampionStatsVO vo = new ChampionStatsVO();
            BeanUtils.copyProperties(s, vo);
            Champion c = map.get(s.getChampionId());
            if (c != null) {
                vo.setName(c.getName());
                vo.setNameCn(c.getNameCn() == null ? c.getName() : c.getNameCn());
                vo.setRole(c.getRole());
                vo.setIconUrl(c.getIconUrl());
            }
            return vo;
        }).toList();
    }

    /**
     * T0 Top N（pick_rate 最高的前 N）
     */
    public List<ChampionStatsVO> topTier(String game, String patch, int topN) {
        return listByPatch(game, patch, null, topN);
    }

    /**
     * 24 小时英雄热度趋势（近 7 天按日聚合的 pickRate 平均）
     */
    public List<ChampionHotVO> hotTrend(String game, String patch, int topN) {
        // 取 pickRate 最高的 topN 个英雄
        List<ChampionStatsVO> top = topTier(game, patch, topN);
        if (top.isEmpty()) return Collections.emptyList();
        Set<String> ids = top.stream().map(ChampionStatsVO::getChampionId).collect(Collectors.toSet());

        LocalDate today = LocalDate.now();
        LocalDate startDate = today.minusDays(6);

        List<ChampionStats> rows = statsMapper.selectList(
                new LambdaQueryWrapper<ChampionStats>()
                        .eq(ChampionStats::getGame, game == null ? "LOL" : game)
                        .eq(ChampionStats::getPatchCode, patch == null ? DashboardConstants.CURRENT_PATCH : patch)
                        .between(ChampionStats::getDt, startDate, today)
                        .in(ChampionStats::getChampionId, ids));
        // 按 (date, champion) 分组
        Map<String, List<ChampionStats>> byDate = rows.stream()
                .collect(Collectors.groupingBy(s -> s.getDt().toString()));

        List<ChampionHotVO> out = new ArrayList<>();
        for (int i = 6; i >= 0; i--) {
            String date = today.minusDays(i).toString();
            List<ChampionStats> dayRows = byDate.getOrDefault(date, Collections.emptyList());
            for (ChampionStats s : dayRows) {
                ChampionHotVO vo = new ChampionHotVO();
                vo.setDate(date);
                vo.setChampionId(s.getChampionId());
                vo.setPickRate(s.getPickRate());
                vo.setWinRate(s.getWinRate());
                Champion c = championMapper.selectById(s.getChampionId());
                if (c != null) {
                    vo.setName(c.getName());
                    vo.setNameCn(c.getNameCn() == null ? c.getName() : c.getNameCn());
                }
                out.add(vo);
            }
        }
        return out;
    }

    /**
     * 英雄详情（含最近版本统计）
     */
    public ChampionStatsVO detail(String championId, String game, String patch) {
        Champion c = championMapper.selectOne(
                new LambdaQueryWrapper<Champion>().eq(Champion::getChampionId, championId).last("LIMIT 1"));
        if (c == null) throw new BusinessException(ResultCode.DATA_NOT_FOUND, "英雄 " + championId);

        ChampionStats s = statsMapper.selectOne(
                new LambdaQueryWrapper<ChampionStats>()
                        .eq(ChampionStats::getChampionId, championId)
                        .eq(ChampionStats::getGame, game == null ? "LOL" : game)
                        .eq(ChampionStats::getPatchCode, patch == null ? DashboardConstants.CURRENT_PATCH : patch)
                        .orderByDesc(ChampionStats::getDt)
                        .last("LIMIT 1"));
        ChampionStatsVO vo = new ChampionStatsVO();
        BeanUtils.copyProperties(c, vo);
        if (s != null) {
            vo.setGames(s.getGames());
            vo.setWins(s.getWins());
            vo.setPickRate(s.getPickRate());
            vo.setBanRate(s.getBanRate());
            vo.setWinRate(s.getWinRate());
            vo.setPatchCode(s.getPatchCode());
        }
        return vo;
    }
}
