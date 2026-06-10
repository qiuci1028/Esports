package com.esports.bigdata.module.match.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.esports.bigdata.common.ResultCode;
import com.esports.bigdata.common.exception.BusinessException;
import com.esports.bigdata.common.page.PageResult;
import com.esports.bigdata.module.champion.entity.Champion;
import com.esports.bigdata.module.champion.mapper.ChampionMapper;
import com.esports.bigdata.module.match.dto.MatchDetailDTO;
import com.esports.bigdata.module.match.dto.MatchListVO;
import com.esports.bigdata.module.match.entity.Match;
import com.esports.bigdata.module.match.entity.MatchPlayer;
import com.esports.bigdata.module.match.mapper.MatchMapper;
import com.esports.bigdata.module.match.mapper.MatchPlayerMapper;
import com.esports.bigdata.module.player.entity.Player;
import com.esports.bigdata.module.player.mapper.PlayerMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MatchService {

    private final MatchMapper matchMapper;
    private final MatchPlayerMapper matchPlayerMapper;
    private final ChampionMapper championMapper;
    private final PlayerMapper playerMapper;

    public PageResult<MatchListVO> page(String game, String gameMode, String patch, Long pageNum, Long pageSize) {
        LambdaQueryWrapper<Match> w = new LambdaQueryWrapper<>();
        if (game != null && !game.isBlank()) w.eq(Match::getGame, game);
        if (gameMode != null && !gameMode.isBlank()) w.eq(Match::getGameMode, gameMode);
        if (patch != null && !patch.isBlank()) w.eq(Match::getPatchCode, patch);
        w.orderByDesc(Match::getCreationTime);

        IPage<Match> page = matchMapper.selectPage(new Page<>(pageNum, pageSize), w);
        List<MatchListVO> list = page.getRecords().stream().map(m -> {
            MatchListVO vo = new MatchListVO();
            BeanUtils.copyProperties(m, vo);
            vo.setCreationTime(m.getCreationTime() == null ? null : m.getCreationTime().toString());
            return vo;
        }).toList();
        return PageResult.of(page, list);
    }

    public MatchDetailDTO detail(String matchId) {
        Match m = matchMapper.selectOne(
                new LambdaQueryWrapper<Match>().eq(Match::getMatchId, matchId).last("LIMIT 1"));
        if (m == null) throw new BusinessException(ResultCode.DATA_NOT_FOUND, "比赛 " + matchId);

        List<MatchPlayer> players = matchPlayerMapper.selectList(
                new LambdaQueryWrapper<MatchPlayer>().eq(MatchPlayer::getMatchId, matchId));
        if (players.isEmpty()) {
            throw new BusinessException(ResultCode.DATA_NOT_FOUND, "比赛 " + matchId + " 玩家数据");
        }

        // 元数据关联
        Set<String> champIds = players.stream()
                .map(MatchPlayer::getChampionId).filter(Objects::nonNull).collect(Collectors.toSet());
        Set<String> puuids = players.stream()
                .map(MatchPlayer::getPuuid).filter(Objects::nonNull).collect(Collectors.toSet());
        Map<String, Champion> champMap = champIds.isEmpty() ? Map.of() :
                championMapper.selectBatchIds(champIds).stream()
                        .collect(Collectors.toMap(Champion::getChampionId, c -> c));
        Map<String, Player> playerMap = puuids.isEmpty() ? Map.of() :
                playerMapper.selectList(
                        new LambdaQueryWrapper<Player>().in(Player::getPuuid, puuids))
                        .stream().collect(Collectors.toMap(Player::getPuuid, p -> p));

        MatchDetailDTO dto = new MatchDetailDTO();
        MatchDetailDTO.MatchBasicVO basic = new MatchDetailDTO.MatchBasicVO();
        BeanUtils.copyProperties(m, basic);
        basic.setCreationTime(m.getCreationTime() == null ? null : m.getCreationTime().toString());
        dto.setBasic(basic);

        // 队员分组
        List<MatchDetailDTO.TeamMemberVO> blue = new ArrayList<>();
        List<MatchDetailDTO.TeamMemberVO> red = new ArrayList<>();
        for (MatchPlayer p : players) {
            MatchDetailDTO.TeamMemberVO vo = new MatchDetailDTO.TeamMemberVO();
            BeanUtils.copyProperties(p, vo);
            Champion c = champMap.get(p.getChampionId());
            if (c != null) {
                vo.setChampionName(c.getNameCn() == null ? c.getName() : c.getNameCn());
                vo.setChampionIcon(c.getIconUrl());
            }
            Player pl = playerMap.get(p.getPuuid());
            if (pl != null) vo.setSummonerName(pl.getSummonerName());
            if ("BLUE".equalsIgnoreCase(p.getTeamSide())) blue.add(vo);
            else red.add(vo);
        }
        dto.setBlueTeam(blue);
        dto.setRedTeam(red);
        dto.setBlueSummary(summarize(blue));
        dto.setRedSummary(summarize(red));
        return dto;
    }

    private MatchDetailDTO.TeamSummaryVO summarize(List<MatchDetailDTO.TeamMemberVO> team) {
        MatchDetailDTO.TeamSummaryVO s = new MatchDetailDTO.TeamSummaryVO();
        s.setTotalKills(team.stream().mapToInt(MatchDetailDTO.TeamMemberVO::getKills).sum());
        s.setTotalDeaths(team.stream().mapToInt(MatchDetailDTO.TeamMemberVO::getDeaths).sum());
        s.setTotalAssists(team.stream().mapToInt(MatchDetailDTO.TeamMemberVO::getAssists).sum());
        s.setTotalGold(team.stream().mapToInt(MatchDetailDTO.TeamMemberVO::getGoldEarned).sum());
        s.setTotalDamage(team.stream().mapToInt(MatchDetailDTO.TeamMemberVO::getDamageDealt).sum());
        s.setTotalVision(team.stream().mapToInt(MatchDetailDTO.TeamMemberVO::getVisionScore).sum());
        return s;
    }
}
