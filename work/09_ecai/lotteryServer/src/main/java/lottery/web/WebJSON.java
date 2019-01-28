package lottery.web;

import java.util.HashMap;
import java.util.Map;

import lottery.domains.pool.DataFactory;

public class WebJSON {

	private int error;
	private String code;
	private String message;
	private Map<String, Object> data = new HashMap<>();
	private DataFactory dataFactory;

	public WebJSON(DataFactory dataFactory) {
		this.dataFactory = dataFactory;
	}
	
	public int getError() {
		return error;
	}
	
	public void setError(int error) {
		this.error = error;
	}
	
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

	public void set(int error, String code, Object ... args) {
		this.error = error;
		this.code = code;
		String message = dataFactory.getSysMessage(code);
		if(args != null && args.length > 0) {
			this.message = String.format(message, args);
		} else {
			this.message = message;
		}
	}
	
	public void data(String key, Object value) {
		this.data.put(key, value);
	}
	
	public Map<String, Object> toJson() {
		data.put("error", error);
		data.put("code", code);
		data.put("message", message);
		return data;
	}

}