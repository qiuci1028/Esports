-- ====================================================================
-- 种子数据：用于联调与页面展示
-- 真实部署请用 Spark 任务从 Riot API 入仓
-- ====================================================================
USE `esports`;

-- -------- tb_user（默认管理员与测试用户） --------
INSERT INTO `tb_user` (`username`, `password`, `nickname`, `role`) VALUES
('admin', '$2b$10$GJ0DHXpKmXFgBoyROugRb.2RbsKxLN3eNSRrfPGEe.VQAS.B/fUWC', '系统管理员', 'ADMIN'),
('demo',  '$2b$10$GJ0DHXpKmXFgBoyROugRb.2RbsKxLN3eNSRrfPGEe.VQAS.B/fUWC', '测试用户', 'USER');
-- 上面两个 BCrypt 哈希对应密码 "123456"

-- -------- dim_patch --------
INSERT INTO `dim_patch` (`game`, `patch_code`, `season`, `release_date`, `is_current`) VALUES
('LOL', '14.10', 'S14', '2026-05-01', 1),
('LOL', '14.09', 'S14', '2026-04-15', 0),
('VALORANT', '9.0', 'V25A3', '2026-05-01', 1),
('TFT', '14.10', 'S10', '2026-05-01', 1);

-- -------- dim_champion（LOL 部分热门英雄） --------
INSERT INTO `dim_champion` (`champion_id`, `name`, `name_cn`, `title`, `role`, `tags`) VALUES
('1',    'Annie',      '安妮',     '黑暗之女',     'mid',     'mage'),
('2',    'Olaf',       '奥拉夫',   '狂战士',       'jungle',  'fighter,tank'),
('3',    'Galio',      '加里奥',   '巨像',         'mid',     'tank,mage'),
('4',    'TwistedFate','崔斯特',   '卡牌大师',     'mid',     'mage'),
('5',    'XinZhao',    '赵信',     '德邦总管',     'jungle',  'fighter'),
('6',    'Urgot',      '厄加特',   '无畏战车',     'top',     'fighter,tank'),
('7',    'LeBlanc',    '乐芙兰',   '诡术妖姬',     'mid',     'mage,assassin'),
('8',    'Vladimir',   '弗拉基米尔','猩红收割者',  'mid',     'mage,tank'),
('9',    'Fiddlesticks','费德提克','恐惧之源',     'jungle',  'mage,support'),
('10',   'Kayle',      '凯尔',     '正义天使',     'top',     'fighter,mage'),
('11',   'MasterYi',   '无极剑圣', '无极剑圣',     'jungle',  'fighter,assassin'),
('12',   'Alistar',    '牛头酋长', '牛头酋长',     'support', 'tank,support'),
('13',   'Ryze',       '瑞兹',     '符文法师',     'mid',     'mage,tank'),
('14',   'Sion',       '赛恩',     '亡灵战神',     'top',     'tank,fighter'),
('15',   'Sivir',      '希维尔',   '战争女神',     'adc',     'marksman'),
('16',   'Soraka',     '索拉卡',   '众星之子',     'support', 'support,mage'),
('17',   'Teemo',      '提莫',     '迅捷斥候',     'top',     'marksman,assassin'),
('18',   'Tristana',   '崔斯塔娜', '麦林炮手',     'adc',     'marksman,assassin'),
('19',   'Warwick',    '沃里克',   '祖安之怒',     'jungle',  'fighter,tank'),
('20',   'Nunu',       '努努和威朗普','雪原双子',  'jungle',  'tank,mage'),
('22',   'Ashe',       '艾希',     '寒冰射手',     'adc',     'marksman,support'),
('24',   'Jax',        '贾克斯',   '武器大师',     'top',     'fighter,assassin'),
('25',   'Morgana',    '莫甘娜',   '堕落天使',     'support', 'mage,support'),
('26',   'Zilean',     '基兰',     '时光守护者',   'support', 'mage,support'),
('51',   'Caitlyn',    '凯特琳',   '皮城女警',     'adc',     'marksman'),
('55',   'Katarina',   '卡特琳娜', '不祥之刃',     'mid',     'assassin,mage'),
('64',   'LeeSin',     '李青',     '盲僧',         'jungle',  'fighter,assassin'),
('67',   'Vayne',      '薇恩',     '暗夜猎手',     'adc',     'marksman,assassin'),
('81',   'Ezreal',     '伊泽瑞尔', '探险家',       'adc',     'marksman,mage'),
('86',   'Garen',      '盖伦',     '德玛西亚之力', 'top',     'fighter,tank'),
('91',   'Talon',      '泰隆',     '刀锋之影',     'mid',     'assassin'),
('99',   'Lux',        '拉克丝',   '光辉女郎',     'support', 'mage,support'),
('103',  'Ahri',       '阿狸',     '九尾妖狐',     'mid',     'mage,assassin'),
('157',  'Yasuo',      '亚索',     '疾风剑豪',     'mid',     'fighter,assassin'),
('222',  'Jinx',       '金克丝',   '暴走萝莉',     'adc',     'marksman'),
('238',  'Zed',        '劫',       '影流之主',     'mid',     'assassin'),
('254',  'Vi',         '蔚',       '皮城执法官',   'jungle',  'fighter'),
('412',  'Thresh',     '锤石',     '魂锁典狱长',   'support', 'tank,support'),
('420',  'Illaoi',     '俄洛伊',   '海兽祭司',     'top',     'fighter,tank');

