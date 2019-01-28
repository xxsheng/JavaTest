package lottery.domains.content.vo.user;

import lottery.domains.content.entity.UserRecharge;

public class UserRechargeVO {
	
	private int id;
	private String billno;
	private int userId;
	private double money;
	private double beforeMoney;
	private double afterMoney;
	private double recMoney;
	private double feeMoney;
	private String time;
	private int status;
	private int type;
	private int subtype;
	private String infos;
	
	public UserRechargeVO(UserRecharge bean) {
		this.id = bean.getId();
		this.billno = bean.getBillno();
		this.userId = bean.getUserId();
		this.money = bean.getMoney();
		this.beforeMoney = bean.getBeforeMoney();
		this.afterMoney = bean.getAfterMoney();
		this.recMoney = bean.getRecMoney();
		this.feeMoney = bean.getFeeMoney();
		this.time = bean.getTime();
		this.status = bean.getStatus();
		this.type = bean.getType();
		this.subtype = bean.getSubtype();
		this.infos = bean.getInfos();
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getBillno() {
		return billno;
	}

	public void setBillno(String billno) {
		this.billno = billno;
	}

	public int getUserId() {
		return userId;
	}

	public void setUserId(int userId) {
		this.userId = userId;
	}

	public double getMoney() {
		return money;
	}

	public void setMoney(double money) {
		this.money = money;
	}

	public double getBeforeMoney() {
		return beforeMoney;
	}

	public void setBeforeMoney(double beforeMoney) {
		this.beforeMoney = beforeMoney;
	}

	public double getAfterMoney() {
		return afterMoney;
	}

	public void setAfterMoney(double afterMoney) {
		this.afterMoney = afterMoney;
	}

	public double getRecMoney() {
		return recMoney;
	}

	public void setRecMoney(double recMoney) {
		this.recMoney = recMoney;
	}

	public double getFeeMoney() {
		return feeMoney;
	}

	public void setFeeMoney(double feeMoney) {
		this.feeMoney = feeMoney;
	}

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public int getSubtype() {
		return subtype;
	}

	public void setSubtype(int subtype) {
		this.subtype = subtype;
	}

	public String getInfos() {
		return infos;
	}

	public void setInfos(String infos) {
		this.infos = infos;
	}
	
}