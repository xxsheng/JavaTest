package lottery.domains.content.vo.config;

import java.util.List;

public class DoMainConfig {
	
	private List<String> urls;
	private String mobileRegisterUrl; // 手机注册跳转地址
	
	public DoMainConfig() {
		
	}

	public List<String> getUrls() {
		return urls;
	}

	public void setUrls(List<String> urls) {
		this.urls = urls;
	}

	public String getMobileRegisterUrl() {
		return mobileRegisterUrl;
	}

	public void setMobileRegisterUrl(String mobileRegisterUrl) {
		this.mobileRegisterUrl = mobileRegisterUrl;
	}
}