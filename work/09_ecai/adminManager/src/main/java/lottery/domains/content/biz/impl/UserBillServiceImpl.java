package lottery.domains.content.biz.impl;

import javautils.StringUtil;
import javautils.date.Moment;
import javautils.jdbc.PageList;
import javautils.math.MathUtil;
import lottery.domains.content.biz.*;
import lottery.domains.content.dao.*;
import lottery.domains.content.entity.*;
import lottery.domains.content.global.Global;
import lottery.domains.content.vo.bill.HistoryUserBillVO;
import lottery.domains.content.vo.bill.UserBillVO;
import lottery.domains.pool.LotteryDataFactory;
import org.apache.commons.collections.CollectionUtils;
import org.bson.types.ObjectId;
import org.hibernate.criterion.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class UserBillServiceImpl implements UserBillService {

	private static final Logger logger = LoggerFactory.getLogger(UserBillServiceImpl.class);
	
	/**
	 * DAO
	 */
	@Autowired
	private UserDao uDao;
	
	@Autowired
	private UserBillDao uBillDao;
	
	@Autowired
	private UserBetsDao uBetsDao;

	@Autowired
	private UserRechargeDao uRechargeDao;

	@Autowired
	private UserWithdrawDao uWithdrawDao;
	
	/**
	 * SERVICE
	 */
	@Autowired
	private UserMainReportService uMainReportService;

	@Autowired
	private UserGameReportService uGameReportService;
	
	@Autowired
	private UserLotteryReportService uLotteryReportService;
	
	@Autowired
	private UserLotteryDetailsReportService uLotteryDetailsReportService;
	
	@Autowired
	private UserBaccaratReportService uBaccaratReportService;
	
	/**
	 * DataFactory
	 */
	@Autowired
	private LotteryDataFactory lotteryDataFactory;
	
	/**
	 * 生成订单号
	 */
	private String billno() {
		// return new Moment().format("yyyyMMdd") + OrderUtil.getBillno(24, true);
		return ObjectId.get().toString();
	}
	
	@Override
	public boolean addRechargeBill(UserRecharge cBean, User uBean, String remarks) {
		boolean flag = false;
		try {
			String billno = billno();
			int userId = uBean.getId();
			//2016-09-20 现在改为直接充值到彩票账户
			//int account = Global.BILL_ACCOUNT_MAIN;
			int account = Global.BILL_ACCOUNT_LOTTERY;
			int type = Global.BILL_TYPE_RECHARGE;
			double money = cBean.getRecMoney();
			//2016-09-20 现在改为直接充值到彩票账户
			//double beforeMoney = uBean.getTotalMoney();
			double beforeMoney = uBean.getLotteryMoney();
			double afterMoney = beforeMoney + money;
			Integer refType = type;
			String refId = cBean.getBillno();
			Moment thisTime = new Moment();
			UserBill tmpBill = new UserBill(billno, userId, account, type, money, beforeMoney, afterMoney, refType, refId, thisTime.toSimpleTime(), remarks);
			flag = uBillDao.add(tmpBill);
			if(flag) {
				uMainReportService.update(userId, type, money, thisTime.toSimpleDate());
				// 将充值手续费计算到优惠中
				if (lotteryDataFactory.getRechargeConfig().getFeePercent() > 0) {
					double feeAmount = MathUtil.multiply(money, lotteryDataFactory.getRechargeConfig().getFeePercent());
					if (feeAmount > 0) {
						uLotteryReportService.updateRechargeFee(uBean.getId(), feeAmount, thisTime.toSimpleDate());
					}
				}
			}
		} catch (Exception e) {
			logger.error("写入存款账单失败！", e);
		}
		return flag;
	}
	
	@Override
	public boolean addWithdrawReport(UserWithdraw wBean) {
		boolean flag = false;
		try {
			int userId = wBean.getUserId();
			int type = Global.BILL_TYPE_WITHDRAWALS;
			double money = wBean.getMoney();
			Moment thisTime = new Moment();
			uMainReportService.update(userId, type, money, thisTime.toSimpleDate());
		} catch (Exception e) {
			logger.error("写入取款账单失败！", e);
		}
		return flag;
	}
	
	@Override
	public boolean addDrawBackBill(UserWithdraw wBean, User uBean, String remarks) {
		boolean flag = false;
		try {
			String billno = billno();
			int userId = wBean.getUserId();
			int account = Global.BILL_ACCOUNT_LOTTERY;
			int type = Global.BILL_TYPE_DRAW_BACK;
			double money = wBean.getMoney();
			double beforeMoney = uBean.getLotteryMoney();
			double afterMoney = beforeMoney + money;
			Integer refType = null;
			String refId = wBean.getBillno();
			Moment thisTime = new Moment();
			UserBill tmpBill = new UserBill(billno, userId, account, type, money, beforeMoney, afterMoney, refType, refId, thisTime.toSimpleTime(), remarks);
			return uBillDao.add(tmpBill);
		} catch (Exception e) {
			logger.error("写入取款账单失败！", e);
		}
		return flag;
	}
	
	@Override
	public boolean addTransInBill(UserTransfers tBean, User uBean, int account, String remarks) {
		boolean flag = false;
		try {
			String billno = billno();
			int userId = uBean.getId();
			int type = Global.BILL_TYPE_TRANS_IN;
			double money = tBean.getMoney();
			double beforeMoney = 0;
			if(account == Global.BILL_ACCOUNT_MAIN) {
				beforeMoney = uBean.getTotalMoney();
			}
			if(account == Global.BILL_ACCOUNT_LOTTERY) {
				beforeMoney = uBean.getLotteryMoney();
			}
			if(account == Global.BILL_ACCOUNT_BACCARAT) {
				beforeMoney = uBean.getBaccaratMoney();
			}
			double afterMoney = beforeMoney + money;
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
				if(account == Global.BILL_ACCOUNT_BACCARAT) {
					uBaccaratReportService.update(userId, type, money, thisTime.toSimpleDate());
				}
			}
		} catch (Exception e) {
			logger.error("写入转入账单失败！", e);
		}
		return flag;
	}
	
	@Override
	public boolean addTransOutBill(UserTransfers tBean, User uBean, int account, String remarks) {
		boolean flag = false;
		try {
			String billno = billno();
			int userId = uBean.getId();
			int type = Global.BILL_TYPE_TRANS_OUT;
			double money = tBean.getMoney();
			double beforeMoney = 0;
			if(account == Global.BILL_ACCOUNT_MAIN) {
				beforeMoney = uBean.getTotalMoney();
			}
			if(account == Global.BILL_ACCOUNT_LOTTERY) {
				beforeMoney = uBean.getLotteryMoney();
			}
			if(account == Global.BILL_ACCOUNT_BACCARAT) {
				beforeMoney = uBean.getBaccaratMoney();
			}
			double afterMoney = beforeMoney - money;
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
				if(account == Global.BILL_ACCOUNT_BACCARAT) {
					uBaccaratReportService.update(userId, type, money, thisTime.toSimpleDate());
				}
			}
		} catch (Exception e) {
			logger.error("写入转出账单失败！", e);
		}
		return flag;
	}
	
	@Override
	public boolean addActivityBill(User uBean, int account, double amount, int refType, String remarks) {
		boolean flag = false;
		try {
			String billno = billno();
			int userId = uBean.getId();
			int type = Global.BILL_TYPE_ACTIVITY;
			double money = amount;
			double beforeMoney = 0;
			if(account == Global.BILL_ACCOUNT_MAIN) {
				beforeMoney = uBean.getTotalMoney();
			}
			if(account == Global.BILL_ACCOUNT_LOTTERY) {
				beforeMoney = uBean.getLotteryMoney();
			}
			if(account == Global.BILL_ACCOUNT_BACCARAT) {
				beforeMoney = uBean.getBaccaratMoney();
			}
			double afterMoney = beforeMoney + money;
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
				if(account == Global.BILL_ACCOUNT_BACCARAT) {
					uBaccaratReportService.update(userId, type, money, thisTime.toSimpleDate());
				}
			}
		} catch (Exception e) {
			logger.error("写入活动账单失败！", e);
		}
		return flag;
	}
	
	@Override
	public boolean addAdminAddBill(User uBean, int account, double amount, String remarks) {
		boolean flag = false;
		try {
			String billno = billno();
			int userId = uBean.getId();
			int type = Global.BILL_TYPE_ADMIN_ADD;
			double money = amount;
			double beforeMoney = 0;
			if(account == Global.BILL_ACCOUNT_MAIN) {
				beforeMoney = uBean.getTotalMoney();
			}
			if(account == Global.BILL_ACCOUNT_LOTTERY) {
				beforeMoney = uBean.getLotteryMoney();
			}
			if(account == Global.BILL_ACCOUNT_BACCARAT) {
				beforeMoney = uBean.getBaccaratMoney();
			}
			double afterMoney = beforeMoney + money;
			Integer refType = null;
			String refId = null;
			Moment thisTime = new Moment();
			UserBill tmpBill = new UserBill(billno, userId, account, type, money, beforeMoney, afterMoney, refType, refId, thisTime.toSimpleTime(), remarks);
			return uBillDao.add(tmpBill);
		} catch (Exception e) {
			logger.error("写入管理员增加资金失败！", e);
		}
		return flag;
	}
	
	@Override
	public boolean addAdminMinusBill(User uBean, int account, double amount, String remarks) {
		boolean flag = false;
		try {
			String billno = billno();
			int userId = uBean.getId();
			int type = Global.BILL_TYPE_ADMIN_MINUS;
			double money = amount;
			double beforeMoney = 0;
			if(account == Global.BILL_ACCOUNT_MAIN) {
				beforeMoney = uBean.getTotalMoney();
			}
			if(account == Global.BILL_ACCOUNT_LOTTERY) {
				beforeMoney = uBean.getLotteryMoney();
			}
			if(account == Global.BILL_ACCOUNT_BACCARAT) {
				beforeMoney = uBean.getBaccaratMoney();
			}
			double afterMoney = beforeMoney - money;
			Integer refType = null;
			String refId = null;
			Moment thisTime = new Moment();
			UserBill tmpBill = new UserBill(billno, userId, account, type, money, beforeMoney, afterMoney, refType, refId, thisTime.toSimpleTime(), remarks);
			return uBillDao.add(tmpBill);
		} catch (Exception e) {
			logger.error("写入管理员减少资金失败！", e);
		}
		return flag;
	}
	
	@Override
	public boolean addSpendBill(UserBets bBean, User uBean) {
		boolean flag = false;
		try {
			String billno = billno();
			int userId = uBean.getId();
			int account = Global.BILL_ACCOUNT_LOTTERY;
			int type = Global.BILL_TYPE_SPEND;
			double money = bBean.getMoney();
			double beforeMoney = uBean.getLotteryMoney();
			double afterMoney = beforeMoney - money;
			Integer refType = null;
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
		}
		return flag;
	}
	
	@Override
	public boolean addCancelOrderBill(UserBets bBean, User uBean) {
		boolean flag = false;
		try {
			String billno = billno();
			int userId = uBean.getId();
			int account = Global.BILL_ACCOUNT_LOTTERY;
			int type = Global.BILL_TYPE_CANCEL_ORDER;
			double money = bBean.getMoney();
			double beforeMoney = uBean.getLotteryMoney();
			double afterMoney = beforeMoney + money;
			Integer refType = null;
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
		}
		return flag;
	}
	
	@Override
	public boolean addDividendBill(User uBean, int account, double amount, String remarks, boolean activity) {
		boolean flag = false;
		try {
			String billno = billno();
			int userId = uBean.getId();
			int type = Global.BILL_TYPE_DIVIDEND;
			double money = amount;
			double beforeMoney = 0;
			if(account == Global.BILL_ACCOUNT_MAIN) {
				beforeMoney = uBean.getTotalMoney();
			}
			if(account == Global.BILL_ACCOUNT_LOTTERY) {
				beforeMoney = uBean.getLotteryMoney();
			}
			if(account == Global.BILL_ACCOUNT_BACCARAT) {
				beforeMoney = uBean.getBaccaratMoney();
			}
			double afterMoney = beforeMoney + money;
			Integer refType = null;
			String refId = null;
			Moment thisTime = new Moment();
			UserBill tmpBill = new UserBill(billno, userId, account, type, Math.abs(money), beforeMoney, afterMoney, refType, refId, thisTime.toSimpleTime(), remarks);
			flag = uBillDao.add(tmpBill);
			if(flag && activity) {
				if(account == Global.BILL_ACCOUNT_MAIN) {
					uMainReportService.update(userId, type, money, thisTime.toSimpleDate());
				}
				if(account == Global.BILL_ACCOUNT_LOTTERY) {
					uLotteryReportService.update(userId, type, money, thisTime.toSimpleDate());
				}
				if(account == Global.BILL_ACCOUNT_BACCARAT) {
					uBaccaratReportService.update(userId, type, money, thisTime.toSimpleDate());
				}
			}
		} catch (Exception e) {
			logger.error("写入分红账单失败！", e);
		}
		return flag;
	}
	
	@Override
	public boolean addRewardPayBill(User uBean, int account, double amount, String remarks) {
		boolean flag = false;
		try {
			String billno = billno();
			int userId = uBean.getId();
			int type = Global.BILL_TYPE_REWARD_PAY;
			double money = amount;
			double beforeMoney = 0;
			if(account == Global.BILL_ACCOUNT_MAIN) {
				beforeMoney = uBean.getTotalMoney();
			}
			if(account == Global.BILL_ACCOUNT_LOTTERY) {
				beforeMoney = uBean.getLotteryMoney();
			}
			if(account == Global.BILL_ACCOUNT_BACCARAT) {
				beforeMoney = uBean.getBaccaratMoney();
			}
			double afterMoney = beforeMoney - money;
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
				if(account == Global.BILL_ACCOUNT_BACCARAT) {
					uBaccaratReportService.update(userId, type, money, thisTime.toSimpleDate());
				}
			}
		} catch (Exception e) {
			logger.error("写入支付佣金账单失败！", e);
		}
		return flag;
	}
	
	@Override
	public boolean addRewardIncomeBill(User uBean, int account, double amount, String remarks) {
		boolean flag = false;
		try {
			String billno = billno();
			int userId = uBean.getId();
			int type = Global.BILL_TYPE_REWARD_INCOME;
			double money = amount;
			double beforeMoney = 0;
			if(account == Global.BILL_ACCOUNT_MAIN) {
				beforeMoney = uBean.getTotalMoney();
			}
			if(account == Global.BILL_ACCOUNT_LOTTERY) {
				beforeMoney = uBean.getLotteryMoney();
			}
			if(account == Global.BILL_ACCOUNT_BACCARAT) {
				beforeMoney = uBean.getBaccaratMoney();
			}
			double afterMoney = beforeMoney + money;
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
				if(account == Global.BILL_ACCOUNT_BACCARAT) {
					uBaccaratReportService.update(userId, type, money, thisTime.toSimpleDate());
				}
			}
		} catch (Exception e) {
			logger.error("写入收取佣金账单失败！", e);
		}
		return flag;
	}
	
	@Override
	public boolean addRewardReturnBill(User uBean, int account, double amount, String remarks) {
		boolean flag = false;
		try {
			String billno = billno();
			int userId = uBean.getId();
			int type = Global.BILL_TYPE_REWARD_RETURN;
			double money = amount;
			double beforeMoney = 0;
			if(account == Global.BILL_ACCOUNT_MAIN) {
				beforeMoney = uBean.getTotalMoney();
			}
			if(account == Global.BILL_ACCOUNT_LOTTERY) {
				beforeMoney = uBean.getLotteryMoney();
			}
			if(account == Global.BILL_ACCOUNT_BACCARAT) {
				beforeMoney = uBean.getBaccaratMoney();
			}
			double afterMoney = beforeMoney + money;
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
				if(account == Global.BILL_ACCOUNT_BACCARAT) {
					uBaccaratReportService.update(userId, type, money, thisTime.toSimpleDate());
				}
			}
		} catch (Exception e) {
			logger.error("写入退还佣金账单失败！", e);
		}
		return flag;
	}

	@Override
	public boolean addDailySettleBill(User uBean, int account, double amount, String remarks, boolean activity) {
		boolean flag = false;
		try {
			String billno = billno();
			int userId = uBean.getId();
			int type = Global.BILL_TYPE_DAILY_SETTLE;
			double money = amount;
			double beforeMoney = 0;
			if(account == Global.BILL_ACCOUNT_MAIN) {
				beforeMoney = uBean.getTotalMoney();
			}
			if(account == Global.BILL_ACCOUNT_LOTTERY) {
				beforeMoney = uBean.getLotteryMoney();
			}
			if(account == Global.BILL_ACCOUNT_BACCARAT) {
				beforeMoney = uBean.getBaccaratMoney();
			}
			double afterMoney = beforeMoney + money;
			Integer refType = null;
			String refId = null;
			Moment thisTime = new Moment();
			UserBill tmpBill = new UserBill(billno, userId, account, type, Math.abs(money), beforeMoney, afterMoney, refType, refId, thisTime.toSimpleTime(), remarks);
			flag = uBillDao.add(tmpBill);
			if(flag && activity) { // 需要更新报表
				if(account == Global.BILL_ACCOUNT_MAIN) {
					uMainReportService.update(userId, Global.BILL_TYPE_ACTIVITY, Math.abs(money), thisTime.toSimpleDate());
				}
				if(account == Global.BILL_ACCOUNT_LOTTERY) {
					uLotteryReportService.update(userId, Global.BILL_TYPE_ACTIVITY, Math.abs(money), thisTime.toSimpleDate());
				}
				if(account == Global.BILL_ACCOUNT_BACCARAT) {
					uBaccaratReportService.update(userId, Global.BILL_TYPE_ACTIVITY, Math.abs(money), thisTime.toSimpleDate());
				}
			}
		} catch (Exception e) {
			logger.error("写入退还佣金账单失败！", e);
		}
		return flag;
	}

	@Override
	public boolean addGameWaterBill(User uBean, int account, int type, double amount, String remarks) {
		boolean flag = false;
		try {
			String billno = billno();
			int userId = uBean.getId();
			double money = amount;
			double beforeMoney = 0;

			int _type = type == Global.GAME_WATER_BILL_TYPE_USER ? Global.BILL_TYPE_WATER_RETURN : Global.BILL_TYPE_PROXY_RETURN;

			if(account == Global.BILL_ACCOUNT_MAIN) {
				beforeMoney = uBean.getTotalMoney();
			}
			if(account == Global.BILL_ACCOUNT_LOTTERY) {
				beforeMoney = uBean.getLotteryMoney();
			}
			if(account == Global.BILL_ACCOUNT_BACCARAT) {
				beforeMoney = uBean.getBaccaratMoney();
			}
			double afterMoney = beforeMoney + money;
			Integer refType = null;
			String refId = null;
			Moment thisTime = new Moment();
			UserBill tmpBill = new UserBill(billno, userId, account, _type, money, beforeMoney, afterMoney, refType, refId, thisTime.toSimpleTime(), remarks);
			flag = uBillDao.add(tmpBill);
			if(flag) {
				if (type == Global.GAME_WATER_BILL_TYPE_USER) {
					uGameReportService.update(userId, Global.BILL_ACCOUNT_AG, 0, 0, amount, 0, thisTime.toSimpleDate());
				}
				else {
					uGameReportService.update(userId, Global.BILL_ACCOUNT_AG, 0, 0, 0, amount, thisTime.toSimpleDate());
				}
			}
		} catch (Exception e) {
			logger.error("写入退还佣金账单失败！", e);
		}
		return flag;
	}

	@Override
	@Transactional(readOnly = true)
	public PageList search(String keyword, String username, Integer utype,Integer type,
			String minTime, String maxTime, Double minMoney, Double maxMoney,
			Integer status, int start, int limit) {
		boolean isSearch = true;
		User targetUser = null;
		StringBuilder sqlStr = new StringBuilder();
		if(StringUtil.isNotNull(username)) {
			targetUser = uDao.getByUsername(username);
			if(targetUser != null) {
				sqlStr.append("	and b.user_id = ").append(targetUser.getId());
			} else {
				isSearch = false;
			}
		}
		if(StringUtil.isNotNull(keyword)) {
			// 投注
			List<UserBets> tmpBets = uBetsDao.getByBillno(keyword, false);
			UserBets uBets = CollectionUtils.isNotEmpty(tmpBets) ? tmpBets.get(0) : null;
			if (uBets != null) {
				sqlStr.append("	and b.ref_id = ").append("'"+uBets.getId()+"'");
				sqlStr.append("	and b.account = ").append(2);
				sqlStr.append("	and b.type  in ").append("(6,7,8,9,10)");
			}

			// 存款
			// if (uBets == null) {
			// 	UserRecharge uRecharge = uRechargeDao.getByBillno(keyword);
			// 	if (uRecharge != null) {
			// 		Conjunction betsConditions = Restrictions.and();
			// 		betsConditions.add(Restrictions.eq("refId", keyword));
			// 		betsConditions.add(Restrictions.eq("type", new Integer[] {6,7,8,9,10}));
			// 	}
			// }
            //
            //
			// // 取款
			// UserWithdraw uWithdraw = uWithdrawDao.getByBillno(keyword);
            //
			// criterions.add(Restrictions.eq("refId", keyword));

			// boolean isOrder = false;
			//
			// Conjunction conjunctionBill = Restrictions.conjunction();
			// conjunctionBill.add(Restrictions.eq("billno", keyword));
			//
			// Conjunction conjunctionOrder = Restrictions.conjunction();
			//
			// List<UserBets> tmpBets = uBetsDao.getByBillno(keyword, false);
			// if(tmpBets.size() > 0) {
			// 	isOrder = true;
			// 	conjunctionOrder.add(Restrictions.eq("account", 2));
			// 	conjunctionOrder.add(Restrictions.in("type", new Integer[] {6,7,9,9,10}));
			// 	List<String> targetIds = new ArrayList<String>();
			// 	for (UserBets tmpBean : tmpBets) {
			// 		targetIds.add(String.valueOf(tmpBean.getId()));
			// 	}
			// 	conjunctionOrder.add(Restrictions.in("refId", targetIds));
			// }
			//
			// if(isOrder) {
			// 	criterions.add(conjunctionOrder);
			// } else {
			// 	criterions.add(conjunctionBill);
			// }
		}
		if(type != null) {
			sqlStr.append(" and b.type  =").append(type.intValue());
		}
		
		if(utype != null) {
			sqlStr.append(" and u.type  =").append(utype.intValue());
		}
		else{
			sqlStr.append("  and u.upid != ").append(0);
		}
		if(StringUtil.isNotNull(minTime)) {
			sqlStr.append(" and b.time  >=").append("'"+minTime+"'");
		}
		if(StringUtil.isNotNull(maxTime)) {
			sqlStr.append(" and b.time  <=").append("'"+maxTime+"'");
		}
		if(minMoney != null) {
			sqlStr.append("  and ABS(b.money) >= ").append(minMoney.doubleValue());
		}
		if(maxMoney != null) {
			sqlStr.append("  and ABS(b.money) <= ").append(maxMoney.doubleValue());
		}
		if(status != null) {
			sqlStr.append("  and b.status = ").append(status.intValue());
		}
	//	String nickname = "试玩用户";
/*		if(type != null){
			sqlStr.append("  and u.type = ").append(type);
		}else{
			sqlStr.append("  and u.upid != ").append(0);
		}*/
		
		sqlStr.append("   ORDER BY  b.time,  b.id desc ");

		if(isSearch) {
			List<UserBillVO> list = new ArrayList<>();
			PageList pList = uBillDao.findNoDemoUserBill(sqlStr.toString(), start, limit);
			for (Object tmpBean : pList.getList()) {
				list.add(new UserBillVO((UserBill) tmpBean, lotteryDataFactory));
			}
			pList.setList(list);
			return pList;
		}
		return null;
	}
	@Override
	@Transactional(readOnly = true)
	public PageList searchHistory(String keyword, String username, Integer type,
			String minTime, String maxTime, Double minMoney, Double maxMoney,
			Integer status, int start, int limit) {
		List<Criterion> criterions = new ArrayList<>();
		List<Order> orders = new ArrayList<>();
		boolean isSearch = true;
		User targetUser = null;
		if(StringUtil.isNotNull(username)) {
			targetUser = uDao.getByUsername(username);
			if(targetUser != null) {
				criterions.add(Restrictions.eq("userId", targetUser.getId()));
			} else {
				isSearch = false;
			}
		}
		if(StringUtil.isNotNull(keyword)) {
			boolean isOrder = false;
			
			Conjunction conjunctionBill = Restrictions.conjunction();
			conjunctionBill.add(Restrictions.like("billno", keyword, MatchMode.ANYWHERE));
			
			Conjunction conjunctionOrder = Restrictions.conjunction();
			
			List<HistoryUserBets> tmpBets = uBetsDao.getHistoryByBillno(keyword, false);
			if(tmpBets.size() > 0) {
				isOrder = true;
				conjunctionOrder.add(Restrictions.eq("account", 2));
				conjunctionOrder.add(Restrictions.in("type", new Integer[] {6,7,9,9,10}));
				List<String> targetIds = new ArrayList<String>();
				for (HistoryUserBets tmpBean : tmpBets) {
					targetIds.add(String.valueOf(tmpBean.getId()));
				}
				conjunctionOrder.add(Restrictions.in("refId", targetIds));
			}
			
			if(isOrder) {
				criterions.add(conjunctionOrder);
			} else {
				criterions.add(conjunctionBill);
			}
		}
		if(type != null) {
			criterions.add(Restrictions.eq("type", type.intValue()));
		}
		if(StringUtil.isNotNull(minTime)) {
			criterions.add(Restrictions.ge("time", minTime));
		}
		if(StringUtil.isNotNull(maxTime)) {
			criterions.add(Restrictions.lt("time", maxTime));
		}
		if(minMoney != null) {
			criterions.add(Restrictions.sqlRestriction("ABS(money) >= " + minMoney.doubleValue()));
		}
		if(maxMoney != null) {
			criterions.add(Restrictions.sqlRestriction("ABS(money) <= " + maxMoney.doubleValue()));
		}
		if(status != null) {
			criterions.add(Restrictions.eq("status", status.intValue()));
		}
		orders.add(Order.desc("time"));
		orders.add(Order.desc("id"));
		if(isSearch) {
			List<HistoryUserBillVO> list = new ArrayList<>();
			PageList pList = uBillDao.findHistory(criterions, orders, start, limit);
			for (Object tmpBean : pList.getList()) {
				HistoryUserBillVO tmpVO = new HistoryUserBillVO((HistoryUserBill) tmpBean, lotteryDataFactory);
				list.add(tmpVO); 
			}
			pList.setList(list);
			return pList;
		}
		return null;
	}
	@Override
	@Transactional(readOnly = true)
	public List<UserBillVO> getLatest(int userId, int type, int count) {
		List<UserBillVO> formatList = new ArrayList<>();
		List<UserBill> list = uBillDao.getLatest(userId, type, count);
		for (UserBill tmpBean : list) {
			formatList.add(new UserBillVO(tmpBean, lotteryDataFactory));
		}
		return formatList;
	}
	
}