package lottery.domains.content.biz.impl;

import admin.web.WebJSONObject;
import javautils.StringUtil;
import javautils.array.ArrayUtils;
import javautils.date.Moment;
import javautils.encrypt.PasswordUtil;
import javautils.jdbc.PageList;
import javautils.math.MathUtil;
import javautils.redis.JedisTemplate;
import lottery.domains.content.biz.UserBillService;
import lottery.domains.content.biz.UserDailySettleService;
import lottery.domains.content.biz.UserDividendService;
import lottery.domains.content.biz.UserService;
import lottery.domains.content.dao.*;
import lottery.domains.content.entity.User;
import lottery.domains.content.global.Global;
import lottery.domains.content.vo.user.UserBaseVO;
import lottery.domains.content.vo.user.UserOnlineVO;
import lottery.domains.content.vo.user.UserProfileVO;
import lottery.domains.content.vo.user.UserVO;
import lottery.domains.pool.LotteryDataFactory;
import lottery.web.content.utils.UserCodePointUtil;
import org.apache.commons.lang.StringUtils;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
public class UserServiceImpl implements UserService {
	private static final String USER_LOGOUT_MSG = "USER:LOGOUT:MSG:";

	/**
	 * DAO
	 */
	@Autowired
	private UserDao uDao;

	@Autowired
	private UserBetsDao uBetsDao;

	@Autowired
	private UserCardDao uCardDao;

	@Autowired
	private UserCodeQuotaDao uCodeQuotaDao;

	@Autowired
	private UserSecurityDao uSecurityDao;

	@Autowired
	private UserRegistLinkDao uRegistLinkDao;

	@Autowired
	private UserDividendService uDividendService;

	@Autowired
	private UserDailySettleService uDailySettleService;

	@Autowired
	private LotteryDataFactory lotteryDataFactory;

	@Autowired
	private UserCodePointUtil uCodePointUtil;

	@Autowired
	private UserBillService uBillService;

	@Autowired
	private JedisTemplate jedisTemplate;

	@Autowired
	private LotteryDataFactory dataFactory;

	@Override
	@Transactional(readOnly = true)
	public User getById(int id) {
		return uDao.getById(id);
	}

	@Override
	@Transactional(readOnly = true)
	public User getByUsername(String username) {
		return uDao.getByUsername(username);
	}

	@Override
	public boolean aStatus(String username, int status, String message) {
		User uBean = uDao.getByUsername(username);

		// 0:正常 -1:冻结 -2:永久冻结 -3:禁用
		if (uBean != null && uBean.getAStatus() != -2) {
			if (uBean.getId() == Global.USER_TOP_ID) {
				return false;
			}

			boolean updated = uDao.updateAStatus(uBean.getId(), status, message);
			if (status != 0 && updated && StringUtils.isNotEmpty(uBean.getSessionId())) {
				kickOutUser(uBean.getId(), uBean.getSessionId());
			}
			return updated;
		}
		return false;
	}

	@Override
	public boolean bStatus(String username, int status, String message) {
		User uBean = uDao.getByUsername(username);
		// 0:正常 -1:禁止投注 -2:自动掉线 -3:投注超时
		if (uBean != null) {
			// 账号状态必须正常
			if (uBean.getAStatus() >= 0) {
				return uDao.updateBStatus(uBean.getId(), status, message);
			}
		}
		return false;
	}

	@Override
	public boolean modifyLoginPwd(String username, String password) {
		User uBean = uDao.getByUsername(username);
		if (uBean != null) {
			String md5Pwd = PasswordUtil.generatePasswordByMD5(password); // 密码前台传输时的加密方式：MD5大写(MD5大写(密码明文),调用$.generatePassword(plainStr)即可
			boolean updated = uDao.updateLoginPwd(uBean.getId(), md5Pwd);
			if (updated && StringUtils.isNotEmpty(uBean.getSessionId())) {
				kickOutUser(uBean.getId(), uBean.getSessionId());
			}

			return updated;
		}
		return false;
	}

	@Override
	public boolean modifyWithdrawPwd(String username, String password) {
		User uBean = uDao.getByUsername(username);
		if (uBean != null) {
			String md5Pwd = PasswordUtil.generatePasswordByMD5(password); // 密码前台传输时的加密方式：MD5大写(MD5大写(密码明文),调用$.generatePassword(plainStr)即可
			boolean flag = uDao.updateWithdrawPassword(uBean.getId(), md5Pwd);
			if (flag) {
				// 加时间锁3天 （update by bill 取消修改自己密码锁定）
				// String lockTime = new Moment().add(3, "days").toSimpleTime();
				// uDao.updateLockTime(uBean.getId(), lockTime);
			}
			return flag;
		}
		return false;
	}

	@Override
	public boolean modifyWithdrawName(String username, String withdrawName) {
		User uBean = uDao.getByUsername(username);
		if (uBean != null) {
			boolean flag = uDao.updateWithdrawName(uBean.getId(), withdrawName);
			if (flag) {
				// 加时间锁3天
				String lockTime = new Moment().add(3, "days").toSimpleTime();
				uDao.updateLockTime(uBean.getId(), lockTime);
				// 更新所有银行卡名字
				uCardDao.updateCardName(uBean.getId(), withdrawName);
			}
			return flag;
		}
		return false;
	}

