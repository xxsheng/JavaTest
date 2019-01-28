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
import lottery.domains.content.biz.UserGameDividendBillService;
import lottery.domains.content.dao.UserDao;
import lottery.domains.content.entity.User;
import lottery.domains.content.entity.UserGameDividendBill;
import lottery.domains.content.global.Global;
import lottery.domains.content.vo.user.UserGameDividendBillVO;
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
public class UserGameDividendBillController extends AbstractActionController {

	@Autowired
	private UserDao userDao;

	@Autowired
	private UserGameDividendBillService uGameDividendBillService;

	@Autowired
	private AdminUserActionLogJob adminUserActionLogJob;

	@Autowired
	private AdminUserLogJob adminUserLogJob;

	@Autowired
	private UserCodePointUtil uCodePointUtil;

	@Autowired
	private LotteryDataFactory dataFactory;

	@RequestMapping(value = WUC.USER_GAME_DIVIDEND_BILL_LIST, method = { RequestMethod.POST })
	@ResponseBody
	public void USER_GAME_DIVIDEND_BILL_LIST(HttpSession session, HttpServletRequest request,
											 HttpServletResponse response) {
		String actionKey = WUC.USER_GAME_DIVIDEND_BILL_LIST;
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
				List<Integer> userIds = new ArrayList<>();
				boolean legalUser = true; // 输入的用户名是否存在
				if (StringUtils.isNotEmpty(username)) {
					User user = userDao.getByUsername(username);
					if (user == null) {
						legalUser = false;
					}
					else {
						userIds.add(user.getId());
						List<User> userDirectLowers = userDao.getUserDirectLower(user.getId());
						for (User userDirectLower : userDirectLowers) {
							userIds.add(userDirectLower.getId());
						}
					}
				}

				double totalUserAmount = 0;
				if (!legalUser) {
					json.accumulate("totalCount", 0);
					json.accumulate("data", "[]");
				}
				else {
					PageList pList = uGameDividendBillService.search(userIds, sTime, eTime, minUserAmount, maxUserAmount, status, start, limit);
					if(pList != null) {
						totalUserAmount = uGameDividendBillService.sumUserAmount(userIds, sTime, eTime, minUserAmount, maxUserAmount, status);

						json.accumulate("totalCount", pList.getCount());
						json.accumulate("data", pList.getList());
					} else {
						json.accumulate("totalCount", 0);
						json.accumulate("data", "[]");
					}
				}

				json.accumulate("totalUserAmount", totalUserAmount);
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

	@RequestMapping(value = WUC.USER_GAME_DIVIDEND_BILL_GET, method = { RequestMethod.POST })
	@ResponseBody
	public void USER_GAME_DIVIDEND_BILL_GET(HttpSession session, HttpServletRequest request,
											HttpServletResponse response) {
		String actionKey = WUC.USER_GAME_DIVIDEND_BILL_GET;
		WebJSONObject json = new WebJSONObject(super.getAdminDataFactory());
		AdminUser uEntity = super.getCurrUser(session, request, response);
		if (uEntity != null) {
			if (super.hasAccess(uEntity, actionKey)) {
				int id = HttpUtil.getIntParameter(request, "id");

				UserGameDividendBill userDividendBill = uGameDividendBillService.getById(id);
				if (userDividendBill == null) {
					json.set(2, "2-3001");
				}
				else {
					json.accumulate("data", new UserGameDividendBillVO(userDividendBill, dataFactory));
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

	@RequestMapping(value = WUC.USER_GAME_DIVIDEND_BILL_AGREE, method = { RequestMethod.POST })
	@ResponseBody
	public void USER_GAME_DIVIDEND_BILL_AGREE(HttpSession session, HttpServletRequest request,
											  HttpServletResponse response) {
		String actionKey = WUC.USER_GAME_DIVIDEND_BILL_AGREE;
		long t1 = System.currentTimeMillis();
		WebJSONObject json = new WebJSONObject(super.getAdminDataFactory());
		AdminUser uEntity = super.getCurrUser(session, request, response);
		if (uEntity != null) {
			if (super.hasAccess(uEntity, actionKey)) {
				int id = HttpUtil.getIntParameter(request, "id");
				double userAmount = HttpUtil.getDoubleParameter(request, "userAmount");
				String withdrawPwd = request.getParameter("withdrawPwd");
				String remarks = request.getParameter("remarks");
				String token = getDisposableToken(session, request);

				if(PasswordUtil.validatePassword(uEntity.getWithdrawPwd(), token, withdrawPwd)) {
					if (!PasswordUtil.isSimplePassword(uEntity.getWithdrawPwd())) {
						if (isUnlockedWithdrawPwd(session)) {
							UserGameDividendBill userDividendBill = uGameDividendBillService.getById(id);
							if (userDividendBill == null || userDividendBill.getStatus() != Global.GAME_DIVIDEND_BILL_UNAPPROVE) {
								json.set(2, "2-3001");
							}
							else {

								boolean result = uGameDividendBillService.agree(id, userAmount, remarks);
								if(result) {
									UserVO user = dataFactory.getUser(userDividendBill.getUserId());
									adminUserLogJob.logAgreeGameDividend(uEntity, request, user == null ? "" : user.getUsername(), userDividendBill, userAmount, remarks);
									json.set(0, "0-5");
								} else {
									json.set(1, "1-5");
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

	@RequestMapping(value = WUC.USER_GAME_DIVIDEND_BILL_DENY, method = { RequestMethod.POST })
	@ResponseBody
	public void USER_GAME_DIVIDEND_BILL_DENY(HttpSession session, HttpServletRequest request,
											 HttpServletResponse response) {
		String actionKey = WUC.USER_GAME_DIVIDEND_BILL_DENY;
		long t1 = System.currentTimeMillis();
		WebJSONObject json = new WebJSONObject(super.getAdminDataFactory());
		AdminUser uEntity = super.getCurrUser(session, request, response);
		if (uEntity != null) {
			if (super.hasAccess(uEntity, actionKey)) {
				int id = HttpUtil.getIntParameter(request, "id");
				double userAmount = HttpUtil.getDoubleParameter(request, "userAmount");
				String withdrawPwd = request.getParameter("withdrawPwd");
				String remarks = request.getParameter("remarks");
				String token = getDisposableToken(session, request);

				if(PasswordUtil.validatePassword(uEntity.getWithdrawPwd(), token, withdrawPwd)) {
					if (!PasswordUtil.isSimplePassword(uEntity.getWithdrawPwd())) {
						if (isUnlockedWithdrawPwd(session)) {
							UserGameDividendBill userDividendBill = uGameDividendBillService.getById(id);
							if (userDividendBill == null || userDividendBill.getStatus() != Global.GAME_DIVIDEND_BILL_UNAPPROVE) {
								json.set(2, "2-3001");
							}
							else {

								boolean result = uGameDividendBillService.deny(id, userAmount, remarks);
								if(result) {
									UserVO user = dataFactory.getUser(userDividendBill.getUserId());
									adminUserLogJob.logDenyGameDividend(uEntity, request, user == null ? "" : user.getUsername(), userDividendBill, userAmount, remarks);
									json.set(0, "0-5");
								} else {
									json.set(1, "1-5");
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

	@RequestMapping(value = WUC.USER_GAME_DIVIDEND_BILL_DEL, method = { RequestMethod.POST })
	@ResponseBody
	public void USER_GAME_DIVIDEND_BILL_DEL(HttpSession session, HttpServletRequest request,
											HttpServletResponse response) {
		String actionKey = WUC.USER_GAME_DIVIDEND_BILL_DEL;
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
							UserGameDividendBill userDividendBill = uGameDividendBillService.getById(id);
							if (userDividendBill == null) {
								json.set(2, "2-3001");
							}
							else {
								boolean result = uGameDividendBillService.del(id);
								if(result) {
									UserVO user = dataFactory.getUser(userDividendBill.getUserId());
									adminUserLogJob.logDelGameDividend(uEntity, request, user == null ? "" : user.getUsername(), userDividendBill, remarks);
									json.set(0, "0-5");
								} else {
									json.set(1, "1-5");
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