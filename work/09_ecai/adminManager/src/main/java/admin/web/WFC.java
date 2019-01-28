package admin.web;

public final class WFC {

	private WFC() {}
	
	public static final String MAIN 						= "/";
	public static final String INDEX 						= "/index";
	public static final String LOGIN 						= "/login";
	public static final String LOGOUT 						= "/logout";
	public static final String ACCESS_DENIED				= "/access-denied";
	public static final String PAGE_NOT_FOUND				= "/page-not-found";
	public static final String PAGE_ERROR					= "/page-error";
	public static final String PAGE_NOT_LOGIN 				= "/page-not-login";
	
	// 控制面板
	public static final String DASHBOARD 					= "/dashboard";
	// 彩票
	public static final String LOTTERY						= "/lottery";
	// 彩票类型
	public static final String LOTTERY_TYPE					= "/lottery-type";
	// 彩票开奖时间
	public static final String LOTTERY_OPEN_TIME			= "/lottery-open-time";
	// 抓取server状态
	public static final String LOTTERY_CRAWLER_STATUS 		= "/lottery-crawler-status";
	// 彩票开奖号码
	public static final String LOTTERY_OPEN_CODE			= "/lottery-open-code";
	// 彩票开奖状态
	public static final String LOTTERY_OPEN_STATUS			= "/lottery-open-status";
	// 彩票玩法列表
	public static final String LOTTERY_PLAY_RULES			= "/lottery-play-rules";
	// 彩票玩法组列表
	public static final String LOTTERY_PLAY_RULES_GROUP		= "/lottery-play-rules-group";
	// 用户
	public static final String LOTTERY_USER					= "/lottery-user";
	// 在线用户
	public static final String LOTTERY_USER_ONLINE			= "/lottery-user-online";
	// 用户黑名单
	public static final String LOTTERY_USER_BLACK_LIST		= "/lottery-user-blacklist";
	// 用户白名单
	public static final String LOTTERY_USER_WHITE_LIST		= "/lottery-user-whitelist";
	// 用户资料
	public static final String LOTTERY_USER_PROFILE			= "/lottery-user-profile";
	// 用户银行卡
	public static final String LOTTERY_USER_CARD			= "/lottery-user-card";
	// 用户解绑银行卡记录
	public static final String LOTTERY_USER_UNBIND_CARD		= "/lottery-user-unbind-card";
	// 用户密保
	public static final String LOTTERY_USER_SECURITY		= "/lottery-user-security";
	// 用户充值
	public static final String LOTTERY_USER_RECHARGE		= "/lottery-user-recharge";
	// 历史用户充值
	public static final String HISTORY_LOTTERY_USER_RECHARGE		= "/history-lottery-user-recharge";
	// 用户取现
	public static final String LOTTERY_USER_WITHDRAW		= "/lottery-user-withdraw";
	// 用户提现可疑查询
	public static final String LOTTERY_USER_WITHDRAW_CHECK	= "/lottery-user-withdraw-check";
	// 历史用户取现
	public static final String HISTORY_LOTTERY_USER_WITHDRAW		= "/history-lottery-user-withdraw";
	// 历史用户提现可疑查询
	public static final String HISTORY_LOTTERY_USER_WITHDRAW_CHECK	= "/history-lottery-user-withdraw-check";
	
	// 用户投注
	public static final String LOTTERY_USER_BETS			= "/lottery-user-bets";
	// 历史用户投注
	public static final String HISTORY_LOTTERY_USER_BETS	= "/history-lottery-user-bets";
	// 用户原始投注
	public static final String LOTTERY_USER_BETS_ORIGINAL	= "/lottery-user-bets-original";
	// 用户合买
	public static final String LOTTERY_USER_BETS_PLAN		= "/lottery-user-bets-plan";
	// 批量操作
	public static final String LOTTERY_USER_BETS_BATCH		= "/lottery-user-bets-batch";
	// 用户账单
	public static final String LOTTERY_USER_BILL			= "/lottery-user-bill";
	// 历史用户账单
	public static final String HISTORY_LOTTERY_USER_BILL	= "/history-lottery-user-bill";
		
	// 用户消息
	public static final String LOTTERY_USER_MESSAGE			= "/lottery-user-message";
	// 大客中奖查询
	public static final String USER_HIGH_PRIZE			    = "/user-high-prize";
	// 平台存取账单
	public static final String LOTTERY_PLATFORM_BILL		= "/lottery-platform-bill";
	// 契约日结账单
	public static final String LOTTERY_USER_DAILY_SETTLE_BILL = "/lottery-user-daily-settle-bill";
	// 契约日结列表
	public static final String LOTTERY_USER_DAILY_SETTLE	= "/lottery-user-daily-settle";
	// 契约分红账单
	public static final String LOTTERY_USER_DIVIDEND_BILL	= "/lottery-user-dividend-bill";
    // 契约分红列表
	public static final String LOTTERY_USER_DIVIDEND		= "/lottery-user-dividend";
	// 老虎机真人体育分红列表
	public static final String USER_GAME_DIVIDEND_BILL		= "/user-game-dividend-bill";
	// 游戏返水
	public static final String USER_GAME_WATER_BILL			= "/user-game-water-bill";

