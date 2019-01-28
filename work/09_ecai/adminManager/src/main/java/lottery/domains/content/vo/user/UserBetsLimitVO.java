package lottery.domains.content.vo.user;

import lottery.domains.content.entity.Lottery;
import lottery.domains.content.entity.UserBetsLimit;
import lottery.domains.pool.LotteryDataFactory;

public class UserBetsLimitVO {

	private String userName;
	
	private String lotteryName;
	
	private UserBetsLimit bean;
	
	public UserBetsLimitVO(UserBetsLimit bean, LotteryDataFactory lotteryDataFactory){
		this.bean = bean;
		UserVO user = lotteryDataFactory.getUser(bean.getUserId()); 
		if(user != null){
			this.userName = user.getUsername();
		}
		
		Lottery lottery = lotteryDataFactory.getLottery(bean.getLotteryId());
		if(lottery != null){
			this.lotteryName = lottery.getShowName();
		}
	}
	
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public String getLotteryName() {
		return lotteryName;
	}
	public void setLotteryName(String lotteryName) {
		this.lotteryName = lotteryName;
	}
	public UserBetsLimit getBean() {
		return bean;
	}
	public void setBean(UserBetsLimit bean) {
		this.bean = bean;
	}
	
	
}
