package lottery.domains.content.api.sb;

import com.alibaba.fastjson.annotation.JSONField;

import java.util.List;

/**
 * 沙巴体育投注记录
 * Created by Nick on 2017-05-28.
 */
public class Win88SBSportBetLogResult extends Win88SBResult {
    @JSONField(name = "LastVersionKey")
    private String LastVersionKey;
    @JSONField(name = "TotalRecord")
    private int TotalRecord;
    @JSONField(name = "Data")
    private List<Data> Data;

    public String getLastVersionKey() {
        return LastVersionKey;
    }

    public void setLastVersionKey(String lastVersionKey) {
        LastVersionKey = lastVersionKey;
    }

    public int getTotalRecord() {
        return TotalRecord;
    }

    public void setTotalRecord(int totalRecord) {
        TotalRecord = totalRecord;
    }

    public List<Win88SBSportBetLogResult.Data> getData() {
        return Data;
    }

    public void setData(List<Win88SBSportBetLogResult.Data> data) {
        Data = data;
    }

    public static class Data {
        @JSONField(name = "TransId")
        private String TransId; // 交易记录 ID
        @JSONField(name = "PlayerName")
        private String PlayerName; // 用户名
        @JSONField(name = "TransactionTime")
        private String TransactionTime; // ISO 8601 格式
        @JSONField(name = "MatchId")
        private String MatchId; // 游戏比赛 ID
        @JSONField(name = "LeagueId")
        private String LeagueId; // 联赛 ID
        @JSONField(name = "LeagueName")
        private String LeagueName; // 联赛名称
        @JSONField(name = "SportType")
        private String SportType; // 请参阅Win88SBSportType
        @JSONField(name = "AwayId")
        private String AwayId; // 客队ID
        @JSONField(name = "AwayIDName")
        private String AwayIDName; // 客队名称
        @JSONField(name = "HomeId")
        private String HomeId; // 主队ID
        @JSONField(name = "HomeIDName")
        private String HomeIDName; // 主队名称
        @JSONField(name = "MatchDateTime")
        private String MatchDateTime; // ISO 8601 格式
        @JSONField(name = "BetType")
        private String BetType; // 请参阅打赌类型表
        @JSONField(name = "ParlayRefNo")
        private String ParlayRefNo; // Parlay 参考编号
        @JSONField(name = "BetTeam")
        private String BetTeam; // 投注站团队
        @JSONField(name = "HDP")
        private String HDP; // 障碍
        @JSONField(name = "AwayHDP")
        private String AwayHDP; // 客队的障碍
        @JSONField(name = "HomeHDP")
        private String HomeHDP; // 主队的障碍
        @JSONField(name = "Odds")
        private String Odds; // 赔率
        @JSONField(name = "AwayScore")
        private String AwayScore; // 客队的得分
        @JSONField(name = "HomeScore")
        private String HomeScore; // 主场球队得分
        @JSONField(name = "IsLive")
        private String IsLive; // 是活的状态
        @JSONField(name = "TicketStatus")
        private String TicketStatus; // 状态
        @JSONField(name = "Stake")
        private String Stake; // 打赌的股份
        @JSONField(name = "WinLoseAmount")
        private String WinLoseAmount; // 赢/亏金额
        @JSONField(name = "WinLostDateTime")
        private String WinLostDateTime; // ISO 8601 格式
        // @JSONField(name = "ParlayData")
        // private String ParlayData; // Parlay 数据的数组
        @JSONField(name = "VersionKey")
        private String VersionKey; // 版本
        @JSONField(name = "LastBallNo")
        private String LastBallNo; // 最后球码

        public String getTransId() {
            return TransId;
        }

        public void setTransId(String transId) {
            TransId = transId;
        }

        public String getPlayerName() {
            return PlayerName;
        }

        public void setPlayerName(String playerName) {
            PlayerName = playerName;
        }

