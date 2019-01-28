package lottery.domains.open.jobs;

import lottery.domains.content.biz.LotteryOpenCodeService;
import lottery.domains.content.biz.UserBetsService;
import lottery.domains.content.biz.UserBetsSettleService;
import lottery.domains.content.dao.UserBetsDao;
import lottery.domains.content.entity.Lottery;
import lottery.domains.content.entity.LotteryOpenCode;
import lottery.domains.content.entity.UserBets;
import lottery.domains.content.global.Global;
import lottery.domains.pool.DataFactory;
import lottery.domains.utils.open.LotteryOpenUtil;
import lottery.domains.utils.open.OpenTime;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * 开奖任务，除急速秒秒彩外
 * Created by Nick on 2016/10/19.
 */
@Component
public class LotteryOpenJob {
    private final static Logger log = LoggerFactory.getLogger(LotteryOpenJob.class);
    private static boolean isRuning = false;
    @Autowired
    protected UserBetsDao userBetsDao;
    @Autowired
    protected LotteryOpenCodeService lotteryOpenCodeService;
    @Autowired
    protected UserBetsSettleService userBetsSettleService;
    @Autowired
    private UserBetsService uBetsService;
    @Autowired
    protected DataFactory dataFactory;
    @Autowired
    protected LotteryOpenUtil lotteryOpenUtil;
    @Autowired
    private MailJob mailJob;