-- -------- dim_agent（VALORANT 部分特工） --------
INSERT INTO `dim_agent` (`agent_id`, `name`, `name_cn`, `role`) VALUES
('1',     'Brimstone',  '布瑞姆',   'Controller'),
('3',     'Phoenix',    '菲尼克斯', 'Duelist'),
('4',     'Sage',       '贤者',     'Sentinel'),
('6',     'Omen',       '欧门',     'Controller'),
('7',     'Brimstone',  '布瑞姆',   'Controller'),
('8',     'Killjoy',    '凯宙',     'Sentinel'),
('9',     'Cypher',     '赛弗',     'Sentinel'),
('10',    'Sova',       '索瓦',     'Initiator'),
('11',    'Jett',       '捷特',     'Duelist'),
('13',    'Deadlock',   '死锁',     'Sentinel'),
('14',    'Iso',        '壹索',     'Duelist'),
('15',    'Clove',      '丁香',     'Controller'),
('17',    'Harbor',     '哈伯',     'Controller'),
('18',    'Gekko',      '盖克',     'Initiator'),
('19',    'Deadlock',   '死锁',     'Sentinel'),
('20',    'Waylay',     '韦雷',     'Duelist'),
('21',    'Tejo',       '特霍',     'Initiator'),
('22',    'Veto',       '维托',     'Sentinel'),
('23',    'Vyse',       '维瑟',     'Sentinel'),
('25',    'Neon',       '霓虹',     'Duelist');

-- -------- dim_comp（TFT 部分阵容） --------
INSERT INTO `dim_comp` (`comp_id`, `name`, `set_version`, `traits`, `core_champs`, `tier`) VALUES
('TFT_CMP_001', '7 大羁绊斗士',  'S10',  'Brawler,Protector',  'Malphite,Blitzcrank',  'S'),
('TFT_CMP_002', '学者黑魔',      'S10',  'Scholar,Black Rose', 'Cassiopeia,Ahri',      'S'),
('TFT_CMP_003', '巫妖王冰霜',    'S10',  'Frost,Sorcerer',     'Lissandra,Ashe',       'A'),
('TFT_CMP_004', '刀锋之怒',      'S10',  'Duelist,Assassin',   'Yasuo,Master Yi',      'A'),
('TFT_CMP_005', '夜之锋刃',      'S10',  'Assassin,Scholar',   'Akali,Evelynn',        'B'),
('TFT_CMP_006', '重装战士',      'S10',  'Brawler,Heavy',      'Sion,Urgot',           'B'),
('TFT_CMP_007', '奥术法师',      'S10',  'Sorcerer,Scholar',   'Viktor,Ahri',          'S'),
('TFT_CMP_008', '射手速推',      'S10',  'Marksman,Protector', 'Jinx,Caitlyn',         'A');

