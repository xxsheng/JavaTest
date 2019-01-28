package lottery.domains.pool.payment;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Nick on 2017/12/6.
 */
public class PrepareResult {
    public static final int JUMP_TYPE_FORM = 1;
    public static final int JUMP_TYPE_QR = 2;
    public static final int QUICK_TYPE_FORM=3; 

    private int jumpType; // 1：form跳转；2：直接展示二维码
    private boolean success; // 是否成功
    private String errorMsg; // 错误消息

    /********表单跳转***********/
    private String formUrl; // 跳转地址
    private String formMethod = "post";
    private Map<String, String> formParams = new HashMap<>(); // 表单参数
    
    
    /********表单跳转***********/

    /********二维码***********/
    private String qrCode;
    /********二维码***********/


    public int getJumpType() {
        return jumpType;
    }

    public void setJumpType(int jumpType) {
        this.jumpType = jumpType;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getErrorMsg() {
        return errorMsg;
    }

    public void setErrorMsg(String errorMsg) {
        this.errorMsg = errorMsg;
    }

    public String getFormUrl() {
        return formUrl;
    }

    public void setFormUrl(String formUrl) {
        this.formUrl = formUrl;
    }

    public String getFormMethod() {
        return formMethod;
    }

    public void setFormMethod(String formMethod) {
        this.formMethod = formMethod;
    }

    public Map<String, String> getFormParams() {
        return formParams;
    }

    public void setFormParams(Map<String, String> formParams) {
        this.formParams = formParams;
    }

    public String getQrCode() {
        return qrCode;
    }

    public void setQrCode(String qrCode) {
        this.qrCode = qrCode;
    }
}