    @Scheduled(cron = "0/5 * * * * *")
    public void openUserBets() {
        synchronized (LotteryOpenJob.class) {
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
                log.debug("开奖完成,共计开奖" + total + "条注单,耗时" + spend);
            }
        } catch (Exception e){
            log.error("开奖异常", e);
        }
        finally {
            isRuning = false;
        }
    }

    private int open() {
        int total = 0;
        List<Lottery> lotteries = dataFactory.listLottery();
        for (Lottery lottery : lotteries) {
            if (lottery.getStatus() == Global.LOTTERY_STATUS_OPEN && lottery.getId() != 117) {
                int lotteryTotal = openByLottery(lottery);
                total += lotteryTotal;
            }
        }

        return total;
    }
    /**
     * 开奖结算
     * @author JiangFengJiong
     * @date 2018-8-27 15:12:46
     */
    private int openByLottery(Lottery lottery) {
        int total = 0;
        try {
            // 获取注单
            long userBetsStart = System.currentTimeMillis();

            List<UserBets> userBetses = getUserBetsNotOpen(lottery.getId());
            if (CollectionUtils.isEmpty(userBetses)) {
                return 0;
            }

            long userBetsSpent = System.currentTimeMillis() - userBetsStart;
            if (userBetsSpent > 10000) {
                String warningMsg = String.format("获取%s未开奖注单耗时告警；获取未开奖注单耗时较多；注单%s条,耗时达到%s", lottery.getShowName(), userBetses.size(), userBetsSpent);
                log.warn(warningMsg);
                mailJob.addWarning(warningMsg);
            }

            // 组装开奖号码
            long groupStart = System.currentTimeMillis();

            LinkedHashMap<LotteryOpenCode, List<UserBets>> groupByOpenCodes = groupUserBetsByOpenCode(lottery, userBetses);
            if (groupByOpenCodes.isEmpty()) {
                return 0;
            }

            long groupSpent = System.currentTimeMillis() - groupStart;
            if (groupSpent > 10000) {
                String warningMsg = String.format("获取%s未开奖注单分组时耗时告警；注单%s条,耗时达到%s", lottery.getShowName(), userBetses.size(), groupSpent);
                log.warn(warningMsg);
                mailJob.addWarning(warningMsg);
            }

            // 按照追号单分组
            LinkedHashMap<String, List<UserBets>> groupByChase = groupByChase(userBetses);

            // 按照开奖号码进行分组
            Map<String, LotteryOpenCode> openCodeMap = new HashMap<>();
            for (LotteryOpenCode lotteryOpenCode : groupByOpenCodes.keySet()) {
                openCodeMap.put(lotteryOpenCode.getExpect(), lotteryOpenCode);
            }

            for (Map.Entry<LotteryOpenCode, List<UserBets>> groupByOpenCode : groupByOpenCodes.entrySet()) {
                LotteryOpenCode openCode = groupByOpenCode.getKey(); // 开奖号码
                List<UserBets> expectUserBets = groupByOpenCode.getValue(); // 当前期注单列表

                // 腾讯分分彩自动撤单
                if ("txffc".equalsIgnoreCase(openCode.getLottery()) && openCode.getOpenStatus() == Global.LOTTERY_OPEN_CODE_STATUS_UNCANCEL) {
                    uBetsService.cancelByTXFFCInvalid(openCode, groupByOpenCode.getValue());
                    continue;
                }

                // 腾讯龙虎斗自动撤单
                if ("txlhd".equalsIgnoreCase(openCode.getLottery()) && openCode.getOpenStatus() == Global.LOTTERY_OPEN_CODE_STATUS_UNCANCEL) {
                    uBetsService.cancelByTXLHDInvalid(openCode, groupByOpenCode.getValue());
                    continue;
                }

                // 开奖号码不是待开奖并且不是已开奖(已开奖也有可能是部分开奖)
                if (openCode.getOpenStatus() != Global.LOTTERY_OPEN_CODE_STATUS_NOT_OPEN && openCode.getOpenStatus() != Global.LOTTERY_OPEN_CODE_STATUS_OPENED) {
                    continue;
                }

                // 如果注单中存在追号注单，并且追号注单的最先开期注单没有开奖号码，那需要排除掉这些数据
                List<UserBets> filterChaseBets = new LinkedList<>();
                for (UserBets expectUserBet : expectUserBets) {
                    // 如果是已经开奖，那么只保留追号单
                    if (openCode.getOpenStatus() == Global.LOTTERY_OPEN_CODE_STATUS_OPENED) {
                        if (expectUserBet.getType() != Global.USER_BETS_TYPE_CHASE) {
                            continue;
                        }
                        if (expectUserBet.getChaseStop() == null) {
                            continue;
                        }
                        if (expectUserBet.getChaseStop() != 1) {
                            continue;
                        }
                    }

                    if (expectUserBet.getType() == Global.USER_BETS_TYPE_CHASE && expectUserBet.getChaseStop() != null && expectUserBet.getChaseStop() == 1) {

                        boolean open = true;

                        List<UserBets> expectChaseBets = groupByChase.get(expectUserBet.getChaseBillno());
                        if (expectChaseBets != null) {
                            for (UserBets expectChaseBet : expectChaseBets) {
                                if (expectChaseBet.getId() != expectUserBet.getId()) {
                                    if (expectChaseBet.getExpect().compareTo(expectUserBet.getExpect()) <= -1) {
                                        // 看比追号单小的期有没有开奖号码，如果没有，则不应该开奖
                                        LotteryOpenCode lotteryOpenCode = openCodeMap.get(expectChaseBet.getExpect());
                                        if (lotteryOpenCode == null) {
                                            open = false;
                                            break;
                                        }
                                    }
                                }
                            }
                        }
                        if (open == true && expectUserBet.getLocked() == 0) {
                            filterChaseBets.add(expectUserBet);
                        }
                    }
                    else {
                    	if(expectUserBet.getLocked() == 0) {
                    		filterChaseBets.add(expectUserBet);
                    	}
                    }
                }

                if (filterChaseBets.size() <= 0) {
                    continue;
                }


                log.debug("开始结算{}第{}期共计{}条注单", lottery.getShowName(), openCode.getExpect(), filterChaseBets.size());
                long start = System.currentTimeMillis();

                userBetsSettleService.settleUserBets(filterChaseBets, openCode, lottery);
                total += filterChaseBets.size();

                long spent = System.currentTimeMillis() - start;
                log.debug("完成结算{}第{}期共计{}条注单,耗时{}", lottery.getShowName(), openCode.getExpect(), filterChaseBets.size(), spent);

                if (spent >= 30000) {
                    String warningMsg = String.format("结算耗时告警；结算%s第%s时注单%s条,耗时达到%s", lottery.getShowName(), openCode.getExpect(), groupByOpenCode.getValue().size(), spent);
                    log.warn(warningMsg);
                    mailJob.addWarning(warningMsg);
                }
            }
        } catch (Exception e) {
            log.error("开奖异常:" + lottery.getShowName(), e);
        }
        return total;
    }

    private LinkedHashMap<String, List<UserBets>> groupByChase(List<UserBets> allUserBets) {
        LinkedHashMap<String, List<UserBets>> groupByChase = new LinkedHashMap<>();
        for (UserBets userBets : allUserBets) {
            if (userBets.getType() == Global.USER_BETS_TYPE_CHASE && userBets.getChaseStop() == 1) {
                if (!groupByChase.containsKey(userBets.getChaseBillno())) {
                    groupByChase.put(userBets.getChaseBillno(), new LinkedList<UserBets>());
                }

                groupByChase.get(userBets.getChaseBillno()).add(userBets);
            }
        }

        return groupByChase;
    }

    private LinkedHashMap<LotteryOpenCode, List<UserBets>> groupUserBetsByOpenCode(Lottery lottery, List<UserBets> userBetses) {

        // 按期数进行分数
        LinkedHashMap<String, List<UserBets>> groupByExpect = new LinkedHashMap<>();
        for (UserBets userBet : userBetses) {
            String key = userBet.getExpect();

            if (!groupByExpect.containsKey(key)) {
                groupByExpect.put(key, new LinkedList<UserBets>());
            }

            groupByExpect.get(key).add(userBet);
        }

        LinkedHashMap<LotteryOpenCode, List<UserBets>> groupByCode = new LinkedHashMap<>(); // LotteryOpenCode:userBets
        LinkedHashMap<String, LotteryOpenCode> expectCodes = new LinkedHashMap<>(); // expect:LotteryOpenCode
        for (Map.Entry<String, List<UserBets>> stringListEntry : groupByExpect.entrySet()) {
            String expect = stringListEntry.getKey();

            // 查找开奖号码
            if (!expectCodes.containsKey(expect)) {
                // 查找开奖号码
                LotteryOpenCode lotteryOpenCode = lotteryOpenCodeService.getByExcept(lottery.getShortName(), expect);
                if (lotteryOpenCode == null || StringUtils.isEmpty(lotteryOpenCode.getCode())) {
                    expectCodes.put(expect, new LotteryOpenCode()); // 实际为空，只是标识一下
                }
                else {
                    expectCodes.put(expect, lotteryOpenCode);
                }
            }

            LotteryOpenCode openCode = expectCodes.get(expect);
            if (openCode == null || StringUtils.isEmpty(openCode.getCode())) {
                continue;
            }

            if (!groupByCode.containsKey(openCode)) {
                groupByCode.put(openCode, new LinkedList<UserBets>());
            }

            groupByCode.get(openCode).addAll(stringListEntry.getValue());
        }

        return groupByCode;
    }


    private List<UserBets> getUserBetsNotOpen(int lotteryId) {
        List<Criterion> criterions = new ArrayList<>();
        criterions.add(Restrictions.eq("status", Global.USER_BETS_STATUS_NOT_OPEN));// 未开奖
        criterions.add(Restrictions.eq("lotteryId", lotteryId));
        criterions.add(Restrictions.gt("id", 0));

        OpenTime currOpenTime;
        try {
            currOpenTime = lotteryOpenUtil.getCurrOpenTime(lotteryId);
            if (currOpenTime == null) {
                log.error("开奖获取当前期数时为空,彩票ID：" + lotteryId);
                return null;
            }
        } catch (Exception e) {
            log.error("开奖获取当前期数时异常,彩票ID：" + lotteryId, e);
            return null;
        }

        criterions.add(Restrictions.lt("expect", currOpenTime.getExpect())); // 小于当前期

        // // 最早投注最先开奖
        // List<Order> orders = new ArrayList<>();
        // orders.add(Order.asc("time"));

        // 查询注单
        List<UserBets> betsList = userBetsDao.list(criterions, null);

        return betsList;
    }
}
