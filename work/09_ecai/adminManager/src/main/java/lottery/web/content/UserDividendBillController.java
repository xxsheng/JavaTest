package lottery.web.content;

import admin.domains.content.entity.AdminUser;
import admin.domains.jobs.AdminUserActionLogJob;
import admin.domains.jobs.AdminUserLogJob;
import admin.web.WUC;
import admin.web.WebJSONObject;
import admin.web.helper.AbstractActionController;
import javautils.encrypt.PasswordUtil;
import javautils.http.HttpUtil;
import javautils.jdbc.PageList;
import lottery.domains.content.biz.UserDividendBillService;
import lottery.domains.content.biz.UserService;
import lottery.domains.content.dao.UserDao;
import lottery.domains.content.entity.User;
import lottery.domains.content.entity.UserDividendBill;
import lottery.domains.content.global.Global;
import lottery.domains.content.vo.user.UserDividendBillVO;
import lottery.domains.content.vo.user.UserVO;
import lottery.domains.pool.LotteryDataFactory;
import lottery.web.content.utils.UserCodePointUtil;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.List;

@Controller
public class UserDividendBillController extends AbstractActionController {

	@Autowired
	private UserService userService;
	
	@Autowired
	private UserDividendBillService uDividendBillService;

	@Autowired
	private AdminUserActionLogJob adminUserActionLogJob;

	@Autowired
	private AdminUserLogJob adminUserLogJob;

	@Autowired
	private UserCodePointUtil uCodePointUtil;

	@Autowired
	private LotteryDataFactory dataFactory;

	@RequestMapping(value = WUC.LOTTERY_USER_DIVIDEND_BILL_LIST, method = { RequestMethod.POST })
	@ResponseBody
	public void LOTTERY_USER_DIVIDEND_BILL_LIST(HttpSession session, HttpServletRequest request,
												HttpServletResponse response) {
		String actionKey = WUC.LOTTERY_USER_DIVIDEND_BILL_LIST;
		WebJSONObject json = new WebJSONObject(super.getAdminDataFactory());
		AdminUser uEntity = super.getCurrUser(session, request, response);
		if (uEntity != null) {
			if (super.hasAccess(uEntity, actionKey)) {
				String username = request.getParameter("username");
				String sTime = request.getParameter("sTime");
				String eTime = request.getParameter("eTime");
				Double minUserAmount = HttpUtil.getDoubleParameter(request, "minUserAmount");
				Double maxUserAmount = HttpUtil.getDoubleParameter(request, "maxUserAmount");
				int start = HttpUtil.getIntParameter(request, "start");
				int limit = HttpUtil.getIntParameter(request, "limit");
				Integer status = HttpUtil.getIntParameter(request, "status");
				Integer issueType = HttpUtil.getIntParameter(request, "issueType");
				List<Integer> userIds = new ArrayList<>();
				boolean legalUser = true; // 输入的用户名是否存在
				if (StringUtils.isNotEmpty(username)) {
					User user = userService.getByUsername(username);
					if (user == null) {
						legalUser = false;
					}
					else {
						userIds.add(user.getId());
						List<User> userDirectLowers = userService.getUserDirectLower(user.getId());
						for (User userDirectLower : userDirectLowers) {
							userIds.add(userDirectLower.getId());
						}
					}
				}

				double platformTotalLoss = 0;
				double platformTotalUserAmount = 0;
				double upperTotalUserAmount = 0;
				if (!legalUser) {
					json.accumulate("totalCount", 0);
					json.accumulate("data", "[]");
				}
				else {
					PageList pList = uDividendBillService.search(userIds, sTime, eTime, minUserAmount, maxUserAmount, status, issueType, start, limit);
					double[] userAmounts = uDividendBillService.sumUserAmount(userIds, sTime, eTime, minUserAmount, maxUserAmount);
					platformTotalLoss = userAmounts[0];
					platformTotalUserAmount = userAmounts[1];
					upperTotalUserAmount = userAmounts[2];
					if(pList != null) {
						json.accumulate("totalCount", pList.getCount());
						json.accumulate("data", pList.getList());
					} else {
						json.accumulate("totalCount", 0);
						json.accumulate("data", "[]");
					}
				}
				json.accumulate("platformTotalLoss", platformTotalLoss);
				json.accumulate("platformTotalUserAmount", platformTotalUserAmount);
				json.accumulate("upperTotalUserAmount", upperTotalUserAmount);
				json.set(0, "0-3");
			}
			else {
				json.set(2, "2-4");
			}
		} else {
			json.set(2, "2-6");
		}
		HttpUtil.write(response, json.toString(), HttpUtil.json);
	}

