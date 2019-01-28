package lottery.web;

public final class AppWUC {

    private AppWUC() {
    	
    }
    // 登出
    public static final String APP_LOGOUT	                = "/App/Logout";

    public static final String APP_LOGIN	                = "/App/Login";
    // 检查是否已经登录
    public static final String APP_CHECK_LOGIN              = "/App/CheckLogin";
    // 获取彩种详情，以及自身返点信息等
    public static final String APP_LOTTERY                  = "/App/Lottery";
    // // 查找注单
    // public static final String APP_USER_BETS_GENERAL_SEARCH	= "/App/UserBetsGeneralSearch";
    // // 查找追号单
    // public static final String APP_USER_BETS_CHASE_SEARCH	= "/App/UserBetsChaseSearch";
    // 列出支付方式
    public static final String APP_LIST_PAYMENT	            = "/App/ListPayment";
    // 列出支付方式
    public static final String APP_RECENT_OPEN_CODE	        = "/App/RecentOpenCode";
}