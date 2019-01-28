package lottery.web.content.utils;

import javautils.redis.JedisTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
public class SessionCounterUtil {

	@Autowired
	private JedisTemplate jedisTemplate;
	
	private static final String BOUNDED_HASH_KEY_PREFIX = "spring:session:sessions:*";
	
	public int getOnlineTotal(){
		Set<String> keys = null;
		try {
			// 这个keys应不会造成多大性能影响
			keys = jedisTemplate.keys(BOUNDED_HASH_KEY_PREFIX);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return keys != null ? keys.size() : 0;
	}
}
