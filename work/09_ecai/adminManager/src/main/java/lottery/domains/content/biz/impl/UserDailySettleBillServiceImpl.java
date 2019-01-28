package lottery.domains.content.biz.impl;

import javautils.StringUtil;
import javautils.jdbc.PageList;
import javautils.math.MathUtil;
import lottery.domains.content.biz.UserBillService;
import lottery.domains.content.biz.UserDailySettleBillService;
import lottery.domains.content.dao.UserDailySettleBillDao;
import lottery.domains.content.dao.UserDailySettleDao;
import lottery.domains.content.dao.UserDao;
import lottery.domains.content.entity.User;
import lottery.domains.content.entity.UserDailySettleBill;
import lottery.domains.content.global.Global;
import lottery.domains.content.vo.user.UserDailySettleBillVO;
import lottery.domains.content.vo.user.UserVO;
import lottery.domains.pool.LotteryDataFactory;
import org.apache.commons.collections.CollectionUtils;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 契约日结账单Service
 * Created by Nick on 2016/10/31.
 */
@Service
public class UserDailySettleBillServiceImpl implements UserDailySettleBillService {
    @Autowired
    private UserBillService uBillService;
    @Autowired
    private UserDailySettleBillDao dailySettleBillDao;
    @Autowired
    private UserDailySettleDao uDailySettleDao;
    @Autowired
    private UserDao uDao;
    @Autowired
    private LotteryDataFactory dataFactory;


    @Override
    public PageList search(List<Integer> userIds, String sTime, String eTime, Double minUserAmount,
                           Double maxUserAmount, Integer status, int start, int limit) {
        start = start < 0 ? 0 : start;
        limit = limit < 0 ? 0 : limit;
        limit = limit > 20 ? 20 : limit;
        // 查询条件
        List<Criterion> criterions = new ArrayList<>();

        if (CollectionUtils.isNotEmpty(userIds)) {
            criterions.add(Restrictions.in("userId", userIds));
        }

        if (StringUtil.isNotNull(sTime)) {
            criterions.add(Restrictions.ge("indicateDate", sTime));
        }
        if (StringUtil.isNotNull(eTime)) {
            criterions.add(Restrictions.lt("indicateDate", eTime));
        }

        if (minUserAmount != null) {
            criterions.add(Restrictions.ge("userAmount", minUserAmount));
        }
        if (maxUserAmount != null) {
            criterions.add(Restrictions.le("userAmount", maxUserAmount));
        }

        if (status != null) {
            criterions.add(Restrictions.eq("status", status));
        }

        // 排序条件
        List<Order> orders = new ArrayList<>();
        orders.add(Order.desc("id"));
        PageList pList = dailySettleBillDao.search(criterions, orders, start, limit);
        List<UserDailySettleBillVO> convertList = new ArrayList<>();
        if(pList != null && pList.getList() != null){
            for (Object tmpBean : pList.getList()) {
                convertList.add(new UserDailySettleBillVO((UserDailySettleBill) tmpBean, dataFactory));
            }
        }
        pList.setList(convertList);
        return pList;
    }

    @Override
    public List<UserDailySettleBill> findByCriteria(List<Criterion> criterions, List<Order> orders) {
        return dailySettleBillDao.findByCriteria(criterions, orders);
    }

    @Override
    public List<UserDailySettleBill> getDirectLowerBills(int userId, String indicateDate, Integer[] status, Integer issueType) {
        List<User> userLowers = uDao.getUserDirectLower(userId);
        if (CollectionUtils.isEmpty(userLowers)) {
            return new ArrayList<>();
        }

        List<Integer> userIds = new ArrayList<>();

        List<Criterion> criterions = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(userLowers)) {
            for (User userLower : userLowers) {
                userIds.add(userLower.getId());
            }

            criterions.add(Restrictions.in("userId", userIds));
        }
        if (status != null && status.length > 0) {
            criterions.add(Restrictions.in("status", Arrays.asList(status)));
        }
        if (issueType != null) {
            criterions.add(Restrictions.eq("issueType", issueType));
        }

