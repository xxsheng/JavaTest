package lottery.domains.content.biz;

import lottery.domains.content.entity.LotteryOpenCode;

import java.util.List;

public interface LotteryOpenCodeService {
	/**
	 * 修改为已开奖
	 */
	boolean updateOpened(LotteryOpenCode openCode);
	/**
	 * 修改为无效已撤单
	 */
	boolean updateCancelled(LotteryOpenCode openCode);

	/**
	 * 获取往期未开奖的期数
	 */
	List<LotteryOpenCode> getBeforeNotOpen(String lottery, int count);

	/**
	 * 根据彩票ID和期号获取开奖号码
	 */
	LotteryOpenCode getByExcept(String lottery, String except);

	/**
	 * 根据彩票ID和期号以及用户获取开奖号码
	 */
	LotteryOpenCode getByExceptAndUserId(String lottery, int userId, String except);

	/**
	 * 添加开奖号码，有则修改，没有就添加
	 */
	boolean add(LotteryOpenCode openCode);

	/**
	 * 查看某个彩种彩期是否已经抓取过了
	 */
	boolean hasCaptured(String lotteryName, String expect);

	/**
	 * 初始化开奖号码到redis中
	 */
	void initLotteryOpenCode();
}