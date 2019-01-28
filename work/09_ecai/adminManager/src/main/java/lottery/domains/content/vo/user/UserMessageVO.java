package lottery.domains.content.vo.user;

import lottery.domains.content.entity.UserMessage;
import lottery.domains.pool.LotteryDataFactory;

public class UserMessageVO {

	private String toUser;
	private String fromUser;
	private UserMessage bean;

	public UserMessageVO(UserMessage bean, LotteryDataFactory lotteryDataFactory) {
		this.bean = bean;
		if (bean.getToUid() != 0) {
			UserVO toUser = lotteryDataFactory.getUser(bean.getToUid());
			this.toUser = toUser.getUsername();
		}
		if (bean.getFromUid() != 0) {
			UserVO fromUser = lotteryDataFactory.getUser(bean.getFromUid());
			this.fromUser = fromUser.getUsername();
		}
	}

	public String getToUser() {
		return toUser;
	}

	public void setToUser(String toUser) {
		this.toUser = toUser;
	}

	public String getFromUser() {
		return fromUser;
	}

	public void setFromUser(String fromUser) {
		this.fromUser = fromUser;
	}

	public UserMessage getBean() {
		return bean;
	}

	public void setBean(UserMessage bean) {
		this.bean = bean;
	}

}