package admin.web;

public final class WUC {

	private WUC() {}

	// 一次性token
	public static final String DISPOSABLE_TOKEN                         = "/DisposableToken";

	// public static final String INIT 									= "/init";
	// public static final String INIT_ADMIN 								= "/init-admin-todo";
	public static final String LOGIN 									= "/login";
	public static final String GLOBAL									= "/global";
	public static final String ADMIN_GLOBAL_CONFIG					    = "/admin-global-config";
	public static final String HIGH_PRIZE_UNPROCESS_COUNT				= "/high-prize-unprocess-count";

//	 public static final String LOTTERY_USER_DAILY_SETTLE_MANUAL         = "/LotteryUserDailySettleManual";
//	 public static final String LOTTERY_USER_DIVIDEND_MANUAL             = "/LotteryUserDividendManual";

	// 获取控制面板的数据
	public static final String DASHBOARD_TOTAL_INFO						= "/dashboard/total-info";
	public static final String DASHBOARD_CHART_USER_REGIST				= "/dashboard/chart-user-regist";
	public static final String DASHBOARD_CHART_USER_LOGIN				= "/dashboard/chart-user-login";
	public static final String DASHBOARD_CHART_USER_BETS				= "/dashboard/chart-user-bets";
	public static final String DASHBOARD_CHART_USER_CASH				= "/dashboard/chart-user-cash";
	public static final String DASHBOARD_CHART_USER_COMPLEX				= "/dashboard/chart-user-complex";
	public static final String DASHBOARD_CHART_LOTTERY_HOT				= "/dashboard/chart-lottery-hot";

	// 彩票列表
	public static final String LOTTERY_LIST								= "/lottery/list";
	// 更新彩票状态
	public static final String LOTTERY_UPDATE_STATUS					= "/lottery/update-status";
	// 彩票类型列表
	public static final String LOTTERY_TYPE_LIST						= "/lottery-type/list";
	// 更新彩票类型
	public static final String LOTTERY_TYPE_UPDATE_STATUS				= "/lottery-type/update-status";
	// 彩票抓取状态
	public static final String LOTTERY_CRAWLER_STATUS_LIST				= "/lottery-crawler-status/list";
	public static final String LOTTERY_CRAWLER_STATUS_GET				= "/lottery-crawler-status/get";
	public static final String LOTTERY_CRAWLER_STATUS_EDIT				= "/lottery-crawler-status/edit";
	// 彩票开奖时间列表
	public static final String LOTTERY_OPEN_TIME_LIST					= "/lottery-open-time/list";
	public static final String LOTTERY_OPEN_TIME_MODIFY					= "/lottery-open-time/modify";
	public static final String LOTTERY_OPEN_TIME_BATCH_MODIFY			= "/lottery-open-time/batch-modify";
	public static final String LOTTERY_OPEN_TIME_MODIFY_REF_EXPECT		= "/lottery-open-time/modify-ref-expect";
	// 彩票开奖号码列表
	public static final String LOTTERY_OPEN_CODE_LIST					= "/lottery-open-code/list";
	public static final String LOTTERY_OPEN_CODE_GET					= "/lottery-open-code/get";
	public static final String LOTTERY_OPEN_CODE_ADD					= "/lottery-open-code/add";
	public static final String LOTTERY_OPEN_CODE_DELETE					= "/lottery-open-code/delete";
	public static final String LOTTERY_OPEN_CODE_CORRECT				= "/lottery-open-code/correct";
	public static final String LOTTERY_OPEN_CODE_SELFLOTTERY_OPEN		= "/lottery-open-code/sleflottery/open";
	// 彩票开奖状态列表
	public static final String LOTTERY_OPEN_STATUS_LIST					= "/lottery-open-status/list";
	// 手动开奖
	public static final String LOTTERY_OPEN_MANUAL_CONTROL				= "/lottery-open-status/manual-control";
	// 彩票玩法列表
	public static final String LOTTERY_PLAY_RULES_GROUP_LIST			= "/lottery-play-rules-group/list";
	public static final String LOTTERY_PLAY_RULES_GROUP_SIMPLE_LIST		= "/lottery-play-rules-group/simple-list";
	public static final String LOTTERY_PLAY_RULES_GROUP_UPDATE_STATUS	= "/lottery-play-rules-group/update-status";
	public static final String LOTTERY_PLAY_RULES_LIST					= "/lottery-play-rules/list";
	public static final String LOTTERY_PLAY_RULES_SIMPLE_LIST			= "/lottery-play-rules/simple-list";
	public static final String LOTTERY_PLAY_RULES_GET					= "/lottery-play-rules/get";
	public static final String LOTTERY_PLAY_RULES_EDIT					= "/lottery-play-rules/edit";
	public static final String LOTTERY_PLAY_RULES_UPDATE_STATUS			= "/lottery-play-rules/update-status";

