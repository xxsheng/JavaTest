package lottery.domains.content.payment.htf;

/**
 * Created by Nick on 2017/12/30.
 */
public class HTFPayQueryResult {
    private String retCode;
    private String retMsg;
    private String agentId;
    private String hyBillNo;
    private String batchNo;
    private String batchAmt;
    private String batchNum;
    private String detailData;
    private String extParam1;
    private String sign;

    public String getRetCode() {
        return retCode;
    }

    public void setRetCode(String retCode) {
        this.retCode = retCode;
    }

    public String getRetMsg() {
        return retMsg;
    }

    public void setRetMsg(String retMsg) {
        this.retMsg = retMsg;
    }

    public String getAgentId() {
        return agentId;
    }

    public void setAgentId(String agentId) {
        this.agentId = agentId;
    }

    public String getHyBillNo() {
        return hyBillNo;
    }

    public void setHyBillNo(String hyBillNo) {
        this.hyBillNo = hyBillNo;
    }

    public String getBatchNo() {
        return batchNo;
    }

    public void setBatchNo(String batchNo) {
        this.batchNo = batchNo;
    }

    public String getBatchAmt() {
        return batchAmt;
    }

    public void setBatchAmt(String batchAmt) {
        this.batchAmt = batchAmt;
    }

    public String getBatchNum() {
        return batchNum;
    }

    public void setBatchNum(String batchNum) {
        this.batchNum = batchNum;
    }

    public String getDetailData() {
        return detailData;
    }

    public void setDetailData(String detailData) {
        this.detailData = detailData;
    }

    public String getExtParam1() {
        return extParam1;
    }

    public void setExtParam1(String extParam1) {
        this.extParam1 = extParam1;
    }

    public String getSign() {
        return sign;
    }

    public void setSign(String sign) {
        this.sign = sign;
    }
}
