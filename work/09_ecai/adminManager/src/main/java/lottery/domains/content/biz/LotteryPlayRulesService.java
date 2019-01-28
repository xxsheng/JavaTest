package lottery.domains.content.biz;

import lottery.domains.content.vo.lottery.LotteryPlayRulesSimpleVO;
import lottery.domains.content.vo.lottery.LotteryPlayRulesVO;

import java.util.List;

public interface LotteryPlayRulesService {
	/**
	 * 根据彩票列出玩法组合数据
     * @param lotteryId 彩票ID，必选
     * @param groupId 玩法组ID，可选
	 */
	List<LotteryPlayRulesVO> list(int lotteryId, Integer groupId);
	/**
	 * 根据彩票列出玩法组合数据简单对象
     * @param typeId 彩票类型ID，必选
     * @param groupId 玩法组ID，可选
	 */
	List<LotteryPlayRulesSimpleVO> listSimple(int typeId, Integer groupId);

	/**
	 * 获取玩法组合数据
	 */
	LotteryPlayRulesVO get(int lotteryId, int ruleId);

	/**
	 * 编辑玩法，lotteryId为空时对整个彩种生效
     * @param ruleId 玩法ID，必选
     * @param lotteryId 彩票ID，可选，lotteryId为空时对整个彩种生效
     * @param minNum 最小注数注数或单行码数，跟原数据格式保持一致
     * @param maxNum 最大注数注数或单行码数，跟原数据格式保持一致
	 */
	boolean edit(int ruleId, Integer lotteryId, String minNum, String maxNum);

	/**
	 * 启用/禁用玩法，lotteryId为空时对整个彩种生效
     * @param ruleId 玩法ID，必选
     * @param lotteryId 彩票ID，可选，lotteryId为空时对整个彩种生效
     * @param enable true：启用；false：禁用
	 */
	boolean updateStatus(int ruleId, Integer lotteryId, boolean enable);
}