	// 用户列表
	public static final String LOTTERY_USER_LIST						= "/lottery-user/list";
	public static final String LOTTERY_USER_LIST_ONLINE					= "/lottery-user/list-online";
	public static final String LOTTERY_USER_LOWER_ONLINE				= "/lottery-user/lower-online";
	public static final String LOTTERY_USER_ADD							= "/lottery-user/add";
	public static final String LOTTERY_USER_CHECK_EXIST					= "/lottery-user/check-exist";
	public static final String LOTTERY_USER_MODIFY_LOGIN_PWD			= "/lottery-user/modify-login-pwd";
	public static final String LOTTERY_USER_MODIFY_WITHDRAW_PWD			= "/lottery-user/modify-withdraw-pwd";
	public static final String LOTTERY_USER_MODIFY_WITHDRAW_NAME 		= "/lottery-user/modify-withdraw-name";
	public static final String LOTTERY_USER_RESET_EMAIL					= "/lottery-user/reset-email";
	public static final String LOTTERY_USER_MODIFY_EMAIL				= "/lottery-user/modify-email";
	public static final String LOTTERY_USER_RESET_IMAGE_PWD				= "/lottery-user/reset-image-pwd";
	public static final String LOTTERY_USER_MODIFY_POINT				= "/lottery-user/modify-point";
	public static final String LOTTERY_USER_DOWN_POINT					= "/lottery-user/down-point";
	public static final String LOTTERY_USER_MODIFY_EXTRA_POINT			= "/lottery-user/modify-extra-point";
	public static final String LOTTERY_USER_MODIFY_QUOTA				= "/lottery-user/modify-quota";
	public static final String LOTTERY_USER_GET_POINT_INFO				= "/lottery-user/get-point-info";
	public static final String LOTTERY_USER_CHANGE_LINE					= "/lottery-user/change-line";
	public static final String LOTTERY_USER_DELETE 						= "/lottery-user/delete";
	public static final String LOTTERY_USER_MODIFY_EQUAL_CODE 			= "/lottery-user/modify-equal-code";
	public static final String LOTTERY_USER_MODIFY_TRANSFERS 			= "/lottery-user/modify-transfers";
	public static final String LOTTERY_USER_MODIFY_WITHDRAW			= "/lottery-user/modify-withdraw";
	public static final String LOTTERY_USER_CHANGE_PROXY				= "/lottery-user/change-proxy";
	public static final String LOTTERY_USER_UNBIND_GOOGLE				= "/lottery-user/unbind-google";
	public static final String LOTTERY_USER_UNBIND_RESET_LOCK_TIME		= "/lottery-user/reset-lock-time";
	public static final String LOTTERY_USER_MODIFY_RELATED_UPPER 		= "/lottery-user/modify-related-upper"; // 修改关联上级
	public static final String LOTTERY_USER_RELIVE_RELATED_UPPER 		= "/lottery-user/relive-related-upper"; // 解除关联上级
	public static final String LOTTERY_USER_MODIFY_RELATED_USERS		= "/lottery-user/modify-related-users"; // 修改关联会员
	public static final String LOTTERY_USER_LOCK_TEAM		            = "/lottery-user/lock-team";
	public static final String LOTTERY_USER_UN_LOCK_TEAM		        = "/lottery-user/un-lock-team";
	public static final String LOTTERY_USER_TRANSFER          	        = "/lottery-user/user-transfer";
	public static final String USER_BETS_SAME_IP_LOG_LIST				= "/user-bets-same-ip-log/list"; // 同IP投注日志
	public static final String LOTTERY_USER_PROHIBIT_TEAM_WITHDRAW      = "/lottery-user/prohibit-team-withdraw";//禁止团队取款
	public static final String LOTTERY_USER_ALLOW_TEAM_WITHDRAW         = "/lottery-user/allow-team-withdraw";//允许团队取款
	public static final String LOTTERY_USER_ALLOW_TEAM_TRANSFERS        = "/lottery-user/allow-team-transfers";//允许团队上下级转账
	public static final String LOTTERY_USER_PROHIBIT_TEAM_TRANSFERS     = "/lottery-user/prohibit-team-transfers";//关闭团队上下级团队转账
	public static final String LOTTERY_USER_MODIFY_PLATFORM_TRANSFERS    = "/lottery-user/modify-platform-transfers";//修改转账权限
	public static final String LOTTERY_USER_ALLOW_TEAM_PLATFORM_TRANSFERS         = "/lottery-user/allow-team-platform-transfers";//允许团队转账
	public static final String LOTTERY_USER_PROHIBIT_TEAM_PLATFORM_TRANSFERS        = "/lottery-user/prohibit-team-platform-transfers";//关闭团队转账
	public static final String LOTTERY_USER_WITHDRAW_LIMIT_LIST        = "/lottery-user/withdraw-limit-list";//关闭团队转账

	//提款消费量清空
	public static final String LOTTERY_USER_RESERT_XIAOFEI				= "/lottery-user/reset-user-xiaofei";

	// 用户详细信息
	public static final String LOTTERY_USER_PROFILE_GET					= "/lottery-user-profile/get";
	public static final String LOTTERY_USER_LOCK						= "/lottery-user/lock";
	public static final String LOTTERY_USER_UNLOCK						= "/lottery-user/unlock";
	public static final String LOTTERY_USER_BETS_STATUS					= "/lottery-user/bets-status";
	public static final String LOTTERY_USER_RECOVER						= "/lottery-user/recover";
	public static final String LOTTERY_USER_CHANGE_ZHAO_SHANG			= "/lottery-user/change-zhaoshang";

	// 用户银行卡
	public static final String LOTTERY_USER_CARD_LIST					= "/lottery-user-card/list";
	public static final String LOTTERY_USER_CARD_GET					= "/lottery-user-card/get";
	public static final String LOTTERY_USER_CARD_EDIT					= "/lottery-user-card/edit";
	public static final String LOTTERY_USER_CARD_ADD					= "/lottery-user-card/add";
	public static final String LOTTERY_USER_CARD_LOCK_STATUS			= "/lottery-user-card/lock-status";
	public static final String LOTTERY_USER_UNBIND_LIST			        = "/lottery-user-card/unbid-list";
	public static final String LOTTERY_USER_UNBIND_DEL			        = "/lottery-user-card/unbid-del";

