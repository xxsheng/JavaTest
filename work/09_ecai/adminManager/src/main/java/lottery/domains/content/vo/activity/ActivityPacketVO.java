package lottery.domains.content.vo.activity;

import lottery.domains.content.entity.ActivityPacketBill;
import lottery.domains.content.entity.ActivityPacketInfo;
import lottery.domains.content.vo.user.UserVO;
import lottery.domains.pool.LotteryDataFactory;

public class ActivityPacketVO {
	
	private String username;
	public ActivityPacketBill bean;
	public ActivityPacketInfo info;
	
	public ActivityPacketVO() {
		
	}
	
	public ActivityPacketVO(ActivityPacketBill bean, LotteryDataFactory df) {
		this.bean = bean;
		UserVO user = df.getUser(bean.getUserId());
		if(user != null) {
			this.username = user.getUsername();
		}
	}
	
	public ActivityPacketVO(ActivityPacketInfo info, LotteryDataFactory df) {
		this.info = info;
		if(info.getUserId() == -1){//系统红包
			this.username = "系统红包";
		}else{
			UserVO user = df.getUser(info.getUserId());
			if(user != null) {
				this.username = user.getUsername();
			}
		}
	
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public ActivityPacketBill getBean() {
		return bean;
	}

	public void setBean(ActivityPacketBill bean) {
		this.bean = bean;
	}

	public ActivityPacketInfo getInfo() {
		return info;
	}

	public void setInfo(ActivityPacketInfo info) {
		this.info = info;
	}


}