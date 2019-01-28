package lottery.domains.content.vo.user;

import lottery.domains.content.entity.UserMessage;
import lottery.domains.content.global.Global;
import lottery.domains.pool.DataFactory;

public class UserMessageVO {

	private String toUser;
	private String fromUser;
	private int upid;
	private UserMessage bean;

	public UserMessageVO(UserMessage bean, int upid, DataFactory dataFactory) {
		this.bean = bean;
		this.upid = upid;
		if(bean.getType() == Global.USER_MESSAGE_TYPE_USER) {
			if(bean.getToUid() != 0) {
				if(bean.getToUid() == upid) {
					this.toUser = "上级代理";
				} else {
					UserVO toUser = dataFactory.getUser(bean.getToUid());
					if(toUser != null) {
						this.toUser = toUser.getUsername();
					}
				}
			}
			if(bean.getFromUid() != 0) {
				if(bean.getFromUid() == upid) {
					this.fromUser = "上级代理";
				} else {
					UserVO fromUser = dataFactory.getUser(bean.getFromUid());
					if(fromUser != null) {
						this.fromUser = fromUser.getUsername();
					}
				}
			}
		}
		if(bean.getType() == Global.USER_MESSAGE_TYPE_SYSTEM) {
			if(bean.getToUid() != 0) {
				UserVO toUser = dataFactory.getUser(bean.getToUid());
				if(toUser != null) {
					this.toUser = toUser.getUsername();
				}
			} else {
				this.toUser = "系统管理员";
			}
			if(bean.getFromUid() != 0) {
				UserVO fromUser = dataFactory.getUser(bean.getFromUid());
				if(fromUser != null) {
					this.fromUser = fromUser.getUsername();
				}
			} else {
				this.fromUser = "系统管理员";
			}
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

	public int getUpid() {
		return upid;
	}

	public void setUpid(int upid) {
		this.upid = upid;
	}

	public UserMessage getBean() {
		return bean;
	}

	public void setBean(UserMessage bean) {
		this.bean = bean;
	}

}