package lottery.domains.content.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;

import static javax.persistence.GenerationType.IDENTITY;

import javax.persistence.Id;
import javax.persistence.Table;

import lottery.domains.content.global.Database;

/**
 * UserBlacklist entity. @author MyEclipse Persistence Tools
 */
@Entity
@Table(name = "user_blacklist", catalog = Database.name)
public class UserBlacklist implements java.io.Serializable {

	// Fields

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private int id;
	private String username;
	private String cardName;
	private String cardId;
	private Integer bankId;
	private String ip;
	private String operatorUser;
	private String operatorTime;
	private String remarks;

	// Constructors

	/** default constructor */
	public UserBlacklist() {
	}

	/** minimal constructor */
	public UserBlacklist(String cardName, String operatorUser,
			String operatorTime) {
		this.cardName = cardName;
		this.operatorUser = operatorUser;
		this.operatorTime = operatorTime;
	}

	/** full constructor */
	public UserBlacklist(String username, String cardName, String cardId,
			Integer bankId, String ip, String operatorUser,
			String operatorTime, String remarks) {
		this.username = username;
		this.cardName = cardName;
		this.cardId = cardId;
		this.bankId = bankId;
		this.ip = ip;
		this.operatorUser = operatorUser;
		this.operatorTime = operatorTime;
		this.remarks = remarks;
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

	@Column(name = "username", length = 20)
	public String getUsername() {
		return this.username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	@Column(name = "card_name", nullable = false, length = 64)
	public String getCardName() {
		return this.cardName;
	}

	public void setCardName(String cardName) {
		this.cardName = cardName;
	}

	@Column(name = "card_id", length = 128)
	public String getCardId() {
		return this.cardId;
	}

	public void setCardId(String cardId) {
		this.cardId = cardId;
	}

	@Column(name = "bank_id")
	public Integer getBankId() {
		return this.bankId;
	}

	public void setBankId(Integer bankId) {
		this.bankId = bankId;
	}

	@Column(name = "ip")
	public String getIp() {
		return this.ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	@Column(name = "operator_user", nullable = false)
	public String getOperatorUser() {
		return this.operatorUser;
	}

	public void setOperatorUser(String operatorUser) {
		this.operatorUser = operatorUser;
	}

	@Column(name = "operator_time", nullable = false, length = 19)
	public String getOperatorTime() {
		return this.operatorTime;
	}

	public void setOperatorTime(String operatorTime) {
		this.operatorTime = operatorTime;
	}

	@Column(name = "remarks")
	public String getRemarks() {
		return this.remarks;
	}

	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}

}