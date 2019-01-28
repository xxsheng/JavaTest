package lottery.domains.content.entity;

import javautils.date.Moment;
import javautils.math.MathUtil;
import lottery.domains.content.api.ag.AGAPI;
import lottery.domains.content.api.ag.AGBetRecord;
import lottery.domains.content.api.pt.PTPlayerGameResult;
import lottery.domains.content.api.sb.Win88SBAPI;
import lottery.domains.content.api.sb.Win88SBSportBetLogResult;
import lottery.domains.content.global.Database;
import lottery.domains.content.global.Global;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.builder.EqualsBuilder;

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

    public GameBets(PTPlayerGameResult result, UserGameAccount account) {
        this.setUserId(account.getUserId());
        this.setPlatformId(account.getPlatformId());
        this.setBetsId(result.getGAMEID());
        this.setGameCode(result.getGAMECODE());
        this.setGameType(result.getGAMETYPE());
        this.setGameName(result.getGAMENAME());
        this.setMoney(Double.valueOf(result.getBET()));
        this.setProgressiveMoney(Double.valueOf(result.getPROGRESSIVEBET()));
        if (this.getMoney() > 0 && this.getProgressiveMoney() > 0) {
            this.setProgressiveMoney(0);
        }
        this.setPrizeMoney(Double.valueOf(result.getWIN()));
        this.setProgressivePrize(Double.valueOf(result.getPROGRESSIVEWIN()));
        this.setBalance(Double.valueOf(result.getBALANCE()));
        this.setTime(result.getGAMEDATE());
        this.setPrizeTime(result.getGAMEDATE());
        this.setStatus(1);
    }

    public GameBets(AGBetRecord record, UserGameAccount account) {
        this.setUserId(account.getUserId());
        this.setPlatformId(account.getPlatformId());
        this.setBetsId(record.getBillNo());
        this.setGameCode(record.getGameCode());
        this.setGameType(AGAPI.transRound(record.getRound()));
        this.setGameName(AGAPI.transGameType(record.getGameType()));
        this.setMoney(Double.valueOf(record.getBetAmount()));

        record.getGameType();

        if ("BR".equals(record.getDataType()) || "EBR".equals(record.getDataType())) {
            Double netAmount = Double.valueOf(record.getNetAmount());
            double prizeMoney = MathUtil.add(this.getMoney(), netAmount);
            this.setPrizeMoney(prizeMoney);
        }
        else {
            double prizeMoney = Double.valueOf(record.getNetAmount());
            this.setPrizeMoney(prizeMoney);
        }

        this.setProgressiveMoney(0);
        this.setProgressivePrize(0);
        double beforeCredit = StringUtils.isEmpty(record.getBeforeCredit()) ? 0 : Double.valueOf(record.getBeforeCredit());
        double balance = beforeCredit - this.getMoney() + prizeMoney;
        if (balance < 0) {
            balance = 0;
        }
        this.setBalance(balance);
        this.setStatus(1);
        this.setTime(record.getBetTime());
        this.setPrizeTime(record.getBetTime());

        if (this.getPrizeMoney() < 0) {
            this.setPrizeMoney(0);
        }
        if (this.getMoney() < 0) {
            this.setMoney(0);
        }
    }

    public GameBets(Win88SBSportBetLogResult.Data record, UserGameAccount account) {
        this.setUserId(account.getUserId());
        this.setPlatformId(Global.BILL_ACCOUNT_SB);
        this.setBetsId(record.getTransId());
        this.setGameCode(record.getMatchId()); // 游戏比赛 ID
        if ("29".equals(record.getBetType())) {
            this.setGameType("串关"); // 体育类型
            this.setGameName("混合赛事"); // 联赛名称
        }
        else {
            this.setGameType(Win88SBAPI.transSportType(record.getSportType())); // 体育类型
            this.setGameName(StringUtils.isEmpty(record.getLeagueName()) ? "未知" : record.getLeagueName()); // 联赛名称
        }
        this.setMoney(Double.valueOf(record.getStake()));

        Double winLoseAmount = Double.valueOf(record.getWinLoseAmount());
        double prizeMoney = MathUtil.add(this.getMoney(), winLoseAmount);
        this.setPrizeMoney(prizeMoney);

        this.setProgressiveMoney(0);
        this.setProgressivePrize(0);
        this.setBalance(0);
        this.setStatus(Win88SBAPI.transTicketStatus(record.getTicketStatus()));

        String transactionTime = record.getTransactionTime();
        transactionTime = transactionTime.replaceAll("T", " ");
        if (transactionTime.lastIndexOf(".") > -1) {
            transactionTime = transactionTime.substring(0, transactionTime.lastIndexOf("."));
        }

        transactionTime = new Moment().fromTime(transactionTime).add(12, "hours").toSimpleTime(); // GMT-4，要加12个小时
        this.setTime(transactionTime);

        if (StringUtils.isNotEmpty(record.getWinLostDateTime())) {
            String winLostDateTime = record.getWinLostDateTime();

            winLostDateTime = winLostDateTime.replaceAll("T", " ");
            if (winLostDateTime.lastIndexOf(".") > -1) {
                winLostDateTime = winLostDateTime.substring(0, winLostDateTime.lastIndexOf("."));
            }
            winLostDateTime = new Moment().fromTime(winLostDateTime).add(12, "hours").toSimpleTime(); // GMT-4，要加12个小时

            this.setPrizeTime(winLostDateTime); // 派奖时间
        }

        this.setExt1(record.getVersionKey()); // ex1字段作为该记录的版本号
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

    @Override
    public boolean equals(Object obj) {
        if (obj == null) return false;

        if (!(obj instanceof GameBets)) return false;

        GameBets other = (GameBets) obj;

        EqualsBuilder builder = new EqualsBuilder();
        return builder
                .append(this.getUserId(), other.getUserId())
                .append(this.getPlatformId(), other.getPlatformId())
                .append(this.getBetsId(), other.getBetsId())
                .append(this.getGameCode(), other.getGameCode())
                .append(this.getGameType(), other.getGameType())
                .append(this.getGameName(), other.getGameName())
                .append(this.getMoney(), other.getMoney())
                .append(this.getPrizeMoney(), other.getPrizeMoney())
                .append(this.getProgressiveMoney(), other.getProgressiveMoney())
                .append(this.getProgressivePrize(), other.getProgressivePrize())
                .append(this.getBalance(), other.getBalance())
                .append(this.getStatus(), other.getStatus())
                .append(this.getTime(), other.getTime())
                .append(this.getPrizeTime(), other.getPrizeTime())
                .append(this.getExt1(), other.getExt1())
                .append(this.getExt2(), other.getExt2())
                .append(this.getExt3(), other.getExt3())
                .isEquals();
    }
}
