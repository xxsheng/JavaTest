package admin.domains.jobs;

import javautils.redis.JedisTemplate;
import lottery.domains.content.dao.UserDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;

/**
 * 前端用户session重置器
 * Created by Nick on 2016/12/4.
 */
@Component
public class SessionResetJob {

    @Autowired
    private JedisTemplate jedisTemplate;

    @Autowired
    private UserDao uDao;

    private static boolean isRunning = false;

    @Scheduled(cron = "0 0/1 * * * *")
    public void reset() {
        synchronized (SessionResetJob.class) {
            if (isRunning == true) {
                // 任务正在运行，本次中断
                return;
            }
            isRunning = true;
        }
        try {
            check();
        } finally {
            isRunning = false;
        }
    }

    private void check() {
        // 先找出redis中所有session
        Set<String> sessionIds = jedisTemplate.keys("spring:session:sessions:*"); // keys一般是不在生产环境使用的，这里为了方便(量小不会有很大影响，量大就不行了)，暂时先这样写，后续有性能瓶颈时再优化
        if (sessionIds == null || sessionIds.isEmpty()) {
            uDao.updateAllOffline();
            return;
        }

        Set<String> processIds = new HashSet<>(sessionIds.size());
        for (String sessionId : sessionIds) {
            processIds.add(sessionId.replaceAll("spring:session:sessions:", ""));
        }

        // 再将所有其它用户的sessionid改成空，onlineStatus改成0
        uDao.updateOnlineStatusNotIn(processIds);
    }
}
