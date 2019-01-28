package lottery.web;

public final class WUC {
	
    private WUC() {
    	
    }

    // 登录请求
    //public static final String LOGIN						= "/Login";

    // 一次性token
    public static final String DISPOSABLE_TOKEN             = "/DisposableToken";

    // App登录
    public static final String APP_LOGIN					= "/AppLogin";
    
    public static final String DEMO_LOGIN	                = "/DemoLogin";
    // 验证银行卡登录
    public static final String VALIDATE_LOGIN				= "/ValidateLogin";
    // 全局请求，会话的保持
    public static final String GLOBAL_GET					= "/GetGlobal";
    // 用户基本资料
    public static final String USER_BASE_GET				= "/GetUserBase";
    // 获取用户其他信息
    public static final String USER_INFO_GET				= "/GetUserInfo";

    public static final String GET_USER_OTHER				= "/GetUserOther";
    //转账保存
    public static final String TRANSFER_SAVE				= "/transferSave";
    // 获取上次登录信息
    public static final String USER_LAST_LOGIN_GET			= "/GetUserLastLogin";
    // 获取登录日志
    public static final String USER_LOGIN_LOG_GET			= "/GetUserLoginLog";
    //获取用户登录集合分页
    public static final String USER_LOGIN_LOG_LIST			= "/GetUserLoginLogList";
    // 修改用户昵称
    public static final String USER_NICKNAME_MOD			= "/ModUserNickname";
    // 修改用户登录密码
    public static final String USER_LOGIN_PWD_MOD			= "/ModUserLoginPwd";
    // 绑定用户资金密码
    public static final String USER_WITHDRAW_PWD_BIND		= "/BindUserWithdrawPwd";
    // 修改用户资金密码
    public static final String USER_WITHDRAW_PWD_MOD		= "/ModUserWithdrawPwd";
    // 绑定用户取款人
    public static final String USER_WITHDRAW_NAME_BIND		= "/BindUserWithdrawName";
    // 绑定Google
    public static final String USER_GOOGLE_BIND		        = "/BindGoogle";
    // 绑定Google获取二维码
    public static final String USER_GOOGLE_BIND_GET		    = "/BindGoogleGet";
    // 获取用户绑定信息
    public static final String USER_BIND_GET				= "/GetUserBind";
    // 记载用户绑定信息
    public static final String USER_BIND_LOAD				= "/LoadUserBind";
    // 修改用户绑定信息
    public static final String USER_BIND_MOD				= "/ModUserBind";
    // 列出用户银行卡
    public static final String USER_CARD_LIST				= "/ListUserCard";
    // 绑定卡片所需
    public static final String USER_CARD_BIND_NEED			= "/GetBindUserCardNeed";
    // 绑定用户卡片
    public static final String USER_CARD_BIND				= "/BindUserCard";
    // 设置默认卡片
    public static final String USER_CARD_SET_DEFAULT		= "/SetDefaultUserCard";
    // 用户密保获取
    public static final String USER_SECURITY_GET			= "/GetUserSecurity";
    //解绑银行卡
    public static final String USER_UNBIND_CARD             = "/UserUnBindCard";
    // 绑定用户密保
    public static final String USER_SECURITY_BIND			= "/BindUserSecurity";
    // 列出直属下级
    public static final String USER_LIST_DIRECT_LOWER		= "/ListUserDirectLower";
    // // 更新用户引导
    // public static final String USER_NAVIGATE_UPDATE			= "/UpdateUserNavigate";
    // // 更改用户头像
    // public static final String USER_AVATAR_MOD				= "/ModUserAvatar";
    // 启用/关闭登录验证
    public static final String LOGIN_VALIDATE_MOD			= "/ModLoginValidate";
    // 获取随机银行卡
    public static final String RANDOM_CARD_GET  			= "/GetRandomCard";

