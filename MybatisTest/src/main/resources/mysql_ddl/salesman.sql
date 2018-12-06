CREATE TABLE `salesman` (
	`sales_id` int(10) NOT NULL AUTO_INCREMENT,
	`sales_name` varchar(64) NOT NULL,
	`sales_phone` varchar(32) DEFAULT NULL,
	`sales_fax` varchar(32) DEFAULT NULL,
	`sales_email` varchar(100) DEFAULT NULL,
	`user_id` int(10) DEFAULT NULL,
	`report_to` int(10) DEFAULT '0',
	`is_valid` tinyint(4) NOT NULL DEFAULT '1',
	`created_time` datetime DEFAULT NULL,
	`updated_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
	PRIMARY KEY (`sales_id`),
	KEY `sales_name` (`sales_name`)
)ENGINE=Innodb AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;