	@RequestMapping(value = WUC.LOTTERY_USER_DIVIDEND_BILL_PLATFORN_LOSS_LIST, method = { RequestMethod.POST })
	@ResponseBody
	public void LOTTERY_USER_DIVIDEND_BILL_PLATFORN_LOSS_LIST(HttpSession session, HttpServletRequest request,
															  HttpServletResponse response) {
		String actionKey = WUC.LOTTERY_USER_DIVIDEND_BILL_PLATFORN_LOSS_LIST;
		WebJSONObject json = new WebJSONObject(super.getAdminDataFactory());
		AdminUser uEntity = super.getCurrUser(session, request, response);
		if (uEntity != null) {
			if (super.hasAccess(uEntity, actionKey)) {
				String username = request.getParameter("username");
				String sTime = request.getParameter("sTime");
				String eTime = request.getParameter("eTime");
				Double minUserAmount = HttpUtil.getDoubleParameter(request, "minUserAmount");
				Double maxUserAmount = HttpUtil.getDoubleParameter(request, "maxUserAmount");
				int start = HttpUtil.getIntParameter(request, "start");
				int limit = HttpUtil.getIntParameter(request, "limit");
				List<Integer> userIds = new ArrayList<>();
				boolean legalUser = true; // 输入的用户名是否存在
				if (StringUtils.isNotEmpty(username)) {
					User user = userService.getByUsername(username);
					if (user == null) {
						legalUser = false;
					}
					else {
						userIds.add(user.getId());
						List<User> userDirectLowers = userService.getUserDirectLower(user.getId());
						for (User userDirectLower : userDirectLowers) {
							userIds.add(userDirectLower.getId());
						}
					}
				}

				if (!legalUser) {
					json.accumulate("totalCount", 0);
					json.accumulate("data", "[]");
				}
				else {
					PageList pList = uDividendBillService.searchPlatformLoss(userIds, sTime, eTime, minUserAmount, maxUserAmount, start, limit);
					if(pList != null) {
						json.accumulate("totalCount", pList.getCount());
						json.accumulate("data", pList.getList());
					} else {
						json.accumulate("totalCount", 0);
						json.accumulate("data", "[]");
					}
				}
				json.set(0, "0-3");
			}
			else {
				json.set(2, "2-4");
			}
		} else {
			json.set(2, "2-6");
		}
		HttpUtil.write(response, json.toString(), HttpUtil.json);
	}

	@RequestMapping(value = WUC.LOTTERY_USER_DIVIDEND_BILL_GET, method = { RequestMethod.POST })
	@ResponseBody
	public void LOTTERY_USER_DIVIDEND_BILL_GET(HttpSession session, HttpServletRequest request,
											   HttpServletResponse response) {
		String actionKey = WUC.LOTTERY_USER_DIVIDEND_BILL_GET;
		WebJSONObject json = new WebJSONObject(super.getAdminDataFactory());
		AdminUser uEntity = super.getCurrUser(session, request, response);
		if (uEntity != null) {
			if (super.hasAccess(uEntity, actionKey)) {
				int id = HttpUtil.getIntParameter(request, "id");

				UserDividendBill userDividendBill = uDividendBillService.getById(id);
				if (userDividendBill == null) {
					json.set(2, "2-3001");
				}
				else {
					json.accumulate("data", new UserDividendBillVO(userDividendBill, dataFactory));
					json.set(0, "0-3");
				}
			}
			else {
				json.set(2, "2-4");
			}
		} else {
			json.set(2, "2-6");
		}
		HttpUtil.write(response, json.toString(), HttpUtil.json);
	}

