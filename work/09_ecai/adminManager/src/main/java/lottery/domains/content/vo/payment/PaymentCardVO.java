package lottery.domains.content.vo.payment;

import lottery.domains.content.entity.PaymentBank;
import lottery.domains.content.entity.PaymentCard;
import lottery.domains.pool.LotteryDataFactory;
import org.apache.commons.lang.StringUtils;

public class PaymentCardVO {
	
	private String bankName;
	private PaymentCard bean;
	
	public PaymentCardVO(PaymentCard bean, LotteryDataFactory df) {
		this.bean = bean;
		PaymentBank bank = df.getPaymentBank(bean.getBankId());
		if(bank != null) {
			this.bankName = bank.getName();
		}
		if (StringUtils.isNotEmpty(bean.getCardId())) {
			int length = bean.getCardId().length();
			int preAndSub;

			if (length <= 4) {
				preAndSub = 1;
			}
			else if (length <= 12) {
				preAndSub = 2;
			}
			else{
				preAndSub = 4;
			}

			String start = bean.getCardId().substring(0, preAndSub);
			String middle = " **** ";
			String end = bean.getCardId().substring(length-preAndSub);
			bean.setCardId(start + middle + end);
		}
		if (StringUtils.isNotEmpty(bean.getCardName())) {
			bean.setCardName("*" + bean.getCardName().substring(bean.getCardName().length()-1));
		}
		if (StringUtils.isNotEmpty(bean.getBranchName())) {
			int length = bean.getBranchName().length();
			int preAndSub;
			if (length <= 5) {
				preAndSub = 1;
			}
			else{
				preAndSub = 2;
			}

			String start = bean.getBranchName().substring(0, preAndSub);
			String middle = "***";
			String end = bean.getBranchName().substring(length-preAndSub);
			bean.setBranchName(start + middle + end);
		}
	}

	public String getBankName() {
		return bankName;
	}

	public void setBankName(String bankName) {
		this.bankName = bankName;
	}

	public PaymentCard getBean() {
		return bean;
	}

	public void setBean(PaymentCard bean) {
		this.bean = bean;
	}
	
}
