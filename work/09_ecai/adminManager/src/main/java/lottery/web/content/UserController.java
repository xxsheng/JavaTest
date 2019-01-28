package lottery.web.content;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import admin.domains.content.entity.AdminUser;
import admin.domains.jobs.AdminUserActionLogJob;
import admin.domains.jobs.AdminUserCriticalLogJob;
import admin.domains.jobs.AdminUserLogJob;
import admin.domains.jobs.MailJob;
import admin.web.WUC;
import admin.web.WebJSONObject;
import admin.web.helper.AbstractActionController;
import javautils.StringUtil;
import javautils.date.Moment;
import javautils.http.HttpUtil;
import javautils.jdbc.PageList;
import javautils.math.MathUtil;
import lottery.domains.content.biz.UserCardService;
import lottery.domains.content.biz.UserInfoService;
import lottery.domains.content.biz.UserSecurityService;
import lottery.domains.content.biz.UserService;
import lottery.domains.content.biz.UserWithdrawLimitService;
import lottery.domains.content.biz.UserWithdrawService;
import lottery.domains.content.dao.UserDao;
import lottery.domains.content.dao.UserGameAccountDao;
import lottery.domains.content.dao.UserInfoDao;
import lottery.domains.content.dao.UserPlanInfoDao;
import lottery.domains.content.entity.User;
import lottery.domains.content.entity.UserGameAccount;
import lottery.domains.content.entity.UserInfo;
import lottery.domains.content.global.Global;
import lottery.domains.content.vo.user.SysCodeRangeVO;
import lottery.domains.content.vo.user.UserBaseVO;
import lottery.domains.content.vo.user.UserCardVO;
import lottery.domains.content.vo.user.UserProfileVO;
import lottery.domains.content.vo.user.UserSecurityVO;
import lottery.domains.content.vo.user.UserVO;
import lottery.domains.content.vo.user.UserWithdrawLimitVO;
import lottery.domains.content.vo.user.UserWithdrawVO;
import lottery.domains.pool.LotteryDataFactory;
import lottery.web.content.utils.UserCodePointUtil;
import lottery.web.content.validate.UserValidate;
import net.sf.json.JSONObject;

@Controller
public class UserController extends AbstractActionController {

	@Autowired
	private AdminUserActionLogJob adminUserActionLogJob;

	@Autowired
	private AdminUserLogJob adminUserLogJob;

	@Autowired
	private AdminUserCriticalLogJob adminUserCriticalLogJob;
	/**
	 * DAO
	 */
	@Autowired
	private UserDao uDao;

	@Autowired
	private UserInfoDao uInfoDao;

	@Autowired
	private UserPlanInfoDao uPlanInfoDao;
	@Autowired
	private UserWithdrawLimitService userWithdrawLimitService;
	@Autowired
	private UserWithdrawService uWithdrawService;
	/**
	 * SERVICE
	 */
	@Autowired
	private UserService uService;

	@Autowired
	private UserInfoService uInfoService;

	@Autowired
	private UserCardService uCardService;

	@Autowired
	private UserSecurityService uSecurityService;

	@Autowired
	private UserGameAccountDao uGameAccountDao;

	/**
	 * UTIL
	 */
	@Autowired
	private UserCodePointUtil uCodePointUtil;

	@Autowired
	private MailJob mailJob;

	/**
	 * VALIDATE
	 */
	@Autowired
	private UserValidate uValidate;

	@Autowired
	private LotteryDataFactory dataFactory;

	@RequestMapping(value = WUC.LOTTERY_USER_LIST, method = { RequestMethod.POST })
	@ResponseBody
	public void LOTTERY_USER_LIST(HttpSession session, HttpServletRequest request, HttpServletResponse response) {
		String actionKey = WUC.LOTTERY_USER_LIST;
		long t1 = System.currentTimeMillis();
		WebJSONObject json = new WebJSONObject(super.getAdminDataFactory());
		AdminUser uEntity = super.getCurrUser(session, request, response);
		if (uEntity != null) {
			if (super.hasAccess(uEntity, actionKey)) {
				String username = request.getParameter("username");
				String matchType = request.getParameter("matchType");
				String registTime = request.getParameter("registTime");
				
				Double minMoney = HttpUtil.getDoubleParameter(request, "minTotalMoney");
				Double maxMoney = HttpUtil.getDoubleParameter(request, "maxTotalMoney");
				Double minLotteryMoney = HttpUtil.getDoubleParameter(request, "minLotteryMoney");
				Double maxLotteryMoney = HttpUtil.getDoubleParameter(request, "maxLotteryMoney");
				Integer minCode = HttpUtil.getIntParameter(request, "minCode");
				Integer maxCode = HttpUtil.getIntParameter(request, "maxCode");
				String sortColoum = request.getParameter("sortColoum");
				String sortType = request.getParameter("sortType");
				Integer aStatus = HttpUtil.getIntParameter(request, "aStatus");
				Integer bStatus = HttpUtil.getIntParameter(request, "bStatus");
				Integer onlineStatus = HttpUtil.getIntParameter(request, "onlineStatus");
				String nickname = request.getParameter("nickname");
				Integer type = HttpUtil.getIntParameter(request, "type");
				int start = HttpUtil.getIntParameter(request, "start");
				int limit = HttpUtil.getIntParameter(request, "limit");
				PageList pList = uService.search(username, matchType, registTime, minMoney, maxMoney, minLotteryMoney, maxLotteryMoney, minCode, maxCode, sortColoum, sortType, aStatus, bStatus, onlineStatus, type, nickname,start, limit);
				if(pList != null) {
					json.accumulate("totalCount", pList.getCount());
					json.accumulate("data", pList.getList());
				} else {
					json.accumulate("totalCount", 0);
					json.accumulate("data", "[]");
				}

				// 层级用户
				List<String> userLevels = uCodePointUtil.getUserLevels(username);
				json.accumulate("userLevels", userLevels);

				json.set(0, "0-3");
			} else {
				json.set(2, "2-4");
			}
		} else {
			json.set(2, "2-6");
		}
		long t2 = System.currentTimeMillis();
		if (uEntity != null) {
			adminUserActionLogJob.add(request, actionKey, uEntity, json, t2 - t1);
		}
		HttpUtil.write(response, json.toString(), HttpUtil.json);
	}

	@RequestMapping(value = WUC.LOTTERY_USER_LIST_ONLINE, method = { RequestMethod.POST })
	@ResponseBody
	public void LOTTERY_USER_LIST_ONLINE(HttpSession session, HttpServletRequest request, HttpServletResponse response) {
		String actionKey = WUC.LOTTERY_USER_LIST_ONLINE;
		long t1 = System.currentTimeMillis();
		WebJSONObject json = new WebJSONObject(super.getAdminDataFactory());
		AdminUser uEntity = super.getCurrUser(session, request, response);
		if (uEntity != null) {
			if (super.hasAccess(uEntity, actionKey)) {
				String sortColoum = request.getParameter("sortColoum");
				String sortType = request.getParameter("sortType");
				int start = HttpUtil.getIntParameter(request, "start");
				int limit = HttpUtil.getIntParameter(request, "limit");
				PageList pList = uService.listOnline(sortColoum, sortType, start, limit);
				if(pList != null) {
					json.accumulate("totalCount", pList.getCount());
					json.accumulate("data", pList.getList());
				} else {
					json.accumulate("totalCount", 0);
					json.accumulate("data", "[]");
				}
				json.set(0, "0-3");
			} else {
				json.set(2, "2-4");
			}
		} else {
			json.set(2, "2-6");
		}
		long t2 = System.currentTimeMillis();
		if (uEntity != null) {
			adminUserActionLogJob.add(request, actionKey, uEntity, json, t2 - t1);
		}
		HttpUtil.write(response, json.toString(), HttpUtil.json);
	}

	@RequestMapping(value = WUC.LOTTERY_USER_LOWER_ONLINE, method = { RequestMethod.POST })
	@ResponseBody
	public void LOTTERY_USER_LOWER_ONLINE(HttpSession session, HttpServletRequest request, HttpServletResponse response) {
		String username = request.getParameter("username");
		JSONObject json = new JSONObject();
		int online = 0;
		if(StringUtil.isNotNull(username)) {
			User targetUser = uDao.getByUsername(username);
			if(targetUser != null) {
				List<User> uList = uDao.getUserLower(targetUser.getId());
				uList.add(targetUser); // 加入自己
				if(uList.size() > 0) {
					Integer[] ids = new Integer[uList.size()];
					for (int i = 0; i < ids.length; i++) {
						ids[i] = uList.get(i).getId();
					}
					online = uDao.getOnlineCount(ids);
				}
			}
		}
		json.accumulate("data", online);
		HttpUtil.write(response, json.toString(), HttpUtil.json);
	}

