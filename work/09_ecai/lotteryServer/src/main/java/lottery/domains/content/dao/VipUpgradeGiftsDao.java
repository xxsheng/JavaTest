package lottery.domains.content.dao;

import lottery.domains.content.entity.VipUpgradeGifts;

public interface VipUpgradeGiftsDao {

	VipUpgradeGifts getByUserId(int userId);
	
	boolean updateReceived(int id, int isReceived);
	
}