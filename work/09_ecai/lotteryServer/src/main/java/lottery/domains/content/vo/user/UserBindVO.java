package lottery.domains.content.vo.user;

import javautils.StringUtil;

public class UserBindVO {

	private String withdrawName;
	private String birthday;
	private String bankName;
	private String bankBranch;
	private String cardId;
	private String withdrawPwd;
	
	/**
	 * 加密姓名
	 * @param name
	 * @return
	 */
	private String encryptWithdrawName(String name) {
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
		if(StringUtil.isNotNull(cardId)) {
			if(cardId.length() > 8) {
				return cardId.substring(0, 4) + " **** **** " + cardId.substring(cardId.length() - 4);
			} else {
				return "**** ****";
			}
		}
		return null;
	}

	public String getWithdrawName() {
		return withdrawName;
	}

	public void setWithdrawName(String withdrawName) {
		this.withdrawName = encryptWithdrawName(withdrawName);
	}

	public String getBirthday() {
		return birthday;
	}

	public void setBirthday(String birthday) {
		this.birthday = birthday;
	}

	public String getBankName() {
		return bankName;
	}

	public void setBankName(String bankName) {
		this.bankName = bankName;
	}

	public String getBankBranch() {
		return bankBranch;
	}

	public void setBankBranch(String bankBranch) {
		this.bankBranch = bankBranch;
	}

	public String getCardId() {
		return cardId;
	}

	public void setCardId(String cardId) {
		this.cardId = encryptCardId(cardId);
	}

	public String getWithdrawPwd() {
		return withdrawPwd;
	}

	public void setWithdrawPwd(String withdrawPwd) {
		this.withdrawPwd = withdrawPwd;
	}

}