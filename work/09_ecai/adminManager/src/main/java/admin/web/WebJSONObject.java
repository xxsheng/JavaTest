package admin.web;

import admin.domains.pool.AdminDataFactory;
import net.sf.json.JSONObject;

public class WebJSONObject {
	
	private AdminDataFactory df;
	
	private String message;
	private Integer error;
	private String code;
	private JSONObject json = new JSONObject();
	
	public WebJSONObject(AdminDataFactory df) {
		this.df = df;
	}
	
	public void set(Integer error, String code) {
		this.message = df.getSysMessage(code);
		this.error = error;
		this.code = code;
	}

	public void setWithParams(Integer error, String code, Object ... args) {
		this.message = df.getSysMessage(code);
		this.error = error;
		this.code = code;
		if(args != null && args.length > 0) {
			this.message = String.format(message, args);
		}
	}

	public String toString() {
		json.put("error", error);
		json.put("message", message);
		json.put("code", code);
		return json.toString();
	}

	public Integer getError() {
		return error;
	}

	public void setError(Integer error) {
		this.error = error;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}
	
	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public JSONObject accumulate(String key, Object value) {
		return json.accumulate(key, value);
	}

}
