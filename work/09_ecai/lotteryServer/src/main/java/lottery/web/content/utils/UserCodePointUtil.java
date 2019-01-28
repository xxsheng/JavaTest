package lottery.web.content.utils;

import javautils.array.ArrayUtils;
import javautils.date.Moment;
import javautils.math.MathUtil;
import lottery.domains.content.biz.UserCodeQuotaService;
import lottery.domains.content.biz.UserService;
import lottery.domains.content.biz.read.UserLotteryReportReadService;
import lottery.domains.content.biz.read.UserReadService;
import lottery.domains.content.entity.SysCodeAmount;
import lottery.domains.content.entity.User;
import lottery.domains.content.entity.UserCodeQuota;
import lottery.domains.content.global.Global;
import lottery.domains.content.vo.config.CodeConfig;
import lottery.domains.content.vo.user.*;
import lottery.domains.pool.DataFactory;
import lottery.web.WebJSON;
import lottery.web.content.validate.UserProxyValidate;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

@Component
public class UserCodePointUtil {
	
	@Autowired
	private UserService uService;

	@Autowired
	private UserReadService uReadService;

	@Autowired
	private UserCodeQuotaService uCodeQuotaService;

	@Autowired
	private UserProxyValidate uProxyValidate;

	@Autowired
	private DataFactory dataFactory;

	@Autowired
	private UserLotteryReportReadService uLotteryReportReadService;
	
	/**
	 * 获取用户账号级别
	 */
	public int getUserCode(double locatePoint) {
		CodeConfig config = dataFactory.getCodeConfig();
		return config.getSysCode() - (int) (MathUtil.subtract(config.getSysLp(), locatePoint) * 20);
	}
	
	/**
	 * 获取定位返点
	 */
	public double getLocatePoint(int code) {
		CodeConfig config = dataFactory.getCodeConfig();
		double result = config.getSysLp() - (config.getSysCode() - code) / 20.0;
		return new BigDecimal(String.valueOf(result)).setScale(2,BigDecimal.ROUND_HALF_UP).doubleValue();
	}

	/**
	 * 获取不定位返点
	 */
	public double getNotLocatePoint(int code) {
		CodeConfig config = dataFactory.getCodeConfig();
		double result = config.getSysNlp() - (config.getSysCode() - code) / 20.0;
		return result < 0 ? 0 : new BigDecimal(String.valueOf(result)).setScale(2,BigDecimal.ROUND_HALF_UP).doubleValue();
	}
	
	/**
	 * 获取不定位返点
	 */
	public double getNotLocatePoint(double locatePoint) {
		return getNotLocatePoint(getUserCode(locatePoint));
	}

	/**
	 * 检查按量升点
	 */
	public boolean checkCodeAmount(WebJSON json, int beforeCode, int afterCode, int userId) {
		if (beforeCode >= afterCode) {
			return true;
		}

		SysCodeAmount codeAmount = getCodeAmount(afterCode);
		if (codeAmount == null) {
			return true;
		}

		double dailyBillingOrderRequires = codeAmount.getDailyBillingOrder();
		if (dailyBillingOrderRequires <= 0) {
			return true;
		}

		String sDate = new Moment().subtract(codeAmount.getDays(), "days").toSimpleDate();
		String eDate = new Moment().add(1, "days").toSimpleDate();

		UserLotteryReportTeamStatisticsVO statistics = uLotteryReportReadService.statisticsByTeam(userId, sDate, eDate);

		// 团队总消费
		double totalBillingOrder = statistics == null ? 0: statistics.getTotalLotteryBillingOrder();

		// 日均消费
		double dailyBillingOrder = MathUtil.divide(totalBillingOrder, codeAmount.getDays(), 3);

		if (dailyBillingOrder < dailyBillingOrderRequires) {
			int dailyBillingOrderRequiresInt = new BigDecimal(dailyBillingOrderRequires).intValue();
			int dailyBillingOrderInt = new BigDecimal(dailyBillingOrder).intValue();
			json.set(2, "2-1064", beforeCode, afterCode, codeAmount.getDays(), dailyBillingOrderRequiresInt, dailyBillingOrderInt);
			return false;
		}
		return true;
	}

	/**
	 * 获取按量升点配置
	 */
	public SysCodeAmount getCodeAmount(int code) {
		List<SysCodeAmount> sysCodeAmounts = dataFactory.listSysCodeAmount();
		if (CollectionUtils.isEmpty(sysCodeAmounts)) {
			return null;
		}

		for (SysCodeAmount sysCodeAmount : sysCodeAmounts) {
			if (sysCodeAmount.getCode() == code) {
				return sysCodeAmount;
			}
		}

		return null;
	}


