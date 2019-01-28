package lottery.web.helper.session;

import java.io.Serializable;

public class SessionUser implements Serializable {

	private static final long serialVersionUID = -3016232654249984072L;
	private int id;
	private String username;
	private String nickname;
	private int type;
	private int upid;
	private String upids;
	private String registTime;
	private String sessionId;

	public SessionUser(int id, String username, String nickname, int type, int upid, String upids, String registTime, String sessionId) {
		this.id = id;
		this.username = username;
		this.nickname = nickname;
		this.type = type;
		this.upid = upid;
		this.upids = upids;
		this.registTime = registTime;
		this.sessionId = sessionId;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getNickname() {
		return nickname;
	}

	public void setNickname(String nickname) {
		this.nickname = nickname;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public int getUpid() {
		return upid;
	}

	public void setUpid(int upid) {
		this.upid = upid;
	}

	public String getUpids() {
		return upids;
	}

	public void setUpids(String upids) {
		this.upids = upids;
	}

	public String getRegistTime() {
		return registTime;
	}

	public void setRegistTime(String registTime) {
		this.registTime = registTime;
	}

	public String getSessionId() {
		return sessionId;
	}

	public void setSessionId(String sessionId) {
		this.sessionId = sessionId;
	}
}