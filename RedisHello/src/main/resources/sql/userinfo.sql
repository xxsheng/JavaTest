SET FOREIGN_KEY_CHECKS=0

DROP TABLE IF EXISTS `userinfo`;
CREATE TABLE `userinfo`(
	`id` int(11) NOT NULL AUTO_INCREMENT,
	`name` varchar(20) NOT NULL,
	`age` int(5) DEFAULT NULL,
	`sex` int(1) DEFAULT NULL,
	`address` varchar(255) DEFAULT NULL,
	PRIMARY KEY (`id`)
)ENGINE=Innodb AUTO_INCREMENT=5 DEFAULT CHARSET=UTF8;

INSERT INTO userinfo(id,name,age,sex,address) 
values
('1', '张三', '10', '0', '北京'),
('2', '李四', '20', '1', '上海'),
('3', '赵武', '25', '1', '广州'),
('4', '前六', '30', '0', '台湾');
