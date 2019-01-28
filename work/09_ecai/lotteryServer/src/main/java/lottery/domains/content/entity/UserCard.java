package lottery.domains.content.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;

import static javax.persistence.GenerationType.IDENTITY;

import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import lottery.domains.content.global.Database;

/**
 * UserCard entity. @author MyEclipse Persistence Tools
 */
@Entity
@Table(name = "user_card", catalog = Database.name, uniqueConstraints = @UniqueConstraint(columnNames = "card_id"))
public class UserCard implements java.io.Serializable {

	// Fields

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private int id;
	private int userId;
	private int bankId;
	private String bankBranch;
	private String cardName;
	private String cardId;
	private int status;
	private String time;
	private String lockTime;
	private int isDefault;

	// Constructors

	/** default constructor */
	public UserCard() {
	}

	/** minimal constructor */
	public UserCard(int userId, int bankId, String cardName,
			String cardId, int status, String time, int isDefault) {
		this.userId = userId;
		this.bankId = bankId;
		this.cardName = cardName;
		this.cardId = cardId;
		this.status = status;
		this.time = time;
		this.isDefault = isDefault;
	}

	/** full constructor */
	public UserCard(int userId, int bankId, String bankBranch,
			String cardName, String cardId, int status, String time,
			String lockTime, int isDefault) {
		this.userId = userId;
		this.bankId = bankId;
		this.bankBranch = bankBranch;
		this.cardName = cardName;
		this.cardId = cardId;
		this.status = status;
		this.time = time;
		this.lockTime = lockTime;
		this.isDefault = isDefault;
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

	@Column(name = "bank_id", nullable = false)
	public int getBankId() {
		return this.bankId;
	}

	public void setBankId(int bankId) {
		this.bankId = bankId;
	}

	@Column(name = "bank_branch", length = 128)
	public String getBankBranch() {
		return this.bankBranch;
	}

	public void setBankBranch(String bankBranch) {
		this.bankBranch = bankBranch;
	}

	@Column(name = "card_name", nullable = false, length = 64)
	public String getCardName() {
		return this.cardName;
	}

	public void setCardName(String cardName) {
		this.cardName = cardName;
	}

	@Column(name = "card_id", unique = true, nullable = false, length = 128)
	public String getCardId() {
		return this.cardId;
	}

	public void setCardId(String cardId) {
		this.cardId = cardId;
	}

	@Column(name = "status", nullable = false)
	public int getStatus() {
		return this.status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	@Column(name = "time", nullable = false, length = 19)
	public String getTime() {
		return this.time;
	}

	public void setTime(String time) {
		this.time = time;
	}

	@Column(name = "lock_time", length = 19)
	public String getLockTime() {
		return this.lockTime;
	}

	public void setLockTime(String lockTime) {
		this.lockTime = lockTime;
	}

	@Column(name = "is_default", nullable = false)
	public int getIsDefault() {
		return this.isDefault;
	}

	public void setIsDefault(int isDefault) {
		this.isDefault = isDefault;
	}

}