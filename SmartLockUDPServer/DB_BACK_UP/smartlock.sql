-- phpMyAdmin SQL Dump
-- version 3.4.7.1
-- http://www.phpmyadmin.net
--
-- 主机: localhost
-- 生成日期: 2018 年 09 月 17 日 14:47
-- 服务器版本: 5.1.69
-- PHP 版本: 5.2.17p1

SET SQL_MODE="NO_AUTO_VALUE_ON_ZERO";
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;

--
-- 数据库: `smartlock`
--

-- --------------------------------------------------------

--
-- 表的结构 `module_batterycharge`
--

CREATE TABLE IF NOT EXISTS `module_batterycharge` (
  `module_id` varchar(32) NOT NULL,
  `oper_date` datetime NOT NULL,
  `charge` double NOT NULL
) ENGINE=MyISAM DEFAULT CHARSET=utf8;

--
-- 转存表中的数据 `module_batterycharge`
--

INSERT INTO `module_batterycharge` (`module_id`, `oper_date`, `charge`) VALUES
('64792801', '2018-02-28 10:11:18', 81.9);

-- --------------------------------------------------------

--
-- 表的结构 `module_batterylocation`
--

CREATE TABLE IF NOT EXISTS `module_batterylocation` (
  `module_id` varchar(32) NOT NULL,
  `oper_date` datetime NOT NULL,
  `longitude` double NOT NULL,
  `dimension` double NOT NULL
) ENGINE=MyISAM DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- 表的结构 `module_charge`
--

CREATE TABLE IF NOT EXISTS `module_charge` (
  `module_id` varchar(32) NOT NULL,
  `oper_date` datetime NOT NULL,
  `charge` double NOT NULL
) ENGINE=MyISAM DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- 表的结构 `module_data`
--

CREATE TABLE IF NOT EXISTS `module_data` (
  `module_id` varchar(32) NOT NULL,
  `module_name` varchar(32) NOT NULL,
  `login_time` datetime NOT NULL,
  `logout_time` datetime NOT NULL,
  `online_time` int(11) unsigned NOT NULL,
  `memo` varchar(255) NOT NULL
) ENGINE=MyISAM DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- 表的结构 `module_info`
--

CREATE TABLE IF NOT EXISTS `module_info` (
  `module_id` varchar(32) NOT NULL,
  `module_name` varchar(32) NOT NULL,
  `mac` varchar(24) NOT NULL,
  `module_version` varchar(32) NOT NULL,
  `module_type` varchar(32) NOT NULL,
  `protype` tinyint(1) unsigned NOT NULL COMMENT 'protocol type',
  `power_status` tinyint(1) unsigned NOT NULL,
  `mode` smallint(1) unsigned NOT NULL,
  `red` tinyint(1) unsigned NOT NULL,
  `green` tinyint(1) unsigned NOT NULL,
  `blue` tinyint(1) unsigned NOT NULL,
  `cookie` varchar(32) NOT NULL,
  `aircon_name` varchar(32) NOT NULL,
  `tv_name` varchar(32) NOT NULL,
  PRIMARY KEY (`module_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- 转存表中的数据 `module_info`
--

INSERT INTO `module_info` (`module_id`, `module_name`, `mac`, `module_version`, `module_type`, `protype`, `power_status`, `mode`, `red`, `green`, `blue`, `cookie`, `aircon_name`, `tv_name`) VALUES
('651000', '651000', '60:01:94:09:df:32', '20171219A1V2.E', '1_3', 2, 4, 0, 255, 0, 0, '20180123151000', '', '');

-- --------------------------------------------------------

--
-- 表的结构 `module_irscene`
--

CREATE TABLE IF NOT EXISTS `module_irscene` (
  `irscene_id` int(11) NOT NULL,
  `module_id` varchar(32) NOT NULL,
  `enable` tinyint(4) NOT NULL,
  `power` tinyint(4) NOT NULL,
  `mode` tinyint(4) NOT NULL,
  `direction` tinyint(4) NOT NULL,
  `scale` tinyint(4) NOT NULL,
  `temperature` tinyint(4) NOT NULL,
  `time` varchar(32) NOT NULL,
  `period` varchar(32) NOT NULL,
  `irname` varchar(32) NOT NULL
) ENGINE=MyISAM DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- 表的结构 `module_scene`
--

CREATE TABLE IF NOT EXISTS `module_scene` (
  `scene_id` int(11) NOT NULL,
  `scene_name` varchar(32) NOT NULL,
  `power_enable` tinyint(4) NOT NULL,
  `power_moduleid` varchar(32) NOT NULL,
  `power_control` tinyint(4) NOT NULL,
  `curtain_enable` tinyint(4) NOT NULL,
  `curtain_moduleid` varchar(32) NOT NULL,
  `curtain_control` tinyint(4) NOT NULL,
  `aircon_enable` tinyint(4) NOT NULL,
  `aircon_moduleid` varchar(32) NOT NULL,
  `aircon_temperature` tinyint(4) NOT NULL,
  `aircon_control` tinyint(4) NOT NULL,
  `pc_enable` tinyint(4) NOT NULL,
  `pc_moduleid` varchar(32) NOT NULL,
  `pc_mac_moduleid` varchar(32) NOT NULL,
  `pc_mac_address` varchar(32) NOT NULL,
  `pc_control` tinyint(4) NOT NULL,
  `user_name` varchar(32) NOT NULL
) ENGINE=MyISAM DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- 表的结构 `timer_info`
--

CREATE TABLE IF NOT EXISTS `timer_info` (
  `timer_id` tinyint(1) unsigned NOT NULL AUTO_INCREMENT,
  `timer_type` tinyint(1) unsigned NOT NULL,
  `module_id` varchar(32) NOT NULL,
  `peroid` varchar(16) NOT NULL,
  `time_on` varchar(32) NOT NULL,
  `time_off` varchar(32) NOT NULL,
  `enable` tinyint(1) unsigned NOT NULL,
  PRIMARY KEY (`timer_id`,`module_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 AUTO_INCREMENT=1 ;

-- --------------------------------------------------------

--
-- 表的结构 `user_info`
--

CREATE TABLE IF NOT EXISTS `user_info` (
  `user_name` varchar(32) NOT NULL,
  `password` varchar(32) NOT NULL,
  `email` varchar(32) NOT NULL,
  `cookie` varchar(64) NOT NULL,
  PRIMARY KEY (`user_name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- 转存表中的数据 `user_info`
--

INSERT INTO `user_info` (`user_name`, `password`, `email`, `cookie`) VALUES
('smli123hz', 'E10ADC3949BA59ABBE56E057F20F883E', 'smli123@163.com', '0');

-- --------------------------------------------------------

--
-- 表的结构 `user_module`
--

CREATE TABLE IF NOT EXISTS `user_module` (
  `user_name` varchar(32) NOT NULL,
  `module_id` varchar(32) NOT NULL,
  `ctrl_mode` tinyint(1) NOT NULL,
  PRIMARY KEY (`user_name`,`module_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
