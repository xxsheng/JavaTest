package lottery.domains.content.entity;

/**
 * Created by Nick on 2017-04-09.
 */
public class My18UserBankTransfer {
    private int id; // ID
    private String billno; // 账单号,系统生成,不用修改
    private String realName; // 打款人名,需要修改
    private int postscript; // 附言,系统生成,不用修改
    private int money; // 金额,系统生成,不用修改
    private String time; // 提交时间,系统生成,不用修改
    private int status; // 1:未支付;2:成功
    private String payBillno; // 支付流水号,需要修改
    private String payTime; // 支付时间,可修改可不修改
    private String secret; // 加密验证码

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getBillno() {
        return billno;
    }

    public void setBillno(String billno) {
        this.billno = billno;
    }

    public String getRealName() {
        return realName;
    }

    public void setRealName(String realName) {
        this.realName = realName;
    }

    public int getPostscript() {
        return postscript;
    }

    public void setPostscript(int postscript) {
        this.postscript = postscript;
    }

    public int getMoney() {
        return money;
    }

    public void setMoney(int money) {
        this.money = money;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getPayBillno() {
        return payBillno;
    }

    public void setPayBillno(String payBillno) {
        this.payBillno = payBillno;
    }

    public String getPayTime() {
        return payTime;
    }

    public void setPayTime(String payTime) {
        this.payTime = payTime;
    }

    public String getSecret() {
        return secret;
    }

    public void setSecret(String secret) {
        this.secret = secret;
    }
}