	@RequestMapping(value = WUC.LOTTERY_USER_ADD, method = { RequestMethod.POST })
	@ResponseBody
	public void LOTTERY_USER_ADD(HttpSession session, HttpServletRequest request, HttpServletResponse response) {
		String actionKey = WUC.LOTTERY_USER_ADD;
		long t1 = System.currentTimeMillis();
		WebJSONObject json = new WebJSONObject(super.getAdminDataFactory());
		AdminUser uEntity = super.getCurrUser(session, request, response);
		if (uEntity != null) {
			if (super.hasAccess(uEntity, actionKey)) {
				String username = HttpUtil.getStringParameterTrim(request, "username");
				String nickname = HttpUtil.getStringParameterTrim(request, "nickname");
				String password = HttpUtil.getStringParameterTrim(request, "password");
				String upperUser = HttpUtil.getStringParameterTrim(request, "upperUser");
				String relatedUsers = HttpUtil.getStringParameterTrim(request, "relatedUsers");
				int type = HttpUtil.getIntParameter(request, "type");
				double locatePoint = HttpUtil.getDoubleParameter(request, "locatePoint");

				if (uValidate.testUsername(json, username)) {
					locatePoint = MathUtil.doubleFormat(locatePoint, 2);
					int code = uCodePointUtil.getUserCode(locatePoint);
					double notLocatePoint = uCodePointUtil.getNotLocatePoint(locatePoint);
					if(StringUtil.isNotNull(upperUser)) {
						User uBean = uDao.getByUsername(upperUser);
						if(uBean != null) {
							if(uBean.getType() == Global.USER_TYPE_PROXY) {
								if(uValidate.testNewUserPoint(json, uBean, locatePoint)) {
									boolean result = uService.addLowerUser(json, uBean, username, nickname, password, type, code, locatePoint, notLocatePoint, relatedUsers);
									if(result) {
										adminUserLogJob.logAddUser(uEntity, request, username, relatedUsers, type, locatePoint);
										adminUserCriticalLogJob.logAddUser(uEntity, request, username, relatedUsers, type, locatePoint, actionKey);
										json.set(0, "0-5");
									}
								}
							} else {
								json.set(2, "2-2012");
							}
						} else {
							json.set(2, "2-2013");
						}
					} else {
						json.set(2, "2-2013");
					}
				}
			} else {
				json.set(2, "2-4");
			}
		} else {
			json.set(2, "2-6");
		}
		long t2 = System.currentTimeMillis();
		if (uEntity != null) {
			adminUserActionLogJob.add(request, actionKey, uEntity, json, t2 - t1);
		}
		HttpUtil.write(response, json.toString(), HttpUtil.json);
	}

	@RequestMapping(value = WUC.LOTTERY_USER_LOCK, method = { RequestMethod.POST })
	@ResponseBody
	public void LOTTERY_USER_LOCK(HttpSession session, HttpServletRequest request, HttpServletResponse response) {
		String actionKey = WUC.LOTTERY_USER_LOCK;
		long t1 = System.currentTimeMillis();
		WebJSONObject json = new WebJSONObject(super.getAdminDataFactory());
		AdminUser uEntity = super.getCurrUser(session, request, response);
		if (uEntity != null) {
			if (super.hasAccess(uEntity, actionKey)) {
				String username = request.getParameter("username");
				int status = HttpUtil.getIntParameter(request, "status");
				String message = request.getParameter("message");
				boolean result = uService.aStatus(username, status, message);
				if(result) {
					adminUserLogJob.logLockUser(uEntity, request, username, status, message);
					adminUserCriticalLogJob.logLockUser(uEntity, request, username, status, message,actionKey);
					mailJob.sendLockUser(uDao.getByUsername(username), uEntity.getUsername(), status, message);
					json.set(0, "0-5");
				} else {
					json.set(1, "1-5");
				}
			} else {
				json.set(2, "2-4");
			}
		} else {
			json.set(2, "2-6");
		}
		long t2 = System.currentTimeMillis();
		if (uEntity != null) {
			adminUserActionLogJob.add(request, actionKey, uEntity, json, t2 - t1);
		}
		HttpUtil.write(response, json.toString(), HttpUtil.json);
	}

	@RequestMapping(value = WUC.LOTTERY_USER_UNLOCK, method = { RequestMethod.POST })
	@ResponseBody
	public void LOTTERY_USER_UNLOCK(HttpSession session, HttpServletRequest request, HttpServletResponse response) {
		String actionKey = WUC.LOTTERY_USER_UNLOCK;
		long t1 = System.currentTimeMillis();
		WebJSONObject json = new WebJSONObject(super.getAdminDataFactory());
		AdminUser uEntity = super.getCurrUser(session, request, response);
		if (uEntity != null) {
			if (super.hasAccess(uEntity, actionKey)) {
				String username = request.getParameter("username");
				int status = 0; // 正常状态
				String message = null; // 清空账号消息
				boolean result = uService.aStatus(username, status, message);
				if(result) {
					adminUserLogJob.logUnlockUser(uEntity, request, username);
					adminUserCriticalLogJob.logUnLockUser(uEntity, request, username, actionKey);
					mailJob.sendUnLockUser(uDao.getByUsername(username), uEntity.getUsername());
					json.set(0, "0-5");
				} else {
					json.set(1, "1-5");
				}
			} else {
				json.set(2, "2-4");
			}
		} else {
			json.set(2, "2-6");
		}
		long t2 = System.currentTimeMillis();
		if (uEntity != null) {
			adminUserActionLogJob.add(request, actionKey, uEntity, json, t2 - t1);
		}
		HttpUtil.write(response, json.toString(), HttpUtil.json);
	}

	@RequestMapping(value = WUC.LOTTERY_USER_RECOVER, method = { RequestMethod.POST })
	@ResponseBody
	public void LOTTERY_USER_RECOVER(HttpSession session, HttpServletRequest request, HttpServletResponse response) {
		String actionKey = WUC.LOTTERY_USER_RECOVER;
		long t1 = System.currentTimeMillis();
		WebJSONObject json = new WebJSONObject(super.getAdminDataFactory());
		AdminUser uEntity = super.getCurrUser(session, request, response);
		if (uEntity != null) {
			if (super.hasAccess(uEntity, actionKey)) {
				String username = request.getParameter("username");
				User result = uService.recover(username);
				if(result != null) {
					adminUserLogJob.logRecoverUser(uEntity, request, result);
					mailJob.sendRecoverUser(result, uEntity.getUsername());
					json.set(0, "0-5");
				} else {
					json.set(1, "1-5");
				}
			} else {
				json.set(2, "2-4");
			}
		} else {
			json.set(2, "2-6");
		}
		long t2 = System.currentTimeMillis();
		if (uEntity != null) {
			adminUserActionLogJob.add(request, actionKey, uEntity, json, t2 - t1);
		}
		HttpUtil.write(response, json.toString(), HttpUtil.json);
	}

	@RequestMapping(value = WUC.LOTTERY_USER_BETS_STATUS, method = { RequestMethod.POST })
	@ResponseBody
	public void LOTTERY_USER_BETS_STATUS(HttpSession session, HttpServletRequest request, HttpServletResponse response) {
		String actionKey = WUC.LOTTERY_USER_BETS_STATUS;
		long t1 = System.currentTimeMillis();
		WebJSONObject json = new WebJSONObject(super.getAdminDataFactory());
		AdminUser uEntity = super.getCurrUser(session, request, response);
		if (uEntity != null) {
			if (super.hasAccess(uEntity, actionKey)) {
				String username = request.getParameter("username");
				int status = HttpUtil.getIntParameter(request, "status");
				String message = request.getParameter("message");
				boolean result = uService.bStatus(username, status, message);
				if(result) {
					adminUserLogJob.logModBStatus(uEntity, request, username, status, message);
					json.set(0, "0-5");
				} else {
					json.set(1, "1-5");
				}
			} else {
				json.set(2, "2-4");
			}
		} else {
			json.set(2, "2-6");
		}
		long t2 = System.currentTimeMillis();
		if (uEntity != null) {
			adminUserActionLogJob.add(request, actionKey, uEntity, json, t2 - t1);
		}
		HttpUtil.write(response, json.toString(), HttpUtil.json);
	}

	@RequestMapping(value = WUC.LOTTERY_USER_PROFILE_GET, method = { RequestMethod.POST })
	@ResponseBody
	public void LOTTERY_USER_PROFILE_GET(HttpSession session, HttpServletRequest request, HttpServletResponse response) {
		String actionKey = WUC.LOTTERY_USER_PROFILE_GET;
		long t1 = System.currentTimeMillis();
		WebJSONObject json = new WebJSONObject(super.getAdminDataFactory());
		AdminUser uEntity = super.getCurrUser(session, request, response);
		if (uEntity != null) {
			if (super.hasAccess(uEntity, actionKey)) {
				// 个人信息
				String username = request.getParameter("username");
				UserProfileVO uBean = uService.getUserProfile(username);
				if(uBean != null) {
					int userId = uBean.getBean().getId();
					// 基础信息
					UserInfo infoBean = uInfoDao.get(userId);
					// // 合买信息
					// UserPlanInfo planInfoBean = uPlanInfoDao.get(userId);
					// 银行卡信息
					List<UserCardVO> clist = uCardService.getByUserId(userId);
					// 密保信息
					List<UserSecurityVO> slist = uSecurityService.getByUserId(userId);
					// // 配额信息
					// List<UserCodeQuotaVO> uQuota = uCodePointUtil.listSurplusQuota(uBean.getBean());

					// PT账号信息
					UserGameAccount ptAccount = uGameAccountDao.get(userId, Global.BILL_ACCOUNT_PT);
					// AG账号信息
					UserGameAccount agAccount = uGameAccountDao.get(userId, Global.BILL_ACCOUNT_AG);

					json.accumulate("UserProfile", uBean);
					json.accumulate("UserInfo", infoBean);
					// json.accumulate("UserPlanInfo", planInfoBean);
					json.accumulate("CardList", clist);
					json.accumulate("SecurityList", slist);
					// json.accumulate("QuotaList", uQuota);
					json.accumulate("PTAccount", ptAccount);
					json.accumulate("AGAccount", agAccount);
					json.set(0, "0-3");
				} else {
					json.set(1, "1-3");
				}
			} else {
				json.set(2, "2-4");
			}
		} else {
			json.set(2, "2-6");
		}
		long t2 = System.currentTimeMillis();
		if (uEntity != null) {
			adminUserActionLogJob.add(request, actionKey, uEntity, json, t2 - t1);
		}
		HttpUtil.write(response, json.toString(), HttpUtil.json);
	}

