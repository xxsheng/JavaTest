package lottery.domains.content.biz.impl;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javautils.StringUtil;
import javautils.date.Moment;
import javautils.jdbc.PageList;
import lottery.domains.content.biz.ActivityFirstRechargeBillService;
import lottery.domains.content.biz.UserBillService;
import lottery.domains.content.biz.UserSysMessageService;
import lottery.domains.content.dao.ActivityFirstRechargeBillDao;
import lottery.domains.content.dao.UserDao;
import lottery.domains.content.entity.ActivityFirstRechargeBill;
import lottery.domains.content.entity.User;
import lottery.domains.content.entity.activity.ActivityFirstRechargeConfigRule;
import lottery.domains.content.entity.activity.ActivityFirstRechargeConfigVO;
import lottery.domains.content.global.Global;
import lottery.domains.content.vo.activity.ActivityFirstRechargeBillVO;
import lottery.domains.pool.LotteryDataFactory;

/**
 * Created by Nick on 2017/3/17.
 */
@Service
public class ActivityFirstRechargeBillServiceImpl implements ActivityFirstRechargeBillService {
    @Autowired
    private ActivityFirstRechargeBillDao billDao;
    
    @Autowired
    private UserBillService uBillService;

    @Autowired
    private UserDao uDao;

    @Autowired
    private LotteryDataFactory dataFactory;
    
    @Autowired
    private UserSysMessageService uSysMessageService;
    
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

    @Override
    public PageList find(String username, String sDate, String eDate, String ip, int start, int limit) {
        List<Criterion> criterions = new ArrayList<>();
        List<Order> orders = new ArrayList<>();
        if(StringUtil.isNotNull(username)) {
            User user = uDao.getByUsername(username);
            if(user != null) {
                criterions.add(Restrictions.eq("userId", user.getId()));
            }
            else {
                return new PageList();
            }
        }
        if(StringUtil.isNotNull(sDate)) {
            criterions.add(Restrictions.ge("date", sDate));
        }
        if(StringUtil.isNotNull(eDate)) {
            criterions.add(Restrictions.lt("date", eDate));
        }
        if(StringUtil.isNotNull(ip)) {
            criterions.add(Restrictions.eq("ip", ip));
        }
        orders.add(Order.desc("time"));
        orders.add(Order.desc("id"));
        List<ActivityFirstRechargeBillVO> list = new ArrayList<>();
        PageList pList = billDao.find(criterions, orders, start, limit);
        for (Object tmpBean : pList.getList()) {
            ActivityFirstRechargeBillVO tmpVO = new ActivityFirstRechargeBillVO((ActivityFirstRechargeBill) tmpBean, dataFactory);
            list.add(tmpVO);
        }
        pList.setList(list);
        return pList;
    }

    @Override
    public double sumAmount(String username, String sDate, String eDate, String ip) {
        Integer userId = null;
        if(StringUtil.isNotNull(username)) {
            User user = uDao.getByUsername(username);
            if(user != null) {
                userId = user.getId();
            }
        }
        return billDao.sumAmount(userId, sDate, eDate, ip);
    }

	@Override
	public double tryCollect(int userId, double rechargeAmount, String ip) {
        ActivityFirstRechargeConfigVO config = dataFactory.getActivityFirstRechargeConfig();
        if (config == null || config.getStatus() != 1) {
            // 活动未开启
            return 0;
        }

        String date = new Moment().toSimpleDate();

        //查看IP今天是否已经赠送了
         ActivityFirstRechargeBill ipBill = billDao.getByDateAndIp(date, ip);
         if (ipBill != null) {
            return 0;
         }

        // 查看用户今天是否已经赠送了
        ActivityFirstRechargeBill userBill = billDao.getByUserIdAndDate(userId, date);
        if (userBill != null) {
            return 0;
        }

        double amount = chooseAmount(rechargeAmount);
        if (amount <= 0) {
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
          
            return amount;
        }
      

        return 0;
	}
}