-- -------- dim_player（10 个示例玩家） --------
INSERT INTO `dim_player` (`game`, `puuid`, `summoner_id`, `summoner_name`, `region`, `level`, `icon_id`, `last_active`) VALUES
('LOL',  'puuid_lol_001', 'sum_001', 'Faker',        'kr',   824, 7,   NOW()),
('LOL',  'puuid_lol_002', 'sum_002', 'Hide on bush', 'kr',   712, 6,   NOW()),
('LOL',  'puuid_lol_003', 'sum_003', 'DWG ShowMaker','kr',   689, 5,   NOW()),
('LOL',  'puuid_lol_004', 'sum_004', 'Jackeylove',   'kr',   553, 4,   NOW()),
('LOL',  'puuid_lol_005', 'sum_005', 'Knight',       'kr',   498, 3,   NOW()),
('LOL',  'puuid_lol_006', 'sum_006', 'Uzi',          'kr',   731, 28,  NOW()),
('LOL',  'puuid_lol_007', 'sum_007', 'Mlxg',         'kr',   612, 6,   NOW()),
('LOL',  'puuid_lol_008', 'sum_008', 'Ming',         'kr',   478, 9,   NOW()),
('LOL',  'puuid_lol_009', 'sum_009', 'Tian',         'kr',   522, 7,   NOW()),
('LOL',  'puuid_lol_010', 'sum_010', 'Crisp',        'kr',   491, 12,  NOW()),
('VALORANT', 'puuid_val_001', NULL, 'TenZ',          'na',  450, 1,  NOW()),
('VALORANT', 'puuid_val_002', NULL, 'Sentinels Zekken','na', 380, 1, NOW()),
('TFT',  'puuid_tft_001', NULL, 'Socks',          'na',   300, 1,  NOW()),
('TFT',  'puuid_tft_002', NULL, 'milala',         'na',   280, 1,  NOW());

-- -------- fact_ranking_snapshot（最近 30 天，每天 1 条，10 个玩家） --------
-- 这里用存储过程方式生成：每玩家 5 条数据
INSERT INTO `fact_ranking_snapshot` (`puuid`, `game`, `queue_type`, `tier`, `rank_tier`, `league_points`, `wins`, `losses`, `snapshot_date`)
SELECT
  d.puuid,
  'LOL' AS game,
  'RANKED_SOLO_5x5' AS queue_type,
  ELT(1 + FLOOR(RAND() * 8), 'IRON','BRONZE','SILVER','GOLD','PLATINUM','EMERALD','DIAMOND','MASTER') AS tier,
  ELT(1 + FLOOR(RAND() * 4), 'I','II','III','IV') AS `rank`,
  FLOOR(RAND() * 500) + 50 AS lp,
  FLOOR(RAND() * 80) + 20 AS wins,
  FLOOR(RAND() * 80) + 20 AS losses,
  DATE_SUB(CURDATE(), INTERVAL seq DAY) AS snapshot_date
FROM (
  SELECT 1 AS seq UNION SELECT 2 UNION SELECT 3 UNION SELECT 4 UNION SELECT 5
      UNION SELECT 6 UNION SELECT 7 UNION SELECT 8 UNION SELECT 9 UNION SELECT 10
) seqs
CROSS JOIN (
  SELECT 'puuid_lol_001' puuid UNION SELECT 'puuid_lol_002' UNION SELECT 'puuid_lol_003'
  UNION SELECT 'puuid_lol_004' UNION SELECT 'puuid_lol_005' UNION SELECT 'puuid_lol_006'
  UNION SELECT 'puuid_lol_007' UNION SELECT 'puuid_lol_008' UNION SELECT 'puuid_lol_009'
  UNION SELECT 'puuid_lol_010'
) d;