    // 普通投注请求
    public static final String USER_BETS_GENERAL 			= "/UserBetsGeneral";
    // 追号投注请求
    public static final String USER_BETS_CHASE 				= "/UserBetsChase";
    // 重复投注
    public static final String USER_BETS_REBET 				= "/UserBetsReBet";
	// 用户订单详情
    public static final String USER_BETS_DETAILS			= "/UserBetsDetails";
    // 取消用户订单
    public static final String USER_BETS_CANCEL 			= "/UserBetsCancel";
    // // 发起跟单
    // public static final String USER_BETS_PLAN_PUBLISH 		= "/UserBetsPlanPublish";
    // // 合买大厅搜索跟单
    // public static final String USER_BETS_PLAN_HALL_SEARCH 	= "/UserBetsPlanHallSearch";
    // // 用户跟单
    // public static final String USER_BETS_PLAN_FOLLOW 		= "/UserBetsPlanFollow";
    // // 用户发表计划准备
    // public static final String USER_BETS_PLAN_PREPARE 		= "/UserBetsPlanPrepare";
    // 普通订单查询
    public static final String USER_BETS_GENERAL_SEARCH		= "/UserBetsGeneralSearch";
    // 追号订单查询
    public static final String USER_BETS_CHASE_SEARCH		= "/UserBetsChaseSearch";
    // 快速获取用户最近的投注订单
    public static final String RECENT_USER_BETS_GENERAL_SEARCH  = "/RecentUserBetsSearch";
    // 快速获取用户最近的追号投注订单
    public static final String RECENT_USER_BETS_CHASE_SEARCH	= "/RecentUserBetsChaseSearch";

    // 快速获取用户最近未结算的投注订单
    public static final String RECENT_USER_BETS_UNOPEN_SEARCH  = "/RecentUserBetsUnOpenSearch";
    // 快速获取用户最近非未结算的投注订单
    public static final String RECENT_USER_BETS_OPENED_SEARCH	= "/RecentUserBetsOpenedSearch";

    // // 合买计划查询
    // public static final String USER_BETS_PLAN_SEARCH		= "/UserBetsPlanSearch";
    // 用户投注盈亏
    public static final String USER_BETS_PROFIT				= "/UserBetsProfit";
    // 用户账单查询
    public static final String USER_BILL_SEARCH				= "/UserBillSearch";
    // 账单详细
    public static final String USER_BILL_DETAILS			= "/UserBillDetails";
    // 主账户报表
    public static final String USER_MAIN_REPORT				= "/UserMainReport";
    // 彩票报表
    public static final String USER_LOTTERY_REPORT			= "/UserLotteryReport";
    // 彩票自己的报表
    public static final String USER_SELF_REPORT		        = "/UserSelfReport";
    // 彩票自己的报表（按天数统计）
    public static final String USER_LOTTERY_REPORT_LIST		= "/UserLotteryReportList";
    // 获取今天报表情况
    public static final String USER_TODAY_REPORT	        = "/UserTodayReport";

    // 游戏报表
    public static final String USER_GAME_REPORT			    = "/UserGameReport";
    // 游戏记录
    public static final String USER_GAME_BETS_SEARCH	    = "/UserGameBetsSearch";


    // 代理总览统计，总计
    public static final String PROXY_INDEX_LOAD				= "/LoadProxyIndex";
    // 代理剩余配额
    public static final String PROXY_QUOTA_INFO				= "/ProxyQuotaInfo";
    // 代理直接开户
    public static final String PROXY_USER_ADD				= "/AddProxyUser";
    // 代理添加注册衔接
    public static final String PROXY_LINK_ADD				= "/AddProxyLink";
    // 代理删除注册衔接
    public static final String PROXY_LINK_DEL				= "/DelProxyLink";
    // 代理衔接查询
    public static final String PROXY_LINK_LIST				= "/ProxyLinkList";
    // 代理团队搜索
    public static final String PROXY_USER_SEARCH			= "/ProxyUserSearch";
    // 在线会员列表
    public static final String PROXY_ONLINE_LIST			= "/ProxyOnlineList";
    // 订单查询
    public static final String PROXY_ORDER_SEARCH			= "/ProxyOrderSearch";
    // 订单详情
    public static final String PROXY_ORDER_DETAILS			= "/ProxyOrderDetails";
    // 账单查询
    public static final String PROXY_BILL_SEARCH			= "/ProxyBillSearch";
    // 加载编辑下级
    public static final String PROXY_EDIT_POINT_LOAD		= "/LoadProxyEditPoint";
    // 编辑下级用户
    public static final String PROXY_EDIT_USER_POINT		= "/ProxyEditUserPoint";
    // // 加载按量升点
    // public static final String PROXY_EDIT_POINT_AMOUNT_LOAD	= "/LoadProxyEditPointByAmount";
    // 编辑用户根据用户量
    // public static final String PROXY_EDIT_USER_POINT_AMOUNT	= "/ProxyEditUserPointByAmount";
    // 加载下级充值
    public static final String PROXY_LOAD_RECHARGE			= "/LoadProxyRecharge";
    // 给下级充值
    public static final String PROXY_RECHARGE_USER			= "/ProxyUserRecharge";
    // 加载编辑下级配额
    public static final String PROXY_EDIT_QUOTA_LOAD		= "/LoadProxyEditQuota";
    // 编辑下级用户配额
    public static final String PROXY_EDIT_USER_QUOTA		= "/ProxyEditUserQuota";
    // 用户取现加载
    public static final String USER_WITHDRAWALS_LOAD		= "/LoadUserWithdrawals";
    // 用户提现申请
    public static final String USER_WITHDRAWALS_APPLY		= "/ApplyUserWithdrawals";
    // 用户提现记录
    public static final String USER_WITHDRAWALS_SEARCH		= "/UserWithdrawalsSearch";