	/**
	 * 获取用户配额信息,返回空表示不限制
	 */
	public List<UserCodeQuotaVO> listUserQuota(User uBean) {
		if (uBean.getId() == Global.USER_TOP_ID) {
			// 总账不限制
			return new ArrayList<>();
		}

		List<SysCodeRangeVO> sysCodeRanges = dataFactory.getCodeConfig().getSysCodeRange(); // 系统默认
		List<UserCodeQuotaVO> sysQuotaList = new LinkedList<>(); // 最终返回
		List<UserCodeQuota> uQuotas = uCodeQuotaService.list(uBean.getId()); // 已配置

		List<Integer> allocateCodes = new ArrayList<>();

		for (int i = 0; i < sysCodeRanges.size(); i++) {
			SysCodeRangeVO range = sysCodeRanges.get(i);

			if (range.getAccountCode() == uBean.getCode()) { // 匹配系统配置账号等级
				UserCodeQuotaVO quota = new UserCodeQuotaVO();
				quota.setCode(range.getAllocateCode()); // 开号等级
				quota.setUserId(uBean.getId());

				boolean configured = false;
				for (UserCodeQuota uQuota : uQuotas) {
					if (uQuota.getCode() == range.getAllocateCode()) {
						// 查看是否已经有配置
						configured = true;
						quota.setSysAllocateQuantity(uQuota.getSysAllocateQuantity());
						quota.setUpAllocateQuantity(uQuota.getUpAllocateQuantity());
						quota.setSurplus(uQuota.getSysAllocateQuantity()+uQuota.getUpAllocateQuantity());
						break;
					}
				}

				if (configured == false) {
					// 没有配置，取系统默认
					quota.setSysAllocateQuantity(range.getQuantity());
					quota.setSurplus(range.getQuantity());
				}

				sysQuotaList.add(quota);
				allocateCodes.add(range.getAllocateCode());
			}
		}

		if (CollectionUtils.isEmpty(sysQuotaList)) {
			return new ArrayList<>();
		}

		// 获取直属下级数量、下级分配数量、剩余可用

		// 减去开户的
		List<User> lowerList = uReadService.getUserDirectLowerFromRead(uBean.getId(), ArrayUtils.listToInt(allocateCodes)); // 获取直属下级
		if (CollectionUtils.isNotEmpty(lowerList)) {
			List<Integer> lowerIds = new ArrayList<>();
			for (User user : lowerList) {
				lowerIds.add(user.getId());

				for (UserCodeQuotaVO userCodeQuotaVO : sysQuotaList) {
					if (user.getCode() == userCodeQuotaVO.getCode()) {
						userCodeQuotaVO.setSurplus(userCodeQuotaVO.getSurplus()-1);
					}
				}
			}

			// 减去分配给下级的
			List<UserCodeQuota> lowerQuotas = uCodeQuotaService.list(ArrayUtils.listToInt(lowerIds)); // 获取直属下级分配数据
			for (UserCodeQuotaVO userCodeQuotaVO : sysQuotaList) {
				for (UserCodeQuota lowerQuota : lowerQuotas) {
					if (lowerQuota.getUpAllocateQuantity() > 0 && userCodeQuotaVO.getCode() == lowerQuota.getCode()) {
						userCodeQuotaVO.setSurplus(userCodeQuotaVO.getSurplus()-lowerQuota.getUpAllocateQuantity());
					}
				}
			}
		}

		return sysQuotaList;
	}

