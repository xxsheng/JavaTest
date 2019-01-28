package admin.web.content;

import admin.domains.content.biz.AdminUserRoleService;
import admin.domains.content.biz.AdminUserService;
import admin.domains.content.dao.AdminUserDao;
import admin.domains.content.entity.AdminUser;
import admin.domains.content.vo.AdminUserRoleVO;
import admin.domains.content.vo.AdminUserVO;
import admin.domains.jobs.AdminUserActionLogJob;
import admin.web.WUC;
import admin.web.WebJSONObject;
import admin.web.helper.AbstractActionController;
import javautils.StringUtil;
import javautils.date.DateUtil;
import javautils.encrypt.PasswordUtil;
import javautils.http.HttpUtil;
import lottery.domains.pool.LotteryDataFactory;
import net.sf.json.JSONObject;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.List;

@Controller
public class AdminUserController extends AbstractActionController {

	@Autowired
	private AdminUserDao adminUserDao;

	@Autowired
	private AdminUserService adminUserService;
	@Autowired
	private AdminUserRoleService roleService;

	@Autowired
	private AdminUserActionLogJob adminUserActionLogJob;

	@Autowired
	private LotteryDataFactory dataFactory;

	@RequestMapping(value = WUC.DISPOSABLE_TOKEN, method = { RequestMethod.POST })
	@ResponseBody
	public void DISPOSABLE_TOKEN(HttpSession session, HttpServletRequest request, HttpServletResponse response) {
		WebJSONObject json = new WebJSONObject(super.getAdminDataFactory());
		json.set(0, "0-3");
		String token = generateDisposableToken(session, request);
		json.accumulate("token", token);
		HttpUtil.write(response, json.toString(), HttpUtil.json);
	}

	@RequestMapping(value = WUC.LOGIN, method = { RequestMethod.POST })
	@ResponseBody
	public void LOGIN(HttpSession session, HttpServletRequest request, HttpServletResponse response) {
		String actionKey = WUC.LOGIN;
		long t1 = System.currentTimeMillis();
		WebJSONObject json = new WebJSONObject(super.getAdminDataFactory());
		AdminUser uEntity = super.getCurrUser(session, request, response);
		String clientIpAddr = HttpUtil.getClientIpAddr(request);
		if (uEntity == null) {
			String username = request.getParameter("username");
			if (StringUtil.isNotNull(username)) {
				uEntity = adminUserDao.getByUsername(username);
			}
		}

		if (uEntity == null) {
			json.set(2, "2-3"); // 用户名或密码错误
			HttpUtil.write(response, json.toString(), HttpUtil.json);
			return;
		}

//		String[] infos = IpUtil.find(clientIpAddr);
//		String address = Arrays.toString(infos);
//		if (StringUtils.isEmpty(address) || (address.indexOf("菲律宾") <= -1 && address.indexOf("本机地址") <= -1)) {
//			json.set(2, "2-23");
//			HttpUtil.write(response, json.toString(), HttpUtil.json);
//			return;
//		}

		String password = request.getParameter("password");
		String token = getDisposableToken(session, request);
		if (!PasswordUtil.validatePassword(uEntity.getPassword(), token, password)) {
			adminUserService.updatePwdError(uEntity.getId(), uEntity.getPwd_error()+1);
			json.set(2, "2-3");
			HttpUtil.write(response, json.toString(), HttpUtil.json);
			return;
		}
		if(uEntity.getPwd_error() >=3){
			// 密码错误3次
			json.set(2, "2-22");
			HttpUtil.write(response, json.toString(), HttpUtil.json);
			return;
		}
		if(uEntity.getStatus() != 0){
			// 冻结
			json.set(2, "2-21");
			HttpUtil.write(response, json.toString(), HttpUtil.json);
			return;
		}

		if (dataFactory.getAdminGoogleConfig().isLoginStatus()) {
			if (StringUtils.isEmpty(uEntity.getSecretKey()) || uEntity.getIsValidate() != 1) {
				super.setGoogleBindUser(session, uEntity);
				json.set(2, "2-27"); // 绑定谷歌
				HttpUtil.write(response, json.toString(), HttpUtil.json);
				return;
			}
			int googlecode = Integer.parseInt(request.getParameter("googlecode"));
			if(!adminUserService.authoriseUser(uEntity.getUsername(), googlecode)){
				// 谷歌
				json.set(2, "2-24");
				HttpUtil.write(response, json.toString(), HttpUtil.json);
				return;
			}
		}

		AdminUserRoleVO role = roleService.getById(uEntity.getRoleId());
		if (role == null || role.getBean().getStatus() != 0) {
			// 角色
			json.set(2, "2-26");
			HttpUtil.write(response, json.toString(), HttpUtil.json);
			return;
		}

		super.setSessionUser(session, uEntity);
		String loginTime = DateUtil.getCurrentTime();
		adminUserService.updateLoginTime(uEntity.getId(), loginTime);
		adminUserService.updatePwdError(uEntity.getId(), 0);
		json.set(0, "0-5");
		long t2 = System.currentTimeMillis();
		adminUserActionLogJob.add(request, actionKey, uEntity, json, t2 - t1);
		HttpUtil.write(response, json.toString(), HttpUtil.json);
	}

