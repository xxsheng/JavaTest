一、mysql自增列充值方法
ALTER TABLE cs MODIFY COLUMN id int(11) NOT NULL FIRST,DROP PRIMARY KEY;【将表里的id列，取消自增，取消主键】
ALTER TABLE cs ADD COLUMN id2 int NOT NULL AUTO_INCREMENT FIRST ,ADD PRIMARY KEY (id2);【新增id2列，自增，主键。名字可以随意，别重复。】
ALTER TABLE cs DROP COLUMN id;【删除id列】
ALTER TABLE cs CHANGE COLUMN id2 id int(11) NOT NULL AUTO_INCREMENT FIRST ;【把id2改为id1】

二、mybatis简单示例学习链接
https://blog.csdn.net/chris_mao/article/details/48805243

三、mybatis映射文件
https://blog.csdn.net/chris_mao/article/details/48811507