	@RequestMapping(value = WUC.LOTTERY_USER_MODIFY_LOGIN_PWD, method = { RequestMethod.POST })
	@ResponseBody
	public void LOTTERY_USER_MODIFY_LOGIN_PWD(HttpSession session, HttpServletRequest request, HttpServletResponse response) {
		String actionKey = WUC.LOTTERY_USER_MODIFY_LOGIN_PWD;
		long t1 = System.currentTimeMillis();
		WebJSONObject json = new WebJSONObject(super.getAdminDataFactory());
		AdminUser uEntity = super.getCurrUser(session, request, response);
		if (uEntity != null) {
			if (super.hasAccess(uEntity, actionKey)) {
				String username = request.getParameter("username");
				String password = request.getParameter("password");
				boolean result = uService.modifyLoginPwd(username, password);
				if(result) {
					adminUserLogJob.logModLoginPwd(uEntity, request, username);
					adminUserCriticalLogJob.logModLoginPwd(uEntity, request, username,actionKey);
					json.set(0, "0-5");
				} else {
					json.set(1, "1-5");
				}
			} else {
				json.set(2, "2-4");
			}
		} else {
			json.set(2, "2-6");
		}
		long t2 = System.currentTimeMillis();
		if (uEntity != null) {
			adminUserActionLogJob.add(request, actionKey, uEntity, json, t2 - t1);
		}
		HttpUtil.write(response, json.toString(), HttpUtil.json);
	}

	@RequestMapping(value = WUC.LOTTERY_USER_MODIFY_WITHDRAW_PWD, method = { RequestMethod.POST })
	@ResponseBody
	public void LOTTERY_USER_MODIFY_WITHDRAW_PWD(HttpSession session, HttpServletRequest request, HttpServletResponse response) {
		String actionKey = WUC.LOTTERY_USER_MODIFY_WITHDRAW_PWD;
		long t1 = System.currentTimeMillis();
		WebJSONObject json = new WebJSONObject(super.getAdminDataFactory());
		AdminUser uEntity = super.getCurrUser(session, request, response);
		if (uEntity != null) {
			if (super.hasAccess(uEntity, actionKey)) {
				String username = request.getParameter("username");
				String password = request.getParameter("password");
				User targetUser = uDao.getByUsername(username);
				if(targetUser != null) {
					if(targetUser.getBindStatus() == 1) {
						boolean result = uService.modifyWithdrawPwd(username, password);
						if(result) {
							adminUserLogJob.logModWithdrawPwd(uEntity, request, username);
							adminUserCriticalLogJob.logModWithdrawPwd(uEntity, request, username,actionKey);
							json.set(0, "0-5");
						} else {
							json.set(1, "1-5");
						}
					} else {
						json.set(2, "2-1016");
					}
				} else {
					json.set(2, "2-3");
				}
			} else {
				json.set(2, "2-4");
			}
		} else {
			json.set(2, "2-6");
		}
		long t2 = System.currentTimeMillis();
		if (uEntity != null) {
			adminUserActionLogJob.add(request, actionKey, uEntity, json, t2 - t1);
		}
		HttpUtil.write(response, json.toString(), HttpUtil.json);
	}

	@RequestMapping(value = WUC.LOTTERY_USER_MODIFY_WITHDRAW_NAME, method = { RequestMethod.POST })
	@ResponseBody
	public void LOTTERY_USER_MODIFY_WITHDRAW_NAME(HttpSession session, HttpServletRequest request, HttpServletResponse response) {
		String actionKey = WUC.LOTTERY_USER_MODIFY_WITHDRAW_NAME;
		long t1 = System.currentTimeMillis();
		WebJSONObject json = new WebJSONObject(super.getAdminDataFactory());
		AdminUser uEntity = super.getCurrUser(session, request, response);
		if (uEntity != null) {
			if (super.hasAccess(uEntity, actionKey)) {
				String username = request.getParameter("username");
				String withdrawName = request.getParameter("withdrawName");

				if (StringUtils.isEmpty(username) || StringUtils.isEmpty(withdrawName)) {
					json.set(2, "2-2");
					HttpUtil.write(response, json.toString(), HttpUtil.json);
					return;
				}

				User targetUser = uDao.getByUsername(username);
				if(targetUser != null) {
					if(targetUser.getBindStatus() == 1) {
						boolean result = uService.modifyWithdrawName(username, withdrawName);
						if(result) {
							adminUserLogJob.logModWithdrawName(uEntity, request, username, withdrawName);
							adminUserCriticalLogJob.logModWithdrawName(uEntity, request, username, withdrawName,actionKey);
							json.set(0, "0-5");
						} else {
							json.set(1, "1-5");
						}
					} else {
						json.set(2, "2-1016");
					}
				} else {
					json.set(2, "2-3");
				}
			} else {
				json.set(2, "2-4");
			}
		} else {
			json.set(2, "2-6");
		}
		long t2 = System.currentTimeMillis();
		if (uEntity != null) {
			adminUserActionLogJob.add(request, actionKey, uEntity, json, t2 - t1);
		}
		HttpUtil.write(response, json.toString(), HttpUtil.json);
	}

	@RequestMapping(value = WUC.LOTTERY_USER_RESET_IMAGE_PWD, method = { RequestMethod.POST })
	@ResponseBody
	public void LOTTERY_USER_RESET_IMAGE_PWD(HttpSession session, HttpServletRequest request, HttpServletResponse response) {
		String actionKey = WUC.LOTTERY_USER_RESET_IMAGE_PWD;
		long t1 = System.currentTimeMillis();
		WebJSONObject json = new WebJSONObject(super.getAdminDataFactory());
		AdminUser uEntity = super.getCurrUser(session, request, response);
		if (uEntity != null) {
			if (super.hasAccess(uEntity, actionKey)) {
				String username = request.getParameter("username");
				boolean result = uService.resetImagePwd(username);
				if(result) {
					json.set(0, "0-5");
				} else {
					json.set(1, "1-5");
				}
			} else {
				json.set(2, "2-4");
			}
		} else {
			json.set(2, "2-6");
		}
		long t2 = System.currentTimeMillis();
		if (uEntity != null) {
			adminUserActionLogJob.add(request, actionKey, uEntity, json, t2 - t1);
		}
		HttpUtil.write(response, json.toString(), HttpUtil.json);
	}

	@RequestMapping(value = WUC.LOTTERY_USER_MODIFY_POINT, method = { RequestMethod.POST })
	@ResponseBody
	public void LOTTERY_USER_MODIFY_POINT(HttpSession session, HttpServletRequest request, HttpServletResponse response) {
		String actionKey = WUC.LOTTERY_USER_MODIFY_POINT;
		long t1 = System.currentTimeMillis();
		WebJSONObject json = new WebJSONObject(super.getAdminDataFactory());
		AdminUser uEntity = super.getCurrUser(session, request, response);
		if (uEntity != null) {
			if (super.hasAccess(uEntity, actionKey)) {
				String username = request.getParameter("username");
				double locatePoint = HttpUtil.getDoubleParameter(request, "locatePoint");
				locatePoint = MathUtil.doubleFormat(locatePoint, 2);
				int code = uCodePointUtil.getUserCode(locatePoint);
				double notLocatePoint = uCodePointUtil.getNotLocatePoint(locatePoint);
				User targerUser = uDao.getByUsername(username);
				if(targerUser != null) {
					SysCodeRangeVO rBean = uValidate.loadEditPoint(targerUser);
					if(locatePoint >= rBean.getMinPoint() && locatePoint <= rBean.getMaxPoint()) {
						boolean result = uService.modifyLotteryPoint(username, code, locatePoint, notLocatePoint);
						if(result) {
							adminUserLogJob.logModPoint(uEntity, request, username, locatePoint);
							adminUserCriticalLogJob.logModPoint(uEntity, request, username, locatePoint, actionKey);
							json.set(0, "0-5");
						} else {
							json.set(1, "1-5");
						}
					} else {
						json.set(2, "2-9");
					}
				} else {
					json.set(2, "2-3");
				}
			} else {
				json.set(2, "2-4");
			}
		} else {
			json.set(2, "2-6");
		}
		long t2 = System.currentTimeMillis();
		if (uEntity != null) {
			adminUserActionLogJob.add(request, actionKey, uEntity, json, t2 - t1);
		}
		HttpUtil.write(response, json.toString(), HttpUtil.json);
	}

	@RequestMapping(value = WUC.LOTTERY_USER_DOWN_POINT, method = { RequestMethod.POST })
	@ResponseBody
	public void LOTTERY_USER_DOWN_POINT(HttpSession session, HttpServletRequest request, HttpServletResponse response) {
		String actionKey = WUC.LOTTERY_USER_DOWN_POINT;
		long t1 = System.currentTimeMillis();
		WebJSONObject json = new WebJSONObject(super.getAdminDataFactory());
		AdminUser uEntity = super.getCurrUser(session, request, response);
		if (uEntity != null) {
			if (super.hasAccess(uEntity, actionKey)) {
				String username = request.getParameter("username");
				boolean result = uService.downLinePoint(username);
				if(result) {
					adminUserLogJob.logDownPoint(uEntity, request, username);
					json.set(0, "0-5");
				} else {
					json.set(1, "1-5");
				}
			} else {
				json.set(2, "2-4");
			}
		} else {
			json.set(2, "2-6");
		}
		long t2 = System.currentTimeMillis();
		if (uEntity != null) {
			adminUserActionLogJob.add(request, actionKey, uEntity, json, t2 - t1);
		}
		HttpUtil.write(response, json.toString(), HttpUtil.json);
	}