	// 黑名单
	public static final String LOTTERY_USER_BLACKLIST_LIST				= "/lottery-user-blacklist/list";
	public static final String LOTTERY_USER_BLACKLIST_ADD				= "/lottery-user-blacklist/add";
	public static final String LOTTERY_USER_BLACKLIST_DELETE			= "/lottery-user-blacklist/delete";

	// 白名单
	public static final String LOTTERY_USER_WHITELIST_LIST				= "/lottery-user-whitelist/list";
	public static final String LOTTERY_USER_WHITELIST_ADD				= "/lottery-user-whitelist/add";
	public static final String LOTTERY_USER_WHITELIST_DELETE			= "/lottery-user-whitelist/delete";

	// 用户密保列表
	public static final String LOTTERY_USER_SECURITY_LIST				= "/lottery-user-security/list";
	public static final String LOTTERY_USER_SECURITY_RESET				= "/lottery-user-security/reset";

	// 用户登录日志
	public static final String LOTTERY_USER_LOGIN_LOG_LIST				= "/lottery-user-login-log/list";
	public static final String LOTTERY_USER_LOGIN_SAMEIP_LOG_LIST		= "/lottery-user-login-sameip-log/list";
	//历史登录日志
	public static final String HISTORY_LOTTERY_USER_LOGIN_LOG_LIST		= "/history-lottery-user-login-log/list";
	// 用户姓名修改日志
	public static final String LOTTERY_USER_UPDATE_LOG					= "/lottery-user-update-log/list";
	// 用户充值列表
	public static final String LOTTERY_USER_RECHARGE_LIST				= "/lottery-user-recharge/list";
	// 历史用户充值列表
	public static final String HISTORY_LOTTERY_USER_RECHARGE_LIST		= "/history-lottery-user-recharge/list";

	public static final String LOTTERY_USER_RECHARGE_ADD				= "/lottery-user-recharge/add";
	public static final String LOTTERY_USER_RECHARGE_GET				= "/lottery-user-recharge/get";
	//历史充值详情
	public static final String HISTORY_LOTTERY_USER_RECHARGE_GET				= "/history-lottery-user-recharge/get";

	public static final String LOTTERY_USER_RECHARGE_TODO				= "/lottery-user-recharge/todo";
	public static final String LOTTERY_USER_RECHARGE_PATCH				= "/lottery-user-recharge/patch";
	public static final String LOTTERY_USER_RECHARGE_CANCEL				= "/lottery-user-recharge/cancel";
	// 用户取现列表
	public static final String LOTTERY_USER_WITHDRAW_LIST				= "/lottery-user-withdraw/list";

	// 历史用户取现列表
	public static final String HISTORY_LOTTERY_USER_WITHDRAW_LIST				= "/history-lottery-user-withdraw/list";

	public static final String LOTTERY_USER_WITHDRAW_GET				= "/lottery-user-withdraw/get";
	// 历史用户取现
	public static final String HISTORY_LOTTERY_USER_WITHDRAW_GET				= "/history-lottery-user-withdraw/get";

	public static final String LOTTERY_USER_WITHDRAW_PAY_GET			= "/lottery-user-withdraw/pay-get";
	//历史用户取现
	public static final String HISTORY_LOTTERY_USER_WITHDRAW_PAY_GET			= "/history-lottery-user-withdraw/pay-get";

	public static final String LOTTERY_USER_WITHDRAW_CHECK				= "/lottery-user-withdraw/check";
	//历史用户取现
	public static final String HISTORY_LOTTERY_USER_WITHDRAW_CHECK				= "/history-lottery-user-withdraw/check";

	public static final String LOTTERY_USER_WITHDRAW_CHECK_RESULT		= "/lottery-user-withdraw/check-result";
	public static final String LOTTERY_USER_WITHDRAW_CONFIRM			= "/lottery-user-withdraw/confirm";
	public static final String LOTTERY_USER_WITHDRAW_LOCK				= "/lottery-user-withdraw/lock";
	public static final String LOTTERY_USER_WITHDRAW_UNLOCK				= "/lottery-user-withdraw/unlock";





	public static final String LOTTERY_USER_WITHDRAW_MANUAL_PAY			= "/lottery-user-withdraw/manual-pay"; // 手动出款
	public static final String LOTTERY_USER_WITHDRAW_REFUSE				= "/lottery-user-withdraw/refuse"; // 拒绝支付
	public static final String LOTTERY_USER_WITHDRAW_WITHDRAW_FAILURE	= "/lottery-user-withdraw/withdraw-failure"; // 提现失败
	public static final String LOTTERY_USER_WITHDRAW_COMPLETE_REMIT		= "/lottery-user-withdraw/complete-remit"; // 确认到账
	public static final String LOTTERY_USER_WITHDRAW_API_PAY        	= "/lottery-user-withdraw/api-pay"; // API代付
	//取款操作日志
	public static final String LOTTERY_USER_WITHDRAW_LOG				= "/lottery-user-withdraw-log/list";

	// 用户投注列表
	public static final String LOTTERY_USER_BETS_LIST					= "/lottery-user-bets/list";
	public static final String LOTTERY_USER_BETS_CANCEL					= "/lottery-user-bets/cancel";
	public static final String LOTTERY_USER_BETS_GET					= "/lottery-user-bets/get";
	public static final String LOTTERY_USER_BETS_CHANGE					= "/lottery-user-bets/change";
	//历史用户投注列表
	public static final String HISTORY_LOTTERY_USER_BETS_LIST			= "/history-lottery-user-bets/list";
	//历史用户投注详情
	public static final String HISTORY_LOTTERY_USER_BETS_GET			= "/history-lottery-user-bets/get";

