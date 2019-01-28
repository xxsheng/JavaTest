package lottery.domains.content.biz.impl;

import activity.domains.content.vo.activity.ActivityWheelVO;
import javautils.date.Moment;
import javautils.math.MathUtil;
import lottery.domains.content.biz.ActivityRebateWheelService;
import lottery.domains.content.biz.UserBillService;
import lottery.domains.content.dao.ActivityRebateWheelBillDao;
import lottery.domains.content.dao.ActivityRebateWheelIpDao;
import lottery.domains.content.dao.UserDao;
import lottery.domains.content.dao.UserLotteryReportDao;
import lottery.domains.content.entity.ActivityRebateWheelBill;
import lottery.domains.content.entity.ActivityRebateWheelIp;
import lottery.domains.content.entity.User;
import lottery.domains.content.entity.UserLotteryReport;
import lottery.domains.content.global.Global;
import lottery.domains.content.vo.activity.ActivityRebateWheelConfigVO;
import lottery.domains.content.vo.activity.ActivityRebateWheelRule;
import lottery.domains.content.vo.user.UserVO;
import lottery.domains.pool.DataFactory;
import lottery.web.WebJSON;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Random;

/**
 * Created by Nick on 2017/11/27.
 */
@Service
public class ActivityRebateWheelServiceImpl implements ActivityRebateWheelService {
    private static final Logger log = LoggerFactory.getLogger(ActivityRebateWheelServiceImpl.class);
    @Autowired
    private ActivityRebateWheelBillDao billDao;
    @Autowired
    private ActivityRebateWheelIpDao ipDao;
    @Autowired
    private UserLotteryReportDao reportDao;
    @Autowired
    private DataFactory dataFactory;
    @Autowired
    private UserBillService uBillService;
    @Autowired
    private UserDao uDao;

    @Override
    public ActivityWheelVO getTodayData(int userId) {
        ActivityWheelVO todayData = new ActivityWheelVO();

        ActivityRebateWheelConfigVO config = dataFactory.getActivityRebateWheelConfig();
        if (config == null || config.getStatus() != 0) {
            todayData.setEnabled(false);
            return todayData;
        }

        String today = new Moment().toSimpleDate();

        todayData.setEnabled(true);
        todayData.setDate(today);

        // 今日消费
        double todayCost = 0;
        UserLotteryReport report = reportDao.get(userId, today);
        if (report != null && report.getBillingOrder() > 0) {
            todayCost = report.getBillingOrder();
        }

        todayData.setTodayCost(todayCost);
        double remainCost = todayCost >= config.getMinCost() ? 0 : MathUtil.subtract(config.getMinCost(), todayCost);
        todayData.setRemainCost(remainCost);


        ActivityRebateWheelBill wheelBill = billDao.getByUserIdAndDate(userId, today);
        if (wheelBill == null) {
            todayData.setTodayDrew(false);
        }
        else {
            todayData.setTodayDrew(true);
            todayData.setTodayPrize(wheelBill.getAmount());
        }
        return todayData;
    }

    @Override
    public Double draw(WebJSON json, int userId, String ip) {
        ActivityRebateWheelConfigVO config = dataFactory.getActivityRebateWheelConfig();
        if (config == null || config.getStatus() != 0) {
            // 活动未开启
            json.set(2, "2-4001");
            return null;
        }

        // 查看是否是同IP
        ActivityRebateWheelIp wheelIp = ipDao.getByIp(ip);
        if (wheelIp != null && wheelIp.getUserId() != userId) {
            // 同IP不同用户
            json.set(2, "2-4011");
            return null;
        }

        Moment now = new Moment();
        String date = now.toSimpleDate();

        // 查看今天是否已经参与过了
        ActivityRebateWheelBill wheelBill = billDao.getByUserIdAndDate(userId, date);
        if (wheelBill != null) {
            UserVO user = dataFactory.getUser(userId);
            String username = user == null ? "未知" : user.getUsername();
            log.error("用户" + username + "已经参与过幸运大转盘活动,尝试再次参与,请检查");
            json.set(2, "2-4030");
            return null;
        }

        // 获取消费量
        UserLotteryReport report = reportDao.get(userId, date);
        if (report == null || report.getBillingOrder() < config.getMinCost()) {
            // 最低消费要求为%s元，您还未达到最低消费！
            json.set(2, "2-4009", config.getMinCost());
            return null;
        }

        // 随机获取金额
        double amount = randomAmount(report.getBillingOrder(), config);
        if (amount <= 0) {
            UserVO user = dataFactory.getUser(userId);
            String username = user == null ? "未知" : user.getUsername();
            log.error("用户" + username + "消费"+report.getBillingOrder()+"参与幸运大转盘活动失败,随机金额为0,请检查");
            json.set(2, "2-4014");
            return null;
        }

        // 领取成功,添加账单
        ActivityRebateWheelBill newRainBill = new ActivityRebateWheelBill();
        newRainBill.setUserId(userId);
        newRainBill.setDate(date);
        newRainBill.setTime(new Moment().toSimpleTime());
        newRainBill.setCost(report.getBillingOrder());
        newRainBill.setAmount(amount);
        newRainBill.setIp(ip);

        boolean added = billDao.add(newRainBill);
        if (added) {

            if (wheelIp == null) {
                wheelIp = new ActivityRebateWheelIp();
                wheelIp.setIp(ip);
                wheelIp.setUserId(userId);
                ipDao.add(wheelIp);
            }

            // 加钱
            uDao.updateLotteryMoney(userId, amount);
            User user = uDao.getById(userId);
            uBillService.addActivityBill(user, Global.BILL_ACCOUNT_LOTTERY, amount, newRainBill.getId(), "幸运大转盘活动");

            return amount;
        }
        else {
            UserVO user = dataFactory.getUser(userId);
            String username = user == null ? "未知" : user.getUsername();
            log.error("用户" + username + "消费"+report.getBillingOrder()+"参与幸运大转盘活动失败，添加账单失败,请检查");
            json.set(2, "2-4014");
            return null;
        }
    }

    private double randomAmount(double billing, ActivityRebateWheelConfigVO config) {
        if (config == null || config.getStatus() != 0) {
            return 0;
        }

        List<ActivityRebateWheelRule> rules = config.getRules();

        for (ActivityRebateWheelRule rule : rules) {
            if (billing >= rule.getMinCost() && (rule.getMaxCost() <= -1 || billing <= rule.getMaxCost())) {

                Random rangeAmountRandom = new Random();
                int index = rangeAmountRandom.nextInt(rule.getAmounts().length);
                double amount = rule.getAmounts()[index];
                if (amount > 0) {
                    return amount;
                }
            }
        }

        return 0;
    }
}