	@Override
	public boolean resetImagePwd(String username) {
		User uBean = uDao.getByUsername(username);
		if (uBean != null) {
			return uDao.updateImgPwd(uBean.getId(), null);
		}
		return false;
	}

	@Override
	public UserProfileVO getUserProfile(String username) {
		User uBean = uDao.getByUsername(username);
		if (uBean != null) {
			List<User> lowerUsers = uDao.getUserLower(uBean.getId());
			UserProfileVO pBean = new UserProfileVO(uBean, lowerUsers, lotteryDataFactory, uCodePointUtil);
			return pBean;
		}
		return null;
	}

	@Override
	public boolean changeLine(int type, String aUser, String bUser) {
		User aBean = uDao.getByUsername(aUser);
		User bBean = uDao.getByUsername(bUser);

		if (aBean != null && bBean != null) {
			if (aBean.getId() == Global.USER_TOP_ID) {
				return false;
			}

			if (type == 0) {
				if (aBean.getCode() <= bBean.getCode()) {
					List<User> uList = uDao.getUserLower(aBean.getId());
					int succ = 0;
					for (User tmpUser : uList) {
						int upid = tmpUser.getUpid();
						// 如果是直属下级
						if (tmpUser.getUpid() == aBean.getId()) {
							upid = bBean.getId();
						}
						String upids = ArrayUtils.deleteInsertIds(tmpUser.getUpids(), aBean.getId(), true);
						// 存在下级代理
						if (StringUtil.isNotNull(upids)) {
							upids += ",";
						} else {
							upids = "";
						}
						// 补上新代理
						upids += "[" + bBean.getId() + "]";
						// 补上新代理的上级代理
						if (bBean.getUpid() != 0) {
							upids += "," + bBean.getUpids();
						}
						boolean flag = uDao.updateProxy(tmpUser.getId(), upid, upids);
						if (flag) {
							succ++;
						}
					}
					boolean updated = succ == uList.size();
					if (updated) {
						// 删除待转移线路下的契约关系
						uDividendService.checkDividend(aBean.getUsername());
						uDailySettleService.checkDailySettle(aBean.getUsername());
					}
					return updated;
				}
			}
			if (type == 1) {
				if (aBean.getCode() <= bBean.getCode()) {
					List<User> uList = uDao.getUserLower(aBean.getId());
					uList.add(aBean); // 加入自己
					int succ = 0;
					for (User tmpUser : uList) {
						int upid = tmpUser.getUpid();
						// 如果是直属下级
						if (tmpUser.getId() == aBean.getId()) {
							upid = bBean.getId();
						}
						String upids = ArrayUtils.deleteInsertIds(tmpUser.getUpids(), aBean.getUpid(), true);
						// 存在下级代理
						if (StringUtil.isNotNull(upids)) {
							upids += ",";
						} else {
							upids = "";
						}
						// 补上新代理
						upids += "[" + bBean.getId() + "]";
						// 补上新代理的上级代理
						if (bBean.getUpid() != 0) {
							upids += "," + bBean.getUpids();
						}
						boolean flag = uDao.updateProxy(tmpUser.getId(), upid, upids);
						if (flag) {
							succ++;
						}
					}
					boolean updated = succ == uList.size();
					if (updated) {
						// 删除待转移线路下的契约关系
						uDividendService.checkDividend(aBean.getUsername());
						uDailySettleService.checkDailySettle(aBean.getUsername());
					}
					return updated;
				}
			}
		}
		return false;
	}

	@Override
	public boolean modifyLotteryPoint(String username, int code, double locatePoint, double notLocatePoint) {
		if (code > dataFactory.getCodeConfig().getSysCode() || code < 1800) {
			return false;
		}
		
		//TODO 1997 1995 特殊处理
		if (code != dataFactory.getCodeConfig().getSysCode() && code % 2 != 0) {
			return false;
		}

		if (code < 1800 || code > dataFactory.getCodeConfig().getSysCode()) {
			return false;
		}

		User uBean = uDao.getByUsername(username);
		if (uBean != null) {
			if (uBean.getId() == Global.USER_TOP_ID) {
				return false;
			}
			
			int BStatus = uBean.getBStatus();
			if(code <= dataFactory.getLotteryConfig().getNotBetPointAccount()){
				BStatus = 0;
			}else{
				BStatus = -1;
			}

			boolean flag = uDao.updateLotteryPoint(uBean.getId(), code, locatePoint, BStatus, notLocatePoint);
			if (flag) {
				// 检查团队契约配置
//				uDividendService.checkDividend(uBean.getUsername());
//				uDailySettleService.checkDailySettle(uBean.getUsername());
			}
			return flag;
		}
		return false;
	}

