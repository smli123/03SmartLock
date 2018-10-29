-- phpMyAdmin SQL Dump
-- version 3.4.7.1
-- http://www.phpmyadmin.net
--
-- 主机: localhost
-- 生成日期: 2018 年 10 月 29 日 15:57
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
-- 表的结构 `message_device`
--

CREATE TABLE IF NOT EXISTS `message_device` (
  `message_id` int(11) NOT NULL,
  `module_id` varchar(32) NOT NULL,
  `user_name` varchar(32) NOT NULL,
  `message_type` int(11) NOT NULL,
  `message_data` int(11) NOT NULL,
  `user_type` int(11) NOT NULL,
  `user_memo` varchar(32) NOT NULL
) ENGINE=MyISAM DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- 表的结构 `message_system`
--

CREATE TABLE IF NOT EXISTS `message_system` (
  `message_id` int(11) NOT NULL,
  `module_id` varchar(32) NOT NULL,
  `message_type` int(11) NOT NULL,
  `message_data` int(11) NOT NULL,
  `user_type` int(11) NOT NULL,
  `user_memo` varchar(32) NOT NULL
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
  `module_mac` varchar(24) NOT NULL,
  `module_version` varchar(32) NOT NULL,
  `module_type` varchar(32) NOT NULL,
  `module_status` tinyint(1) unsigned NOT NULL,
  `module_charge` tinyint(1) unsigned NOT NULL,
  `cookie` varchar(32) NOT NULL,
  PRIMARY KEY (`module_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- 转存表中的数据 `module_info`
--

INSERT INTO `module_info` (`module_id`, `module_name`, `module_mac`, `module_version`, `module_type`, `module_status`, `module_charge`, `cookie`) VALUES
('60:01:94:09:df:31', '651001Name', '60:01:94:09:df:31', '20171219A1V2.E', '1', 1, 82, '20180123151000'),
('651002', '651002', '60:01:94:09:df:32', '20171219A1V2.E', '1', 1, 82, '20180123151000');

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
('lishimin', 'E10ADC3949BA59ABBE56E057F20F883E', 'smli123@163.com', '0');

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

--
-- 转存表中的数据 `user_module`
--

INSERT INTO `user_module` (`user_name`, `module_id`, `ctrl_mode`) VALUES
('lishimin', '60:01:94:09:df:31', 0),
('lishimin', '651002', 1);

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
