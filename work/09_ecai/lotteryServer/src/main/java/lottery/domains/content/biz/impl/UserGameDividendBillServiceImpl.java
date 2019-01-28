package lottery.domains.content.biz.impl;

import javautils.math.MathUtil;
import lottery.domains.content.biz.UserBillService;
import lottery.domains.content.biz.UserGameDividendBillService;
import lottery.domains.content.dao.UserDao;
import lottery.domains.content.dao.UserGameDividendBillDao;
import lottery.domains.content.entity.UserGameDividendBill;
import lottery.domains.content.global.Global;
import lottery.web.WebJSON;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 老虎机真人体育账单Service
 * Created by Nick on 2016/10/31.
 */
@Service
public class UserGameDividendBillServiceImpl implements UserGameDividendBillService {
    @Autowired
    private UserGameDividendBillDao uGameDividendBillDao;
    @Autowired
    private UserDao uDao;
    @Autowired
    private UserBillService uBillService;


    @Override
    @Transactional(readOnly = true)
    public UserGameDividendBill getById(int id) {
        return uGameDividendBillDao.getById(id);
    }

    @Override
    public boolean collect(WebJSON json, int userId, int id) {
        UserGameDividendBill userDividendBill = uGameDividendBillDao.getById(id);
        if (userDividendBill == null || userDividendBill.getStatus() != Global.GAME_DIVIDEND_BILL_UNCOLLECT
                || userDividendBill.getUserAmount() <= 0) {
            json.set(2, "2-6025"); // 没有可以领取的金额
            return false;
        }

        double userAmount = userDividendBill.getUserAmount(); // 可领取的金额

        // 修改状态
        uGameDividendBillDao.updateStatus(id, Global.GAME_DIVIDEND_BILL_ISSUED, Global.GAME_DIVIDEND_BILL_UNCOLLECT);

        // 生成账单并更新报表
        String remarks = concatRemarks(userDividendBill);
        uBillService.addDividendBill(userId, Global.BILL_ACCOUNT_LOTTERY, userAmount, remarks);

        // 加钱到彩票账户
        uDao.updateLotteryMoney(userId, userAmount);

        return true;
    }

    private String concatRemarks(UserGameDividendBill userDividendBill) {
        String remarks = "领取%s~%s老虎机真人体育分红；金额：%s；";

        String startDate = userDividendBill.getIndicateStartDate();
        String endDate = userDividendBill.getIndicateEndDate();
        double amount = MathUtil.doubleFormat(userDividendBill.getUserAmount(), 4);

        Object[] formatValues = {startDate, endDate, amount};
        remarks = String.format(remarks, formatValues);

        return remarks;
    }
}
