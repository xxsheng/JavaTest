package lottery.domains.content.vo.user;

import lottery.domains.content.entity.UserRecharge;
import lottery.domains.content.vo.payment.PaymentCardVO;
import lottery.domains.content.vo.payment.PaymentChannelVO;
import lottery.domains.pool.LotteryDataFactory;

public class UserRechargeVO {

	private UserRecharge bean;
	private String username;
	private String name;
	private PaymentCardVO receiveCard;

	public UserRechargeVO(UserRecharge bean, LotteryDataFactory df) {
		this.bean = bean;
		UserVO user = df.getUser(bean.getUserId());
		if (user != null) {
			this.username = user.getUsername();
		}

		if (bean.getCardId() != null) {
			PaymentCardVO paymentCard = df.getPaymentCard(bean.getCardId());
			this.receiveCard = paymentCard;
		}

		PaymentChannelVO channel = df.getPaymentChannelVO(bean.getChannelId());
		if (channel != null) {
			this.name = channel.getName();
		}
	}

	public UserRecharge getBean() {
		return bean;
	}

	public void setBean(UserRecharge bean) {
		this.bean = bean;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public PaymentCardVO getReceiveCard() {
		return receiveCard;
	}

	public void setReceiveCard(PaymentCardVO receiveCard) {
		this.receiveCard = receiveCard;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}
