package lottery.domains.content.biz.impl;

import javautils.array.ArrayUtils;
import javautils.math.MathUtil;
import lottery.domains.content.biz.UserBetsPointService;
import lottery.domains.content.biz.UserBillService;
import lottery.domains.content.dao.UserDao;
import lottery.domains.content.entity.Lottery;
import lottery.domains.content.entity.LotteryPlayRules;
import lottery.domains.content.entity.User;
import lottery.domains.content.entity.UserBets;
import lottery.domains.content.global.Global;
import lottery.domains.open.jobs.MailJob;
import lottery.domains.pool.DataFactory;
import lottery.domains.utils.user.UserCodePointUtil;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;

/**
 * Created by Nick on 2017-05-19.
 */
@Service
public class UserBetsPointServiceImpl implements UserBetsPointService {
    private static final Logger log = LoggerFactory.getLogger(UserBetsPointServiceImpl.class);
    private BlockingQueue<UserBets> userBetsQueue = new LinkedBlockingDeque<>();
    private static boolean isRunning = false;

    @Autowired
    private MailJob mailJob;

    @Autowired
    private UserDao uDao;

    @Autowired
    private UserBillService uBillService;

    @Autowired
    private UserCodePointUtil uCodePointUtil;

    @Autowired
    private DataFactory dataFactory;

    @Override
    public void add(UserBets userBets) {
        userBets.setCodes(null); // 将号码置空，减少内存占用
        userBetsQueue.offer(userBets);
    }

    @Scheduled(cron = "0/3 * * * * *")
    public void run() {
        synchronized (UserBetsPointServiceImpl.class) {
            if (isRunning == true) {
                // 任务正在运行，本次中断
                return;
            }
            isRunning = true;
        }

        try {
            calculateOrder();
        } finally {
            isRunning = false;
        }
    }

    private void calculateOrder() {
        if(userBetsQueue != null && userBetsQueue.size() > 0) {
            try {
                List<UserBets> userBetsList = new LinkedList<>();
                userBetsQueue.drainTo(userBetsList, 3000); // 每次取3000条，不知道会不会太慢
                if (CollectionUtils.isNotEmpty(userBetsList)) {
                    calculateOrders(userBetsList);
                }
            } catch (Exception e) {
                log.error("添加用户大额中奖失败", e);
            }
        }
    }


    private void calculateOrders(List<UserBets> userBetsList) {
        log.debug("正在进行注单返点，共计{}条注单", userBetsList.size());
        long start = System.currentTimeMillis();

        for (UserBets userBets : userBetsList) {
            calculatePointMoney(userBets);
        }

        long spent = System.currentTimeMillis() - start;
        log.debug("完成注单返点，共计{}条注单,耗时{}", userBetsList.size(), spent);

        if (spent >= 60000) {
            String warningMsg = String.format("返点耗时告警；对%s条注单进行返点时耗时达到%s", userBetsList.size(), spent);
            log.warn(warningMsg);
            mailJob.addWarning(warningMsg);
        }
    }


    /**
     * 增加上级返点与用户投注返点
     */
    private void calculatePointMoney(UserBets userBets) {
        // 上级返点
        calculateUpPoint(userBets);

        // 投注返点
        calculateUserPoint(userBets);
    }

