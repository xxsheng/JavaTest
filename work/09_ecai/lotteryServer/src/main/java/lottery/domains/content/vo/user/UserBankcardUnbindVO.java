package lottery.domains.content.vo.user;

import lottery.domains.content.entity.UserBankcardUnbindRecord;
import lottery.domains.pool.DataFactory;

public class UserBankcardUnbindVO {
	private String userIds;	//用户ID
	private String cardId;//银行卡号 索引  唯一约束
	private int unbindNum;//解绑次数
	private String unbindTime;//最后一次解绑时间
	private String username;//用户名
	
	public UserBankcardUnbindVO(UserBankcardUnbindRecord entity,DataFactory dataFactory){
		this.userIds = entity.getUserIds();
		this.cardId = entity.getCardId();
		this.unbindNum = entity.getUnbindNum();
		this.unbindTime = entity.getUnbindTime();
		this.username = "";
		
	}
	
	public String getUserIds() {
		return userIds;
	}
	public void setUserIds(String userIds) {
		this.userIds = userIds;
	}
	public String getCardId() {
		return cardId;
	}

	public void setCardId(String cardId) {
		this.cardId = cardId;
	}

	public int getUnbindNum() {
		return unbindNum;
	}

	public void setUnbindNum(int unbindNum) {
		this.unbindNum = unbindNum;
	}

	public String getUnbindTime() {
		return unbindTime;
	}

	public void setUnbindTime(String unbindTime) {
		this.unbindTime = unbindTime;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}
}
