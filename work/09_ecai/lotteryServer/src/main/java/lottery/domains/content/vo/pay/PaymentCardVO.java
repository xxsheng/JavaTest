package lottery.domains.content.vo.pay;

import lottery.domains.content.entity.PaymentBank;
import lottery.domains.content.entity.PaymentCard;
import lottery.domains.pool.DataFactory;

public class PaymentCardVO {

	private int id;
	private int bankId;
	private String bankName;
	private String link;
	 private double minUnitRecharge;
	 private double maxUnitRecharge;
	private String cardName;
	private String cardId;
	private String branchName;
	
	public PaymentCardVO(PaymentCard bean, DataFactory df) {
		this.id = bean.getId();
		this.bankId = bean.getBankId();
		
		PaymentBank bank = df.getPaymentBank(bean.getBankId());
		if(bank != null) {
			this.bankName = bank.getName();
			this.link = bank.getUrl();
		}
		 this.minUnitRecharge = bean.getMinUnitRecharge();
		 this.maxUnitRecharge = bean.getMaxUnitRecharge();
		this.cardName = bean.getCardName();
		this.cardId = bean.getCardId();
		this.branchName = bean.getBranchName();
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

	public String getLink() {
		return link;
	}

	public void setLink(String link) {
		this.link = link;
	}

	 public double getMinUnitRecharge() {
	 	return minUnitRecharge;
	 }
    
	 public void setMinUnitRecharge(double minUnitRecharge) {
	 	this.minUnitRecharge = minUnitRecharge;
	 }
    
	 public double getMaxUnitRecharge() {
	 	return maxUnitRecharge;
	 }
    
	 public void setMaxUnitRecharge(double maxUnitRecharge) {
	 	this.maxUnitRecharge = maxUnitRecharge;
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

	public String getBranchName() {
		return branchName;
	}

	public void setBranchName(String branchName) {
		this.branchName = branchName;
	}
}