	@RequestMapping(value = WUC.LOTTERY_USER_MODIFY_EXTRA_POINT, method = { RequestMethod.POST })
	@ResponseBody
	public void LOTTERY_USER_MODIFY_EXTRA_POINT(HttpSession session, HttpServletRequest request, HttpServletResponse response) {
		String actionKey = WUC.LOTTERY_USER_MODIFY_EXTRA_POINT;
		long t1 = System.currentTimeMillis();
		WebJSONObject json = new WebJSONObject(super.getAdminDataFactory());
		AdminUser uEntity = super.getCurrUser(session, request, response);
		if (uEntity != null) {
			if (super.hasAccess(uEntity, actionKey)) {
				String username = request.getParameter("username");
				double point = HttpUtil.getDoubleParameter(request, "point");
				point = MathUtil.doubleFormat(point, 1);
				boolean result = uService.modifyExtraPoint(username, point);
				if(result) {
					adminUserLogJob.logModExtraPoint(uEntity, request, username, point);
					adminUserCriticalLogJob.logModExtraPoint(uEntity, request, username, point, actionKey);
					json.set(0, "0-5");
				} else {
					json.set(1, "1-5");
				}
			} else {
				json.set(2, "2-4");
			}
		} else {
			json.set(2, "2-6");
		}
		long t2 = System.currentTimeMillis();
		if (uEntity != null) {
			adminUserActionLogJob.add(request, actionKey, uEntity, json, t2 - t1);
		}
		HttpUtil.write(response, json.toString(), HttpUtil.json);
	}

	@RequestMapping(value = WUC.LOTTERY_USER_MODIFY_QUOTA, method = { RequestMethod.POST })
	@ResponseBody
	public void LOTTERY_USER_MODIFY_QUOTA(HttpSession session, HttpServletRequest request, HttpServletResponse response) {
		String actionKey = WUC.LOTTERY_USER_MODIFY_QUOTA;
		long t1 = System.currentTimeMillis();
		WebJSONObject json = new WebJSONObject(super.getAdminDataFactory());
		AdminUser uEntity = super.getCurrUser(session, request, response);
		if (uEntity != null) {
			if (super.hasAccess(uEntity, actionKey)) {
				String username = request.getParameter("username");
				User uBean = uDao.getByUsername(username);
				if(uBean != null) {
					if(uBean.getType() == Global.USER_TYPE_PROXY) {
						int count1 = HttpUtil.getIntParameter(request, "count1");
						int count2 = HttpUtil.getIntParameter(request, "count2");
						int count3 = HttpUtil.getIntParameter(request, "count3");
						boolean result = uService.modifyQuota(username, count1, count2, count3);
						if(result) {
							json.set(0, "0-5");
						} else {
							json.set(1, "1-5");
						}
					} else {
						json.set(2, "2-2018");
					}
				} else {
					json.set(2, "2-3");
				}
			} else {
				json.set(2, "2-4");
			}
		} else {
			json.set(2, "2-6");
		}
		long t2 = System.currentTimeMillis();
		if (uEntity != null) {
			adminUserActionLogJob.add(request, actionKey, uEntity, json, t2 - t1);
		}
		HttpUtil.write(response, json.toString(), HttpUtil.json);
	}

	@RequestMapping(value = WUC.LOTTERY_USER_GET_POINT_INFO, method = { RequestMethod.POST })
	@ResponseBody
	public void LOTTERY_USER_GET_POINT_INFO(HttpSession session, HttpServletRequest request, HttpServletResponse response) {
		String username = request.getParameter("username");
		User entity = uDao.getByUsername(username);
		UserBaseVO uBean = new UserBaseVO(entity);
		SysCodeRangeVO rBean = uValidate.loadEditPoint(entity);
		JSONObject json = new JSONObject();
		json.accumulate("uBean", uBean);
		json.accumulate("rBean", rBean);
		HttpUtil.write(response, json.toString(), HttpUtil.json);
	}

	@RequestMapping(value = WUC.LOTTERY_USER_CHANGE_LINE, method = { RequestMethod.POST })
	@ResponseBody
	public void LOTTERY_USER_CHANGE_LINE(HttpSession session, HttpServletRequest request, HttpServletResponse response) {
		String actionKey = WUC.LOTTERY_USER_CHANGE_LINE;
		long t1 = System.currentTimeMillis();
		WebJSONObject json = new WebJSONObject(super.getAdminDataFactory());
		AdminUser uEntity = super.getCurrUser(session, request, response);
		if (uEntity != null) {
			if (super.hasAccess(uEntity, actionKey)) {
				int type = HttpUtil.getIntParameter(request, "type");
				String aUser = request.getParameter("aUser").trim();
				String bUser = request.getParameter("bUser").trim();

				if (StringUtils.equalsIgnoreCase(aUser, bUser)) {
					json.set(1, "1-5");
				}
				else {
					User aBean = uDao.getByUsername(aUser);
					User bBean = uDao.getByUsername(bUser);
					if(aBean != null && bBean != null) {

						// 不允许操作总账
						if (aBean.getUpid() == 0) {
							json.set(2, "2-33");
						}
						// 主管账号只能管理员才能操作
						else if ((uCodePointUtil.isLevel1Proxy(aBean) && uEntity.getRoleId() != 1)) {
							json.set(2, "2-31");
						}
						// 暂不允许从上级转移到下级
						else if (bBean.getUpids().indexOf("[" + aBean.getId() + "]") >= 0) {
							json.set(2, "2-2024");
						}
						// 已经是直属下级，不需要进行转移操作
						else if (aBean.getUpid() == bBean.getId()) {
							json.set(2, "2-2025");
						}
						else {
							boolean flag = uService.changeLine(type, aUser, bUser);
							if(flag) {
								adminUserLogJob.logChangeLine(uEntity, request, aUser, bUser);
								adminUserCriticalLogJob.logChangeLine(uEntity, request, aUser, bUser, actionKey);
								json.set(0, "0-5");
							} else {
								json.set(1, "1-5");
							}
						}
					} else {
						json.set(2, "2-32");
					}
				}
			} else {
				json.set(2, "2-4");
			}
		} else {
			json.set(2, "2-6");
		}
		long t2 = System.currentTimeMillis();
		if (uEntity != null) {
			adminUserActionLogJob.add(request, actionKey, uEntity, json, t2 - t1);
		}
		HttpUtil.write(response, json.toString(), HttpUtil.json);
	}

	@RequestMapping(value = WUC.LOTTERY_USER_MODIFY_EQUAL_CODE, method = { RequestMethod.POST })
	@ResponseBody
	public void LOTTERY_USER_MODIFY_EQUAL_CODE(HttpSession session, HttpServletRequest request, HttpServletResponse response) {
		String actionKey = WUC.LOTTERY_USER_MODIFY_EQUAL_CODE;
		long t1 = System.currentTimeMillis();
		WebJSONObject json = new WebJSONObject(super.getAdminDataFactory());
		AdminUser uEntity = super.getCurrUser(session, request, response);
		if (uEntity != null) {
			if (super.hasAccess(uEntity, actionKey)) {
				String username = request.getParameter("username");
				int status = HttpUtil.getIntParameter(request, "status");
				boolean result = uService.modifyEqualCode(username, status);
				if(result) {
					adminUserLogJob.logModEqualCode(uEntity, request, username, status);
					json.set(0, "0-5");
				} else {
					json.set(1, "1-5");
				}
			} else {
				json.set(2, "2-4");
			}
		} else {
			json.set(2, "2-6");
		}
		long t2 = System.currentTimeMillis();
		if (uEntity != null) {
			adminUserActionLogJob.add(request, actionKey, uEntity, json, t2 - t1);
		}
		HttpUtil.write(response, json.toString(), HttpUtil.json);
	}

	@RequestMapping(value = WUC.LOTTERY_USER_MODIFY_TRANSFERS, method = { RequestMethod.POST })
	@ResponseBody
	public void LOTTERY_USER_MODIFY_TRANSFERS(HttpSession session, HttpServletRequest request, HttpServletResponse response) {
		String actionKey = WUC.LOTTERY_USER_MODIFY_TRANSFERS;
		long t1 = System.currentTimeMillis();
		WebJSONObject json = new WebJSONObject(super.getAdminDataFactory());
		AdminUser uEntity = super.getCurrUser(session, request, response);
		if (uEntity != null) {
			if (super.hasAccess(uEntity, actionKey)) {
				String username = request.getParameter("username");
				int status = HttpUtil.getIntParameter(request, "status");
				boolean result = uService.modifyTransfers(username, status);
				if(result) {
					adminUserLogJob.logModTransfers(uEntity, request, username, status);
					json.set(0, "0-5");
				} else {
					json.set(1, "1-5");
				}
			} else {
				json.set(2, "2-4");
			}
		} else {
			json.set(2, "2-6");
		}
		long t2 = System.currentTimeMillis();
		if (uEntity != null) {
			adminUserActionLogJob.add(request, actionKey, uEntity, json, t2 - t1);
		}
		HttpUtil.write(response, json.toString(), HttpUtil.json);
	}

