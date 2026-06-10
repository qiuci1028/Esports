-- ====================================================================
-- Riot 电竞大数据分析平台 - 数据库 DDL
-- MySQL 8.0+ | Database: esports
-- 字符集: utf8mb4
-- ====================================================================

CREATE DATABASE IF NOT EXISTS `esports` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE `esports`;

-- --------------------------------------------------------------------
-- 0. 用户与订阅（小程序/三端鉴权用）
-- --------------------------------------------------------------------
DROP TABLE IF EXISTS `tb_user`;
CREATE TABLE `tb_user` (
  `id`          BIGINT       NOT NULL AUTO_INCREMENT COMMENT '主键',
  `username`    VARCHAR(64)  NOT NULL COMMENT '用户名',
  `password`    VARCHAR(128) NOT NULL COMMENT 'BCrypt 密码',
  `nickname`    VARCHAR(64)           DEFAULT NULL COMMENT '昵称',
  `email`       VARCHAR(128)          DEFAULT NULL COMMENT '邮箱',
  `phone`       VARCHAR(32)           DEFAULT NULL COMMENT '手机号',
  `avatar`      VARCHAR(255)          DEFAULT NULL COMMENT '头像 URL',
  `role`        VARCHAR(32)  NOT NULL DEFAULT 'USER' COMMENT '角色：USER/ADMIN',
  `status`      TINYINT      NOT NULL DEFAULT 1 COMMENT '状态：1=正常 0=禁用',
  `create_time` DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `deleted`     TINYINT      NOT NULL DEFAULT 0,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_username` (`username`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';

DROP TABLE IF EXISTS `tb_subscribe`;
CREATE TABLE `tb_subscribe` (
  `id`              BIGINT       NOT NULL AUTO_INCREMENT,
  `user_id`         BIGINT       NOT NULL COMMENT '订阅人',
  `game`            VARCHAR(16)  NOT NULL COMMENT '游戏：LOL/VALORANT/TFT',
  `target_puuid`    VARCHAR(128) NOT NULL COMMENT '目标玩家 PUUID',
  `target_name`     VARCHAR(128)          DEFAULT NULL,
  `notify_type`     VARCHAR(32)  NOT NULL DEFAULT 'WECHAT' COMMENT '通知方式',
  `openid`          VARCHAR(128)          DEFAULT NULL COMMENT '微信 openid',
  `status`          TINYINT      NOT NULL DEFAULT 1 COMMENT '1=生效 0=取消',
  `create_time`     DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time`     DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `deleted`         TINYINT      NOT NULL DEFAULT 0,
  PRIMARY KEY (`id`),
  KEY `idx_user` (`user_id`),
  KEY `idx_target` (`target_puuid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='玩家订阅表（小程序推送）';

-- --------------------------------------------------------------------
-- 1. 维度表
-- --------------------------------------------------------------------
DROP TABLE IF EXISTS `dim_patch`;
CREATE TABLE `dim_patch` (
  `id`           BIGINT       NOT NULL AUTO_INCREMENT,
  `game`         VARCHAR(16)  NOT NULL COMMENT '游戏',
  `patch_code`   VARCHAR(32)  NOT NULL COMMENT '版本号，如 14.10',
  `season`       VARCHAR(32)           DEFAULT NULL,
  `release_date` DATE                  DEFAULT NULL,
  `is_current`   TINYINT      NOT NULL DEFAULT 0,
  `create_time`  DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_game_patch` (`game`, `patch_code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='游戏版本维度';

DROP TABLE IF EXISTS `dim_player`;
CREATE TABLE `dim_player` (
  `id`             BIGINT       NOT NULL AUTO_INCREMENT,
  `game`           VARCHAR(16)  NOT NULL COMMENT '游戏：LOL/VALORANT/TFT',
  `puuid`          VARCHAR(128) NOT NULL COMMENT 'Riot PUUID',
  `summoner_id`    VARCHAR(128)          DEFAULT NULL,
  `summoner_name`  VARCHAR(128) NOT NULL,
  `region`         VARCHAR(16)  NOT NULL COMMENT '区域：kr/na1/euw1...',
  `level`          INT                   DEFAULT NULL,
  `icon_id`        INT                   DEFAULT NULL COMMENT '头像 ID',
  `profile_icon_url` VARCHAR(255)         DEFAULT NULL,
  `last_active`    DATETIME              DEFAULT NULL,
  `create_time`    DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time`    DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `deleted`        TINYINT      NOT NULL DEFAULT 0,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_puuid` (`puuid`),
  KEY `idx_name` (`summoner_name`),
  KEY `idx_game_region` (`game`, `region`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='玩家维度表';

DROP TABLE IF EXISTS `dim_champion`;
CREATE TABLE `dim_champion` (
  `id`            BIGINT       NOT NULL AUTO_INCREMENT,
  `champion_id`   VARCHAR(32)  NOT NULL COMMENT 'Riot 英雄 ID',
  `name`          VARCHAR(64)  NOT NULL,
  `name_cn`       VARCHAR(64)           DEFAULT NULL COMMENT '中文名',
  `title`         VARCHAR(128)          DEFAULT NULL,
  `role`          VARCHAR(32)           DEFAULT NULL COMMENT 'top/jungle/mid/adc/support',
  `tags`          VARCHAR(128)          DEFAULT NULL,
  `icon_url`      VARCHAR(255)          DEFAULT NULL,
  `splash_url`    VARCHAR(255)          DEFAULT NULL,
  `release_date`  DATE                  DEFAULT NULL,
  `create_time`   DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time`   DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `deleted`       TINYINT      NOT NULL DEFAULT 0,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_champion_id` (`champion_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='LOL 英雄维度表';

DROP TABLE IF EXISTS `dim_agent`;
CREATE TABLE `dim_agent` (
  `id`            BIGINT       NOT NULL AUTO_INCREMENT,
  `agent_id`      VARCHAR(32)  NOT NULL,
  `name`          VARCHAR(64)  NOT NULL,
  `name_cn`       VARCHAR(64)           DEFAULT NULL,
  `role`          VARCHAR(32)           DEFAULT NULL,
  `icon_url`      VARCHAR(255)          DEFAULT NULL,
  `create_time`   DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time`   DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `deleted`       TINYINT      NOT NULL DEFAULT 0,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_agent_id` (`agent_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='VALORANT 特工维度表';

DROP TABLE IF EXISTS `dim_comp`;
CREATE TABLE `dim_comp` (
  `id`           BIGINT       NOT NULL AUTO_INCREMENT,
  `comp_id`      VARCHAR(32)  NOT NULL,
  `name`         VARCHAR(128) NOT NULL COMMENT '阵容名',
  `set_version`  VARCHAR(32)           DEFAULT NULL COMMENT 'TFT 赛季',
  `traits`       VARCHAR(255)          DEFAULT NULL COMMENT '羁绊组合，逗号分隔',
  `core_champs`  VARCHAR(255)          DEFAULT NULL COMMENT '核心棋子',
  `tier`         VARCHAR(8)            DEFAULT NULL COMMENT 'S/A/B/C 评级',
  `create_time`  DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time`  DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `deleted`      TINYINT      NOT NULL DEFAULT 0,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_comp_id` (`comp_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='TFT 阵容维度表';

-- --------------------------------------------------------------------
-- 2. 事实表
-- --------------------------------------------------------------------
DROP TABLE IF EXISTS `fact_match`;
CREATE TABLE `fact_match` (
  `id`             BIGINT       NOT NULL AUTO_INCREMENT,
  `match_id`       VARCHAR(64)  NOT NULL,
  `game`           VARCHAR(16)  NOT NULL,
  `game_mode`      VARCHAR(32)           DEFAULT NULL COMMENT '经典/排位/ARAM...',
  `map_name`       VARCHAR(64)           DEFAULT NULL,
  `patch_code`     VARCHAR(32)           DEFAULT NULL,
  `duration_sec`   INT                   DEFAULT NULL,
  `creation_ts`    BIGINT                DEFAULT NULL COMMENT 'Riot 时间戳 ms',
  `creation_time`  DATETIME              DEFAULT NULL,
  `winning_team`   VARCHAR(16)           DEFAULT NULL COMMENT 'BLUE/RED/NONE',
  `create_time`    DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `deleted`        TINYINT      NOT NULL DEFAULT 0,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_match_id` (`match_id`),
  KEY `idx_game_creation` (`game`, `creation_time`),
  KEY `idx_patch` (`patch_code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='比赛事实表';

DROP TABLE IF EXISTS `fact_match_player`;
CREATE TABLE `fact_match_player` (
  `id`             BIGINT       NOT NULL AUTO_INCREMENT,
  `match_id`       VARCHAR(64)  NOT NULL,
  `player_id`      BIGINT                DEFAULT NULL COMMENT '关联 dim_player.id',
  `puuid`          VARCHAR(128)          DEFAULT NULL,
  `team_side`      VARCHAR(16)           DEFAULT NULL COMMENT 'BLUE/RED',
  `champion_id`    VARCHAR(32)           DEFAULT NULL,
  `agent_id`       VARCHAR(32)           DEFAULT NULL,
  `role_position`  VARCHAR(32)           DEFAULT NULL,
  `kills`          INT                   DEFAULT 0,
  `deaths`         INT                   DEFAULT 0,
  `assists`        INT                   DEFAULT 0,
  `gold_earned`    INT                   DEFAULT 0,
  `damage_dealt`   INT                   DEFAULT 0,
  `vision_score`   INT                   DEFAULT 0,
  `win`            TINYINT               DEFAULT 0,
  `create_time`    DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idx_match` (`match_id`),
  KEY `idx_puuid` (`puuid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='比赛-玩家事实表';

DROP TABLE IF EXISTS `fact_ranking_snapshot`;
CREATE TABLE `fact_ranking_snapshot` (
  `id`              BIGINT       NOT NULL AUTO_INCREMENT,
  `player_id`       BIGINT                DEFAULT NULL,
  `puuid`           VARCHAR(128) NOT NULL,
  `game`            VARCHAR(16)  NOT NULL,
  `queue_type`      VARCHAR(32)  NOT NULL DEFAULT 'RANKED_SOLO_5x5',
  `tier`            VARCHAR(16)           DEFAULT NULL COMMENT 'IRON/BRONZE/SILVER/GOLD/PLATINUM/EMERALD/DIAMOND/MASTER/GRANDMASTER/CHALLENGER',
  `rank_tier`       VARCHAR(8)            DEFAULT NULL COMMENT 'I/II/III/IV',
  `league_points`   INT                   DEFAULT 0,
  `wins`            INT                   DEFAULT 0,
  `losses`          INT                   DEFAULT 0,
  `snapshot_date`   DATE         NOT NULL,
  `create_time`     DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idx_puuid_date` (`puuid`, `snapshot_date`),
  KEY `idx_tier` (`tier`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='玩家段位快照';

-- --------------------------------------------------------------------
-- 3. 应用层宽表
-- --------------------------------------------------------------------
DROP TABLE IF EXISTS `ads_champion_stats_d`;
CREATE TABLE `ads_champion_stats_d` (
  `id`           BIGINT       NOT NULL AUTO_INCREMENT,
  `dt`           DATE         NOT NULL,
  `game`         VARCHAR(16)  NOT NULL,
  `patch_code`   VARCHAR(32)           DEFAULT NULL,
  `role`         VARCHAR(32)           DEFAULT NULL,
  `champion_id`  VARCHAR(32)           DEFAULT NULL,
  `games`        INT                   DEFAULT 0,
  `wins`         INT                   DEFAULT 0,
  `pick_rate`    DECIMAL(6,4)          DEFAULT 0,
  `ban_rate`     DECIMAL(6,4)          DEFAULT 0,
  `win_rate`     DECIMAL(6,4)          DEFAULT 0,
  `create_time`  DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_dt_champ` (`dt`, `game`, `champion_id`),
  KEY `idx_patch` (`patch_code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='英雄每日统计宽表';

DROP TABLE IF EXISTS `ads_player_profile`;
CREATE TABLE `ads_player_profile` (
  `id`              BIGINT       NOT NULL AUTO_INCREMENT,
  `player_id`       BIGINT                DEFAULT NULL,
  `puuid`           VARCHAR(128) NOT NULL,
  `game`            VARCHAR(16)  NOT NULL,
  `total_games`     INT                   DEFAULT 0,
  `total_wins`      INT                   DEFAULT 0,
  `win_rate`        DECIMAL(6,4)          DEFAULT 0,
  `avg_kda`         DECIMAL(6,2)          DEFAULT 0,
  `main_role`       VARCHAR(32)           DEFAULT NULL,
  `hero_pool_size`  INT                   DEFAULT 0,
  `active_hour`     INT                   DEFAULT NULL COMMENT '最活跃小时',
  `last_update`     DATETIME              DEFAULT NULL,
  `create_time`     DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_puuid_game` (`puuid`, `game`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='玩家画像宽表';
