package lottery.domains.content.vo.bets;

import lottery.domains.content.entity.User;

/**
 * 上级返点
 * Created by Nick on 2016/8/29.
 */
public class UserBetsSettleUpPoint {
    private User user;
    private Double lotteryMoney; // 彩票账户加钱

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Double getLotteryMoney() {
        return lotteryMoney;
    }

    public void setLotteryMoney(Double lotteryMoney) {
        this.lotteryMoney = lotteryMoney;
    }
}