	// 用户原始投注列表
	public static final String LOTTERY_USER_BETS_ORIGINAL_LIST			= "/lottery-user-bets/original-list";
	public static final String LOTTERY_USER_BETS_ORIGINAL_GET			= "/lottery-user-bets/original-get";

	// 用户大额中奖
	public static final String USER_HIGH_PRIZE_LIST						= "/user-high-prize/list";
	public static final String USER_HIGH_PRIZE_LOCK						= "/user-high-prize/lock";
	public static final String USER_HIGH_PRIZE_UNLOCK					= "/user-high-prize/unlock";
	public static final String USER_HIGH_PRIZE_CONFIRM					= "/user-high-prize/confirm";

	// 用户投注限额设置
	public static final String LOTTERY_USER_BETS_LIMIT_LIST				= "/lottery-user-bets-limit/list";
	public static final String LOTTERY_USER_BETS_LIMIT_DELETE			= "/lottery-user-bets-limit/delete";
	public static final String LOTTERY_USER_BETS_LIMIT_GET				= "/lottery-user-bets-limit/get";
	public static final String LOTTERY_USER_BETS_LIMIT_ADD_UPDATE		= "/lottery-user-bets-limit/add-update";


	// 用户合买计划列表
	public static final String LOTTERY_USER_BETS_PLAN_LIST				= "/lottery-user-bets-plan/list";

	// 用户投注操作
	public static final String LOTTERY_USER_BETS_BATCH					= "/lottery-user-bets/batch";
	// 用户账单列表
	public static final String LOTTERY_USER_BILL_LIST					= "/lottery-user-bill/list";
	public static final String LOTTERY_USER_BILL_DETAILS				= "/lottery-user-bill/details";
	// 历史用户账单列表
	public static final String HISTORY_LOTTERY_USER_BILL_LIST			= "/history-lottery-user-bill/list";
	// 历史用户账单详情
	public static final String HISTORY_LOTTERY_USER_BILL_DETAILS		= "/history-lottery-user-bill/details";

	// 用户消息列表
	public static final String LOTTERY_USER_MESSAGE_LIST				= "/lottery-user-message/list";
	public static final String LOTTERY_USER_MESSAGE_GET					= "/lottery-user-message/get";
	public static final String LOTTERY_USER_MESSAGE_DELETE				= "/lottery-user-message/delete";
	public static final String LOTTERY_USER_MESSAGE_REPLY				= "/lottery-user-message/reply";
	// 用户盈亏报表
	public static final String MAIN_REPORT_COMPLEX						= "/main-report/complex";
	public static final String LOTTERY_REPORT_COMPLEX					= "/lottery-report/complex";
	public static final String LOTTERY_REPORT_COMPLEX_DETAILS			= "/lottery-report/complex-details";
	public static final String LOTTERY_REPORT_PROFIT					= "/lottery-report/profit";
	public static final String GAME_REPORT_COMPLEX					    = "/game-report/complex";
	public static final String RECHARGE_WITHDRAW_COMPLEX                = "/recharge-withdraw-complex"; // 充提报表
	public static final String LOTTERY_REPORT_USER_PROFIT_RANKING       = "/lottery-report/user-profit-ranking"; // 用户盈利排行榜
	// 历史彩票综合报表
	public static final String HISTORY_LOTTERY_REPORT_COMPLEX			= "/history-lottery-report/complex";
	public static final String HISTORY_LOTTERY_REPORT_COMPLEX_DETAILS	= "/history-lottery-report/complex-details";
	//历史真人体育报表
	public static final String HISTORY_GAME_REPORT_COMPLEX				= "/history-game-report/complex";

	// 充值通道银行列表
	public static final String LOTTERY_PAYMENT_BANK_LIST 				= "/lottery-payment-bank/list";
	public static final String LOTTERY_PAYMENT_BANK_GET 				= "/lottery-payment-bank/get";
	public static final String LOTTERY_PAYMENT_BANK_EDIT 				= "/lottery-payment-bank/edit";
	// 转账账号列表
	public static final String LOTTERY_PAYMENT_CARD_LIST 				= "/lottery-payment-card/list";
	public static final String LOTTERY_PAYMENT_CARD_GET 				= "/lottery-payment-card/get";
	public static final String LOTTERY_PAYMENT_CARD_ADD 				= "/lottery-payment-card/add";
	public static final String LOTTERY_PAYMENT_CARD_EDIT 				= "/lottery-payment-card/edit";
	public static final String LOTTERY_PAYMENT_CARD_UPDATE_STATUS 		= "/lottery-payment-card/update-status";
	public static final String LOTTERY_PAYMENT_CARD_RESET_CREDITS 		= "/lottery-payment-card/reset-credits";
	public static final String LOTTERY_PAYMENT_CARD_DELETE		 		= "/lottery-payment-card/delete";

	// 手机扫码转账
	public static final String LOTTERY_PAYMENT_CHANNEL_MOBILESCAN_LIST 				= "/lottery-payment-channel-mobilescan/list";
	public static final String LOTTERY_PAYMENT_CHANNEL_MOBILESCAN_GET 				= "/lottery-payment-channel-mobilescan/get";
	public static final String LOTTERY_PAYMENT_CHANNEL_MOBILESCAN_ADD 				= "/lottery-payment-channel-mobilescan/add";
	public static final String LOTTERY_PAYMENT_CHANNEL_MOBILESCAN_EDIT 				= "/lottery-payment-channel-mobilescan/edit";
	public static final String LOTTERY_PAYMENT_CHANNEL_MOBILESCAN_UPDATE_STATUS 	= "/lottery-payment-channel-mobilescan/update-status";
	public static final String LOTTERY_PAYMENT_CHANNEL_MOBILESCAN_DELETE		 	= "/lottery-payment-channel-mobilescan/delete";

