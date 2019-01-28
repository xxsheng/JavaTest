package lottery.domains.pool;

import lottery.domains.content.entity.Lottery;
import lottery.domains.content.entity.LotteryOpenTime;
import lottery.domains.content.entity.LotteryPlayRules;
import lottery.domains.content.entity.LotteryPlayRulesGroup;
import lottery.domains.content.vo.config.*;
import lottery.domains.content.vo.user.UserVO;

import java.util.List;

public interface DataFactory {
	
	/**
	 * 初始化所有配置
	 */
	void init();

	/**
	 * 初始化系统配置
	 */
	void initSysConfig();

	/**
	 * 获取系统配置
	 */
	CodeConfig getCodeConfig();
	
	/**
	 * 彩票配置
	 * @return
	 */
	LotteryConfig getLotteryConfig();

	/**
	 * 计划配置
	 * @return
	 */
	PlanConfig getPlanConfig();

	/**
	 * 邮件配置
	 */
	MailConfig getMailConfig();

	/**
	 * 自主彩配置
	 */
	SelfLotteryConfig getSelfLotteryConfig();

	/**
	 * 初始化彩票信息
	 */
	void initLottery();
	
	/**
	 * 获取彩票信息
	 * @param id
	 * @return
	 */
	Lottery getLottery(int id);
	
	/**
	 * 获取彩票信息
	 * @param shortName
	 * @return
	 */
	Lottery getLottery(String shortName);

	/**
	 * 列出所有彩票
	 * @return
	 */
	List<Lottery> listLottery();

	/**
	 * 列出所有彩票
	 * @param type
	 * @return
	 */
	List<Lottery> listLottery(int type);

	/**
	 * 列出所有自主彩
	 * @return
	 */
	List<Lottery> listSelfLottery();

	/**
	 * 初始化彩票开奖时间信息
	 */
	void initLotteryOpenTime();

	/**
	 * 列出彩票开奖时间信息
	 * @param lottery
	 * @return
	 */
	List<LotteryOpenTime> listLotteryOpenTime(String lottery);

	/**
	 * 初始化彩票玩法规则
	 */
	void initLotteryPlayRules();

	/**
	 * 获取彩票玩法
	 */
	LotteryPlayRules getLotteryPlayRules(int lotteryId, int ruleId);

	/**
	 * 获取彩票玩法
	 */
	LotteryPlayRules getLotteryPlayRules(int lotteryId, String code);

	/**
	 * 获取彩票玩法组
	 */
	LotteryPlayRulesGroup getLotteryPlayRulesGroup(int lotteryId, int groupId);

	void initLotteryOpenCode();

	/**
	 * 初始化用户基础信息
	 */
	void initUser();

	/**
	 * 获取用户基本信息
	 * @param id
	 * @return
	 */
	UserVO getUser(int id);
}