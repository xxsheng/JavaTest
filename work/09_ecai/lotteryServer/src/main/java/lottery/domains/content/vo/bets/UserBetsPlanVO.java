package lottery.domains.content.vo.bets;

import lottery.domains.content.entity.Lottery;
import lottery.domains.content.entity.LotteryPlayRules;
import lottery.domains.content.entity.LotteryPlayRulesGroup;
import lottery.domains.content.entity.UserBetsPlan;
import lottery.domains.content.vo.user.UserVO;
import lottery.domains.pool.DataFactory;

public class UserBetsPlanVO {

	private String username;
	private String lottery;
	private String mname;
	private UserBetsPlan bean;

	public UserBetsPlanVO(UserBetsPlan bean, DataFactory dataFactory) {
		this.bean = bean;
		UserVO tmpUser = dataFactory.getUser(bean.getUserId());
		if (tmpUser != null) {
			this.username = tmpUser.getUsername();
		}
		Lottery lottery = dataFactory.getLottery(bean.getLotteryId());
		if (lottery != null) {
			this.lottery = lottery.getShowName();
		}
		LotteryPlayRules rule = dataFactory.getLotteryPlayRules(lottery.getId(), bean.getRuleId());
		if(rule != null) {
			LotteryPlayRulesGroup group = dataFactory.getLotteryPlayRulesGroup(lottery.getId(), rule.getGroupId());
			if (group != null) {
				this.mname = "[" + group.getName() + "_" + rule.getName() + "]";
			}
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

	public UserBetsPlan getBean() {
		return bean;
	}

	public void setBean(UserBetsPlan bean) {
		this.bean = bean;
	}

}