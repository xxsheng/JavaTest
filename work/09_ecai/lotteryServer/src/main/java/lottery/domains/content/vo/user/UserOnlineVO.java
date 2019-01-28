package lottery.domains.content.vo.user;

import java.util.ArrayList;
import java.util.List;

import javautils.StringUtil;
import javautils.array.ArrayUtils;
import lottery.domains.content.entity.User;
import lottery.domains.pool.DataFactory;

public class UserOnlineVO {

	private String username;
	private String nickname;
	private int type;
	private double totalMoney;
	private double lotteryMoney;
	private double baccaratMoney;
	private List<String> levelUsers = new ArrayList<>();
	private String loginTime;

	public UserOnlineVO(User bean, int thisId, DataFactory dataFactory) {
		this.username = bean.getUsername();
		this.nickname = bean.getNickname();
		this.type = bean.getType();
		this.totalMoney = bean.getTotalMoney();
		this.lotteryMoney = bean.getLotteryMoney();
		this.baccaratMoney = bean.getBaccaratMoney();
		this.loginTime = bean.getLoginTime();
		// 查询层级关系
		// // 第一层显示用户自己
		// UserVO user = dataFactory.getUser(thisId);
		// if (user != null) {
		// 	this.levelUsers.add(bean.getUsername());
		// }
		// if (StringUtil.isNotNull(bean.getUpids())) {
		// 	String visibleUpids = ArrayUtils.deleteInsertIds(bean.getUpids(), thisId, true);
		// 	if (StringUtil.isNotNull(visibleUpids)) {
		// 		String[] ids = visibleUpids.replaceAll("\\[|\\]", "").split(",");
		// 		for (int i = ids.length - 1; i >= 0; i--) {
		// 			UserVO thisUser = dataFactory.getUser(Integer.parseInt(ids[i]));
		// 			if (thisUser != null) {
		// 				this.levelUsers.add(thisUser.getUsername());
		// 			}
		// 		}
		// 	}
		// }
		if (bean.getId() == thisId) {
			// this.levelUsers.add(bean.getUsername());
		}
		else {
			// 查询层级关系
			if (StringUtil.isNotNull(bean.getUpids())) {
				String[] ids = bean.getUpids().replaceAll("\\[|\\]", "").split(",");
				for (String id : ids) {
					UserVO thisUser = dataFactory.getUser(Integer.parseInt(id));
					if (thisUser != null) {
						this.levelUsers.add(thisUser.getUsername());
					}
					// 只能显示到直属上级
					if (thisId == Integer.parseInt(id)) break;
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

	public String getNickname() {
		return nickname;
	}

	public void setNickname(String nickname) {
		this.nickname = nickname;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
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