-- -------- fact_match（20 场示例比赛） --------
INSERT INTO `fact_match` (`match_id`, `game`, `game_mode`, `map_name`, `patch_code`, `duration_sec`, `creation_time`, `winning_team`) VALUES
('KR_1001','LOL','CLASSIC','Summoners Rift','14.10',1820,NOW(),'BLUE'),
('KR_1002','LOL','CLASSIC','Summoners Rift','14.10',2050,NOW(),'RED'),
('KR_1003','LOL','CLASSIC','Summoners Rift','14.10',1730,NOW(),'BLUE'),
('KR_1004','LOL','CLASSIC','Summoners Rift','14.10',1980,NOW(),'BLUE'),
('KR_1005','LOL','CLASSIC','Summoners Rift','14.10',2310,NOW(),'RED'),
('KR_1006','LOL','CLASSIC','Summoners Rift','14.10',1640,NOW(),'BLUE'),
('KR_1007','LOL','CLASSIC','Summoners Rift','14.10',2150,NOW(),'RED'),
('KR_1008','LOL','CLASSIC','Summoners Rift','14.10',1880,NOW(),'BLUE'),
('KR_1009','LOL','CLASSIC','Summoners Rift','14.10',2010,NOW(),'BLUE'),
('KR_1010','LOL','CLASSIC','Summoners Rift','14.10',1740,NOW(),'RED'),
('KR_1011','LOL','RANKED','Summoners Rift','14.10',2240,NOW(),'BLUE'),
('KR_1012','LOL','RANKED','Summoners Rift','14.10',1990,NOW(),'BLUE'),
('KR_1013','LOL','RANKED','Summoners Rift','14.10',1830,NOW(),'RED'),
('KR_1014','LOL','RANKED','Summoners Rift','14.10',2170,NOW(),'BLUE'),
('KR_1015','LOL','RANKED','Summoners Rift','14.10',1910,NOW(),'BLUE'),
('VAL_2001','VALORANT','COMPETITIVE','Bind','9.0',1820,NOW(),'BLUE'),
('VAL_2002','VALORANT','COMPETITIVE','Haven','9.0',2150,NOW(),'RED'),
('VAL_2003','VALORANT','COMPETITIVE','Ascent','9.0',1990,NOW(),'BLUE'),
('VAL_2004','VALORANT','COMPETITIVE','Split','9.0',2380,NOW(),'BLUE'),
('VAL_2005','VALORANT','COMPETITIVE','Lotus','9.0',2010,NOW(),'RED');

