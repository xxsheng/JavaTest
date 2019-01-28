package lottery.domains.content.api.sb;

import com.alibaba.fastjson.annotation.JSONField;

/**
 * Created by Nick on 2017-05-27.
 */
public abstract class Win88SBResult {
    @JSONField(name = "error_code")
    private String errorCode;
    @JSONField(name = "message")
    private String message;

    public String getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
