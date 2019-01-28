package lottery.domains.pool;

import lottery.domains.content.entity.*;
import lottery.domains.content.vo.SysNoticeVO;
import lottery.domains.content.vo.activity.ActivityFirstRechargeConfigVO;
import lottery.domains.content.vo.activity.ActivityRebateWheelConfigVO;
import lottery.domains.content.vo.activity.ActivityRedPacketRainConfigVO;
import lottery.domains.content.vo.activity.ActivitySignConfigVO;
import lottery.domains.content.vo.config.*;
import lottery.domains.content.vo.lottery.LotteryOpenCodeVO;
import lottery.domains.content.vo.lottery.LotteryPlayRulesGroupVO;
import lottery.domains.content.vo.lottery.LotteryPlayRulesVO;
import lottery.domains.content.vo.user.UserVO;

import java.util.List;
import java.util.Map;

public interface DataFactory {
	
	/**
	 * 初始化所有配置
	 */
	void init();
	
	/**
	 * 获取文件
	 * @param classpath
	 * @return
	 */
	String getFileAsString(String classpath);

	/**
	 * 初始化系统消息文件
	 */
	void initSysMessage();

	/**
	 * 获取系统消息
	 * @param key
	 * @return
	 */
	String getSysMessage(String key);
	
	/**
	 * 初始化系统配置
	 */
	void initSysConfig();

	/**
	 * 初始化CDN配置
	 */
	void initCDNConfig();

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
	 * 取款配置
	 * @return
	 */
	WithdrawConfig getWithdrawConfig();

	/**
	 * 充值配置
	 * @return
	 */
	RechargeConfig getRechargeConfig();

	/**
	 * 获取契约分红配置
	 */
	DividendConfig getDividendConfig();

	/**
	 * 获取契约日结配置
	 */
	DailySettleConfig getDailySettleConfig();
	
	/**
	 * 服务配置
	 * @return
	 */
	ServiceConfig getServiceConfig();
	
	/**
	 * VIP配置
	 * @return
	 */
	VipConfig getVipConfig();
	
	/**
	 * 计划配置
	 * @return
	 */
	PlanConfig getPlanConfig();
	
	/**
	 * 域名配置
	 * @return
	 */
	DoMainConfig getDoMainConfig();

	/**
	 * CDN配置
	 */
	CDNConfig getCDNConfig();

	/**
	 * 邮件配置
	 */
	MailConfig getMailConfig();

	/**
	 * 注册配置
	 */
	RegistConfig getRegistConfig();

	/**
	 * 初始化平台列表
	 */
	void initSysPlatform();
	
	/**
	 * 列出所有平台
	 * @return
	 */
	List<SysPlatform> listSysPlatform();
	
	/**
	 * 获取平台
	 * @param id
	 */
	SysPlatform getSysPlatform(int id);
	
	/**
	 * 获取平台
	 * @param name
	 */
	SysPlatform getSysPlatform(String name);
	
	/**
	 * 初始化按量升点配置
	 */
	void initSysCodeAmount();
	
	/**
	 * 列出所有升点
	 * @return
	 */
	List<SysCodeAmount> listSysCodeAmount();
	
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

	/**
	 * 设置最新用户
	 */
	void setUser(User user);

	/**
	 * 初始化彩票类别
	 */
	void initLotteryType();
	
	/**
	 * 获取彩票类型信息
	 * @param id
	 * @return
	 */
	LotteryType getLotteryType(int id);
	
	/**
	 * 列出所有彩票类型
	 * @return
	 */
	List<LotteryType> listLotteryType();

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
	 * 根据状态列出所有彩票
	 * @return
	 */
	List<Lottery> listLotteryByStatus(int status);
	
	/**
	 * 列出所有彩票
	 * @param type
	 * @return
	 */
	List<Lottery> listLottery(int type);
	
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
	 * 获取彩票玩法组
	 */
	LotteryPlayRulesGroup getLotteryPlayRulesGroup(int lotteryId, int groupId);

	/**
	 * 列出彩票玩法组，供前台使用
	 */
	Map<String, LotteryPlayRulesGroupVO> listLotteryPlayRulesGroupVOs(int lotteryId);

	/**
	 * 列出彩票玩法，供前台使用
	 */
	Map<String, LotteryPlayRulesVO> listLotteryPlayRulesVOs(int lotteryId);

	/**
	 * 获取开奖数据
	 */
	List<LotteryOpenCodeVO> listLotteryOpenCode(int lotteryId, int count, Integer userId);

	/**
	 * 初始化中奖排行榜
	 */
	void initUserBetsHitRankings();

	/**
	 * 列出中奖排行榜
	 */
	List<UserBetsHitRanking> listUserBetsHitRanking();

	/**
	 * 列出充值通道
	 */
	List<PaymentChannel> listPaymentChannel();

	PaymentChannel getPaymentChannel(int id);

	/**
	 * 初始化支付银行列表
	 */
	void initPaymentBank();
	
	/**
	 * 列出所有银行列表
	 * @return
	 */
	List<PaymentBank> listPaymentBank();
	
	/**
	 * 根据银行名字获取银行
	 * @return
	 */
	PaymentBank getPaymentBank(int id);
	
	/**
	 * 初始化第三方支付银行列表
	 */
	void initPaymentChannelBank();
	
	/**
	 * 根据类型获取第三发支付列表
	 * @param channelCode
	 * @return
	 */
	List<PaymentChannelBank> listPaymentChannelBank(String channelCode);

	/**
	 * 根据游戏类型ID列出游戏
	 */
	List<Game> listGameByType(int typeId);

	/**
	 * 根据平台ID列出游戏
	 */
	List<Game> listGameByPlatform(int platformId);

	/**
	 * 根据平台ID列出游戏类型
	 */
	List<GameType> listGameTypeByPlatform(int platformId);

	UserGameAccount getGameAccount(int userId, int platformId, int model);

	List<SysNoticeVO> listSysNotices();

	List<SysNoticeVO> listSysNoticeSimples();

	SysNoticeVO getSysNotice(int id);

	/**
	 * 初始化红包雨时间
	 */
	void initActivityRedPacketRainTimes();

	List<ActivityRedPacketRainTime> getActivityRedPacketRainTimes();

	/**
	 * 获取本轮红包雨时间
	 */
	ActivityRedPacketRainTime getCurrentActivityRedPacketRainTime();

	/**
	 * 获取下一轮红包雨时间
	 */
	ActivityRedPacketRainTime getNextActivityRedPacketRainTime(ActivityRedPacketRainTime currentTime);

	ActivityRedPacketRainConfigVO getActivityRedPacketRainConfig();

	ActivityFirstRechargeConfigVO getActivityFirstRechargeConfig();

	ActivitySignConfigVO getActivitySignConfig();

	ActivityRebateWheelConfigVO getActivityRebateWheelConfig();
}