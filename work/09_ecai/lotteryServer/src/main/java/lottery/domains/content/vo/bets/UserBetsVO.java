package lottery.domains.content.vo.bets;

import javautils.date.Moment;
import lottery.domains.content.entity.Lottery;
import lottery.domains.content.entity.LotteryPlayRules;
import lottery.domains.content.entity.LotteryPlayRulesGroup;
import lottery.domains.content.entity.UserBets;
import lottery.domains.content.vo.user.UserVO;
import lottery.domains.pool.DataFactory;

public class UserBetsVO {

	private String username;
	private String lottery;
	private String mname;
	private UserBets bean;
	private boolean allowCancel = false;

	public UserBetsVO(UserBets bean, DataFactory dataFactory) {
		this.bean = bean;
		UserVO tmpUser = dataFactory.getUser(bean.getUserId());
		if(tmpUser != null) {
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
		Moment thisTime = new Moment();
		Moment stopTime = new Moment().fromTime(bean.getStopTime());
		if (bean.getStatus() == 0 && thisTime.lt(stopTime)) {
			allowCancel = true;
		}
		// 急速秒秒彩屏蔽期号
		if ("jsmmc".equalsIgnoreCase(lottery.getShortName())) {
			bean.setExpect("");
		}
		bean.setIp(null);
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

	public UserBets getBean() {
		return bean;
	}

	public void setBean(UserBets bean) {
		this.bean = bean;
	}

	public boolean isAllowCancel() {
		return allowCancel;
	}

	public void setAllowCancel(boolean allowCancel) {
		this.allowCancel = allowCancel;
	}
}