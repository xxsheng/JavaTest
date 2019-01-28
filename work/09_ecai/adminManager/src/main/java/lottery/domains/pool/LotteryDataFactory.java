package lottery.domains.pool;

import java.util.List;

import lottery.domains.content.entity.GameType;
import lottery.domains.content.entity.Lottery;
import lottery.domains.content.entity.LotteryOpenTime;
import lottery.domains.content.entity.LotteryPlayRules;
import lottery.domains.content.entity.LotteryPlayRulesGroup;
import lottery.domains.content.entity.LotteryType;
import lottery.domains.content.entity.PaymentBank;
import lottery.domains.content.entity.PaymentChannel;
import lottery.domains.content.entity.SysPlatform;
import lottery.domains.content.entity.UserGameAccount;
import lottery.domains.content.entity.activity.ActivityFirstRechargeConfigVO;
import lottery.domains.content.vo.config.AdminGlobalConfig;
import lottery.domains.content.vo.config.AdminGoogleConfig;
import lottery.domains.content.vo.config.CodeConfig;
import lottery.domains.content.vo.config.DailySettleConfig;
import lottery.domains.content.vo.config.DividendConfig;
import lottery.domains.content.vo.config.DividendConfigRule;
import lottery.domains.content.vo.config.GameDividendConfig;
import lottery.domains.content.vo.config.GameDividendConfigRule;
import lottery.domains.content.vo.config.LotteryConfig;
import lottery.domains.content.vo.config.MailConfig;
import lottery.domains.content.vo.config.PlanConfig;
import lottery.domains.content.vo.config.RechargeConfig;
import lottery.domains.content.vo.config.VipConfig;
import lottery.domains.content.vo.config.WithdrawConfig;
import lottery.domains.content.vo.payment.PaymentCardVO;
import lottery.domains.content.vo.payment.PaymentChannelSimpleVO;
import lottery.domains.content.vo.payment.PaymentChannelVO;
import lottery.domains.content.vo.user.UserVO;

public interface LotteryDataFactory {

	/**
	 * 初始化所有配置
	 */
	void init();
	
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
	 * 获取契约分红配置
	 */
	DividendConfig getDividendConfig();

	/**
	 * 招商契约分红配置，按日量计算
	 */
	DividendConfigRule determineZhaoShangDividendRule(double dailyBilling);

	/**
	 * 招商契约分红配置，按日量计算
	 */
	DividendConfigRule determineCJZhaoShangDividendRule(double dailyBilling);

	/**
	 * 获取游戏分红配置
	 */
	GameDividendConfig getGameDividendConfig();

	/**
	 * 主管游戏分红配置，按亏损计算
	 */
	GameDividendConfigRule determineGameDividendRule(double loss);

	/**
	 * 获取契约日结配置
	 */
	DailySettleConfig getDailySettleConfig();

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
	 * VIP配置
	 * @return
	 */
	VipConfig getVipConfig();
	
	/**
	 * 计划配置
	 */
	PlanConfig getPlanConfig();

	/**
	 * 邮件配置
	 */
	MailConfig getMailConfig();

	/**
	 * 后台google配置
	 */
	AdminGoogleConfig getAdminGoogleConfig();

	AdminGlobalConfig getAdminGlobalConfig();
	
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
	 * 获取用户基本信息
	 */
	UserVO getUser(String username);

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
	 * 获取彩票玩法规则
	 */
	LotteryPlayRules getLotteryPlayRules(int id);

	/**
	 * 获取彩票玩法组
	 */
	LotteryPlayRulesGroup getLotteryPlayRulesGroup(int id);
	
	/**
	 * 初始化支付银行列表
	 */
	void initPaymentBank();
	
	/**
	 * 列出所有银行列表
	 * @return
	 */
	List<PaymentBank> listPaymentBank();
	

	ActivityFirstRechargeConfigVO getActivityFirstRechargeConfig();
	
	/**
	 * 根据银行名字获取银行
	 * @return
	 */
	PaymentBank getPaymentBank(int id);

	void initPaymentCard();

	List<PaymentCardVO> listPaymentCard();

	PaymentCardVO getPaymentCard(int id);

	void initPaymentChannel();

	List<PaymentChannelVO> listPaymentChannelVOs();

	List<PaymentChannelSimpleVO> listPaymentChannelVOsSimple();

	PaymentChannelVO getPaymentChannelVO(int id);

	PaymentChannelVO getPaymentChannelVO(String channelCode, Integer id);

	PaymentChannel getPaymentChannelFullProperty(int id);

	PaymentChannel getPaymentChannelFullProperty(String channelCode, Integer id);

	/**
	 * 获取游戏类型
	 */
	GameType getGameType(int id);

	/**
	 * 根据平台用户名与平台ID获取平台用户信息
	 */
	UserGameAccount getGameAccount(String platformName, int platformId);

	/**
	 * 初始化当天的红包雨随机时间
	 */
	void initActivityRedPacketRainTimes();
}