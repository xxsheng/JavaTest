create table `communicator`(
	`communicator_id` int(10) NOT NUll AUTO_INCREMENT,
	`communicator_name` varchar(64) NOT NULL,
	`phone` varchar(32) DEFAULT NULL,
	`fax` varchar(32) DEFAULT NULL,
	`email` varchar(100) DEFAULT NULL,
	`user_id` int(10) DEFAULT NULL,
	`report_to` int(10) DEFAULT '0',
	`is_valid` tinyint(4) NOT NULL DEFAULT '1',
	`create_time` datetime DEFAULT NULL,
	`update_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
	PRIMARY KEY (`communicator_id`),
	UNIQUE KEY `communicator_name` (`communicator_name`) USING BTREE
	)ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8