	@Override
	public boolean downLinePoint(String username) {
		User uBean = uDao.getByUsername(username);

		if (uBean != null) {
			if (uBean.getId() == Global.USER_TOP_ID) {
				return false;
			}

			List<User> teamList = uDao.getUserLower(uBean.getId());
			teamList.add(uBean); // 加入自己
			for (User tmpBean : teamList) {
				int code = tmpBean.getCode();
				double locatePoint = tmpBean.getLocatePoint();
				double notLocatePoint = tmpBean.getNotLocatePoint();
				if (locatePoint > 0) {
					code = code - 2;
					locatePoint = locatePoint - 0.1;
					if (notLocatePoint > 0) {
						notLocatePoint = notLocatePoint - 0.1;
					}

					if (code < 1800) {
						code = 1800;
						locatePoint = uCodePointUtil.getLocatePoint(code);
						notLocatePoint = uCodePointUtil.getNotLocatePoint(code);
					}
				}
				
				int BStatus = tmpBean.getBStatus();
				if(locatePoint <= dataFactory.getLotteryConfig().getNotBetPointAccount()){
					BStatus = 0;
				}else{
					BStatus = -1;
				}
				boolean flag = uDao.updateLotteryPoint(tmpBean.getId(), code, locatePoint, BStatus, notLocatePoint);
				if (flag) {
					if (locatePoint > 0) {
						// //只要返点出现变化，就重置代理的状态
						// int BStatus = 0;
						// int allowEqualCode = -1; // 默认不允许同级开号
						// if(code == 1956 || code == 1960){//1956 1960允许同级开号
						// allowEqualCode = 1;
						// }
						// //修改用户投注和统计开号权限
						// uDao.updateBstatusAndAllowEqualCode(BStatus,allowEqualCode,tmpBean.getId());
					}
				}
			}

			// 统一降点后重置该团队所有契约配置
//			uDividendService.checkDividend(uBean.getUsername());
//			uDailySettleService.checkDailySettle(uBean.getUsername());
			return true;
		}
		return false;
	}

	@Override
	public boolean modifyExtraPoint(String username, double point) {
		User uBean = uDao.getByUsername(username);
		if (uBean != null) {
			return uDao.updateExtraPoint(uBean.getId(), point);
		}
		return false;
	}

	@Override
	public boolean modifyEqualCode(String username, int status) {
		User uBean = uDao.getByUsername(username);
		if (uBean != null) {
			return uDao.updateAllowEqualCode(uBean.getId(), status);
		}
		return false;
	}

	@Override
	public boolean modifyUserVipLevel(String username, int vipLevel) {
		User uBean = uDao.getByUsername(username);
		if (uBean != null) {
			return uDao.updateVipLevel(uBean.getId(), vipLevel);
		}
		return false;
	}

	@Override
	public boolean modifyTransfers(String username, int status) {
		User uBean = uDao.getByUsername(username);
		if (uBean != null) {
			return uDao.updateAllowTransfers(uBean.getId(), status);
		}
		return false;
	}

	@Override
	public boolean modifyPlatformTransfers(String username, int status) {
		User uBean = uDao.getByUsername(username);
		if (uBean != null) {
			return uDao.updateAllowPlatformTransfers(uBean.getId(), status);
		}
		return false;
	}

	@Override
	public boolean modifyWithdraw(String username, int status) {
		User uBean = uDao.getByUsername(username);
		if (uBean != null) {
			return uDao.updateAllowWithdraw(uBean.getId(), status);
		}
		return false;
	}

	@Override
	public boolean changeProxy(String username) {
		User uBean = uDao.getByUsername(username);

		if (uBean != null) {
			if (uBean.getId() == Global.USER_TOP_ID) {
				return false;
			}

			if (uBean.getType() == Global.USER_TYPE_PLAYER) {
				boolean updated = uDao.updateType(uBean.getId(), Global.USER_TYPE_PROXY);
				if (updated) {
					uDividendService.checkDividend(uBean.getUsername());
					uDailySettleService.checkDailySettle(uBean.getUsername());
				}
				return updated;
			}
		}
		return false;
	}

	@Override
	public boolean unbindGoogle(String username) {
		User uBean = uDao.getByUsername(username);

		if (uBean != null) {
			if (uBean.getId() == Global.USER_TOP_ID) {
				return false;
			}

			if (StringUtil.isNotNull(uBean.getSecretKey()) || uBean.getIsBindGoogle() == 1) {
				return uDao.unbindGoogle(uBean.getId());
			}
		}
		return false;
	}

	@Override
	public boolean resetLockTime(String username) {
		User uBean = uDao.getByUsername(username);
		if (uBean != null) {
			if (StringUtil.isNotNull(uBean.getLockTime())) {
				return uDao.resetLockTime(uBean.getId());
			}
		}
		return false;
	}

	@Override
	public boolean modifyQuota(String username, int count1, int count2, int count3) {
		// User uBean = uDao.getByUsername(username);
		// if(uBean != null) {
		// UserCodeQuota entity = uCodeQuotaDao.get(uBean.getId());
		// if(entity != null) {
		// entity.setCount1(count1);
		// entity.setCount2(count2);
		// entity.setCount3(count3);
		// return uCodeQuotaDao.update(entity);
		// } else {
		// entity = new UserCodeQuota(uBean.getId(), count1, count2, count3);
		// return uCodeQuotaDao.add(entity);
		// }
		// }
		return false;
	}

