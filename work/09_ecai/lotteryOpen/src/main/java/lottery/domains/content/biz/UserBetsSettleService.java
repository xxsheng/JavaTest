package lottery.domains.content.biz;

import lottery.domains.content.entity.Lottery;
import lottery.domains.content.entity.LotteryOpenCode;
import lottery.domains.content.entity.UserBets;

import java.util.List;

/**
 * 用户注单结算service
 * Created by Nick on 2016/8/29.
 */
public interface UserBetsSettleService {
    void settleUserBets(List<UserBets> userBetsList, LotteryOpenCode openCode, Lottery lottery);

    Object[] testUsersBets(UserBets userBets, String openCode, Lottery lottery, boolean cacheDantiao);
}