	@RequestMapping(value = WUC.LOTTERY_USER_DIVIDEND_BILL_AGREE, method = { RequestMethod.POST })
	@ResponseBody
	public void LOTTERY_USER_DIVIDEND_BILL_AGREE(HttpSession session, HttpServletRequest request,
												 HttpServletResponse response) {
		String actionKey = WUC.LOTTERY_USER_DIVIDEND_BILL_AGREE;
		long t1 = System.currentTimeMillis();
		WebJSONObject json = new WebJSONObject(super.getAdminDataFactory());
		AdminUser uEntity = super.getCurrUser(session, request, response);
		if (uEntity != null) {
			if (super.hasAccess(uEntity, actionKey)) {
				int id = HttpUtil.getIntParameter(request, "id");
				String withdrawPwd = request.getParameter("withdrawPwd");
				String remarks = request.getParameter("remarks");
				String token = getDisposableToken(session, request);

				if(PasswordUtil.validatePassword(uEntity.getWithdrawPwd(), token, withdrawPwd)) {
					if (!PasswordUtil.isSimplePassword(uEntity.getWithdrawPwd())) {
						if (isUnlockedWithdrawPwd(session)) {
							UserDividendBill userDividendBill = uDividendBillService.getById(id);
							if (userDividendBill == null || userDividendBill.getStatus() != Global.DIVIDEND_BILL_UNAPPROVE) {
								json.set(2, "2-3001");
							}
							else {

								boolean result = uDividendBillService.agree(json, id, remarks);
								if(result) {
									UserVO user = dataFactory.getUser(userDividendBill.getUserId());
									adminUserLogJob.logAgreeDividend(uEntity, request, user == null ? "" : user.getUsername(), userDividendBill, remarks);
									json.set(0, "0-5");
								}
							}
						}
						else {
							json.set(2, "2-43");
						}
					}
					else {
						json.set(2, "2-41");
					}
				}
				else {
					json.set(2, "2-12");
				}
			}
			else {
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

	@RequestMapping(value = WUC.LOTTERY_USER_DIVIDEND_BILL_DENY, method = { RequestMethod.POST })
	@ResponseBody
	public void LOTTERY_USER_DIVIDEND_BILL_DENY(HttpSession session, HttpServletRequest request,
												HttpServletResponse response) {
		String actionKey = WUC.LOTTERY_USER_DIVIDEND_BILL_DENY;
		long t1 = System.currentTimeMillis();
		WebJSONObject json = new WebJSONObject(super.getAdminDataFactory());
		AdminUser uEntity = super.getCurrUser(session, request, response);
		if (uEntity != null) {
			if (super.hasAccess(uEntity, actionKey)) {
				int id = HttpUtil.getIntParameter(request, "id");
				String withdrawPwd = request.getParameter("withdrawPwd");
				String remarks = request.getParameter("remarks");
				String token = getDisposableToken(session, request);

				if(PasswordUtil.validatePassword(uEntity.getWithdrawPwd(), token, withdrawPwd)) {
					if (!PasswordUtil.isSimplePassword(uEntity.getWithdrawPwd())) {
						if (isUnlockedWithdrawPwd(session)) {
							UserDividendBill userDividendBill = uDividendBillService.getById(id);
							if (userDividendBill == null || userDividendBill.getStatus() != Global.DIVIDEND_BILL_UNAPPROVE) {
								json.set(2, "2-3001");
							}
							else {

								boolean result = uDividendBillService.deny(json, id, remarks);
								if(result) {
									UserVO user = dataFactory.getUser(userDividendBill.getUserId());
									adminUserLogJob.logDenyDividend(uEntity, request, user == null ? "" : user.getUsername(), userDividendBill, remarks);
									json.set(0, "0-5");
								}
							}
						}
						else {
							json.set(2, "2-43");
						}
					}
					else {
						json.set(2, "2-41");
					}
				}
				else {
					json.set(2, "2-12");
				}
			}
			else {
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

	@RequestMapping(value = WUC.LOTTERY_USER_DIVIDEND_BILL_DEL, method = { RequestMethod.POST })
	@ResponseBody
	public void LOTTERY_USER_DIVIDEND_BILL_DEL(HttpSession session, HttpServletRequest request,
											   HttpServletResponse response) {
		String actionKey = WUC.LOTTERY_USER_DIVIDEND_BILL_DEL;
		long t1 = System.currentTimeMillis();
		WebJSONObject json = new WebJSONObject(super.getAdminDataFactory());
		AdminUser uEntity = super.getCurrUser(session, request, response);
		if (uEntity != null) {
			if (super.hasAccess(uEntity, actionKey)) {
				int id = HttpUtil.getIntParameter(request, "id");
				String withdrawPwd = request.getParameter("withdrawPwd");
				String remarks = request.getParameter("remarks");
				String token = getDisposableToken(session, request);

				if(PasswordUtil.validatePassword(uEntity.getWithdrawPwd(), token, withdrawPwd)) {
					if (!PasswordUtil.isSimplePassword(uEntity.getWithdrawPwd())) {
						if (isUnlockedWithdrawPwd(session)) {
							UserDividendBill userDividendBill = uDividendBillService.getById(id);
							if (userDividendBill == null) {
								json.set(2, "2-3001");
							}
							else {
								boolean result = uDividendBillService.del(json, id);
								if(result) {
									UserVO user = dataFactory.getUser(userDividendBill.getUserId());
									adminUserLogJob.logDelDividend(uEntity, request, user == null ? "" : user.getUsername(), userDividendBill, remarks);
									json.set(0, "0-5");
								}
							}
						}
						else {
							json.set(2, "2-43");
						}
					}
					else {
						json.set(2, "2-41");
					}
				}
				else {
					json.set(2, "2-12");
				}
			}
			else {
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

	@RequestMapping(value = WUC.LOTTERY_USER_DIVIDEND_BILL_RESET, method = { RequestMethod.POST })
	@ResponseBody
	public void LOTTERY_USER_DIVIDEND_BILL_RESET(HttpSession session, HttpServletRequest request,
												 HttpServletResponse response) {
		String actionKey = WUC.LOTTERY_USER_DIVIDEND_BILL_RESET;
		long t1 = System.currentTimeMillis();
		WebJSONObject json = new WebJSONObject(super.getAdminDataFactory());
		AdminUser uEntity = super.getCurrUser(session, request, response);
		if (uEntity != null) {
			if (super.hasAccess(uEntity, actionKey)) {
				int id = HttpUtil.getIntParameter(request, "id");
				String withdrawPwd = request.getParameter("withdrawPwd");
				String remarks = request.getParameter("remarks");
				String token = getDisposableToken(session, request);

				if(PasswordUtil.validatePassword(uEntity.getWithdrawPwd(), token, withdrawPwd)) {
					if (!PasswordUtil.isSimplePassword(uEntity.getWithdrawPwd())) {
						if (isUnlockedWithdrawPwd(session)) {
							UserDividendBill userDividendBill = uDividendBillService.getById(id);
							if (userDividendBill == null) {
								json.set(2, "2-3001");
							}
							else {
								boolean result = uDividendBillService.reset(json, id, remarks);
								if(result) {
									UserVO user = dataFactory.getUser(userDividendBill.getUserId());
									adminUserLogJob.logResetDividend(uEntity, request, user == null ? "" : user.getUsername(), userDividendBill, remarks);
									json.set(0, "0-5");
								} else {
									if (json.getError() == null) {
										json.set(1, "1-5");
									}
								}
							}
						}
						else {
							json.set(2, "2-43");
						}
					}
					else {
						json.set(2, "2-41");
					}
				}
				else {
					json.set(2, "2-12");
				}
			}
			else {
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