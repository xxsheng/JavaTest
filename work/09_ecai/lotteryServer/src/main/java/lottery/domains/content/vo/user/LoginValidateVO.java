package lottery.domains.content.vo.user;

import org.apache.commons.lang.StringUtils;

/**
 * Created by Nick on 2017/1/8.
 */
public class LoginValidateVO {
    private String cardId;
    private String thisAddress;
    private String lastAddress;

    public LoginValidateVO(String cardId, String thisAddress, String lastAddress) {
        if (StringUtils.isNotEmpty(cardId)) {
            this.cardId = "**** " + cardId.substring(cardId.length()-4);
        }
        this.thisAddress = thisAddress;
        this.lastAddress = lastAddress;
    }

    public String getCardId() {
        return cardId;
    }

    public void setCardId(String cardId) {
        if (StringUtils.isNotEmpty(cardId)) {
            this.cardId = "**** " + cardId.substring(cardId.length()-4);
        }
        else {
            this.cardId = cardId;
        }
    }

    public String getThisAddress() {
        return thisAddress;
    }

    public void setThisAddress(String thisAddress) {
        this.thisAddress = thisAddress;
    }

    public String getLastAddress() {
        return lastAddress;
    }

    public void setLastAddress(String lastAddress) {
        this.lastAddress = lastAddress;
    }
}
