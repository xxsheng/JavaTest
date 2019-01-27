CREATE TABLE `message` (
	`id` varchar(32) NOT NULL COMMENT '主键',
	`full_name` varchar(128) DEFAULT NULL COMMENT '用户姓名',
	`phone_number` varchar(32) DEFAULT NULL COMMENT '电话',
	`email_address` varchar(32) DEFAULT NULL COMMENT '邮箱',
	`subject` varchar(256) DEFAULT NULL COMMENT '简要信息',
	`message` varchar(1024) DEFAULT NULL COMMENT '留言信息',
	`user_ip` varchar(128) DEFAULT NULL COMMENT '用户ip',
	`create_user` varchar(32) DEFAULT NULL COMMENT '创建者',
	`created_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
	`del_flag` int(1) DEFAULT '0' COMMENT '数据状态',
	PRIMARY KEY(`id`)
)ENGINE=Innodb DEFAULT CHARSET=UTF8
