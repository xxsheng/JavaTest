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
	
	/**
	 * 通过redis存入键值对
	 * @param key
	 * @param value
	 * @return
	 */
	long setnx(String key, String value);
	
	/**
	 * 存入byte数组键值对
	 * @param key
	 * @param value
	 * @return
	 */
	String setKeyValue(byte[] key, byte[] value);
	
	/**
	 * 通过key取得redis中的value
	 * @param key
	 * @return
	 */
	byte[] getByteValue(byte[] key);
}
