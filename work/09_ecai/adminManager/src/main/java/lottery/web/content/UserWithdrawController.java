package lottery.web.content;

import admin.domains.content.entity.AdminUser;
import admin.domains.jobs.AdminUserActionLogJob;
import admin.domains.jobs.AdminUserLogJob;
import admin.web.WUC;
import admin.web.WebJSONObject;
import admin.web.helper.AbstractActionController;
import javautils.http.HttpUtil;
import javautils.jdbc.PageList;
import lottery.domains.content.biz.*;
import lottery.domains.content.entity.PaymentChannel;
import lottery.domains.content.vo.bill.UserBillVO;
import lottery.domains.content.vo.user.*;
import lottery.domains.pool.LotteryDataFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

@Controller
public class UserWithdrawController extends AbstractActionController {
	private static ConcurrentHashMap<Integer, Boolean> API_PAY_ORDER_CACHE = new ConcurrentHashMap<>();

	@Autowired
	private AdminUserActionLogJob adminUserActionLogJob;
	
	@Autowired
	private AdminUserLogJob adminUserLogJob;
	
	@Autowired
	private UserService uService;
	
	@Autowired
	private UserBetsService uBetsService;
	
	@Autowired
	private UserBillService uBillService;
	
	@Autowired
	private UserCardService uCardService;
	
	@Autowired
	private UserRechargeService uRechargeService;
	
	@Autowired
	private UserWithdrawService uWithdrawService;
	
	@Autowired
	private UserWithdrawLimitService userWithdrawLimitService;

	@Autowired
	private LotteryDataFactory dataFactory;
	
