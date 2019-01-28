package lottery.domains.open.jobs;

import javautils.date.DateUtil;
import javautils.date.Moment;
import lottery.domains.content.biz.LotteryOpenCodeService;
import lottery.domains.content.biz.UserBetsSettleService;
import lottery.domains.content.dao.UserBetsDao;
import lottery.domains.content.entity.Lottery;
import lottery.domains.content.entity.LotteryOpenCode;
import lottery.domains.content.entity.UserBets;
import lottery.domains.content.global.Global;
import lottery.domains.pool.DataFactory;
import lottery.domains.utils.open.LotteryOpenUtil;
import org.apache.commons.lang.StringUtils;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 急速秒秒彩开奖任务
 * Created by Nick on 2016/10/19.
 */
@Component
public class JSMMCLotteryOpenJob {
    private final static Logger log = LoggerFactory.getLogger(JSMMCLotteryOpenJob.class);
    private static boolean isRuning = false;
    @Autowired
    protected UserBetsDao userBetsDao;
    @Autowired
    protected LotteryOpenCodeService lotteryOpenCodeService;
    @Autowired
    protected UserBetsSettleService userBetsSettleService;
    @Autowired
    protected DataFactory dataFactory;
    @Autowired
    protected LotteryOpenUtil lotteryOpenUtil;

    @Scheduled(cron = "0/3 * * * * *")
    public void openUserBets() {
        synchronized (JSMMCLotteryOpenJob.class) {
            if (isRuning == true) {
                return;
            }
            isRuning = true;
        }

        try {
            long start = System.currentTimeMillis();

            // 开奖
            int total = open();
            long spend = System.currentTimeMillis() - start;

            if (total > 0) {
                log.debug("急速秒秒彩开奖完成,共计开奖" + total + "条注单,耗时" + spend);
            }
        } catch (Exception e){
            log.error("急速秒秒彩开奖异常", e);
        }
        finally {
            isRuning = false;
        }
    }

    private int open() {
        Lottery lottery = dataFactory.getLottery(117);
        if (lottery == null || lottery.getStatus() != Global.LOTTERY_STATUS_OPEN) {
            return 0;
        }
        int total = openByLottery(lottery);
        return total;
    }

    private int openByLottery(Lottery lottery) {
        int total = 0;
        try {
            // 获取注单
            List<UserBets> userBetses = getUserBetsNotOpen(lottery.getId());

            // 组装开奖号码
            Map<LotteryOpenCode, List<UserBets>> groupByOpenCodes = groupUserBetsByOpenCode(lottery, userBetses);
            if (groupByOpenCodes.isEmpty()) {
                return 0;
            }

            for (Map.Entry<LotteryOpenCode, List<UserBets>> groupByOpenCode : groupByOpenCodes.entrySet()) {
                LotteryOpenCode openCode = groupByOpenCode.getKey();
                userBetsSettleService.settleUserBets(groupByOpenCode.getValue(), openCode, lottery);
                total += groupByOpenCode.getValue().size();
            }
        } catch (Exception e) {
            log.error("急速秒秒彩开奖异常:" + lottery.getShowName(), e);
        }
        return total;
    }

    private Map<LotteryOpenCode, List<UserBets>> groupUserBetsByOpenCode(Lottery lottery, List<UserBets> userBetses) {

        // 用户以及期数
        Map<String, List<UserBets>> groupByExpect = new HashMap<>();
        for (UserBets userBet : userBetses) {
            String key = userBet.getUserId() + "_" + userBet.getExpect();

            if (!groupByExpect.containsKey(key)) {
                groupByExpect.put(key, new ArrayList<UserBets>());
            }

            groupByExpect.get(key).add(userBet);
        }

        Map<LotteryOpenCode, List<UserBets>> groupByCode = new HashMap<>(); // LotteryOpenCode:userBets
        Map<String, LotteryOpenCode> expectCodes = new HashMap<>(); // userId_expect:LotteryOpenCode

        for (Map.Entry<String, List<UserBets>> entry : groupByExpect.entrySet()) {
            if (!expectCodes.containsKey(entry.getKey())) {
                String[] split = entry.getKey().split("_");
                int userId = Integer.valueOf(split[0]);
                String expect = String.valueOf(split[1]);

                LotteryOpenCode lotteryOpenCode = lotteryOpenCodeService.getByExceptAndUserId(lottery.getShortName(), userId, expect);
                if (lotteryOpenCode == null) {
                    continue;
                }
                else {
                    expectCodes.put(entry.getKey(), lotteryOpenCode);
                }
            }

            LotteryOpenCode openCode = expectCodes.get(entry.getKey());
            if (openCode == null || StringUtils.isEmpty(openCode.getCode())) {
                continue;
            }

            if (!groupByCode.containsKey(openCode)) {
                groupByCode.put(openCode, new ArrayList<UserBets>());
            }

            groupByCode.get(openCode).addAll(entry.getValue());
        }

        return groupByCode;
    }


    private List<UserBets> getUserBetsNotOpen(int lotteryId) {
        List<Criterion> criterions = new ArrayList<>();
        criterions.add(Restrictions.eq("status", Global.USER_BETS_STATUS_NOT_OPEN));// 未开奖
        criterions.add(Restrictions.eq("lotteryId", lotteryId));
        criterions.add(Restrictions.gt("id", 0));

        // 只查询过去2小时的
        String start = new Moment().add(-2, "hours").toSimpleTime();
        String end = new Moment().add(-2, "seconds").toSimpleTime();
        criterions.add(Restrictions.ge("time", start));
        criterions.add(Restrictions.le("time", end));

        // // 最早投注最先开奖
        // List<Order> orders = new ArrayList<>();
        // orders.add(Order.asc("time"));

        // 查询注单
        List<UserBets> betsList = userBetsDao.list(criterions, null);

        return betsList;
    }
}
