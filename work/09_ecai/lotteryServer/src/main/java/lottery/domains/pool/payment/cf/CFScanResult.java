package lottery.domains.pool.payment.cf;

import java.util.Map;

/**
 * Created by Nick on 2017/11/23.
 */
public class CFScanResult {
    private String codeUrl; // 二维码链接
    private String respCode; // 错误代码
    private String respMessage; // 错误消息
    private String pay_QR;
    private String successno;
    private Map<String ,String> data ;
    public String getCodeUrl() {
        return codeUrl;
    }

    public void setCodeUrl(String codeUrl) {
        this.codeUrl = codeUrl;
    }

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

	public String getPay_QR() {
		return pay_QR;
	}

	public void setPay_QR(String pay_QR) {
		this.pay_QR = pay_QR;
	}

	public String getSuccessno() {
		return successno;
	}

	public void setSuccessno(String successno) {
		this.successno = successno;
	}

	public Map<String, String> getData() {
		return data;
	}

	public void setData(Map<String, String> data) {
		this.data = data;
	}


    
    
}
