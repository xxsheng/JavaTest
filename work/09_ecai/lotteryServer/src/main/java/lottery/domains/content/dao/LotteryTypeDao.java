package lottery.domains.content.dao;

import java.util.List;

import lottery.domains.content.entity.LotteryType;

public interface LotteryTypeDao {
	
	List<LotteryType> listAll();
	
}