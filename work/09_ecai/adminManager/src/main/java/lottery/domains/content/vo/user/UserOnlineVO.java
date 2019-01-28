package lottery.domains.content.vo.user;

import java.util.ArrayList;
import java.util.List;

import javautils.StringUtil;
import lottery.domains.content.entity.User;
import lottery.domains.pool.LotteryDataFactory;

public class UserOnlineVO {

	private String username;
	private double totalMoney;
	private double lotteryMoney;
	private double baccaratMoney;
	private List<String> levelUsers = new ArrayList<>();
	private String loginTime;

	public UserOnlineVO(User bean, LotteryDataFactory lotteryDataFactory) {
		this.username = bean.getUsername();
		this.totalMoney = bean.getTotalMoney();
		this.lotteryMoney = bean.getLotteryMoney();
		this.baccaratMoney = bean.getBaccaratMoney();
		this.loginTime = bean.getLoginTime();
		// 查询层级关系
		if (StringUtil.isNotNull(bean.getUpids())) {
			String[] ids = bean.getUpids().replaceAll("\\[|\\]", "").split(",");
			for (String id : ids) {
				UserVO thisUser = lotteryDataFactory.getUser(Integer.parseInt(id));
				if (thisUser != null) {
					this.levelUsers.add(thisUser.getUsername());
				}
			}
		}
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public double getTotalMoney() {
		return totalMoney;
	}

	public void setTotalMoney(double totalMoney) {
		this.totalMoney = totalMoney;
	}

	public double getLotteryMoney() {
		return lotteryMoney;
	}

	public void setLotteryMoney(double lotteryMoney) {
		this.lotteryMoney = lotteryMoney;
	}

	public double getBaccaratMoney() {
		return baccaratMoney;
	}

	public void setBaccaratMoney(double baccaratMoney) {
		this.baccaratMoney = baccaratMoney;
	}

	public List<String> getLevelUsers() {
		return levelUsers;
	}

	public void setLevelUsers(List<String> levelUsers) {
		this.levelUsers = levelUsers;
	}

	public String getLoginTime() {
		return loginTime;
	}

	public void setLoginTime(String loginTime) {
		this.loginTime = loginTime;
	}

}