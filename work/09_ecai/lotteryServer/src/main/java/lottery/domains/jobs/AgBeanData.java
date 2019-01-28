package lottery.domains.jobs;

public class AgBeanData implements java.io.Serializable{

    private static final long serialVersionUID = 160056186994417905L;

    private String billNo;
    private String playName;
    private String gameCode;
    private String netAmount;
    private String betTime;
    private String betAmount;
    private String validBetAmount;
    private String flag;
    private String playType;
    private String tableCode;
    private String recalcuTime;
    private String beforeCredit;
    private String remark;
    private String round;
    private String gameType;
    private String deviceType;

    public String getBillNo() {
        return billNo;
    }

    public void setBillNo(String billNo) {
        this.billNo = billNo;
    }

    public String getPlayName() {
        return playName;
    }

    public void setPlayName(String playName) {
        this.playName = playName;
    }

    public String getGameCode() {
        return gameCode;
    }

    public void setGameCode(String gameCode) {
        this.gameCode = gameCode;
    }

    public String getNetAmount() {
        return netAmount;
    }

    public void setNetAmount(String netAmount) {
        this.netAmount = netAmount;
    }

    public String getBetTime() {
        return betTime;
    }

    public void setBetTime(String betTime) {
        this.betTime = betTime;
    }

    public String getBetAmount() {
        return betAmount;
    }

    public void setBetAmount(String betAmount) {
        this.betAmount = betAmount;
    }

    public String getValidBetAmount() {
        return validBetAmount;
    }

    public void setValidBetAmount(String validBetAmount) {
        this.validBetAmount = validBetAmount;
    }

    public String getFlag() {
        return flag;
    }

    public void setFlag(String flag) {
        this.flag = flag;
    }

    public String getPlayType() {
        return playType;
    }

    public void setPlayType(String playType) {
        this.playType = playType;
    }

    public String getTableCode() {
        return tableCode;
    }

    public void setTableCode(String tableCode) {
        this.tableCode = tableCode;
    }

    public String getRecalcuTime() {
        return recalcuTime;
    }

    public void setRecalcuTime(String recalcuTime) {
        this.recalcuTime = recalcuTime;
    }

    public String getBeforeCredit() {
        return beforeCredit;
    }

    public void setBeforeCredit(String beforeCredit) {
        this.beforeCredit = beforeCredit;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getRound() {
        return round;
    }

    public void setRound(String round) {
        this.round = round;
    }

    public String getGameType() {
        return gameType;
    }

    public void setGameType(String gameType) {
        this.gameType = gameType;
    }

    public String getDeviceType() {
        return deviceType;
    }

    public void setDeviceType(String deviceType) {
        this.deviceType = deviceType;
    }
}
