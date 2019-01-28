package lottery.domains.content.dao;

import lottery.domains.content.entity.VipFreeChips;

public interface VipFreeChipsDao {
	
	VipFreeChips getByUserId(int userId);
	
	boolean updateReceived(int id, int isReceived);

}