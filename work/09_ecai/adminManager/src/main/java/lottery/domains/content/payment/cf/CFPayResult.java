package lottery.domains.content.payment.cf;

/**
 * Created by Nick on 2017-09-04.
 */
public class CFPayResult {
    private String respCode;
    private String respMessage;

    public String getRespCode() {
        return respCode;
    }

    public void setRespCode(String respCode) {
        this.respCode = respCode;
    }

    public String getRespMessage() {
        return respMessage;
    }

    public void setRespMessage(String respMessage) {
        this.respMessage = respMessage;
    }
}
