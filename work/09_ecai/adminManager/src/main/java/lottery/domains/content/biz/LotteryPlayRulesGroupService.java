package lottery.domains.content.biz;

import lottery.domains.content.vo.lottery.LotteryPlayRulesGroupSimpleVO;
import lottery.domains.content.vo.lottery.LotteryPlayRulesGroupVO;

import java.util.List;

public interface LotteryPlayRulesGroupService {
	/**
	 * 根据彩票列出玩法组组合数据
	 */
	List<LotteryPlayRulesGroupSimpleVO> listSimpleByType(int typeId);

	/**
	 * 根据彩票列出玩法组组合数据
	 */
	List<LotteryPlayRulesGroupVO> list(int lotteryId);

	/**
	 * 启用/禁用玩法组，lotteryId为空时对整个彩种生效
	 */
	boolean updateStatus(int groupId, Integer lotteryId, boolean enable);
}