	@RequestMapping(value = WUC.LOTTERY_USER_MODIFY_WITHDRAW, method = { RequestMethod.POST })
	@ResponseBody
	public void LOTTERY_USER_MODIFY_WITHDRAW(HttpSession session, HttpServletRequest request, HttpServletResponse response) {
		String actionKey = WUC.LOTTERY_USER_MODIFY_WITHDRAW;
		long t1 = System.currentTimeMillis();
		WebJSONObject json = new WebJSONObject(super.getAdminDataFactory());
		AdminUser uEntity = super.getCurrUser(session, request, response);
		if (uEntity != null) {
			if (super.hasAccess(uEntity, actionKey)) {
				String username = request.getParameter("username");
				int status = HttpUtil.getIntParameter(request, "status");
				boolean result = uService.modifyWithdraw(username, status);
				if(result) {
					adminUserLogJob.logModWithdraw(uEntity, request, username, status);
					json.set(0, "0-5");
				} else {
					json.set(1, "1-5");
				}
			} else {
				json.set(2, "2-4");
			}
		} else {
			json.set(2, "2-6");
		}
		long t2 = System.currentTimeMillis();
		if (uEntity != null) {
			adminUserActionLogJob.add(request, actionKey, uEntity, json, t2 - t1);
		}
		HttpUtil.write(response, json.toString(), HttpUtil.json);
	}
	/**
	 * 修改个人平台转账
	 * @param session
	 * @param request
	 * @param response
	 */
	@RequestMapping(value = WUC.LOTTERY_USER_MODIFY_PLATFORM_TRANSFERS, method = { RequestMethod.POST })
	@ResponseBody
	public void LOTTERY_USER_MODIFY_PLATFORM_TRANSFERS(HttpSession session, HttpServletRequest request, HttpServletResponse response) {
		String actionKey = WUC.LOTTERY_USER_MODIFY_PLATFORM_TRANSFERS;
		long t1 = System.currentTimeMillis();
		WebJSONObject json = new WebJSONObject(super.getAdminDataFactory());
		AdminUser uEntity = super.getCurrUser(session, request, response);
		if (uEntity != null) {
			if (super.hasAccess(uEntity, actionKey)) {
				String username = request.getParameter("username");
				int status = HttpUtil.getIntParameter(request, "status");
				boolean result = uService.modifyPlatformTransfers(username, status);
				if(result) {
					adminUserLogJob.logModPlatformTransfers(uEntity, request, username, status);
					json.set(0, "0-5");
				} else {
					json.set(1, "1-5");
				}
			} else {
				json.set(2, "2-4");
			}
		} else {
			json.set(2, "2-6");
		}
		long t2 = System.currentTimeMillis();
		if (uEntity != null) {
			adminUserActionLogJob.add(request, actionKey, uEntity, json, t2 - t1);
		}
		HttpUtil.write(response, json.toString(), HttpUtil.json);
	}
	
	@RequestMapping(value = WUC.LOTTERY_USER_CHANGE_PROXY, method = { RequestMethod.POST })
	@ResponseBody
	public void LOTTERY_USER_CHANGE_PROXY(HttpSession session, HttpServletRequest request, HttpServletResponse response) {
		String actionKey = WUC.LOTTERY_USER_CHANGE_PROXY;
		long t1 = System.currentTimeMillis();
		WebJSONObject json = new WebJSONObject(super.getAdminDataFactory());
		AdminUser uEntity = super.getCurrUser(session, request, response);
		if (uEntity != null) {
			if (super.hasAccess(uEntity, actionKey)) {
				String username = request.getParameter("username");
				boolean result = uService.changeProxy(username);
				if(result) {
					adminUserLogJob.logChangeProxy(uEntity, request, username);
					json.set(0, "0-5");
				} else {
					json.set(1, "1-5");
				}
			} else {
				json.set(2, "2-4");
			}
		} else {
			json.set(2, "2-6");
		}
		long t2 = System.currentTimeMillis();
		if (uEntity != null) {
			adminUserActionLogJob.add(request, actionKey, uEntity, json, t2 - t1);
		}
		HttpUtil.write(response, json.toString(), HttpUtil.json);
	}

	@RequestMapping(value = WUC.LOTTERY_USER_UNBIND_GOOGLE, method = { RequestMethod.POST })
	@ResponseBody
	public void LOTTERY_USER_UNBIND_GOOGLE(HttpSession session, HttpServletRequest request, HttpServletResponse response) {
		String actionKey = WUC.LOTTERY_USER_UNBIND_GOOGLE;
		long t1 = System.currentTimeMillis();
		WebJSONObject json = new WebJSONObject(super.getAdminDataFactory());
		AdminUser uEntity = super.getCurrUser(session, request, response);
		if (uEntity != null) {
			if (super.hasAccess(uEntity, actionKey)) {
				String username = request.getParameter("username");
				boolean result = uService.unbindGoogle(username);
				if(result) {
					adminUserLogJob.unbindGoogle(uEntity, request, username);
					adminUserCriticalLogJob.unbindGoogle(uEntity, request, username, actionKey);
					json.set(0, "0-5");
				} else {
					json.set(1, "1-5");
				}
			} else {
				json.set(2, "2-4");
			}
		} else {
			json.set(2, "2-6");
		}
		long t2 = System.currentTimeMillis();
		if (uEntity != null) {
			adminUserActionLogJob.add(request, actionKey, uEntity, json, t2 - t1);
		}
		HttpUtil.write(response, json.toString(), HttpUtil.json);
	}

	@RequestMapping(value = WUC.LOTTERY_USER_UNBIND_RESET_LOCK_TIME, method = { RequestMethod.POST })
	@ResponseBody
	public void LOTTERY_USER_UNBIND_RESET_LOCK_TIME(HttpSession session, HttpServletRequest request, HttpServletResponse response) {
		String actionKey = WUC.LOTTERY_USER_UNBIND_RESET_LOCK_TIME;
		long t1 = System.currentTimeMillis();
		WebJSONObject json = new WebJSONObject(super.getAdminDataFactory());
		AdminUser uEntity = super.getCurrUser(session, request, response);
		if (uEntity != null) {
			if (super.hasAccess(uEntity, actionKey)) {
				String username = request.getParameter("username");
				boolean result = uService.resetLockTime(username);
				if(result) {
					adminUserLogJob.resetLockTime(uEntity, request, username);
					json.set(0, "0-5");
				} else {
					json.set(1, "1-5");
				}
			} else {
				json.set(2, "2-4");
			}
		} else {
			json.set(2, "2-6");
		}
		long t2 = System.currentTimeMillis();
		if (uEntity != null) {
			adminUserActionLogJob.add(request, actionKey, uEntity, json, t2 - t1);
		}
		HttpUtil.write(response, json.toString(), HttpUtil.json);
	}

	@RequestMapping(value = WUC.LOTTERY_USER_RESET_EMAIL, method = { RequestMethod.POST })
	@ResponseBody
	public void LOTTERY_USER_RESET_EMAIL(HttpSession session, HttpServletRequest request, HttpServletResponse response) {
		String actionKey = WUC.LOTTERY_USER_RESET_EMAIL;
		long t1 = System.currentTimeMillis();
		WebJSONObject json = new WebJSONObject(super.getAdminDataFactory());
		AdminUser uEntity = super.getCurrUser(session, request, response);
		if (uEntity != null) {
			if (super.hasAccess(uEntity, actionKey)) {
				String username = request.getParameter("username");
				boolean result = uInfoService.resetEmail(username);
				if(result) {
					adminUserLogJob.logResetEmail(uEntity, request, username);
					json.set(0, "0-5");
				} else {
					json.set(1, "1-5");
				}
			} else {
				json.set(2, "2-4");
			}
		} else {
			json.set(2, "2-6");
		}
		long t2 = System.currentTimeMillis();
		if (uEntity != null) {
			adminUserActionLogJob.add(request, actionKey, uEntity, json, t2 - t1);
		}
		HttpUtil.write(response, json.toString(), HttpUtil.json);
	}

	@RequestMapping(value = WUC.LOTTERY_USER_MODIFY_EMAIL, method = { RequestMethod.POST })
	@ResponseBody
	public void LOTTERY_USER_MODIFY_EMAIL(HttpSession session, HttpServletRequest request, HttpServletResponse response) {
		String actionKey = WUC.LOTTERY_USER_MODIFY_EMAIL;
		long t1 = System.currentTimeMillis();
		WebJSONObject json = new WebJSONObject(super.getAdminDataFactory());
		AdminUser uEntity = super.getCurrUser(session, request, response);
		if (uEntity != null) {
			if (super.hasAccess(uEntity, actionKey)) {
				String username = request.getParameter("username");
				String email = request.getParameter("email");
				boolean result = uInfoService.modifyEmail(username, email);
				if(result) {
					adminUserLogJob.logModEmail(uEntity, request, username, email);
					json.set(0, "0-5");
				} else {
					json.set(1, "1-5");
				}
			} else {
				json.set(2, "2-4");
			}
		} else {
			json.set(2, "2-6");
		}
		long t2 = System.currentTimeMillis();
		if (uEntity != null) {
			adminUserActionLogJob.add(request, actionKey, uEntity, json, t2 - t1);
		}
		HttpUtil.write(response, json.toString(), HttpUtil.json);
	}

	@RequestMapping(value = WUC.LOTTERY_USER_CHECK_EXIST, method = { RequestMethod.POST })
	@ResponseBody
	public void LOTTERY_USER_CHECK_EXIST(HttpSession session, HttpServletRequest request, HttpServletResponse response) {
		String username = HttpUtil.getStringParameterTrim(request, "username");
		username = username.toLowerCase();
		User bean = uDao.getByUsername(username);
		String isExist = bean == null ? "true" : "false";
		HttpUtil.write(response, isExist, HttpUtil.json);
	}

	@Autowired
	private UserWithdrawLimitService mUserWithdrawLimitService;

	@RequestMapping(value = WUC.LOTTERY_USER_RESERT_XIAOFEI, method = { RequestMethod.POST })
	@ResponseBody
	public void LOTTERY_USER_RESERT_XIAOFEI(HttpSession session, HttpServletRequest request, HttpServletResponse response) {
		String actionKey = WUC.LOTTERY_USER_RESERT_XIAOFEI;
		long t1 = System.currentTimeMillis();
		WebJSONObject json = new WebJSONObject(super.getAdminDataFactory());
		AdminUser uEntity = super.getCurrUser(session, request, response);
		if (uEntity != null) {
			if (super.hasAccess(uEntity, actionKey)) {
				String username = request.getParameter("username");
				User bean = uDao.getByUsername(username);
				boolean resetTotal = mUserWithdrawLimitService.delByUserId(bean.getId());
				if(resetTotal){
					adminUserLogJob.logResetLimit(uEntity, request, username);
					json.set(0, "0-5");
				} else {
					json.set(1, "1-5");
				}
			} else {
				json.set(2, "2-4");
			}
		} else {
			json.set(2, "2-6");
		}
		long t2 = System.currentTimeMillis();
		if (uEntity != null) {
			adminUserActionLogJob.add(request, actionKey, uEntity, json, t2 - t1);
		}
		HttpUtil.write(response, json.toString(), HttpUtil.json);

	}

