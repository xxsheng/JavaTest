package lottery.domains.utils.lottery.open;

import java.util.List;

public interface OpenTimeUtil {
	
	/**
	 * 获取当前(销售期)开奖时间
	 * @return
	 */
	OpenTime getCurrOpenTime(int lotteryId, String currTime);
	
	/**
	 * 获取上一期开奖时间
	 * @return
	 */
	OpenTime getLastOpenTime(int lotteryId, String currTime);
	
	/**
	 * 获取开奖时间列表
	 * @param lotteryId
	 * @param count
	 * @return
	 */
	List<OpenTime> getOpenTimeList(int lotteryId, int count);
	
	/**
	 * 获取开奖时间列表
	 * @param date
	 * @return
	 */
	List<OpenTime> getOpenDateList(int lotteryId, String date);
	
	/**
	 * 根据期数来获取开奖时间
	 * @param lotteryId
	 * @param expect
	 * @return
	 */
	OpenTime getOpenTime(int lotteryId, String expect);

}