create table `sys_user`(
	`user_id` int(10) unsigned NOT NUll AUTO_INCREMENT,
	`user_name` varchar(64) NOT NULL,
	`user_password` varchar(32) NOT NULL,
	`nick_name` varchar(64) NOT NULL,
	`email` varchar(128) DEFAULT NULL,
	`user_type_id` smallint(4) NOT NULL,
	`is_valid` tinyint(1) NOT NULL DEFAULT '1',
	`create_time` datetime NOT NULL,
	`update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
	`last_login_time` datetime DEFAULT NULL,
	`online` tinyint(4) NOT NULL DEFAULT '0',
	`language` varchar(6) NOT NULL DEFAULT 'zh-cn',
	`psd_changed_date` datetime DEFAULT NULL,
	`mphone` varchar(12) DEFAULT NULL,
	PRIMARY KEY (`user_id`),
	UNIQUE KEY `user_name` (`user_name`) USING BTREE
)ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8