	@RequestMapping(value = WUC.LOTTERY_USER_MODIFY_RELATED_UPPER, method = { RequestMethod.POST })
	@ResponseBody
	public void LOTTERY_USER_MODIFY_RELATED_INFO(HttpSession session, HttpServletRequest request, HttpServletResponse response) {
		String actionKey = WUC.LOTTERY_USER_MODIFY_RELATED_UPPER;
		long t1 = System.currentTimeMillis();
		WebJSONObject json = new WebJSONObject(super.getAdminDataFactory());
		AdminUser uEntity = super.getCurrUser(session, request, response);
		if (uEntity != null) {
			if (super.hasAccess(uEntity, actionKey)) {
				String username = HttpUtil.getStringParameterTrim(request, "username");
				String relatedUpUser = HttpUtil.getStringParameterTrim(request, "relatedUpUser");
				double relatedPoint = HttpUtil.getDoubleParameter(request, "relatedPoint");
				String remarks = HttpUtil.getStringParameterTrim(request, "remarks");
				if (StringUtils.isEmpty(username) || StringUtils.isEmpty(remarks)
						|| StringUtils.isEmpty(relatedUpUser)
						|| relatedPoint < 0
						|| relatedPoint > 1) {
					json.set(2, "2-2"); // 参数错误
				}
				else {
					boolean updated = uService.modifyRelatedUpper(json, username, relatedUpUser, relatedPoint);
					if(updated){
						adminUserLogJob.logModifyRelatedUpper(uEntity, request, username, relatedUpUser, relatedPoint,  remarks);
						json.set(0, "0-5");
					} else {
						if (json.getError() == null) {
							json.set(1, "1-5");
						}
					}
				}
			} else {
				json.set(2, "2-4");
			}
		} else {
			json.set(2, "2-6");
		}
		long t2 = System.currentTimeMillis();
		if (uEntity != null) {
			adminUserActionLogJob.add(request, actionKey, uEntity, json, t2 - t1);
		}
		HttpUtil.write(response, json.toString(), HttpUtil.json);
	}

	@RequestMapping(value = WUC.LOTTERY_USER_RELIVE_RELATED_UPPER, method = { RequestMethod.POST })
	@ResponseBody
	public void LOTTERY_USER_RELIVE_RELATED_INFO(HttpSession session, HttpServletRequest request, HttpServletResponse response) {
		String actionKey = WUC.LOTTERY_USER_RELIVE_RELATED_UPPER;
		long t1 = System.currentTimeMillis();
		WebJSONObject json = new WebJSONObject(super.getAdminDataFactory());
		AdminUser uEntity = super.getCurrUser(session, request, response);
		if (uEntity != null) {
			if (super.hasAccess(uEntity, actionKey)) {
				String username = HttpUtil.getStringParameterTrim(request, "username");
				String remarks = HttpUtil.getStringParameterTrim(request, "remarks");
				if (StringUtils.isEmpty(username)) {
					json.set(2, "2-2"); // 参数错误
				}
				else if (StringUtils.isEmpty(remarks)) {
					json.set(2, "2-30"); // 请输入备注
				}
				else {
					boolean updated = uService.reliveRelatedUpper(json, username);
					if(updated){
						adminUserLogJob.logReliveRelatedUpper(uEntity, request, username, remarks);
						json.set(0, "0-5");
					} else {
						if (json.getError() == null) {
							json.set(1, "1-5");
						}
					}
				}
			} else {
				json.set(2, "2-4");
			}
		} else {
			json.set(2, "2-6");
		}
		long t2 = System.currentTimeMillis();
		if (uEntity != null) {
			adminUserActionLogJob.add(request, actionKey, uEntity, json, t2 - t1);
		}
		HttpUtil.write(response, json.toString(), HttpUtil.json);
	}

	@RequestMapping(value = WUC.LOTTERY_USER_MODIFY_RELATED_USERS, method = { RequestMethod.POST })
	@ResponseBody
	public void LOTTERY_USER_MODIFY_RELATED_USERS(HttpSession session, HttpServletRequest request, HttpServletResponse response) {
		String actionKey = WUC.LOTTERY_USER_MODIFY_RELATED_USERS;
		long t1 = System.currentTimeMillis();
		WebJSONObject json = new WebJSONObject(super.getAdminDataFactory());
		AdminUser uEntity = super.getCurrUser(session, request, response);
		if (uEntity != null) {
			if (super.hasAccess(uEntity, actionKey)) {
				String username = HttpUtil.getStringParameterTrim(request, "username");
				String relatedUsers = HttpUtil.getStringParameterTrim(request, "relatedUsers");
				String remarks = HttpUtil.getStringParameterTrim(request, "remarks");
				if (StringUtils.isEmpty(username) || StringUtils.isEmpty(remarks)) {
					json.set(2, "2-2"); // 参数错误
				}
				else {
					boolean updated = uService.modifyRelatedUsers(json, username, relatedUsers);
					if(updated){
						adminUserLogJob.logModifyUpdateRelatedUsers(uEntity, request, username, relatedUsers, remarks);
						json.set(0, "0-5");
					} else {
						if (json.getError() == null) {
							json.set(1, "1-5");
						}
					}
				}
			} else {
				json.set(2, "2-4");
			}
		} else {
			json.set(2, "2-6");
		}
		long t2 = System.currentTimeMillis();
		if (uEntity != null) {
			adminUserActionLogJob.add(request, actionKey, uEntity, json, t2 - t1);
		}
		HttpUtil.write(response, json.toString(), HttpUtil.json);
	}

