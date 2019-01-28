package lottery.domains.content.biz;

import lottery.domains.content.entity.User;
import lottery.domains.content.entity.UserBets;
import lottery.domains.content.vo.bets.UserBetsVO;
import lottery.web.WebJSON;
import net.sf.json.JSONArray;

public interface UserBetsService {
	
	/**
	 * 投注
	 */
	boolean general(WebJSON json, User uEntity, JSONArray bjson, String ip);

	/**
	 * 追号
	 */
	boolean chase(WebJSON json, User uEntity, int lotteryId, JSONArray bjson, JSONArray cjson, String isStop, String ip);

	/**
	 * 取消普通订单
	 */
	boolean cancelGeneral(int id, int userId, User sessionUser);
	
	/**
	 * 取消追号订单
	 */
	boolean cancelChase(String chaseBillno, int userId, User sessionUser);

	/**
	 * 重复投注订单
	 */
	boolean reBet(WebJSON json, User uEntity, int id, int userId, String ip);
}