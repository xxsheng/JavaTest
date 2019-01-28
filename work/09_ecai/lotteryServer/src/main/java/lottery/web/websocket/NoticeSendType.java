package lottery.web.websocket;

/**
 * Created by Nick on 2017/2/25.
 */
public final class NoticeSendType {
    public static final int LOTTERY_OPEN_CODE = 1; // 彩票抓取号码
    public static final int USER_BETS_NOTICE = 2; // 注单结果通知
    public static final int REDPACKET_RAIN_START = 3; // 红包雨活动开始
    public static final int REDPACKET_RAIN_END = 4; // 红包雨活动结束
}
