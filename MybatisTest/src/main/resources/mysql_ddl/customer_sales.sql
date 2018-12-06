CREATE TABLE customer_sales (
	`id` int(10) NOT NULL AUTO_INCREMENT,
	`customer_id` int(10) NOT NULL,
	`sales_id` int(10) NOT NULL,
	`created_time` datetime NOT NULL,
	`updated_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
	PRIMARY KEY (`id`),
	UNIQUE KEY `customer_id` (`customer_id`, `sales_id`) USING BTREE,
	KEY `sales_id` (`sales_id`)
)ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;