	/**
	 * 返回指定开号等级的配额信息，返回空表示不限制
	 */
	public UserCodeQuotaVO getQuota(User uBean, int code) {
		if (uBean.getId() == Global.USER_TOP_ID) {
			// 总账不限制
			return null;
		}

		List<SysCodeRangeVO> sysCodeRanges = dataFactory.getCodeConfig().getSysCodeRange(); // 系统默认
		UserCodeQuotaVO quota = new UserCodeQuotaVO(); // 最终返回
		UserCodeQuota uQuota = uCodeQuotaService.get(uBean.getId(), code); // 已配置

		quota.setCode(code); // 开号等级
		quota.setUserId(uBean.getId());

		if (uQuota != null) {
			quota.setSysAllocateQuantity(uQuota.getSysAllocateQuantity());
			quota.setUpAllocateQuantity(uQuota.getUpAllocateQuantity());
			quota.setSurplus(uQuota.getSysAllocateQuantity() + uQuota.getUpAllocateQuantity());
		}
		else {
			// 没有配置，取系统默认
			boolean hasConfig = false;
			for (SysCodeRangeVO range : sysCodeRanges) {
				if (range.getAccountCode() == uBean.getCode() && range.getAllocateCode() == code) { // 匹配系统配置账号等级
					quota.setSysAllocateQuantity(range.getQuantity());
					quota.setSurplus(range.getQuantity());
					hasConfig = true;
					break;
				}
			}
			if (hasConfig == false) {
				return null;
			}
		}

		// 获取直属下级数量、下级分配数量、剩余可用
		int quantity = quota.getSysAllocateQuantity() + quota.getUpAllocateQuantity();
		if (quantity  > 0) {
			List<User> lowerList = uReadService.getUserDirectLowerFromRead(uBean.getId(), code); // 获取直属下级
			if (CollectionUtils.isNotEmpty(lowerList)) {
				// 减去开户的
				List<Integer> lowerIds = new ArrayList<>();
				for (User user : lowerList) {
					lowerIds.add(user.getId());
				}
				quota.setSurplus(quota.getSurplus() - lowerList.size());

				// 减去分配给下级的
				List<UserCodeQuota> lowerQuotas = uCodeQuotaService.list(ArrayUtils.listToInt(lowerIds)); // 获取直属下级分配数据
				for (UserCodeQuota lowerQuota : lowerQuotas) {
					if (lowerQuota.getUpAllocateQuantity() > 0) {
						quota.setSurplus(quota.getSurplus() - lowerQuota.getUpAllocateQuantity());
					}
				}
			}
		}

		return quota;
	}

	/**
	 * 检查是否有配额
	 */
	public boolean hasQuota(User uBean, int code) {
		UserCodeQuotaVO quota = getQuota(uBean, code);
		if (quota == null) {
			return true;
		}

		int quantity = quota.getSysAllocateQuantity() + quota.getUpAllocateQuantity();
		if (quantity > 0 && quota.getSurplus() > 0) {
			return true;
		}

		return false;
	}

	/**
	 * 获取可分配配额区间
	 */
	public UserCodeRangeVO getUserCodeRange(User uBean) {
		// 是否同级开户
		double maxLocatePoint = uBean.getLocatePoint();
		/// 如果不允许同级开号
		if(uBean.getAllowEqualCode() != 1) {
			maxLocatePoint = MathUtil.subtract(maxLocatePoint, 0.1);
		}
		int code = getUserCode(maxLocatePoint);
		double maxNotLocatePoint = getNotLocatePoint(maxLocatePoint);

		int maxLocatePointCode = getUserCode(maxNotLocatePoint);//转换为账户级别

		return new UserCodeRangeVO(code, maxLocatePoint, maxNotLocatePoint, maxLocatePointCode);
	}

