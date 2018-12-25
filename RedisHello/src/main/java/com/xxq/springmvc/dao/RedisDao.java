/**
 * 
 */
package com.xxq.springmvc.dao;

import org.springframework.stereotype.Repository;

/**
 * redis操作层
 * @author xxq_1
 *
 */
@Repository
public interface RedisDao {

	/**
	 * 通过redis的key获取字符串value
	 * @param key
	 * @return value
	 */
	String get(String key);
}
