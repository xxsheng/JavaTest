set foreign_key_checks = 0;

drop table if exists `user`;

CREATE TABLE `user` (
                      `id` bigint(11) NOT NULL AUTO_INCREMENT COMMENT '用户id',
                      `phone` varchar(11) NOT NULL,
                      `name` varchar(255) DEFAULT NULL,
                      `sex` int(1) DEFAULT '0',
                      `register_time` bigint(13) DEFAULT NULL,
                      `password` varchar(255) NOT NULL,
                      PRIMARY KEY (`id`) USING BTREE,
                      UNIQUE KEY `user_i_phone` (`phone`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=13 DEFAULT CHARSET=utf8;

drop table if exists `article`;

CREATE TABLE `article` (
                         `id` bigint(11) NOT NULL AUTO_INCREMENT COMMENT '文章id',
                         `title` varchar(255) DEFAULT NULL,
                         `content` text,
                         `user_id` bigint(11) DEFAULT NULL,
                         `release_time` int(13) NOT NULL,
                         PRIMARY KEY (`id`),
                         KEY `fk_article_user` (`user_id`),
                         CONSTRAINT `fk_article_user` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

drop table if exists `comment`;

CREATE TABLE `comment` (
                         `id` bigint(11) NOT NULL AUTO_INCREMENT,
                         `content` text,
                         `article_id` bigint(11) DEFAULT NULL,
                         `user_id` bigint(11) DEFAULT NULL,
                         `release_time` int(13) NOT NULL,
                         PRIMARY KEY (`id`),
                         KEY `fk_comment_article` (`article_id`),
                         KEY `fk_comment_user` (`user_id`),
                         CONSTRAINT `fk_comment_article` FOREIGN KEY (`article_id`) REFERENCES `article` (`id`),
                         CONSTRAINT `fk_comment_user` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;