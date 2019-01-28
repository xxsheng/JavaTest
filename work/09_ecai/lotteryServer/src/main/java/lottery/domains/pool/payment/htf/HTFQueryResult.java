package lottery.domains.pool.payment.htf;

/**
 * Created by Nick on 2017/12/30.
 */
public class HTFQueryResult {
    private String P_UserId;
    private String P_OrderId;
    private String P_ChannelId;
    private String P_CardId;
    private String P_payMoney;
    private String P_flag;
    private String P_status;
    private String P_ErrMsg;
    private String P_PostKey;

    public String getP_UserId() {
        return P_UserId;
    }

    public void setP_UserId(String p_UserId) {
        P_UserId = p_UserId;
    }

    public String getP_OrderId() {
        return P_OrderId;
    }

    public void setP_OrderId(String p_OrderId) {
        P_OrderId = p_OrderId;
    }

    public String getP_ChannelId() {
        return P_ChannelId;
    }

    public void setP_ChannelId(String p_ChannelId) {
        P_ChannelId = p_ChannelId;
    }

    public String getP_CardId() {
        return P_CardId;
    }

    public void setP_CardId(String p_CardId) {
        P_CardId = p_CardId;
    }

    public String getP_payMoney() {
        return P_payMoney;
    }

    public void setP_payMoney(String p_payMoney) {
        P_payMoney = p_payMoney;
    }

    public String getP_flag() {
        return P_flag;
    }

    public void setP_flag(String p_flag) {
        P_flag = p_flag;
    }

    public String getP_status() {
        return P_status;
    }

    public void setP_status(String p_status) {
        P_status = p_status;
    }

    public String getP_ErrMsg() {
        return P_ErrMsg;
    }

    public void setP_ErrMsg(String p_ErrMsg) {
        P_ErrMsg = p_ErrMsg;
    }

    public String getP_PostKey() {
        return P_PostKey;
    }

    public void setP_PostKey(String p_PostKey) {
        P_PostKey = p_PostKey;
    }
}