	@RequestMapping(value = WUC.LOTTERY_USER_LOCK_TEAM, method = { RequestMethod.POST })
	@ResponseBody
	public void LOTTERY_USER_LOCK_TEAM(HttpSession session, HttpServletRequest request, HttpServletResponse response) {
		String actionKey = WUC.LOTTERY_USER_LOCK_TEAM;
		long t1 = System.currentTimeMillis();
		WebJSONObject json = new WebJSONObject(super.getAdminDataFactory());
		AdminUser uEntity = super.getCurrUser(session, request, response);
		if (uEntity != null) {
			if (super.hasAccess(uEntity, actionKey)) {
				if (uEntity.getRoleId() != 1) {
					json.set(2, "2-37"); // 该操作必须使用管理员账号操作
				}
				else {
					String username = HttpUtil.getStringParameterTrim(request, "username");
					String remark = HttpUtil.getStringParameterTrim(request, "remark");
					if (StringUtils.isEmpty(username) || StringUtils.isEmpty(remark)) {
						json.set(2, "2-2"); // 参数错误
					}
					else {
						User uBean = uDao.getByUsername(username);
						if (uBean == null) {
							json.set(2, "2-32");
						}
						else {
							if (uBean.getId() == Global.USER_TOP_ID) {
								json.set(2, "2-33"); // 不允许操作总账号
							}
							else if (uCodePointUtil.isLevel1Proxy(uBean)) {
								json.set(2, "2-36"); // 不允许操作主管账号
							}
							else {
								boolean updated = uService.lockTeam(json, username, remark);
								if(updated){
									mailJob.sendLockTeam(username, uEntity.getUsername(), remark);
									adminUserLogJob.logLockTeam(uEntity, request, username, remark);
									json.set(0, "0-5");
								} else {
									if (json.getError() == null) {
										json.set(1, "1-5");
									}
								}
							}
						}
					}
				}
			} else {
				json.set(2, "2-4");
			}
		} else {
			json.set(2, "2-6");
		}
		long t2 = System.currentTimeMillis();
		if (uEntity != null) {
			adminUserActionLogJob.add(request, actionKey, uEntity, json, t2 - t1);
		}
		HttpUtil.write(response, json.toString(), HttpUtil.json);
	}
	//禁止团队取款
	@RequestMapping(value = WUC.LOTTERY_USER_PROHIBIT_TEAM_WITHDRAW, method = { RequestMethod.POST })
	@ResponseBody
	public void LOTTERY_USER_PROHIBIT_TEAM_WITHDRAW(HttpSession session, HttpServletRequest request, HttpServletResponse response) {
		String actionKey = WUC.LOTTERY_USER_PROHIBIT_TEAM_WITHDRAW;
		long t1 = System.currentTimeMillis();
		WebJSONObject json = new WebJSONObject(super.getAdminDataFactory());
		AdminUser uEntity = super.getCurrUser(session, request, response);
		if (uEntity != null) {
			if (super.hasAccess(uEntity, actionKey)) {
				if (uEntity.getRoleId() != 1) {
					json.set(2, "2-37"); // 该操作必须使用管理员账号操作
				}
				else {
					String username = HttpUtil.getStringParameterTrim(request, "username");
					if (StringUtils.isEmpty(username)) {
						json.set(2, "2-2"); // 参数错误
					}
					else {
						User uBean = uDao.getByUsername(username);
						if (uBean == null) {
							json.set(2, "2-32");
						}
						else {
							if (uBean.getId() == Global.USER_TOP_ID) {
								json.set(2, "2-33"); // 不允许操作总账号
							}
							else {
								boolean updated = uService.prohibitTeamWithdraw(json, username);
								if(updated){
									mailJob.sendProhibitTeamWithdraw(username, uEntity.getUsername());
									adminUserLogJob.prohibitTeamWithdraw(uEntity, request, username);
									json.set(0, "0-5");
								} else {
									if (json.getError() == null) {
										json.set(1, "1-5");
									}
								}
							}
						}
					}
				}
			} else {
				json.set(2, "2-4");
			}
		} else {
			json.set(2, "2-6");
		}
		long t2 = System.currentTimeMillis();
		if (uEntity != null) {
			adminUserActionLogJob.add(request, actionKey, uEntity, json, t2 - t1);
		}
		HttpUtil.write(response, json.toString(), HttpUtil.json);
	}
	/**
	 * 允许团队取款
	 * @param session
	 * @param request
	 * @param response
	 */
	@RequestMapping(value = WUC.LOTTERY_USER_ALLOW_TEAM_WITHDRAW, method = { RequestMethod.POST })
	@ResponseBody
	public void LOTTERY_USER_ALLOW_TEAM_WITHDRAW(HttpSession session, HttpServletRequest request, HttpServletResponse response) {
		String actionKey = WUC.LOTTERY_USER_ALLOW_TEAM_WITHDRAW;
		long t1 = System.currentTimeMillis();
		WebJSONObject json = new WebJSONObject(super.getAdminDataFactory());
		AdminUser uEntity = super.getCurrUser(session, request, response);
		if (uEntity != null) {
			if (super.hasAccess(uEntity, actionKey)) {
				if (uEntity.getRoleId() != 1) {
					json.set(2, "2-37"); // 该操作必须使用管理员账号操作
				}
				else {
					String username = HttpUtil.getStringParameterTrim(request, "username");
					if (StringUtils.isEmpty(username)) {
						json.set(2, "2-2"); // 参数错误
					}
					else {
						User uBean = uDao.getByUsername(username);
						if (uBean == null) {
							json.set(2, "2-32");
						}
						else {
							if (uBean.getId() == Global.USER_TOP_ID) {
								json.set(2, "2-33"); // 不允许操作总账号
							}
							else {
								boolean updated = uService.allowTeamWithdraw(json, username);
								if(updated){
									mailJob.sendAllowTeamWithdraw(username, uEntity.getUsername());
									adminUserLogJob.allowTeamWithdraw(uEntity, request, username);
									json.set(0, "0-5");
								} else {
									if (json.getError() == null) {
										json.set(1, "1-5");
									}
								}
							}
						}
					}
				}
			} else {
				json.set(2, "2-4");
			}
		} else {
			json.set(2, "2-6");
		}
		long t2 = System.currentTimeMillis();
		if (uEntity != null) {
			adminUserActionLogJob.add(request, actionKey, uEntity, json, t2 - t1);
		}
		HttpUtil.write(response, json.toString(), HttpUtil.json);
	}
	
	
	/**
	 * 允许团队上下级转账
	 * @param session
	 * @param request
	 * @param response
	 */
	@RequestMapping(value = WUC.LOTTERY_USER_ALLOW_TEAM_TRANSFERS, method = { RequestMethod.POST })
	@ResponseBody
	public void LOTTERY_USER_ALLOW_TEAM_TRANSFERS(HttpSession session, HttpServletRequest request, HttpServletResponse response) {
		String actionKey = WUC.LOTTERY_USER_ALLOW_TEAM_TRANSFERS;
		long t1 = System.currentTimeMillis();
		WebJSONObject json = new WebJSONObject(super.getAdminDataFactory());
		AdminUser uEntity = super.getCurrUser(session, request, response);
		if (uEntity != null) {
			if (super.hasAccess(uEntity, actionKey)) {
				if (uEntity.getRoleId() != 1) {
					json.set(2, "2-37"); // 该操作必须使用管理员账号操作
				}
				else {
					String username = HttpUtil.getStringParameterTrim(request, "username");
					if (StringUtils.isEmpty(username)) {
						json.set(2, "2-2"); // 参数错误
					}
					else {
						User uBean = uDao.getByUsername(username);
						if (uBean == null) {
							json.set(2, "2-32");
						}
						else {
							if (uBean.getId() == Global.USER_TOP_ID) {
								json.set(2, "2-33"); // 不允许操作总账号
							}
							else {
								boolean updated = uService.allowTeamTransfers(json, username);
								if(updated){
									mailJob.sendAllowTeamTransfers(username, uEntity.getUsername());
									adminUserLogJob.allowTeamTransfers(uEntity, request, username);
									json.set(0, "0-5");
								} else {
									if (json.getError() == null) {
										json.set(1, "1-5");
									}
								}
							}
						}
					}
				}
			} else {
				json.set(2, "2-4");
			}
		} else {
			json.set(2, "2-6");
		}
		long t2 = System.currentTimeMillis();
		if (uEntity != null) {
			adminUserActionLogJob.add(request, actionKey, uEntity, json, t2 - t1);
		}
		HttpUtil.write(response, json.toString(), HttpUtil.json);
	}
	
	/**
	 * 关闭团队上下级转账
	 * @param session
	 * @param request
	 * @param response
	 */
	@RequestMapping(value = WUC.LOTTERY_USER_PROHIBIT_TEAM_TRANSFERS, method = { RequestMethod.POST })
	@ResponseBody
	public void LOTTERY_USER_PROHIBIT_TEAM_TRANSFERS(HttpSession session, HttpServletRequest request, HttpServletResponse response) {
		String actionKey = WUC.LOTTERY_USER_PROHIBIT_TEAM_TRANSFERS;
		long t1 = System.currentTimeMillis();
		WebJSONObject json = new WebJSONObject(super.getAdminDataFactory());
		AdminUser uEntity = super.getCurrUser(session, request, response);
		if (uEntity != null) {
			if (super.hasAccess(uEntity, actionKey)) {
				if (uEntity.getRoleId() != 1) {
					json.set(2, "2-37"); // 该操作必须使用管理员账号操作
				}
				else {
					String username = HttpUtil.getStringParameterTrim(request, "username");
					if (StringUtils.isEmpty(username)) {
						json.set(2, "2-2"); // 参数错误
					}
					else {
						User uBean = uDao.getByUsername(username);
						if (uBean == null) {
							json.set(2, "2-32");
						}
						else {
							if (uBean.getId() == Global.USER_TOP_ID) {
								json.set(2, "2-33"); // 不允许操作总账号
							}
							else {
								boolean updated = uService.prohibitTeamTransfers(json, username);
								if(updated){
									mailJob.sendProhibitTeamTransfers(username, uEntity.getUsername());
									adminUserLogJob.prohibitTeamTransfers(uEntity, request, username);
									json.set(0, "0-5");
								} else {
									if (json.getError() == null) {
										json.set(1, "1-5");
									}
								}
							}
						}
					}
				}
			} else {
				json.set(2, "2-4");
			}
		} else {
			json.set(2, "2-6");
		}
		long t2 = System.currentTimeMillis();
		if (uEntity != null) {
			adminUserActionLogJob.add(request, actionKey, uEntity, json, t2 - t1);
		}
		HttpUtil.write(response, json.toString(), HttpUtil.json);
	}
	
	/**
	 * 允许团队平台转账
	 * @param session
	 * @param request
	 * @param response
	 */
	@RequestMapping(value = WUC.LOTTERY_USER_ALLOW_TEAM_PLATFORM_TRANSFERS, method = { RequestMethod.POST })
	@ResponseBody
	public void LOTTERY_USER_ALLOW_TEAM_PLATFORM_TRANSFERS(HttpSession session, HttpServletRequest request, HttpServletResponse response) {
		String actionKey = WUC.LOTTERY_USER_ALLOW_TEAM_PLATFORM_TRANSFERS;
		long t1 = System.currentTimeMillis();
		WebJSONObject json = new WebJSONObject(super.getAdminDataFactory());
		AdminUser uEntity = super.getCurrUser(session, request, response);
		if (uEntity != null) {
			if (super.hasAccess(uEntity, actionKey)) {
				if (uEntity.getRoleId() != 1) {
					json.set(2, "2-37"); // 该操作必须使用管理员账号操作
				}
				else {
					String username = HttpUtil.getStringParameterTrim(request, "username");
					if (StringUtils.isEmpty(username)) {
						json.set(2, "2-2"); // 参数错误
					}
					else {
						User uBean = uDao.getByUsername(username);
						if (uBean == null) {
							json.set(2, "2-32");
						}
						else {
							if (uBean.getId() == Global.USER_TOP_ID) {
								json.set(2, "2-33"); // 不允许操作总账号
							}
							else {
								boolean updated = uService.allowTeamPlatformTransfers(json, username);
								if(updated){
									mailJob.sendAllowTeamPlatformTransfers(username, uEntity.getUsername());
									adminUserLogJob.allowTeamPlatformTransfers(uEntity, request, username);
									json.set(0, "0-5");
								} else {
									if (json.getError() == null) {
										json.set(1, "1-5");
									}
								}
							}
						}
					}
				}
			} else {
				json.set(2, "2-4");
			}
		} else {
			json.set(2, "2-6");
		}
		long t2 = System.currentTimeMillis();
		if (uEntity != null) {
			adminUserActionLogJob.add(request, actionKey, uEntity, json, t2 - t1);
		}
		HttpUtil.write(response, json.toString(), HttpUtil.json);
	}
	
