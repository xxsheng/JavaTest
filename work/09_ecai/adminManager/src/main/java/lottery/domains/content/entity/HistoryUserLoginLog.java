package lottery.domains.content.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;

import static javax.persistence.GenerationType.IDENTITY;

import javax.persistence.Id;
import javax.persistence.Table;

import lottery.domains.content.global.Database;

/**
 * UserLoginLog entity. @author MyEclipse Persistence Tools
 */
@Entity
@Table(name = "user_login_log", catalog = Database.backup)
public class HistoryUserLoginLog implements java.io.Serializable {

	// Fields

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private int id;
	private int userId;
	private String ip;
	private String address;
	private String userAgent;
	private String time;
	private String loginLine;

	// Constructors

	/** default constructor */
	public HistoryUserLoginLog() {
	}

	/** minimal constructor */
	public HistoryUserLoginLog(int userId, String ip, String time,String loginLine) {
		this.userId = userId;
		this.ip = ip;
		this.time = time;
		this.loginLine = loginLine;
	}

	/** full constructor */
	public HistoryUserLoginLog(int userId, String ip, String address,
			String userAgent, String time,String loginLine) {
		this.userId = userId;
		this.ip = ip;
		this.address = address;
		this.userAgent = userAgent;
		this.time = time;
		this.loginLine = loginLine;
	}

	// Property accessors
	@Id
	@GeneratedValue(strategy = IDENTITY)
	@Column(name = "id", unique = true, nullable = false)
	public int getId() {
		return this.id;
	}

	public void setId(int id) {
		this.id = id;
	}

	@Column(name = "user_id", nullable = false)
	public int getUserId() {
		return this.userId;
	}

	public void setUserId(int userId) {
		this.userId = userId;
	}

	@Column(name = "ip", nullable = false, length = 128)
	public String getIp() {
		return this.ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	@Column(name = "address")
	public String getAddress() {
		return this.address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	@Column(name = "user_agent")
	public String getUserAgent() {
		return this.userAgent;
	}

	public void setUserAgent(String userAgent) {
		this.userAgent = userAgent;
	}

	@Column(name = "time", nullable = false, length = 19)
	public String getTime() {
		return this.time;
	}

	public void setTime(String time) {
		this.time = time;
	}
	@Column(name = "login_line")
	public String getLoginLine() {
		return loginLine;
	}

	public void setLoginLine(String loginLine) {
		this.loginLine = loginLine;
	}
}