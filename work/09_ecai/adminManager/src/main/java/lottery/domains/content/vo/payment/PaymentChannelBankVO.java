package lottery.domains.content.vo.payment;

import lottery.domains.content.entity.PaymentBank;
import lottery.domains.content.entity.PaymentChannelBank;
import lottery.domains.pool.LotteryDataFactory;

public class PaymentChannelBankVO {

	private String bankName;
	private PaymentChannelBank bean;

	public PaymentChannelBankVO(PaymentChannelBank bean, LotteryDataFactory lotteryDataFactory) {
		this.bean = bean;
		PaymentBank paymentBank = lotteryDataFactory.getPaymentBank(bean.getBankId());
		if(paymentBank != null) {
			this.bankName = paymentBank.getName();
		}
	}

	public String getBankName() {
		return bankName;
	}

	public void setBankName(String bankName) {
		this.bankName = bankName;
	}

	public PaymentChannelBank getBean() {
		return bean;
	}

	public void setBean(PaymentChannelBank bean) {
		this.bean = bean;
	}
}