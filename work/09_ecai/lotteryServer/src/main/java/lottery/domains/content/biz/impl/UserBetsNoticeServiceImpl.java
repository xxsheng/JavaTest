package lottery.domains.content.biz.impl;

import javautils.redis.JedisTemplate;
import lottery.domains.content.biz.UserBetsNoticeService;
import lottery.web.WSC;
import org.apache.commons.collections.MapUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by Nick on 2017/2/26.
 */
@Service
public class UserBetsNoticeServiceImpl implements UserBetsNoticeService{

    @Autowired
    private JedisTemplate jedisTemplate;


    @Override
    @Transactional(readOnly = true)
    public Map<Integer, Map<String, String>> getAllBetsNotices() {
        // 获取所有数据
        Map<String, String> allBetsNotices = jedisTemplate.hgetAll(WSC.USER_BETS_NOTICE_KEY);
        if (MapUtils.isEmpty(allBetsNotices)) {
            return null;
        }

        // 有效数据
        Map<Integer, Map<String, String>> validBetsNotices = new HashMap<>();

        Iterator<String> keysIterator = allBetsNotices.keySet().iterator();
        long now = new Date().getTime();
        while (keysIterator.hasNext()) {
            String field = keysIterator.next();
            String[] fields = field.split(":");

            int userId = Integer.valueOf(fields[0]);
            long expireAt = Long.valueOf(fields[2]);

            if (now > expireAt) {
                delBetsNotice(field);
            }
            else {
                if (!validBetsNotices.containsKey(userId)) {
                    validBetsNotices.put(userId, new HashMap<String, String>());
                }

                String value = allBetsNotices.get(field);
                validBetsNotices.get(userId).put(field, value);
            }
        }

        return validBetsNotices;
    }

    @Override
    public void delBetsNotice(String field) {
        jedisTemplate.hdel(WSC.USER_BETS_NOTICE_KEY, field);
    }
}
