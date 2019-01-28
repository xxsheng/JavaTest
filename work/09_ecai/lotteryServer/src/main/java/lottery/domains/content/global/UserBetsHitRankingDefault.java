package lottery.domains.content.global;

import javautils.date.Moment;
import lottery.domains.content.entity.UserBetsHitRanking;

import java.util.ArrayList;
import java.util.List;

/**
 * 中奖排行榜默认列表
 * Created by Nick on 2016/09/19
 */
public final class UserBetsHitRankingDefault {
    private UserBetsHitRankingDefault(){}
    public static final List<UserBetsHitRanking> DEFAULT_LIST = new ArrayList<>();
    static {
        Moment now = new Moment();

        int hours = 24;
        DEFAULT_LIST.add(new UserBetsHitRanking(0, "重庆时时彩", "Qwws1234" , 1956 * 30, now.subtract(hours--, "hours").toSimpleTime(), "cqssc", "1", 2));
        DEFAULT_LIST.add(new UserBetsHitRanking(0, "腾讯分分彩", "nnh1234", 1954 * 28, now.subtract(hours--, "hours").toSimpleTime(), "txffc", "1", 2));
        DEFAULT_LIST.add(new UserBetsHitRanking(0, "重庆时时彩", "Nih1234", 1952 * 27, now.subtract(hours--, "hours").toSimpleTime(), "cqssc", "1", 2));
        DEFAULT_LIST.add(new UserBetsHitRanking(0, "重庆时时彩", "jIL1234", 1948 * 26, now.subtract(hours--, "hours").toSimpleTime(), "cqssc", "1", 2));
        DEFAULT_LIST.add(new UserBetsHitRanking(0, "腾讯分分彩", "dan1234", 1948 * 25, now.subtract(hours--, "hours").toSimpleTime(), "txffc", "1", 2));

        DEFAULT_LIST.add(new UserBetsHitRanking(0, "腾讯分分彩", "rio1234", 1948 * 24, now.subtract(hours--, "hours").toSimpleTime(), "txffc", "1", 2));
        DEFAULT_LIST.add(new UserBetsHitRanking(0, "重庆时时彩", "avv1234", 1946 * 23, now.subtract(hours--, "hours").toSimpleTime(), "cqssc", "1", 2));
        DEFAULT_LIST.add(new UserBetsHitRanking(0, "腾讯分分彩", "dde1234", 1946 * 22, now.subtract(hours--, "hours").toSimpleTime(), "txffc", "1", 2));
        DEFAULT_LIST.add(new UserBetsHitRanking(0, "腾讯分分彩", "pui1234", 1946 * 21, now.subtract(hours--, "hours").toSimpleTime(), "txffc", "1", 2));
        DEFAULT_LIST.add(new UserBetsHitRanking(0, "腾讯分分彩", "pui1234", 1944 * 20, now.subtract(hours--, "hours").toSimpleTime(), "txffc", "1", 2));

        // DEFAULT_LIST.add(new UserBetsHitRanking(0, "重庆时时彩", "wei1234", 1944 * 18, now.subtract(hours--, "hours").toSimpleTime(), "cqssc", "1", 2));
        // DEFAULT_LIST.add(new UserBetsHitRanking(0, "重庆时时彩", "yx81234", 1944 * 16, now.subtract(hours--, "hours").toSimpleTime(), "cqssc", "1", 2));
        // DEFAULT_LIST.add(new UserBetsHitRanking(0, "北京PK10", "tyy1234", 1942 * 14, now.subtract(hours--, "hours").toSimpleTime(), "bjpk10", "6", 2));
        // DEFAULT_LIST.add(new UserBetsHitRanking(0, "北京PK10", "bww1234", 1942 * 13, now.subtract(hours--, "hours").toSimpleTime(), "bjpk10", "6", 2));
        // DEFAULT_LIST.add(new UserBetsHitRanking(0, "山东11选5", "czp1234", 1942 * 12, now.subtract(hours--, "hours").toSimpleTime(), "sd11x5", "2", 2));
        //
        // DEFAULT_LIST.add(new UserBetsHitRanking(0, "山东11选5", "tee1234", 1940 * 11, now.subtract(hours--, "hours").toSimpleTime(), "sd11x5", "2", 2));
        // DEFAULT_LIST.add(new UserBetsHitRanking(0, "天津时时彩", "leq1234", 1940 * 10, now.subtract(hours--, "hours").toSimpleTime(), "tjssc", "1", 2));
        // DEFAULT_LIST.add(new UserBetsHitRanking(0, "重庆时时彩", "wos1234", 1940 * 9, now.subtract(hours--, "hours").toSimpleTime(), "cqssc", "1", 2));
        // DEFAULT_LIST.add(new UserBetsHitRanking(0, "重庆时时彩", "daj1234", 1940 * 8, now.subtract(hours--, "hours").toSimpleTime(), "cqssc", "1", 2));
        // DEFAULT_LIST.add(new UserBetsHitRanking(0, "重庆时时彩", "boc1234", 1940 * 6, now.subtract(hours--, "hours").toSimpleTime(), "cqssc", "1", 2));
    }
}
