package lottery.domains.content.biz.impl;

import com.alibaba.fastjson.JSON;
import javautils.math.MathUtil;
import javautils.redis.JedisTemplate;
import lottery.domains.content.biz.UserHighPrizeService;
import lottery.domains.content.dao.UserDao;
import lottery.domains.content.dao.UserHighPrizeDao;
import lottery.domains.content.entity.Lottery;
import lottery.domains.content.entity.User;
import lottery.domains.content.entity.UserBets;
import lottery.domains.content.entity.UserHighPrize;
import lottery.domains.pool.DataFactory;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;

/**
 * Created by Nick on 2017/3/13.
 */
@Service
public class UserHighPrizeServiceImpl implements UserHighPrizeService {
    private static final Logger log = LoggerFactory.getLogger(UserHighPrizeServiceImpl.class);

    private static final String USER_HIGH_PRIZE_NOTICE_KEY = "USER:HIGH_PRIZE:NOTICE";

    private BlockingQueue<UserBets> userBetsQueue = new LinkedBlockingDeque<>();

    @Autowired
    private UserHighPrizeDao highTimesDao;

    @Autowired
    private JedisTemplate jedisTemplate;

    @Autowired
    private DataFactory dataFactory;

    @Autowired
    private UserDao uDao;

    private static boolean isRunning = false;

    @Scheduled(cron = "0/3 * * * * *")
    public void run() {
        synchronized (UserHighPrizeServiceImpl.class) {
            if (isRunning == true) {
                // 任务正在运行，本次中断
                return;
            }
            isRunning = true;
        }
        try {
            add();
        } finally {
            isRunning = false;
        }
    }

    private void add() {
        if(userBetsQueue != null && userBetsQueue.size() > 0) {
            try {
                List<UserBets> adds = new LinkedList<>();
                userBetsQueue.drainTo(adds, 50);
                if (CollectionUtils.isNotEmpty(adds)) {
                    add(adds);
                }
            } catch (Exception e) {
                log.error("添加用户大额中奖失败", e);
            }
        }
    }

    private void add(List<UserBets> adds) {
        Map<Integer, User> userMap = new HashMap<>();
        for (UserBets add : adds) {
            Lottery lottery = dataFactory.getLottery(add.getLotteryId());
            if (lottery == null) {
                continue;
            }

            User user = null;
            if (userMap.containsKey(add.getUserId())) {
                user = userMap.get(add.getUserId());
            }
            if (user == null) {
                user = uDao.getById(add.getUserId());
            }
            if (user == null) {
                continue;
            }

            UserHighPrize highTimes = convert(add, lottery, user);

            // 加到数据库
            highTimesDao.add(highTimes);

            // // 加到redis提醒 暂时取消使用，把这句注释取消，再在global.js里把registerWebSocket()取消注释就可以启用了
            // addToRedis(highTimes, user);
        }
    }

    private UserHighPrize convert(UserBets userBets, Lottery lottery, User user) {
        UserHighPrize highPrize = new UserHighPrize();
        highPrize.setUserId(user.getId());
        highPrize.setPlatform(2); // 类型;2:彩票;11:PT;4:AG
        highPrize.setName(lottery.getShowName());
        highPrize.setNameId(lottery.getId()+"");
        highPrize.setSubName(userBets.getExpect());
        highPrize.setRefId(userBets.getId()+"");
        highPrize.setMoney(userBets.getMoney());
        highPrize.setPrizeMoney(userBets.getPrizeMoney());

        double times = userBets.getPrizeMoney() / userBets.getMoney();
        times = MathUtil.doubleFormat(times, 3);
        highPrize.setTimes(times);
        highPrize.setTime(userBets.getPrizeTime());
        highPrize.setStatus(0); // 状态;0:待确认;1:已锁定;2:已确认

        return highPrize;
    }

    private void addToRedis(UserHighPrize highPrize, User user) {
        // 域：数据ID
        // 值：描述语言

        String field = highPrize.getId()+""; // 数据ID
        Map<String, Object> value = new HashMap<>();

        value.put("platform", highPrize.getPlatform()); // 类型;2:彩票;11:PT;4:AG
        value.put("username", user.getUsername()); // 用户名
        value.put("name", highPrize.getName()); // 游戏名称
        value.put("subName", highPrize.getSubName()); // 期号或桌号等
        value.put("refId", highPrize.getRefId()); // 游戏记录ID,可以是user_bets的id也可能是game_bets的id
        value.put("money", highPrize.getMoney()); // 投注金额
        value.put("prizeMoney", highPrize.getPrizeMoney()); // 奖金
        value.put("times", highPrize.getTimes()); // 倍数
        value.put("type", 1); // 1:大额中奖提醒

        // String money = BigDecimal.valueOf(highPrize.getMoney()).setScale(4, BigDecimal.ROUND_HALF_DOWN).toString();
        // String prizeMoney = BigDecimal.valueOf(highPrize.getPrizeMoney()).setScale(5, BigDecimal.ROUND_HALF_DOWN).toString();

        // if (highPrize.getTimes() >= 3) {
        //     value = "用户%s在%s投注￥%s元中奖￥%s元,奖金倍数:%s,请在<大额中奖查询>中确认";
        //     value = String.format(value, user.getUsername(), highPrize.getName(), money, prizeMoney, highPrize.getTimes());
        // }
        // else {
        //     value = "用户%s在%s投注￥%s元中奖￥%s元,请在<大额中奖查询>中确认";
        //     value = String.format(value, user.getUsername(), highPrize.getName(), money, prizeMoney);
        // }

        jedisTemplate.hset(USER_HIGH_PRIZE_NOTICE_KEY, field, JSON.toJSONString(value));
    }

    @Override
    public void addIfNecessary(UserBets userBets) {
        if (userBets.getPrizeMoney() <= 0) {
            return;
        }

        if (userBets.getPrizeMoney() <= 100 && userBets.getMoney() <= 100) {
            return;
        }

        // 奖金大于1万,倍数高于5倍
        double times = userBets.getPrizeMoney() / userBets.getMoney();
        if (userBets.getPrizeMoney() >= 10000 || times >= 5.0) {
            userBetsQueue.offer(userBets);
        }
    }

    public static void main(String[] args) {
        String value = "用户%s在%s%s投注￥%s元中奖￥%s元,请在<大额中奖查询>中确认";
        value = String.format(value, "aaa", "aaa", "aaa", "100", "200");
        System.out.println(value);
    }
}
