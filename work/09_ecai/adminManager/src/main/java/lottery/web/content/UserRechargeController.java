package lottery.web.content;


import admin.domains.content.entity.AdminUser;
import admin.domains.jobs.AdminUserActionLogJob;
import admin.domains.jobs.AdminUserCriticalLogJob;
import admin.domains.jobs.AdminUserLogJob;
import admin.domains.jobs.MailJob;
import admin.web.WUC;
import admin.web.WebJSONObject;
import admin.web.helper.AbstractActionController;
import javautils.StringUtil;
import javautils.encrypt.PasswordUtil;
import javautils.http.HttpUtil;
import javautils.jdbc.PageList;
import lottery.domains.content.biz.PaymentChannelService;
import lottery.domains.content.biz.UserRechargeService;
import lottery.domains.content.vo.user.HistoryUserRechargeVO;
import lottery.domains.content.vo.user.UserRechargeVO;
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
public class UserRechargeController extends AbstractActionController {

	@Autowired
	private AdminUserActionLogJob adminUserActionLogJob;

	@Autowired
	private AdminUserLogJob adminUserLogJob;

	@Autowired
	private UserRechargeService uRechargeService;

	@Autowired
	private MailJob mailJob;

	@Autowired
	private PaymentChannelService paymentChannelService;

	@Autowired
	private AdminUserCriticalLogJob adminUserCriticalLogJob;

