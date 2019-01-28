package lottery.domains.content.biz;

public interface UserSysMessageService {
	
	boolean addTransToUser(int userId, double amount);
	
	boolean addSysRecharge(int userId, double amount, String remarks);
	
	boolean addOnlineRecharge(int userId, double amount);

	boolean addTransfersRecharge(int userId, double amount);
	
	boolean addConfirmWithdraw(int userId, double amount, double recAmount);
	/**
	 * 提现拒绝
	 * @param userId
	 * @param amount
	 * @return
	 */
	boolean addRefuseWithdraw(int userId, double amount);
	/**
	 * 提现失败
	 * @param userId
	 * @param amount
	 * @return
	 */
	boolean addRefuse(int userId, double amount);
	
	/**
	 * 审核失败
	 * @param userId
	 * @param amount
	 * @return
	 */
	boolean addShFail(int userId, double amount);
	
	/**
	 * 添加首充活动消息
	 * @param userId 用户ID
	 * @param rechargeAmount 充值金额
	 * @param amount 赠送金额
	 * @return
	 */
	boolean addFirstRecharge(int userId, double rechargeAmount, double amount);
	

	boolean addActivityBind(int userId, double amount);

	boolean addActivityRecharge(int userId, double amount);
	
	boolean addRewardMessage(int userId, String date);
	
	boolean addVipLevelUp(int userId, String level);

	boolean addDividendBill(int userId, String startDate, String endDate);

	boolean addGameDividendBill(int userId, String startDate, String endDate);

	boolean addDailySettleBill(int userId, String date);

	boolean addGameWaterBill(int userId, String date, String fromUser, String toUser);
}