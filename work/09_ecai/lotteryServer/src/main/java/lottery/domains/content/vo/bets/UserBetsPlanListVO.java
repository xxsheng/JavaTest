package lottery.domains.content.vo.bets;

import javautils.StringUtil;
import lottery.domains.content.entity.*;
import lottery.domains.content.vo.user.UserVO;
import lottery.domains.pool.DataFactory;

public class UserBetsPlanListVO {

	private String username;
	private String lottery;
	private String mname;
	private UserBetsPlan planBean;
	private UserPlanInfo infoBean;
	
	private String encryptUsername(String username) {
		if(StringUtil.isNotNull(username)) {
			return username.substring(0, 3) + "***";
		}
		return null;
	}
	
	public UserBetsPlanListVO(UserBetsPlan planBean, UserPlanInfo infoBean, DataFactory dataFactory) {
		this.planBean = planBean;
		this.infoBean = infoBean;
		UserVO tmpUser = dataFactory.getUser(planBean.getUserId());
		if(tmpUser != null) {
			this.username = encryptUsername(tmpUser.getUsername());
		}
		Lottery lottery = dataFactory.getLottery(planBean.getLotteryId());
		if (lottery != null) {
			this.lottery = lottery.getShowName();
		}
		LotteryPlayRules rule = dataFactory.getLotteryPlayRules(lottery.getId(), planBean.getRuleId());
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

	public UserBetsPlan getPlanBean() {
		return planBean;
	}

	public void setPlanBean(UserBetsPlan planBean) {
		this.planBean = planBean;
	}

	public UserPlanInfo getInfoBean() {
		return infoBean;
	}

	public void setInfoBean(UserPlanInfo infoBean) {
		this.infoBean = infoBean;
	}
	
}