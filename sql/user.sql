

CREATE TABLE `user` (
  `id` bigint(20) NOT NULL COMMENT 'id',
  `name` varchar(64) CHARACTER SET utf8mb4 NOT NULL DEFAULT '' COMMENT '姓名',
  `sex` varchar(16) CHARACTER SET utf8mb4 DEFAULT NULL COMMENT '性别',
  `age` int(255) DEFAULT NULL COMMENT '年龄',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `update_time` datetime NOT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `status` int(10) NOT NULL DEFAULT '0' COMMENT '是否删除 1删除 0未删除',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='用户表';


INSERT INTO `user` (`id`, `name`, `sex`, `age`, `create_time`, `update_time`, `status`)
VALUES
	(36663930135646208,'小小','女',3,'2019-08-20 00:05:51','2019-08-20 00:05:51',1),
	(36664096632737792,'奶奶','女',66,'2019-08-20 00:06:28','2019-08-20 00:06:28',1),
	(36664096632737793,'妈妈','女',33,'2019-08-20 00:06:28','2019-08-20 00:06:28',1),
	(36664096632737794,'小小','女',3,'2019-08-20 00:06:28','2019-08-20 00:06:28',1),
	(36664096632737795,'爷爷','男',66,'2019-08-20 00:06:28','2019-08-20 00:06:28',1),
	(36664096632737796,'爸爸','男',33,'2019-08-20 00:06:28','2019-08-20 00:06:28',1);