	@RequestMapping(value = WUC.LOTTERY_USER_WITHDRAW_LIST, method = { RequestMethod.POST })
	@ResponseBody
	public void LOTTERY_USER_WITHDRAW_LIST(HttpSession session, HttpServletRequest request, HttpServletResponse response) {
		String actionKey = WUC.LOTTERY_USER_WITHDRAW_LIST;
		long t1 = System.currentTimeMillis();
		WebJSONObject json = new WebJSONObject(super.getAdminDataFactory());
		AdminUser uEntity = super.getCurrUser(session, request, response);
		if (uEntity != null) {
			if (super.hasAccess(uEntity, actionKey)) {
				String billno = request.getParameter("billno");
				String username = request.getParameter("username");
				String minTime = request.getParameter("minTime");
				String maxTime = request.getParameter("maxTime");
				String minOperatorTime = request.getParameter("minOperatorTime");
				String maxOperatorTime = request.getParameter("maxOperatorTime");
				Double minMoney = HttpUtil.getDoubleParameter(request, "minMoney");
				Double maxMoney = HttpUtil.getDoubleParameter(request, "maxMoney");
				String keyword = request.getParameter("keyword");
				Integer status = HttpUtil.getIntParameter(request, "status");
				Integer type = HttpUtil.getIntParameter(request, "type");
				Integer checkStatus = HttpUtil.getIntParameter(request, "checkStatus");
				Integer remitStatus = HttpUtil.getIntParameter(request, "remitStatus");
				Integer paymentChannelId = HttpUtil.getIntParameter(request, "paymentChannelId");
				int start = HttpUtil.getIntParameter(request, "start");
				int limit = HttpUtil.getIntParameter(request, "limit");
				PageList pList = uWithdrawService.search(type,billno, username, minTime, maxTime, minOperatorTime, maxOperatorTime, minMoney, maxMoney, keyword, status, checkStatus, remitStatus, paymentChannelId, start, limit);
				if(pList != null) {
					double[] totalWithdraw = uWithdrawService.getTotalWithdraw(billno, username, minTime, maxTime, minOperatorTime, maxOperatorTime, minMoney, maxMoney, keyword, status, checkStatus, remitStatus, paymentChannelId);
					json.accumulate("totalRecMoney", totalWithdraw[0]);
					json.accumulate("totalFeeMoney", totalWithdraw[1]);
					json.accumulate("totalCount", pList.getCount());
					json.accumulate("data", pList.getList());
				} else {
					json.accumulate("totalRecMoney", 0);
					json.accumulate("totalFeeMoney", 0);
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
	
	//历史提现列表
	@RequestMapping(value = WUC.HISTORY_LOTTERY_USER_WITHDRAW_LIST, method = { RequestMethod.POST })
	@ResponseBody
	public void HISTORY_LOTTERY_USER_WITHDRAW_LIST(HttpSession session, HttpServletRequest request, HttpServletResponse response) {
		String actionKey = WUC.HISTORY_LOTTERY_USER_WITHDRAW_LIST;
		long t1 = System.currentTimeMillis();
		WebJSONObject json = new WebJSONObject(super.getAdminDataFactory());
		AdminUser uEntity = super.getCurrUser(session, request, response);
		if (uEntity != null) {
			if (super.hasAccess(uEntity, actionKey)) {
				String billno = request.getParameter("billno");
				String username = request.getParameter("username");
				String minTime = request.getParameter("minTime");
				String maxTime = request.getParameter("maxTime");
				String minOperatorTime = request.getParameter("minOperatorTime");
				String maxOperatorTime = request.getParameter("maxOperatorTime");
				Double minMoney = HttpUtil.getDoubleParameter(request, "minMoney");
				Double maxMoney = HttpUtil.getDoubleParameter(request, "maxMoney");
				String keyword = request.getParameter("keyword");
				Integer status = HttpUtil.getIntParameter(request, "status");
				Integer checkStatus = HttpUtil.getIntParameter(request, "checkStatus");
				Integer remitStatus = HttpUtil.getIntParameter(request, "remitStatus");
				Integer paymentChannelId = HttpUtil.getIntParameter(request, "paymentChannelId");
				int start = HttpUtil.getIntParameter(request, "start");
				int limit = HttpUtil.getIntParameter(request, "limit");
				PageList pList = uWithdrawService.searchHistory(billno, username, minTime, maxTime, minOperatorTime, maxOperatorTime, minMoney, maxMoney, keyword, status, checkStatus, remitStatus, paymentChannelId, start, limit);
				if(pList != null) {
					double[] totalWithdraw = uWithdrawService.getHistoryTotalWithdraw(billno, username, minTime, maxTime, minOperatorTime, maxOperatorTime, minMoney, maxMoney, keyword, status, checkStatus, remitStatus, paymentChannelId);
					json.accumulate("totalRecMoney", totalWithdraw[0]);
					json.accumulate("totalFeeMoney", totalWithdraw[1]);
					json.accumulate("totalCount", pList.getCount());
					json.accumulate("data", pList.getList());
				} else {
					json.accumulate("totalRecMoney", 0);
					json.accumulate("totalFeeMoney", 0);
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
	
	@RequestMapping(value = WUC.LOTTERY_USER_WITHDRAW_GET, method = { RequestMethod.POST })
	@ResponseBody
	public void LOTTERY_USER_WITHDRAW_GET(HttpSession session, HttpServletRequest request, HttpServletResponse response) {
		String actionKey = WUC.LOTTERY_USER_WITHDRAW_GET;
		long t1 = System.currentTimeMillis();
		WebJSONObject json = new WebJSONObject(super.getAdminDataFactory());
		AdminUser uEntity = super.getCurrUser(session, request, response);
		if (uEntity != null) {
			if (super.hasAccess(uEntity, actionKey)) {
				int id = HttpUtil.getIntParameter(request, "id");
				UserWithdrawVO result = uWithdrawService.getById(id);
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

	@RequestMapping(value = WUC.HISTORY_LOTTERY_USER_WITHDRAW_GET, method = { RequestMethod.POST })
	@ResponseBody
	public void HISTORY_LOTTERY_USER_WITHDRAW_GET(HttpSession session, HttpServletRequest request, HttpServletResponse response) {
		String actionKey = WUC.HISTORY_LOTTERY_USER_WITHDRAW_GET;
		long t1 = System.currentTimeMillis();
		WebJSONObject json = new WebJSONObject(super.getAdminDataFactory());
		AdminUser uEntity = super.getCurrUser(session, request, response);
		if (uEntity != null) {
			if (super.hasAccess(uEntity, actionKey)) {
				int id = HttpUtil.getIntParameter(request, "id");
				HistoryUserWithdrawVO result = uWithdrawService.getHistoryById(id);
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
	@RequestMapping(value = WUC.LOTTERY_USER_WITHDRAW_PAY_GET, method = { RequestMethod.POST })
	@ResponseBody
	public void LOTTERY_USER_WITHDRAW_PAY_GET(HttpSession session, HttpServletRequest request, HttpServletResponse response) {
		String actionKey = WUC.LOTTERY_USER_WITHDRAW_PAY_GET;
		long t1 = System.currentTimeMillis();
		WebJSONObject json = new WebJSONObject(super.getAdminDataFactory());
		AdminUser uEntity = super.getCurrUser(session, request, response);
		if (uEntity != null) {
			if (super.hasAccess(uEntity, actionKey)) {
				int id = HttpUtil.getIntParameter(request, "id");
				UserWithdrawVO result = uWithdrawService.getById(id);
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

	@RequestMapping(value = WUC.HISTORY_LOTTERY_USER_WITHDRAW_PAY_GET, method = { RequestMethod.POST })
	@ResponseBody
	public void HISTORY_LOTTERY_USER_WITHDRAW_PAY_GET(HttpSession session, HttpServletRequest request, HttpServletResponse response) {
		String actionKey = WUC.HISTORY_LOTTERY_USER_WITHDRAW_PAY_GET;
		long t1 = System.currentTimeMillis();
		WebJSONObject json = new WebJSONObject(super.getAdminDataFactory());
		AdminUser uEntity = super.getCurrUser(session, request, response);
		if (uEntity != null) {
			if (super.hasAccess(uEntity, actionKey)) {
				int id = HttpUtil.getIntParameter(request, "id");
				HistoryUserWithdrawVO result = uWithdrawService.getHistoryById(id);
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
	
	@RequestMapping(value = WUC.LOTTERY_USER_WITHDRAW_CHECK, method = { RequestMethod.POST })
	@ResponseBody
	public void LOTTERY_USER_WITHDRAW_CHECK(HttpSession session, HttpServletRequest request, HttpServletResponse response) {
		String actionKey = WUC.LOTTERY_USER_WITHDRAW_CHECK;
		long t1 = System.currentTimeMillis();
		WebJSONObject json = new WebJSONObject(super.getAdminDataFactory());
		AdminUser uEntity = super.getCurrUser(session, request, response);
		if (uEntity != null) {
			if (super.hasAccess(uEntity, actionKey)) {
				int id = HttpUtil.getIntParameter(request, "id");
				UserWithdrawVO wBean = uWithdrawService.getById(id);
				if(wBean != null) {
					int userId = wBean.getBean().getUserId();
					UserProfileVO uBean = uService.getUserProfile(wBean.getUsername());
					List<UserBillVO> uBillList = uBillService.getLatest(userId, 5, 20);
					List<UserCardVO> uCardList = uCardService.getByUserId(userId);
					List<UserRechargeVO> uRechargeList = uRechargeService.getLatest(userId, 1, 10);
					List<UserWithdrawVO> uWithdrawList = uWithdrawService.getLatest(userId, 1, 10);
					List<UserBetsVO> uOrderList = uBetsService.getSuspiciousOrder(userId, 10);
					json.accumulate("wBean", wBean);
					json.accumulate("uBean", uBean);
					json.accumulate("uBillList", uBillList);
					json.accumulate("uCardList", uCardList);
					json.accumulate("uRechargeList", uRechargeList);
					json.accumulate("uWithdrawList", uWithdrawList);
					json.accumulate("uOrderList", uOrderList);
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
	
	@RequestMapping(value = WUC.HISTORY_LOTTERY_USER_WITHDRAW_CHECK, method = { RequestMethod.POST })
	@ResponseBody
	public void HISTORY_LOTTERY_USER_WITHDRAW_CHECK(HttpSession session, HttpServletRequest request, HttpServletResponse response) {
		String actionKey = WUC.HISTORY_LOTTERY_USER_WITHDRAW_CHECK;
		long t1 = System.currentTimeMillis();
		WebJSONObject json = new WebJSONObject(super.getAdminDataFactory());
		AdminUser uEntity = super.getCurrUser(session, request, response);
		if (uEntity != null) {
			if (super.hasAccess(uEntity, actionKey)) {
				int id = HttpUtil.getIntParameter(request, "id");
				HistoryUserWithdrawVO wBean = uWithdrawService.getHistoryById(id);
				if(wBean != null) {
					int userId = wBean.getBean().getUserId();
					UserProfileVO uBean = uService.getUserProfile(wBean.getUsername());
					List<UserBillVO> uBillList = uBillService.getLatest(userId, 5, 20);
					List<UserCardVO> uCardList = uCardService.getByUserId(userId);
					List<UserRechargeVO> uRechargeList = uRechargeService.getLatest(userId, 1, 10);
					List<UserWithdrawVO> uWithdrawList = uWithdrawService.getLatest(userId, 1, 10);
					List<UserBetsVO> uOrderList = uBetsService.getSuspiciousOrder(userId, 10);
					
					json.accumulate("wBean", wBean);
					json.accumulate("uBean", uBean);
					json.accumulate("uBillList", uBillList);
					json.accumulate("uCardList", uCardList);
					json.accumulate("uRechargeList", uRechargeList);
					json.accumulate("uWithdrawList", uWithdrawList);
					json.accumulate("uOrderList", uOrderList);
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
	
	@RequestMapping(value = WUC.LOTTERY_USER_WITHDRAW_CHECK_RESULT, method = { RequestMethod.POST })
	@ResponseBody
	public void LOTTERY_USER_WITHDRAW_CHECK_RESULT(HttpSession session, HttpServletRequest request, HttpServletResponse response) {
		String actionKey = WUC.LOTTERY_USER_WITHDRAW_CHECK_RESULT;
		WebJSONObject json = new WebJSONObject(super.getAdminDataFactory());
		AdminUser uEntity = super.getCurrUser(session, request, response);
		if (uEntity != null) {
			if (super.hasAccess(uEntity, actionKey)) {
				int id = HttpUtil.getIntParameter(request, "id");
				int status = HttpUtil.getIntParameter(request, "status");
				if (status==1) {//审核成功
					boolean result = uWithdrawService.check(uEntity,json, id, status);
					if(result) {
						adminUserLogJob.logCheckWithdraw(uEntity, request, id, status);
						json.set(0, "0-5");
					}
				}else{//审核失败
					String remarks = request.getParameter("remarks");
					boolean result = uWithdrawService.reviewedFail(uEntity,json, id, remarks, uEntity.getUsername());
					if(result) {
						adminUserLogJob.reviewedFail(uEntity, request, id, remarks);
						json.set(0, "0-5");
					}
				}
				
			} else {
				json.set(2, "2-4");
			}
		} else {
			json.set(2, "2-6");
		}
		HttpUtil.write(response, json.toString(), HttpUtil.json);
	}
	

	@RequestMapping(value = WUC.LOTTERY_USER_WITHDRAW_MANUAL_PAY, method = { RequestMethod.POST })
	@ResponseBody
	public void LOTTERY_USER_WITHDRAW_MANUAL_PAY(HttpSession session, HttpServletRequest request, HttpServletResponse response) {
		String actionKey = WUC.LOTTERY_USER_WITHDRAW_MANUAL_PAY;
		long t1 = System.currentTimeMillis();
		WebJSONObject json = new WebJSONObject(super.getAdminDataFactory());
		AdminUser uEntity = super.getCurrUser(session, request, response);
		if (uEntity != null) {
			if (super.hasAccess(uEntity, actionKey)) {
				int id = HttpUtil.getIntParameter(request, "id");
				String payBillno = request.getParameter("payBillno");
				String remarks = request.getParameter("remarks");
				boolean result = uWithdrawService.manualPay(uEntity,json, id, payBillno, remarks, uEntity.getUsername());
				if(result) {
					adminUserLogJob.logManualPay(uEntity, request, id, payBillno, remarks);
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

	@RequestMapping(value = WUC.LOTTERY_USER_WITHDRAW_REFUSE, method = { RequestMethod.POST })
	@ResponseBody
	public void LOTTERY_USER_WITHDRAW_REFUSE(HttpSession session, HttpServletRequest request, HttpServletResponse response) {
		String actionKey = WUC.LOTTERY_USER_WITHDRAW_REFUSE;
		long t1 = System.currentTimeMillis();
		WebJSONObject json = new WebJSONObject(super.getAdminDataFactory());
		AdminUser uEntity = super.getCurrUser(session, request, response);
		if (uEntity != null) {
			if (super.hasAccess(uEntity, actionKey)) {
				int id = HttpUtil.getIntParameter(request, "id");
				String reason = request.getParameter("reason");
				String remarks = request.getParameter("remarks");
				boolean result = uWithdrawService.refuse(uEntity,json, id, reason, remarks, uEntity.getUsername());
				if(result) {
					adminUserLogJob.logRefuseWithdraw(uEntity, request, id, reason, remarks);
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

	@RequestMapping(value = WUC.LOTTERY_USER_WITHDRAW_WITHDRAW_FAILURE, method = { RequestMethod.POST })
	@ResponseBody
	public void LOTTERY_USER_WITHDRAW_WITHDRAW_FAILURE(HttpSession session, HttpServletRequest request, HttpServletResponse response) {
		String actionKey = WUC.LOTTERY_USER_WITHDRAW_WITHDRAW_FAILURE;
		long t1 = System.currentTimeMillis();
		WebJSONObject json = new WebJSONObject(super.getAdminDataFactory());
		AdminUser uEntity = super.getCurrUser(session, request, response);
		if (uEntity != null) {
			if (super.hasAccess(uEntity, actionKey)) {
				int id = HttpUtil.getIntParameter(request, "id");
				String remarks = request.getParameter("remarks");
				boolean result = uWithdrawService.withdrawFailure(uEntity,json, id, remarks, uEntity.getUsername());
				if(result) {
					adminUserLogJob.logWithdrawFailure(uEntity, request, id, remarks);
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

	@RequestMapping(value = WUC.LOTTERY_USER_WITHDRAW_COMPLETE_REMIT, method = { RequestMethod.POST })
	@ResponseBody
	public void LOTTERY_USER_WITHDRAW_COMPLETE_REMIT(HttpSession session, HttpServletRequest request, HttpServletResponse response) {
		String actionKey = WUC.LOTTERY_USER_WITHDRAW_COMPLETE_REMIT;
		long t1 = System.currentTimeMillis();
		WebJSONObject json = new WebJSONObject(super.getAdminDataFactory());
		AdminUser uEntity = super.getCurrUser(session, request, response);
		if (uEntity != null) {
			if (super.hasAccess(uEntity, actionKey)) {
				int id = HttpUtil.getIntParameter(request, "id");
				boolean result = uWithdrawService.completeRemit(uEntity,json, id, uEntity.getUsername());
				if(result) {
					adminUserLogJob.logCompleteRemitWithdraw(uEntity, request, id);
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


	@RequestMapping(value = WUC.LOTTERY_USER_WITHDRAW_LOCK, method = { RequestMethod.POST })
	@ResponseBody
	public void LOTTERY_USER_WITHDRAW_LOCK(HttpSession session, HttpServletRequest request, HttpServletResponse response) {
		String actionKey = WUC.LOTTERY_USER_WITHDRAW_LOCK;
		WebJSONObject json = new WebJSONObject(super.getAdminDataFactory());
		AdminUser uEntity = super.getCurrUser(session, request, response);
		if (uEntity != null) {
			if (super.hasAccess(uEntity, actionKey)) {
				int id = HttpUtil.getIntParameter(request, "id");
				String operatorUser = uEntity.getUsername(); // 操作人
				boolean result = uWithdrawService.lock(uEntity,json, id, operatorUser);
				if(result) {
					adminUserLogJob.logLockWithdraw(uEntity, request, id);
					json.set(0, "0-5");
				}
			} else {
				json.set(2, "2-4");
			}
		} else {
			json.set(2, "2-6");
		}
		HttpUtil.write(response, json.toString(), HttpUtil.json);
	}
	
	@RequestMapping(value = WUC.LOTTERY_USER_WITHDRAW_UNLOCK, method = { RequestMethod.POST })
	@ResponseBody
	public void LOTTERY_USER_WITHDRAW_UNLOCK(HttpSession session, HttpServletRequest request, HttpServletResponse response) {
		String actionKey = WUC.LOTTERY_USER_WITHDRAW_UNLOCK;
		WebJSONObject json = new WebJSONObject(super.getAdminDataFactory());
		AdminUser uEntity = super.getCurrUser(session, request, response);
		if (uEntity != null) {
			if (super.hasAccess(uEntity, actionKey)) {
				int id = HttpUtil.getIntParameter(request, "id");
				String operatorUser = uEntity.getUsername(); // 操作人
				boolean result = uWithdrawService.unlock(uEntity,json, id, operatorUser);
				if(result) {
					adminUserLogJob.logUnLockWithdraw(uEntity, request, id);
					json.set(0, "0-5");
				}
			} else {
				json.set(2, "2-4");
			}
		} else {
			json.set(2, "2-6");
		}
		HttpUtil.write(response, json.toString(), HttpUtil.json);
	}

	@RequestMapping(value = WUC.LOTTERY_USER_WITHDRAW_API_PAY, method = { RequestMethod.POST })
	@ResponseBody
	public void LOTTERY_USER_WITHDRAW_API_PAY(HttpSession session, HttpServletRequest request, HttpServletResponse response) {
		 String actionKey = WUC.LOTTERY_USER_WITHDRAW_API_PAY;
		 long t1 = System.currentTimeMillis();
		 WebJSONObject json = new WebJSONObject(super.getAdminDataFactory());
		 AdminUser uEntity = super.getCurrUser(session, request, response);
		 if (uEntity != null) {
		 	if (super.hasAccess(uEntity, actionKey)) {
		 		int id = HttpUtil.getIntParameter(request, "id");
		 		String channelCode = HttpUtil.getStringParameterTrim(request, "channelCode");
		 		Integer channelId = HttpUtil.getIntParameter(request, "channelId");
		 		if (API_PAY_ORDER_CACHE.containsKey(id)) {
		 			json.set(2, "2-4017");
		 		}
				else {

					PaymentChannel channel = dataFactory.getPaymentChannelFullProperty(channelCode, channelId);
					if (channel == null) {
						json.setWithParams(2, "2-4015", channelCode);
					}
					else {
						API_PAY_ORDER_CACHE.put(id, true);

						boolean result = uWithdrawService.apiPay(uEntity, json, id, channel);
						if(result) {
							adminUserLogJob.logAPIPay(uEntity, request, id, channel);
							if (json.getError() == null || json.getError() == 0) {
								json.set(0, "0-5");
							}
						}
						else {
							API_PAY_ORDER_CACHE.remove(id);
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
}