package lottery.web.content.validate;

import javautils.StringUtil;
import javautils.array.ArrayUtils;
import lottery.domains.content.entity.User;
import lottery.domains.content.global.Global;
import lottery.web.WebJSON;
import lottery.web.content.utils.UserCodePointUtil;
import lottery.web.helper.session.SessionUser;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class UserProxyValidate {

	@Autowired
	private UserCodePointUtil uCodePointUtil;

	/**
	 * 判断是否是直属下级
	 */
	public boolean isDirectLower(User uBean, User tmpUser) {
		if(tmpUser == null) return false;
		if(tmpUser.getUpid() == 0) return false;
		return tmpUser.getUpid() == uBean.getId();
	}

	/**
	 * 判断是否是直属下级
	 */
	public boolean isDirectLower(SessionUser uBean, User tmpUser) {
		if(tmpUser == null) return false;
		if(tmpUser.getUpid() == 0) return false;
		return tmpUser.getUpid() == uBean.getId();
	}

	/**
	 * 是否是下级用户
	 */
	public boolean isLowerUser(User uBean, User tmpUser) {
		if(tmpUser == null) return false;
		String upids = tmpUser.getUpids();
		if(!StringUtil.isNotNull(upids)) return false;
		if(upids.indexOf("[" + uBean.getId() + "]") != -1) {
			return true;
		}
		return false;
	}


	/**
	 * 是否是下级用户
	 */
	public boolean isLowerUser(SessionUser uBean, User tmpUser) {
		if(tmpUser == null) return false;
		String upids = tmpUser.getUpids();
		if(!StringUtil.isNotNull(upids)) return false;
		if(upids.indexOf("[" + uBean.getId() + "]") != -1) {
			return true;
		}
		return false;
	}

	/**
	 * 是否是二级下级用户
	 */
	// public boolean isLowerUserInSecondLevels(User parentUser, User lowerUser) {
	// 	if(parentUser == null || lowerUser == null) return false;
    //
	// 	if (!isLowerUser(parentUser, lowerUser)) {
	// 		return false;
	// 	}
    //
	// 	int upid = parentUser.getId();
	// 	String upidsStr = lowerUser.getUpids();
    //
	// 	String sstr = ArrayUtils.deleteInsertIds(upidsStr, upid, true);
	// 	if (StringUtils.isEmpty(sstr)) {
	// 		return true;
	// 	}
    //
	// 	int levels = ArrayUtils.transGetIds(sstr).length+1;
    //
	// 	return levels > 2 ? false : true;
	// }

	/**
	 * 是否有关联关系
	 * 1：判断是否是自己的返点关联下级
	 * 2：判断是否是关联会员或或团队成员(relatedUsers)
	 */
	public boolean isRelated(User upUser, User lowerUser) {
		if(upUser == null || lowerUser == null) return false;

		if (StringUtils.isEmpty(lowerUser.getUpids())) return false;

		// 是否是返点关联下级或返点关联下级的团队成员
		if (isRelatedLowers(upUser, lowerUser)) {
			return true;
		}

		// 是否是关联会员或关联会员的团队成员
		if (isRelatedUsers(upUser, lowerUser)) {
			return true;
		}

		return false;
	}

	/**
	 * 判断是否是自己的返点关联下级
	 */
	public boolean isRelatedLowers(User upUser, User lowerUser) {
		if(upUser == null || lowerUser == null) return false;

		if (StringUtils.isEmpty(lowerUser.getUpids())) return false;

		// 是否是返点关联下级或返点关联下级的团队成员
		{
			// 是否是直接关联下级
			if (lowerUser.getRelatedUpid() == upUser.getId()) return true;

			// 是否是关联下级的团队成员
			if (StringUtils.isNotEmpty(upUser.getRelatedLowers())) {
				int[] lowerUpids = ArrayUtils.transGetIds(lowerUser.getUpids()); // 下级的所有上级ID

				int[] relatedLowerIds = ArrayUtils.transGetIds(upUser.getRelatedLowers()); // 上级用户所有关联下级ID

				for (int lowerUpid : lowerUpids) {
					for (int relatedLowerId : relatedLowerIds) {
						if (lowerUpid == relatedLowerId) {
							return true;
						}
					}
				}
			}
		}

		return false;
	}

	/**
	 * 判断是否是关联会员或或团队成员
	 */
	public boolean isRelatedUsers(User upUser, User lowerUser) {
		if(upUser == null || lowerUser == null) return false;

		if (StringUtils.isEmpty(lowerUser.getUpids())) return false;

		// 是否是关联会员或关联会员的团队成员
		{
			// 是否是直接关联会员
			String relatedUserIds = upUser.getRelatedUsers();
			if(!StringUtil.isNotNull(relatedUserIds)) return false;
			if(relatedUserIds.indexOf("[" + lowerUser.getId() + "]") > -1) {
				return true;
			}

			// 是否是关联会员的团队成员
			String upids = lowerUser.getUpids();
			if(!StringUtil.isNotNull(upids)) return false;
			String[] upidsArr = upids.replaceAll("\\[|\\]", "").split(",");
			for (String upid : upidsArr) {
				if(relatedUserIds.indexOf("[" + upid + "]") != -1) {
					return true;
				}
			}
		}

		return false;
	}

	/**
	 * 给下级充值的时候验证参数
	 */
	public boolean validateWithdrawToLowerUser(WebJSON json, String username, double amount) {
		if(!StringUtil.isNotNull(username)) {
			json.set(2, "2-1000");
			return false;
		}
		if(amount <= 0) {
			json.set(2, "2-1017");
			return false;
		}
		return true;
	}

	/**
	 * 验证下级开户返点
	 */
	public boolean testLowerPoint(WebJSON json, User uBean, int type, double locatePoint) {
		// 如果是玩家
		if(type == Global.USER_TYPE_PLAYER) {
			if(locatePoint < 0) {
				json.set(2, "2-2005");
				return false;
			}
		}
		// 如果是代理
		if(type == Global.USER_TYPE_PROXY) {
			if(locatePoint < 0) {
				json.set(2, "2-2005");
				return false;
			}
		}

		// 不允许创建1958
		if (locatePoint > uBean.getLocatePoint() || locatePoint < 0) {
			json.set(2, "2-2022");
			return false;
		}

		// 允许同级开号
		if(uBean.getAllowEqualCode() == 1) {
			if(locatePoint > uBean.getLocatePoint()) {
				json.set(2, "2-2008");
				return false;
			}
		} else {
			if(locatePoint >= uBean.getLocatePoint()) {
				json.set(2, "2-2008");
				return false;
			}
		}
		return true;
	}

	/**
	 * 验证编辑下级配额
	 */
	public boolean testEditLowerQuota(WebJSON json, User uBean, User lBean, int[] amount) {
		return uCodePointUtil.hasQuota(uBean, lBean.getCode());
		// // 我的信息
		// List<UserCodeQuotaVO> uQuota = uCodePointUtil.listSurplusQuota(uBean);
		// // 下级信息
		// List<UserCodeQuotaVO> lQuota = uCodePointUtil.listSurplusQuota(lBean);
        //
		// double maxLocatePoint = lBean.getLocatePoint();
		// if(lBean.getAllowEqualCode() != 1) {
		// 	maxLocatePoint = MathUtil.subtract(maxLocatePoint, 0.1);
		// }
		// for (int i = 0; i < lQuota.size(); i++) {
		// 	if(maxLocatePoint >= lQuota.get(i).getMinPoint()) {
		// 		// 说明要扣除自己的配额
		// 		if(amount[i] > 0) {
		// 			if(uQuota.get(i).getSurplusCount() - amount[i] < 0) {
		// 				json.set(2, "2-2011");
		// 				return false;
		// 			}
		// 		}
		// 		// 说明要回收下级的配额
		// 		if(amount[i] < 0) {
		// 			if(lQuota.get(i).getSurplusCount() + amount[i] < 0) {
		// 				json.set(2, "2-2021");
		// 				return false;
		// 			}
		// 		}
		// 	} else {
		// 		if(amount[i] != 0) {
		// 			json.set(2, "2-2019");
		// 			return false;
		// 		}
		// 	}
		// }
		// return true;
	}

	public static void main(String[] args) {
		int userId = 304;
		String upidsStr = "[305],[304],[303],[302],[301],[72]";

		String sstr = ArrayUtils.deleteInsertIds(upidsStr, userId, true);
		System.out.println(ArrayUtils.transGetIds(sstr).length+1);

		// int[] upids = ArrayUtils.transGetIds(upidsStr);
        //
		// for (int i = 0; i < upids.length; i++) {
		// 	if (upids[i] == parentUser.getId()) {
		// 		return i + 1;
		// 	}
		// }
        //
		// return -1;
	}

}