package admin.domains.content.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;

import static javax.persistence.GenerationType.IDENTITY;

import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import lottery.domains.content.global.Database;

/**
 * AdminUser entity. @author MyEclipse Persistence Tools
 */
@Entity
@Table(name = "admin_user", catalog = Database.name, uniqueConstraints = @UniqueConstraint(columnNames = "username"))
public class AdminUser implements java.io.Serializable {

	// Fields

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private int id;
	private String username;
	private String password;
	private String withdrawPwd;
	private int roleId;
	private String registTime;
	private String loginTime;
	private int status;
	private String ips;
	private int pwd_error;
	private String secretKey;
	private int isValidate;//默认0 已绑定1
	

	// Constructors

	/** default constructor */
	public AdminUser() {
	}

	/** minimal constructor */
	public AdminUser(String username, String password, String withdrawPwd,
			int roleId, String registTime) {
		this.username = username;
		this.password = password;
		this.withdrawPwd = withdrawPwd;
		this.roleId = roleId;
		this.registTime = registTime;
	}

	/** full constructor */
	public AdminUser(String username, String password, String withdrawPwd,
			int roleId, String registTime, String loginTime, int status, String ips, int pwd_error) {
		this.username = username;
		this.password = password;
		this.withdrawPwd = withdrawPwd;
		this.roleId = roleId;
		this.registTime = registTime;
		this.loginTime = loginTime;
		this.status = status;
		this.ips = ips;
		this.pwd_error = pwd_error;
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

	@Column(name = "username", unique = true, nullable = false, length = 128)
	public String getUsername() {
		return this.username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	@Column(name = "password", nullable = false, length = 128)
	public String getPassword() {
		return this.password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	@Column(name = "withdraw_pwd", nullable = false, length = 128)
	public String getWithdrawPwd() {
		return withdrawPwd;
	}

	public void setWithdrawPwd(String withdrawPwd) {
		this.withdrawPwd = withdrawPwd;
	}

	@Column(name = "role_id", nullable = false)
	public int getRoleId() {
		return this.roleId;
	}

	public void setRoleId(int roleId) {
		this.roleId = roleId;
	}

	@Column(name = "regist_time", nullable = false, length = 19)
	public String getRegistTime() {
		return this.registTime;
	}

	public void setRegistTime(String registTime) {
		this.registTime = registTime;
	}

	@Column(name = "login_time", length = 19)
	public String getLoginTime() {
		return this.loginTime;
	}

	public void setLoginTime(String loginTime) {
		this.loginTime = loginTime;
	}

	@Column(name = "status")
	public int getStatus() {
		return this.status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	@Column(name = "ips", length=255)
	public String getIps() {
		return ips;
	}

	public void setIps(String ips) {
		this.ips = ips;
	}

	@Column(name = "pwd_error")
	public int getPwd_error() {
		return pwd_error;
	}

	public void setPwd_error(int pwd_error) {
		this.pwd_error = pwd_error;
	}

	@Column(name = "secret_key")
	public String getSecretKey() {
		return secretKey;
	}

	public void setSecretKey(String secretKey) {
		this.secretKey = secretKey;
	}

	@Column(name = "is_validate")
	public int getIsValidate() {
		return isValidate;
	}

	public void setIsValidate(int isValidate) {
		this.isValidate = isValidate;
	}
}