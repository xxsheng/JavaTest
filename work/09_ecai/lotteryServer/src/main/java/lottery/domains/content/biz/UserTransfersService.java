package lottery.domains.content.biz;

import lottery.web.WebJSON;

public interface UserTransfersService {
	
	/**
	 * 自己账户转账，1：主账户，2：彩票账户，11：PT账户，4：AG账户
	 * @param json
	 * @param userId
	 * @param toAccount
	 * @param fromAccount
	 * @param amount
	 * @return
	 */
	boolean transfersToSelf(WebJSON json, int userId, int toAccount, int fromAccount, double amount);

	/**
	 * 转账级下级，上级从主账户中扣除，并给下级加到彩票账户中
	 * @return
	 */
	boolean transfersToUser(int toUser, int fromUser, double amount,int transferType);

	/**
	 * 资金归集
	 */
	boolean transferAll(WebJSON json, int userId);
}