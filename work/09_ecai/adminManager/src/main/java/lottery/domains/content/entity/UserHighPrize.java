package lottery.domains.content.entity;

import lottery.domains.content.global.Database;

import javax.persistence.*;

import static javax.persistence.GenerationType.IDENTITY;

@Entity
@Table(name = "user_high_prize", catalog = Database.name)
public class UserHighPrize implements java.io.Serializable, Cloneable {

	private int id;
	private int userId; // 用户ID
	private int platform; // 类型;2:彩票;11:PT;4:AG
	private String name; // 游戏名称
	private String nameId; // 游戏名称ID
	private String subName; // 期号或桌号等
	private String refId; // 游戏记录ID
	private double money; // 投注金额
	private double prizeMoney; // 中奖金额
	private double times; // 倍数
	private String time; // 时间
	private int status; // 状态;0:待确认;1:已锁定;2:已确认
	private String confirmUsername; // 确认人


	@Override
	public UserHighPrize clone() {
		try {
			return (UserHighPrize) super.clone();
		} catch (Exception e) {}
		return null;
	}

	@Id
	@GeneratedValue(strategy = IDENTITY)
	@Column(name = "id", unique = true, nullable = false)
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	@Column(name = "user_id", nullable = false)
	public int getUserId() {
		return userId;
	}

	public void setUserId(int userId) {
		this.userId = userId;
	}

	@Column(name = "platform", nullable = false)
	public int getPlatform() {
		return platform;
	}

	public void setPlatform(int platform) {
		this.platform = platform;
	}

	@Column(name = "name", nullable = false, length = 512)
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Column(name = "name_id", nullable = false, length = 256)
	public String getNameId() {
		return nameId;
	}

	public void setNameId(String nameId) {
		this.nameId = nameId;
	}

	@Column(name = "sub_name", nullable = false, length = 512)
	public String getSubName() {
		return subName;
	}

	public void setSubName(String subName) {
		this.subName = subName;
	}

	@Column(name = "ref_id", nullable = false, length = 512)
	public String getRefId() {
		return refId;
	}

	public void setRefId(String refId) {
		this.refId = refId;
	}

	@Column(name = "money", nullable = false, precision = 16, scale = 5)
	public double getMoney() {
		return money;
	}

	public void setMoney(double money) {
		this.money = money;
	}

	@Column(name = "prize_money", nullable = false, precision = 16, scale = 5)
	public double getPrizeMoney() {
		return prizeMoney;
	}

	public void setPrizeMoney(double prizeMoney) {
		this.prizeMoney = prizeMoney;
	}

	@Column(name = "times", nullable = false, precision = 16, scale = 5)
	public double getTimes() {
		return times;
	}

	public void setTimes(double times) {
		this.times = times;
	}

	@Column(name = "time", nullable = false, length = 19)
	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}

	@Column(name = "status", nullable = false)
	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	@Column(name = "confirm_username", length = 50)
	public String getConfirmUsername() {
		return confirmUsername;
	}

	public void setConfirmUsername(String confirmUsername) {
		this.confirmUsername = confirmUsername;
	}
}