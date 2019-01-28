package lottery.domains.content.vo.user;

import lottery.domains.content.entity.User;
import org.apache.commons.lang.StringUtils;

public class UserTeamVO {
	private int id;
	private String username;
	private double totalMoney;
	private double lotteryMoney;
	private int type;
	private int code;
	private double locatePoint;
	private double notLocatePoint;
	private String registTime;
	private String loginTime;
	private int onlineStatus;
	private int allowEqualCode;
	private int allowTransfers;
	private double teamTotalBalance; // 团队主账余额
	private double teamLotteryBalance; // 团队彩账余额
	private int totalUser = 0; // 团队人数
	private int onlineUser = 0; // 团队在线人数

	public UserTeamVO(User bean) {
		this.id = bean.getId();
		this.username = bean.getUsername();
		this.totalMoney = bean.getTotalMoney();
		this.lotteryMoney = bean.getLotteryMoney();
		this.type = bean.getType();
		this.code = bean.getCode();
		this.locatePoint = bean.getLocatePoint();
		this.notLocatePoint = bean.getNotLocatePoint();
		this.registTime = bean.getRegistTime();
		this.loginTime = bean.getLoginTime();
		this.onlineStatus = bean.getOnlineStatus();
		this.allowEqualCode = bean.getAllowEqualCode();
		this.allowTransfers = bean.getAllowTransfers();

		addTeamData(bean);
	}

	public void addTeamData(User user) {
		this.teamTotalBalance += user.getTotalMoney();
		this.teamLotteryBalance += user.getLotteryMoney();
		this.totalUser++;
		if (StringUtils.isNotEmpty(user.getSessionId()) && user.getOnlineStatus() == 1) {
			this.onlineUser++;
		}
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public double getTotalMoney() {
		return totalMoney;
	}

	public void setTotalMoney(double totalMoney) {
		this.totalMoney = totalMoney;
	}

	public double getLotteryMoney() {
		return lotteryMoney;
	}

	public void setLotteryMoney(double lotteryMoney) {
		this.lotteryMoney = lotteryMoney;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public int getCode() {
		return code;
	}

	public void setCode(int code) {
		this.code = code;
	}

	public double getLocatePoint() {
		return locatePoint;
	}

	public void setLocatePoint(double locatePoint) {
		this.locatePoint = locatePoint;
	}

	public double getNotLocatePoint() {
		return notLocatePoint;
	}

	public void setNotLocatePoint(double notLocatePoint) {
		this.notLocatePoint = notLocatePoint;
	}

	public String getRegistTime() {
		return registTime;
	}

	public void setRegistTime(String registTime) {
		this.registTime = registTime;
	}

	public String getLoginTime() {
		return loginTime;
	}

	public void setLoginTime(String loginTime) {
		this.loginTime = loginTime;
	}

	public int getOnlineStatus() {
		return onlineStatus;
	}

	public void setOnlineStatus(int onlineStatus) {
		this.onlineStatus = onlineStatus;
	}

	public int getAllowEqualCode() {
		return allowEqualCode;
	}

	public void setAllowEqualCode(int allowEqualCode) {
		this.allowEqualCode = allowEqualCode;
	}

	public int getAllowTransfers() {
		return allowTransfers;
	}

	public void setAllowTransfers(int allowTransfers) {
		this.allowTransfers = allowTransfers;
	}

	public double getTeamTotalBalance() {
		return teamTotalBalance;
	}

	public void setTeamTotalBalance(double teamTotalBalance) {
		this.teamTotalBalance = teamTotalBalance;
	}

	public double getTeamLotteryBalance() {
		return teamLotteryBalance;
	}

	public void setTeamLotteryBalance(double teamLotteryBalance) {
		this.teamLotteryBalance = teamLotteryBalance;
	}

	public int getTotalUser() {
		return totalUser;
	}

	public void setTotalUser(int totalUser) {
		this.totalUser = totalUser;
	}

	public int getOnlineUser() {
		return onlineUser;
	}

	public void setOnlineUser(int onlineUser) {
		this.onlineUser = onlineUser;
	}
}