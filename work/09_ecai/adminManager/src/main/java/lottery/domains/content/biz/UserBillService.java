package lottery.domains.content.biz;

import java.util.List;

import javautils.jdbc.PageList;
import lottery.domains.content.entity.User;
import lottery.domains.content.entity.UserBets;
import lottery.domains.content.entity.UserRecharge;
import lottery.domains.content.entity.UserTransfers;
import lottery.domains.content.entity.UserWithdraw;
import lottery.domains.content.vo.bill.UserBillVO;

public interface UserBillService {
	
	/**
	 * 充值账单
	 */
	boolean addRechargeBill(UserRecharge cBean, User uBean, String remarks);
	
	/**
	 * 取款报表
	 */
	boolean addWithdrawReport(UserWithdraw wBean);
	
	/**
	 * 取款退回
	 */
	boolean addDrawBackBill(UserWithdraw wBean, User uBean, String remarks);
	
	/**
	 * 转入账单
	 */
	boolean addTransInBill(UserTransfers tBean, User uBean, int account, String remarks);
	
	/**
	 * 转出账单
	 */
	boolean addTransOutBill(UserTransfers tBean, User uBean, int account, String remarks);
	
	/**
	 * 活动账单
	 */
	boolean addActivityBill(User uBean, int account, double amount, int refType, String remarks);
	
	/**
	 * 管理员增
	 */
	boolean addAdminAddBill(User uBean, int account, double amount, String remarks);
	
	/**
	 * 管理员减
	 */
	boolean addAdminMinusBill(User uBean, int account, double amount, String remarks);
	
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
	boolean addDividendBill(User uBean, int account, double amount, String remarks, boolean activity);
	
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

	/**
	 * 日结
	 */
	boolean addDailySettleBill(User uBean, int account, double amount, String remarks, boolean activity);

	/**
	 * 游戏返水
	 */
	boolean addGameWaterBill(User uBean, int account, int type, double amount, String remarks);

	PageList search(String keyword, String username, Integer utype,  Integer type, String minTime, String maxTime, Double minMoney, Double maxMoney, Integer status, int start, int limit);
	
	PageList searchHistory(String keyword, String username, Integer type, String minTime, String maxTime, Double minMoney, Double maxMoney, Integer status, int start, int limit);
	
	List<UserBillVO> getLatest(int userId, int type, int count);
}