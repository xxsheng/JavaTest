package lottery.domains.content.biz.impl;

import javautils.date.Moment;
import lottery.domains.content.biz.ActivityFirstRechargeBillService;
import lottery.domains.content.biz.UserBillService;
import lottery.domains.content.biz.UserSysMessageService;
import lottery.domains.content.dao.ActivityFirstRechargeBillDao;
import lottery.domains.content.dao.UserDao;
import lottery.domains.content.entity.ActivityFirstRechargeBill;
import lottery.domains.content.entity.User;
import lottery.domains.content.global.Global;
import lottery.domains.content.vo.activity.ActivityFirstRechargeConfigRule;
import lottery.domains.content.vo.activity.ActivityFirstRechargeConfigVO;
import lottery.domains.pool.DataFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Created by Nick on 2017/3/17.
 */
@Service
public class ActivityFirstRechargeBillServiceImpl implements ActivityFirstRechargeBillService {
    private static final Logger log = LoggerFactory.getLogger(ActivityFirstRechargeBillServiceImpl.class);
    @Autowired
    private ActivityFirstRechargeBillDao billDao;
    @Autowired
    private DataFactory dataFactory;
    @Autowired
    private UserBillService uBillService;
    @Autowired
    private UserSysMessageService uSysMessageService;
    @Autowired
    private UserDao uDao;

    @Override
    public boolean add(ActivityFirstRechargeBill bill) {
        return billDao.add(bill);
    }

    @Override
    @Transactional(readOnly = true)
    public ActivityFirstRechargeBill getByUserIdAndDate(int userId, String date) {
        return billDao.getByUserIdAndDate(userId, date);
    }

    @Override
    public double tryCollect(int userId, double rechargeAmount, String ip) {
        ActivityFirstRechargeConfigVO config = dataFactory.getActivityFirstRechargeConfig();
        if (config == null || config.getStatus() != 1) {
            // 活动未开启
            return 0;
        }

        String date = new Moment().toSimpleDate();

        // // 查看IP今天是否已经赠送了，不能使用IP，因为微信充值是异步的
        // ActivityFirstRechargeBill ipBill = billDao.getByDateAndIp(date, ip);
        // if (ipBill != null) {
        //     log.debug("用户充值不享受首冲活动，因为该IP今天已经赠送过了；日期{}；IP{}；充值金额{}；用户ID{}", date, ip, rechargeAmount, userId);
        //     return 0;
        // }

        // 查看用户今天是否已经赠送了
        ActivityFirstRechargeBill userBill = billDao.getByUserIdAndDate(userId, date);
        if (userBill != null) {
            log.debug("用户充值不享受首冲活动，因为该用户今天已经赠送过了；日期{}；IP{}；充值金额{}；用户ID{}", date, ip, rechargeAmount, userId);
            return 0;
        }

        double amount = chooseAmount(rechargeAmount);
        if (amount <= 0) {
            log.debug("用户充值不享受首冲活动，因为未挑选到金额；日期{}；IP{}；充值金额{}；用户ID{}", date, ip, rechargeAmount, userId);
            return 0;
        }

        // 赠送成功,添加账单
        ActivityFirstRechargeBill firstRechargeBill = new ActivityFirstRechargeBill();
        firstRechargeBill.setUserId(userId);
        firstRechargeBill.setDate(date);
        firstRechargeBill.setTime(new Moment().toSimpleTime());
        firstRechargeBill.setRecharge(rechargeAmount);
        firstRechargeBill.setAmount(amount);
        firstRechargeBill.setIp(ip);

        boolean added = billDao.add(firstRechargeBill);
        if (added) {

            // 生成账单
            User user = uDao.getById(userId);
            boolean addedBill = uBillService.addActivityBill(user, Global.BILL_ACCOUNT_LOTTERY, amount, firstRechargeBill.getId(), "首充活动");
            if (addedBill) {
                // 加钱
                uDao.updateLotteryMoney(userId, amount);

                // 发送系统消息
                uSysMessageService.addFirstRecharge(userId, rechargeAmount, amount);
            }
            else {
                log.debug("用户充值不享受首冲活动，因为用户账单生成失败；日期{}；IP{}；充值金额{}；用户ID{}", date, ip, rechargeAmount, userId);
            }
            return amount;
        }
        else {
            log.debug("用户充值不享受首冲活动，因为首冲账单生成失败；日期{}；IP{}；充值金额{}；用户ID{}", date, ip, rechargeAmount, userId);
        }

        return 0;
    }

    private double chooseAmount(double rechargeAmount) {
        ActivityFirstRechargeConfigVO config = dataFactory.getActivityFirstRechargeConfig();
        if (config == null || config.getStatus() != 1) {
            return 0;
        }

        List<ActivityFirstRechargeConfigRule> rules = config.getRuleVOs();

        for (ActivityFirstRechargeConfigRule rule : rules) {
            if (rechargeAmount >= rule.getMinRecharge() && (rule.getMaxRecharge() <= -1 || rechargeAmount <= rule.getMaxRecharge())) {
                return rule.getAmount();
            }
        }

        return 0;
    }
}
