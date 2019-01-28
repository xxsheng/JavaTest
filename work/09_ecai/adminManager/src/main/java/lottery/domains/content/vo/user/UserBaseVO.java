package lottery.domains.content.vo.user;

import lottery.domains.content.entity.User;

public class UserBaseVO {

	private int id;
	private String username;
	private String nickname;
	private double totalMoney;
	private double lotteryMoney;
	private double baccaratMoney;
	private double freezeMoney;
	private double dividendMoney;
	private int type;
	private int code;
	private double locatePoint;
	private double notLocatePoint;
	private double extraPoint;
	private String registTime;
	private String loginTime;
	private int AStatus;
	private int BStatus;
	private int onlineStatus;
	private int isCjZhaoShang; // 0：不是超级招商；1：是超级招商

	public UserBaseVO(User bean) {
		this.id = bean.getId();
		this.username = bean.getUsername();
		this.nickname = bean.getNickname();
		this.totalMoney = bean.getTotalMoney();
		this.lotteryMoney = bean.getLotteryMoney();
		this.baccaratMoney = bean.getBaccaratMoney();
		this.freezeMoney = bean.getFreezeMoney();
		this.dividendMoney = bean.getDividendMoney();
		this.type = bean.getType();
		this.code = bean.getCode();
		this.locatePoint = bean.getLocatePoint();
		this.notLocatePoint = bean.getNotLocatePoint();
		this.extraPoint = bean.getExtraPoint();
		this.registTime = bean.getRegistTime();
		this.loginTime = bean.getLoginTime();
		this.AStatus = bean.getAStatus();
		this.BStatus = bean.getBStatus();
		this.onlineStatus = bean.getOnlineStatus();
		this.isCjZhaoShang = bean.getIsCjZhaoShang();
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

	public String getNickname() {
		return nickname;
	}

	public void setNickname(String nickname) {
		this.nickname = nickname;
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

	public double getBaccaratMoney() {
		return baccaratMoney;
	}

	public void setBaccaratMoney(double baccaratMoney) {
		this.baccaratMoney = baccaratMoney;
	}

	public double getFreezeMoney() {
		return freezeMoney;
	}

	public void setFreezeMoney(double freezeMoney) {
		this.freezeMoney = freezeMoney;
	}

	public double getDividendMoney() {
		return dividendMoney;
	}

	public void setDividendMoney(double dividendMoney) {
		this.dividendMoney = dividendMoney;
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

	public double getExtraPoint() {
		return extraPoint;
	}

	public void setExtraPoint(double extraPoint) {
		this.extraPoint = extraPoint;
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

	public int getAStatus() {
		return AStatus;
	}

	public void setAStatus(int aStatus) {
		AStatus = aStatus;
	}

	public int getBStatus() {
		return BStatus;
	}

	public void setBStatus(int bStatus) {
		BStatus = bStatus;
	}

	public int getOnlineStatus() {
		return onlineStatus;
	}

	public void setOnlineStatus(int onlineStatus) {
		this.onlineStatus = onlineStatus;
	}

	public int getIsCjZhaoShang() {
		return isCjZhaoShang;
	}

	public void setIsCjZhaoShang(int isCjZhaoShang) {
		this.isCjZhaoShang = isCjZhaoShang;
	}
}