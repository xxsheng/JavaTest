package lottery.domains.content.dao;

import java.util.List;

import lottery.domains.content.entity.LotteryOpenCode;

public interface LotteryOpenCodeDao {
	List<LotteryOpenCode> getLatest(String lotteryName, int count, Integer userId);

	boolean update(int id, String updateTime);
	
	LotteryOpenCode getByExcept(String except);
	
	LotteryOpenCode getByExcept(String lottery,String except);
	
	List<LotteryOpenCode> getOpenCodeByDate(String lotteryName, String sTime,String eTime);

}