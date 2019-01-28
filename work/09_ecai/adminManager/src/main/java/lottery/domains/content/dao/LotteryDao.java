package lottery.domains.content.dao;

import java.util.List;

import lottery.domains.content.entity.Lottery;

public interface LotteryDao {
	
	List<Lottery> listAll();
	
	boolean updateStatus(int id, int status);

	boolean updateTimes(int id, int times);
	
	Lottery getById(int id);

	Lottery getByName(String name);
	
	Lottery getByShortName(String name);
}