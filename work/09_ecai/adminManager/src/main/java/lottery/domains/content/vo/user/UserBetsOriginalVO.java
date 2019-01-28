package lottery.domains.content.vo.user;

import javautils.encrypt.UserBetsEncrypt;
import lottery.domains.content.entity.Lottery;
import lottery.domains.content.entity.LotteryPlayRules;
import lottery.domains.content.entity.LotteryPlayRulesGroup;
import lottery.domains.content.entity.UserBetsOriginal;
import lottery.domains.pool.LotteryDataFactory;

public class UserBetsOriginalVO {

	private String username;
	private String lottery;
	private String mname;
	private UserBetsOriginal bean;
	private String currIdentification;
	private int manipulate;

	public UserBetsOriginalVO(UserBetsOriginal bean, LotteryDataFactory lotteryDataFactory, boolean isShowNum) {
		this.bean = bean;
		UserVO tmpUser = lotteryDataFactory.getUser(bean.getUserId());
		if(tmpUser != null) {
			this.username = tmpUser.getUsername();
		}
		Lottery lottery = lotteryDataFactory.getLottery(bean.getLotteryId());
		if (lottery != null) {
			this.lottery = lottery.getShowName();
		}
		LotteryPlayRules playRules = lotteryDataFactory.getLotteryPlayRules(bean.getRuleId());
		if(playRules != null) {
			LotteryPlayRulesGroup group = lotteryDataFactory.getLotteryPlayRulesGroup(playRules.getGroupId());
			if (group != null) {
				this.mname = "[" + group.getName() + "_" + playRules.getName() + "]";
			}
		}
		String cer = UserBetsEncrypt.decryptCertification(bean.getCertification());
		this.currIdentification = UserBetsEncrypt.getIdentification(bean, cer);
		this.manipulate = this.currIdentification.equals(bean.getIdentification()) ? 0 : 1; // 0：false；1：true
		bean.setCertification(null);
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

	public UserBetsOriginal getBean() {
		return bean;
	}

	public void setBean(UserBetsOriginal bean) {
		this.bean = bean;
	}

	public String getCurrIdentification() {
		return currIdentification;
	}

	public void setCurrIdentification(String currIdentification) {
		this.currIdentification = currIdentification;
	}

	public int getManipulate() {
		return manipulate;
	}

	public void setManipulate(int manipulate) {
		this.manipulate = manipulate;
	}
}