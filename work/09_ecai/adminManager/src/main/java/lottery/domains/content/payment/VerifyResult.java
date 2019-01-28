package lottery.domains.content.payment;

import java.util.Date;

/**
 * Created by Nick on 2017/12/30.
 */
public class VerifyResult {
    private String selfBillno; // 内部订单号
    private String channelBillno; // 外部订单号
    private Date payTime; // 支付时间
    private boolean success; // 是否成功处理
    private String output; // 输出
    private String successOutput; // 成功输出
    private String failedOutput; // 失败输出
    private Integer remitStatus; // 打款状态

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

    public Integer getRemitStatus() {
        return remitStatus;
    }

    public void setRemitStatus(Integer remitStatus) {
        this.remitStatus = remitStatus;
    }
}
