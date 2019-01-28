package lottery.domains.pool.payment.ay;

import java.util.Map;

public class AYScanResult {
    private String code; // 响应码 100001-成功,其他失败
    private String message; // 响应消息
    private String status;
    private String success;
    private Map<String ,String> info ;
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getSuccess() {
		return success;
	}
	public void setSuccess(String success) {
		this.success = success;
	}
	public Map<String, String> getInfo() {
		return info;
	}
	public void setInfo(Map<String, String> info) {
		this.info = info;
	}

    
    
    

 
}