    /**
     * 逐级为上级投注返点
     * 用户投注额 * ((上级返点 - 用户返点 - 累计返点) / 100)
     *   玩家a：12.0 -> 代理d：12.2 -> 代理c：12.4 -> 代理b：12.6 -> 代理a：13.0
     *
     *  玩家a投注：20,000
     *
     *  代理d返点：
     *   返点比例：  12.2 - 12.0 - 0.0 = 0.2
     *  计算比例：  0.2 / 100 = 0.002
     *  实际返钱：  20,000 * 0.002 ＝ 40
     *  累计返点：  0.2
     *
     *  代理c返点：
     *   返点比例：  12.4 - 12.0 - 0.2 = 0.2
     *  计算比例：  0.2 / 100 = 0.002
     *  返钱：     20,000 * 0.002 ＝ 40
     *  累计返点：  0.4
     *
     *  代理b返点：
     *   返点比例：  12.6 - 12.0 - 0.4 = 0.2
     *  计算比例：  0.2 / 100 = 0.002
     *  返钱：     20,000 * 0.002 ＝ 40
     *  累计返点：  0.6
     *
     *  代理a返点：
     *  返占比例：  13.0 - 12.0 - 0.6 = 0.4
     *  计算比例：  0.4 / 100 = 0.004
     *  返钱：     20,000 * 0.004 ＝ 80
     *  累计返点：  1.0
     */
    private void calculateUpPoint(UserBets userBets) {
        if (userBets.getCode() == dataFactory.getCodeConfig().getSysCode()) {
            return; // 如果投注是最高，那么就不处理了
        }

        User user = uDao.getById(userBets.getUserId());
        if (user == null || user.getUpid() == 0 || user.getUpid() == Global.USER_TOP_ID || StringUtils.isEmpty(user.getUpids())) {
            // 没有上级代理或上级是总账号时，不做任何处理
            return;
        }
        if (user.getCode() == dataFactory.getCodeConfig().getSysCode()) {
            return; // 如果用户是最高，那么就不处理了
        }

        Lottery lottery = dataFactory.getLottery(userBets.getLotteryId());
        LotteryPlayRules rules = dataFactory.getLotteryPlayRules(lottery.getId(), userBets.getRuleId());
        boolean isLocate = rules.getIsLocate() == 0 ? false: true;

        int [] upIds = ArrayUtils.transGetIds(user.getUpids()); // 上级ID
        double currentPoint = 0; // 当前累计返点比例
        double totalPoint; // 本次总计应该返点
        if (isLocate) {
            totalPoint = MathUtil.subtract(dataFactory.getCodeConfig().getSysLp(), user.getLocatePoint());
        }
        else {
            totalPoint = MathUtil.subtract(dataFactory.getCodeConfig().getSysNlp(), user.getNotLocatePoint());
        }
        for (int upId : upIds) {
            // 总账号不返点
            if (upId == Global.USER_TOP_ID) {
                continue;
            }
            if (currentPoint >= totalPoint) {
                return; // 如果已经返完就不再返了
            }

            User upUser = uDao.getById(upId);
            // 关联账号不返点
            if (upUser == null || upUser.getType() == Global.USER_TYPE_RELATED) continue;

            double upPoint = isLocate ? upUser.getLocatePoint() : upUser.getNotLocatePoint(); // 上级返点
            double point = MathUtil.subtract(upPoint, isLocate ? user.getLocatePoint() : user.getNotLocatePoint()); // 返点比例：上级返点-用户返点
            point = MathUtil.subtract(point, currentPoint); // 返点比例：减去累计返点
            if(point <= 0) continue; // 小于0就不计算了

            // 返给上级的钱：(注单金额 * (返点比例/100) )
            double upPointMoney = calculateMoneyByPoint(userBets.getMoney(), point); // 返给上级的钱

            if (upPointMoney <= 0 || upPointMoney <= 0.000001) continue;  // 小于0.00001就不计算了

            currentPoint = MathUtil.add(currentPoint, point); // 累计返点

            if (upUser.getAStatus() != 0 && upUser.getAStatus() != -1) continue; // 非正常或冻结用户不返点，系统扣除

//            // 如果用户有上级关联用户，则还需要返给上级
//            if (upUser.getRelatedUpid() != 0 && upUser.getRelatedUpid() != Global.USER_TOP_ID && upUser.getRelatedPoint() > 0 && upUser.getRelatedPoint() <= 1) {
//                double relatedMoney = MathUtil.multiply(upPointMoney, upUser.getRelatedPoint());
//                if (relatedMoney > 0.0001) {
//                    upPointMoney = MathUtil.subtract(upPointMoney, relatedMoney);
//
//                    // 生成账单
//                    boolean succeed = uBillService.addProxyReturnBill(userBets, upUser.getRelatedUpid(), relatedMoney, "关联返点");
//
//                    // 修改金额
//                    if (succeed) {
//                        uDao.updateLotteryMoney(upUser.getRelatedUpid(), relatedMoney);
//                    }
//                }
//            }

            if (upPointMoney <= 0) {
                return;
            }

            // 生成账单
            boolean succeed = uBillService.addProxyReturnBill(userBets, upId, upPointMoney, "上级返点");

            // 修改金额
            if (succeed) {
                uDao.updateLotteryMoney(upId, upPointMoney);
            }

            if (upUser.getCode() == dataFactory.getCodeConfig().getSysCode()) {
                return; // 如果上级已经是最高，那么就不处理了
            }
        }
    }

    /**
     * 增加用户投注返点
     */
    private void calculateUserPoint(UserBets userBets) {
        // 本次投注返点
        double point = userBets.getPoint();
        if (point <= 0) {
            return;
        }

        Lottery lottery = dataFactory.getLottery(userBets.getLotteryId());
        if (lottery != null) {
            LotteryPlayRules lotteryPlayRules = dataFactory.getLotteryPlayRules(lottery.getId(), userBets.getRuleId());
            // 固定奖金玩法没有投注返点
            if (lotteryPlayRules != null && lotteryPlayRules.getFixed() == 0) {
                double pointMoney = calculateMoneyByPoint(userBets.getMoney(), point);

                if (pointMoney > 0 && pointMoney >= 0.00001) {
                    boolean succeed = uBillService.addSpendReturnBill(userBets, pointMoney, "投注返点");
                    if (succeed) {
                        // 修改金额
                        uDao.updateLotteryMoney(userBets.getUserId(), pointMoney);
                    }
                }
            }
        }
    }

    /**
     * 根据返点与金额计算返钱(金额 * (返点比例/100) )
     */
    private double calculateMoneyByPoint(double money, double point) {
        double percent = MathUtil.divide(point, 100, 6);
        double _money = MathUtil.multiply(money, percent);
        return _money;
    }
}