    // 发送消息
    public static final String USER_MESSAGE_SEND			= "/SendUserMessage";
    // 获取收件箱消息
    public static final String USER_MESSAGE_INBOX			= "/UserMessageInbox";
    // 获取发件箱消息
    public static final String USER_MESSAGE_OUTBOX			= "/UserMessageOutbox";
    // 删除用户消息
    public static final String USER_MESSAGE_DEL				= "/DelUserMessage";
    // 设为已读
    public static final String USER_MESSAGE_READ			= "/ReadUserMessage";
    // 系统消息
    public static final String USER_SYS_MESSAGE		        = "/UserSysMessage";
    // 设为已读
    public static final String USER_SYS_MESSAGE_READ		= "/ReadUserSysMessage";
    // 删除系统消息
    public static final String USER_SYS_MESSAGE_DEL 		= "/DelUserSysMessage";

    // 列出所有充值方式
    public static final String PAYMENT_LIST					= "/ListPayment";
    // 列出银行列表
    public static final String PAYMENT_BANK_LIST			= "/ListPaymentBank";
    // 第三方充值
    public static final String RECHARGE_ADD				    = "/RechargeAdd";
    // 第三方充值支付跳转
    public static final String RECHARGE_SELF_REDIRECT		= "/RechargeSelfRedirect";
    // 第三方充值支付跳转
    public static final String RECHARGE_REDIRECT		    = "/RechargeRedirect";
    // 转账汇款
    public static final String BANK_TRANSFERS			    = "/BankTransfers";
    
    // 转账汇款
    public static final String BANK_TRANSFERS_LOAD		    = "/LoadBankTransfers";
    // 充值记录
    public static final String USER_RECHARGE_SEARCH			= "/UserRechargeSearch";
    // 加载用户转账
    public static final String USER_TRANSFERS_LOAD			= "/LoadUserTransfers";
    // 用户自己转账
    public static final String USER_TRANSFERS_SELF			= "/SelfUserTransfers";
    // 用户自己转账，资金归集，全部转移至主账户
    public static final String USER_TRANSFERS_SELF_ALL		= "/SelfUserTransfersAll";
    // 转账记录
    public static final String USER_TRANSFERS_SEARCH		= "/UserTransfersSearch";
    //第三方支付 异步通知
    public static final String USER_PAYMENT_NOTIFY          = "/payment-notify";
    //第三方支付同步返回
    public static final String USER_PAYMENT_RESULT          = "/payment-result";
    //第三方代付异步通知 
    public static final String ANOTHER_PAYMENT_NOTIFY          = "/another_payment-notify";
    
    // 日工资活动
    // public static final String ACTIVITY_SALARY_ZS_LOAD		= "/LoadActivitySalaryZhiShu";
    // public static final String ACTIVITY_SALARY_ZS_RECEIVE	= "/ActivitySalaryZhiShuReceive";
    // public static final String ACTIVITY_SALARY_ZD_LOAD		= "/LoadActivitySalaryZongDai";
    // public static final String ACTIVITY_SALARY_ZD_RECEIVE	= "/ActivitySalaryZongDaiReceive";
    
