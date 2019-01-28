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
 * LotteryOpenCode entity. @author MyEclipse Persistence Tools
 */
@Entity
@Table(name = "lottery_open_code", catalog = Database.name, uniqueConstraints = @UniqueConstraint(columnNames = {
		"lottery", "expect", "user_id" }))
public class LotteryOpenCode implements java.io.Serializable {

	// Fields

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private int id;
	private Integer userId;
	private String lottery;
	private String expect;
	private String code;
	private String time;
	private int openStatus;
	private String openTime;

	// Constructors

	/** default constructor */
	public LotteryOpenCode() {
	}

	/** minimal constructor */
	public LotteryOpenCode(String lottery, String expect, String code,
			String time, int openStatus) {
		this.lottery = lottery;
		this.expect = expect;
		this.code = code;
		this.time = time;
		this.openStatus = openStatus;
	}

	/** full constructor */
	public LotteryOpenCode(String lottery, String expect, String code,
			String time, int openStatus, String openTime) {
		this.lottery = lottery;
		this.expect = expect;
		this.code = code;
		this.time = time;
		this.openStatus = openStatus;
		this.openTime = openTime;
	}

	// Property accessors
	@Id
	@GeneratedValue(strategy = IDENTITY)
	@Column(name = "id", unique = true, nullable = false)
	public int getId() {
		return this.id;
	}

	@Column(name = "user_id")
	public Integer getUserId() {
		return this.userId;
	}

	public void setUserId(Integer userId) {
		this.userId = userId;
	}

	public void setId(int id) {
		this.id = id;
	}

	@Column(name = "lottery", nullable = false, length = 32)
	public String getLottery() {
		return this.lottery;
	}

	public void setLottery(String lottery) {
		this.lottery = lottery;
	}

	@Column(name = "expect", nullable = false, length = 32)
	public String getExpect() {
		return this.expect;
	}

	public void setExpect(String expect) {
		this.expect = expect;
	}

	@Column(name = "code", nullable = false, length = 128)
	public String getCode() {
		return this.code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	@Column(name = "time", nullable = false, length = 19)
	public String getTime() {
		return this.time;
	}

	public void setTime(String time) {
		this.time = time;
	}

	@Column(name = "open_status", nullable = false)
	public int getOpenStatus() {
		return this.openStatus;
	}

	public void setOpenStatus(int openStatus) {
		this.openStatus = openStatus;
	}

	@Column(name = "open_time", length = 19)
	public String getOpenTime() {
		return this.openTime;
	}

	public void setOpenTime(String openTime) {
		this.openTime = openTime;
	}

}