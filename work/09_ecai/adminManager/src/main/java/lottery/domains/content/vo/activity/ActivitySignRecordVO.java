package lottery.domains.content.vo.activity;

import lottery.domains.content.entity.ActivitySignRecord;
import lottery.domains.content.vo.user.UserVO;
import lottery.domains.pool.LotteryDataFactory;

public class ActivitySignRecordVO {
	
	private String username;
	public ActivitySignRecord bean;
	
	public ActivitySignRecordVO() {
		
	}
	
	public ActivitySignRecordVO(ActivitySignRecord bean, LotteryDataFactory df) {
		this.bean = bean;
		UserVO user = df.getUser(bean.getUserId());
		if(user != null) {
			this.username = user.getUsername();
		}
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public ActivitySignRecord getBean() {
		return bean;
	}

	public void setBean(ActivitySignRecord bean) {
		this.bean = bean;
	}

}