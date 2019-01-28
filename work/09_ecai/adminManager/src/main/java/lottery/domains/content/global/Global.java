package lottery.domains.content.global;

public class Global {

	// 账户类型
	public static final int USER_TYPE_PROXY = 1; // 代理
	public static final int USER_TYPE_PLAYER = 2; // 玩家
	public static final int USER_TYPE_RELATED = 3; // 关联账号
	public static final int USER_TYPE_DEMO = 4; // 试玩账号
	// 总账号ID
	public static final int USER_TOP_ID = 72;

	// 账单账户
	public static final int BILL_ACCOUNT_MAIN = 1; // 主账户
	public static final int BILL_ACCOUNT_LOTTERY = 2; // 彩票账户
	public static final int BILL_ACCOUNT_BACCARAT = 3; // 百家乐账户
	public static final int BILL_ACCOUNT_PT = 11; // PT账户
	public static final int BILL_ACCOUNT_AG = 4; // AG账户
	public static final int BILL_ACCOUNT_SB = 13; // SB账户
	// 账单类型
	public static final int BILL_TYPE_RECHARGE = 1; // 存款
	public static final int BILL_TYPE_WITHDRAWALS = 2; // 取款
	public static final int BILL_TYPE_TRANS_IN = 3; // 转入
	public static final int BILL_TYPE_TRANS_OUT = 4; // 转出
	public static final int BILL_TYPE_ACTIVITY = 5; // 优惠活动
	public static final int BILL_TYPE_SPEND = 6; // 消费gh
	public static final int BILL_TYPE_PRIZE = 7; // 派奖
	public static final int BILL_TYPE_SPEND_RETURN = 8; // 投注返点
	public static final int BILL_TYPE_PROXY_RETURN = 9; // 代理返点
	public static final int BILL_TYPE_CANCEL_ORDER = 10; // 取消订单
	public static final int BILL_TYPE_WATER_RETURN = 11; // 会员返水
	public static final int BILL_TYPE_DIVIDEND = 12; // 代理分红
	public static final int BILL_TYPE_ADMIN_ADD = 13; // 管理员增
	public static final int BILL_TYPE_ADMIN_MINUS = 14; // 管理员减
	public static final int BILL_TYPE_USER_TRANS = 15; // 上下级转账
	public static final int BILL_TYPE_DRAW_BACK = 16; // 取款退回
	public static final int BILL_TYPE_INTEGRAL = 17; // 积分兑换
	public static final int BILL_TYPE_REWARD_PAY = 18; // 支付佣金
	public static final int BILL_TYPE_REWARD_INCOME = 19; // 收取佣金
	public static final int BILL_TYPE_REWARD_RETURN = 20; // 退还佣金
	public static final int BILL_TYPE_RED_PACKET = 21; // 红包
	public static final int BILL_TYPE_DAILY_SETTLE = 22; // 日结
	
	
	// 投注类型
	public static final int USER_BETS_TYPE_GENERAL = 0; // 普通订单
	public static final int USER_BETS_TYPE_CHASE = 1; // 追号订单
	public static final int USER_BETS_TYPE_PLAN = 2; // 计划订单
	
	// 投注状态类型
	public static final int USER_BETS_STATUS_NOT_OPEN = 0; // 未开奖
	public static final int USER_BETS_STATUS_CANCELED = -1; // 已撤单
	public static final int USER_BETS_STATUS_OPENED = 1; // 已开奖
	public static final int USER_BETS_STATUS_WIN = 2; // 已中奖
	
	// 充值类型
	public static final int PAYMENT_CHANNEL_TYPE_WANGYING = 1; // 网银充值
	public static final int PAYMENT_CHANNEL_TYPE_MOBILE = 2; // 手机充值
	public static final int PAYMENT_CHANNEL_TYPE_SYSTEM = 3; // 系统充值
	public static final int PAYMENT_CHANNEL_TYPE_TRANSFER = 4; // 上下级转账

