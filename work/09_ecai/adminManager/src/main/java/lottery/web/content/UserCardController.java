package lottery.web.content;

import admin.domains.content.entity.AdminUser;
import admin.domains.jobs.AdminUserActionLogJob;
import admin.domains.jobs.AdminUserCriticalLogJob;
import admin.domains.jobs.AdminUserLogJob;
import admin.web.WUC;
import admin.web.WebJSONObject;
import admin.web.helper.AbstractActionController;
import javautils.http.HttpUtil;
import javautils.jdbc.PageList;
import lottery.domains.content.biz.UserBankcardUnbindService;
import lottery.domains.content.biz.UserCardService;
import lottery.domains.content.dao.UserCardDao;
import lottery.domains.content.dao.UserDao;
import lottery.domains.content.entity.User;
import lottery.domains.content.entity.UserCard;
import lottery.domains.content.vo.user.UserCardVO;
import lottery.web.content.validate.UserCardValidate;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@Controller
public class UserCardController extends AbstractActionController {

	@Autowired
	private AdminUserActionLogJob adminUserActionLogJob;
	
	@Autowired
	private AdminUserLogJob adminUserLogJob;
	
	@Autowired
	private AdminUserCriticalLogJob adminUserCriticalLogJob;
	
	@Autowired
	private UserDao uDao;
	
	@Autowired
	private UserCardDao uCardDao;
	
	@Autowired
	private UserCardService uCardService;
	
	@Autowired
	private UserBankcardUnbindService uBankcardUnbindService;
	
