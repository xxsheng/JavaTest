package lottery.domains.content.biz.impl;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import admin.web.WebJSONObject;
import javautils.StringUtil;
import javautils.array.ArrayUtils;
import javautils.jdbc.PageList;
import javautils.math.MathUtil;
import lottery.domains.content.biz.UserBillService;
import lottery.domains.content.biz.UserDividendBillService;
import lottery.domains.content.biz.UserSysMessageService;
import lottery.domains.content.dao.UserDao;
import lottery.domains.content.dao.UserDividendBillDao;
import lottery.domains.content.entity.User;
import lottery.domains.content.entity.UserDividendBill;
import lottery.domains.content.global.Global;
import lottery.domains.content.vo.user.UserDividendBillVO;
import lottery.domains.content.vo.user.UserVO;
import lottery.domains.pool.LotteryDataFactory;
import lottery.web.content.utils.UserCodePointUtil;

/**
 * 契约分红账单Service
 * Created by Nick on 2016/10/31.
 */
@Service
public class UserDividendBillServiceImpl implements UserDividendBillService {
    @Autowired
    private UserDividendBillDao uDividendBillDao;
    @Autowired
    private LotteryDataFactory dataFactory;

    @Autowired
    private UserDao uDao;
    @Autowired
    private UserBillService uBillService;

    @Autowired
    private UserCodePointUtil uCodePointUtil;

    @Autowired
    private UserSysMessageService uSysMessageService;