-- -------- fact_match_player（每场比赛 10 名玩家） --------
-- 简化：用存储过程生成，每场比赛随机 10 名玩家、随机英雄、随机 KDA
DROP PROCEDURE IF EXISTS seed_match_players;
DELIMITER //
CREATE PROCEDURE seed_match_players()
BEGIN
  DECLARE i INT DEFAULT 0;
  DECLARE v_match VARCHAR(64);
  DECLARE v_side VARCHAR(16);
  DECLARE v_champ VARCHAR(32);
  DECLARE v_kills INT;
  DECLARE v_deaths INT;
  DECLARE v_assists INT;
  DECLARE v_win TINYINT;

  DECLARE done INT DEFAULT 0;
  DECLARE cur CURSOR FOR SELECT match_id, winning_team FROM fact_match;
  DECLARE CONTINUE HANDLER FOR NOT FOUND SET done = 1;

  OPEN cur;
  read_loop: LOOP
    FETCH cur INTO v_match, v_side;
    IF done THEN LEAVE read_loop; END IF;

    -- 蓝方 5 人
    SET i = 0;
    WHILE i < 5 DO
      SET v_champ = ELT(1 + FLOOR(RAND() * 40),
        '1','2','3','4','5','6','7','8','9','10',
        '11','12','13','14','15','16','17','18','19','20',
        '22','24','25','26','51','55','64','67','81','86',
        '91','99','103','157','222','238','254','412','420','157');
      SET v_kills   = FLOOR(RAND() * 12);
      SET v_deaths  = FLOOR(RAND() * 8);
      SET v_assists = FLOOR(RAND() * 15);
      SET v_win     = IF(v_side='BLUE', 1, 0);
      INSERT INTO fact_match_player (match_id, puuid, team_side, champion_id, role_position, kills, deaths, assists, gold_earned, damage_dealt, vision_score, win)
      VALUES (v_match, CONCAT('puuid_lol_00', 1 + FLOOR(RAND()*10)), 'BLUE', v_champ,
              ELT(1+FLOOR(RAND()*5),'top','jungle','mid','adc','support'),
              v_kills, v_deaths, v_assists, 8000+FLOOR(RAND()*8000), 10000+FLOOR(RAND()*20000), FLOOR(RAND()*50), v_win);
      SET i = i + 1;
    END WHILE;

    -- 红方 5 人
    SET i = 0;
    WHILE i < 5 DO
      SET v_champ = ELT(1 + FLOOR(RAND() * 40),
        '1','2','3','4','5','6','7','8','9','10',
        '11','12','13','14','15','16','17','18','19','20',
        '22','24','25','26','51','55','64','67','81','86',
        '91','99','103','157','222','238','254','412','420','157');
      SET v_kills   = FLOOR(RAND() * 12);
      SET v_deaths  = FLOOR(RAND() * 8);
      SET v_assists = FLOOR(RAND() * 15);
      SET v_win     = IF(v_side='RED', 1, 0);
      INSERT INTO fact_match_player (match_id, puuid, team_side, champion_id, role_position, kills, deaths, assists, gold_earned, damage_dealt, vision_score, win)
      VALUES (v_match, CONCAT('puuid_lol_00', 1 + FLOOR(RAND()*10)), 'RED', v_champ,
              ELT(1+FLOOR(RAND()*5),'top','jungle','mid','adc','support'),
              v_kills, v_deaths, v_assists, 8000+FLOOR(RAND()*8000), 10000+FLOOR(RAND()*20000), FLOOR(RAND()*50), v_win);
      SET i = i + 1;
    END WHILE;
  END LOOP;
  CLOSE cur;
END //
DELIMITER ;

CALL seed_match_players();
DROP PROCEDURE seed_match_players;

-- -------- ads_champion_stats_d（最近 7 天英雄统计） --------
INSERT INTO `ads_champion_stats_d` (`dt`, `game`, `patch_code`, `role`, `champion_id`, `games`, `wins`, `pick_rate`, `ban_rate`, `win_rate`)
SELECT
  DATE_SUB(CURDATE(), INTERVAL FLOOR(RAND()*7) DAY) AS dt,
  'LOL' AS game,
  '14.10' AS patch_code,
  ELT(1+FLOOR(RAND()*5),'top','jungle','mid','adc','support') AS role,
  c.champion_id,
  50 + FLOOR(RAND() * 200) AS games,
  FLOOR(RAND() * 150) AS wins,
  ROUND(RAND() * 0.35, 4) AS pick_rate,
  ROUND(RAND() * 0.20, 4) AS ban_rate,
  ROUND(0.40 + RAND() * 0.20, 4) AS win_rate
FROM dim_champion c
ORDER BY c.id
LIMIT 100;

-- -------- ads_player_profile（玩家画像） --------
INSERT INTO `ads_player_profile` (`puuid`, `game`, `total_games`, `total_wins`, `win_rate`, `avg_kda`, `main_role`, `hero_pool_size`, `active_hour`, `last_update`)
SELECT
  d.puuid, 'LOL' AS game,
  200 + FLOOR(RAND() * 500) AS total_games,
  100 + FLOOR(RAND() * 250) AS total_wins,
  ROUND(0.45 + RAND() * 0.20, 4) AS win_rate,
  ROUND(1.5 + RAND() * 3, 2) AS avg_kda,
  ELT(1+FLOOR(RAND()*5),'top','jungle','mid','adc','support') AS main_role,
  10 + FLOOR(RAND() * 40) AS hero_pool_size,
  FLOOR(RAND() * 24) AS active_hour,
  NOW() AS last_update
FROM dim_player d WHERE d.game = 'LOL';
