/**
 * 
 */
package com.xxq.springmvc.cache;

import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import org.apache.ibatis.cache.Cache;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.connection.jedis.JedisConnection;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.serializer.JdkSerializationRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializer;

/**
 * @author Olympus_Pactera
 *
 */
public class RedisCache implements Cache {

	private static final Logger logger = LoggerFactory.getLogger(RedisCache.class);
	
	private static JedisConnectionFactory jedisConnectionFactory;
	
	public static void setJedisConnectionFactory(JedisConnectionFactory jedisConnectionFactory) {
		RedisCache.jedisConnectionFactory = jedisConnectionFactory;
	}

	private final String id;
	
	/**
	 * ReentrantReadWriteLock 是Lock的另一种实现方式，ReentrantLock是排他锁，同一时间只允许一个线程访问，而ReentrantReadWriteLock
	 * 允许多个线程访问，但是不允许 读 写，写 写同时访问。提高并发性
	 * The {@code ReadWriteLock}
	 */
	private final ReadWriteLock readWriteLock = new ReentrantReadWriteLock();
	
	public RedisCache(String id) {
		if(id ==null) {
			throw new IllegalArgumentException("Cache instance require an ID");
		}
		logger.debug("MybatisRedisCache:id" + id);
		this.id = id;
	}

	/* (non-Javadoc)
	 * @see org.apache.ibatis.cache.Cache#getId()
	 */
	@Override
	public String getId() {
		// TODO Auto-generated method stub
		return this.id;
	}

	/* (non-Javadoc)
	 * @see org.apache.ibatis.cache.Cache#getSize()
	 */
	@Override
	public int getSize() {
		// TODO Auto-generated method stub
		int result = 0;
		JedisConnection connection = null;
		try {
			connection = jedisConnectionFactory.getConnection();
			result = Integer.valueOf(connection.dbSize().toString());
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}finally {
			if(connection != null) {
				connection.close();
			}
		}
		return result;
	}

	/* (non-Javadoc)
	 * @see org.apache.ibatis.cache.Cache#putObject(java.lang.Object, java.lang.Object)
	 */
	@Override
	public void putObject(Object key, Object value) {
		// TODO Auto-generated method stub

		JedisConnection connection = null;
		
		try {
			connection = jedisConnectionFactory.getConnection();
			RedisSerializer<Object> serializer = new JdkSerializationRedisSerializer();
			connection.set(serializer.serialize(key), serializer.serialize(value));
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}finally {
			if(connection != null) {
				connection.close();
			}
		}
	}

	/* (non-Javadoc)
	 * @see org.apache.ibatis.cache.Cache#getObject(java.lang.Object)
	 */
	@Override
	public Object getObject(Object key) {
		// TODO Auto-generated method stub
		JedisConnection connection = null;
		Object object = null;
		try {
			connection = jedisConnectionFactory.getConnection();
			RedisSerializer<Object> redisSerializer = new JdkSerializationRedisSerializer();
			System.out.println(key);
			System.out.println("---------------------");
			System.out.println(redisSerializer.serialize(key));
			object = redisSerializer.deserialize(connection.get(redisSerializer.serialize(key)));
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}finally {
			if(connection != null) {
				connection.close();
			}
		}
		
		return object;
	}

	/* (non-Javadoc)
	 * @see org.apache.ibatis.cache.Cache#removeObject(java.lang.Object)
	 */
	@Override
	public Object removeObject(Object key) {
		// TODO Auto-generated method stub
		JedisConnection connection = null;
		Object object = null;
		try {
			connection = jedisConnectionFactory.getConnection();
			RedisSerializer<Object> redisSerializer = new JdkSerializationRedisSerializer();
			connection.expire(redisSerializer.serialize(key), 0);
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}finally {
			if(connection !=null) {
				connection.close();
			}
		}
		return object;
	}

	/* (non-Javadoc)
	 * @see org.apache.ibatis.cache.Cache#clear()
	 */
	@Override
	public void clear() {
		// TODO Auto-generated method stub
		JedisConnection connection = null;
		try {
			connection = jedisConnectionFactory.getConnection();
			//当前db的情况下，删除当前db
			connection.flushDb();
			//删除所有库
			connection.flushAll();
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}finally {
			if(connection != null) {
				connection.close();
			}
		}
	}

	/* (non-Javadoc)
	 * @see org.apache.ibatis.cache.Cache#getReadWriteLock()
	 */
	@Override
	public ReadWriteLock getReadWriteLock() {
		// TODO Auto-generated method stub
		return this.readWriteLock;
	}

}