    @Override
    public PageList search(List<Integer> userIds, String sTime, String eTime, Double minUserAmount,
                           Double maxUserAmount, Integer status, Integer issueType, int start, int limit) {
        start = start < 0 ? 0 : start;
        limit = limit < 0 ? 0 : limit;
        limit = limit > 20 ? 20 : limit;
        // 查询条件
        List<Criterion> criterions = new ArrayList<>();

        if (CollectionUtils.isNotEmpty(userIds)) {
            criterions.add(Restrictions.in("userId", userIds));
        }

        if (StringUtil.isNotNull(sTime)) {
            criterions.add(Restrictions.ge("indicateStartDate", sTime));
        }
        if (StringUtil.isNotNull(eTime)) {
            criterions.add(Restrictions.le("indicateEndDate", eTime));
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
        if (issueType != null) {
            criterions.add(Restrictions.eq("issueType", issueType));
        }

        // 排序条件
        List<Order> orders = new ArrayList<>();
        orders.add(Order.desc("id"));
        PageList pList = uDividendBillDao.search(criterions, orders, start, limit);
        List<UserDividendBillVO> voList = new ArrayList<>();
        if(pList != null && pList.getList() != null){
            for (Object tmpBean : pList.getList()) {
                voList.add(new UserDividendBillVO((UserDividendBill) tmpBean, dataFactory));
            }
        }

        // // 设置用户层级
        // voList = convertUserLevels(voList);

        pList.setList(voList);
        return pList;
    }

    @Override
    public PageList searchPlatformLoss(List<Integer> userIds, String sTime, String eTime, Double minUserAmount, Double maxUserAmount, int start, int limit) {
        start = start < 0 ? 0 : start;
        limit = limit < 0 ? 0 : limit;
        limit = limit > 20 ? 20 : limit;
        // 查询条件
        List<Criterion> criterions = new ArrayList<>();

        if (CollectionUtils.isNotEmpty(userIds)) {
            criterions.add(Restrictions.in("userId", userIds));
        }

        if (StringUtil.isNotNull(sTime)) {
            criterions.add(Restrictions.ge("indicateStartDate", sTime));
        }
        if (StringUtil.isNotNull(eTime)) {
            criterions.add(Restrictions.le("indicateEndDate", eTime));
        }

        if (minUserAmount != null) {
            criterions.add(Restrictions.ge("userAmount", minUserAmount));
        }
        if (maxUserAmount != null) {
            criterions.add(Restrictions.le("userAmount", maxUserAmount));
        }

        // 平台发放
        criterions.add(Restrictions.eq("issueType", Global.DIVIDEND_ISSUE_TYPE_PLATFORM));
        // 亏损小于0
        criterions.add(Restrictions.lt("totalLoss", 0d));
        // 亏损小于0
        List<Integer> status = new ArrayList<>();
        status.add(Global.DIVIDEND_BILL_ISSUED);
        status.add(Global.DIVIDEND_BILL_UNAPPROVE);
        status.add(Global.DIVIDEND_BILL_UNCOLLECT);
        status.add(Global.DIVIDEND_BILL_INSUFFICIENT);
        criterions.add(Restrictions.in("status", status));

        // 排序条件
        List<Order> orders = new ArrayList<>();
        orders.add(Order.desc("id"));
        PageList pList = uDividendBillDao.search(criterions, orders, start, limit);
        List<UserDividendBillVO> voList = new ArrayList<>();
        if(pList != null && pList.getList() != null){
            for (Object tmpBean : pList.getList()) {
                voList.add(new UserDividendBillVO((UserDividendBill) tmpBean, dataFactory));
            }
        }

        // 设置用户层级
        voList = convertUserLevels(voList);

        pList.setList(voList);
        return pList;
    }

    private List<UserDividendBillVO> convertUserLevels(List<UserDividendBillVO> voList) {
        Set<Integer> userIds = new HashSet<>();
        for (UserDividendBillVO userDividendBillVO : voList) {
            userIds.add(userDividendBillVO.getBean().getUserId());
        }

        List<Criterion> criterions = new ArrayList<>();
        criterions.add(Restrictions.in("id", userIds));

        List<User> users = uDao.list(criterions, null);

        for (UserDividendBillVO userDividendBillVO : voList) {
            for (User user : users) {
                if (userDividendBillVO.getBean().getUserId() == user.getId()) {
                    int[] upIds = ArrayUtils.transGetIds(user.getUpids());
                    for (int upId : upIds) {
                        UserVO upUser = dataFactory.getUser(upId);
                        if (upUser != null) {
                            userDividendBillVO.getUserLevels().add(upUser.getUsername());
                        }
                    }
                }
            }
        }

        return voList;
    }

    @Override
    public double[] sumUserAmount(List<Integer> userIds, String sTime, String eTime, Double minUserAmount, Double maxUserAmount) {
        return uDividendBillDao.sumUserAmount(userIds, sTime, eTime, minUserAmount, maxUserAmount);
    }

    @Override
    public List<UserDividendBill> findByCriteria(List<Criterion> criterions, List<Order> orders) {
        return uDividendBillDao.findByCriteria(criterions, orders);
    }

    @Override
    public boolean updateAllExpire() {
        return uDividendBillDao.updateAllExpire();
    }

    @Override
    public List<UserDividendBill> getLowerBills(int userId, String sTime, String eTime) {
        List<User> userLowers = uDao.getUserLower(userId);
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
        if (CollectionUtils.isEmpty(userIds)) {
            return new ArrayList<>();
        }

        if (StringUtil.isNotNull(sTime)) {
            criterions.add(Restrictions.ge("indicateStartDate", sTime));
        }
        if (StringUtil.isNotNull(eTime)) {
            criterions.add(Restrictions.le("indicateEndDate", eTime));
        }

        return uDividendBillDao.findByCriteria(criterions, null);
    }

    @Override
    public List<UserDividendBill> getDirectLowerBills(int userId, String sTime, String eTime) {
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
        if (CollectionUtils.isEmpty(userIds)) {
            return new ArrayList<>();
        }

        if (StringUtil.isNotNull(sTime)) {
            criterions.add(Restrictions.ge("indicateStartDate", sTime));
        }
        if (StringUtil.isNotNull(eTime)) {
            criterions.add(Restrictions.le("indicateEndDate", eTime));
        }

        return uDividendBillDao.findByCriteria(criterions, null);
    }

    @Override
    public UserDividendBill getById(int id) {
        return uDividendBillDao.getById(id);
    }

    @Override
    public UserDividendBill getBill(int userId, String sTime, String eTime) {
        List<Criterion> criterions = new ArrayList<>();
        criterions.add(Restrictions.eq("userId", userId));

        if (StringUtil.isNotNull(sTime)) {
            criterions.add(Restrictions.ge("indicateStartDate", sTime));
        }
        if (StringUtil.isNotNull(eTime)) {
            criterions.add(Restrictions.le("indicateEndDate", eTime));
        }

        List<Order> orders = new ArrayList<>();
        orders.add(Order.desc("id"));

        PageList pList = uDividendBillDao.search(criterions, orders, 0, 1);

        if(pList != null && CollectionUtils.isNotEmpty(pList.getList())){
            return (UserDividendBill) pList.getList().get(0);
        }
        return null;
    }

    @Override
    public boolean add(UserDividendBill dividendBill) {
        return uDividendBillDao.add(dividendBill);
    }

    @Override
    public boolean addAvailableMoney(int id, double money) {
        return uDividendBillDao.addAvailableMoney(id, money);
    }

    @Override
    public synchronized void issueInsufficient(int id) {
        // 处理余额不足的账单
        UserDividendBill dividendBill = getById(id);
        if (dividendBill.getStatus() != Global.DIVIDEND_BILL_INSUFFICIENT) {
            return;
        }

        if (dividendBill.getLowerTotalAmount() <= 0) {
            return;
        }

        double upperBillMoney = 0; // 目前分红账单可供发放的金额
        double upperLowerTotalAmount = dividendBill.getLowerTotalAmount(); // 需要向下级发放多少
        double upperLowerPaidAmount = dividendBill.getLowerPaidAmount(); // 已经向下级发放多少
        if (dividendBill.getIssueType() == Global.DIVIDEND_ISSUE_TYPE_PLATFORM) {
        }
        else {
            // 如果不是平台发放，那他可以发放的钱就是他待领取的钱
            upperBillMoney = dividendBill.getAvailableAmount();
        }

        double upperStillNotPay = MathUtil.subtract(upperLowerTotalAmount, upperLowerPaidAmount); // 还需要向下级发放多少
        if (upperStillNotPay <= 0) {
            // 已经不需要向下级发放了，
            if (upperBillMoney > 0) {
                uDividendBillDao.updateStatus(dividendBill.getId(), Global.DIVIDEND_BILL_UNCOLLECT);
            }
            else {
                uDividendBillDao.updateStatus(dividendBill.getId(), Global.DIVIDEND_BILL_ISSUED);
            }
            return;
        }

        // 向直属下级开始派发
        double upperThisTimePaid = 0;
        List<UserDividendBill> directLowerBills = getDirectLowerBills(dividendBill.getUserId(), dividendBill.getIndicateStartDate(), dividendBill.getIndicateEndDate());
        for (UserDividendBill directLowerBill : directLowerBills) {
            if (directLowerBill.getStatus() != Global.DIVIDEND_BILL_UNCOLLECT
                    && directLowerBill.getStatus() != Global.DIVIDEND_BILL_INSUFFICIENT
                    && directLowerBill.getStatus() != Global.DIVIDEND_BILL_PART_COLLECTED) {
                continue;
            }

            // 直属下级的钱
            double lowerCalAmount = directLowerBill.getUserAmount();
            if (directLowerBill.getStatus() == Global.DIVIDEND_BILL_INSUFFICIENT) {
                lowerCalAmount = MathUtil.add(lowerCalAmount, directLowerBill.getCalAmount());
            }
            if (lowerCalAmount <= 0) continue;

            // 直属下级已经领取的金额，表明之前已经给该下级发放的总额
            double lowerReceived = MathUtil.add(directLowerBill.getAvailableAmount(), directLowerBill.getTotalReceived());

            // 还有多少没有领取
            double lowerRemainReceived = MathUtil.subtract(lowerCalAmount, lowerReceived);

            // 如果只有0.1了，那忽略
            if (lowerRemainReceived <= 0.1) continue;

            double billGive = 0; // 上级的账单中可以给他发放多少钱
            // 先从上级的账单金额中扣除
            if (upperBillMoney > 0 && lowerRemainReceived > 0) {
                // 先查上级的账单可以发放多少
                billGive = lowerRemainReceived >= upperBillMoney ? upperBillMoney : lowerRemainReceived;
                upperBillMoney = MathUtil.subtract(upperBillMoney, billGive);
                lowerRemainReceived = MathUtil.subtract(lowerRemainReceived, billGive);
                upperStillNotPay = MathUtil.subtract(upperStillNotPay, billGive);
            }

            // 下级还没有领完且上级的账单金额也扣完了，再看从上级的主账户中扣
            double totalMoneyGive = 0;
            User upUser = uDao.getById(dividendBill.getUserId());
            if (lowerRemainReceived > 0 && upperStillNotPay > 0 && upUser.getTotalMoney() > 0) {
                totalMoneyGive = lowerRemainReceived >= upUser.getTotalMoney() ? upUser.getTotalMoney() : lowerRemainReceived;
                lowerRemainReceived = MathUtil.subtract(lowerRemainReceived, totalMoneyGive);
                upperStillNotPay = MathUtil.subtract(upperStillNotPay, totalMoneyGive);
            }

            // 下级还没有领完且上级的主账户金额也扣完了，再看从上级的彩票账户中扣
            double lotteryMoneyGive = 0;
            if (lowerRemainReceived > 0 && upperStillNotPay > 0 && upUser.getLotteryMoney() > 0) {
                lotteryMoneyGive = lowerRemainReceived >= upUser.getLotteryMoney() ? upUser.getLotteryMoney() : lowerRemainReceived;
                lowerRemainReceived = MathUtil.subtract(lowerRemainReceived, lotteryMoneyGive);
                upperStillNotPay = MathUtil.subtract(upperStillNotPay, lotteryMoneyGive);
            }

            // 本次上级总共派发多少
            double totalGive = MathUtil.add(MathUtil.add(billGive, totalMoneyGive), lotteryMoneyGive);
            if (totalGive <= 0) break; // 没有派出去钱，说明上级账户没钱了，中断不再处理

            if (totalMoneyGive > 0) {
                UserVO subUser = dataFactory.getUser(directLowerBill.getUserId());
                uDao.updateTotalMoney(upUser.getId(), -totalMoneyGive);
                uBillService.addDividendBill(upUser, Global.BILL_ACCOUNT_MAIN, -totalMoneyGive, "系统自动扣发" + totalMoneyGive + "分红金额到" + subUser.getUsername(), false);
            }
            if (lotteryMoneyGive > 0) {
                UserVO subUser = dataFactory.getUser(directLowerBill.getUserId());
                uDao.updateLotteryMoney(upUser.getId(), -lotteryMoneyGive);
                uBillService.addDividendBill(upUser, Global.BILL_ACCOUNT_LOTTERY, -lotteryMoneyGive, "系统自动扣发" + lotteryMoneyGive + "分红金额到" + subUser.getUsername(), false);
            }

            upperThisTimePaid = MathUtil.add(upperThisTimePaid, totalGive); // 增加上级本次已派发金额
            // 增加下级的待领取金额
            uDividendBillDao.addAvailableMoney(directLowerBill.getId(), totalGive); // 增加下级的可领取金额

            if (directLowerBill.getStatus() == Global.DIVIDEND_BILL_INSUFFICIENT) {
                uDividendBillDao.addTotalReceived(directLowerBill.getId(), totalGive);
            }
        }

        // 增加上级的已派发金额
        if (upperThisTimePaid > 0) {
            uDividendBillDao.addLowerPaidAmount(dividendBill.getId(), upperThisTimePaid);
        }

        if (dividendBill.getIssueType() == Global.DIVIDEND_ISSUE_TYPE_PLATFORM
                && upperStillNotPay <= 0.1) {
            // 如果是平台发放，并且已经给下级发完了，状态修改为完成
            uDividendBillDao.update(dividendBill.getId(), Global.DIVIDEND_BILL_ISSUED, "");
        }
        else if (dividendBill.getIssueType() == Global.DIVIDEND_ISSUE_TYPE_UPPER) {

            double upperRemains = MathUtil.subtract(dividendBill.getAvailableAmount(), upperThisTimePaid);

            if (upperRemains > 0) {
                // 在派发给下级完成之后，还有剩余，那么就是待领取，金额是账单中可领取的金额减去本次派发的金额
                uDividendBillDao.update(dividendBill.getId(), Global.DIVIDEND_BILL_UNCOLLECT, upperRemains, "");
            }
            else {
                // 否则，清空可领取金额，表示已经账单中可用金额已经发完了，状态不变，还是余额不足
                uDividendBillDao.setAvailableMoney(dividendBill.getId(), 0);
            }
        }
    }

    @Override
    public boolean agree(WebJSONObject json, int id ,String remarks) {
        // 查找契约
        UserDividendBill dividendBill = getById(id);

        if (dividendBill.getStatus() != Global.DIVIDEND_BILL_UNAPPROVE) {
            json.set(2, "2-3004");
            return false;
        }

        // 必须先审核上级
        if (dividendBill.getIssueType() == Global.DIVIDEND_ISSUE_TYPE_UPPER) {
            User user = uDao.getById(dividendBill.getUserId());

            UserDividendBill bill = getBill(user.getUpid(), dividendBill.getIndicateStartDate(), dividendBill.getIndicateEndDate());
            if (bill != null && bill.getStatus() == Global.DIVIDEND_BILL_UNAPPROVE) {
                UserVO upper = dataFactory.getUser(user.getUpid());
                json.setWithParams(2, "2-3003", upper == null ? "" : upper.getUsername());
                return false;
            }
        }

        // 需要给下级进行扣发
        boolean updated = false;
        if (dividendBill.getLowerTotalAmount() > 0) {
            // 扣发给下级
            double[] result = agreeProcess(id); // 只扣发给直属下级，不需要无限下级
            double stillNotPay = result[0];
            double availableAmount = result[1];
            double upperNotYetPay = result[2];
            stillNotPay -= 0.1; // 允许有0.1误差

            if (stillNotPay > 0) { // 在经过发放逻辑以后，还没有向下级支付完，那么就是余额不足了
                String _remarks = remarks;
                if (StringUtils.isEmpty(_remarks)) {
                    _remarks = "余额不足，请充值";
                }
                // 没有发完，余额不足
                updated = uDividendBillDao.update(id, Global.DIVIDEND_BILL_INSUFFICIENT, _remarks);
            }
            else{
                if (availableAmount <= 0) {
                    // 账上的钱足够发给下级，并且上级欠钱，部分发放
                    if (upperNotYetPay > 0) {
                        updated = uDividendBillDao.update(id, Global.DIVIDEND_BILL_PART_COLLECTED, remarks);
                    }
                    else {
                        // 账上的钱足够发给下级，并且上级不欠钱，已发放
                        updated = uDividendBillDao.update(id, Global.DIVIDEND_BILL_ISSUED, remarks);
                    }
                }
                else if (availableAmount > 0) {
                    // 账上的钱足够发给下级，待领取
                    updated = uDividendBillDao.update(id, Global.DIVIDEND_BILL_UNCOLLECT, remarks);
                }
            }
        }
        else {
            if (dividendBill.getIssueType() == Global.DIVIDEND_ISSUE_TYPE_PLATFORM) {
                uDividendBillDao.addAvailableMoney(id, dividendBill.getCalAmount()); // 平台发放直接增加可领取金额
            }
            updated = uDividendBillDao.update(id, Global.DIVIDEND_BILL_UNCOLLECT, remarks);
        }

        // 发放系统消息
        if (updated) {
            uSysMessageService.addDividendBill(dividendBill.getUserId(), dividendBill.getIndicateStartDate(), dividendBill.getIndicateEndDate());
        }
        return updated;
    }

    private double[] agreeProcess(int id) {
        UserDividendBill dividendBill = getById(id);
        if (dividendBill.getStatus() != Global.DIVIDEND_BILL_UNAPPROVE
                && dividendBill.getStatus() != Global.DIVIDEND_BILL_UNCOLLECT
                && dividendBill.getStatus() != Global.DIVIDEND_BILL_INSUFFICIENT
                && dividendBill.getStatus() != Global.DIVIDEND_BILL_PART_COLLECTED) {
            return new double[]{0, 0};
        }

        // 返回数组：[还需要向下级发放多少钱(大于0即表示余额不足), 自己可以领取多少钱]


        // 审核处理用户分红账单
        // 1：不需要向下级发放时，用户可领取金额=用户账单计算金额
        // 2：需要向下级发放时，用户可领取金额=用户账单计算金额-所有直属下级账单计算金额之和，如果用户

        // 如果不需要往下级发放，那么直接返回账单金额
        if (dividendBill.getLowerTotalAmount() <= 0) {
            if (dividendBill.getIssueType() == Global.DIVIDEND_ISSUE_TYPE_PLATFORM) {
                uDividendBillDao.addAvailableMoney(id, dividendBill.getCalAmount()); // 增加可领取金额
            }
            return new double[]{0, dividendBill.getCalAmount()};
        }

        double upperBillMoney; // 目前分红账单可供发放的金额
        double upperLowerTotalAmount = dividendBill.getLowerTotalAmount(); // 需要向下级发放多少
        double upperLowerPaidAmount = dividendBill.getLowerPaidAmount(); // 已经向下级发放多少
        if (dividendBill.getIssueType() == Global.DIVIDEND_ISSUE_TYPE_PLATFORM) {
            // 如果是平台发放，那他可以发放的钱就是账单中的钱
            upperBillMoney = dividendBill.getCalAmount();
        }
        else {
            // 如果不是平台发放，那他可以发放的钱就是他待领取的钱
            upperBillMoney = dividendBill.getAvailableAmount();
        }
        double upperStillNotPay = MathUtil.subtract(upperLowerTotalAmount, upperLowerPaidAmount); // 还需要向下级发放多少
        if (upperStillNotPay <= 0) {
            // 已经不需要向下级发放了，返回可用金额
            if (dividendBill.getIssueType() == Global.DIVIDEND_ISSUE_TYPE_PLATFORM) {
                uDividendBillDao.addAvailableMoney(id, upperBillMoney); // 增加可领取金额
            }
            return new double[]{0, upperBillMoney};
        }

        // 向直属下级发放分红
        double upperThisTimePaid = 0;
        List<UserDividendBill> directLowerBills = getDirectLowerBills(dividendBill.getUserId(), dividendBill.getIndicateStartDate(), dividendBill.getIndicateEndDate());
        double accountPayout = 0;
        for (UserDividendBill directLowerBill : directLowerBills) {
            if (directLowerBill.getStatus() != Global.DIVIDEND_BILL_UNAPPROVE
                    && directLowerBill.getStatus() != Global.DIVIDEND_BILL_UNCOLLECT
                    && directLowerBill.getStatus() != Global.DIVIDEND_BILL_INSUFFICIENT
                    && directLowerBill.getStatus() != Global.DIVIDEND_BILL_PART_COLLECTED) {
                continue;
            }

            // 直属下级的钱
            double lowerCalAmount = directLowerBill.getCalAmount();
            if (lowerCalAmount <= 0) continue;

            // 直属下级已经领取的金额，表明之前已经给该下级发放的总额
            double lowerReceived = MathUtil.add(directLowerBill.getAvailableAmount(), directLowerBill.getTotalReceived());

            // 还有多少没有领取
            double lowerRemainReceived = MathUtil.subtract(lowerCalAmount, lowerReceived);

            // 如果只有0.1了，那忽略
            if (lowerRemainReceived <= 0.1) continue;

            double billGive = 0; // 上级的账单中可以给他发放多少钱
            // 先从上级的账单金额中扣除
            if (upperBillMoney > 0 && lowerRemainReceived > 0) {
                // 先查上级的账单可以发放多少
                billGive = lowerRemainReceived >= upperBillMoney ? upperBillMoney : lowerRemainReceived;
                upperBillMoney = MathUtil.subtract(upperBillMoney, billGive);
                lowerRemainReceived = MathUtil.subtract(lowerRemainReceived, billGive);
                upperStillNotPay = MathUtil.subtract(upperStillNotPay, billGive);
            }

            // 下级还没有领完且上级的账单金额也扣完了，再看从上级的主账户中扣
            double totalMoneyGive = 0;
            User upUser = uDao.getById(dividendBill.getUserId());
            if (lowerRemainReceived > 0 && upperStillNotPay > 0 && upUser.getTotalMoney() > 0) {
                totalMoneyGive = lowerRemainReceived >= upUser.getTotalMoney() ? upUser.getTotalMoney() : lowerRemainReceived;
                lowerRemainReceived = MathUtil.subtract(lowerRemainReceived, totalMoneyGive);
                upperStillNotPay = MathUtil.subtract(upperStillNotPay, totalMoneyGive);
            }

            // 下级还没有领完且上级的主账户金额也扣完了，再看从上级的彩票账户中扣
            double lotteryMoneyGive = 0;
            if (lowerRemainReceived > 0 && upperStillNotPay > 0 && upUser.getLotteryMoney() > 0) {
                lotteryMoneyGive = lowerRemainReceived >= upUser.getLotteryMoney() ? upUser.getLotteryMoney() : lowerRemainReceived;
                lowerRemainReceived = MathUtil.subtract(lowerRemainReceived, lotteryMoneyGive);
                upperStillNotPay = MathUtil.subtract(upperStillNotPay, lotteryMoneyGive);
            }

            // 本次上级总共派发多少
            double totalGive = MathUtil.add(MathUtil.add(billGive, totalMoneyGive), lotteryMoneyGive);
            if (totalGive <= 0) break; // 没有派出去钱，说明上级账户没钱了，中断不再处理

            if (totalMoneyGive > 0) {
                UserVO subUser = dataFactory.getUser(directLowerBill.getUserId());
                uDao.updateTotalMoney(upUser.getId(), -totalMoneyGive);
                uBillService.addDividendBill(upUser, Global.BILL_ACCOUNT_MAIN, -totalMoneyGive, "系统自动扣发" + totalMoneyGive + "分红金额到" + subUser.getUsername(), false);
            }
            if (lotteryMoneyGive > 0) {
                UserVO subUser = dataFactory.getUser(directLowerBill.getUserId());
                uDao.updateLotteryMoney(upUser.getId(), -lotteryMoneyGive);
                uBillService.addDividendBill(upUser, Global.BILL_ACCOUNT_LOTTERY, -lotteryMoneyGive, "系统自动扣发" + lotteryMoneyGive + "分红金额到" + subUser.getUsername(), false);
            }

            upperThisTimePaid = MathUtil.add(upperThisTimePaid, totalGive); // 增加上级本次已派发金额
            // 增加下级的待领取金额
            uDividendBillDao.addAvailableMoney(directLowerBill.getId(), totalGive); // 增加下级的可领取金额

            accountPayout = MathUtil.add(totalMoneyGive, lotteryMoneyGive); // 通过账户发放出去的金额
        }

        // 我的上级仍有金额没有派给我，我又通过扣除自己账户给了下级，那么
        User user = uDao.getById(dividendBill.getUserId());
        UserDividendBill upperBill = uDividendBillDao.getByUserId(user.getUpid(), dividendBill.getIndicateStartDate(), dividendBill.getIndicateEndDate());
        // 我上级的账单是余额不足状态
        double upperNotYetPay = 0;
        if (upperBill != null && upperBill.getStatus() == Global.DIVIDEND_BILL_INSUFFICIENT && accountPayout > 0) {
            double meReceived = MathUtil.add(dividendBill.getAvailableAmount(), dividendBill.getTotalReceived());
            // 还有多少没有领取
            double meRemainReceived = MathUtil.subtract(dividendBill.getCalAmount(), meReceived);
            if (meRemainReceived > 0) {
                double addUserAmount = accountPayout > meRemainReceived ? meRemainReceived : accountPayout;
                uDividendBillDao.addUserAmount(dividendBill.getId(), addUserAmount);
                upperNotYetPay = addUserAmount;
            }
        }

        // 增加上级的已派发金额
        if (upperThisTimePaid > 0) {
            uDividendBillDao.addLowerPaidAmount(dividendBill.getId(), upperThisTimePaid);
        }
        // 设置可领取金额
        if (dividendBill.getIssueType() == Global.DIVIDEND_ISSUE_TYPE_PLATFORM
                && dividendBill.getStatus() == Global.DIVIDEND_BILL_UNAPPROVE
                && upperBillMoney > 0
                && dividendBill.getCalAmount() > 0
                && upperStillNotPay <= 0.1
                && dividendBill.getAvailableAmount() <= 0) {
            // 如果是平台发放，发放完以后，还剩余有钱，那么剩下的就是该用户可以领取的
            uDividendBillDao.setAvailableMoney(dividendBill.getId(), upperBillMoney);
        }
        else if (dividendBill.getIssueType() == Global.DIVIDEND_ISSUE_TYPE_UPPER) {

            double upperRemains = MathUtil.subtract(dividendBill.getAvailableAmount(), upperThisTimePaid);

            if (upperRemains > 0) {
                uDividendBillDao.addAvailableMoney(dividendBill.getId(), -upperThisTimePaid);
            }
            else {
                uDividendBillDao.setAvailableMoney(dividendBill.getId(), 0);
            }
        }

        return new double[]{upperStillNotPay, upperBillMoney, upperNotYetPay};


        // UserDividendBill dividendBill = getById(id);
        // if (dividendBill.getStatus() != Global.DIVIDEND_BILL_UNAPPROVE
        //         && dividendBill.getStatus() != Global.DIVIDEND_BILL_UNCOLLECT
        //         && dividendBill.getStatus() != Global.DIVIDEND_BILL_INSUFFICIENT
        //         && dividendBill.getStatus() != Global.DIVIDEND_BILL_PART_COLLECTED) {
        //     return new double[]{0, 0};
        // }
        // if (dividendBill.getLowerTotalAmount() <= 0) {
        //     return new double[]{0, 0};
        // }
        //
        // // 目前账单中可供发放的金额
        // double billMoney = dividendBill.getTotalReceived();
        // if (dividendBill.getIssueType() == Global.DIVIDEND_ISSUE_TYPE_PLATFORM) {
        //     if (dividendBill.getTotalReceived() < dividendBill.getUserAmount()) {
        //         billMoney = MathUtil.add(billMoney, dividendBill.getUserAmount());
        //     }
        // }
        // billMoney = MathUtil.subtract(billMoney, dividendBill.getLowerPaidAmount());
        //
        // // 仍需要向下级支付的金额
        // double stillNotPay = MathUtil.subtract(dividendBill.getLowerTotalAmount(), dividendBill.getLowerPaidAmount());
        // if (stillNotPay > 0) {
        //     // 扣发给直属下级
        //     List<UserDividendBill> directLowerBills = getDirectLowerBills(dividendBill.getUserId(), dividendBill.getIndicateStartDate(), dividendBill.getIndicateEndDate());
        //     for (UserDividendBill directLowerBill : directLowerBills) {
        //         if (directLowerBill.getStatus() != Global.DIVIDEND_BILL_UNAPPROVE
        //                 && directLowerBill.getStatus() != Global.DIVIDEND_BILL_UNCOLLECT
        //                 && directLowerBill.getStatus() != Global.DIVIDEND_BILL_INSUFFICIENT
        //                 && directLowerBill.getStatus() != Global.DIVIDEND_BILL_PART_COLLECTED) {
        //             continue;
        //         }
        //
        //         // 当前还有多少没有领取   分红金额-(已领取+可用领取)=剩余多少还没有领取
        //         double remainReceive = MathUtil.subtract(directLowerBill.getUserAmount(), directLowerBill.getTotalReceived());
        //         remainReceive = MathUtil.subtract(remainReceive, directLowerBill.getAvailableAmount());
        //
        //         if (remainReceive > 0) {
        //             // 先看账单中能发出去多少钱
        //             double billGive = 0;
        //             if (billMoney > 0) {
        //                 billGive = remainReceive >= billMoney ? billMoney : remainReceive;
        //                 billMoney = MathUtil.subtract(billMoney, billGive);
        //                 remainReceive = MathUtil.subtract(remainReceive, billGive);
        //             }
        //
        //             // 再看主账户中能发出去多少钱
        //             double totalMoneyGive = 0;
        //             User user = uDao.getById(dividendBill.getUserId());
        //             if (remainReceive > 0) {
        //                 totalMoneyGive = remainReceive >= user.getTotalMoney() ? user.getTotalMoney() : remainReceive;
        //                 remainReceive = MathUtil.subtract(remainReceive, totalMoneyGive);
        //             }
        //
        //             // 再看彩票账户中能发出去多少钱
        //             double lotteryMoneyGive = 0;
        //             if (remainReceive > 0) {
        //                 lotteryMoneyGive = remainReceive >= user.getLotteryMoney() ? user.getLotteryMoney() : remainReceive;
        //                 remainReceive = MathUtil.subtract(remainReceive, lotteryMoneyGive);
        //             }
        //
        //             // 本次总共派发多少
        //             double totalGive = MathUtil.add(MathUtil.add(billGive, totalMoneyGive), lotteryMoneyGive);
        //             if (totalGive <= 0) {
        //                 break;
        //             }
        //
        //             if (totalMoneyGive > 0) {
        //                 UserVO subUser = dataFactory.getUser(directLowerBill.getUserId());
        //                 uDao.updateTotalMoney(user.getId(), -totalMoneyGive);
        //                 uBillService.addDividendBill(user, Global.BILL_ACCOUNT_MAIN, -totalMoneyGive, "系统自动扣发" + totalMoneyGive + "分红金额到" + subUser.getUsername());
        //             }
        //             if (lotteryMoneyGive > 0) {
        //                 UserVO subUser = dataFactory.getUser(directLowerBill.getUserId());
        //                 uDao.updateLotteryMoney(user.getId(), -lotteryMoneyGive);
        //                 uBillService.addDividendBill(user, Global.BILL_ACCOUNT_LOTTERY, -lotteryMoneyGive, "系统自动扣发" + lotteryMoneyGive + "分红金额到" + subUser.getUsername());
        //             }
        //
        //             // 如果直属下级也要往下发
        //             if (directLowerBill.getLowerTotalAmount() > 0) {
        //                 double totalReceived = 0;
        //                 double availableMoney = 0;
        //
        //                 // 还剩余多少没有给下级发
        //                 double remainLowerTotal = MathUtil.subtract(directLowerBill.getLowerTotalAmount(), directLowerBill.getLowerPaidAmount());
        //
        //                 // 如果本次上级发放的钱够钱发给下级
        //                 if (remainLowerTotal < totalGive) {
        //                     totalReceived = remainLowerTotal;
        //                     availableMoney = MathUtil.subtract(totalGive, remainLowerTotal);
        //                 }
        //                 else {
        //                     totalReceived = totalGive;
        //                 }
        //
        //                 uDividendBillDao.addTotalReceived(directLowerBill.getId(), totalReceived); // 增加下级的已领取金额
        //                 if (availableMoney > 0) {
        //                     uDividendBillDao.addAvailableMoney(directLowerBill.getId(), availableMoney); // 增加下级的可领取金额
        //                 }
        //             }
        //             else {
        //                 uDividendBillDao.addAvailableMoney(directLowerBill.getId(), totalGive); // 增加下级的可领取金额
        //             }
        //             uDividendBillDao.addLowerPaidAmount(dividendBill.getId(), totalGive); // 增加上级的已向下级扣发的金额
        //             // if (dividendBill.getTotalReceived()  <= 0) {
        //             //     uDividendBillDao.addTotalReceived(dividendBill.getId(), billGive);
        //             // }
        //             stillNotPay = MathUtil.subtract(stillNotPay, totalGive);
        //             //
        //             // // 继续往下扣发
        //             // issue(directLowerBill.getId());
        //         }
        //     }
        // }
        //
        // if (stillNotPay <= 0.1 && billMoney > 0) {
        //     uDividendBillDao.addAvailableMoney(dividendBill.getId(), billMoney);
        // }
        //
        // return new double[]{stillNotPay, billMoney};
    }

    @Override
    public boolean deny(WebJSONObject json, int id, String remarks) {
        // 查找契约
        UserDividendBill dividendBill = getById(id);

        if (dividendBill.getStatus() != Global.DIVIDEND_BILL_UNAPPROVE) {
            json.set(2, "2-3004");
            return false;
        }

        User user = uDao.getById(dividendBill.getUserId());
        if (user == null) {
            json.set(2, "2-32");
            return false;
        }

        // 如果是拒绝主管的分红，那么只需要拒绝主管
        boolean isZhuGuan = uCodePointUtil.isLevel1Proxy(user);
        if (isZhuGuan) {
            return uDividendBillDao.update(id, Global.DIVIDEND_BILL_DENIED, remarks);
        }
        else {
            json.set(2, "2-3005");
            return false;
        }
    }

    @Override
    public boolean del(WebJSONObject json, int id) {
        return uDividendBillDao.del(id);
    }

    @Override
    public boolean reset(WebJSONObject json, int id, String remarks) {
        UserDividendBill userDividendBill = getById(id);
        if (userDividendBill == null) {
            json.set(2, "2-3001");
            return false;
        }
        if (userDividendBill.getStatus() != Global.DIVIDEND_BILL_INSUFFICIENT) {
            json.set(2, "2-3002");
            return false;
        }

        double stillNotPay = MathUtil.subtract(userDividendBill.getLowerTotalAmount(), userDividendBill.getLowerPaidAmount());
        if (stillNotPay <= 0) {
            json.set(2, "2-3002");
            return false;
        }

        // 该账单修改为已发放
        uDividendBillDao.updateStatus(id, Global.DIVIDEND_BILL_ISSUED, remarks);

        // 所有下级账单也修改为已发放
        List<UserDividendBill> lowerBills = getLowerBills(userDividendBill.getUserId(), userDividendBill.getIndicateStartDate(), userDividendBill.getIndicateEndDate());
        if (CollectionUtils.isNotEmpty(lowerBills)) {
            for (UserDividendBill lowerBill : lowerBills) {
                if (lowerBill.getStatus() == Global.DIVIDEND_BILL_UNCOLLECT
                        || lowerBill.getStatus() == Global.DIVIDEND_BILL_PART_COLLECTED) {
                    // uDividendBillDao.updateStatus(lowerBill.getId(), Global.DIVIDEND_BILL_ISSUED);
                    uDividendBillDao.updateStatus(lowerBill.getId(), Global.DIVIDEND_BILL_ISSUED, remarks);
                }
            }
        }

        return true;
    }

	@Override
	public double queryPeriodCollect(int userId, String sTime, String eTime) {
		// 查询条件
		List<Criterion> criterions = new ArrayList<>();
		criterions.add(Restrictions.ge("collectTime", sTime));
		criterions.add(Restrictions.lt("collectTime", eTime));
		criterions.add(Restrictions.eq("userId", userId));
		List<UserDividendBill> lists = uDividendBillDao.findByCriteria(criterions, null);
		double result = 0;
		if(null == lists || lists.isEmpty()){
			return result;
		}
		for (UserDividendBill bill : lists) {
			result += bill.getUserAmount();
		}
		return result;
	}
}