        public String getTransactionTime() {
            return TransactionTime;
        }

        public void setTransactionTime(String transactionTime) {
            TransactionTime = transactionTime;
        }

        public String getMatchId() {
            return MatchId;
        }

        public void setMatchId(String matchId) {
            MatchId = matchId;
        }

        public String getLeagueId() {
            return LeagueId;
        }

        public void setLeagueId(String leagueId) {
            LeagueId = leagueId;
        }

        public String getLeagueName() {
            return LeagueName;
        }

        public void setLeagueName(String leagueName) {
            LeagueName = leagueName;
        }

        public String getSportType() {
            return SportType;
        }

        public void setSportType(String sportType) {
            SportType = sportType;
        }

        public String getAwayId() {
            return AwayId;
        }

        public void setAwayId(String awayId) {
            AwayId = awayId;
        }

        public String getAwayIDName() {
            return AwayIDName;
        }

        public void setAwayIDName(String awayIDName) {
            AwayIDName = awayIDName;
        }

        public String getHomeId() {
            return HomeId;
        }

        public void setHomeId(String homeId) {
            HomeId = homeId;
        }

        public String getHomeIDName() {
            return HomeIDName;
        }

        public void setHomeIDName(String homeIDName) {
            HomeIDName = homeIDName;
        }

        public String getMatchDateTime() {
            return MatchDateTime;
        }

        public void setMatchDateTime(String matchDateTime) {
            MatchDateTime = matchDateTime;
        }

        public String getBetType() {
            return BetType;
        }

        public void setBetType(String betType) {
            BetType = betType;
        }

        public String getParlayRefNo() {
            return ParlayRefNo;
        }

        public void setParlayRefNo(String parlayRefNo) {
            ParlayRefNo = parlayRefNo;
        }

        public String getBetTeam() {
            return BetTeam;
        }

        public void setBetTeam(String betTeam) {
            BetTeam = betTeam;
        }

        public String getHDP() {
            return HDP;
        }

        public void setHDP(String HDP) {
            this.HDP = HDP;
        }

        public String getAwayHDP() {
            return AwayHDP;
        }

        public void setAwayHDP(String awayHDP) {
            AwayHDP = awayHDP;
        }

        public String getHomeHDP() {
            return HomeHDP;
        }

        public void setHomeHDP(String homeHDP) {
            HomeHDP = homeHDP;
        }

        public String getOdds() {
            return Odds;
        }

        public void setOdds(String odds) {
            Odds = odds;
        }

        public String getAwayScore() {
            return AwayScore;
        }

        public void setAwayScore(String awayScore) {
            AwayScore = awayScore;
        }

        public String getHomeScore() {
            return HomeScore;
        }

        public void setHomeScore(String homeScore) {
            HomeScore = homeScore;
        }

        public String getIsLive() {
            return IsLive;
        }

        public void setIsLive(String isLive) {
            IsLive = isLive;
        }

        public String getTicketStatus() {
            return TicketStatus;
        }

        public void setTicketStatus(String ticketStatus) {
            TicketStatus = ticketStatus;
        }

        public String getStake() {
            return Stake;
        }

        public void setStake(String stake) {
            Stake = stake;
        }

        public String getWinLoseAmount() {
            return WinLoseAmount;
        }

        public void setWinLoseAmount(String winLoseAmount) {
            WinLoseAmount = winLoseAmount;
        }

        public String getWinLostDateTime() {
            return WinLostDateTime;
        }

        public void setWinLostDateTime(String winLostDateTime) {
            WinLostDateTime = winLostDateTime;
        }

        public String getVersionKey() {
            return VersionKey;
        }

        public void setVersionKey(String versionKey) {
            VersionKey = versionKey;
        }

        public String getLastBallNo() {
            return LastBallNo;
        }

        public void setLastBallNo(String lastBallNo) {
            LastBallNo = lastBallNo;
        }
    }
}