	public List<String> getUserLevels(User currentUser, User searchUser) {
		List<String> userLevels = new LinkedList<>();
		userLevels.add(currentUser.getUsername()); // 第一个是自己

		if (searchUser == null || currentUser.getUsername().equalsIgnoreCase(searchUser.getUsername())) {
			return userLevels;
		}

		if (StringUtils.isEmpty(searchUser.getUpids())) {
			return userLevels;
		}

		String upids = null;
		// 直属下级
		if (uProxyValidate.isLowerUser(currentUser, searchUser)) {
			upids = ArrayUtils.deleteInsertIds(searchUser.getUpids(), currentUser.getId(), true);
		}
		// 关联下级
		else if (uProxyValidate.isRelatedLowers(currentUser, searchUser)) {
			int relatedUpid = 0;

			// 找出是通过哪个会员进行关联的
			int[] relatedLowerIds = ArrayUtils.transGetIds(currentUser.getRelatedLowers());
			int[] upidArr = ArrayUtils.transGetIds(searchUser.getUpids());
			for (int upid : upidArr) {
				for (int relatedLowerId : relatedLowerIds) {
					if (upid == relatedLowerId) {
						relatedUpid = relatedLowerId;
						UserVO lowerUser = dataFactory.getUser(relatedLowerId);
						if (lowerUser != null) {
							userLevels.add(lowerUser.getUsername());
						}
						break;
					}
				}
			}

			if (relatedUpid != 0) {
				upids = ArrayUtils.deleteInsertIds(searchUser.getUpids(), relatedUpid, true);
			}
		}
		// 关联会员
		else if (uProxyValidate.isRelatedUsers(currentUser, searchUser)) {
			int relatedUpid = 0;

			// 找出是通过哪个会员进行关联的
			int[] relatedUserIds = ArrayUtils.transGetIds(currentUser.getRelatedUsers());
			int[] upidArr = ArrayUtils.transGetIds(searchUser.getUpids());
			for (int upid : upidArr) {
				for (int relatedUserId : relatedUserIds) {
					if (upid == relatedUserId) {
						relatedUpid = relatedUserId;
						UserVO lowerUser = dataFactory.getUser(relatedUserId);
						if (lowerUser != null) {
							userLevels.add(lowerUser.getUsername());
						}
						break;
					}
				}
			}

			if (relatedUpid != 0) {
				upids = ArrayUtils.deleteInsertIds(searchUser.getUpids(), relatedUpid, true);
			}
		}

		if (StringUtils.isNotEmpty(upids)) {
			int[] upidArr = ArrayUtils.transGetIds(upids);

			for (int i = upidArr.length - 1; i >= 0; i--) {
				UserVO lowerUser = dataFactory.getUser(upidArr[i]);
				if (lowerUser != null) {
					userLevels.add(lowerUser.getUsername());
				}
			}
		}

		userLevels.add(searchUser.getUsername());
		return userLevels;
	}

	/**
	 * 等级1代理：系统最高等级 & 上级是总账(72) （等级1用户一般是指主管或内部招商   主管|内部招商 -> 招商 -> 直属）
	 */
	public boolean isLevel1Proxy(User uBean) {
		if (uBean.getType() == Global.USER_TYPE_PROXY
//				&& uBean.getCode() == dataFactory.getCodeConfig().getSysCode()
				&& uBean.getUpid() == Global.USER_TOP_ID) {
			return true;
		}
		return false;
	}

	/**
	 * 等级2代理：系统最高等级 & 上级是等级1用户 （等级2一般是指招商   主管|内部招商 -> 招商 -> 直属）
	 */
	public boolean isLevel2Proxy(User uBean) {
		if (uBean.getType() == Global.USER_TYPE_PROXY
                && uBean.getUpid() != 0
                && uBean.getUpid() != Global.USER_TOP_ID) {
            User upBean = uService.getById(uBean.getUpid());
            if (isLevel1Proxy(upBean)) {
                return true;
            }
        }
		return false;
	}

	/**
	 * 等级2代理（招商）：系统最高等级 & 上级是等级1用户
	 */
	public boolean isLevel2ZhaoShangProxy(User uBean) {
		if (uBean.getType() == Global.USER_TYPE_PROXY
				&& uBean.getCode() == dataFactory.getCodeConfig().getSysCode()
				&& uBean.getUpid() != 0
				&& uBean.getUpid() != Global.USER_TOP_ID
				&& uBean.getIsCjZhaoShang() == Global.DAIYU_IS_CJ_ZHAO_SHANG_NO) {
			User upBean = uService.getById(uBean.getUpid());
			if (isLevel1Proxy(upBean)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * 等级2代理（超级招商）：系统最高等级 & 上级是等级1用户
	 */
	public boolean isLevel2CJZhaoShangProxy(User uBean) {
		if (uBean.getType() == Global.USER_TYPE_PROXY
				&& uBean.getCode() == dataFactory.getCodeConfig().getSysCode()
				&& uBean.getUpid() != 0
				&& uBean.getUpid() != Global.USER_TOP_ID
				&& uBean.getIsCjZhaoShang() == Global.DAIYU_IS_CJ_ZHAO_SHANG_YES) {
			User upBean = uService.getById(uBean.getUpid());
			if (isLevel1Proxy(upBean)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * 等级3代理：系统最高等级 & 上级是等级2用户 （等级3用户一般是指直属   主管|内部招商 -> 招商 -> 直属）
	 */
	public boolean isLevel3Proxy(User uBean) {
		if (uBean.getType() == Global.USER_TYPE_PROXY
                && uBean.getUpid() != 0
                && uBean.getUpid() != Global.USER_TOP_ID) {
            User upBean = uService.getById(uBean.getUpid());
            if (isLevel2Proxy(upBean)) {
                return true;
            }
        }
        return false;
	}
}