	/**
	 * 关闭团队平台转账
	 * @param session
	 * @param request
	 * @param response
	 */
	@RequestMapping(value = WUC.LOTTERY_USER_PROHIBIT_TEAM_PLATFORM_TRANSFERS, method = { RequestMethod.POST })
	@ResponseBody
	public void LOTTERY_USER_PROHIBIT_TEAM_PLATFORM_TRANSFERS(HttpSession session, HttpServletRequest request, HttpServletResponse response) {
		String actionKey = WUC.LOTTERY_USER_PROHIBIT_TEAM_PLATFORM_TRANSFERS;
		long t1 = System.currentTimeMillis();
		WebJSONObject json = new WebJSONObject(super.getAdminDataFactory());
		AdminUser uEntity = super.getCurrUser(session, request, response);
		if (uEntity != null) {
			if (super.hasAccess(uEntity, actionKey)) {
				if (uEntity.getRoleId() != 1) {
					json.set(2, "2-37"); // 该操作必须使用管理员账号操作
				}
				else {
					String username = HttpUtil.getStringParameterTrim(request, "username");
					if (StringUtils.isEmpty(username)) {
						json.set(2, "2-2"); // 参数错误
					}
					else {
						User uBean = uDao.getByUsername(username);
						if (uBean == null) {
							json.set(2, "2-32");
						}
						else {
							if (uBean.getId() == Global.USER_TOP_ID) {
								json.set(2, "2-33"); // 不允许操作总账号
							}
							else {
								boolean updated = uService.prohibitTeamPlatformTransfers(json, username);
								if(updated){
									mailJob.sendProhibitTeamPlatformTransfers(username, uEntity.getUsername());
									adminUserLogJob.prohibitTeamPlatformTransfers(uEntity, request, username);
									json.set(0, "0-5");
								} else {
									if (json.getError() == null) {
										json.set(1, "1-5");
									}
								}
							}
						}
					}
				}
			} else {
				json.set(2, "2-4");
			}
		} else {
			json.set(2, "2-6");
		}
		long t2 = System.currentTimeMillis();
		if (uEntity != null) {
			adminUserActionLogJob.add(request, actionKey, uEntity, json, t2 - t1);
		}
		HttpUtil.write(response, json.toString(), HttpUtil.json);
	}
	
	@RequestMapping(value = WUC.LOTTERY_USER_UN_LOCK_TEAM, method = { RequestMethod.POST })
	@ResponseBody
	public void LOTTERY_USER_UN_LOCK_TEAM(HttpSession session, HttpServletRequest request, HttpServletResponse response) {
		String actionKey = WUC.LOTTERY_USER_UN_LOCK_TEAM;
		long t1 = System.currentTimeMillis();
		WebJSONObject json = new WebJSONObject(super.getAdminDataFactory());
		AdminUser uEntity = super.getCurrUser(session, request, response);
		if (uEntity != null) {
			if (super.hasAccess(uEntity, actionKey)) {
				if (uEntity.getRoleId() != 1) {
					json.set(2, "2-37"); // 该操作必须使用管理员账号操作
				}
				else {
					String username = HttpUtil.getStringParameterTrim(request, "username");
					String remark = HttpUtil.getStringParameterTrim(request, "remark");
					if (StringUtils.isEmpty(username) || StringUtils.isEmpty(remark)) {
						json.set(2, "2-2"); // 参数错误
					}
					else {
						User uBean = uDao.getByUsername(username);
						if (uBean == null) {
							json.set(2, "2-32");
						}
						else {
							if (uBean.getId() == Global.USER_TOP_ID) {
								json.set(2, "2-33"); // 不允许操作总账号
							}
							else if (uCodePointUtil.isLevel1Proxy(uBean)) {
								json.set(2, "2-36"); // 不允许操作主管账号
							}
							else {
								boolean updated = uService.unLockTeam(json, username);
								if(updated){
									mailJob.sendUnLockTeam(username, uEntity.getUsername(), remark);
									adminUserLogJob.logUnLockTeam(uEntity, request, username, remark);
									json.set(0, "0-5");
								} else {
									if (json.getError() == null) {
										json.set(1, "1-5");
									}
								}
							}
						}
					}
				}
			} else {
				json.set(2, "2-4");
			}
		} else {
			json.set(2, "2-6");
		}
		long t2 = System.currentTimeMillis();
		if (uEntity != null) {
			adminUserActionLogJob.add(request, actionKey, uEntity, json, t2 - t1);
		}
		HttpUtil.write(response, json.toString(), HttpUtil.json);
	}

	@RequestMapping(value = WUC.LOTTERY_USER_TRANSFER, method = { RequestMethod.POST })
	@ResponseBody
	public void LOTTERY_USER_TRANSFER(HttpSession session, HttpServletRequest request, HttpServletResponse response) {
		String actionKey = WUC.LOTTERY_USER_TRANSFER;
		long t1 = System.currentTimeMillis();
		WebJSONObject json = new WebJSONObject(super.getAdminDataFactory());
		AdminUser uEntity = super.getCurrUser(session, request, response);
		if (uEntity != null) {
			if (super.hasAccess(uEntity, actionKey)) {
				double money = HttpUtil.getDoubleParameter(request, "money");
				String aUser = HttpUtil.getStringParameterTrim(request, "aUser");
				String bUser = HttpUtil.getStringParameterTrim(request, "bUser");
				String remarks = HttpUtil.getStringParameterTrim(request, "remarks");
				if (StringUtils.equalsIgnoreCase(aUser, bUser)) {
					json.set(1, "1-5");
				}
				else {
					if (money <= 0) {
						json.set(1, "1-5");
					}
					else {
						User aBean = uDao.getByUsername(aUser);
						User bBean = uDao.getByUsername(bUser);
						if(aBean != null && bBean != null) {
							boolean flag = uService.transfer(json, aBean, bBean, money,remarks);
							if(flag) {
								adminUserLogJob.logUserTransfer(uEntity, request, aUser, bUser, money,remarks);
								adminUserCriticalLogJob.logUserTransfer(uEntity, request, aUser, bUser, money, remarks,actionKey);
								mailJob.sendUserTransfer(aUser, bUser, money,remarks);
								json.set(0, "0-5");
							}
						} else {
							json.set(2, "2-32");
						}
					}
				}
			} else {
				json.set(2, "2-4");
			}
		} else {
			json.set(2, "2-6");
		}
		long t2 = System.currentTimeMillis();
		if (uEntity != null) {
			adminUserActionLogJob.add(request, actionKey, uEntity, json, t2 - t1);
		}
		HttpUtil.write(response, json.toString(), HttpUtil.json);
	}

	@RequestMapping(value = WUC.LOTTERY_USER_WITHDRAW_LIMIT_LIST, method = { RequestMethod.POST })
	@ResponseBody
	public void LOTTERY_USER_WITHDRAW_LIMIT_LIST(HttpSession session, HttpServletRequest request, HttpServletResponse response) {
		String actionKey = WUC.LOTTERY_USER_WITHDRAW_LIMIT_LIST;
		long t1 = System.currentTimeMillis();
		WebJSONObject json = new WebJSONObject(super.getAdminDataFactory());
		AdminUser uEntity = super.getCurrUser(session, request, response);
		if (uEntity != null) {
			if (super.hasAccess(uEntity, actionKey)) {
				String username = HttpUtil.getStringParameterTrim(request, "username");
				Integer withdrawId = HttpUtil.getIntParameter(request, "withdrawId");
				int start = HttpUtil.getIntParameter(request, "start");
				int limit = HttpUtil.getIntParameter(request, "limit");
				UserVO user = dataFactory.getUser(username);

				String time;
				if (withdrawId != null) {
					UserWithdrawVO withdrawVO = uWithdrawService.getById(withdrawId);
					if (withdrawVO == null) {
						json.accumulate("totalCount", 0);
						json.accumulate("totalRemainConsumption",  "0.00");
						json.accumulate("data", "[]");
						HttpUtil.write(response, json.toString(), HttpUtil.json);
						return;
					}
					time = withdrawVO.getBean().getTime();
				}
				else {
				 	time = new Moment().toSimpleTime();
				}
				Map<String, Object> map = userWithdrawLimitService.getUserWithdrawLimits(user.getId(), time);
				if(map != null) {

					List<UserWithdrawLimitVO> list = (List<UserWithdrawLimitVO>) map.get("list");
					int totalCount = list.size();

					List<UserWithdrawLimitVO> subList = new ArrayList<>();

					if (start < list.size()) {
						for (int i = start; i < list.size(); i++) {
							if (subList.size() < limit) {
								subList.add(list.get(i));
							}
						}

						list = subList;
					}

					json.accumulate("totalCount", totalCount);
					json.accumulate("totalRemainConsumption",  map.get("totalRemainConsumption"));
					json.accumulate("data",  list);
				} else {
					json.accumulate("totalCount", 0);
					json.accumulate("totalRemainConsumption",  "0.00");
					json.accumulate("data", "[]");
				}
				json.set(0, "0-3");
			} else {
				json.set(2, "2-4");
			}
		} else {
			json.set(2, "2-6");
		}
		long t2 = System.currentTimeMillis();
		if (uEntity != null) {
			adminUserActionLogJob.add(request, actionKey, uEntity, json, t2 - t1);
		}
		HttpUtil.write(response, json.toString(), HttpUtil.json);
	}

	@RequestMapping(value = WUC.LOTTERY_USER_CHANGE_ZHAO_SHANG, method = { RequestMethod.POST })
	@ResponseBody
	public void LOTTERY_USER_CHANGE_ZHAO_SHANG(HttpSession session, HttpServletRequest request, HttpServletResponse response) {
		String actionKey = WUC.LOTTERY_USER_CHANGE_ZHAO_SHANG;
		long t1 = System.currentTimeMillis();
		WebJSONObject json = new WebJSONObject(super.getAdminDataFactory());
		AdminUser uEntity = super.getCurrUser(session, request, response);
		if (uEntity != null) {
			if (super.hasAccess(uEntity, actionKey)) {
				String username = HttpUtil.getStringParameterTrim(request, "username");
				int isCJZhaoShang = HttpUtil.getIntParameter(request, "isCJZhaoShang");
				boolean result = uService.changeZhaoShang(json, username, isCJZhaoShang);
				if(result) {
					adminUserLogJob.logChangeZhaoShang(uEntity, request, username, isCJZhaoShang);
					adminUserCriticalLogJob.logChangeZhaoShang(uEntity, request, username, isCJZhaoShang, actionKey);
					json.set(0, "0-5");
				}
			} else {
				json.set(2, "2-4");
			}
		} else {
			json.set(2, "2-6");
		}
		long t2 = System.currentTimeMillis();
		if (uEntity != null) {
			adminUserActionLogJob.add(request, actionKey, uEntity, json, t2 - t1);
		}
		HttpUtil.write(response, json.toString(), HttpUtil.json);
	}
}