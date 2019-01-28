package lottery.domains.content.vo.user;

import lottery.domains.content.entity.HistoryUserLoginLog;
import lottery.domains.pool.LotteryDataFactory;

public class HistoryUserLoginLogVO {

	private String username;
	private int id;
	private int userId;
	private String ip;
	private String address;
	private String userAgent;
	private String time;
	private String loginLine;

	public HistoryUserLoginLogVO(HistoryUserLoginLog bean, LotteryDataFactory df) {
		UserVO user = df.getUser(bean.getUserId());
		if (user != null) {
			this.username = user.getUsername();
		}
		this.id = bean.getId();
		this.userId = bean.getUserId();
		this.ip = bean.getIp();
		this.address = bean.getAddress();
		this.userAgent = bean.getUserAgent();
		this.time = bean.getTime();
		this.loginLine =bean.getLoginLine();
	}

	
	public String getLoginLine() {
		return loginLine;
	}


	public void setLoginLine(String loginLine) {
		this.loginLine = loginLine;
	}


	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getUserId() {
		return userId;
	}

	public void setUserId(int userId) {
		this.userId = userId;
	}

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getUserAgent() {
		return userAgent;
	}

	public void setUserAgent(String userAgent) {
		this.userAgent = userAgent;
	}

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}
}