	public static final String LOTTERY_PAYMENT_CHANNEL_MOBILESCAN_QR_CODE_DELETE	= "/lottery-payment-channel-mobilescan-qr-code/delete";
	// 充值通道
	public static final String LOTTERY_PAYMENT_CHANNEL_SIMPLE_LIST 					= "/lottery-payment-channel/simple-list";
	public static final String LOTTERY_PAYMENT_CHANNEL_LIST 						= "/lottery-payment-channel/list";
	public static final String LOTTERY_PAYMENT_CHANNEL_GET 						    = "/lottery-payment-channel/get";
	public static final String LOTTERY_PAYMENT_CHANNEL_ADD 							= "/lottery-payment-channel/add";
	public static final String LOTTERY_PAYMENT_CHANNEL_EDIT 						= "/lottery-payment-channel/edit";
	public static final String LOTTERY_PAYMENT_CHANNEL_UPDATE_STATUS 				= "/lottery-payment-channel/update-status";
	public static final String LOTTERY_PAYMENT_CHANNEL_RESET_CREDITS 				= "/lottery-payment-channel/reset-credits";
	public static final String LOTTERY_PAYMENT_CHANNEL_DELETE		 				= "/lottery-payment-channel/delete";
	public static final String LOTTERY_PAYMENT_CHANNEL_MOVEUP		 				= "/lottery-payment-channel/move-up";
	public static final String LOTTERY_PAYMENT_CHANNEL_MOVEDOWN		 				= "/lottery-payment-channel/move-down";
	// 充值通道银行代码
	public static final String LOTTERY_PAYMENT_CHANNEL_BANK_LIST 			= "/lottery-payment-channel-bank/list";
	public static final String LOTTERY_PAYMENT_CHANNEL_BANK_UPDATE_STATUS = "/lottery-payment-channel-bank/update-status";

	// 前台操作日志
	public static final String LOTTERY_USER_ACTION_LOG_LIST 			= "/lottery-user-action-log/list";

	// 活动公告
	public static final String LOTTERY_SYS_NOTICE 						= "/lottery-sys-notice/list";
	public static final String LOTTERY_SYS_NOTICE_GET 					= "/lottery-sys-notice/get";
	public static final String LOTTERY_SYS_NOTICE_ADD 					= "/lottery-sys-notice/add";
	public static final String LOTTERY_SYS_NOTICE_EDIT 					= "/lottery-sys-notice/edit";
	public static final String LOTTERY_SYS_NOTICE_DELETE 				= "/lottery-sys-notice/delete";
	public static final String LOTTERY_SYS_NOTICE_UPDATE_STATUS 		= "/lottery-sys-notice/update-status";
	public static final String LOTTERY_SYS_NOTICE_UPDATE_SORT	 		= "/lottery-sys-notice/update-sort";

	// 系统配置
	public static final String LOTTERY_SYS_CONFIG_LIST 					= "/lottery-sys-config/list";
	public static final String LOTTERY_SYS_CONFIG_GET 					= "/lottery-sys-config/get";
	public static final String LOTTERY_SYS_CONFIG_UPDATE 				= "/lottery-sys-config/update";

	// 平台存取账单下载
	public static final String LOTTERY_PLATFORM_BILL_DOWNLOAD			= "/lottery-platform-bill/download";

	// 佣金活动
	public static final String ACTIVITY_REBATE_REWARD_LIST				= "/activity-rebate-reward/list";
	public static final String ACTIVITY_REBATE_REWARD_GET				= "/activity-rebate-reward/get";
	public static final String ACTIVITY_REBATE_REWARD_EDIT				= "/activity-rebate-reward/edit";
	public static final String ACTIVITY_REBATE_REWARD_UPDATE_STATUS		= "/activity-rebate-reward/update-status";
	public static final String ACTIVITY_REBATE_REWARD_BILL_CALCULATE	= "/activity-rebate-reward-bill/calculate";
	public static final String ACTIVITY_REBATE_REWARD_BILL_CONFIRM		= "/activity-rebate-reward-bill/confirm";
	public static final String ACTIVITY_REBATE_REWARD_BILL_LIST			= "/activity-rebate-reward-bill/list";

	// 日工资活动
	public static final String ACTIVITY_REBATE_SALARY_LIST 				= "/activity-rebate-salary/list";
	public static final String ACTIVITY_REBATE_SALARY_GET 				= "/activity-rebate-salary/get";
	public static final String ACTIVITY_REBATE_SALARY_EDIT 				= "/activity-rebate-salary/edit";
	public static final String ACTIVITY_REBATE_SALARY_UPDATE_STATUS 	= "/activity-rebate-salary/update-status";
	public static final String ACTIVITY_REBATE_SALARY_BILL_LIST		 	= "/activity-rebate-salary-bill/list";

	// 绑定资料活动
	public static final String ACTIVITY_REBATE_BIND_LIST				= "/activity-rebate-bind/list";
	public static final String ACTIVITY_REBATE_BIND_GET					= "/activity-rebate-bind/get";
	public static final String ACTIVITY_REBATE_BIND_EDIT				= "/activity-rebate-bind/edit";
	public static final String ACTIVITY_REBATE_BIND_UPDATE_STATUS		= "/activity-rebate-bind/update-status";
	public static final String ACTIVITY_REBATE_BIND_BILL_CONFIRM		= "/activity-rebate-bind-bill/confirm";
	public static final String ACTIVITY_REBATE_BIND_BILL_REFUSE			= "/activity-rebate-bind-bill/refuse";
	public static final String ACTIVITY_REBATE_BIND_BILL_LIST			= "/activity-rebate-bind-bill/list";

