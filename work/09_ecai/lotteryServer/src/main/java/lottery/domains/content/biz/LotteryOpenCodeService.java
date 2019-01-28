package lottery.domains.content.biz;

import java.util.List;

import lottery.domains.content.entity.LotteryOpenCode;
import lottery.domains.content.vo.lottery.LotteryOpenCodeVO;

public interface LotteryOpenCodeService {
	/**
	 * 从redis中获取最近的开奖号码
	 */
	List<LotteryOpenCodeVO> getLatestFromRedis(String lotteryName);

	/**
	 * 从redis中获取最近的开奖号码
	 */
	List<LotteryOpenCodeVO> getLatestFromRedis(final List<String> lotteryNames);

	/**
	 * 从数据库中删除急速秒秒彩消息
	 */
	void delJSMMCOpenCodeFromRedis(String expect, int userId);

	boolean hasCaptured(String lotteryName, String expect);

	LotteryOpenCode getByExcept(String except);

	LotteryOpenCode getByExcept(String lottery,String except);

	List<LotteryOpenCode> getOpenCodeByDate(String lotteryName, String sTime,String eTime);

	List<LotteryOpenCode> getLatest(String lotteryName, int count, Integer userId);
}