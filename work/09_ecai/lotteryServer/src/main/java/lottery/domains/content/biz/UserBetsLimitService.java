package lottery.domains.content.biz;

import java.util.List;

import lottery.domains.content.entity.UserBets;
import lottery.domains.content.entity.UserBetsLimit;

public interface UserBetsLimitService {

	List<UserBetsLimit> getGlobalSetting();
	
	List<UserBetsLimit> getByUserId(int userId);
	
	UserBetsLimit get(int userId, int lotteryId);
	
	/**
	 * 1.把用户每个采种每期，每注的最大奖金(倍数 * 单期奖金) 累加存在Redis
	 * 2.如果后台配置了 投注限制，需要校验是否超过最大奖金
	 * @param userId
	 * @param maxPrize
	 */
	double getMaxPrizeOneExcept(int userId, int lotteryId, String except, double currentMaxPrize);
	
	/**
	 * 获取当期所有用户已金投注的奖金总和
	 */
	double getMaxPrizeOneExcept(int lotteryId, String except);
	
	void setMaxPrizeOneExcept(int userId, int lotteryId, String except, double maxPrize, boolean isChase);
	
	/**
	 * 撤单时减去 奖金限额
	 */
	void deleteLimitAfterCancelOder(UserBets userBet);
	
}
