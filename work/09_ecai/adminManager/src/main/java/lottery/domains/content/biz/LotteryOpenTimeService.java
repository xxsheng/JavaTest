package lottery.domains.content.biz;

import javautils.jdbc.PageList;
import lottery.domains.content.entity.LotteryOpenTime;

public interface LotteryOpenTimeService {

	PageList search(String lottery, String expect, int start, int limit);
	
	boolean modify(int id, String startTime, String stopTime);

	boolean modifyRefExpect(String lottery, int count);

	boolean modify(String lottery, int seconds);

	LotteryOpenTime getByLottery(String lottery);

	boolean update(LotteryOpenTime entity);
}