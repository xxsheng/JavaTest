package lottery.domains.open.jobs;

import javautils.date.Moment;
import lottery.domains.content.biz.LotteryOpenCodeService;
import lottery.domains.content.entity.Lottery;
import lottery.domains.content.entity.LotteryOpenCode;
import lottery.domains.content.entity.UserBets;
import lottery.domains.content.global.Global;
import org.apache.commons.collections.CollectionUtils;
import org.hibernate.criterion.Criterion;
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
 * 急速秒秒彩开奖号码任务
 * Created by Nick on 2016/12/19.
 */
@Component
public class JSMMCOpenCodeJob extends AbstractSelfLotteryOpenCodeJob {
    private final static Logger log = LoggerFactory.getLogger(JSMMCOpenCodeJob.class);
    private static boolean isRuning = false;

    @Autowired
    protected LotteryOpenCodeService lotteryOpenCodeService;

    @Scheduled(cron = "0/5 * * * * *")
    public void generate() {
        synchronized (JSMMCOpenCodeJob.class) {
            if (isRuning == true) {
                return;
            }
            isRuning = true;
        }

        try {
            // 开始生成
            doGenerate();
        } catch (Exception e) {
            log.error("自主彩生成开奖号码异常", e);
        } finally {
            isRuning = false;
        }
    }

    /**
     * 开始生成
     */
    private void doGenerate() {
        Lottery lottery = dataFactory.getLottery(117);
        if (lottery == null || lottery.getStatus() != Global.LOTTERY_STATUS_OPEN) {
            return;
        }

        // 查找待开奖注单
        List<UserBets> userBets = getUserBets();

        // 如果没有注单，返回
        if (CollectionUtils.isEmpty(userBets)) {
            return;
        }

        // 按照用户ID,期号分组
        Map<String, List<UserBets>> group = group(userBets);

        for (List<UserBets> userBetsList : group.values()) {
            doGenerateByUserBets(userBetsList);
        }
    }

    private Map<String, List<UserBets>> group(List<UserBets> userBets) {
        Map<String, List<UserBets>> group = new HashMap<>();
        for (UserBets userBet : userBets) {
            String key = userBet.getUserId() + "_" + userBet.getExpect();

            if (!group.containsKey(key)) {
                group.put(key, new ArrayList<UserBets>());
            }

            group.get(key).add(userBet);
        }

        return group;
    }

    /**
     * 根据注单生成
     */
    private void doGenerateByUserBets(List<UserBets> userBets) {
        // 如果已经有开奖号码，那就不开了
        UserBets userBet = userBets.get(0);
        Lottery lottery = dataFactory.getLottery(userBet.getLotteryId());
        int userId = userBet.getUserId();
        String expect = userBet.getExpect();

        LotteryOpenCode openCode = lotteryOpenCodeService.getByExceptAndUserId(lottery.getShortName(), userId, expect);
        if (openCode != null) {
            return;
        }

        generateOpenCode(lottery, expect, userId);
    }

    private List<UserBets> getUserBets() {
        List<Criterion> criterions = new ArrayList<>();
        criterions.add(Restrictions.eq("status", Global.USER_BETS_STATUS_NOT_OPEN));// 未开奖
        criterions.add(Restrictions.eq("lotteryId", 117)); // 117就是急速秒秒彩
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
