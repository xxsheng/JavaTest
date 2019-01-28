package admin.web.helper;

import admin.domains.content.biz.utils.JSMenuVO;
import admin.domains.content.biz.utils.TreeUtil;
import admin.domains.content.dao.AdminUserDao;
import admin.domains.content.entity.AdminUser;
import admin.domains.content.entity.AdminUserAction;
import admin.domains.content.entity.AdminUserMenu;
import admin.domains.content.entity.AdminUserRole;
import admin.domains.pool.AdminDataFactory;
import admin.web.WSC;
import admin.web.helper.session.SessionManager;
import admin.web.helper.session.SessionUser;
import javautils.StringUtil;
import javautils.encrypt.TokenUtil;
import lottery.domains.pool.LotteryDataFactory;
import net.sf.json.JSONArray;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.List;

public abstract class AbstractActionController {

	private static final Logger logger = LoggerFactory.getLogger(AbstractActionController.class);

	@Autowired
	private AdminDataFactory adminDataFactory;

	@Autowired
	private LotteryDataFactory lotteryDataFactory;

	@Autowired
	private AdminUserDao adminUserDao;

	protected final AdminDataFactory getAdminDataFactory() {
		return adminDataFactory;
	}

	protected final LotteryDataFactory getLotteryDataFactory() {
		return lotteryDataFactory;
	}

	/**
	 * 获取当前Session用户
	 * @param session
	 * @return
	 */
	protected final AdminUser getSessionUser(final HttpSession session) {
		final SessionUser sessionUser = SessionManager.getCurrentUser(session);
		if(sessionUser != null) {
			AdminUser uEntity = adminUserDao.getByUsername(sessionUser.getUsername());
			if(uEntity != null && uEntity.getPassword().equals(sessionUser.getPassword())) {
				return uEntity;
			}
		}
		return null;
	}

	protected final List<String> listSysConfigKey(final AdminUser bean) {
		List<String> list = new ArrayList<>();
		if(bean != null) {
			AdminUserRole adminUserRole = adminDataFactory.getAdminUserRole(bean.getRoleId());
			AdminUserMenu adminUserMenu = adminDataFactory.getAdminUserMenuByLink("lottery-sys-config");
			if(adminUserRole != null && adminUserMenu != null) {
				JSONArray uActions = JSONArray.fromObject(adminUserRole.getActions());
				JSONArray sActions = JSONArray.fromObject(adminUserMenu.getAllActions());
				for (Object uAid : uActions) {
					for (Object sAid : sActions) {
						if((int) uAid == (int) sAid) {
							AdminUserAction adminUserAction = adminDataFactory.getAdminUserAction((int) sAid);
							if(adminUserAction != null) {
								list.add(adminUserAction.getKey());
							}
						}
					}
				}
			}
		}
		return list;
	}

	/**
	 * 判断用户是否有操作权限
	 * @param bean
	 * @param actionKey
	 * @return
	 */
	protected final boolean hasAccess(final AdminUser bean, String actionKey) {
		// 判断用户和用户状态
		if(bean != null && bean.getStatus() == 0) {
			if(StringUtil.isNotNull(actionKey)) {
				AdminUserRole adminUserRole = adminDataFactory.getAdminUserRole(bean.getRoleId());
				// 判断角色和角色状态
				if(adminUserRole != null && adminUserRole.getStatus() == 0) {
					if(StringUtil.isNotNull(adminUserRole.getActions())) {
						JSONArray actionJson = JSONArray.fromObject(adminUserRole.getActions());
						List<AdminUserAction> adminUserActionList = new ArrayList<>();
						for (Object actionId : actionJson) {
							AdminUserAction adminUserAction = adminDataFactory.getAdminUserAction((int) actionId);
							// 判断行为和行为状态
							if(adminUserAction != null && adminUserAction.getStatus() == 0) {
								adminUserActionList.add(adminUserAction);
							}
						}
						// 进行匹配，看看有木有操作权限
						for (AdminUserAction adminUserAction : adminUserActionList) {
							if(actionKey.equals(adminUserAction.getKey())) {
								return true;
							}
						}
					}
				}
			}
		}
		return false;
	}

	/**
	 * 列出用户权限菜单
	 * @param bean
	 * @return
	 */
	protected final List<JSMenuVO> listUserMenu(final AdminUser bean) {
		List<AdminUserMenu> mlist = adminDataFactory.getAdminUserMenuByRoleId(bean.getRoleId());
		List<AdminUserMenu> list = new ArrayList<>();
		for (AdminUserMenu tmpBean : mlist) {
			if(tmpBean.getStatus() != -1) {
				list.add(tmpBean);
			}
		}
		return TreeUtil.listJSMenuRoot(TreeUtil.listMenuRoot(list));
	}