	@Autowired
	private UserCardValidate uCardValidate;

	
	@RequestMapping(value = WUC.LOTTERY_USER_CARD_LIST, method = { RequestMethod.POST })
	@ResponseBody
	public void LOTTERY_USER_CARD_LIST(HttpSession session, HttpServletRequest request, HttpServletResponse response) {
		String actionKey = WUC.LOTTERY_USER_CARD_LIST;
		long t1 = System.currentTimeMillis();
		WebJSONObject json = new WebJSONObject(super.getAdminDataFactory());
		AdminUser uEntity = super.getCurrUser(session, request, response);
		if (uEntity != null) {
			if (super.hasAccess(uEntity, actionKey)) {
				String username = request.getParameter("username");
				String keyword = request.getParameter("keyword");
				Integer status = HttpUtil.getIntParameter(request, "status");
				int start = HttpUtil.getIntParameter(request, "start");
				int limit = HttpUtil.getIntParameter(request, "limit");
				PageList pList = uCardService.search(username, keyword, status, start, limit);
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
	
	@RequestMapping(value = WUC.LOTTERY_USER_CARD_GET, method = { RequestMethod.POST })
	@ResponseBody
	public void LOTTERY_USER_CARD_GET(HttpSession session, HttpServletRequest request, HttpServletResponse response) {
		String actionKey = WUC.LOTTERY_USER_CARD_GET;
		long t1 = System.currentTimeMillis();
		WebJSONObject json = new WebJSONObject(super.getAdminDataFactory());
		AdminUser uEntity = super.getCurrUser(session, request, response);
		if (uEntity != null) {
			if (super.hasAccess(uEntity, actionKey)) {
				int id = HttpUtil.getIntParameter(request, "id");
				UserCardVO result = uCardService.getById(id);
				json.accumulate("data", result);
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
	
	@RequestMapping(value = WUC.LOTTERY_USER_CARD_EDIT, method = { RequestMethod.POST })
	@ResponseBody
	public void LOTTERY_USER_CARD_EDIT(HttpSession session, HttpServletRequest request, HttpServletResponse response) {
		String actionKey = WUC.LOTTERY_USER_CARD_EDIT;
		long t1 = System.currentTimeMillis();
		WebJSONObject json = new WebJSONObject(super.getAdminDataFactory());
		AdminUser uEntity = super.getCurrUser(session, request, response);
		if (uEntity != null) {
			if (super.hasAccess(uEntity, actionKey)) {
				int id = HttpUtil.getIntParameter(request, "id");
				int bankId = HttpUtil.getIntParameter(request, "bankId");
				String bankBranch = request.getParameter("bankBranch");
				String cardId = request.getParameter("cardId");
				UserCard cBean = uCardDao.getById(id);
				if(cBean != null) {
					User targetUser = uDao.getById(cBean.getUserId());
					if(targetUser != null) {
						String cardName = targetUser.getWithdrawName();
						if(uCardValidate.required(json, bankId, cardName, cardId)) {
							if(uCardValidate.checkCardId(cardId)) {
								UserCard exBean = uCardDao.getByCardId(cardId);
								if(exBean == null || exBean.getId() == id) {
									boolean result = uCardService.edit(id, bankId, bankBranch, cardId);
									if(result) {
										adminUserLogJob.logModUserCard(uEntity, request, targetUser.getUsername(), bankId, bankBranch, cardId);
										adminUserCriticalLogJob.logModUserCard(uEntity, request, targetUser.getUsername(), bankId, bankBranch, cardId, actionKey);
										json.set(0, "0-6");
									} else {
										json.set(1, "1-6");
									}
								} else {
									json.set(2, "2-1015");
								}
							} else {
								json.set(2, "2-1014");
							}
						}
					} else {
						json.set(2, "2-3");
					}
				} else {
					json.set(1, "1-6");
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
	
	@RequestMapping(value = WUC.LOTTERY_USER_CARD_ADD, method = { RequestMethod.POST })
	@ResponseBody
	public void LOTTERY_USER_CARD_ADD(HttpSession session, HttpServletRequest request, HttpServletResponse response) {
		String actionKey = WUC.LOTTERY_USER_CARD_ADD;
		long t1 = System.currentTimeMillis();
		WebJSONObject json = new WebJSONObject(super.getAdminDataFactory());
		AdminUser uEntity = super.getCurrUser(session, request, response);
		if (uEntity != null) {
			if (super.hasAccess(uEntity, actionKey)) {
				String username = request.getParameter("username");
				int bankId = HttpUtil.getIntParameter(request, "bankId");
				String bankBranch = request.getParameter("bankBranch");
				String cardId = request.getParameter("cardId");
				User targetUser = uDao.getByUsername(username);
				if(targetUser != null) {
					if(targetUser.getBindStatus() == 1) {
						String cardName = targetUser.getWithdrawName();
						if(uCardValidate.required(json, bankId, cardName, cardId)) {
							if(uCardValidate.checkCardId(cardId)) {
								if(uCardDao.getByCardId(cardId) == null) {
									boolean result = uCardService.add(username, bankId, bankBranch, cardName, cardId, 0);
									if(result) {
										adminUserLogJob.logAddUserCard(uEntity, request, username, bankId, bankBranch, cardId);
										adminUserCriticalLogJob.logAddUserCard(uEntity, request, username, bankId, bankBranch, cardId, actionKey);
										json.set(0, "0-6");
									} else {
										json.set(1, "1-6");
									}
								} else {
									json.set(2, "2-1015");
								}
							} else {
								json.set(2, "2-1014");
							}
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
	
	@RequestMapping(value = WUC.LOTTERY_USER_CARD_LOCK_STATUS, method = { RequestMethod.POST })
	@ResponseBody
	public void LOTTERY_USER_CARD_LOCK_STATUS(HttpSession session, HttpServletRequest request, HttpServletResponse response) {
		String actionKey = WUC.LOTTERY_USER_CARD_LOCK_STATUS;
		long t1 = System.currentTimeMillis();
		WebJSONObject json = new WebJSONObject(super.getAdminDataFactory());
		AdminUser uEntity = super.getCurrUser(session, request, response);
		if (uEntity != null) {
			if (super.hasAccess(uEntity, actionKey)) {
				int id = HttpUtil.getIntParameter(request, "id");
				int status = HttpUtil.getIntParameter(request, "status");
				boolean result = uCardService.updateStatus(id, status);
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

	@RequestMapping(value = WUC.LOTTERY_USER_UNBIND_LIST, method = { RequestMethod.POST })
	@ResponseBody
	public void LOTTERY_USER_UNBIND_LIST(HttpSession session, HttpServletRequest request, HttpServletResponse response) {
		String actionKey = WUC.LOTTERY_USER_UNBIND_LIST;
		long t1 = System.currentTimeMillis();
		WebJSONObject json = new WebJSONObject(super.getAdminDataFactory());
		AdminUser uEntity = super.getCurrUser(session, request, response);
		if (uEntity != null) {
			String userName = request.getParameter("username");
			String cardId = request.getParameter("cardId");
			String unbindTime = request.getParameter("unbindTime");
			int start = HttpUtil.getIntParameter(request, "start");
			int limit = HttpUtil.getIntParameter(request, "limit");
			PageList pList = uBankcardUnbindService.search(userName, cardId, 
					unbindTime, start, limit);
			if(pList != null) {
				json.accumulate("totalCount", pList.getCount());
				json.accumulate("data", pList.getList());
			} else {
				json.accumulate("totalCount", 0);
				json.accumulate("data", "[]");
			}
			json.set(0, "0-3");
		} else {
			json.set(2, "2-6");
		}
		long t2 = System.currentTimeMillis();
		if (uEntity != null) {
			adminUserActionLogJob.add(request, actionKey, uEntity, json, t2 - t1);
		}
		HttpUtil.write(response, json.toString(), HttpUtil.json);
	}


	@RequestMapping(value = WUC.LOTTERY_USER_UNBIND_DEL, method = { RequestMethod.POST })
	@ResponseBody
	public void LOTTERY_USER_UNBIND_DEL(HttpSession session, HttpServletRequest request, HttpServletResponse response) {
		String actionKey = WUC.LOTTERY_USER_UNBIND_DEL;
		long t1 = System.currentTimeMillis();
		WebJSONObject json = new WebJSONObject(super.getAdminDataFactory());
		AdminUser uEntity = super.getCurrUser(session, request, response);
		if (uEntity != null) {
			if (super.hasAccess(uEntity, actionKey)) {
				String cardId = HttpUtil.getStringParameterTrim(request, "cardId");
				String remark = HttpUtil.getStringParameterTrim(request, "remark");

				if (StringUtils.isEmpty(cardId)) {
					json.set(2, "2-2");
					HttpUtil.write(response, json.toString(), HttpUtil.json);
					return;
				}
				if (StringUtils.isEmpty(remark)) {
					json.set(2, "2-30");
					HttpUtil.write(response, json.toString(), HttpUtil.json);
					return;
				}
				if (remark.length() > 128) {
					json.set(2, "2-35");
					HttpUtil.write(response, json.toString(), HttpUtil.json);
					return;
				}

				boolean result = uBankcardUnbindService.delByCardId(cardId);
				if(result) {
					adminUserLogJob.logDelUserCardUnbindRecord(uEntity, request, cardId, remark);
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