	@RequestMapping(value = WUC.LOTTERY_USER_RECHARGE_LIST, method = { RequestMethod.POST })
	@ResponseBody
	public void LOTTERY_USER_RECHARGE_LIST(HttpSession session, HttpServletRequest request, HttpServletResponse response) {
		String actionKey = WUC.LOTTERY_USER_RECHARGE_LIST;
		long t1 = System.currentTimeMillis();
		WebJSONObject json = new WebJSONObject(super.getAdminDataFactory());
		AdminUser uEntity = super.getCurrUser(session, request, response);
		if (uEntity != null) {
			if (super.hasAccess(uEntity, actionKey)) {
				String billno = request.getParameter("billno");
				String username = request.getParameter("username");
				String minTime = request.getParameter("minTime");
				if (StringUtil.isNotNull(minTime)) {
					minTime += " 00:00:00";
				}
				String maxTime = request.getParameter("maxTime");
				if (StringUtil.isNotNull(maxTime)) {
					maxTime += " 00:00:00";
				}
				String minPayTime = request.getParameter("minPayTime");
				if (StringUtil.isNotNull(minPayTime)) {
					minPayTime += " 00:00:00";
				}
				String maxPayTime = request.getParameter("maxPayTime");
				if (StringUtil.isNotNull(maxPayTime)) {
					maxPayTime += " 00:00:00";
				}
				Double minMoney = HttpUtil.getDoubleParameter(request, "minMoney");
				Double maxMoney = HttpUtil.getDoubleParameter(request, "maxMoney");
				Integer status = HttpUtil.getIntParameter(request, "status");
				Integer channelId = HttpUtil.getIntParameter(request, "channelId");
				int start = HttpUtil.getIntParameter(request, "start");
				int limit = HttpUtil.getIntParameter(request, "limit");
				Integer type = HttpUtil.getIntParameter(request, "type");
				PageList pList = uRechargeService.search(type,billno, username, minTime, maxTime, minPayTime, maxPayTime, minMoney, maxMoney, status, channelId, start, limit);
				if(pList != null) {
					double totalRecharge = uRechargeService.getTotalRecharge(type,billno, username, minTime, maxTime, minPayTime, maxPayTime, minMoney, maxMoney, status, channelId);
					json.accumulate("totalRecharge", totalRecharge);
					json.accumulate("totalCount", pList.getCount());
					json.accumulate("data", pList.getList());
				} else {
					json.accumulate("totalRecharge", 0);
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
	/**
	 * 历史充值记录
	 * @param session
	 * @param request
	 * @param response
	 */
	@RequestMapping(value = WUC.HISTORY_LOTTERY_USER_RECHARGE_LIST, method = { RequestMethod.POST })
	@ResponseBody
	public void HISTORY_LOTTERY_USER_RECHARGE_LIST(HttpSession session, HttpServletRequest request, HttpServletResponse response) {
		String actionKey = WUC.HISTORY_LOTTERY_USER_RECHARGE_LIST;
		long t1 = System.currentTimeMillis();
		WebJSONObject json = new WebJSONObject(super.getAdminDataFactory());
		AdminUser uEntity = super.getCurrUser(session, request, response);
		if (uEntity != null) {
			if (super.hasAccess(uEntity, actionKey)) {
				String billno = request.getParameter("billno");
				String username = request.getParameter("username");
				String minTime = request.getParameter("minTime");
				if (StringUtil.isNotNull(minTime)) {
					minTime += " 00:00:00";
				}
				String maxTime = request.getParameter("maxTime");
				if (StringUtil.isNotNull(maxTime)) {
					maxTime += " 00:00:00";
				}
				String minPayTime = request.getParameter("minPayTime");
				if (StringUtil.isNotNull(minPayTime)) {
					minPayTime += " 00:00:00";
				}
				String maxPayTime = request.getParameter("maxPayTime");
				if (StringUtil.isNotNull(maxPayTime)) {
					maxPayTime += " 00:00:00";
				}
				Double minMoney = HttpUtil.getDoubleParameter(request, "minMoney");
				Double maxMoney = HttpUtil.getDoubleParameter(request, "maxMoney");
				Integer status = HttpUtil.getIntParameter(request, "status");
				Integer channelId = HttpUtil.getIntParameter(request, "channelId");
				int start = HttpUtil.getIntParameter(request, "start");
				int limit = HttpUtil.getIntParameter(request, "limit");
				PageList pList = uRechargeService.searchHistory(billno, username, minTime, maxTime, minPayTime, maxPayTime, minMoney, maxMoney, status, channelId, start, limit);
				if(pList != null) {
					double totalRecharge = uRechargeService.getHistoryTotalRecharge(billno, username, minTime, maxTime, minPayTime, maxPayTime, minMoney, maxMoney, status, channelId);
					json.accumulate("totalRecharge", totalRecharge);
					json.accumulate("totalCount", pList.getCount());
					json.accumulate("data", pList.getList());
				} else {
					json.accumulate("totalRecharge", 0);
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

	@RequestMapping(value = WUC.LOTTERY_USER_RECHARGE_ADD, method = { RequestMethod.POST })
	@ResponseBody
	public void LOTTERY_USER_RECHARGE_ADD(HttpSession session, HttpServletRequest request, HttpServletResponse response) {
		String actionKey = WUC.LOTTERY_USER_RECHARGE_ADD;
		long t1 = System.currentTimeMillis();
		WebJSONObject json = new WebJSONObject(super.getAdminDataFactory());
		AdminUser uEntity = super.getCurrUser(session, request, response);
		if (uEntity != null) {
			if (super.hasAccess(uEntity, actionKey)) {
				String username = request.getParameter("username");
				int type = HttpUtil.getIntParameter(request, "type");
				int account = HttpUtil.getIntParameter(request, "account");
				double amount = HttpUtil.getDoubleParameter(request, "amount");
				String withdrawPwd = request.getParameter("withdrawPwd");
				int limit = HttpUtil.getIntParameter(request, "limit");
				String remarks = request.getParameter("remarks");

				if (remarks == null || StringUtils.isEmpty(remarks.trim())) {
					json.set(2, "2-30");
					HttpUtil.write(response, json.toString(), HttpUtil.json);
					return;
				}

				String token = getDisposableToken(session, request);

				if(PasswordUtil.validatePassword(uEntity.getWithdrawPwd(), token, withdrawPwd)) {
					if (!PasswordUtil.isSimplePassword(uEntity.getWithdrawPwd())) {
						if (isUnlockedWithdrawPwd(session)) {
							boolean result = uRechargeService.addSystemRecharge(username, type, account, amount, limit, remarks);
							if(result) {
								adminUserLogJob.logRecharge(uEntity, request, username, type, account, amount, limit, remarks);
								mailJob.sendSystemRecharge(username, uEntity.getUsername(), type, account, amount, remarks);
								json.set(0, "0-5");
							} else {
								json.set(1, "1-5");
							}
						}
						else {
							json.set(2, "2-43");
						}
					}
					else {
						json.set(2, "2-41");
					}
				} else {
					json.set(2, "2-12");
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
	@RequestMapping(value = WUC.LOTTERY_USER_RECHARGE_GET, method = { RequestMethod.POST })
	@ResponseBody
	public void LOTTERY_USER_RECHARGE_GET(HttpSession session, HttpServletRequest request, HttpServletResponse response) {
		String actionKey = WUC.LOTTERY_USER_RECHARGE_GET;
		long t1 = System.currentTimeMillis();
		WebJSONObject json = new WebJSONObject(super.getAdminDataFactory());
		AdminUser uEntity = super.getCurrUser(session, request, response);
		if (uEntity != null) {
			if (super.hasAccess(uEntity, actionKey)) {
				int id = HttpUtil.getIntParameter(request, "id");
				UserRechargeVO result = uRechargeService.getById(id);
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

	@RequestMapping(value = WUC.HISTORY_LOTTERY_USER_RECHARGE_GET, method = { RequestMethod.POST })
	@ResponseBody
	public void HISTORY_LOTTERY_USER_RECHARGE_GET(HttpSession session, HttpServletRequest request, HttpServletResponse response) {
		String actionKey = WUC.HISTORY_LOTTERY_USER_RECHARGE_GET;
		long t1 = System.currentTimeMillis();
		WebJSONObject json = new WebJSONObject(super.getAdminDataFactory());
		AdminUser uEntity = super.getCurrUser(session, request, response);
		if (uEntity != null) {
			if (super.hasAccess(uEntity, actionKey)) {
				int id = HttpUtil.getIntParameter(request, "id");
				HistoryUserRechargeVO result = uRechargeService.getHistoryById(id);
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

	@RequestMapping(value = WUC.LOTTERY_USER_RECHARGE_PATCH, method = { RequestMethod.POST })
	@ResponseBody
	public void LOTTERY_USER_RECHARGE_PATCH(HttpSession session, HttpServletRequest request, HttpServletResponse response) {
		String actionKey = WUC.LOTTERY_USER_RECHARGE_PATCH;
		long t1 = System.currentTimeMillis();
		WebJSONObject json = new WebJSONObject(super.getAdminDataFactory());
		AdminUser uEntity = super.getCurrUser(session, request, response);
		if (uEntity != null) {
			if (super.hasAccess(uEntity, actionKey)) {
				String billno = request.getParameter("billno");
				String payBillno = request.getParameter("paybillno");
				String remarks = request.getParameter("remarks");
				String withdrawPwd = request.getParameter("withdrawPwd");
				String token = getDisposableToken(session, request);
				if(PasswordUtil.validatePassword(uEntity.getWithdrawPwd(), token, withdrawPwd)) {
					if (!PasswordUtil.isSimplePassword(uEntity.getWithdrawPwd())) {
						if (isUnlockedWithdrawPwd(session)) {
							boolean result = uRechargeService.patchOrder(billno, payBillno, remarks);
							if(result) {
								adminUserLogJob.logPatchRecharge(uEntity, request, billno, payBillno, remarks);
								json.set(0, "0-3");
							} else {
								json.set(1, "1-3");
							}
						}
						else {
							json.set(2, "2-43");
						}
					}
					else {
						json.set(2, "2-41");
					}
				} else {
					json.set(2, "2-12");
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

	@RequestMapping(value = WUC.LOTTERY_USER_RECHARGE_CANCEL, method = { RequestMethod.POST })
	@ResponseBody
	public void LOTTERY_USER_RECHARGE_CANCEL(HttpSession session, HttpServletRequest request, HttpServletResponse response) {
		String actionKey = WUC.LOTTERY_USER_RECHARGE_CANCEL;
		long t1 = System.currentTimeMillis();
		WebJSONObject json = new WebJSONObject(super.getAdminDataFactory());
		AdminUser uEntity = super.getCurrUser(session, request, response);
		if (uEntity != null) {
			if (super.hasAccess(uEntity, actionKey)) {
				String billno = request.getParameter("billno");
				boolean result = uRechargeService.cancelOrder(billno);
				if(result) {
					adminUserLogJob.logCancelRecharge(uEntity, request, billno);
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
}