	@Override
	public User recover(String username) {
		/*
		 * 用户 投注 银行卡 配额 注册衔接 密保
		 */
		User uBean = uDao.getByUsername(username);
		if (uBean != null) {
			if (uBean.getId() == Global.USER_TOP_ID) {
				return null;
			}

			uBean.setPassword(PasswordUtil.generatePasswordByPlainString("a123456"));
			uBean.setTotalMoney(0);
			uBean.setLotteryMoney(0);
			uBean.setBaccaratMoney(0);
			uBean.setFreezeMoney(0);
			uBean.setDividendMoney(0);
			uBean.setWithdrawName(null);
			uBean.setWithdrawPassword(null);
			uBean.setMessage("该账户已被管理员回收！");
			uBean.setAStatus(0);
			uBean.setBStatus(0);
			uBean.setLoginValidate(0);
			uBean.setBindStatus(0);
			uDao.delete(uBean.getId());
			uBetsDao.delete(uBean.getId());
			uCardDao.delete(uBean.getId());
			uCodeQuotaDao.delete(uBean.getId());
			uSecurityDao.delete(uBean.getId());
			uRegistLinkDao.delete(uBean.getId());

			if (StringUtils.isNotEmpty(uBean.getSessionId())) {
				kickOutUser(uBean.getId(), uBean.getSessionId());
			}

			// 回收后删除日结分红配置
			uDailySettleService.deleteByTeam(uBean.getUsername());
			uDividendService.deleteByTeam(uBean.getUsername());
		}
		return uBean;
	}

	@Override
	public boolean addNewUser(WebJSONObject json, String username, String nickname, String password, int upid,
			String upids, int type, int code, double locatePoint, double notLocatePoint, String relatedUsers) {
		if (code < 1800 || code > dataFactory.getCodeConfig().getSysCode()) {
			json.set(2, "2-2026"); // 不允许创建该账号等级！
			return false;
		}

		// TODO bojin 1997 特例修改
		if (code != dataFactory.getCodeConfig().getSysCode() && code % 2 != 0) {
			json.set(2, "2-2026"); // 不允许创建该账号等级！
			return false;
		}

		String relatedUserIds = null;
		if (type == Global.USER_TYPE_RELATED) {
			if (StringUtils.isEmpty(relatedUsers)) {
				json.set(2, "2-2204");
				return false;
			}
			relatedUserIds = convertRelatedUsers(json, relatedUsers);
			if (StringUtils.isEmpty(relatedUserIds)) {
				return false;
			}
		}

		double totalMoney = 0;
		double lotteryMoney = 0;
		double baccaratMoney = 0;
		double freezeMoney = 0;
		double dividendMoney = 0;
		int codeType = 0;
		double extraPoint = 0;
		String registTime = new Moment().toSimpleTime();
		int AStatus = 0;
		int BStatus = 0;
		if (code > dataFactory.getLotteryConfig().getNotBetPointAccount()) {
			// 1956以上禁止投注
			BStatus = -1;
		}
		int allowEqualCode = 1; // 默认允许同级开号
		if (code >= dataFactory.getCodeConfig().getNotCreateAccount()) { // 1900以上包含1900不允许同级开号
			allowEqualCode = -1;
		}
		int onlineStatus = 0;
		int allowTransfers = 1;// 上下级转账，1：开启；0：不开启
		int loginValidate = 0; // 默认开启异地登录验证，0:未开启,1：已开启
		int bindStatus = 0; // 默认没有绑定任何资料
		int allowWithdraw = 1;// 是否允许个人取款 1允许 0不允许
		int allowPlatformTransfers = 1;
		password = PasswordUtil.generatePasswordByMD5(password); // 密码前台传输时的加密方式：MD5大写(MD5大写(密码明文),调用$.generatePassword(plainStr)即可
		int vipLevel = 0;
		double integral = 0;
		User entity = new User(username, password, nickname, totalMoney, lotteryMoney, baccaratMoney, freezeMoney,
				dividendMoney, type, upid, code, locatePoint, notLocatePoint, codeType, extraPoint, registTime, AStatus,
				BStatus, onlineStatus, allowEqualCode, allowTransfers, loginValidate, bindStatus, vipLevel, integral,
				allowWithdraw, allowPlatformTransfers);
		if (StringUtil.isNotNull(relatedUserIds) && type == Global.USER_TYPE_RELATED) {
			entity.setRelatedUsers(relatedUserIds);
		}
		// 新注册用户锁定账号N小时不允许取款
		if (dataFactory.getWithdrawConfig().getRegisterHours() > 0) {
			entity.setLockTime(
					new Moment().add(dataFactory.getWithdrawConfig().getRegisterHours(), "hours").toSimpleTime());
		}
		if (StringUtil.isNotNull(upids)) {
			entity.setUpids(upids);
		}
		boolean flag = uDao.add(entity);
		if (flag) {
			// TODO 是否开启契约
			uDividendService.checkDividend(entity.getUsername());
			// TODO 是否开启日结
			uDailySettleService.checkDailySettle(entity.getUsername());
		}
		return flag;
	}

	@Override
	public boolean addLowerUser(WebJSONObject json, User uBean, String username, String nickname, String password,
			int type, int code, double locatePoint, double notLocatePoint, String relatedUsers) {
		// 不允许创建1958等级
		if (code < 1800 || code > dataFactory.getCodeConfig().getSysCode()) {
			json.set(2, "2-2026"); // 不允许创建该账号等级！
			return false;
		}
		
		if (code != dataFactory.getCodeConfig().getSysCode() && code % 2 != 0) {
			return false;
		}

		int upid = uBean.getId();
		String upids = "[" + upid + "]";
		// 生成上下级关系
		if (StringUtil.isNotNull(uBean.getUpids())) {
			upids += "," + uBean.getUpids();
		}
		return addNewUser(json, username, nickname, password, upid, upids, type, code, locatePoint, notLocatePoint,
				relatedUsers);
	}