	// 用户盈亏
	public static final String MAIN_REPORT_COMPLEX			= "/main-report-complex";
	public static final String LOTTERY_REPORT_COMPLEX		= "/lottery-report-complex";
	public static final String LOTTERY_REPORT_PROFIT		= "/lottery-report-profit";
	public static final String GAME_REPORT_COMPLEX		    = "/game-report-complex";
	public static final String RECHARGE_WITHDRAW_COMPLEX	= "/recharge-withdraw-complex"; // 充提报表
	public static final String LOTTERY_REPORT_USER_PROFIT_RANKING = "/lottery-report-user-profit-ranking"; // 用户盈利排行榜
	//历史彩票综合报表
	public static final String HISTORY_LOTTERY_REPORT_COMPLEX		= "/history-lottery-report-complex";
	//历史真人体育报表
	public static final String HISTORY_GAME_REPORT_COMPLEX		    = "/history-game-report-complex";
		
	// 银行列表
	public static final String LOTTERY_PAYMENT_BANK			= "/lottery-payment-bank";
	// 转账账号
	public static final String LOTTERY_PAYMENT_CARD			= "/lottery-payment-card";
	// 充值通道
	public static final String LOTTERY_PAYMENT_CHANNEL		= "/lottery-payment-channel";
	// 充值通道支付银行
	public static final String LOTTERY_PAYMENT_CHANNEL_BANK	= "/lottery-payment-channel-bank";
	// 手机扫码转账
	public static final String LOTTERY_PAYMENT_CHANNEL_MOBILESCAN	= "/lottery-payment-channel-mobilescan";
	// 公告&活动
	public static final String LOTTERY_SYS_NOTICE			= "/lottery-sys-notice";
	// 系统配置
	public static final String LOTTERY_SYS_CONFIG			= "/lottery-sys-config";
	// 前台日志
	public static final String LOTTERY_USER_ACTION_LOG 		= "/lottery-user-action-log";
	// 登录日志
	public static final String LOTTERY_USER_LOGIN_LOG 		= "/lottery-user-login-log";
	// 历史登录日志
	public static final String HISTORY_LOTTERY_USER_LOGIN_LOG 		= "/history-lottery-user-login-log";
		
	// 同IP登录日志
	public static final String LOTTERY_USER_LOGIN_SAMIP_LOG = "/lottery-user-login-sameip-log";
	// 用户奖金限额
	public static final String LOTTERY_USER_BETS_LIMIT 		= "/lottery-user-bets-limit";
	// 同IP登录日志
	public static final String USER_BETS_SAME_IP_LOG 		= "/user-bets-same-ip-log";
	
	// 佣金活动
	public static final String ACTIVITY_REBATE_REWARD		= "/activity-rebate-reward";
	// 日工资活动
	public static final String ACTIVITY_REBATE_SALARY		= "/activity-rebate-salary";
	// 绑定资料活动
	public static final String ACTIVITY_REBATE_BIND			= "/activity-rebate-bind";
	// 开业大酬宾活动
	public static final String ACTIVITY_REBATE_RECHARGE		= "/activity-rebate-recharge";
	// 开业大酬宾，连坏嗨不停
	public static final String ACTIVITY_REBATE_RECHARGE_LOOP	= "/activity-rebate-recharge-loop";
	// 签到活动
	public static final String ACTIVITY_REBATE_SIGN			= "/activity-rebate-sign";
	// 抢红包活动
	public static final String ACTIVITY_REBATE_GRAB			= "/activity-rebate-grab";
	// 抢红包活动
	public static final String ACTIVITY_REBATE_PACKET		= "/activity-rebate-packet";
	// 消费奖励活动
	public static final String ACTIVITY_REBATE_COST		    = "/activity-rebate-cost";
	// 红包雨活动
	public static final String ACTIVITY_RED_PACKET_RAIN		= "/activity-red-packet-rain";
	// 首充活动
	public static final String ACTIVITY_FIRST_RECHARGE		= "/activity-first-recharge";
	// 幸运大转盘
	public static final String ACTIVITY_REBATE_WHEEL		= "/activity-rebate-wheel";

	// VIP晋级管理
	public static final String VIP_UPGRADE_LIST				= "/vip-upgrade-list";
	// VIP晋级礼金
	public static final String VIP_UPGRADE_GIFTS			= "/vip-upgrade-gifts";
	// VIP生日礼金
	public static final String VIP_BIRTHDAY_GIFTS			= "/vip-birthday-gifts";
	// VIP免费筹码
	public static final String VIP_FREE_CHIPS				= "/vip-free-chips";
	// VIP积分兑换
	public static final String VIP_INTEGRAL_EXCHANGE		= "/vip-integral-exchange";
	
	// 即时统计
	public static final String LOTTERY_INSTANT_STAT			= "/lottery-instant-stat";

	// 用户余额
	public static final String USER_BALANCE_SNAPSHOT		= "/user-balance-snapshot";

	public static final String ADMIN_USER					= "/admin-user";
	public static final String ADMIN_USER_ROLE				= "/admin-user-role";
	public static final String ADMIN_USER_ACTION			= "/admin-user-action";
	public static final String ADMIN_USER_MENU				= "/admin-user-menu";
	public static final String ADMIN_USER_ACTION_LOG		= "/admin-user-action-log";
	public static final String ADMIN_USER_LOG				= "/admin-user-log";
	//后台操作关键日志
	public static final String ADMIN_USER_CRITICAL_LOG		= "/admin-user-critical-log";
		
		
	// 运维管理
	public static final String LOTTERY_SYS_CONTROL			= "/lottery-sys-control";

	// 游戏管理
	public static final String GAME_LIST			        = "/game-list";
	public static final String GAME_PLATFORM_LIST			= "/game-platform-list";
	public static final String GAME_BETS			        = "/game-bets";

	// 中奖排行榜
	public static final String USER_BETS_HIT_RANKING		= "/user-bets-hit-ranking";

}