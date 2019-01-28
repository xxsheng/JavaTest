package lottery.domains.content.vo.user;

import javautils.StringUtil;
import lottery.domains.content.entity.User;
import lottery.domains.content.global.Global;
import lottery.domains.pool.LotteryDataFactory;
import lottery.web.content.utils.UserCodePointUtil;
import org.apache.commons.lang.math.NumberUtils;

import java.util.LinkedList;
import java.util.List;

public class UserProfileVO {

	private String upUser; // 直属上级
	private List<String> levelUsers = new LinkedList<>(); // 层级关系
	private List<String> relatedUsers = new LinkedList<>(); // 关联用户
	private String relatedUpUser; // 关联上级
	private int lowerUsers; // 下级人数
	private User bean;
	private boolean isZhaoShang; // 是否是招商或超级招商

	public UserProfileVO(User bean, List<User> lowerUsers, LotteryDataFactory df, UserCodePointUtil uCodePointUtil) {
		this.bean = bean;
		this.bean.setPassword("***");
		this.bean.setSecretKey("***");
		if(StringUtil.isNotNull(this.bean.getWithdrawPassword())) {
			this.bean.setWithdrawPassword("***");
		}
		if(StringUtil.isNotNull(this.bean.getImgPassword())) {
			this.bean.setImgPassword("***");
		}
		this.lowerUsers = lowerUsers.size();
		// 查询上级用户
		if (bean.getUpid() != 0) {
			UserVO user = df.getUser(bean.getUpid());
			this.upUser = user.getUsername();
		}
		// 查询层级关系
		if (StringUtil.isNotNull(bean.getUpids())) {
			String[] ids = bean.getUpids().replaceAll("\\[|\\]", "").split(",");
			for (String id : ids) {
				UserVO user = df.getUser(Integer.parseInt(id));
				if (user != null) {
					this.levelUsers.add(user.getUsername());
				} else {
					this.levelUsers.add("unknown");
				}
			}
		}

		if (bean.getRelatedUpid() != 0) {
			UserVO user = df.getUser(bean.getRelatedUpid());
			if (user != null) {
				this.relatedUpUser = user.getUsername();
			}
		}

		// 关联账号
		if (this.bean.getType() == Global.USER_TYPE_RELATED && StringUtil.isNotNull(this.bean.getRelatedUsers())) {
			String[] ids = bean.getRelatedUsers().replaceAll("\\[|\\]", "").split(",");
			for (String id : ids) {
				if (NumberUtils.isDigits(id)) {
					UserVO relatedUser = df.getUser(Integer.valueOf(id));
					if (relatedUser != null) {
						this.relatedUsers.add(relatedUser.getUsername());
					}
				}
			}
		}

		isZhaoShang = uCodePointUtil.isLevel2Proxy(bean);
	}
	
	public String getUpUser() {
		return upUser;
	}

	public void setUpUser(String upUser) {
		this.upUser = upUser;
	}

	public List<String> getLevelUsers() {
		return levelUsers;
	}

	public void setLevelUsers(List<String> levelUsers) {
		this.levelUsers = levelUsers;
	}

	public String getRelatedUpUser() {
		return relatedUpUser;
	}

	public void setRelatedUpUser(String relatedUpUser) {
		this.relatedUpUser = relatedUpUser;
	}

	public int getLowerUsers() {
		return lowerUsers;
	}

	public void setLowerUsers(int lowerUsers) {
		this.lowerUsers = lowerUsers;
	}

	public User getBean() {
		return bean;
	}

	public void setBean(User bean) {
		this.bean = bean;
	}

	public List<String> getRelatedUsers() {
		return relatedUsers;
	}

	public void setRelatedUsers(List<String> relatedUsers) {
		this.relatedUsers = relatedUsers;
	}

	public boolean isZhaoShang() {
		return isZhaoShang;
	}

	public void setZhaoShang(boolean zhaoShang) {
		isZhaoShang = zhaoShang;
	}
}