    // 开业大酬宾，连环嗨不停
    // public static final String ACTIVITY_RECHARGE_LOOP_LOAD		= "/LoadActivityRechargeLoop";
    // public static final String ACTIVITY_RECHARGE_LOOP_RECEIVE	= "/ActivityRechargeLoopReceive";
    
	// 签到活动
	public static final String ACTIVITY_SIGN_LOAD			= "/LoadActivitySign";
	public static final String ACTIVITY_SIGN		        = "/ActivitySign";

	// 抢红包活动
	// public static final String ACTIVITY_PACKET_LOAD			= "/LoadActivityPacket";
	  public static final String ACTIVITY_PACKET_SEND			= "/ActivityPacketSend";
	// public static final String ACTIVITY_PACKET_RECEIVE		= "/ActivityPacketReceive";
	
	//消费奖励活动
	// public static final String ACTIVITY_COST_LOAD           = "/activityCostLoad";
	// public static final String ACTIVITY_COST_CONSUMPTION    = "/activityCostConsumption";
	// public static final String ACTIVITY_COST_RECEIVE        = "/activityCostReceive";

    // 红包雨活动
    // public static final String ACTIVITY_REDPACKET_STATUS    = "/activityRedPacketStatus";
    // public static final String ACTIVITY_REDPACKET_COLLECT   = "/activityRedPacketCollect";
    // public static final String ACTIVITY_REDPACKET_TIME      = "/activityRedPacketTime";

    // 幸运大转盘
    public static final String ACTIVITY_WHEEL_LOAD			= "/LoadActivityWheel";
    public static final String ACTIVITY_LIST_LOAD			= "/LoadActivityList";
    public static final String ACTIVITY_WHEEL_DRAW		    = "/ActivityWheelDraw";

    // web socket token
    public static final String WEB_SOCKET_TOKEN             = "/webSocketToken";

    // VIP积分兑换
    // public static final String VIP_INTEGRAL_EXCHANGE_LOAD	= "/LoadVipIntegralExchange";
    // public static final String VIP_INTEGRAL_EXCHANGE_DO		= "/DoVipIntegralExchange";
    
    // VIP晋级礼金
    // public static final String VIP_UPGRADE_GIFTS_GET		= "/GetVipUpgradeGifts";
    // public static final String VIP_UPGRADE_GIFTS_RECEIVE	= "/VipUpgradeGiftsReceive";
    
    // VIP生日礼金
    // public static final String VIP_BIRTHDAY_GIFTS_GET		= "/GetVipBirthdayGifts";
    // public static final String VIP_BIRTHDAY_GIFTS_RECEIVE	= "/VipBirthdayGiftsReceive";
    
    // VIP生日礼金
    // public static final String VIP_FREE_CHIPS_GET			= "/GetFreeChipsGifts";
    // public static final String VIP_FREE_CHIPS_RECEIVE		= "/VipFreeChipsReceive";
    
    // 公告
    public static final String SYS_NOTICE_LIST			    = "/SysNoticeList";
    public static final String SYS_NOTICE_LIST_SIMPLE		= "/SysNoticeListSimple";
    public static final String SYS_NOTICE_LAST_SIMPLE		= "/SysNoticeLastSimple";
    // 试玩用户注册
    public static final String DEMO_USER_LOGIN				= "/DemoUserLogin";
    
    // 用户注册
    public static final String USER_REGIST					= "/UserRegist";
    
    // 用户是否存在
    public static final String USER_CHECK_EXIST				= "/UserCheckExist";
    // 客服地址
    public static final String SERVICE_KEFU					= "/ServiceKefu";
    
