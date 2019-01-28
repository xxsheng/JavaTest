package lottery.domains.content.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import static javax.persistence.GenerationType.IDENTITY;
import javax.persistence.Id;
import javax.persistence.Table;

import lottery.domains.content.global.Database;

/**
 * VipBirthdayGifts entity. @author MyEclipse Persistence Tools
 */
@Entity
@Table(name = "vip_birthday_gifts", catalog = Database.name)
public class VipBirthdayGifts implements java.io.Serializable {

	// Fields

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private int id;
	private int userId;
	private int level;
	private double money;
	private String birthday;
	private String time;
	private int status;
	private int isReceived;

	// Constructors

	/** default constructor */
	public VipBirthdayGifts() {
	}

	/** full constructor */
	public VipBirthdayGifts(int userId, int level, double money,
			String birthday, String time, int status, int isReceived) {
		this.userId = userId;
		this.level = level;
		this.money = money;
		this.birthday = birthday;
		this.time = time;
		this.status = status;
		this.isReceived = isReceived;
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

	@Column(name = "level", nullable = false)
	public int getLevel() {
		return this.level;
	}

	public void setLevel(int level) {
		this.level = level;
	}

	@Column(name = "money", nullable = false, precision = 16, scale = 5)
	public double getMoney() {
		return this.money;
	}

	public void setMoney(double money) {
		this.money = money;
	}

	@Column(name = "birthday", nullable = false, length = 10)
	public String getBirthday() {
		return this.birthday;
	}

	public void setBirthday(String birthday) {
		this.birthday = birthday;
	}

	@Column(name = "time", nullable = false, length = 19)
	public String getTime() {
		return this.time;
	}

	public void setTime(String time) {
		this.time = time;
	}

	@Column(name = "status", nullable = false)
	public int getStatus() {
		return this.status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	@Column(name = "is_received", nullable = false)
	public int getIsReceived() {
		return this.isReceived;
	}

	public void setIsReceived(int isReceived) {
		this.isReceived = isReceived;
	}

}