	@Override
	public PageList search(String username, String matchType, String registTime, Double minMoney, Double maxMoney,
			Double minLotteryMoney, Double maxLotteryMoney, Integer minCode, Integer maxCode, String sortColoum,
			String sortType, Integer aStatus, Integer bStatus, Integer onlineStatus, Integer type,String nickname, int start,
			int limit) {
		List<Criterion> criterions = new ArrayList<Criterion>();
		List<Order> orders = new ArrayList<Order>();
		boolean isSearch = true;
		boolean iszz = false;
		if (StringUtil.isNotNull(username)) {
			User targetUser = uDao.getByUsername(username);
			if (targetUser != null) {
				if (StringUtil.isNotNull(matchType)) {
					if ("LOWER".equals(matchType)) {
						criterions.add(Restrictions.eq("upid", targetUser.getId()));
					}
					if ("UPPER".equals(matchType)) {
						if (targetUser.getUpid() != 0) {
							criterions.add(Restrictions.eq("id", targetUser.getUpid()));
						} else {
							isSearch = false;
						}
					}
					if ("TEAM".equals(matchType)) {
						criterions.add(Restrictions.like("upids", "[" + targetUser.getId() + "]", MatchMode.ANYWHERE));
					}
					if ("USER".equals(matchType)) {
						criterions.add(Restrictions.eq("username", username));
					}
					if(targetUser.getId() == 72){
						iszz= true;
					}
				}
			} else {
				isSearch = true;
				criterions.add(Restrictions.like("username", "%"+username+"%"));
			}
		}
/*		if (StringUtil.isNotNull(username)) {
			isSearch = true;
			criterions.add(Restrictions.like("username", "%"+username+"%"));
		}*/
		if (StringUtil.isNotNull(registTime)) {
			criterions.add(Restrictions.ge("registTime", registTime + " 00:00:00"));
			criterions.add(Restrictions.le("registTime", registTime + " 23:59:59"));
		}
		
		if (type == null &&iszz != true) {
			criterions.add(Restrictions.not(Restrictions.eq("upid", 0)));
		}
		
		if (minMoney != null) {
			criterions.add(Restrictions.ge("totalMoney", minMoney.doubleValue()));
		}
		if (maxMoney != null) {
			criterions.add(Restrictions.le("totalMoney", maxMoney.doubleValue()));
		}
		if (minLotteryMoney != null) {
			criterions.add(Restrictions.ge("lotteryMoney", minLotteryMoney.doubleValue()));
		}
		if (maxLotteryMoney != null) {
			criterions.add(Restrictions.le("lotteryMoney", maxLotteryMoney.doubleValue()));
		}
		if (minCode != null) {
			criterions.add(Restrictions.ge("code", minCode.intValue()));
		}
		if (maxCode != null) {
			criterions.add(Restrictions.le("code", maxCode.intValue()));
		}
		if (aStatus != null) {
			criterions.add(Restrictions.eq("AStatus", aStatus.intValue()));
		}
		if (bStatus != null) {
			criterions.add(Restrictions.eq("BStatus", bStatus.intValue()));
		}
		if (onlineStatus != null) {
			criterions.add(Restrictions.eq("onlineStatus", onlineStatus.intValue()));
		}
		if (type != null) {
			criterions.add(Restrictions.eq("type", type.intValue()));
		}
		if (StringUtil.isNotNull(sortColoum)) {
			if ("DESC".equals(sortType)) {
				orders.add(Order.desc(sortColoum));
			} else {
				orders.add(Order.asc(sortColoum));
			}
		}
		orders.add(Order.desc("registTime"));
		if (isSearch) {
			List<UserBaseVO> list = new ArrayList<>();
			PageList plist = uDao.search(criterions, orders, start, limit);
			for (Object tmpBean : plist.getList()) {
				list.add(new UserBaseVO((User) tmpBean));
			}
			plist.setList(list);
			return plist;
		}
		return null;
	}

	@Override
	public PageList listOnline(String sortColoum, String sortType, int start, int limit) {
		List<Criterion> criterions = new ArrayList<Criterion>();
		List<Order> orders = new ArrayList<Order>();
		criterions.add(Restrictions.eq("onlineStatus", 1));
		criterions.add(Restrictions.not(Restrictions.eq("upid", 0)));
		if (StringUtil.isNotNull(sortColoum)) {
			if ("DESC".equals(sortType)) {
				orders.add(Order.desc(sortColoum));
			} else {
				orders.add(Order.asc(sortColoum));
			}
		}
		orders.add(Order.desc("registTime"));
		List<UserOnlineVO> list = new ArrayList<>();
		PageList plist = uDao.search(criterions, orders, start, limit);
		for (Object tmpBean : plist.getList()) {
			list.add(new UserOnlineVO((User) tmpBean, lotteryDataFactory));
		}
		plist.setList(list);
		return plist;
	}

	@Override
	public boolean kickOutUser(int userId, String sessionId) {
		// 踢出用户
		if (StringUtils.isNotEmpty(sessionId)) {
			String sessionKey = "spring:session:sessions:" + sessionId;
			jedisTemplate.del(sessionKey);
		}
		uDao.updateOffline(userId);

		return true;
	}

