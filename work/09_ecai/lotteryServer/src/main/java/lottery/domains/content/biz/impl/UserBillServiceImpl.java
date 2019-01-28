package lottery.domains.content.biz.impl;

import javautils.date.Moment;
import javautils.math.MathUtil;
import lottery.domains.content.biz.*;
import lottery.domains.content.dao.UserBillDao;
import lottery.domains.content.dao.UserDao;
import lottery.domains.content.entity.*;
import lottery.domains.content.global.Global;
import lottery.domains.pool.DataFactory;
import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserBillServiceImpl implements UserBillService {

	private static final Logger logger = LoggerFactory.getLogger(UserBillServiceImpl.class);
	
	/**
	 * DAO
	 */
	@Autowired
	private UserBillDao uBillDao;
	@Autowired
	private UserDao userDao;
	
	/**
	 * SERVICE
	 */
	@Autowired
	private UserMainReportService uMainReportService;
	
	@Autowired
	private UserLotteryReportService uLotteryReportService;
	
	@Autowired
	private UserLotteryDetailsReportService uLotteryDetailsReportService;
	
	// @Autowired
	// private UserBaccaratReportService uBaccaratReportService;

	@Autowired
	private UserGameReportService uGameReportService;
	
	/**
	 * DataFactory
	 */
	@Autowired
	private DataFactory dataFactory;
	
	/**
	 * 生成订单号
	 */
	private String billno() {
		// return new Moment().format("yyyyMMdd") + OrderUtil.getBillno(24, true);
		return ObjectId.get().toString();
	}
	
	@Override
	public boolean addRechargeBill(UserRecharge cBean, User user, double receiveFeeMoney) {
		try {
			User newestUser = userDao.getById(user.getId()); // 取最新数据
			String billno = billno();
			int userId = newestUser.getId();
			//2016-09-20  现在修改为直接充值到彩票账户
			//int account = Global.BILL_ACCOUNT_MAIN;
			int account = Global.BILL_ACCOUNT_LOTTERY;
			int type = Global.BILL_TYPE_RECHARGE;
			double money = cBean.getRecMoney();
			//2016-09-20  现在修改为直接充值到彩票账户
			//double beforeMoney = uBean.getTotalMoney();
			double beforeMoney = newestUser.getLotteryMoney();
			double afterMoney = newestUser.getLotteryMoney() + money;
			String remarks = "充值";

			if (beforeMoney <= 0) {
				beforeMoney = 0;
			}
			if (afterMoney <= 0) {
				afterMoney = 0;
			}

			Integer refType = type;
			String refId = cBean.getBillno();
			Moment thisTime = new Moment();
			UserBill tmpBill = new UserBill(billno, userId, account, type, money, beforeMoney, afterMoney, refType, refId, thisTime.toSimpleTime(), remarks);
			boolean added = uBillDao.add(tmpBill);
			if(added) {
				// 第三方实际收的手续费
				uMainReportService.updateRecharge(userId, money, thisTime.toSimpleDate(), receiveFeeMoney);
				// 将充值手续费计算到优惠中，平台这边收的
				if (dataFactory.getRechargeConfig().getFeePercent() > 0) {
					double rechargeFee = MathUtil.multiply(money, dataFactory.getRechargeConfig().getFeePercent(), 4);
					if (rechargeFee > 0) {
						uLotteryReportService.updateRechargeFee(userId, rechargeFee, thisTime.toSimpleDate());
					}
				}
				return true;
			}
			return false;
		} catch (Exception e) {
			logger.error("写入存款账单失败！", e);
			throw e;//在嵌套事物中必须抛出 不然无法触发roll-back
		}
	}
	
	@Override
	public boolean addWithdrawBill(UserWithdraw wBean, int account, double amount, User user, String remarks) {
		boolean flag = false;
		try {
			User newestUser = userDao.getById(user.getId());
			String billno = billno();
			int userId = newestUser.getId();
			int type = Global.BILL_TYPE_WITHDRAWALS;
			double money = amount;

			double beforeMoney = 0;
			double afterMoney = 0;
			if(account == Global.BILL_ACCOUNT_LOTTERY){
				beforeMoney = newestUser.getLotteryMoney() + money;
				afterMoney = newestUser.getLotteryMoney();
			}
			if(account == Global.BILL_ACCOUNT_MAIN){
				beforeMoney = newestUser.getTotalMoney() + money;
				afterMoney = newestUser.getTotalMoney();
			}

			if (beforeMoney <= 0) {
				beforeMoney = 0;
			}
			if (afterMoney <= 0) {
				afterMoney = 0;
			}

			Integer refType = null;
			String refId = wBean.getBillno();
			Moment thisTime = new Moment();
			UserBill tmpBill = new UserBill(billno, userId, account, type, money, beforeMoney, afterMoney, refType, refId, thisTime.toSimpleTime(), remarks);
			flag = uBillDao.add(tmpBill);
		} catch (Exception e) {
			logger.error("写入取款账单失败！", e);
			throw e;//在嵌套事物中必须抛出 不然无法触发roll-back
		}
		return flag;
	}
	
	@Override
	public boolean addTransInBill(UserTransfers tBean, User user, int account, String remarks, double beforeMoney, double afterMoney) {
		boolean flag = false;
		try {
			User newestUser = userDao.getById(user.getId());
			String billno = billno();
			int userId = newestUser.getId();
			int type = Global.BILL_TYPE_TRANS_IN;
			double money = tBean.getMoney();

			Integer refType = null;
			String refId = tBean.getBillno();
			Moment thisTime = new Moment();
			UserBill tmpBill = new UserBill(billno, userId, account, type, money, beforeMoney, afterMoney, refType, refId, thisTime.toSimpleTime(), remarks);
			flag = uBillDao.add(tmpBill);
			if(flag) {
				if(account == Global.BILL_ACCOUNT_MAIN) {
					uMainReportService.update(userId, type, money, thisTime.toSimpleDate());
				}
				if(account == Global.BILL_ACCOUNT_LOTTERY) {
					uLotteryReportService.update(userId, type, money, thisTime.toSimpleDate());
				}
				if(account == Global.BILL_ACCOUNT_PT || account == Global.BILL_ACCOUNT_AG) {
					uGameReportService.update(userId, account, type, money, thisTime.toSimpleDate());
				}
			}
		} catch (Exception e) {
			logger.error("写入转入账单失败！", e);
			throw e;//在嵌套事物中必须抛出 不然无法触发roll-back
		}
		return flag;
	}
	
	@Override
	public boolean addTransOutBill(UserTransfers tBean, User user, int account, String remarks, double beforeMoney, double afterMoney) {
		boolean flag = false;
		try {
			User newestUser = userDao.getById(user.getId());
			String billno = billno();
			int userId = newestUser.getId();
			int type = Global.BILL_TYPE_TRANS_OUT;
			double money = tBean.getMoney();

			Integer refType = null;
			String refId = tBean.getBillno();
			Moment thisTime = new Moment();
			UserBill tmpBill = new UserBill(billno, userId, account, type, money, beforeMoney, afterMoney, refType, refId, thisTime.toSimpleTime(), remarks);
			flag = uBillDao.add(tmpBill);
			if(flag) {
				if(account == Global.BILL_ACCOUNT_MAIN) {
					uMainReportService.update(userId, type, money, thisTime.toSimpleDate());
				}
				if(account == Global.BILL_ACCOUNT_LOTTERY) {
					uLotteryReportService.update(userId, type, money, thisTime.toSimpleDate());
				}
				if(account == Global.BILL_ACCOUNT_PT || account == Global.BILL_ACCOUNT_AG) {
					uGameReportService.update(userId, account, type, money, thisTime.toSimpleDate());
				}
			}
		} catch (Exception e) {
			logger.error("写入转出账单失败！", e);
			throw e;//在嵌套事物中必须抛出 不然无法触发roll-back
		}
		return flag;
	}
	
	@Override
	public boolean addUserTransInBill(User user, int account, double amount, String remarks) {
		boolean flag = false;
		try {
			User newestUser = userDao.getById(user.getId());
			String billno = billno();
			int userId = newestUser.getId();
			int type = Global.BILL_TYPE_USER_TRANS;
			double money = amount;

			double beforeMoney = 0;
			double afterMoney = 0;
			if(account == Global.BILL_ACCOUNT_LOTTERY){
				beforeMoney = newestUser.getLotteryMoney() - money;
				afterMoney = newestUser.getLotteryMoney();
			}	
			if(account == Global.BILL_ACCOUNT_MAIN){
				beforeMoney = newestUser.getTotalMoney() - money;
				afterMoney = newestUser.getTotalMoney();
			}
			if(account == Global.BILL_ACCOUNT_BACCARAT){
				beforeMoney = newestUser.getBaccaratMoney() - money;
				afterMoney = newestUser.getBaccaratMoney();
			}
			if(account == Global.BILL_TYPE_TRANS_ACCOUNT){
				account = Global.BILL_ACCOUNT_MAIN;
				type = Global.BILL_TYPE_TRANS_ACCOUNT;
				beforeMoney = newestUser.getTotalMoney() + money;
				afterMoney = newestUser.getTotalMoney();
			}

			if (beforeMoney <= 0) {
				beforeMoney = 0;
			}
			if (afterMoney <= 0) {
				afterMoney = 0;
			}
			
			Integer refType = null;
			String refId = null;
			Moment thisTime = new Moment();
			UserBill tmpBill = new UserBill(billno, userId, account, type, money, beforeMoney, afterMoney, refType, refId, thisTime.toSimpleTime(), remarks);
			flag = uBillDao.add(tmpBill);
			if(flag) {
				uMainReportService.updateUserTrans(userId, type, money, thisTime.toSimpleDate(), true);
			}
		} catch (Exception e) {
			logger.error("写入上下级转账（转入）账单失败！", e);
			throw e;//在嵌套事物中必须抛出 不然无法触发roll-back
		}
		return flag;
	}
	
	@Override
	public boolean addUserTransOutBill(User user, int account, double amount, String remarks) {
		boolean flag = false;
		try {
			User newestUser = userDao.getById(user.getId());
			String billno = billno();
			int userId = newestUser.getId();
			int type = Global.BILL_TYPE_USER_TRANS;
			double money = amount;
//			double beforeMoney = uBean.getTotalMoney();

			double beforeMoney = 0;
			double afterMoney = 0;
			if(account == Global.BILL_ACCOUNT_LOTTERY){
				beforeMoney = newestUser.getLotteryMoney() + money;
				afterMoney = newestUser.getLotteryMoney();
			}	
			if(account == Global.BILL_ACCOUNT_MAIN){
				beforeMoney = newestUser.getTotalMoney() + money;
				afterMoney = newestUser.getTotalMoney();
			}
			if(account == Global.BILL_ACCOUNT_BACCARAT){
				beforeMoney = newestUser.getBaccaratMoney() + money;
				afterMoney = newestUser.getBaccaratMoney();
			}

			if (beforeMoney <= 0) {
				beforeMoney = 0;
			}
			if (afterMoney <= 0) {
				afterMoney = 0;
			}

			Integer refType = null;
			String refId = null;
			Moment thisTime = new Moment();
			UserBill tmpBill = new UserBill(billno, userId, account, type, money, beforeMoney, afterMoney, refType, refId, thisTime.toSimpleTime(), remarks);
			flag = uBillDao.add(tmpBill);
			if(flag) {
				uMainReportService.updateUserTrans(userId, type, money, thisTime.toSimpleDate(), false);
			}
		} catch (Exception e) {
			logger.error("写入上下级转账（转出）账单失败！", e);
			throw e;//在嵌套事物中必须抛出 不然无法触发roll-back
		}
		return flag;
	}

	@Override
	public boolean addActivityBill(User user, int account, double amount, int refType, String remarks) {
		boolean flag = false;
		try {
			User newestUser = userDao.getById(user.getId());
			String billno = billno();
			int userId = newestUser.getId();
			int type = Global.BILL_TYPE_ACTIVITY;
			double money = amount;

			double beforeMoney = 0;
			double afterMoney = 0;
			if(account == Global.BILL_ACCOUNT_MAIN) {
				beforeMoney = newestUser.getTotalMoney() - money;
				afterMoney = newestUser.getTotalMoney();
			}
			if(account == Global.BILL_ACCOUNT_LOTTERY) {
				beforeMoney = newestUser.getLotteryMoney() - money;
				afterMoney = newestUser.getLotteryMoney();
			}
			if(account == Global.BILL_ACCOUNT_BACCARAT) {
				beforeMoney = newestUser.getBaccaratMoney() - money;
				afterMoney = newestUser.getBaccaratMoney();
			}

			if (beforeMoney <= 0) {
				beforeMoney = 0;
			}
			if (afterMoney <= 0) {
				afterMoney = 0;
			}

			String refId = null;
			Moment thisTime = new Moment();
			UserBill tmpBill = new UserBill(billno, userId, account, type, money, beforeMoney, afterMoney, refType, refId, thisTime.toSimpleTime(), remarks);
			flag = uBillDao.add(tmpBill);
			if(flag) {
				if(account == Global.BILL_ACCOUNT_MAIN) {
					uMainReportService.update(userId, type, money, thisTime.toSimpleDate());
				}
				if(account == Global.BILL_ACCOUNT_LOTTERY) {
					uLotteryReportService.update(userId, type, money, thisTime.toSimpleDate());
				}
				// if(account == Global.BILL_ACCOUNT_BACCARAT) {
				// 	uBaccaratReportService.update(userId, type, money, thisTime.toSimpleDate());
				// }
			}
		} catch (Exception e) {
			logger.error("写入活动账单失败！", e);
			throw e;//在嵌套事物中必须抛出 不然无法触发roll-back
		}
		return flag;
	}
	
	
	@Override
	public boolean addPacketBill(User user, int account, double amount, int refType, String remarks) {
		boolean flag = false;
		try {
			User newestUser = userDao.getById(user.getId());

			String billno = billno();
			int userId = newestUser.getId();
			int type = Global.BILL_TYPE_RED_PACKET;
			double money = amount;
			double beforeMoney = 0;
			double afterMoney = 0;
			if(account == Global.BILL_ACCOUNT_MAIN) {
				beforeMoney = newestUser.getTotalMoney() - money;
				afterMoney = newestUser.getTotalMoney();
			}
			if(account == Global.BILL_ACCOUNT_LOTTERY) {
				beforeMoney = newestUser.getLotteryMoney() - money;
				afterMoney = newestUser.getLotteryMoney();
			}
			if(account == Global.BILL_ACCOUNT_BACCARAT) {
				beforeMoney = newestUser.getBaccaratMoney() - money;
				afterMoney = newestUser.getBaccaratMoney();
			}

			if (beforeMoney <= 0) {
				beforeMoney = 0;
			}
			if (afterMoney <= 0) {
				afterMoney = 0;
			}

			String refId = null;
			Moment thisTime = new Moment();
			UserBill tmpBill = new UserBill(billno, userId, account, type, money, beforeMoney, afterMoney, refType, refId, thisTime.toSimpleTime(), remarks);
			flag = uBillDao.add(tmpBill);
			if(flag) {
				if(account == Global.BILL_ACCOUNT_MAIN) {
					uMainReportService.update(userId, type, money, thisTime.toSimpleDate());
				}
				if(account == Global.BILL_ACCOUNT_LOTTERY) {
					uLotteryReportService.update(userId, type, money, thisTime.toSimpleDate());
				}
				// if(account == Global.BILL_ACCOUNT_BACCARAT) {
				// 	uBaccaratReportService.update(userId, type, money, thisTime.toSimpleDate());
				// }
			}
		} catch (Exception e) {
			logger.error("写入活动账单失败！", e);
			throw e;//在嵌套事物中必须抛出 不然无法触发roll-back
		}
		return flag;
	}
	
	@Override
	public boolean addIntegralBill(User user, int account, double amount, String remarks) {
		boolean flag = false;
		try {
			User newestUser = userDao.getById(user.getId());

			String billno = billno();
			int userId = newestUser.getId();
			int type = Global.BILL_TYPE_INTEGRAL;
			double money = amount;
			double beforeMoney = 0;
			double afterMoney = 0;
			if(account == Global.BILL_ACCOUNT_MAIN) {
				beforeMoney = newestUser.getTotalMoney() - money;
				afterMoney = newestUser.getTotalMoney();
			}
			if(account == Global.BILL_ACCOUNT_LOTTERY) {
				beforeMoney = newestUser.getLotteryMoney() - money;
				afterMoney = newestUser.getLotteryMoney();
			}
			if(account == Global.BILL_ACCOUNT_BACCARAT) {
				beforeMoney = newestUser.getBaccaratMoney() - money;
				afterMoney = newestUser.getBaccaratMoney();
			}

			if (beforeMoney <= 0) {
				beforeMoney = 0;
			}
			if (afterMoney <= 0) {
				afterMoney = 0;
			}

			Integer refType = null;
			String refId = null;
			Moment thisTime = new Moment();
			UserBill tmpBill = new UserBill(billno, userId, account, type, money, beforeMoney, afterMoney, refType, refId, thisTime.toSimpleTime(), remarks);
			flag = uBillDao.add(tmpBill);
			if(flag) {
				if(account == Global.BILL_ACCOUNT_MAIN) {
					uMainReportService.update(userId, type, money, thisTime.toSimpleDate());
				}
				if(account == Global.BILL_ACCOUNT_LOTTERY) {
					uLotteryReportService.update(userId, type, money, thisTime.toSimpleDate());
				}
				// if(account == Global.BILL_ACCOUNT_BACCARAT) {
				// 	uBaccaratReportService.update(userId, type, money, thisTime.toSimpleDate());
				// }
			}
		} catch (Exception e) {
			logger.error("写入活动账单失败！", e);
			throw e;//在嵌套事物中必须抛出 不然无法触发roll-back
		}
		return flag;
	}
	
	@Override
	public boolean addSpendBill(UserBets bBean, User user) {
		boolean flag = false;
		try {
			User newestUser = userDao.getById(user.getId());

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

			user.setLotteryMoney(afterMoney);//由于service设置事物获取最新金额应该从session中获取 by anson 2015/12/02
			Integer refType = type;
			String refId = String.valueOf(bBean.getId());
			Moment thisTime = new Moment();
			String remarks = "用户投注：" + bBean.getExpect();
			UserBill tmpBill = new UserBill(billno, userId, account, type, money, beforeMoney, afterMoney, refType, refId, thisTime.toSimpleTime(), remarks);
			flag = uBillDao.add(tmpBill);
			if(flag) {
				uLotteryReportService.update(userId, type, money, thisTime.toSimpleDate());
				uLotteryDetailsReportService.update(userId, bBean.getLotteryId(), bBean.getRuleId(), type, money, thisTime.toSimpleDate());
			}
		} catch (Exception e) {
			logger.error("写入彩票消费失败！", e);
			throw e;//在嵌套事物中必须抛出 不然无法触发roll-back
		}
		return flag;
	}
	
	@Override
	public boolean addCancelOrderBill(UserBets bBean, User user) {
		boolean flag = false;
		try {
			User newestUser = userDao.getById(user.getId());

			String billno = billno();
			int userId = newestUser.getId();
			int account = Global.BILL_ACCOUNT_LOTTERY;
			int type = Global.BILL_TYPE_CANCEL_ORDER;
			double money = bBean.getMoney();

			double beforeMoney = newestUser.getLotteryMoney() - money;
			double afterMoney = newestUser.getLotteryMoney();

			if (beforeMoney <= 0) {
				beforeMoney = 0;
			}
			if (afterMoney <= 0) {
				afterMoney = 0;
			}

			user.setLotteryMoney(afterMoney);//由于service设置事物获取最新金额应该从session中获取 by anson 2015/12/02
			Integer refType = type;
			String refId = String.valueOf(bBean.getId());
			Moment thisTime = new Moment();
			String remarks = "用户撤单：" + bBean.getExpect();
			UserBill tmpBill = new UserBill(billno, userId, account, type, money, beforeMoney, afterMoney, refType, refId, thisTime.toSimpleTime(), remarks);
			flag = uBillDao.add(tmpBill);
			if(flag) {
				uLotteryReportService.update(userId, type, money, thisTime.toSimpleDate());
				uLotteryDetailsReportService.update(userId, bBean.getLotteryId(), bBean.getRuleId(), type, money, thisTime.toSimpleDate());
			}
		} catch (Exception e) {
			logger.error("写入彩票撤单失败！", e);
			throw e;//在嵌套事物中必须抛出 不然无法触发roll-back
		}
		return flag;
	}
	
	@Override
	public boolean addDividendBill(int userId, int account, double amount, String remarks) {
		boolean flag = false;
		try {
			User newestUser = userDao.getById(userId);

			String billno = billno();
			int type = Global.BILL_TYPE_DIVIDEND;
			double money = amount;
			double beforeMoney = 0;
			if(account == Global.BILL_ACCOUNT_MAIN) {
				beforeMoney = newestUser.getTotalMoney();
			}
			if(account == Global.BILL_ACCOUNT_LOTTERY) {
				beforeMoney = newestUser.getLotteryMoney();
			}
			if(account == Global.BILL_ACCOUNT_BACCARAT) {
				beforeMoney = newestUser.getBaccaratMoney();
			}
			double afterMoney = beforeMoney + money;

			Integer refType = null;
			String refId = null;
			Moment thisTime = new Moment();
			UserBill tmpBill = new UserBill(billno, userId, account, type, Math.abs(money), beforeMoney, afterMoney, refType, refId, thisTime.toSimpleTime(), remarks);
			flag = uBillDao.add(tmpBill);
			if(flag) {
				uLotteryReportService.update(userId, type, money, thisTime.toSimpleDate());
			}
		} catch (Exception e) {
			logger.error("写入分红账单失败！", e);
			throw e;//在嵌套事物中必须抛出 不然无法触发roll-back
		}
		return flag;
	}
	
	@Override
	public boolean addRewardPayBill(User user, int account, double amount, String remarks) {
		boolean flag = false;
		try {
			User newestUser = userDao.getById(user.getId());

			String billno = billno();
			int userId = newestUser.getId();
			int type = Global.BILL_TYPE_REWARD_PAY;
			double money = amount;
			double beforeMoney = 0;
			double afterMoney = 0;
			if(account == Global.BILL_ACCOUNT_MAIN) {
				beforeMoney = newestUser.getTotalMoney() + money;
				afterMoney = newestUser.getTotalMoney();
			}
			if(account == Global.BILL_ACCOUNT_LOTTERY) {
				beforeMoney = newestUser.getLotteryMoney() + money;
				afterMoney = newestUser.getLotteryMoney();
			}
			if(account == Global.BILL_ACCOUNT_BACCARAT) {
				beforeMoney = newestUser.getBaccaratMoney() + money;
				afterMoney = newestUser.getBaccaratMoney();
			}

			if (beforeMoney <= 0) {
				beforeMoney = 0;
			}
			if (afterMoney <= 0) {
				afterMoney = 0;
			}

			Integer refType = null;
			String refId = null;
			Moment thisTime = new Moment();
			UserBill tmpBill = new UserBill(billno, userId, account, type, money, beforeMoney, afterMoney, refType, refId, thisTime.toSimpleTime(), remarks);
			flag = uBillDao.add(tmpBill);
			if(flag) {
				// TODO 需要加入对应报表
				if(account == Global.BILL_ACCOUNT_MAIN) {
					uMainReportService.update(userId, type, money, thisTime.toSimpleDate());
				}
				if(account == Global.BILL_ACCOUNT_LOTTERY) {
					uLotteryReportService.update(userId, type, money, thisTime.toSimpleDate());
				}
				// if(account == Global.BILL_ACCOUNT_BACCARAT) {
				// 	uBaccaratReportService.update(userId, type, money, thisTime.toSimpleDate());
				// }
			}
		} catch (Exception e) {
			logger.error("写入支付佣金账单失败！", e);
			throw e;//在嵌套事物中必须抛出 不然无法触发roll-back
		}
		return flag;
	}
	
	@Override
	public boolean addRewardIncomeBill(User user, int account, double amount, String remarks) {
		boolean flag = false;
		try {
			User newestUser = userDao.getById(user.getId());

			String billno = billno();
			int userId = newestUser.getId();
			int type = Global.BILL_TYPE_REWARD_INCOME;
			double money = amount;
			double beforeMoney = 0;
			double afterMoney = 0;
			if(account == Global.BILL_ACCOUNT_MAIN) {
				beforeMoney = newestUser.getTotalMoney() - money;
				afterMoney = newestUser.getTotalMoney();
			}
			if(account == Global.BILL_ACCOUNT_LOTTERY) {
				beforeMoney = newestUser.getLotteryMoney() - money;
				afterMoney = newestUser.getLotteryMoney();
			}
			if(account == Global.BILL_ACCOUNT_BACCARAT) {
				beforeMoney = newestUser.getBaccaratMoney() - money;
				afterMoney = newestUser.getBaccaratMoney();
			}

			if (beforeMoney <= 0) {
				beforeMoney = 0;
			}
			if (afterMoney <= 0) {
				afterMoney = 0;
			}

			Integer refType = null;
			String refId = null;
			Moment thisTime = new Moment();
			UserBill tmpBill = new UserBill(billno, userId, account, type, money, beforeMoney, afterMoney, refType, refId, thisTime.toSimpleTime(), remarks);
			flag = uBillDao.add(tmpBill);
			if(flag) {
				// TODO 需要加入对应报表
				if(account == Global.BILL_ACCOUNT_MAIN) {
					uMainReportService.update(userId, type, money, thisTime.toSimpleDate());
				}
				if(account == Global.BILL_ACCOUNT_LOTTERY) {
					uLotteryReportService.update(userId, type, money, thisTime.toSimpleDate());
				}
				// if(account == Global.BILL_ACCOUNT_BACCARAT) {
				// 	uBaccaratReportService.update(userId, type, money, thisTime.toSimpleDate());
				// }
			}
		} catch (Exception e) {
			logger.error("写入收取佣金账单失败！", e);
			throw e;//在嵌套事物中必须抛出 不然无法触发roll-back
		}
		return flag;
	}
	
	@Override
	public boolean addRewardReturnBill(User user, int account, double amount, String remarks) {
		boolean flag = false;
		try {
			User newestUser = userDao.getById(user.getId());

			String billno = billno();
			int userId = newestUser.getId();
			int type = Global.BILL_TYPE_REWARD_RETURN;
			double money = amount;
			double beforeMoney = 0;
			double afterMoney = 0;
			if(account == Global.BILL_ACCOUNT_MAIN) {
				beforeMoney = newestUser.getTotalMoney() - money;
				afterMoney = newestUser.getTotalMoney();
			}
			if(account == Global.BILL_ACCOUNT_LOTTERY) {
				beforeMoney = newestUser.getLotteryMoney() - money;
				afterMoney = newestUser.getLotteryMoney();
			}
			if(account == Global.BILL_ACCOUNT_BACCARAT) {
				beforeMoney = newestUser.getBaccaratMoney() - money;
				afterMoney = newestUser.getBaccaratMoney();
			}

			if (beforeMoney <= 0) {
				beforeMoney = 0;
			}
			if (afterMoney <= 0) {
				afterMoney = 0;
			}

			Integer refType = null;
			String refId = null;
			Moment thisTime = new Moment();
			UserBill tmpBill = new UserBill(billno, userId, account, type, money, beforeMoney, afterMoney, refType, refId, thisTime.toSimpleTime(), remarks);
			flag = uBillDao.add(tmpBill);
			if(flag) {
				// TODO 需要加入对应报表
				if(account == Global.BILL_ACCOUNT_MAIN) {
					uMainReportService.update(userId, type, money, thisTime.toSimpleDate());
				}
				if(account == Global.BILL_ACCOUNT_LOTTERY) {
					uLotteryReportService.update(userId, type, money, thisTime.toSimpleDate());
				}
				// if(account == Global.BILL_ACCOUNT_BACCARAT) {
				// 	uBaccaratReportService.update(userId, type, money, thisTime.toSimpleDate());
				// }
			}
		} catch (Exception e) {
			logger.error("写入退还佣金账单失败！", e);
			throw e;//在嵌套事物中必须抛出 不然无法触发roll-back
		}
		return flag;
	}

	@Override
	public boolean addUserWinBill(User user, int account, double amount,
			int refType, String remarks) {
		boolean flag = false;
		try {
			User newestUser = userDao.getById(user.getId());

			String billno = billno();
			int userId = newestUser.getId();
			int type = Global.BILL_TYPE_PRIZE;
			double money = amount;
			double beforeMoney = 0;
			double afterMoney = 0;
			if(account == Global.BILL_ACCOUNT_MAIN) {
				beforeMoney = newestUser.getTotalMoney() - money;
				afterMoney = newestUser.getTotalMoney();
			}
			if(account == Global.BILL_ACCOUNT_LOTTERY) {
				beforeMoney = newestUser.getLotteryMoney() - money;
				afterMoney = newestUser.getLotteryMoney();
			}
			if(account == Global.BILL_ACCOUNT_BACCARAT) {
				beforeMoney = newestUser.getBaccaratMoney() - money;
				afterMoney = newestUser.getBaccaratMoney();
			}

			if (beforeMoney <= 0) {
				beforeMoney = 0;
			}
			if (afterMoney <= 0) {
				afterMoney = 0;
			}

			String refId = null;
			Moment thisTime = new Moment();
			UserBill tmpBill = new UserBill(billno, userId, account, type, money, beforeMoney, afterMoney, refType, refId, thisTime.toSimpleTime(), remarks);
			flag = uBillDao.add(tmpBill);
			if(flag) {
				if(account == Global.BILL_ACCOUNT_MAIN) {
					uMainReportService.update(userId, type, money, thisTime.toSimpleDate());
				}
				if(account == Global.BILL_ACCOUNT_LOTTERY) {
					uLotteryReportService.update(userId, type, money, thisTime.toSimpleDate());
				}
				// if(account == Global.BILL_ACCOUNT_BACCARAT) {
				// 	uBaccaratReportService.update(userId, type, money, thisTime.toSimpleDate());
				// }
			}
		} catch (Exception e) {
			logger.error("写入账单失败！", e);
			throw e;//在嵌套事物中必须抛出 不然无法触发roll-back
		}
		return flag;
	}

	@Override
	public boolean add(UserBill bean) {
		return uBillDao.add(bean);
	}
}