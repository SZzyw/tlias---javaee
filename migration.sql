-- 操作日志表
CREATE TABLE IF NOT EXISTS `operate_log` (
  `id` int unsigned NOT NULL AUTO_INCREMENT COMMENT 'ID,主键',
  `operator` varchar(50) DEFAULT NULL COMMENT '操作人',
  `operation` varchar(200) DEFAULT NULL COMMENT '操作描述',
  `method` varchar(100) DEFAULT NULL COMMENT '方法名',
  `params` varchar(500) DEFAULT NULL COMMENT '参数',
  `cost_time` bigint DEFAULT NULL COMMENT '耗时(ms)',
  `ip` varchar(50) DEFAULT NULL COMMENT 'IP地址',
  `create_time` datetime DEFAULT NULL COMMENT '操作时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='操作日志表';