	// 网银充值
	public static final int PAYMENT_CHANNEL_SUB_TYPE_WANGYING_ONLINE = 1; // 网银在线
	public static final int PAYMENT_CHANNEL_SUB_TYPE_WANGYING_TRANSFER = 2; // 网银转账
	public static final int PAYMENT_CHANNEL_SUB_TYPE_WANGYING_SPEED = 3; // 快捷支付
	public static final int PAYMENT_CHANNEL_SUB_TYPE_WANGYING_YLSCAN = 4; // 银联扫码
	public static final int PAYMENT_CHANNEL_SUB_TYPE_WANGYING_SCAN = 5; // 网银扫码转账

	// 手机充值
	public static final int PAYMENT_CHANNEL_SUB_TYPE_MOBILE_WECHAT_ONLINE = 1; // 微信在线
	public static final int PAYMENT_CHANNEL_SUB_TYPE_MOBILE_WECHAT_SCAN = 2; // 微信扫码转账
	public static final int PAYMENT_CHANNEL_SUB_TYPE_MOBILE_ALIPAY_ONLINE = 3; // 支付宝在线
	public static final int PAYMENT_CHANNEL_SUB_TYPE_MOBILE_ALIPAY_SCAN = 4; // 支付宝扫码转账
	public static final int PAYMENT_CHANNEL_SUB_TYPE_MOBILE_QQ_ONLINE = 5; // QQ在线
	public static final int PAYMENT_CHANNEL_SUB_TYPE_MOBILE_QQ_SCAN = 6; // QQ扫码转账
	public static final int PAYMENT_CHANNEL_SUB_TYPE_MOBILE_JD_ONLINE = 7; // 京东钱包

	// 系统充值子类型
	public static final int PAYMENT_CHANNEL_SUB_TYPE_SYSTEM_NOTARRIVAL = 1; // 充值未到账
	public static final int PAYMENT_CHANNEL_SUB_TYPE_SYSTEM_ACTIVITY = 2; // 优惠活动
	public static final int PAYMENT_CHANNEL_SUB_TYPE_SYSTEM_ADD = 3; // 修改资金（增）
	public static final int PAYMENT_CHANNEL_SUB_TYPE_SYSTEM_MINUS = 4; // 修改资金（减）

	// 上下级转账子类型
	public static final int PAYMENT_CHANNEL_SUB_TYPE_TRANSFER_IN = 1; // 转入
	public static final int PAYMENT_CHANNEL_SUB_TYPE_TRANSFER_OUT = 2; // 转出

	// 转账类型
	public static final int USER_TRANSFERS_TYPE_DEFAULT = 0; // 平台转账
	public static final int USER_TRANSFERS_TYPE_USER_IN = 1; // 上下级转账（存）
	public static final int USER_TRANSFERS_TYPE_USER_OUT = 2; // 上下级转账（取）

	// 消息状态
	public static final int USER_MESSAGE_STATUS_UNREAD = 0;//未读
	public static final int USER_MESSAGE_STATUS_READED = 1;//已读
	public static final int USER_MESSAGE_STATUS_DELETED = -1;//已删除
	
	// 系统消息类型
	public static final int USER_SYS_MESSAGE_TYPE_SYSTEM = 0; // 系统消息
	public static final int USER_SYS_MESSAGE_TYPE_RECHARGE = 1; // 充值消息
	public static final int USER_SYS_MESSAGE_TYPE_WITHDRAW = 2; // 取款消息

	// 系统消息状态
	public static final int USER_SYS_MESSAGE_STATUS_UNREAD = 0;//未读
	public static final int USER_SYS_MESSAGE_STATUS_READED = 1;//已读
	public static final int USER_SYS_MESSAGE_STATUS_DELETED = -1;//已删除
	
