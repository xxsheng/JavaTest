package lottery.domains.content.entity;

import lottery.domains.content.global.Database;

import javax.persistence.*;
import java.io.Serializable;

import static javax.persistence.GenerationType.IDENTITY;

/**
 * Created by Nick on 2016/12/24.
 */
@Entity
@Table(name = "game_bets", catalog = Database.name, uniqueConstraints = @UniqueConstraint(columnNames = {
        "user_id", "platform_id", "bets_id", "game_code"}))
public class GameBets implements Serializable{
    private int id; // ID
    private int userId; // 用户ID
    private int platformId; // 平台ID
    private String betsId; // 平台方的注单ID
    private String gameCode; // 游戏编码，与本系统不同，是第三方自己的编码
    private String gameType; // 游戏类型
    private String gameName; // 游戏名
    private double money; // 下注金额
    private double prizeMoney; // 奖金金额
    private double progressiveMoney; // 奖池下注金额
    private double progressivePrize; // 奖池中奖金额
    private double balance; // 余额
    private int status; // 状态，-1：未知；1：完成；2：等待中；3：进行中；4：赢；5：输；6：平局；7：拒绝；8：退钱；9：取消；10：上半场赢；11：上半场输；12：和；
    private String time; // 下注时间
    private String prizeTime; // 派奖时间
    private String ext1; // 额外字段1
    private String ext2; // 额外字段2
    private String ext3; // 额外字段3

    public GameBets(){
    }

    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Column(name = "id", unique = true, nullable = false)
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Column(name = "user_id", nullable = false)
    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    @Column(name = "platform_id", nullable = false)
    public int getPlatformId() {
        return platformId;
    }

    public void setPlatformId(int platformId) {
        this.platformId = platformId;
    }

    @Column(name = "bets_id", nullable = false, length = 128)
    public String getBetsId() {
        return betsId;
    }

    public void setBetsId(String betsId) {
        this.betsId = betsId;
    }

    @Column(name = "game_code", nullable = false, length = 128)
    public String getGameCode() {
        return gameCode;
    }

    public void setGameCode(String gameCode) {
        this.gameCode = gameCode;
    }

    @Column(name = "game_type", nullable = false, length = 128)
    public String getGameType() {
        return gameType;
    }

    public void setGameType(String gameType) {
        this.gameType = gameType;
    }

    @Column(name = "game_name", nullable = false, length = 128)
    public String getGameName() {
        return gameName;
    }

    public void setGameName(String gameName) {
        this.gameName = gameName;
    }

    @Column(name = "money", nullable = false, precision = 12, scale = 4)
    public double getMoney() {
        return money;
    }

    public void setMoney(double money) {
        this.money = money;
    }

    @Column(name = "prize_money", nullable = false, precision = 12, scale = 4)
    public double getPrizeMoney() {
        return prizeMoney;
    }

    public void setPrizeMoney(double prizeMoney) {
        this.prizeMoney = prizeMoney;
    }

    @Column(name = "progressive_money", precision = 12, scale = 4)
    public double getProgressiveMoney() {
        return progressiveMoney;
    }

    public void setProgressiveMoney(double progressiveMoney) {
        this.progressiveMoney = progressiveMoney;
    }

    @Column(name = "progressive_prize", precision = 12, scale = 4)
    public double getProgressivePrize() {
        return progressivePrize;
    }

    public void setProgressivePrize(double progressivePrize) {
        this.progressivePrize = progressivePrize;
    }

    @Column(name = "balance", precision = 12, scale = 4)
    public double getBalance() {
        return balance;
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }

    @Column(name = "status")
    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    @Column(name = "time", nullable = false, length = 50)
    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    @Column(name = "prize_time", length = 50)
    public String getPrizeTime() {
        return prizeTime;
    }

    public void setPrizeTime(String prizeTime) {
        this.prizeTime = prizeTime;
    }

    @Column(name = "ext1", length = 128)
    public String getExt1() {
        return ext1;
    }

    public void setExt1(String ext1) {
        this.ext1 = ext1;
    }

    @Column(name = "ext2", length = 128)
    public String getExt2() {
        return ext2;
    }

    public void setExt2(String ext2) {
        this.ext2 = ext2;
    }

    @Column(name = "ext3", length = 128)
    public String getExt3() {
        return ext3;
    }

    public void setExt3(String ext3) {
        this.ext3 = ext3;
    }
}