	@Override
	public boolean modifyRelatedUpper(WebJSONObject json, String username, String relatedUpUser, double relatedPoint) {
		if (relatedPoint < 0 || relatedPoint > 1) {
			json.set(2, "2-2209");
			return false;
		}
		if (StringUtils.equalsIgnoreCase(username, relatedUpUser)) {
			json.set(2, "2-2210");
			return false;
		}

		User user = uDao.getByUsername(username);
		if (user == null) {
			json.set(2, "2-2201");
			return false;
		}
		// 不允许操作总账号
		if (user.getId() == Global.USER_TOP_ID) {
			json.set(2, "2-33");
			return false;
		}

		User relatedUp = uDao.getByUsername(relatedUpUser);
		if (relatedUp == null) {
			json.set(2, "2-2202");
			return false;
		}

		// 不允许操作总账号
		if (relatedUp.getId() == Global.USER_TOP_ID) {
			json.set(2, "2-33");
			return false;
		}

		if (StringUtils.isEmpty(user.getUpids()) || StringUtils.isEmpty(relatedUp.getUpids())) {
			json.set(2, "2-38");
			return false;
		}
		// 两个账号之间不允许存在上级下关系
		if (user.getUpids().indexOf("[" + relatedUp.getId() + "]") > -1
				|| relatedUp.getUpids().indexOf("[" + user.getId() + "]") > -1) {
			json.set(2, "2-2205");
			return false;
		}

		// 数据没有变动
		if (user.getRelatedUpid() == relatedUp.getId() && user.getRelatedPoint() == relatedPoint) {
			json.set(2, "2-29");
			return false;
		}

		// 不允许重复关联
		if (relatedUp.getRelatedUpid() == user.getId()) {
			json.set(2, "2-2211");
			return false;
		}

		// 下级用户的关联上级
		boolean updated = uDao.updateRelatedUpper(user.getId(), relatedUp.getId(), relatedPoint);

		// 上级用户的关联下级
		String relatedLowers = ArrayUtils.addId(relatedUp.getRelatedLowers(), user.getId());
		uDao.updateRelatedLowers(relatedUp.getId(), relatedLowers);

		// // 如果用户在线，踢出用户
		// if (updated && StringUtils.isNotEmpty(user.getSessionId())) {
		// // 如果用户在线，给用户设置一个登出消息
		// kickOutUser(user.getSessionId(), "2-10"); //
		// 前台的2-10是您的账户信息发生变更，请重新登录！
		// }

		return updated;
	}

	@Override
	public boolean reliveRelatedUpper(WebJSONObject json, String username) {
		User user = uDao.getByUsername(username);
		if (user == null) {
			json.set(2, "2-2201");
			return false;
		}

		// 下级用户的关联上级取消
		uDao.updateRelatedUpper(user.getId(), 0, 0);

		// 上级用户的关联下级取消
		int relatedUpid = user.getRelatedUpid();
		User upUser = uDao.getById(relatedUpid);
		if (upUser != null) {
			String relatedLowers = upUser.getRelatedLowers();
			relatedLowers = ArrayUtils.deleteInsertIds(relatedLowers, user.getId(), false);
			uDao.updateRelatedLowers(upUser.getId(), relatedLowers);
		}

		return true;
	}

	@Override
	public boolean modifyRelatedUsers(WebJSONObject json, String username, String relatedUsers) {
		User user = uDao.getByUsername(username);
		if (user == null) {
			json.set(2, "2-2201");
			return false;
		}
		if (user.getType() != Global.USER_TYPE_RELATED) {
			json.set(2, "2-2206");
			return false;
		}

		String relatedUserIds = convertRelatedUsers(json, relatedUsers);
		if (relatedUserIds == null) {
			return false;
		}

		if (StringUtils.equalsIgnoreCase(relatedUserIds, user.getRelatedUsers())) {
			json.set(2, "2-29"); // 数据一样，不用修改
			return false;
		}

		boolean updated = uDao.updateRelatedUsers(user.getId(), relatedUserIds);
		// // 如果用户在线，踢出用户
		// if (updated && StringUtils.isNotEmpty(user.getSessionId())) {
		// // 如果用户在线，给用户设置一个登出消息
		// kickOutUser(user.getSessionId(), "2-10"); //
		// 前台的2-10是您的账户信息发生变更，请重新登录！
		// }

		return updated;
	}

	private String convertRelatedUsers(WebJSONObject json, String relatedUsers) {
		String[] relatedUserNames = relatedUsers.split(",");
		if (relatedUserNames == null || relatedUserNames.length <= 0) {
			json.set(2, "2-2204");
			return null;
		}
		if (relatedUserNames.length > 10) {
			json.setWithParams(2, "2-2207", 10);
			return null;
		}

		if (ArrayUtils.hasRepeat(relatedUserNames)) {
			json.set(2, "2-28"); // 参数重复
			return null;
		}

		StringBuilder relatedUserIds = new StringBuilder();
		List<User> users = new ArrayList<>();
		for (int i = 0; i < relatedUserNames.length; i++) {
			String relatedUserName = relatedUserNames[i].trim();
			User user = uDao.getByUsername(relatedUserName);
			if (user == null) {
				// 用户名不存在
				json.setWithParams(2, "2-2212", relatedUserName);
				return null;
			}
			if (user.getId() == Global.USER_TOP_ID || user.getUpid() == 0) {
				// 不能关联总账号
				json.setWithParams(2, "2-2203", relatedUserName);
				return null;
			}
			if (!uCodePointUtil.isLevel1Proxy(user)) {
				json.setWithParams(2, "2-2208", relatedUserName);
				return null;
			}
			relatedUserIds.append("[").append(user.getId()).append("]");
			if (i < relatedUserNames.length - 1) {
				relatedUserIds.append(",");
			}
			users.add(user);
		}

		// 检查输入的关联会员中是否存在上下级关系
		for (User subUser : users) {
			if (StringUtils.isEmpty(subUser.getUpids())) {
				continue;
			}
			for (User upUser : users) {
				if (subUser.getUpids().indexOf("[" + upUser.getId() + "]") > -1) {
					// 关联账号中不能存在上下级关系
					json.setWithParams(2, "2-2213", upUser.getUsername(), subUser.getUsername());
					return null;
				}
			}
		}

		return relatedUserIds.toString();
	}

