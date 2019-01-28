package lottery.domains.content.vo.user;

import lottery.domains.content.entity.UserBetsSameIpLog;
import org.apache.commons.lang.StringUtils;

/**
 * 同IP投注日志
 */
public class UserBetsSameIpLogVO {

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

	public UserBetsSameIpLogVO(UserBetsSameIpLog userBetsSameIpLog) {
		this.setId(userBetsSameIpLog.getId());
		this.setIp(userBetsSameIpLog.getIp());
		this.setAddress(userBetsSameIpLog.getAddress());
		if (StringUtils.isNotEmpty(userBetsSameIpLog.getUsers())) {
			this.setUsers(userBetsSameIpLog.getUsers().replaceAll("\\[", "").replaceAll("\\]", ""));
		}
		this.setUsersCount(userBetsSameIpLog.getUsersCount());
		this.setLastTime(userBetsSameIpLog.getLastTime());
		this.setLastUser(userBetsSameIpLog.getLastUser());
		this.setLastUserBetsId(userBetsSameIpLog.getLastUserBetsId());
		this.setTimes(userBetsSameIpLog.getTimes());
		this.setAmount(userBetsSameIpLog.getAmount());
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getUsers() {
		return users;
	}

	public void setUsers(String users) {
		this.users = users;
	}

	public int getUsersCount() {
		return usersCount;
	}

	public void setUsersCount(int usersCount) {
		this.usersCount = usersCount;
	}

	public String getLastTime() {
		return lastTime;
	}

	public void setLastTime(String lastTime) {
		this.lastTime = lastTime;
	}

	public String getLastUser() {
		return lastUser;
	}

	public void setLastUser(String lastUser) {
		this.lastUser = lastUser;
	}

	public int getLastUserBetsId() {
		return lastUserBetsId;
	}

	public void setLastUserBetsId(int lastUserBetsId) {
		this.lastUserBetsId = lastUserBetsId;
	}

	public int getTimes() {
		return times;
	}

	public void setTimes(int times) {
		this.times = times;
	}

	public double getAmount() {
		return amount;
	}

	public void setAmount(double amount) {
		this.amount = amount;
	}
}