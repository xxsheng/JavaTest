package lottery.domains.content.biz;


import lottery.domains.content.entity.LotteryOpenCode;
import lottery.domains.content.entity.UserBets;

import java.util.List;

public interface UserBetsService {
	/**
	 * 取消追号订单
	 */
	boolean cancelChase(String chaseBillno, int userId, String winExpect);

	/**
	 * 是否是待取消的追号单
	 */
	boolean isCancelChase(String chaseBillno);

	/**
	 * 对腾讯分分彩因在线人数无变动进行撤单
	 */
	void cancelByTXFFCInvalid(LotteryOpenCode lotteryOpenCode, List<UserBets> userBetsList);

	/**
	 * 对腾讯龙虎斗因在线人数无变动进行撤单
	 */
	void cancelByTXLHDInvalid(LotteryOpenCode lotteryOpenCode, List<UserBets> userBetsList);

}