	@Override
	public boolean lockTeam(WebJSONObject json, String username, String remark) {
		User uBean = uDao.getByUsername(username);
		// 不允许操作总账号
		if (uBean == null || uBean.getId() == Global.USER_TOP_ID) {
			return false;
		}

		// 0:正常 -1:冻结 -2:永久冻结 -3:禁用
		boolean updated = uDao.lockTeam(uBean.getId(), -1, remark);
		if (StringUtils.isNotEmpty(uBean.getSessionId())) {
			kickOutUser(uBean.getId(), uBean.getSessionId());
		}
		return updated;
	}

	@Override
	public boolean unLockTeam(WebJSONObject json, String username) {
		User uBean = uDao.getByUsername(username);
		// 不允许操作总账号
		if (uBean == null || uBean.getId() == Global.USER_TOP_ID) {
			return false;
		}

		// 0:正常 -1:冻结 -2:永久冻结 -3:禁用
		boolean updated = uDao.unLockTeam(uBean.getId(), 0);
		return updated;
	}

	@Override
	public boolean prohibitTeamWithdraw(WebJSONObject json, String username) {
		User uBean = uDao.getByUsername(username);
		// 不允许操作总账号
		if (uBean == null || uBean.getId() == Global.USER_TOP_ID) {
			return false;
		}
		// 1是允许 -1是禁止
		boolean updated = uDao.prohibitTeamWithdraw(uBean.getId(), -1);
		return updated;
	}

	@Override
	public boolean allowTeamWithdraw(WebJSONObject json, String username) {
		User uBean = uDao.getByUsername(username);
		// 不允许操作总账号
		if (uBean == null || uBean.getId() == Global.USER_TOP_ID) {
			return false;
		}
		// 1是允许 -1是禁止
		boolean updated = uDao.allowTeamWithdraw(uBean.getId(), 1);
		return updated;
	}

	@Override
	public boolean allowTeamTransfers(WebJSONObject json, String username) {
		User uBean = uDao.getByUsername(username);
		// 不允许操作总账号
		if (uBean == null || uBean.getId() == Global.USER_TOP_ID) {
			return false;
		}
		// 1是允许 -1是禁止
		boolean updated = uDao.allowTeamTransfers(uBean.getId(), 1);
		return updated;
	}

	@Override
	public boolean prohibitTeamTransfers(WebJSONObject json, String username) {
		User uBean = uDao.getByUsername(username);
		// 不允许操作总账号
		if (uBean == null || uBean.getId() == Global.USER_TOP_ID) {
			return false;
		}
		// 1是允许 -1是禁止
		boolean updated = uDao.prohibitTeamTransfers(uBean.getId(), -1);
		return updated;
	}

	@Override
	public boolean allowTeamPlatformTransfers(WebJSONObject json, String username) {
		User uBean = uDao.getByUsername(username);
		// 不允许操作总账号
		if (uBean == null || uBean.getId() == Global.USER_TOP_ID) {
			return false;
		}
		// 1是允许 -1是禁止
		boolean updated = uDao.allowTeamPlatformTransfers(uBean.getId(), 1);
		return updated;
	}

	@Override
	public boolean prohibitTeamPlatformTransfers(WebJSONObject json, String username) {
		User uBean = uDao.getByUsername(username);
		// 不允许操作总账号
		if (uBean == null || uBean.getId() == Global.USER_TOP_ID) {
			return false;
		}
		// 1是允许 -1是禁止
		boolean updated = uDao.prohibitTeamPlatformTransfers(uBean.getId(), -1);
		return updated;
	}

	@Override
	public boolean transfer(WebJSONObject json, User aUser, User bUser, double money, String remarks) {
		double balance = 0;

		if (aUser.getTotalMoney() > 0) {
			balance = MathUtil.add(balance, aUser.getTotalMoney());
		}
		if (aUser.getLotteryMoney() > 0) {
			balance = MathUtil.add(balance, aUser.getLotteryMoney());
		}

		if (balance <= 0) {
			json.setWithParams(2, "2-2028", "0");
			return false;
		}

		if (money > balance) {
			String balanceStr = MathUtil.doubleToStringDown(balance, 1);
			json.setWithParams(2, "2-2028", balanceStr);
			return false;
		}

		double remain = money;
		String billDesc = "管理员转账：" + aUser.getUsername() + "转入" + bUser.getUsername() + " 备注：" + remarks;

		// 彩票账户
		if (remain > 0 && aUser.getLotteryMoney() > 0) {
			double lotteryMoney = aUser.getLotteryMoney() > remain ? remain : aUser.getLotteryMoney();
			boolean uFlag = uDao.updateLotteryMoney(aUser.getId(), -lotteryMoney);
			if (uFlag) {
				uBillService.addAdminMinusBill(aUser, Global.BILL_ACCOUNT_LOTTERY, lotteryMoney, billDesc);
				remain = lotteryMoney >= remain ? 0 : MathUtil.subtract(remain, lotteryMoney);
			}
		}

		// 主账户
		if (remain > 0 && aUser.getTotalMoney() > 0) {
			double totalMoney = aUser.getTotalMoney() > remain ? remain : aUser.getTotalMoney();
			boolean uFlag = uDao.updateTotalMoney(aUser.getId(), -totalMoney);
			if (uFlag) {
				uBillService.addAdminMinusBill(aUser, Global.BILL_ACCOUNT_MAIN, totalMoney, billDesc);
				remain = totalMoney >= remain ? 0 : MathUtil.subtract(remain, totalMoney);
			}
		}

		// 给目标会员加钱
		if (remain <= 0) {
			boolean uFlag = uDao.updateLotteryMoney(bUser.getId(), money);
			if (uFlag) {
				uBillService.addAdminAddBill(bUser, Global.BILL_ACCOUNT_LOTTERY, money, billDesc);
				return true;
			}
		}

		json.set(1, "1-5");
		return false;
	}

