package lottery.domains.content.global;

public class Global {
	
	// 账户类型
	public static final int USER_TYPE_PROXY = 1; // 代理
	public static final int USER_TYPE_PLAYER = 2; // 玩家
	public static final int USER_TYPE_RELATED = 3; // 关联账号

	// 总账号ID
	public static final int USER_TOP_ID = 72;

	// 账单账户
	public static final int BILL_ACCOUNT_MAIN = 1; // 主账户
	public static final int BILL_ACCOUNT_LOTTERY = 2; // 彩票账户
	public static final int BILL_ACCOUNT_BACCARAT = 3; // 百家乐账户
	// 账单类型
	public static final int BILL_TYPE_RECHARGE = 1; // 存款
	public static final int BILL_TYPE_WITHDRAWALS = 2; // 取款
	public static final int BILL_TYPE_TRANS_IN = 3; // 转入
	public static final int BILL_TYPE_TRANS_OUT = 4; // 转出
	public static final int BILL_TYPE_ACTIVITY = 5; // 优惠活动
	public static final int BILL_TYPE_SPEND = 6; // 消费
	public static final int BILL_TYPE_PRIZE = 7; // 派奖
	public static final int BILL_TYPE_SPEND_RETURN = 8; // 消费返点
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
	public static final int BILL_TYPE_BILLING_ORDER = 22; // 投注量
	
	// 投注类型
	public static final int USER_BETS_TYPE_GENERAL = 0; // 普通订单
	public static final int USER_BETS_TYPE_CHASE = 1; // 追号订单
	public static final int USER_BETS_TYPE_PLAN = 2; // 计划订单
	
	// 投注状态
	public static final int USER_BETS_STATUS_CANCELED = -1; // 已撤单
	public static final int USER_BETS_STATUS_NOT_OPEN = 0; // 未开奖
	public static final int USER_BETS_STATUS_OPENED = 1; // 已开奖
	public static final int USER_BETS_STATUS_WIN = 2; // 已中奖
	public static final int USER_BETS_STATUS_MONITORING = 3; // 风控中
    public static final int USER_BETS_STATUS_ADMIN_RETURNED = 4; // 人工返还
    public static final int USER_BETS_STATUS_SYS_RETURNED = 5; // 系统返还
	    
	
	// 充值类型
	public static final int USER_RECHARGE_TYPE_ONLINE = 1; // 在线充值
	public static final int USER_RECHARGE_TYPE_TRANSFER = 2; // 转账汇款
	public static final int USER_RECHARGE_TYPE_MOBILE_TRANSFER = 3; // 微信&支付宝转账

	// 在线充值子类型
	public static final int USER_RECHARGE_TYPE_ONLINE_IPS = 1; // IPS
	public static final int USER_RECHARGE_TYPE_ONLINE_BAOFOO = 2; // 宝付
	public static final int USER_RECHARGE_TYPE_ONLINE_NEWPAY = 3; // 新生
	public static final int USER_RECHARGE_TYPE_ONLINE_ECPSS = 4; // 汇潮
	public static final int USER_RECHARGE_TYPE_ONLINE_YEEPAY = 5; // 易宝
	public static final int USER_RECHARGE_TYPE_ONLINE_MOBAO = 6; // 摩宝
	public static final int USER_RECHARGE_TYPE_ONLINE_GOPAY = 7; // 国付宝
	public static final int USER_RECHARGE_TYPE_ONLINE_PAY41 = 8; // 通汇支付
	public static final int USER_RECHARGE_TYPE_ONLINE_DINPAY = 9; // 智付支付
	
	// 转账类型
	public static final int USER_TRANSFERS_TYPE_DEFAULT = 0; // 平台转账
	public static final int USER_TRANSFERS_TYPE_USER_IN = 1; // 上下级转账（存）
	public static final int USER_TRANSFERS_TYPE_USER_OUT = 2; // 上下级转账（取）
	
	// 消息类型
	public static final int USER_MESSAGE_TYPE_USER = 0; // 用户消息
	public static final int USER_MESSAGE_TYPE_SYSTEM = 1; // 系统管理员
	
	// 系统消息类型
	public static final int USER_SYS_MESSAGE_TYPE_SYSTEM = 0; // 系统消息
	public static final int USER_SYS_MESSAGE_TYPE_RECHARGE = 1; // 充值消息
	public static final int USER_SYS_MESSAGE_TYPE_WITHDRAW = 2; // 取款消息
	
	// 活动子类型
	public static final int ACTIVITY_REBATE_REWARD_XIAOFEI = 1; // 消费佣金
	public static final int ACTIVITY_REBATE_REWARD_YINGKUI = 2; // 盈亏佣金
	public static final int ACTIVITY_REBATE_BIND = 3; // 绑定资料
	public static final int ACTIVITY_REBATE_SALARY_ZHISHU = 4; // 直属工资
	public static final int ACTIVITY_REBATE_SALARY_ZONGDAI = 5; // 总代工资
	public static final int ACTIVITY_REBATE_RECHARGE = 6; // 开业大酬宾
	public static final int ACTIVITY_REBATE_RECHARGE_LOOP = 7; // 开业大酬宾，连环嗨不停
	public static final int ACTIVITY_REBATE_SIGN = 8; // 签到活动
	public static final int ACTIVITY_REBATE_PACKET = 12; // 抢红包活动 九游活动已经使用了10 11
	public static final int ACTIVITY_REBATE_COST = 15;//消费奖励
	
	//彩种状态
	public static final int LOTTERY_STATUS_OPEN = 0;//启用状态
	public static final int LOTTERY_STATUS_STOP = -1;//启用状态

	//彩票开奖状态
	public static final int LOTTERY_OPEN_CODE_STATUS_NOT_OPEN = 0;// 未开奖
	public static final int LOTTERY_OPEN_CODE_STATUS_OPENED = 1;// 已开奖
	public static final int LOTTERY_OPEN_CODE_STATUS_UNCANCEL = 2;// 无效待撤单
	public static final int LOTTERY_OPEN_CODE_STATUS_CANCELLED = 3;// 无效已撤单

	//领取状态
	public static final int ACTIVITY_COST_STATUS_B = 0;//不可领（未达标）
	public static final int ACTIVITY_COST_STATUS_K = 1;//可领取
	public static final int ACTIVITY_COST_STATUS_Y = 2;//已领取
	
	//活动状态
	public static final int ACTIVITY_STATUS_OPEN = 0;//启用状态
	public static final int ACTIVITY_STATUS_STOP = -1;//启用状态
}