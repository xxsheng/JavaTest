package lottery.domains.content.dao;

import java.util.List;

import lottery.domains.content.entity.LotteryType;

public interface LotteryTypeDao {
	
	List<LotteryType> listAll();
	
	boolean updateStatus(int id, int status);
	
}