	/**
	 * 保存Session用户
	 * @param session
	 * @param uBean
	 */
	protected final void setSessionUser(final HttpSession session, final AdminUser bean) {
		SessionUser sessionUser = new SessionUser();
		sessionUser.setUsername(bean.getUsername());
		sessionUser.setPassword(bean.getPassword());
		sessionUser.setRoleId(bean.getRoleId());
		SessionManager.setCurrentUser(session, sessionUser);
	}

	// /**
	//  * 获取当前Cookie用户
	//  * @param request
	//  * @param response
	//  * @return
	//  */
	// protected final AdminUser getCookieUser(final HttpServletRequest request, final HttpServletResponse response) {
	// 	final CookieUser cookieUser = CookieManager.getCurrentUser(request);
	// 	if(cookieUser != null) {
	// 		AdminUser uEntity = adminUserDao.getByUsername(cookieUser.getUsername());
	// 		if(uEntity != null && uEntity.getPassword().equals(cookieUser.getPassword())) {
	// 			logger.info("Cookie用户验证通过..");
	// 			return uEntity;
	// 		}
	// 		logger.info("Cookie无效，清理Cookie..");
	// 		CookieManager.cleanUserCookie(request, response);
	// 	}
	// 	return null;
	// }
	//
	// /**
	//  * 保存Cookie用户
	//  * @param response
	//  * @param uBean
	//  */
	// protected final void setCookieUser(final HttpServletResponse response, final AdminUser bean) {
	// 	CookieUser cookieUser = new CookieUser();
	// 	cookieUser.setUsername(bean.getUsername());
	// 	cookieUser.setPassword(bean.getPassword());
	// 	CookieManager.setCurrentUser(response, cookieUser);
	// }

	protected final void setGoogleBindUser(final HttpSession session, final AdminUser bean) {
		session.setAttribute(WSC.SESSION_GOOGLE_USER, bean);
	}

	protected final AdminUser getGoogleBindUser(final HttpSession session) {
		Object attribute = session.getAttribute(WSC.SESSION_GOOGLE_USER);
		if (attribute == null) {
			return null;
		}
		return (AdminUser) attribute;
	}

	protected final void clearGoogleBindUser(final HttpSession session) {
		session.removeAttribute(WSC.SESSION_GOOGLE_USER);
	}

	/**
	 * 获取当前用户
	 * @param session
	 * @param request
	 * @param response
	 * @return
	 */
	protected final AdminUser getCurrUser(final HttpSession session, final HttpServletRequest request, final HttpServletResponse response) {
		try {
			AdminUser uEntity = getSessionUser(session);
			if(uEntity != null) {
				return uEntity;
			}
		} catch (Exception e) {
			logger.error("从session获取用户失败", e);
		}
		//
		// try {
		// 	AdminUser uEntity = getCookieUser(request, response);
		// 	if(uEntity != null) {
		// 		setSessionUser(session, uEntity);
		// 		return uEntity;
		// 	}
		// } catch (Exception e) {
		// 	logger.error("从cookie获取用户失败", e);
		// }
		return null;
	}

	/**
	 * 用户退出
	 * @param session
	 * @param request
	 * @param response
	 */
	protected final void logOut(final HttpSession session, final HttpServletRequest request, final HttpServletResponse response) {
		session.invalidate();
		// CookieManager.cleanUserCookie(request, response);
	}

	/**
	 * 生成一次性(一次性是指跟session生命周期一样)使用的加密token,如果session中已经存在，则会返回原来的
	 */
	protected String generateDisposableToken(HttpSession session, HttpServletRequest request) {
		Object attribute = session.getAttribute(WSC.DISPOSABLE_TOKEN);
		if (attribute != null) {
			return attribute.toString();
		}

		String tokenStr = TokenUtil.generateDisposableToken();
		session.setAttribute(WSC.DISPOSABLE_TOKEN, tokenStr);

		return tokenStr;
	}

	/**
	 * 获取一次性token
	 */
	protected String getDisposableToken(HttpSession session, HttpServletRequest request) {
		Object disposableToken = session.getAttribute(WSC.DISPOSABLE_TOKEN);
		if (disposableToken == null) return null;

		// session.removeAttribute(WSC.DISPOSABLE_TOKEN);

		return disposableToken.toString();
	}

	protected boolean setUnlockWithdrawPwd(HttpSession session, boolean unlocked) {
		session.setAttribute(WSC.SESSION_UNLOCK_WITHDARWPWD, unlocked);
		return true;
	}

	protected boolean isUnlockedWithdrawPwd(HttpSession session) {
		Object attribute = session.getAttribute(WSC.SESSION_UNLOCK_WITHDARWPWD);
		if (attribute == null) {
			return false;
		}

		return (Boolean) attribute;
	}
}
