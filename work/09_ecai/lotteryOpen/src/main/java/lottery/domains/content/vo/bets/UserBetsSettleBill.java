package lottery.domains.content.vo.bets;

import lottery.domains.content.entity.User;

/**
 * 结算注单中产生的bill
 * Created by Nick on 2016/8/29.
 */
public class UserBetsSettleBill {
    private User user; // 给哪个用户添加账单
    private Integer account; // 账户ID，对应Global.BILL_ACCOUNT_*
    private Double amount; // 金额
    private Integer refType; // 账单类型，对应Global.BILL_TYPE_*
    private Integer refId; // 引用ID
    private String remarks; // 备注

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Integer getAccount() {
        return account;
    }

    public void setAccount(Integer account) {
        this.account = account;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public Integer getRefType() {
        return refType;
    }

    public void setRefType(Integer refType) {
        this.refType = refType;
    }

    public Integer getRefId() {
        return refId;
    }

    public void setRefId(Integer refId) {
        this.refId = refId;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }
}