        if (StringUtil.isNotNull(indicateDate)) {
            criterions.add(Restrictions.eq("indicateDate", indicateDate));
        }

        return dailySettleBillDao.findByCriteria(criterions, null);
    }

    @Override
    public boolean add(UserDailySettleBill settleBill) {

        // 先增加数据
        boolean added = dailySettleBillDao.add(settleBill);
        if (!added) {
            return added;
        }

        // 更新总日结金额
        uDailySettleDao.updateTotalAmount(settleBill.getUserId(), settleBill.getUserAmount());

        return added;
    }

    @Override
    public boolean update(UserDailySettleBill settleBill) {
        return dailySettleBillDao.update(settleBill);
    }

    @Override
    public synchronized UserDailySettleBill issue(int id) {
        UserDailySettleBill upperBill = dailySettleBillDao.getById(id);
        if (upperBill == null || upperBill.getStatus() != Global.DAILY_SETTLE_BILL_INSUFFICIENT) {
            // 账单不是余额不足状态，不发放
            return upperBill;
        }

        // 所有下级部分领取或余额不足账单
        Integer[] status = new Integer[]{Global.DAILY_SETTLE_BILL_PART_RECEIVED, Global.DAILY_SETTLE_BILL_INSUFFICIENT};
        List<UserDailySettleBill> lowerBills = getDirectLowerBills(upperBill.getUserId(), upperBill.getIndicateDate(), status, Global.DAILY_SETTLE_ISSUE_TYPE_UPPER);

        if (CollectionUtils.isEmpty(lowerBills)) {
            // 没有下级账单，直接修改为已发放
            upperBill.setRemarks("已发放");
            upperBill.setStatus(Global.DAILY_SETTLE_BILL_ISSUED);
            update(upperBill);
            return upperBill;
        }

        double upperBillMoney = 0; // 目前上级日结账单中可供发放的金额
        if (upperBill.getIssueType() == Global.DAILY_SETTLE_ISSUE_TYPE_UPPER) {
            upperBillMoney = upperBill.getAvailableAmount();
            upperBill.setAvailableAmount(0);
        }

        double upperThisTimePaid = 0; // 本次共计发放
        for (UserDailySettleBill lowerBill : lowerBills) {
            // 下级总共需要领取的钱
            double lowerAmount = lowerBill.getCalAmount();
            // 下级还剩余多少没有领取
            double lowerRemainReceived = MathUtil.subtract(lowerAmount, lowerBill.getTotalReceived());

            double billGive = 0; // 上级的账单中可以给他发放多少钱
            // 先从上级的账单金额中扣除
            if (upperBillMoney > 0 && lowerRemainReceived > 0) {
                billGive = lowerAmount >= upperBillMoney ? upperBillMoney : lowerAmount;
                upperBillMoney = MathUtil.subtract(upperBillMoney, billGive);
                lowerRemainReceived = MathUtil.subtract(lowerRemainReceived, billGive);
            }

            // 下级还没有领完且上级的账单金额也扣完了，再看从上级的主账户中扣
            double totalMoneyGive = 0;
            User upperUser = uDao.getById(upperBill.getUserId());
            if (lowerRemainReceived > 0 && upperUser.getTotalMoney() > 0) {
                totalMoneyGive = lowerRemainReceived >= upperUser.getTotalMoney() ? upperUser.getTotalMoney() : lowerRemainReceived;
                lowerRemainReceived = MathUtil.subtract(lowerRemainReceived, totalMoneyGive);
            }

            // 下级还没有领完且上级的主账户金额也扣完了，再看从上级的彩票账户中扣
            double lotteryMoneyGive = 0;
            if (lowerRemainReceived > 0 && upperUser.getLotteryMoney() > 0) {
                lotteryMoneyGive = lowerRemainReceived >= upperUser.getLotteryMoney() ? upperUser.getLotteryMoney() : lowerRemainReceived;
                lowerRemainReceived = MathUtil.subtract(lowerRemainReceived, lotteryMoneyGive);
            }

            double totalGive = MathUtil.add(MathUtil.add(billGive, totalMoneyGive), lotteryMoneyGive);
            if (totalGive > 0) {
                if (totalMoneyGive > 0) {
                    UserVO subUser = dataFactory.getUser(lowerBill.getUserId());
                    uDao.updateTotalMoney(upperUser.getId(), -totalMoneyGive); // 扣钱不需要加报表
                    uBillService.addDailySettleBill(upperUser, Global.BILL_ACCOUNT_MAIN, -totalMoneyGive, "系统自动扣发" + totalMoneyGive + "日结金额到" + subUser.getUsername(), false);
                }
                if (lotteryMoneyGive > 0) {
                    UserVO subUser = dataFactory.getUser(lowerBill.getUserId());
                    uDao.updateLotteryMoney(upperUser.getId(), -lotteryMoneyGive); // 扣钱不需要加报表
                    uBillService.addDailySettleBill(upperUser, Global.BILL_ACCOUNT_LOTTERY, -lotteryMoneyGive, "系统自动扣发" + lotteryMoneyGive + "日结金额到" + subUser.getUsername(), false);
                }

                upperThisTimePaid = MathUtil.add(upperThisTimePaid, totalGive); // 增加上级本次已派发金额

                lowerBill.setTotalReceived(MathUtil.add(lowerBill.getTotalReceived(), totalGive)); // 增加下级的已领取金额

                if (lowerBill.getStatus() == Global.DAILY_SETTLE_BILL_PART_RECEIVED) {
                    // 如果下级是部分领取状态，那么直接加钱给下级
                    User lowerUser = uDao.getById(lowerBill.getUserId()); // 加钱需要加报表
                    boolean addedBill = uBillService.addDailySettleBill(lowerUser, Global.BILL_ACCOUNT_LOTTERY, totalGive, "系统自动从上级账户中扣发日结金额", true);
                    if (addedBill) {
                        // 如果下级已经领取完了，账单结束
                        if (lowerRemainReceived <= 0) {
                            // 修改契约账单，账单结束
                            lowerBill.setRemarks("已发放");
                            lowerBill.setStatus(Global.DAILY_SETTLE_BILL_ISSUED);
                        }

                        uDao.updateLotteryMoney(lowerBill.getUserId(), totalGive);
                    }

                }
                else {
                    // 否则的话加到用户的账单上
                    lowerBill.setAvailableAmount(totalGive);
                }

                // 修改下级账单
                dailySettleBillDao.update(lowerBill);
            }
        }

        // 增加上级的已派发金额
        upperBill.setLowerPaidAmount(MathUtil.add(upperBill.getLowerPaidAmount(), upperThisTimePaid));

        if (upperBillMoney > 0) {
            // 已经发完了，账上还有剩余钱，把钱加到上级彩票账户中，账单结束
            User upperUser = uDao.getById(upperBill.getUserId());
            if (upperUser != null) {
                // 新增系统账单
                // 加钱需要加报表
                boolean addedBill = uBillService.addDailySettleBill(upperUser, Global.BILL_ACCOUNT_LOTTERY, upperBillMoney, "系统自动从上级账户中扣发日结金额", true);
                if (addedBill) {

                    // 修改契约账单，账单结束
                    upperBill.setRemarks("已发放");
                    upperBill.setStatus(Global.DAILY_SETTLE_BILL_ISSUED);
                    dailySettleBillDao.update(upperBill);

                    // 加钱
                    uDao.updateLotteryMoney(upperBill.getUserId(), upperBillMoney);
                }
            }
        }
        else {
            double notYetPay = MathUtil.subtract(upperBill.getLowerTotalAmount(), upperBill.getLowerPaidAmount());
            if (notYetPay > 0) {
                // 还没有发完，还是余额不足
                dailySettleBillDao.update(upperBill);
            }
            else {
                // 已发放，账单结束
                upperBill.setRemarks("已发放");
                upperBill.setStatus(Global.DAILY_SETTLE_BILL_ISSUED);
                dailySettleBillDao.update(upperBill);
            }
        }

        return upperBill;
    }
}
