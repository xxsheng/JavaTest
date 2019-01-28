package lottery.domains.content.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;

import static javax.persistence.GenerationType.IDENTITY;

import javax.persistence.Id;
import javax.persistence.Table;

import lottery.domains.content.global.Database;

/**
 * AdminUserLog entity. @author MyEclipse Persistence Tools
 */
@Entity
@Table(name = "user_withdraw_log", catalog = Database.name)
public class UserWithdrawLog implements java.io.Serializable {

	// Fields

	/**
	 * 
	 */
	private static final long serialVersionUID = -7560535410474004055L;
	private int id;
	private int userId;
	private int adminUserId;
	private String billno;
	private String action;
	private String time;

	// Constructors

	/** default constructor */
	public UserWithdrawLog() {
	}

	/** minimal constructor */
	public UserWithdrawLog(String billno,int userId,int adminUserId, String action, String time) {
		this.userId = userId;
		this.action = action;
		this.time = time;
		this.billno=billno;
		this.adminUserId=adminUserId;
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

	@Column(name = "action", nullable = false, length = 65535)
	public String getAction() {
		return this.action;
	}

	public void setAction(String action) {
		this.action = action;
	}

	@Column(name = "time", nullable = false, length = 20)
	public String getTime() {
		return this.time;
	}

	public void setTime(String time) {
		this.time = time;
	}
	
	@Column(name = "admin_user_id", nullable = false)
	public int getAdminUserId() {
		return adminUserId;
	}

	public void setAdminUserId(int adminUserId) {
		this.adminUserId = adminUserId;
	}
	@Column(name = "billno", nullable = false)
	public String getBillno() {
		return billno;
	}

	public void setBillno(String billno) {
		this.billno = billno;
	}

}