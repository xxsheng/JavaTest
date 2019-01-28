package lottery.web;

public final class WFC {

	private WFC() {}
	
	public static final String DOMAIN 						= "/";
	public static final String MAIN 						= "/main";
	public static final String INDEX 						= "/index";
	public static final String CENTER 						= "/center";
	public static final String LOGIN 						= "/login";
	public static final String LOGOUT 						= "/logout";
	public static final String PAY 							= "/pay";
	public static final String PAGE_NOT_FOUND				= "/page-not-found";
	public static final String PAGE_ERROR					= "/page-error";
	public static final String PAGE_DENY					= "/page-deny";
	// public static final String DOMAINS 						= "/domains";
	public static final String LOTTERT_NOT_FOUND            = "/lottery-not-found";

	// 登录前
	// public static final String GAME 						= "/game";
	public static final String BRAND 						= "/brand";
	// public static final String COUPON 						= "/coupon";
	public static final String ACTIVITY 					= "/activity";
	public static final String NOTICE 						= "/notice";
	// public static final String VIP_CLUB 					= "/vipclub";
	// public static final String JOIN_US 						= "/joinus";
	// public static final String LICENSE 						= "/license";
	// public static final String QUESTION 					= "/question";
	public static final String REGISTER 					= "/register";
	
	// 登录后
	// public static final String GAME_HAPPY 					= "/game-happy";
	// public static final String GAME_CENTER 					= "/game-center";
	// public static final String GAME_LOTTERY 				= "/game-lottery";
	public static final String REAL 						= "/real";
	// public static final String GAME_ATHLETIC	 			= "/game-athletic";
	public static final String GAME				 			= "/game";
	public static final String SPORT	 			    	= "/sport";
	public static final String LOTTERY 						= "/lottery-{name}";
	public static final String LOTTERY_SSC 					= "/lottery-ssc";
	public static final String LOTTERY_JSMMC 				= "/lottery-jsmmc";
	public static final String LOTTERY_11X5 				= "/lottery-11x5";
	public static final String LOTTERY_K3 					= "/lottery-k3";
	public static final String LOTTERY_3D 					= "/lottery-3d";
	public static final String LOTTERY_KL8	 				= "/lottery-kl8";
	public static final String LOTTERY_PK10 				= "/lottery-pk10";
	public static final String LOTTERY_LHD 					= "/lottery-lhd";
	// public static final String LOTTERY_PLAN 				= "/lottery-plan";
	public static final String LOTTERY_TREND 				= "/lottery-trend";

	// 管理中心主页
	public static final String MANAGER	 					= "/manager";

	// 会员中心
	public static final String ACCOUNT_MANAGER	 			= "/account-manager";
	public static final String ACCOUNT_CARD	 				= "/account-card";
	public static final String ACCOUNT_LOGIN 				= "/account-login";

	// 财务中心
	public static final String FUND_RECHARGE	 			= "/fund-recharge";
	public static final String FUND_WITHDRAW	 			= "/fund-withdraw";
	public static final String FUND_TRANSFER	 			= "/fund-transfer";
	public static final String FUND_RECHARGE_RECORD	 		= "/fund-recharge-record";
	public static final String FUND_WITHDRAW_RECORD	 		= "/fund-withdraw-record";
	public static final String FUND_TRANSFER_RECORD	 		= "/fund-transfer-record";

	// 订单报表
	public static final String REPORT_MAIN 					= "/report-main";
	public static final String REPORT_LOTTERY	 			= "/report-lottery";
	public static final String REPORT_GAME	 				= "/report-game";
	public static final String REPORT_LOTTERY_RECORD	 	= "/report-lottery-record";
	public static final String REPORT_GAME_RECORD	 		= "/report-game-record";
	public static final String REPORT_CHASE_RECORD	 		= "/report-chase-record";
	public static final String REPORT_BILL_RECORD	 		= "/report-bill-record";

	// 代理管理
	public static final String PROXY_INDEX	 				= "/proxy-index";
	public static final String PROXY_ACCOUNT	 			= "/proxy-account";
	public static final String PROXY_TEAM	 				= "/proxy-team";
	public static final String PROXY_ONLINE	 				= "/proxy-online";
	public static final String PROXY_LOTTERY_RECORD	 		= "/proxy-lottery-record";
	public static final String PROXY_GAME_RECORD	 		= "/proxy-game-record";
	public static final String PROXY_BILL_RECORD	 		= "/proxy-bill-record";
	public static final String PROXY_SALARY	 				= "/proxy-salary";
	public static final String PROXY_DIVIDEND	 			= "/proxy-dividend";

	// 消息中心
	public static final String MESSAGE_INBOX	 			= "/message-inbox";
	public static final String MESSAGE_OUTBOX	 			= "/message-outbox";
	public static final String MESSAGE_SYS	 				= "/message-sys";
	public static final String MESSAGE_NEW	 				= "/message-new";

	// public static final String MANAGER_FUNDS	 			= "/manager-funds";
	// public static final String MANAGER_REPORT	 			= "/manager-report";
	// public static final String MANAGER_ACCOUNT	 			= "/manager-account";
	// public static final String MANAGER_PROXY	 			= "/manager-proxy";
	// public static final String MANAGER_MESSAGE	 			= "/manager-message";
	
	//线路检测
	// public static final String LINE_DETECTION               = "/line-detection";

	// 进入游戏
	public static final String GAME_LAUNCHER               = "/game-launcher";
	public static final String MOBILE_LAUNCHER             = "/mobile-launcher";
	public static final String PT_LAUNCHER                 = "/pt-launcher";
	public static final String PT_H5_LAUNCHER              = "/pt-h5-launcher";
	public static final String GAME_REDIRECT               = "/gameRedirect";

	//转账中心
	public static final String TRANSFER_ACCOUNT	 			= "/transfer-account";
}