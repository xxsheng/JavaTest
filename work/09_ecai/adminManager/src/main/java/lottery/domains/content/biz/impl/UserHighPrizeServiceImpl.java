package lottery.domains.content.biz.impl;

import com.alibaba.fastjson.JSON;
import javautils.StringUtil;
import javautils.jdbc.PageList;
import javautils.math.MathUtil;
import javautils.redis.JedisTemplate;
import lottery.domains.content.biz.UserHighPrizeService;
import lottery.domains.content.dao.UserDao;
import lottery.domains.content.dao.UserHighPrizeDao;
import lottery.domains.content.entity.GameBets;
import lottery.domains.content.entity.SysPlatform;
import lottery.domains.content.entity.User;
import lottery.domains.content.entity.UserHighPrize;
import lottery.domains.content.vo.user.UserHighPrizeTimesVO;
import lottery.domains.content.vo.user.UserVO;
import lottery.domains.pool.LotteryDataFactory;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;

/**
 * Created by Nick on 2017/3/13.
 */
@Service
public class UserHighPrizeServiceImpl implements UserHighPrizeService {
    private static final Logger log = LoggerFactory.getLogger(UserHighPrizeServiceImpl.class);

    private static final String USER_HIGH_PRIZE_NOTICE_KEY = "USER:HIGH_PRIZE:NOTICE";

    private BlockingQueue<GameBets> gameBetsQueue = new LinkedBlockingDeque<>();

    @Autowired
    private UserHighPrizeDao highPrizeDao;

    @Autowired
    private JedisTemplate jedisTemplate;

    @Autowired
    private UserDao uDao;

