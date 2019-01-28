package lottery.domains.content.biz.impl;

import javautils.date.Moment;
import lottery.domains.content.biz.ActivityRedPacketRainBillService;
import lottery.domains.content.biz.UserBillService;
import lottery.domains.content.dao.ActivityRedPacketRainBillDao;
import lottery.domains.content.dao.ActivityRedPacketRainIpDao;
import lottery.domains.content.dao.UserDao;
import lottery.domains.content.dao.UserLotteryReportDao;
import lottery.domains.content.entity.*;
import lottery.domains.content.global.Global;
import lottery.domains.content.vo.activity.ActivityRedPacketRainConfigProbability;
import lottery.domains.content.vo.activity.ActivityRedPacketRainConfigRule;
import lottery.domains.content.vo.activity.ActivityRedPacketRainConfigVO;
import lottery.domains.pool.DataFactory;
import lottery.web.WebJSON;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by Nick on 2017/3/17.
 */
@Service
public class ActivityRedPacketRainBillServiceImpl implements ActivityRedPacketRainBillService {
    private static final Logger log = LoggerFactory.getLogger(ActivityRedPacketRainBillServiceImpl.class);
    @Autowired
    private ActivityRedPacketRainBillDao billDao;
    @Autowired
    private ActivityRedPacketRainIpDao rainIpDao;
    @Autowired
    private UserLotteryReportDao reportDao;
    @Autowired
    private DataFactory dataFactory;
    @Autowired
    private UserBillService uBillService;
    @Autowired
    private UserDao uDao;

    @Override
    public boolean add(ActivityRedPacketRainBill bill) {
        return billDao.add(bill);
    }

    @Override
    @Transactional(readOnly = true)
    public ActivityRedPacketRainBill getByUserIdAndDateAndHour(int userId, String date, String hour) {
        return billDao.getByUserIdAndDateAndHour(userId, date, hour);
    }

    @Override
    public Double collect(WebJSON json, int userId, String ip) {
        ActivityRedPacketRainConfigVO config = dataFactory.getActivityRedPacketRainConfig();
        if (config == null || config.getStatus() != 1) {
            // 活动未开启
            json.set(2, "2-4001");
            return null;
        }

        // 查看是否是同IP
        ActivityRedPacketRainIp rainIp = rainIpDao.getByIp(ip);
        if (rainIp != null && rainIp.getUserId() != userId) {
            // 同IP不同用户
            json.set(2, "2-4011");
            return null;
        }

        // 获取时间
        ActivityRedPacketRainTime time = dataFactory.getCurrentActivityRedPacketRainTime();
        if (time == null) {
            // 当前未到领取时间,请等待下一轮!
            json.set(2, "2-4027");
            return null;
        }

        // 查看该时间段是否已经领取了
        ActivityRedPacketRainBill rainBill = billDao.getByUserIdAndDateAndHour(userId, time.getDate(), time.getHour());
        if (rainBill != null) {
            // 您本轮已领取过该奖励,请等待下一轮!
            log.error("用户" + userId + "已经领取过奖励,尝试再次领取,请检查");
            json.set(2, "2-4028");
            return null;
        }

        // 获取消费量
        UserLotteryReport report = reportDao.get(userId, time.getDate());
        if (report == null || report.getBillingOrder() < config.getMinCost()) {
            // 抢红包最低消费要求为%s元，您还未达到最低消费！
            json.set(2, "2-4009", config.getMinCost());
            return null;
        }

        // 根据消费量获取对应随机范围规则
        List<ActivityRedPacketRainConfigProbability> probabilities = getProbability(report.getBillingOrder());
        if (CollectionUtils.isEmpty(probabilities)) {
            // 没有获取到红包规则,领取失败
            log.error("用户" + userId + "消费"+report.getBillingOrder()+"没有获取到红包雨规则,领取失败,请检查");
            json.set(2, "2-4014");
            return null;
        }

        // 根据机率获取金额
        double amount = randomAmount(probabilities);
        if (amount <= 0) {
            // 没有获取到红包规则,领取失败
            log.error("用户" + userId + "消费"+report.getBillingOrder()+"领取红包雨失败,随机金额为0,请检查");
            json.set(2, "2-4014");
            return null;
        }

        // 领取成功,添加账单
        ActivityRedPacketRainBill newRainBill = new ActivityRedPacketRainBill();
        newRainBill.setHour(time.getHour());
        newRainBill.setDate(time.getDate());
        newRainBill.setTime(new Moment().toSimpleTime());
        newRainBill.setCost(report.getBillingOrder());
        newRainBill.setAmount(amount);
        newRainBill.setIp(ip);
        newRainBill.setUserId(userId);

        boolean added = billDao.add(newRainBill);
        if (added) {

            if (rainIp == null) {
                rainIp = new ActivityRedPacketRainIp();
                rainIp.setIp(ip);
                rainIp.setUserId(userId);
                rainIpDao.add(rainIp);
            }

            // 加钱
            uDao.updateLotteryMoney(userId, amount);
            // 生成账单
            User user = uDao.getById(userId);
            uBillService.addActivityBill(user, Global.BILL_ACCOUNT_LOTTERY, amount, newRainBill.getId(), "红包雨活动");

            return amount;
        }
        else {
            log.error("用户" + userId + "消费"+report.getBillingOrder()+"领取红包雨失败,添加账单失败,请检查");
            json.set(2, "2-4014");
            return null;
        }
    }

    private List<ActivityRedPacketRainConfigProbability> getProbability(double billing) {
        ActivityRedPacketRainConfigVO config = dataFactory.getActivityRedPacketRainConfig();
        if (config == null || config.getStatus() != 1) {
            return null;
        }

        List<ActivityRedPacketRainConfigRule> rules = config.getRuleVOs();

        for (ActivityRedPacketRainConfigRule rule : rules) {
            if (billing >= rule.getMinCost() && (rule.getMaxCost() <= -1 || billing <= rule.getMaxCost())) {
                return rule.getProbabilities();
            }
        }

        return null;
    }

    private double randomAmount(List<ActivityRedPacketRainConfigProbability> probabilities) {
        List<Double> amounts = new ArrayList<>();
        for (ActivityRedPacketRainConfigProbability probability : probabilities) {
            String strAmount = probability.getAmount().trim();
            if (strAmount.indexOf("-") > -1) {
                String[] split = strAmount.split("-");

                int from = Integer.valueOf(split[0]);
                int to = Integer.valueOf(split[1]);

                List<String> intervalAmount = new ArrayList<>();
                for (int i = from; i <= to; i++) {
                    intervalAmount.add(i+"");
                }

                Random rangeAmountRandom = new Random();
                int index = rangeAmountRandom.nextInt(intervalAmount.size());
                strAmount = intervalAmount.get(index);
            }

            double amount = Double.valueOf(strAmount);

            int times = BigDecimal.valueOf(probability.getProbability() * 100).intValue();

            for (int i = 0; i < times; i++) {
                amounts.add(amount);
            }
        }

        Random random = new Random();
        int index = random.nextInt(amounts.size());
        return amounts.get(index);
    }
}
