package lottery.domains.content.vo.bets;

import lottery.domains.content.entity.User;
import lottery.domains.content.entity.UserBets;

import java.util.List;

/**
 * 结算注单中产生的基本信息
 * Created by Nick on 2016/8/29.
 */
public class UserBetsSettleResult {
    private User user; // 注单用户
    private Integer userBetsStatus; // 用户注单状态
    private boolean newSettle; // 新的计算，不是重结算的
    private String settleTime; // 结算时间
    private String openCode; // 开奖号码
    private Double prize; // 中奖金额，没中就是0
    private UserBets userBets; // 注单
    private Double lotteryMoney; // 彩票账户加钱，包含中奖的钱与返点的钱，总数
    private Double freezeMoney; // 解冻金额，总数
    private List<UserBetsSettleBill> bills; // 包含自己的账单与上级代理的账单
    private List<UserBetsSettleUpPoint> upPoints; // 上级返点

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Integer getUserBetsStatus() {
        return userBetsStatus;
    }

    public void setUserBetsStatus(Integer userBetsStatus) {
        this.userBetsStatus = userBetsStatus;
    }

    public boolean isNewSettle() {
        return newSettle;
    }

    public void setNewSettle(boolean newSettle) {
        this.newSettle = newSettle;
    }

    public String getSettleTime() {
        return settleTime;
    }

    public void setSettleTime(String settleTime) {
        this.settleTime = settleTime;
    }

    public String getOpenCode() {
        return openCode;
    }

    public void setOpenCode(String openCode) {
        this.openCode = openCode;
    }

    public Double getPrize() {
        return prize;
    }

    public void setPrize(Double prize) {
        this.prize = prize;
    }

    public UserBets getUserBets() {
        return userBets;
    }

    public void setUserBets(UserBets userBets) {
        this.userBets = userBets;
    }

    public Double getLotteryMoney() {
        return lotteryMoney;
    }

    public void setLotteryMoney(Double lotteryMoney) {
        this.lotteryMoney = lotteryMoney;
    }

    public Double getFreezeMoney() {
        return freezeMoney;
    }

    public void setFreezeMoney(Double freezeMoney) {
        this.freezeMoney = freezeMoney;
    }

    public List<UserBetsSettleBill> getBills() {
        return bills;
    }

    public void setBills(List<UserBetsSettleBill> bills) {
        this.bills = bills;
    }

    public List<UserBetsSettleUpPoint> getUpPoints() {
        return upPoints;
    }

    public void setUpPoints(List<UserBetsSettleUpPoint> upPoints) {
        this.upPoints = upPoints;
    }
}