    @Autowired
    private LotteryDataFactory dataFactory;

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
        if(gameBetsQueue != null && gameBetsQueue.size() > 0) {
            try {
                List<GameBets> adds = new LinkedList<>();
                gameBetsQueue.drainTo(adds, 50);
                if (CollectionUtils.isNotEmpty(adds)) {
                    add(adds);
                }
            } catch (Exception e) {
                log.error("添加用户大额中奖失败", e);
            }
        }
    }

    private void add(List<GameBets> adds) {
        for (GameBets add : adds) {

            UserVO user = dataFactory.getUser(add.getUserId());
            if (user == null) {
                continue;
            }

            UserHighPrize highPrize = convert(add, user);

            // 加到数据库
            highPrizeDao.add(highPrize);

            // 加到redis提醒
            addToRedis(highPrize, user);
        }
    }

    private UserHighPrize convert(GameBets gameBets, UserVO user) {
        UserHighPrize highTimes = new UserHighPrize();
        highTimes.setUserId(user.getId());
        highTimes.setPlatform(gameBets.getPlatformId()); // 平台;2:彩票;11:PT;4:AG
        SysPlatform sysPlatform = dataFactory.getSysPlatform(gameBets.getPlatformId());
        highTimes.setName(sysPlatform.getName());
        highTimes.setNameId(gameBets.getPlatformId()+"");
        highTimes.setSubName(gameBets.getGameName());
        highTimes.setRefId(gameBets.getId()+"");
        highTimes.setMoney(gameBets.getMoney());
        highTimes.setPrizeMoney(gameBets.getPrizeMoney());

        double times = gameBets.getPrizeMoney();
        if (gameBets.getMoney() > 0) {
            times = gameBets.getPrizeMoney() / gameBets.getMoney();
        }

        times = MathUtil.doubleFormat(times, 3);
        highTimes.setTimes(times);
        highTimes.setTime(gameBets.getTime());
        highTimes.setStatus(0); // 状态;0:待确认;1:已锁定;2:已确认

        return highTimes;
    }

    private void addToRedis(UserHighPrize highPrize, UserVO user) {
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

        // String field = highPrize.getId()+""; // 数据ID
        // String value;
        // String money = BigDecimal.valueOf(highPrize.getMoney()).setScale(4, BigDecimal.ROUND_HALF_DOWN).toString();
        // String prizeMoney = BigDecimal.valueOf(highPrize.getPrizeMoney()).setScale(5, BigDecimal.ROUND_HALF_DOWN).toString();
        //
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
    public PageList search(Integer type,String username, Integer platform,
                           String nameId, String subName, String refId,
                           Double minMoney, Double maxMoney,
                           Double minPrizeMoney, Double maxPrizeMoney,
                           Double minTimes, Double maxTimes,
                           String minTime, String maxTime,
                           Integer status, String confirmUsername, int start, int limit) {
        List<Criterion> criterions = new ArrayList<>();
        List<Order> orders = new ArrayList<>();
        StringBuilder sqlStr = new StringBuilder();
        if(StringUtil.isNotNull(username)) {
            User user = uDao.getByUsername(username);
            if(user != null) {
            	sqlStr.append(" and b.user_id = ").append(user.getId());
            }
        }
        if(platform != null) {
        	sqlStr.append(" and b.platform = ").append(platform);
        }
        if(StringUtils.isNotEmpty(nameId)) {
        	sqlStr.append(" and b.name_id = ").append("'"+nameId+"'");
        }
        if(StringUtil.isNotNull(subName)) {
        	sqlStr.append(" and b.sub_name = ").append("'"+subName+"'");
        }
        if(StringUtil.isNotNull(refId)) {
        	sqlStr.append(" and b.ref_id = ").append("'"+refId+"'");
        }
        if(minMoney != null) {
        	sqlStr.append(" and b.money >= ").append(minMoney);
        }
        if(maxMoney != null) {
        	sqlStr.append(" and b.money <= ").append(maxMoney);
        }
        if(minPrizeMoney != null) {
        	sqlStr.append(" and b.prize_money >= ").append(minPrizeMoney);
        }
        if(maxPrizeMoney != null) {
        	sqlStr.append(" and b.prize_money <= ").append(maxPrizeMoney);
        }
        if(minTimes != null) {
        	sqlStr.append(" and b.times >= ").append("'"+minTimes+"'");
        }
        if(maxTimes != null) {
        	sqlStr.append(" and b.times <= ").append("'"+maxTimes+"'");
        }
        if(StringUtil.isNotNull(minTime)) {
        	sqlStr.append(" and b.time >= ").append("'"+minTime+"'");
        }
        if(StringUtil.isNotNull(maxTime)) {
            sqlStr.append(" and b.time <= ").append("'"+maxTime+"'");
        }
        if(status != null) {
        	   sqlStr.append(" and b.status = ").append(status);
        }
        if(StringUtil.isNotNull(confirmUsername)) {
        	 sqlStr.append(" and b.confirm_username = ").append("'"+confirmUsername+"'");
        }
        sqlStr.append(" and b.id > ").append(0);
		String nickname = "试玩用户";
		if(type != null){
			sqlStr.append("  and u.type = ").append(type);
		}else{
			sqlStr.append("  and u.upid != ").append(0);
		}
		sqlStr.append("  and u.upid != ").append(0);
        sqlStr.append(" order by b.id desc ");

        List<UserHighPrizeTimesVO> list = new ArrayList<>();
        PageList pList = highPrizeDao.find(sqlStr.toString(),start, limit);
        for (Object tmpBean : pList.getList()) {
            UserHighPrizeTimesVO tmpVO = new UserHighPrizeTimesVO((UserHighPrize) tmpBean, dataFactory);
            list.add(tmpVO);
        }
        pList.setList(list);
        return pList;
    }

    @Override
    public UserHighPrize getById(int id) {
        return highPrizeDao.getById(id);
    }

    @Override
    public synchronized boolean lock(int id, String username) {
        UserHighPrize highPrize = highPrizeDao.getById(id);
        if (highPrize == null) {
            return false;
        }
        if (highPrize.getStatus() == 0) {
            return highPrizeDao.updateStatusAndConfirmUsername(id, 1, username);
        }

        if (highPrize.getStatus() == 1 && StringUtils.equals(username, highPrize.getConfirmUsername())) {
            // 0:待确认;1:已锁定;2:已确认
            return true;
        }

        return false;
    }

    @Override
    public synchronized boolean unlock(int id, String username) {
        UserHighPrize highPrize = highPrizeDao.getById(id);
        if (highPrize == null) {
            return false;
        }
        if (highPrize.getStatus() == 1 && username.equals(highPrize.getConfirmUsername())) {
            // 0:待确认;1:已锁定;2:已确认
            return highPrizeDao.updateStatusAndConfirmUsername(id, 0, null);
        }

        return false;
    }

    @Override
    public synchronized boolean confirm(int id, String username) {
        UserHighPrize highPrize = highPrizeDao.getById(id);
        if (highPrize == null) {
            return false;
        }
        if (highPrize.getStatus() == 1 && username.equals(highPrize.getConfirmUsername())) {
            // 0:待确认;1:已锁定;2:已确认
            return highPrizeDao.updateStatus(id, 2);
        }

        return false;
    }

    @Override
    public void addIfNecessary(GameBets gameBets) {
        if (gameBets.getPrizeMoney() <= 0) {
            return;
        }
        if (gameBets.getPrizeMoney() <= 100 && gameBets.getMoney() <= 100) {
            return;
        }

        // 奖金大于1万,倍数高于3倍
        double times = gameBets.getPrizeMoney();
        if (gameBets.getMoney() > 0) {
            times = gameBets.getPrizeMoney() / gameBets.getMoney();
        }
        if (gameBets.getPrizeMoney() >= 5000 || times >= 13.0) {
            gameBetsQueue.offer(gameBets);
        }
    }

    @Override
    public int getUnProcessCount() {
        return highPrizeDao.getUnProcessCount();
    }

    @Override
    public Map<String, String> getAllHighPrizeNotices() {
        return jedisTemplate.hgetAll(USER_HIGH_PRIZE_NOTICE_KEY);
    }

    @Override
    public void delHighPrizeNotice(String field) {
        jedisTemplate.hdel(USER_HIGH_PRIZE_NOTICE_KEY, field);
    }
}
