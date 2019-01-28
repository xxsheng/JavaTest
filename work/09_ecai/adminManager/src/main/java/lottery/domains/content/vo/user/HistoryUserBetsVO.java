package lottery.domains.content.vo.user;

import lottery.domains.content.entity.HistoryUserBets;
import lottery.domains.content.entity.Lottery;
import lottery.domains.content.entity.LotteryPlayRules;
import lottery.domains.content.entity.LotteryPlayRulesGroup;
import lottery.domains.pool.LotteryDataFactory;

public class HistoryUserBetsVO {

	private String username;
	private String lottery;
	private String mname;
	private HistoryUserBets bean;
	
	public HistoryUserBetsVO(HistoryUserBets bean, LotteryDataFactory lotteryDataFactory, boolean isShowNum) {
		this.bean = bean;
		UserVO tmpUser = lotteryDataFactory.getUser(bean.getUserId());
		if(tmpUser != null) {
			this.username = tmpUser.getUsername();
		}
		Lottery lottery = lotteryDataFactory.getLottery(bean.getLotteryId());
		if (lottery != null) {
			this.lottery = lottery.getShowName();
			LotteryPlayRules playRules = lotteryDataFactory.getLotteryPlayRules(bean.getRuleId());
			if(playRules != null) {
				LotteryPlayRulesGroup group = lotteryDataFactory.getLotteryPlayRulesGroup(playRules.getGroupId());
				if (group != null) {
					this.mname = "[" + group.getName() + "_" + playRules.getName() + "]";
				}
			}
		}
		if(!isShowNum) {
			this.bean.setCodes(null);
		}
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getLottery() {
		return lottery;
	}

	public void setLottery(String lottery) {
		this.lottery = lottery;
	}

	public String getMname() {
		return mname;
	}

	public void setMname(String mname) {
		this.mname = mname;
	}

	public HistoryUserBets getBean() {
		return bean;
	}

	public void setBean(HistoryUserBets bean) {
		this.bean = bean;
	}



}