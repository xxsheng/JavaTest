package lottery.domains.content.biz;

import javautils.jdbc.PageList;
import lottery.domains.content.entity.UserSysMessage;

public interface UserSysMessageService {
	
	boolean addTransToUser(int userId, double amount);
	
	boolean addOnlineRecharge(int userId, double amount);

	boolean addDividendRequest(int userId, int upId);

	boolean addDividendAgree(String username, int upId);

	boolean addDividendDeny(String username, int upId);

	boolean addDailySettleRequest(int userId, int upId);

	boolean addDailySettleAgree(String username, int upId);

	boolean addDailySettleDeny(String username, int upId);

	/**
	 * 添加首充活动消息
	 * @param userId 用户ID
	 * @param rechargeAmount 充值金额
	 * @param amount 赠送金额
	 * @return
	 */
	boolean addFirstRecharge(int userId, double rechargeAmount, double amount);

	boolean updateUnread(int userId, int[] ids);

	boolean deleteMsg(int userId, int[] ids);

	boolean add(UserSysMessage entity);
}