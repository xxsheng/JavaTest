package lottery.domains.pool.payment;

import java.util.Date;

/**
 * Created by Nick on 2017/12/6.
 */
public class VerifyResult {
    private String selfBillno; // 内部订单号
    private String channelBillno; // 外部订单号
    private double requestMoney; // 充值前请求的金额
    private double receiveMoney; // 实际支付的金额
    private Date payTime; // 支付时间
    private boolean success; // 是否成功支付
    private String output; // 输出
    private String successOutput; // 成功输出
    private String failedOutput; // 失败输出

    public String getSelfBillno() {
        return selfBillno;
    }

    public void setSelfBillno(String selfBillno) {
        this.selfBillno = selfBillno;
    }

    public String getChannelBillno() {
        return channelBillno;
    }

    public void setChannelBillno(String channelBillno) {
        this.channelBillno = channelBillno;
    }

    public double getRequestMoney() {
        return requestMoney;
    }

    public void setRequestMoney(double requestMoney) {
        this.requestMoney = requestMoney;
    }

    public double getReceiveMoney() {
        return receiveMoney;
    }

    public void setReceiveMoney(double receiveMoney) {
        this.receiveMoney = receiveMoney;
    }

    public Date getPayTime() {
        return payTime;
    }

    public void setPayTime(Date payTime) {
        this.payTime = payTime;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getOutput() {
        return output;
    }

    public void setOutput(String output) {
        this.output = output;
    }

    public String getSuccessOutput() {
        return successOutput;
    }

    public void setSuccessOutput(String successOutput) {
        this.successOutput = successOutput;
    }

    public String getFailedOutput() {
        return failedOutput;
    }

    public void setFailedOutput(String failedOutput) {
        this.failedOutput = failedOutput;
    }
}
