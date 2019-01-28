package lottery.domains.content.biz;

import javautils.jdbc.PageList;

public interface VipUpgradeGiftsService {
	
	PageList search(String username, String date, Integer status, int start, int limit);
	
	/**
	 * 发放礼品
	 */
	boolean doIssuingGift(int userId, int beforeLevel, int afterLevel);
	
}