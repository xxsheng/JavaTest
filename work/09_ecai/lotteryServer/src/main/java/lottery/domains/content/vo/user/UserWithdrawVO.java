package lottery.domains.content.vo.user;

import javautils.StringUtil;
import lottery.domains.content.entity.UserWithdraw;

public class UserWithdrawVO {
	
	private int id;
	private String billno;
	private int userId;
	private double money;
	private double beforeMoney;
	private double afterMoney;
	private double recMoney;
	private double feeMoney;
	private String time;
	private int status; // －1：拒绝支付；0：待处理；1：已完成；2：处理中；3：银行处理中；4：提现失败
	private String infos;
	private String bankName;
	private String cardName;
	private String cardId;

	/**
	 * 加密姓名
	 * @param name
	 * @return
	 */
	private String encryptCardName(String name) {
		if(StringUtil.isNotNull(name)) {
			return name.substring(0, 1) + "**";
		}
		return null;
	}
	
	/**
	 * 加密卡号
	 * @param cardId
	 * @return
	 */
	private String encryptCardId(String cardId) {
		if(StringUtil.isNotNull(cardId) && cardId.length() > 8) {
			return cardId.substring(0, 4) + " **** **** " + cardId.substring(cardId.length() - 4);
		}
		return null;
	}
	
	public UserWithdrawVO(UserWithdraw bean) {
		this.id = bean.getId();
		this.billno = bean.getBillno();
		this.userId = bean.getUserId();
		this.money = bean.getMoney();
		this.beforeMoney = bean.getBeforeMoney();
		this.afterMoney = bean.getAfterMoney();
		this.recMoney = bean.getRecMoney();
		this.feeMoney = bean.getFeeMoney();
		this.time = bean.getTime();

		// status; －1：拒绝支付；0：待处理；1：已完成；2：处理中；3：银行处理中；4：提现失败

		// status: 0：未处理；1：已完成；-1：拒绝支付；
		// checkStatus: 0：待审核；1：已通过；2：未通过；
		// lockStatus: 0：未锁定；1：已锁定；
		// remitStatus: 0：未处理；1：正在打款；2：打款完成；3：第三方待处理；-1：请求失败；-2：打款失败；-3查询状态中；-4：未知状态；-5：第三方处理失败；-6：银行处理失败；-7：第三方拒绝支付
		if (bean.getStatus() == -1) {
			this.status = -1;
		}
		else if (bean.getStatus() == 0 && bean.getCheckStatus() == 0) {
			this.status = 0;
		}
		else if (bean.getStatus() == 1 && bean.getCheckStatus() == 1 && bean.getRemitStatus() == 2) {
			this.status = 1;
		}
		else if (bean.getStatus() == 0 && bean.getCheckStatus() == 1 && (bean.getRemitStatus() == 1 || bean.getRemitStatus() == 3 || bean.getRemitStatus() == -3 || bean.getRemitStatus() == -4 || bean.getRemitStatus() == -5 || bean.getRemitStatus() == -6 || bean.getRemitStatus() == -7)) {
			this.status = 3;
		}
		else if (bean.getStatus() == 1 && bean.getCheckStatus() == -1) {
			this.status = 4;
		}
		else if (bean.getStatus() == 1 && bean.getCheckStatus() == 1 && (bean.getRemitStatus() == -1 || bean.getRemitStatus() == -2)) {
			this.status = 4;
		}
		else {
			this.status = 2;
		}

		this.infos = bean.getInfos();
		this.bankName = bean.getBankName();
		this.cardName = encryptCardName(bean.getCardName());
		this.cardId = encryptCardId(bean.getCardId());
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

	public String getInfos() {
		return infos;
	}

	public void setInfos(String infos) {
		this.infos = infos;
	}

	public String getBankName() {
		return bankName;
	}

	public void setBankName(String bankName) {
		this.bankName = bankName;
	}

	public String getCardName() {
		return cardName;
	}

	public void setCardName(String cardName) {
		this.cardName = cardName;
	}

	public String getCardId() {
		return cardId;
	}

	public void setCardId(String cardId) {
		this.cardId = cardId;
	}
}