	// 开业大酬宾活动
	public static final String ACTIVITY_REBATE_RECHARGE_LIST			= "/activity-rebate-recharge/list";
	public static final String ACTIVITY_REBATE_RECHARGE_GET				= "/activity-rebate-recharge/get";
	public static final String ACTIVITY_REBATE_RECHARGE_EDIT			= "/activity-rebate-recharge/edit";
	public static final String ACTIVITY_REBATE_RECHARGE_UPDATE_STATUS	= "/activity-rebate-recharge/update-status";
	public static final String ACTIVITY_REBATE_RECHARGE_BILL_CONFIRM	= "/activity-rebate-recharge-bill/confirm";
	public static final String ACTIVITY_REBATE_RECHARGE_BILL_LIST		= "/activity-rebate-recharge-bill/list";

	// 开业大酬宾，连环嗨不停
	public static final String ACTIVITY_REBATE_RECHARGE_LOOP_LIST 				= "/activity-rebate-recharge-loop/list";
	public static final String ACTIVITY_REBATE_RECHARGE_LOOP_GET 				= "/activity-rebate-recharge-loop/get";
	public static final String ACTIVITY_REBATE_RECHARGE_LOOP_EDIT 				= "/activity-rebate-recharge-loop/edit";
	public static final String ACTIVITY_REBATE_RECHARGE_LOOP_UPDATE_STATUS 		= "/activity-rebate-recharge-loop/update-status";
	public static final String ACTIVITY_REBATE_RECHARGE_LOOP_BILL_LIST		 	= "/activity-rebate-recharge-loop-bill/list";

	// 签到礼金大放送
	public static final String ACTIVITY_REBATE_SIGN_LIST 				= "/activity-rebate-sign/list";
	public static final String ACTIVITY_REBATE_SIGN_GET 				= "/activity-rebate-sign/get";
	public static final String ACTIVITY_REBATE_SIGN_EDIT 				= "/activity-rebate-sign/edit";
	public static final String ACTIVITY_REBATE_SIGN_UPDATE_STATUS 		= "/activity-rebate-sign/update-status";
	public static final String ACTIVITY_REBATE_SIGN_BILL		 		= "/activity-rebate-sign/bill";
	public static final String ACTIVITY_REBATE_SIGN_RECORD		 		= "/activity-rebate-sign/record";

	// 拆红包活动
	public static final String ACTIVITY_REBATE_GRAB_PACKAGE_LIST		= "/activity-rebate-grab/list";
	public static final String ACTIVITY_REBATE_GRAB_PACKAGE_GET		    = "/activity-rebate-grab/get";
	public static final String ACTIVITY_REBATE_GRAB_PACKAGE_EDIT		= "/activity-rebate-grab/edit";
	public static final String ACTIVITY_REBATE_GRAB_PACKAGE_BILL	    = "/activity-rebate-grab/bill";
	public static final String ACTIVITY_REBATE_GRAB_PACKAGE_STATUS	    = "/activity-rebate-grab/update-status";
	public static final String ACTIVITY_REBATE_GRAB_PACKAGE_OUTTOTAL	= "/activity-rebate-grab/out-total";

	// 抢红包活动
	public static final String ACTIVITY_REBATE_PACKET_LIST 				= "/activity-rebate-packet/list";
	public static final String ACTIVITY_REBATE_PACKET_GET 				= "/activity-rebate-packet/get";
	public static final String ACTIVITY_REBATE_PACKET_EDIT 				= "/activity-rebate-packet/edit";
	public static final String ACTIVITY_REBATE_PACKET_UPDATE_STATUS     = "/activity-rebate-packet/update-status";
	public static final String ACTIVITY_REBATE_PACKET_BILL		 		= "/activity-rebate-packet/bill";
	public static final String ACTIVITY_REBATE_PACKET_SEND		 		= "/activity-rebate-packet/send";
	public static final String ACTIVITY_REBATE_PACKET_INFO		 		= "/activity-rebate-packet/info";

	//消费奖励活动															 
	public static final String ACTIVITY_REBATE_COST_LIST                = "/activity-rebate-cost/list";
	public static final String ACTIVITY_REBATE_COST_GET                 = "/activity-rebate-cost/get";
	public static final String ACTIVITY_REBATE_COST_EDIT                = "/activity-rebate-cost/edit";
	public static final String ACTIVITY_REBATE_COST_UPDATE_STATUS       = "/activity-rebate-cost/update-status";
	public static final String ACTIVITY_REBATE_COST_BILL                = "/activity-rebate-cost/bill";

	// 红包雨活动
	public static final String ACTIVITY_RED_PACKET_RAIN_LIST            = "/activity-red-packet-rain/list";
	public static final String ACTIVITY_RED_PACKET_RAIN_EDIT            = "/activity-red-packet-rain/edit";
	public static final String ACTIVITY_RED_PACKET_RAIN_UPDATE_STATUS   = "/activity-red-packet-rain/update-status";
	public static final String ACTIVITY_RED_PACKET_RAIN_BILL            = "/activity-red-packet-rain/bill";

	// 幸运大转盘
	public static final String ACTIVITY_REBATE_WHEEL_LIST            = "/activity-rebate-wheel/list";
	public static final String ACTIVITY_REBATE_WHEEL_EDIT            = "/activity-rebate-wheel/edit";
	public static final String ACTIVITY_REBATE_WHEEL_UPDATE_STATUS   = "/activity-rebate-wheel/update-status";
	public static final String ACTIVITY_REBATE_WHEEL_BILL            = "/activity-rebate-wheel/bill";

