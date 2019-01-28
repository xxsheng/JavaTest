package lottery.domains.content.vo.user;

import javautils.StringUtil;
import lottery.domains.content.entity.PaymentBank;
import lottery.domains.content.entity.UserCard;
import lottery.domains.pool.DataFactory;

public class UserCardVO {
	
	private int id;
	private int bankId;
	private String bankName;
	private String bankBranch;
	private String cardName;
	private String cardId;
	private String time;
	private int status;
	private int isDefault;
	
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
	
	public UserCardVO(UserCard bean, DataFactory dataFactory) {
		this.id = bean.getId();
		this.bankId = bean.getBankId();
		this.cardName = encryptCardName(bean.getCardName());
		this.cardId = encryptCardId(bean.getCardId());
		this.bankBranch = bean.getBankBranch();
		this.time = bean.getTime();
		this.status = bean.getStatus();
		this.isDefault = bean.getIsDefault();
		PaymentBank bank = dataFactory.getPaymentBank(bean.getBankId());
		if(bank != null) {
			this.bankName = bank.getName();
		}
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getBankId() {
		return bankId;
	}

	public void setBankId(int bankId) {
		this.bankId = bankId;
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

	public int getIsDefault() {
		return isDefault;
	}

	public void setIsDefault(int isDefault) {
		this.isDefault = isDefault;
	}

}