	// 活动子类型
	public static final int ACTIVITY_REBATE_REWARD_XIAOFEI = 1; // 消费佣金
	public static final int ACTIVITY_REBATE_REWARD_YINGKUI = 2; // 盈亏佣金
	public static final int ACTIVITY_REBATE_BIND = 3; // 绑定资料
	public static final int ACTIVITY_REBATE_SALARY_ZHISHU = 4; // 直属工资
	public static final int ACTIVITY_REBATE_SALARY_ZONGDAI = 5; // 总代工资
	public static final int ACTIVITY_REBATE_RECHARGE = 6; // 开业大酬宾
	public static final int ACTIVITY_REBATE_RECHARGE_LOOP = 7; // 开业大酬宾，连环嗨不停
	public static final int ACTIVITY_REBATE_SIGN = 8; // 签到活动
	public static final int ACTIVITY_REBATE_GRAB = 10; // 拆红包活动
	public static final int ACTIVITY_REBATE_PACKAGE = 11; // 拆红包活动红包金额和数量
	public static final int ACTIVITY_REBATE_PACKET = 12;//抢红包
	public static final int ACTIVITY_REBATE_COST = 15;//消费奖励
	public static final int ACTIVITY_REBATE_WHEEL = 16;//幸运大转盘

	public static final String PAYMENT_CHANNEL_HT = "ht"; // 汇通网银
	public static final String PAYMENT_CHANNEL_HTQQ = "htQQ"; // 汇通QQ钱包
	public static final String PAYMENT_CHANNEL_HTWECHAT = "htWeChat"; // 汇通微信
	public static final String PAYMENT_CHANNEL_HTALIPAY = "htAlipay"; // 汇通支付宝
	public static final String PAYMENT_CHANNEL_HTJDPAY = "htJDPay"; // 汇通京东钱包

	public static final String PAYMENT_CHANNEL_ZS = "zs"; // 泽圣网银
	public static final String PAYMENT_CHANNEL_ZSWECHAT = "zsWeChat"; // 泽圣微信
	public static final String PAYMENT_CHANNEL_ZSALIPAY = "zsAlipay"; // 泽圣支付宝
	public static final String PAYMENT_CHANNEL_ZSQQ = "zsQQ"; // 泽圣QQ钱包
	
	public static final String PAYMENT_CHANNEL_AF = "af"; // AF网银
	public static final String PAYMENT_CHANNEL_AFWECHAT = "afWeChat"; // AF微信
	public static final String PAYMENT_CHANNEL_AFALIPAY = "afAlipay"; // AF支付宝
	public static final String PAYMENT_CHANNEL_AFQUICK = "afQuick"; // AF快捷支付
	public static final String PAYMENT_CHANNEL_AFQQ = "afQQ"; // AFQQ钱包

	public static final String PAYMENT_CHANNEL_RX = "rx"; // 荣讯网银
	public static final String PAYMENT_CHANNEL_RXWECHAT = "rxWeChat"; // 荣讯网银
	public static final String PAYMENT_CHANNEL_RXQQ = "rxQQ"; // 荣讯QQ

	public static final String PAYMENT_CHANNEL_CF = "cf"; // 创富网银
	public static final String PAYMENT_CHANNEL_CFWECHAT = "cfWeChat"; // 创富微信
	public static final String PAYMENT_CHANNEL_CFALIPAY = "cfAlipay"; // 创富支付宝
	public static final String PAYMENT_CHANNEL_CFQQ = "cfQQ"; // 创富QQ
	public static final String PAYMENT_CHANNEL_CFJDPAY = "cfJDPay"; // 创富京东钱包

	public static final String PAYMENT_CHANNEL_FKT = "fkt"; // 福卡通网银
	public static final String PAYMENT_CHANNEL_FKTWECHAT = "fktWeChat"; // 福卡通微信
	public static final String PAYMENT_CHANNEL_FKTALIPAY = "fktAlipay"; // 福卡通支付宝
	public static final String PAYMENT_CHANNEL_FKTQQ = "fktQQ"; // 福卡通QQ
	public static final String PAYMENT_CHANNEL_FKTJDPAY = "fktJDPay"; // 福卡通京东钱包