    // 获取彩票信息
    public static final String LOTTERY_GET					= "/GetLottery";
    // public static final String LOTTERY_OPEN_INFO			= "/LotteryOpenInfo";
    public static final String LOTTERY_OPEN_TIME_CODE       = "/LotteryOpenTimeCode";
    public static final String LOTTERY_OPEN_TIME			= "/LotteryOpenTime";
    public static final String LOTTERY_LAST_OPEN_TIME		= "/LotteryLastOpenTime";
    public static final String LOTTERY_LAST_EXPECT		    = "/LotteryLastExpect";
    public static final String LOTTERY_CHASE_TIME			= "/LotteryChaseTime";
    public static final String LOTTERY_OPENCODE				= "/LotteryOpenCode";
    public static final String LOTTERY_CODE_TREND			= "/LotteryCodeTrend";
//    public static final String LOTTERY_PLAY_RULES			= "/LotteryPlayRules";
    public static final String USER_BETS_HIT_RANKING        = "/UserBetsHitRanking"; // 中奖排行榜

    // 契约日结
    public static final String DAILY_SETTLE_SEARCH          = "/DailySettleSearch"; // 查询契约日结
    public static final String DAILY_SETTLE_REQUEST         = "/DailySettleRequest"; // 发起契约日结
    public static final String DAILY_SETTLE_REQUEST_DATA    = "/DailySettleRequestData"; // 请求自己的契约日结记录
    public static final String DAILY_SETTLE_BILL_SEARCH     = "/DailySettleBillSearch"; // 查询契约日结账单
    public static final String DAILY_SETTLE_BILL_DETAILS    = "/DailySettleBillDetails"; // 获取契约日结账单详情
    public static final String DAILY_SETTLE_LIST_LOWER      = "/DailySettleListLower"; // 列出可以发起契约日结的用户
    public static final String DAILY_SETTLE_AGREE           = "/DailySettleAgree"; // 接受契约日结
    public static final String DAILY_SETTLE_DENY            = "/DailySettleDeny"; // 拒绝契约日结
    public static final String SEARCH_DIVIDEND_INFO          = "/DividendSettleInfo"; // 查询契约分红日结

    // 契约分红
    public static final String DIVIDEND_SEARCH          = "/DividendSearch"; // 查询契约分红
    public static final String DIVIDEND_GET             = "/DividendGet"; // 查询契约分红账单详情
    public static final String DIVIDEND_REQUEST         = "/DividendRequest"; // 发起契约分红
    public static final String DIVIDEND_REQUEST_DATA    = "/DividendRequestData"; // 请求自己的契约分红记录
    public static final String DIVIDEND_BILL_SEARCH     = "/DividendBillSearch"; // 查询契约分红账单
    public static final String DIVIDEND_LIST_LOWER      = "/DividendListLower"; // 列出可以发起契约分红的用户
    public static final String DIVIDEND_AGREE           = "/DividendAgree"; // 接受契约分红
    public static final String DIVIDEND_DENY            = "/DividendDeny"; // 拒绝契约分红
    public static final String DIVIDEND_COLLECT         = "/DividendCollect"; // 领取契约分红
//    public static final String DIVIDEND_ISSUE           = "/DividendIssue"; // 发放契约分红
    //是否显示契约日结与分红
    public static final String SHOW_DAIYU               = "/SHOW_DAIYU";
    // 老虎机真人体育分红
    public static final String GAME_DIVIDEND_BILL_SEARCH     = "/GameDividendBillSearch"; // 查询老虎机真人体育分红
    public static final String GAME_DIVIDEND_BILL_GET        = "/GameDividendGet"; // 查询老虎机真人体育分红
    public static final String GAME_DIVIDEND_BILL_COLLECT    = "/GameDividendCollect"; // 领取老虎机真人体育分红
    public static final String GAME_WATER_BILL_SEARCH	     = "/GameWaterBillSearch"; // 查询老虎机真人体育返水

    // 账户余额
    public static final String ACCOUNT_BALANCE          = "/AccountBalance";


    // 游戏
    public static final String GAME_TYPE_LIST           = "/GameTypeSearch";
    public static final String GAME_LIST                = "/GameSearch";
    public static final String PT_USER_INFO             = "/PTUserInfo";
    public static final String PT_MOD_PWD               = "/PTModPwd";
    public static final String AG_USER_INFO             = "/AGUserInfo";
    public static final String PROXY_GAME_ORDER_SEARCH	= "/ProxyGameOrderSearch";


    // AG客户端验证
    public static final String AG_VALIDATION = "/ag/validation";

    // IM验证
    public static final String IM_VALIDATION = "/im/validation";
}