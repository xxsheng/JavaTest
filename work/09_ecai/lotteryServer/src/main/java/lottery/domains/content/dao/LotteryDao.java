package lottery.domains.content.dao;

import java.util.List;

import lottery.domains.content.entity.Lottery;

public interface LotteryDao {
	
	List<Lottery> listAll();
	
}