	public static final String PAYMENT_CHANNEL_HTF = "htf"; // 汇天付网银
	public static final String PAYMENT_CHANNEL_HTFQQ = "htfQQ"; // 汇天付QQ钱包
	public static final String PAYMENT_CHANNEL_HTFWECHAT = "htfWeChat"; // 汇天付微信
	public static final String PAYMENT_CHANNEL_HTFALIPAY = "htfAlipay"; // 汇天付支付宝
	public static final String PAYMENT_CHANNEL_HTFJDPAY = "htfJDPay"; // 汇天付京东钱包
	
	public static final String PAYMENT_CHANNEL_YR = "yr"; // 易云网银
	public static final String PAYMENT_CHANNEL_YRQQ = "yrQQ"; // 易云QQ钱包
	public static final String PAYMENT_CHANNEL_YRWECHAT = "yrWeChat"; // 易云微信
	public static final String PAYMENT_CHANNEL_YRALIPAY   =  "yrAlipay"; // 易云支付宝
	
	public static final String PAYMENT_CHANNEL_TGF = "tgf"; // 天机付网银
	public static final String PAYMENT_CHANNEL_TGFQUICK = "tgfQuick"; //天机付快捷支付
	public static final String PAYMENT_CHANNEL_TGFQQ = "tgfQQ"; // 天机付QQ钱包
	public static final String PAYMENT_CHANNEL_TGFJDPAY = "tgfJDPay"; // 天机付京东钱包
	


	//彩票开奖状态
	public static final int LOTTERY_OPEN_CODE_STATUS_NOT_OPEN = 0;// 未开奖
	public static final int LOTTERY_OPEN_CODE_STATUS_OPENED = 1;// 已开奖
	public static final int LOTTERY_OPEN_CODE_STATUS_UNCANCEL = 1;// 无效待撤单
	public static final int LOTTERY_OPEN_CODE_STATUS_CANCELLED = 2;// 无效已撤单
	
	// 契约日结状态
	public static final int DAILY_SETTLE_VALID = 1; // 生效
	public static final int DAILY_SETTLE_REQUESTED = 2; // 待同意
	public static final int DAILY_SETTLE_EXPIRED = 3; // 已过期
	public static final int DAILY_SETTLE_INVALID = 4; // 无效
	public static final int DAILY_SETTLE_DENY = 5; // 已拒绝

	// 契约日结账单状态
	public static final int DAILY_SETTLE_BILL_ISSUED = 1; // 已发放
	public static final int DAILY_SETTLE_BILL_PART_RECEIVED = 2; // 部分发放
	public static final int DAILY_SETTLE_BILL_INSUFFICIENT = 3; // 余额不足
	public static final int DAILY_SETTLE_BILL_NOT_REACHED = 4; // 未达标
	public static final int DAILY_SETTLE_BILL_REJECTED = 5; // 已拒绝

	// 契约日结账单发放类型
	public static final int DAILY_SETTLE_ISSUE_TYPE_PLATFORM = 1; // 平台发放
	public static final int DAILY_SETTLE_ISSUE_TYPE_UPPER = 2; // 上级发放

	// 契约分红状态
	public static final int DIVIDEND_VALID = 1; // 生效
	public static final int DIVIDEND_REQUESTED = 2; // 待同意
	public static final int DIVIDEND_EXPIRED = 3; // 已过期
	public static final int DIVIDEND_INVALID = 4; // 无效
	public static final int DIVIDEND_DENY = 5; // 已拒绝

	// 契约分红账单状态
	public static final int DIVIDEND_BILL_ISSUED = 1; // 已发放
	public static final int DIVIDEND_BILL_UNAPPROVE = 2; // 待审核
	public static final int DIVIDEND_BILL_UNCOLLECT = 3; // 待领取
	public static final int DIVIDEND_BILL_DENIED = 4; // 已拒绝
	public static final int DIVIDEND_BILL_NOT_REACHED = 5; // 未达标
	public static final int DIVIDEND_BILL_INSUFFICIENT = 6; // 余额不足
	public static final int DIVIDEND_BILL_PART_COLLECTED = 7; // 部分发放
	public static final int DIVIDEND_BILL_EXPIRED = 8; // 已过期