	// 首充活动
	public static final String ACTIVITY_FIRST_RECHARGE_LIST            = "/activity-first-recharge/list";
	public static final String ACTIVITY_FIRST_RECHARGE_EDIT            = "/activity-first-recharge/edit";
	public static final String ACTIVITY_FIRST_RECHARGE_UPDATE_STATUS   = "/activity-first-recharge/update-status";
	public static final String ACTIVITY_FIRST_RECHARGE_BILL            = "/activity-first-recharge/bill";

	// VIP晋级列表
	public static final String VIP_UPGRADE_LIST_LIST					= "/vip-upgrade-list/list";
	public static final String VIP_UPGRADE_LIST_CALCULATE				= "/vip-upgrade-list/calculate";
	// 特邀VIP	 
	public static final String VIP_UPGRADE_TEYAO						= "/vip-upgrade-list/teyao";

	// VIP晋级礼金
	public static final String VIP_UPGRADE_GIFTS_LIST					= "/vip-upgrade-gifts/list";

	// VIP生日礼金
	public static final String VIP_BIRTHDAY_GIFTS_LIST					= "/vip-birthday-gifts/list";
	public static final String VIP_BIRTHDAY_GIFTS_CALCULATE				= "/vip-birthday-gifts/calculate";

	// VIP免费筹码
	public static final String VIP_FREE_CHIPS_LIST						= "/vip-free-chips/list";
	public static final String VIP_FREE_CHIPS_CALCULATE					= "/vip-free-chips/calculate";
	public static final String VIP_FREE_CHIPS_CONFIRM					= "/vip-free-chips/confirm";

	public static final String VIP_INTEGRAL_EXCHANGE_LIST				= "/vip-integral-exchange/list";

	// 即时统计
	public static final String LOTTERY_INSTANT_STAT_LIST				= "/lottery-instant-stat/list";

	// 余额快照
	public static final String USER_BALANCE_SNAPSHOT_LIST 				= "/user-balance-snapshot/list";

	// 用户管理
	public static final String ADMIN_USER_LIST 							= "/admin-user/list";
	public static final String ADMIN_USER_CHECK_EXIST 					= "/admin-user/check-exist";
	public static final String ADMIN_USER_GET 							= "/admin-user/get";
	public static final String ADMIN_USER_INFO 							= "/admin-user/info";
	public static final String ADMIN_USER_ADD 							= "/admin-user/add";
	public static final String ADMIN_USER_EDIT 							= "/admin-user/edit";
	public static final String ADMIN_USER_UPDATE_STATUS					= "/admin-user/update-status";
	public static final String ADMIN_USER_MOD_LOGIN_PWD					= "/admin-user/mod-login-pwd";
	public static final String ADMIN_USER_MOD_WITHDRAW_PWD				= "/admin-user/mod-withdraw-pwd";
	public static final String ADMIN_USER_CLOSE_GOOGLE_AUTH				= "/admin-user/close-google-auth";
	public static final String ADMIN_USER_CLOSE_WITHDRAW_PWD			= "/admin-user/close-withdraw-pwd";
	public static final String ADMIN_USER_OPEN_WITHDRAW_PWD				= "/admin-user/open-withdraw-pwd";
	public static final String ADMIN_USER_UNLOCK_WITHDRAW_PWD			= "/admin-user/unlock-withdraw-pwd";
	public static final String ADMIN_USER_LOCK_WITHDRAW_PWD				= "/admin-user/lock-withdraw-pwd";
	public static final String ADMIN_USER_RESET_PWD_ERROR				= "/admin-user/reset-pwd-error";
	public static final String ADMIN_USER_EDIT_IPS				        = "/admin-user/edit-ips";

	// 用户角色
	public static final String ADMIN_USER_ROLE_LIST 					= "/admin-user-role/list";
	public static final String ADMIN_USER_ROLE_TREE_LIST 				= "/admin-user-role/tree-list";
	public static final String ADMIN_USER_ROLE_CHECK_EXIST 				= "/admin-user-role/check-exist";
	public static final String ADMIN_USER_ROLE_GET 						= "/admin-user-role/get";
	public static final String ADMIN_USER_ROLE_ADD 						= "/admin-user-role/add";
	public static final String ADMIN_USER_ROLE_EDIT 					= "/admin-user-role/edit";
	public static final String ADMIN_USER_ROLE_UPDATE_STATUS 			= "/admin-user-role/update-status";
	public static final String ADMIN_USER_ROLE_SAVE_ACCESS	 			= "/admin-user-role/save-access";

	// 用户菜单
	public static final String ADMIN_USER_MENU_LIST 					= "/admin-user-menu/list";
	public static final String ADMIN_USER_MENU_UPDATE_STATUS 			= "/admin-user-menu/update-status";
	public static final String ADMIN_USER_MENU_MOVEUP 	= "/admin-user-menu/moveup";
	public static final String ADMIN_USER_MENU_MOVEDOWN 	= "/admin-user-menu/movedown";
	// 用户操作权限
	public static final String ADMIN_USER_ACTION_LIST 					= "/admin-user-action/list";
	public static final String ADMIN_USER_ACTION_JSTREE 				= "/admin-user-action/jstree";
	public static final String AMDIN_USER_ACTION_UPDATE_STATUS 			= "/admin-user-action/update-status";

