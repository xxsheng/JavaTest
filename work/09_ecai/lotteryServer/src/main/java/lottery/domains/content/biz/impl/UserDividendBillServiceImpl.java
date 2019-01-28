package lottery.domains.content.biz.impl;

import javautils.StringUtil;
import javautils.math.MathUtil;
import lottery.domains.content.biz.UserBillService;
import lottery.domains.content.biz.UserDividendBillService;
import lottery.domains.content.dao.UserDao;
import lottery.domains.content.dao.UserDividendBillDao;
import lottery.domains.content.dao.read.UserReadDao;
import lottery.domains.content.entity.User;
import lottery.domains.content.entity.UserDividendBill;
import lottery.domains.content.global.Global;
import lottery.domains.content.vo.user.UserVO;
import lottery.domains.pool.DataFactory;
import lottery.web.WebJSON;
import lottery.web.content.validate.UserDividendValidate;
import org.apache.commons.collections.CollectionUtils;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * 契约日结账单Service
 * Created by Nick on 2016/10/31.
 */
@Service
public class UserDividendBillServiceImpl implements UserDividendBillService {
    @Autowired
    private UserDividendBillDao uDividendBillDao;
    @Autowired
    private UserDao uDao;
    @Autowired
    private UserReadDao uReadDao;

    @Autowired
    private UserDividendValidate dividendValidate;
    @Autowired
    private UserBillService uBillService;

    @Autowired
    private DataFactory dataFactory;


    @Override
    public UserDividendBill getById(int id) {
        return uDividendBillDao.getById(id);
    }

    @Override
    public boolean collect(WebJSON json, int userId, int id) {
        UserDividendBill userDividendBill = uDividendBillDao.getById(id);
        boolean valid = dividendValidate.isCollectLegal(json, userId, userDividendBill);
        if (!valid) {
            return valid;
        }

        double availableAmount = userDividendBill.getAvailableAmount(); // 可领取的金额
        double currentReceived = MathUtil.add(availableAmount, userDividendBill.getTotalReceived());

        boolean isAllCollected = (currentReceived + 0.1) >= userDividendBill.getUserAmount(); // 是否领取完成，允许有0.1误差，因为有精度问题

        // 修改状态
        uDividendBillDao.update(id, isAllCollected ? Global.DIVIDEND_BILL_ISSUED : Global.DIVIDEND_BILL_PART_COLLECTED, 0, currentReceived);

        // 生成账单并更新报表
        String remarks = concatRemarks(userDividendBill);
        boolean added = uBillService.addDividendBill(userId, Global.BILL_ACCOUNT_LOTTERY, availableAmount, remarks);

        // 加钱到彩票账户
        if (added) {
            uDao.updateLotteryMoney(userId, availableAmount);
        }

        return added;
        // UserDividendBill userDividendBill = uDividendBillDao.getById(id);
        // boolean valid = dividendValidate.isCollectLegal(json, userId, userDividendBill);
        // if (!valid) {
        //     return valid;
        // }
        //
        // double availableAmount = userDividendBill.getAvailableAmount(); // 可领取的金额
        // double currentReceived = MathUtil.add(availableAmount, userDividendBill.getTotalReceived());
        // double totalIssued = MathUtil.add(currentReceived, userDividendBill.getLowerPaidAmount());
        //
        // boolean isAllCollected = (totalIssued + 0.1) >= userDividendBill.getUserAmount(); // 是否领取完成，允许有0.1误差，因为有精度问题
        //
        // // 修改状态
        // uDividendBillDao.update(id, isAllCollected ? Global.DIVIDEND_BILL_ISSUED : Global.DIVIDEND_BILL_PART_COLLECTED, 0, currentReceived);
        //
        // // 生成账单并更新报表
        // String remarks = concatRemarks(userDividendBill);
        // boolean added = uBillService.addDividendBill(userId, Global.BILL_ACCOUNT_LOTTERY, availableAmount, remarks);
        //
        // // 加钱到彩票账户
        // if (added) {
        //     uDao.updateLotteryMoney(userId, availableAmount);
        // }
        //
        // return added;
    }

    private String concatRemarks(UserDividendBill userDividendBill) {
        String remarks = "领取%s~%s分红；金额：%s；";

        String startDate = userDividendBill.getIndicateStartDate();
        String endDate = userDividendBill.getIndicateEndDate();
        double amount = MathUtil.doubleFormat(userDividendBill.getAvailableAmount(), 4);

        Object[] formatValues = {startDate, endDate, amount};
        remarks = String.format(remarks, formatValues);

        return remarks;
    }

    @Override
    public boolean updateStatus(int id, int status) {
        return uDividendBillDao.updateStatus(id, status);
    }

    @Override
    public boolean updateStatus(int id, int status, String remarks) {
        return uDividendBillDao.updateStatus(id, status, remarks);
    }

    @Override
    public boolean update(int id, int status, double availableAmount, double totalReceived) {
        return uDividendBillDao.update(id, status, availableAmount, totalReceived);
    }

    @Override
    @Transactional(readOnly = true)
    public double getTotalUnIssue(int userId) {
        return uDividendBillDao.getTotalUnIssue(userId);
    }

    @Override
    public boolean addAvailableMoney(int id, double money) {
        return uDividendBillDao.addAvailableMoney(id, money);
    }

    @Override
    public boolean addTotalReceived(int id, double money) {
        return uDividendBillDao.addTotalReceived(id, money);
    }

    @Override
    public boolean addLowerPaidAmount(int id, double money) {
        return uDividendBillDao.addLowerPaidAmount(id, money);
    }
}
