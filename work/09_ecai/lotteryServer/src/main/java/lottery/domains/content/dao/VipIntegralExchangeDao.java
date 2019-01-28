package lottery.domains.content.dao;

import lottery.domains.content.entity.VipIntegralExchange;

public interface VipIntegralExchangeDao {
	
	boolean add(VipIntegralExchange entity);
	
	int getDateCount(int userId, String date);

}