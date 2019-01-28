package lottery.domains.content.vo.bets;

import lottery.domains.content.entity.LotteryOpenCode;
import lottery.domains.content.entity.UserBets;

import java.util.List;

/**
 * Created by Nick on 2017-05-19.
 */
public class UserBetsAutoCancelAdapter {
    private LotteryOpenCode lotteryOpenCode;
    private List<UserBets> userBetsList;

    public UserBetsAutoCancelAdapter(LotteryOpenCode lotteryOpenCode, List<UserBets> userBetsList) {
        this.lotteryOpenCode = lotteryOpenCode;
        this.userBetsList = userBetsList;
    }

    public LotteryOpenCode getLotteryOpenCode() {
        return lotteryOpenCode;
    }

    public void setLotteryOpenCode(LotteryOpenCode lotteryOpenCode) {
        this.lotteryOpenCode = lotteryOpenCode;
    }

    public List<UserBets> getUserBetsList() {
        return userBetsList;
    }

    public void setUserBetsList(List<UserBets> userBetsList) {
        this.userBetsList = userBetsList;
    }
}
