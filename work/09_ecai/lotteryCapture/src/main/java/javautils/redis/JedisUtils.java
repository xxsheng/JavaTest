/*******************************************************************************
 * Copyright (c) 2005, 2014 springside.github.io
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 *******************************************************************************/
package javautils.redis;

import javautils.redis.pool.JedisPool;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.exceptions.JedisException;

public class JedisUtils {

	private static final String OK_CODE = "OK";
	private static final String OK_MULTI_CODE = "+OK";

	/**
	 * 判断 返回值是否ok.
	 */
	public static boolean isStatusOk(String status) {
		return (status != null) && (OK_CODE.equals(status) || OK_MULTI_CODE.equals(status));
	}

	/**
	 * 在Pool以外强行销毁Jedis.
	 */
	public static void destroyJedis(Jedis jedis) {
		if ((jedis != null) && jedis.isConnected()) {
			try {
				try {
					jedis.quit();
				} catch (Exception e) {
				}
				jedis.disconnect();
			} catch (Exception e) {
			}
		}
	}

	/**
	 * Ping the jedis instance, return true is the result is PONG.
	 */
	public static boolean ping(JedisPool pool) {
		JedisTemplate template = new JedisTemplate(pool);
		try {
			String result = template.execute(new JedisTemplate.JedisAction<String>() {
				public String action(Jedis jedis) {
					return jedis.ping();
				}
			});
			return (result != null) && result.equals("PONG");
		} catch (JedisException e) {
			return false;
		}
	}
}
