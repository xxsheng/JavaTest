package lottery.domains.content.biz;

import lottery.domains.content.entity.UserBets;

public interface UserBillService {
	/**
	 * 添加投注账单
	 */
	boolean addSpendBill(UserBets bBean);
	/**
	 * 取消订单账单
	 */
	boolean addCancelOrderBill(UserBets bBean, String remarks);

	/**
	 * 添加用户赢账单
	 */
	boolean addUserWinBill(UserBets userBets, double amount, String remarks);

	/**
	 * 添加上级返点账单
	 */
	boolean addProxyReturnBill(UserBets userBets, int upId, double amount, String remarks);

	/**
	 * 添加投注返水
	 */
	boolean addSpendReturnBill(UserBets userBets, double amount, String remarks);
}