	@RequestMapping(value = WUC.ADMIN_USER_LIST, method = { RequestMethod.POST })
	@ResponseBody
	public void ADMIN_USER_LIST(HttpSession session, HttpServletRequest request, HttpServletResponse response) {
		String actionKey = WUC.ADMIN_USER_LIST;
		long t1 = System.currentTimeMillis();
		WebJSONObject json = new WebJSONObject(super.getAdminDataFactory());
		AdminUser uEntity = super.getCurrUser(session, request, response);
		if (uEntity != null) {
			if (super.hasAccess(uEntity, actionKey)) {
				List<AdminUserVO> list = adminUserService.listAll(uEntity.getRoleId());
				json.set(0, "0-3");
				json.accumulate("data", list);
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

	@RequestMapping(value = WUC.ADMIN_USER_ADD, method = { RequestMethod.POST })
	@ResponseBody
	public void ADMIN_USER_ADD(HttpSession session, HttpServletRequest request, HttpServletResponse response) {
		String actionKey = WUC.ADMIN_USER_ADD;
		long t1 = System.currentTimeMillis();
		WebJSONObject json = new WebJSONObject(super.getAdminDataFactory());
		AdminUser uEntity = super.getCurrUser(session, request, response);
		if (uEntity != null) {
			if (super.hasAccess(uEntity, actionKey)) {
				String username = HttpUtil.getStringParameterTrim(request, "username");
				String password = request.getParameter("password");
				int roleId = HttpUtil.getIntParameter(request, "roleId");
				Integer setWithdrawPwd = HttpUtil.getIntParameter(request, "setWithdrawPwd");
				String withdrawPwd = HttpUtil.getStringParameterTrim(request, "withdrawPwd");

				if (!PasswordUtil.isSimplePasswordForGenerate(password)) {
					if (setWithdrawPwd != null && setWithdrawPwd == 1 ) {
						if (PasswordUtil.isSimplePasswordForGenerate(withdrawPwd)) {
							json.set(2, "2-41");
							HttpUtil.write(response, json.toString(), HttpUtil.json);
							return;
						}
						if (uEntity.getRoleId() != 1) {
							json.set(2, "2-37");
							HttpUtil.write(response, json.toString(), HttpUtil.json);
							return;
						}
					}

					if (dataFactory.getAdminGoogleConfig().isLoginStatus()) {
						if (StringUtils.isEmpty(uEntity.getSecretKey()) || uEntity.getIsValidate() != 1) {
							json.set(2, "2-27"); // 绑定谷歌
							HttpUtil.write(response, json.toString(), HttpUtil.json);
							return;
						}
						int googleCode = Integer.parseInt(request.getParameter("googleCode"));
						if(!adminUserService.authoriseUser(uEntity.getUsername(), googleCode)){
							// 谷歌
							json.set(2, "2-24");
							HttpUtil.write(response, json.toString(), HttpUtil.json);
							return;
						}
					}

					boolean result = adminUserService.add(username, password, roleId, setWithdrawPwd, withdrawPwd);
					if(result) {
						json.set(0, "0-6");
					} else {
						json.set(1, "1-6");
					}
				}
				else {
					json.set(2, "2-42");
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

	@RequestMapping(value = WUC.ADMIN_USER_EDIT, method = { RequestMethod.POST })
	@ResponseBody
	public void ADMIN_USER_EDIT(HttpSession session, HttpServletRequest request, HttpServletResponse response) {
		String actionKey = WUC.ADMIN_USER_EDIT;
		long t1 = System.currentTimeMillis();
		WebJSONObject json = new WebJSONObject(super.getAdminDataFactory());
		AdminUser uEntity = super.getCurrUser(session, request, response);
		if (uEntity != null) {
			if (super.hasAccess(uEntity, actionKey)) {
				String username = request.getParameter("username");
				String password = request.getParameter("password");
				int roleId = HttpUtil.getIntParameter(request, "roleId");
				Integer setWithdrawPwd = HttpUtil.getIntParameter(request, "setWithdrawPwd");
				String withdrawPwd = HttpUtil.getStringParameterTrim(request, "withdrawPwd");

				if (!PasswordUtil.isSimplePasswordForGenerate(password)) {

					if (setWithdrawPwd != null && setWithdrawPwd == 1 ) {
						if (PasswordUtil.isSimplePasswordForGenerate(withdrawPwd)) {
							json.set(2, "2-41");
							HttpUtil.write(response, json.toString(), HttpUtil.json);
							return;
						}
						if (uEntity.getRoleId() != 1) {
							json.set(2, "2-37");
							HttpUtil.write(response, json.toString(), HttpUtil.json);
							return;
						}
					}

					if (dataFactory.getAdminGoogleConfig().isLoginStatus()) {
						if (StringUtils.isEmpty(uEntity.getSecretKey()) || uEntity.getIsValidate() != 1) {
							json.set(2, "2-27"); // 绑定谷歌
							HttpUtil.write(response, json.toString(), HttpUtil.json);
							return;
						}
						int googleCode = Integer.parseInt(request.getParameter("googleCode"));
						if(!adminUserService.authoriseUser(uEntity.getUsername(), googleCode)){
							// 谷歌
							json.set(2, "2-24");
							HttpUtil.write(response, json.toString(), HttpUtil.json);
							return;
						}
					}

					boolean result = adminUserService.edit(username, password, roleId, setWithdrawPwd, withdrawPwd);
					if(result) {
						json.set(0, "0-6");
					} else {
						json.set(1, "1-6");
					}
				}
				else {
					json.set(2, "2-42");
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

	@RequestMapping(value = WUC.ADMIN_USER_UPDATE_STATUS, method = { RequestMethod.POST })
	@ResponseBody
	public void ADMIN_USER_UPDATE_STATUS(HttpSession session, HttpServletRequest request, HttpServletResponse response) {
		String actionKey = WUC.ADMIN_USER_UPDATE_STATUS;
		long t1 = System.currentTimeMillis();
		WebJSONObject json = new WebJSONObject(super.getAdminDataFactory());
		AdminUser uEntity = super.getCurrUser(session, request, response);
		if (uEntity != null) {
			if (super.hasAccess(uEntity, actionKey)) {
				int id = HttpUtil.getIntParameter(request, "id");
				int status = HttpUtil.getIntParameter(request, "status");
				boolean result = adminUserService.updateStatus(id, status);
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

	@RequestMapping(value = WUC.ADMIN_USER_CHECK_EXIST, method = { RequestMethod.POST })
	@ResponseBody
	public void ADMIN_USER_CHECK_EXIST(HttpSession session, HttpServletRequest request, HttpServletResponse response) {
		String username = request.getParameter("username");
		AdminUser bean = adminUserDao.getByUsername(username);
		String isExist = bean == null ? "true" : "false";
		HttpUtil.write(response, isExist, HttpUtil.json);
	}

	@RequestMapping(value = WUC.ADMIN_USER_GET, method = { RequestMethod.POST })
	@ResponseBody
	public void ADMIN_USER_GET(HttpSession session, HttpServletRequest request, HttpServletResponse response) {
		int id = HttpUtil.getIntParameter(request, "id");
		AdminUser uEntity = adminUserDao.getById(id);
		uEntity.setPassword("***"); // 不显示密码
		if (!"notset".equalsIgnoreCase(uEntity.getWithdrawPwd())) {
			uEntity.setWithdrawPwd("***"); // 不显示资金密码
		}
		uEntity.setSecretKey("***");
		JSONObject json = JSONObject.fromObject(uEntity);
		HttpUtil.write(response, json.toString(), HttpUtil.json);
	}

	@RequestMapping(value = WUC.ADMIN_USER_INFO, method = { RequestMethod.POST })
	@ResponseBody
	public void ADMIN_USER_INFO(HttpSession session, HttpServletRequest request, HttpServletResponse response) {
		WebJSONObject json = new WebJSONObject(super.getAdminDataFactory());
		AdminUser uEntity = super.getCurrUser(session, request, response);
		if (uEntity != null) {
			AdminUserVO bean = new AdminUserVO(uEntity, super.getAdminDataFactory());
			json.accumulate("data", bean);
			json.set(0, "0-3");
		} else {
			json.set(2, "2-6");
		}
		HttpUtil.write(response, json.toString(), HttpUtil.json);
	}

	@RequestMapping(value = WUC.ADMIN_USER_MOD_LOGIN_PWD, method = { RequestMethod.POST })
	@ResponseBody
	public void ADMIN_USER_MOD_LOGIN_PWD(HttpSession session, HttpServletRequest request, HttpServletResponse response) {
		WebJSONObject json = new WebJSONObject(super.getAdminDataFactory());
		AdminUser uEntity = super.getCurrUser(session, request, response);
		if (uEntity != null) {
			String oldPassword = request.getParameter("oldPassword");
			String password = request.getParameter("password");
			String token = getDisposableToken(session, request);

			if (dataFactory.getAdminGoogleConfig().isLoginStatus()) {
				if (StringUtils.isEmpty(uEntity.getSecretKey()) || uEntity.getIsValidate() != 1) {
					json.set(2, "2-27"); // 绑定谷歌
					HttpUtil.write(response, json.toString(), HttpUtil.json);
					return;
				}
				int googleCode = Integer.parseInt(request.getParameter("googleCode"));
				if(!adminUserService.authoriseUser(uEntity.getUsername(), googleCode)){
					json.set(2, "2-24");
					HttpUtil.write(response, json.toString(), HttpUtil.json);
					return;
				}
			}

			if(PasswordUtil.validatePassword(uEntity.getPassword(), token, oldPassword)) {
				if (!PasswordUtil.isSimplePasswordForGenerate(password)) {
					boolean result = adminUserService.modUserLoginPwd(uEntity.getId(), password);
					if(result) {
						json.set(0, "0-5");
					} else {
						json.set(1, "1-1");
					}
				}
				else {
					json.set(2, "2-42");
				}
			} else {
				json.set(2, "2-11");
			}
		} else {
			json.set(2, "2-6");
		}
		HttpUtil.write(response, json.toString(), HttpUtil.json);
	}

	@RequestMapping(value = WUC.ADMIN_USER_MOD_WITHDRAW_PWD, method = { RequestMethod.POST })
	@ResponseBody
	public void ADMIN_USER_MOD_WITHDRAW_PWD(HttpSession session, HttpServletRequest request, HttpServletResponse response) {
		WebJSONObject json = new WebJSONObject(super.getAdminDataFactory());
		AdminUser uEntity = super.getCurrUser(session, request, response);
		if (uEntity != null) {
			String oldPassword = request.getParameter("oldPassword");
			String password = request.getParameter("password");
			String token = getDisposableToken(session, request);

			if (dataFactory.getAdminGoogleConfig().isLoginStatus()) {
				if (StringUtils.isEmpty(uEntity.getSecretKey()) || uEntity.getIsValidate() != 1) {
					json.set(2, "2-27"); // 绑定谷歌
					HttpUtil.write(response, json.toString(), HttpUtil.json);
					return;
				}
				int googleCode = Integer.parseInt(request.getParameter("googleCode"));
				if(!adminUserService.authoriseUser(uEntity.getUsername(), googleCode)){
					json.set(2, "2-24");
					HttpUtil.write(response, json.toString(), HttpUtil.json);
					return;
				}
			}

			if(PasswordUtil.validatePassword(uEntity.getWithdrawPwd(), token, oldPassword)) {
				if (!PasswordUtil.isSimplePasswordForGenerate(password)) {
					boolean result = adminUserService.modUserWithdrawPwd(uEntity.getId(), password);
					if(result) {
						json.set(0, "0-5");
					} else {
						json.set(1, "1-1");
					}
				}
				else {
					json.set(2, "2-41");
				}
			} else {
				json.set(2, "2-11");
			}
		} else {
			json.set(2, "2-6");
		}
		HttpUtil.write(response, json.toString(), HttpUtil.json);
	}

	@RequestMapping(value = WUC.ADMIN_USER_CLOSE_GOOGLE_AUTH, method = { RequestMethod.POST })
	@ResponseBody
	public void ADMIN_USER_CLOSE_GOOGLE_AUTH(HttpSession session, HttpServletRequest request, HttpServletResponse response) {
		String actionKey = WUC.ADMIN_USER_CLOSE_GOOGLE_AUTH;
		long t1 = System.currentTimeMillis();
		WebJSONObject json = new WebJSONObject(super.getAdminDataFactory());
		AdminUser uEntity = super.getCurrUser(session, request, response);
		if (uEntity != null) {
			if (super.hasAccess(uEntity, actionKey)) {
				int id = HttpUtil.getIntParameter(request, "id");
				AdminUser upadteUser = adminUserDao.getById(id);
				upadteUser.setIsValidate(0);
				boolean result = adminUserDao.update(upadteUser);
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

	@RequestMapping(value = WUC.ADMIN_USER_CLOSE_WITHDRAW_PWD, method = { RequestMethod.POST })
	@ResponseBody
	public void ADMIN_USER_CLOSE_WITHDRAW_PWD(HttpSession session, HttpServletRequest request, HttpServletResponse response) {
		String actionKey = WUC.ADMIN_USER_CLOSE_WITHDRAW_PWD;
		long t1 = System.currentTimeMillis();
		WebJSONObject json = new WebJSONObject(super.getAdminDataFactory());
		AdminUser uEntity = super.getCurrUser(session, request, response);
		if (uEntity != null) {
			String username = HttpUtil.getStringParameterTrim(request, "username");

			if (StringUtils.isEmpty(username)) {
				json.set(2, "2-2");
				HttpUtil.write(response, json.toString(), HttpUtil.json);
				return;
			}

			if (uEntity.getRoleId() != 1) {
				json.set(2, "2-37");
				HttpUtil.write(response, json.toString(), HttpUtil.json);
				return;
			}

			if (dataFactory.getAdminGoogleConfig().isLoginStatus()) {
				if (StringUtils.isEmpty(uEntity.getSecretKey()) || uEntity.getIsValidate() != 1) {
					json.set(2, "2-27"); // 绑定谷歌
					HttpUtil.write(response, json.toString(), HttpUtil.json);
					return;
				}
				int googleCode = Integer.parseInt(request.getParameter("googleCode"));
				if(!adminUserService.authoriseUser(uEntity.getUsername(), googleCode)){
					json.set(2, "2-24");
					HttpUtil.write(response, json.toString(), HttpUtil.json);
					return;
				}
			}

			boolean result = adminUserService.closeWithdrawPwd(username);
			if(result) {
				json.set(0, "0-5");
			} else {
				json.set(1, "1-5");
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

	@RequestMapping(value = WUC.ADMIN_USER_OPEN_WITHDRAW_PWD, method = { RequestMethod.POST })
	@ResponseBody
	public void ADMIN_USER_OPEN_WITHDRAW_PWD(HttpSession session, HttpServletRequest request, HttpServletResponse response) {
		String actionKey = WUC.ADMIN_USER_OPEN_WITHDRAW_PWD;
		long t1 = System.currentTimeMillis();
		WebJSONObject json = new WebJSONObject(super.getAdminDataFactory());
		AdminUser uEntity = super.getCurrUser(session, request, response);
		if (uEntity != null) {
			String username = HttpUtil.getStringParameterTrim(request, "username");
			String withdrawPwd = HttpUtil.getStringParameterTrim(request, "withdrawPwd");
			if (StringUtils.isEmpty(username) || StringUtils.isEmpty(withdrawPwd)) {
				json.set(2, "2-2");
				HttpUtil.write(response, json.toString(), HttpUtil.json);
				return;
			}

			if (uEntity.getRoleId() != 1) {
				json.set(2, "2-37");
				HttpUtil.write(response, json.toString(), HttpUtil.json);
				return;
			}

			if (PasswordUtil.isSimplePasswordForGenerate(withdrawPwd)) {
				json.set(2, "2-41");
				HttpUtil.write(response, json.toString(), HttpUtil.json);
				return;
			}

			if (dataFactory.getAdminGoogleConfig().isLoginStatus()) {
				if (StringUtils.isEmpty(uEntity.getSecretKey()) || uEntity.getIsValidate() != 1) {
					json.set(2, "2-27"); // 绑定谷歌
					HttpUtil.write(response, json.toString(), HttpUtil.json);
					return;
				}
				int googleCode = Integer.parseInt(request.getParameter("googleCode"));
				if(!adminUserService.authoriseUser(uEntity.getUsername(), googleCode)){
					json.set(2, "2-24");
					HttpUtil.write(response, json.toString(), HttpUtil.json);
					return;
				}
			}

			boolean result = adminUserService.openWithdrawPwd(username, withdrawPwd);
			if(result) {
				json.set(0, "0-5");
			} else {
				json.set(1, "1-5");
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

	@RequestMapping(value = WUC.ADMIN_USER_UNLOCK_WITHDRAW_PWD, method = { RequestMethod.POST })
	@ResponseBody
	public void ADMIN_USER_UNLOCK_WITHDRAW_PWD(HttpSession session, HttpServletRequest request, HttpServletResponse response) {
		String actionKey = WUC.ADMIN_USER_UNLOCK_WITHDRAW_PWD;
		long t1 = System.currentTimeMillis();
		WebJSONObject json = new WebJSONObject(super.getAdminDataFactory());
		AdminUser uEntity = super.getCurrUser(session, request, response);
		if (uEntity != null) {
			String withdrawPwd = HttpUtil.getStringParameterTrim(request, "withdrawPwd");
			String token = getDisposableToken(session, request);
			if (StringUtils.isEmpty(withdrawPwd) || StringUtils.isEmpty(token)) {
				json.set(2, "2-2");
				HttpUtil.write(response, json.toString(), HttpUtil.json);
				return;
			}

			if (dataFactory.getAdminGoogleConfig().isLoginStatus()) {
				if (StringUtils.isEmpty(uEntity.getSecretKey()) || uEntity.getIsValidate() != 1) {
					json.set(2, "2-27"); // 绑定谷歌
					HttpUtil.write(response, json.toString(), HttpUtil.json);
					return;
				}
				int googleCode = Integer.parseInt(request.getParameter("googleCode"));
				if(!adminUserService.authoriseUser(uEntity.getUsername(), googleCode)){
					json.set(2, "2-24");
					HttpUtil.write(response, json.toString(), HttpUtil.json);
					return;
				}
			}

			if(PasswordUtil.validatePassword(uEntity.getWithdrawPwd(), token, withdrawPwd)) {
				boolean result = setUnlockWithdrawPwd(session, true);
				if(result) {
					json.set(0, "0-5");
				} else {
					json.set(1, "1-5");
				}
			} else {
				json.set(2, "2-12");
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

	@RequestMapping(value = WUC.ADMIN_USER_LOCK_WITHDRAW_PWD, method = { RequestMethod.POST })
	@ResponseBody
	public void ADMIN_USER_LOCK_WITHDRAW_PWD(HttpSession session, HttpServletRequest request, HttpServletResponse response) {
		String actionKey = WUC.ADMIN_USER_LOCK_WITHDRAW_PWD;
		long t1 = System.currentTimeMillis();
		WebJSONObject json = new WebJSONObject(super.getAdminDataFactory());
		AdminUser uEntity = super.getCurrUser(session, request, response);
		if (uEntity != null) {
			boolean result = setUnlockWithdrawPwd(session, false);
			if(result) {
				json.set(0, "0-5");
			} else {
				json.set(1, "1-5");
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

	@RequestMapping(value = WUC.ADMIN_USER_RESET_PWD_ERROR, method = { RequestMethod.POST })
	@ResponseBody
	public void ADMIN_USER_RESET_PWD_ERROR(HttpSession session, HttpServletRequest request, HttpServletResponse response) {
		String actionKey = WUC.ADMIN_USER_RESET_PWD_ERROR;
		long t1 = System.currentTimeMillis();
		WebJSONObject json = new WebJSONObject(super.getAdminDataFactory());
		AdminUser uEntity = super.getCurrUser(session, request, response);
		if (uEntity != null) {
			if (super.hasAccess(uEntity, actionKey)) {
				int id = HttpUtil.getIntParameter(request, "id");
				boolean updated = adminUserService.updatePwdError(id, 0);
				if(updated) {
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

	@RequestMapping(value = WUC.ADMIN_USER_EDIT_IPS, method = { RequestMethod.POST })
	@ResponseBody
	public void ADMIN_USER_EDIT_IPS(HttpSession session, HttpServletRequest request, HttpServletResponse response) {
		String actionKey = WUC.ADMIN_USER_EDIT_IPS;
		long t1 = System.currentTimeMillis();
		WebJSONObject json = new WebJSONObject(super.getAdminDataFactory());
		AdminUser uEntity = super.getCurrUser(session, request, response);
		if (uEntity != null) {
			if (super.hasAccess(uEntity, actionKey)) {
				int id = HttpUtil.getIntParameter(request, "id");
				String ips = request.getParameter("ips");
				boolean updated = adminUserService.updateIps(id, ips);
				if(updated) {
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
}