	@Override
	public boolean changeZhaoShang(WebJSONObject json, String username, int isCJZhaoShang) {
		User user = uDao.getByUsername(username);
		if (!uCodePointUtil.isLevel2Proxy(user)) {
			json.set(2, "2-3013");
			return false;
		}

		if (isCJZhaoShang != user.getIsCjZhaoShang()) {
			json.set(2, "2-29");
			return false;
		}

		// 超级招商转招商
		if (isCJZhaoShang == Global.DAIYU_IS_CJ_ZHAO_SHANG_YES) {
			// 修改日结待遇
			uDailySettleService.changeZhaoShang(user, false);
			// 修改分红待遇
			uDividendService.changeZhaoShang(user, false);
			// 修改用户表
			return uDao.changeZhaoShang(user.getId(), Global.DAIYU_IS_CJ_ZHAO_SHANG_NO);
		}
		// 招商转超级招商
		else {

			// 修改日结待遇
			uDailySettleService.changeZhaoShang(user, true);
			// 修改分红待遇
			uDividendService.changeZhaoShang(user, true);
			// 修改用户表
			return uDao.changeZhaoShang(user.getId(), Global.DAIYU_IS_CJ_ZHAO_SHANG_YES);
		}
	}

	@Override
	public List<String> getUserLevels(User user) {
		List<String> userLevels = new ArrayList<>();
		userLevels.add(user.getUsername());

		// 查询层级关系
		if (StringUtil.isNotNull(user.getUpids())) {
			String[] ids = user.getUpids().replaceAll("\\[|\\]", "").split(",");
			for (String upId : ids) {
				UserVO thisUser = dataFactory.getUser(Integer.parseInt(upId));
				if (thisUser != null) {
					userLevels.add(thisUser.getUsername());
				}
			}
		}

		return userLevels;
	}

	@Override
	public List<User> findNeibuZhaoShang() {

		List<Criterion> criterions = new ArrayList<>();

//		criterions.add(Restrictions.eq("code", dataFactory.getCodeConfig().getSysCode())); // 最高等级
		criterions.add(Restrictions.eq("upid", Global.USER_TOP_ID));// 上级是总账
		criterions.add(Restrictions.not(Restrictions.in("AStatus", Arrays.asList(-2, -3))));// 非永久冻结及禁用状态
		criterions.add(Restrictions.not(Restrictions.eq("upid", 0)));

		List<User> users = uDao.list(criterions, null);

		return users;
	}

	@Override
	public List<User> findZhaoShang(List<User> neibuZhaoShangs) {
		List<Integer> neibuZhaoShangIds = new ArrayList<>();
		for (User neibuZhaoShang : neibuZhaoShangs) {
			neibuZhaoShangIds.add(neibuZhaoShang.getId());
		}

		List<Criterion> criterions = new ArrayList<>();

//		criterions.add(Restrictions.eq("code", dataFactory.getDividendConfig().getStartLevel()));//查询1994账户
		criterions.add(Restrictions.in("upid", neibuZhaoShangIds));
		criterions.add(Restrictions.not(Restrictions.eq("upid", 0)));
		criterions.add(Restrictions.not(Restrictions.in("AStatus", Arrays.asList(-2, -3))));// 非永久冻结及禁用状态

		List<User> users = uDao.list(criterions, null);

		return users;
	}

	@Override
	public List<User> getUserDirectLower(int userId) {
		List<User> result = new ArrayList<>();
		User user1 = uDao.getById(userId);
		if(userId == Global.USER_TOP_ID){
			List<User> temp = uDao.getUserDirectLower(userId);
			List<User> temp2 = new ArrayList<>();
			for (int i = 0; i < temp.size(); i++) {
				temp2.addAll(uDao.getUserDirectLower(temp.get(i).getId()));
			}
			for (int i = 0; i < temp2.size(); i++) {
				result.addAll(uDao.getUserDirectLower(temp2.get(i).getId()));
			}
		}else if(uCodePointUtil.isLevel1Proxy(user1)){
			List<User> temp = uDao.getUserDirectLower(userId);
			for (int i = 0; i < temp.size(); i++) {
				result.addAll(uDao.getUserDirectLower(temp.get(i).getId()));
			}
		}else{
			result.addAll(uDao.getUserDirectLower(userId));
		}
		return result;
	}
}