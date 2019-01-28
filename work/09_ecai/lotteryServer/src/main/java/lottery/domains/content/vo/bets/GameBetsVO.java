package lottery.domains.content.vo.bets;

import lottery.domains.content.entity.GameBets;
import lottery.domains.content.entity.SysPlatform;
import lottery.domains.content.vo.user.UserVO;
import lottery.domains.pool.DataFactory;

/**
 * Created by Nick on 2016/12/28.
 */
public class GameBetsVO {
    private String username;
    private String platform;
    private String gameType;
    private String gameName;
    private int status; // 状态，-1：未知；1：完成；2：等待中；3：进行中；4：赢；5：输；6：平局；7：拒绝；8：退钱；9：取消；10：上半场赢；11：上半场输；12：和；
    private String time; // 下注时间
    private String prizeTime; // 派奖时间
    private double money;
    private double prizeMoney;

    public GameBetsVO(GameBets gameBets, DataFactory dataFactory) {
        SysPlatform sysPlatform = dataFactory.getSysPlatform(gameBets.getPlatformId());
        if (sysPlatform != null) {
            this.platform = sysPlatform.getName();
        }

        UserVO user = dataFactory.getUser(gameBets.getUserId());
        if (user != null) {
            this.username = user.getUsername();
        }

        this.gameType = gameBets.getGameType();
        this.gameName = gameBets.getGameName();
        this.status = gameBets.getStatus();
        this.time = gameBets.getTime();
        // 状态，-1：未知；1：完成；2：等待中；3：进行中；4：赢；5：输；6：平局；7：拒绝；8：退钱；9：取消；10：上半场赢；11：上半场输；12：和；
        if (this.status != 2 && this.status != 3) {
            this.prizeTime = gameBets.getPrizeTime();
        }
        this.money = gameBets.getMoney();
        this.prizeMoney = gameBets.getPrizeMoney();
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPlatform() {
        return platform;
    }

    public void setPlatform(String platform) {
        this.platform = platform;
    }

    public String getGameType() {
        return gameType;
    }

    public void setGameType(String gameType) {
        this.gameType = gameType;
    }

    public String getGameName() {
        return gameName;
    }

    public void setGameName(String gameName) {
        this.gameName = gameName;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getPrizeTime() {
        return prizeTime;
    }

    public void setPrizeTime(String prizeTime) {
        this.prizeTime = prizeTime;
    }

    public double getMoney() {
        return money;
    }

    public void setMoney(double money) {
        this.money = money;
    }

    public double getPrizeMoney() {
        return prizeMoney;
    }

    public void setPrizeMoney(double prizeMoney) {
        this.prizeMoney = prizeMoney;
    }
}