	// 用户操作日志
	public static final String ADMIN_USER_ACTION_LOG_LIST 				= "/admin-user-action-log/list";
	public static final String ADMIN_USER_LOG_LIST 						= "/admin-user-log/list";
	// 用户操作关键日志
	public static final String ADMIN_USER_CRITICAL_LOG_LIST 			= "/admin-user-critical-log/list";

	// 运维管理
	public static final String LOTTERY_SYS_CONTROL_DO					= "/lottery-sys-control/do";
	public static final String LOTTERY_SYS_CONTROL_STATUS				= "/lottery-sys-control/status";

	// Google Authenticator
	public static final String GOOGLE_AUTH_BIND 						= "/google-auth/bind";
	public static final String GOOGLE_AUTH_AUTHROIZE 					= "/google-auth/authorize";
	public static final String GOOGLE_AUTH_ISBIND 						= "/google-auth/isbind";

	// 契约日结
	public static final String LOTTERY_USER_DAILY_SETTLE_BILL_LIST		= "/lottery-user-daily-settle-bill/list";
	public static final String LOTTERY_USER_DAILY_SETTLE_LIST			= "/lottery-user-daily-settle/list";
	public static final String LOTTERY_USER_DAILY_SETTLE_DEL			= "/lottery-user-daily-settle/del";
	public static final String LOTTERY_USER_DAILY_SETTLE_EDIT_GET 		= "/lottery-user-daily-settle/edit-get";
	public static final String LOTTERY_USER_DAILY_SETTLE_EDIT	    	= "/lottery-user-daily-settle/edit";
	public static final String LOTTERY_USER_DAILY_SETTLE_ADD_GET 		= "/lottery-user-daily-settle/add-get";
	public static final String LOTTERY_USER_DAILY_SETTLE_ADD	    	= "/lottery-user-daily-settle/add";

	// 契约分红
	public static final String LOTTERY_USER_DIVIDEND_LIST			    = "/lottery-user-dividend/list";
	public static final String LOTTERY_USER_DIVIDEND_DEL			    = "/lottery-user-dividend/del";
	public static final String LOTTERY_USER_DIVIDEND_BILL_LIST		    = "/lottery-user-dividend-bill/list";
	public static final String LOTTERY_USER_DIVIDEND_BILL_PLATFORN_LOSS_LIST = "/lottery-user-dividend-bill/platform-loss-list";
	public static final String LOTTERY_USER_DIVIDEND_BILL_GET			= "/lottery-user-dividend-bill/get";
	public static final String LOTTERY_USER_DIVIDEND_BILL_AGREE			= "/lottery-user-dividend-bill/agree";
	public static final String LOTTERY_USER_DIVIDEND_BILL_DENY			= "/lottery-user-dividend-bill/deny";
	public static final String LOTTERY_USER_DIVIDEND_BILL_DEL			= "/lottery-user-dividend-bill/del";
	public static final String LOTTERY_USER_DIVIDEND_BILL_RESET			= "/lottery-user-dividend-bill/reset";
	public static final String LOTTERY_USER_DIVIDEND_EDIT_GET 			= "/lottery-user-dividend/edit-get";
	public static final String LOTTERY_USER_DIVIDEND_EDIT	    		= "/lottery-user-dividend/edit";
	public static final String LOTTERY_USER_DIVIDEND_ADD_GET 			= "/lottery-user-dividend/add-get";
	public static final String LOTTERY_USER_DIVIDEND_ADD	    		= "/lottery-user-dividend/add";

	// 游戏分红
	public static final String USER_GAME_DIVIDEND_BILL_LIST				= "/user-game-dividend-bill/list";
	public static final String USER_GAME_DIVIDEND_BILL_GET				= "/user-game-dividend-bill/get";
	public static final String USER_GAME_DIVIDEND_BILL_AGREE			= "/user-game-dividend-bill/agree";
	public static final String USER_GAME_DIVIDEND_BILL_DENY			    = "/user-game-dividend-bill/deny";
	public static final String USER_GAME_DIVIDEND_BILL_DEL				= "/user-game-dividend-bill/del";

	// 游戏返水
	public static final String USER_GAME_WATER_BILL_LIST				= "/user-game-water-bill/list";

	// 游戏
	public static final String GAME_LIST		                        = "/game/list";
	public static final String GAME_GET		                            = "/game/get";
	public static final String GAME_ADD		                            = "/game/add";
	public static final String GAME_MOD		                            = "/game/mod";
	public static final String GAME_DEL		                            = "/game/del";
	public static final String GAME_MOD_DISPLAY		                    = "/game/mod-display";
	public static final String GAME_CHECK_GAMENAME_EXIST		        = "/game/check-gamename-exist";
	public static final String GAME_CHECK_GAMECODE_EXIST		        = "/game/check-gamecode-exist";
	public static final String GAME_PLATFORM_LIST		                = "/game/platform/list";
	public static final String GAME_PLATFORM_MOD_STATUS		            = "/game/platform/mod-status";
	public static final String GAME_BALANCE		                        = "/game/balance";
	public static final String GAME_BETS_LIST		                    = "/game-bets/list";
	public static final String GAME_BETS_GET		                    = "/game-bets/get";

	// 中奖排行榜
	public static final String USER_BETS_HIT_RANKING_LIST               = "/user-bets-hit-ranking/list";
	public static final String USER_BETS_HIT_RANKING_GET                = "/user-bets-hit-ranking/get";
	public static final String USER_BETS_HIT_RANKING_ADD                = "/user-bets-hit-ranking/add";
	public static final String USER_BETS_HIT_RANKING_EDIT               = "/user-bets-hit-ranking/edit";
	public static final String USER_BETS_HIT_RANKING_DEL                = "/user-bets-hit-ranking/del";
}