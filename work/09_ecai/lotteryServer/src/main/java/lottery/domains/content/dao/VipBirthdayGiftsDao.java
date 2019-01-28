package lottery.domains.content.dao;

import lottery.domains.content.entity.VipBirthdayGifts;

public interface VipBirthdayGiftsDao {
	
	VipBirthdayGifts getByUserId(int userId);
	
	boolean updateReceived(int id, int isReceived);

}