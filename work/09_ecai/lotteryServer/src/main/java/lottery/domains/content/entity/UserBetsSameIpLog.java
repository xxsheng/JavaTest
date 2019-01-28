package lottery.domains.content.entity;

import lottery.domains.content.global.Database;

import javax.persistence.*;

import static javax.persistence.GenerationType.IDENTITY;

/**
 * 同IP投注日志
 */
@Entity
@Table(name = "user_bets_same_ip_log", catalog = Database.name)
public class UserBetsSameIpLog implements java.io.Serializable {

	private int id;
	private String ip; // IP地址
	private String address; // 参考地址
	private String users; // 用户列表，格式[aa],[bb],[cc]
	private int usersCount; // 用户人数
	private String lastTime; // 最后投注时间
	private String lastUser; // 最后投注用户
	private int lastUserBetsId; // 最后投注ID
	private int times; // 总计投注次数
	private double amount; // 总计投注金额

	public UserBetsSameIpLog() {
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

	// @Column(name = "username", unique = true, nullable = false, length = 20)
	// @Column(name = "total_money", nullable = false, precision = 16, scale = 5)
	// @Column(name = "type", nullable = false)

	@Column(name = "ip", unique = true, nullable = false, length = 20)
	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	@Column(name = "address", nullable = false, length = 256)
	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	@Column(name = "users", nullable = false, length = 4096)
	public String getUsers() {
		return users;
	}

	public void setUsers(String users) {
		this.users = users;
	}

	@Column(name = "users_count", nullable = false, length = 11)
	public int getUsersCount() {
		return usersCount;
	}

	public void setUsersCount(int usersCount) {
		this.usersCount = usersCount;
	}

	@Column(name = "last_time", nullable = false, length = 20)
	public String getLastTime() {
		return lastTime;
	}

	public void setLastTime(String lastTime) {
		this.lastTime = lastTime;
	}

	@Column(name = "times", length = 11)
	public int getTimes() {
		return times;
	}

	public void setTimes(int times) {
		this.times = times;
	}

	@Column(name = "amount", nullable = false, precision = 16, scale = 5)
	public double getAmount() {
		return amount;
	}

	public void setAmount(double amount) {
		this.amount = amount;
	}

    @Column(name = "last_user", nullable = false, length = 20)
    public String getLastUser() {
        return lastUser;
    }

    public void setLastUser(String lastUser) {
        this.lastUser = lastUser;
    }

    @Column(name = "last_user_bets_id", nullable = false, length = 11)
    public int getLastUserBetsId() {
        return lastUserBetsId;
    }

    public void setLastUserBetsId(int lastUserBetsId) {
        this.lastUserBetsId = lastUserBetsId;
    }
}