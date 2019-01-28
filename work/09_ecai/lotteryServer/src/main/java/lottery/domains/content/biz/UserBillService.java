package lottery.domains.content.biz;

import lottery.domains.content.entity.*;

public interface UserBillService {
	
	/**
	 * 充值账单
	 */
	boolean addRechargeBill(UserRecharge cBean, User uBean,  double receiveFeeMoney);
	
	/**
	 * 取款账单
	 */
	boolean addWithdrawBill(UserWithdraw wBean, int account, double amount, User uBean, String remarks);
	
	/**
	 * 转入账单
	 */
	boolean addTransInBill(UserTransfers tBean, User uBean, int account, String remarks, double beforeMoney, double afterMoney);
	
	/**
	 * 转出账单
	 */
	boolean addTransOutBill(UserTransfers tBean, User uBean, int account, String remarks, double beforeMoney, double afterMoney);
	
	/**
	 * 上下级充值（转入）
	 */
	boolean addUserTransInBill(User uBean, int account, double amount, String remarks);
	/**
	 * 上下级充值（转出）
	 */
	boolean addUserTransOutBill(User uBean, int account, double amount, String remarks);
	/**
	 * 活动账单
	 */
	boolean addActivityBill(User uBean, int account, double amount, int refType, String remarks);

	/**
	 * 派奖账单
	 */
	boolean addUserWinBill(User uBean, int account, double amount, int refType, String remarks);

	/**
	 * 红包账单
	 */
	boolean addPacketBill(User uBean, int account, double amount, int refType, String remarks);
	
	/**
	 * Vip兑换账单
	 */
	boolean addIntegralBill(User uBean, int account, double amount, String remarks);
	
	/**
	 * 彩票消费账单
	 */
	boolean addSpendBill(UserBets bBean, User uBean);
	
	/**
	 * 取消订单账单
	 */
	boolean addCancelOrderBill(UserBets bBean, User uBean);
	
	/**
	 * 分红账单
	 */
	boolean addDividendBill(int userId, int account, double amount, String remarks);
	
	/**
	 * 支付佣金
	 */
	boolean addRewardPayBill(User uBean, int account, double amount, String remarks);
	
	/**
	 * 收取佣金
	 */
	boolean addRewardIncomeBill(User uBean, int account, double amount, String remarks);
	
	/**
	 * 退还佣金
	 */
	boolean addRewardReturnBill(User uBean, int account, double amount, String remarks);
	
	boolean add(UserBill bean);
	
}