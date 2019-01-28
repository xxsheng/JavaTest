package lottery.domains.content.dao;

import lottery.domains.content.entity.LotteryOpenCode;

import java.util.List;

public interface LotteryOpenCodeDao {
	/**
	 * 修改为已开奖
	 */
	boolean updateOpened(int id, String updateTime);
	/**
	 * 修改为无效已撤单
	 */
	boolean updateCancelled(int id, String updateTime);

	List<LotteryOpenCode> getLatest(String lottery, int count);

	List<LotteryOpenCode> listAfter(String lottery, String expect);

	/**
	 * 获取往期未开奖的期数
	 */
	List<LotteryOpenCode> getBeforeNotOpen(String lottery, int count);
	
	LotteryOpenCode getByExcept(String lottery, String except);

	/**
	 * 根据彩票ID和期号以及用户获取开奖号码
	 */
	LotteryOpenCode getByExceptAndUserId(String lottery, int userId, String except);

	boolean add(LotteryOpenCode openCode);

	boolean update(LotteryOpenCode openCode);
}