	public static final int DIVIDEND_ISSUE_TYPE_PLATFORM = 1; // 平台发放
	public static final int DIVIDEND_ISSUE_TYPE_UPPER = 2; // 上级发放

	// 游戏分红账单状态
	public static final int GAME_DIVIDEND_BILL_ISSUED = 1; // 已发放
	public static final int GAME_DIVIDEND_BILL_UNAPPROVE = 2; // 待审核
	public static final int GAME_DIVIDEND_BILL_UNCOLLECT = 3; // 待领取
	public static final int GAME_DIVIDEND_BILL_DENIED = 4; // 已拒绝
	public static final int GAME_DIVIDEND_BILL_NOT_REACHED = 5; //

	// 游戏返水
	public static final int GAME_WATER_BILL_TYPE_USER = 1; // 用户消费返水
	public static final int GAME_WATER_BILL_TYPE_PROXY = 2; // 代理返水
	public static final int GAME_WATER_BILL_STATUS_ISSUED = 1; // 已发放
	public static final int GAME_WATER_BILL_STATUS_DENIED = 2; // 已拒绝

	// 打款状态
	public static final int USER_WITHDRAW_REMITSTATUS_UNPROCESS = 0; // 待处理
	public static final int USER_WITHDRAW_REMITSTATUS_BANK_PROCESSING = 1; // 银行处理中
	public static final int USER_WITHDRAW_REMITSTATUS_BANK_PROCESSED = 2; // 打款完成
	public static final int USER_WITHDRAW_REMITSTATUS_THIRD_UNPROCESS = 3; // 第三方待处理
	public static final int USER_WITHDRAW_REMITSTATUS_REQUEST_FAILED = -1; // 请求失败
	public static final int USER_WITHDRAW_REMITSTATUS_PROCESS_FAILED = -2; // 打款失败
	public static final int USER_WITHDRAW_REMITSTATUS_SYNC_STATUS = -3; // 查询状态中
	public static final int USER_WITHDRAW_REMITSTATUS_UNKNOWN = -4; // 未知状态
	public static final int USER_WITHDRAW_REMITSTATUS_THIRD_PROCESS_FAILED = -5; // 第三方处理失败
	public static final int USER_WITHDRAW_REMITSTATUS_BANK_PROCESS_FAILED = -6; // 银行处理失败
	public static final int USER_WITHDRAW_REMITSTATUS_THIRD_DENIED = -7; // 第三方拒绝支付

	//user_withdraw_limit 充值类型
	public static final int USER_WITHDRAW_LIMIT_TYPE_ONLINE = 1;//网银充值
	public static final int USER_WITHDRAW_LIMIT_TYPE_MOBILE = 2;//微信&支付宝
	public static final int USER_WITHDRAW_LIMIT_TYPE_ADMIN = 3;//充值未到账
	public static final int USER_WITHDRAW_TYPE_USER_IN = 4; // 上下级转账（存）
	public static final int USER_WITHDRAW_TYPE_USER_OUT = 5; // 上下级转账（取）
	public static final int USER_WITHDRAW_LIMIT_TYPE_ACTIVITY = 6; // 活动补贴

	public static final int ADD_MONEY_TYPE_AUTO = 1; // 自动上分
	public static final int ADD_MONEY_TYPE_MANUAL = 2; // 手动上分

	public static final int WITHDRAW_PAY_TYPE_MANUAL_API = 1; // 1：手动API代付；
	public static final int WITHDRAW_PAY_TYPE_MANUAL = 2; // 2：手动；
	public static final int WITHDRAW_PAY_TYPE_AUTO_MANUAL = 3; // 3：自动API代付；

	public static final int DAIYU_FIXED_FLOAT = 0; // 浮动比例
	public static final int DAIYU_FIXED_FIXED= 1; // 固定比例

	public static final int DAIYU_IS_CJ_ZHAO_SHANG_YES = 1; // 是超级招商
	public static final int DAIYU_IS_CJ_ZHAO_SHANG_NO = 0; // 不是超级招商
}