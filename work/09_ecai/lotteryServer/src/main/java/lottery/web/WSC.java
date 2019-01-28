package lottery.web;

public final class WSC {
	
	private WSC() {}
	
	public static String PROJECT_PATH;
	
	// Session设置
	public static final String HTTP_SESSION_USER = "HTTP_SESSION_USER";
	public static final String LOGIN_VALIDATE_CODE = "LOGIN_VALIDATE_CODE";
	public static final String REGIST_VALIDATE_CODE = "REGIST_VALIDATE_CODE";
	public static final String WEB_SOCKET_TOKEN = "WEB_SOCKET_TOKEN";
	public static final String DISPOSABLE_TOKEN = "DISPOSABLE_TOKEN";

	// redis设置
	/**
	 * 域：用户ID:注单ID:失效时间
	 * 值：json，彩票ID，彩票名称，彩票期号，注单ID，注单状态，投注金额，奖金，玩法名称，通知类型
	 */
	public static final String USER_BETS_NOTICE_KEY = "USER:BETS_NOTICE";
	public static final String USER_GAME_TOKEN_KEY = "USER:GAME:TOKEN:";
	public static final String USER_GAME_BEFORE_URL_KEY = "USER:GAME:BEFORE_URL:";
	public static final String OPEN_CODE_KEY = "OPEN_CODE:%s"; // 开奖号码 %s:彩票编码
	public static final String USER_LIMIT_KEY = "USER:LIMIT:";
	public static final String USER_CURRENT_PRIZE_KEY = "USER:CURRENT_PRIZE:";
	public static final String LOTTERY_CURRENT_PRIZE_KEY = "LOTTERY:CURRENT_PRIZE:";
	public static final String USER_LOGOUT_MSG_KEY = "USER:LOGOUT:MSG:";

	public static final String USER_BETS_RECENT_KEY = "USER:BETS:RECENT:%s"; // 用户最近彩票投注ID  用户ID
	public static final String USER_BETS_RECENT_CHASE_KEY = "USER:BETS:RECENT_CHASE:%s"; // 用户最近彩票追号投注ID  用户ID

	public static final String USER_BETS_UNOPEN_RECENT_KEY = "USER:BETS:UNOPEN:RECENT:%s"; // 用户最近未结算彩票投注ID  用户ID
	public static final String USER_BETS_OPENED_RECENT_KEY = "USER:BETS:OPENED:RECENT:%s"; // 用户最近已结算彩票投注ID  用户ID
}
