package lottery.domains.content.biz.impl;

import javautils.date.Moment;
import lottery.domains.content.biz.UserBillService;
import lottery.domains.content.biz.UserLotteryDetailsReportService;
import lottery.domains.content.biz.UserLotteryReportService;
import lottery.domains.content.dao.UserBillDao;
import lottery.domains.content.dao.UserDao;
import lottery.domains.content.entity.User;
import lottery.domains.content.entity.UserBets;
import lottery.domains.content.entity.UserBill;
import lottery.domains.content.global.Global;
import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserBillServiceImpl implements UserBillService {
    private static final Logger log = LoggerFactory.getLogger(UserBillServiceImpl.class);

    //region services
    @Autowired
    private UserBillDao uBillDao;
    @Autowired
    private UserDao userDao;
    @Autowired
    private UserLotteryReportService uLotteryReportService;
    @Autowired
    private UserLotteryDetailsReportService uLotteryDetailsReportService;
    //endregion

    /**
     * 生成订单号
     */
    private String billno() {
        return ObjectId.get().toString();
    }

    @Override
	public boolean addCancelOrderBill(UserBets userBets, String remarks) {
		boolean flag = false;
		try {
            User newestUser = userDao.getById(userBets.getUserId());

            String billno = billno();
            int userId = newestUser.getId();
            int account = Global.BILL_ACCOUNT_LOTTERY;
            int type = Global.BILL_TYPE_CANCEL_ORDER;
            double money = userBets.getMoney();
            double beforeMoney = newestUser.getLotteryMoney();
            if (beforeMoney < 0) {
                beforeMoney = 0;
            }
            double afterMoney = newestUser.getLotteryMoney() + money;
            if (afterMoney < 0) {
                afterMoney = 0;
            }
            Integer refType = type;
            String refId = String.valueOf(userBets.getId());
            Moment thisTime = new Moment();
            UserBill tmpBill = new UserBill(billno, userId, account, type, money, beforeMoney, afterMoney, refType, refId, thisTime.toSimpleTime(), remarks);
            flag = uBillDao.add(tmpBill);
            // if(flag) {
            // 	uLotteryReportService.update(userId, type, money, thisTime.toSimpleDate());
            // 	uLotteryDetailsReportService.update(userId, bBean.getLotteryId(), bBean.getMethod(), type, money, thisTime.toSimpleDate());
            // }
		} catch (Exception e) {
			log.error("写入彩票撤单失败！", e);
			throw e;//在嵌套事物中必须抛出 不然无法触发roll-back
		}
		return flag;
	}

    @Override
    public boolean addUserWinBill(UserBets userBets, double amount, String remarks) {
        boolean flag = false;
        try {
            User newestUser = userDao.getById(userBets.getUserId());

            String billno = billno();
            int userId = newestUser.getId();
            int type = Global.BILL_TYPE_PRIZE;
            int refType = type;
            int account = Global.BILL_ACCOUNT_LOTTERY;
            String refId = String.valueOf(userBets.getId());
            double money = amount;
            double beforeMoney = newestUser.getLotteryMoney();
            if (beforeMoney < 0) {
                beforeMoney = 0;
            }
            double afterMoney = newestUser.getLotteryMoney() + money;
            if (afterMoney < 0) {
                afterMoney = 0;
            }
            Moment thisTime = new Moment();
            UserBill tmpBill = new UserBill(billno, userId, account, type, money, beforeMoney, afterMoney, refType, refId, thisTime.toSimpleTime(), remarks);
            flag = uBillDao.add(tmpBill);
            if(flag) {
                // Moment betTime = new Moment().fromTime(userBets.getTime()); // 报表计算到投注时间那边
                uLotteryReportService.update(userId, type, money, thisTime.toSimpleDate());
                uLotteryDetailsReportService.update(userId, userBets.getLotteryId(), userBets.getRuleId(), type, amount, thisTime.toSimpleDate());
            }
        } catch (Exception e) {
            log.error("写入账单失败！", e);
            throw e;//在嵌套事物中必须抛出 不然无法触发roll-back
        }
        return flag;
    }

    @Override
    public boolean addProxyReturnBill(UserBets userBets, int upId, double amount, String remarks) {
        boolean flag = false;
        try {
            User newestUser = userDao.getById(upId);

            String billno = billno();
            int userId = newestUser.getId();
            int type = Global.BILL_TYPE_PROXY_RETURN;
            int refType = type;
            int account = Global.BILL_ACCOUNT_LOTTERY;
            String refId = String.valueOf(userBets.getId());
            double money = amount;
            double beforeMoney = newestUser.getLotteryMoney();
            if (beforeMoney < 0) {
                beforeMoney = 0;
            }
            double afterMoney = newestUser.getLotteryMoney() + money;
            if (afterMoney < 0) {
                afterMoney = 0;
            }
            Moment thisTime = new Moment();
            UserBill tmpBill = new UserBill(billno, userId, account, type, money, beforeMoney, afterMoney, refType, refId, thisTime.toSimpleTime(), remarks);
            flag = uBillDao.add(tmpBill);
            if(flag) {
                // Moment betTime = new Moment().fromTime(userBets.getTime()); // 报表计算到投注时间那边
                uLotteryReportService.update(userId, type, money, thisTime.toSimpleDate());
                uLotteryDetailsReportService.update(userId, userBets.getLotteryId(), userBets.getRuleId(), type, amount, thisTime.toSimpleDate());
            }
        } catch (Exception e) {
            log.error("写入账单失败！", e);
            throw e;//在嵌套事物中必须抛出 不然无法触发roll-back
        }
        return flag;
    }

    @Override
    public boolean addSpendReturnBill(UserBets userBets, double amount, String remarks) {
        boolean flag = false;
        try {
            User newestUser = userDao.getById(userBets.getUserId());
            String billno = billno();
            int userId = newestUser.getId();
            int type = Global.BILL_TYPE_SPEND_RETURN;
            int refType = type;
            int account = Global.BILL_ACCOUNT_LOTTERY;
            String refId = String.valueOf(userBets.getId());
            double money = amount;
            double beforeMoney = newestUser.getLotteryMoney();
            if (beforeMoney < 0) {
                beforeMoney = 0;
            }
            double afterMoney = newestUser.getLotteryMoney() + money;
            if (afterMoney < 0) {
                afterMoney = 0;
            }
            Moment thisTime = new Moment();
            UserBill tmpBill = new UserBill(billno, userId, account, type, money, beforeMoney, afterMoney, refType, refId, thisTime.toSimpleTime(), remarks);
            flag = uBillDao.add(tmpBill);
            if(flag) {
                uLotteryReportService.update(userId, type, money, thisTime.toSimpleDate());
                uLotteryDetailsReportService.update(userId, userBets.getLotteryId(), userBets.getRuleId(), type, amount, thisTime.toSimpleDate());
            }
        } catch (Exception e) {
            log.error("写入账单失败！", e);
            throw e;//在嵌套事物中必须抛出 不然无法触发roll-back
        }
        return flag;
    }
    
    @Override
    public boolean addSpendBill(UserBets bBean) {
        boolean flag = false;
        try {
            User newestUser = userDao.getById(bBean.getUserId());

            String billno = billno();
            int userId = newestUser.getId();
            int account = Global.BILL_ACCOUNT_LOTTERY;
            int type = Global.BILL_TYPE_SPEND;
            double money = bBean.getMoney();

            double beforeMoney = newestUser.getLotteryMoney() + money;
            double afterMoney = newestUser.getLotteryMoney();

            if (beforeMoney <= 0) {
                beforeMoney = 0;
            }
            if (afterMoney <= 0) {
                afterMoney = 0;
            }

            Integer refType = type;
            String refId = String.valueOf(bBean.getId());
            Moment thisTime = new Moment();
            String remarks = "用户投注：" + bBean.getExpect();
            UserBill tmpBill = new UserBill(billno, userId, account, type, money, beforeMoney, afterMoney, refType, refId, thisTime.toSimpleTime(), remarks);
            flag = uBillDao.add(tmpBill);
            if(flag) {
                uLotteryDetailsReportService.update(userId, bBean.getLotteryId(), bBean.getRuleId(), type, money, thisTime.toSimpleDate());
            }
        } catch (Exception e) {
            log.error("写入彩票消费失败！", e);
            throw e;//在嵌套事物中必须抛出 不然无法触发roll-back
        }
        return flag;
    }
}