CREATE TABLE `customer`(
	`customer_id` int(10) NOT NULL AUTO_INCREMENT,
	`customer_name` varchar(200) NOT NULL,
	`user_id` int(10) DEFAULT NULL,
	`is_valid` tinyint(4) NOT NULL DEFAULT '1',
	`created_time` datetime NOT NULL,
	`updated_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
	PRIMARY KEY (`customer_id`),
	key `customer_name` (`customer_name`) USING BTREE	
)ENGINE=Innodb AUTO_INCREMENT=1 DEFAULT CHARSET=UTF8;