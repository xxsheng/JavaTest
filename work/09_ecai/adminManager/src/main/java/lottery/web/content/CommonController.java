package lottery.web.content;


import admin.domains.content.entity.AdminUser;
import admin.web.WUC;
import admin.web.WebJSONObject;
import admin.web.helper.AbstractActionController;
import javautils.http.HttpUtil;
import lottery.domains.content.biz.UserHighPrizeService;
import lottery.domains.content.dao.*;
import lottery.domains.pool.LotteryDataFactory;
import lottery.web.content.utils.SessionCounterUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@Controller
public class CommonController extends AbstractActionController {

	@Autowired
	private UserDao uDao;


	@Autowired
	private UserWithdrawDao uWithdrawDao;

@Autowired
	private UserRechargeDao uRechargeDao;

	/**
	 * 活动
	 */
	@Autowired
	private ActivityBindBillDao aBindBillDao;

	@Autowired
	private ActivityRechargeBillDao aRechargeBillDao;

	@Autowired
	private UserHighPrizeService highPrizeService;

	/**
	 * 支付
	 */
	@Autowired
	private PaymentCardDao paymentCardDao;

	@Autowired
	private PaymentChannelDao paymentChannelDao;

	@Autowired
	private SessionCounterUtil sessionCounterUtil;

	@Autowired
	private LotteryDataFactory dataFactory;

	@RequestMapping(value = WUC.GLOBAL, method = { RequestMethod.POST })
	@ResponseBody
	public void GLOBAL(HttpSession session, HttpServletRequest request, HttpServletResponse response) {
		WebJSONObject json = new WebJSONObject(super.getAdminDataFactory());
		AdminUser uEntity = super.getCurrUser(session, request, response);
		if (uEntity != null) {
			int uOnlineCount = uDao.getOnlineCount(null);
			int uWithdrawCount = uWithdrawDao.getWaitTodo();
			int aBindCount = aBindBillDao.getWaitTodo();
			int aRechargeCount = aRechargeBillDao.getWaitTodo();
			int bRegchargeCount = uRechargeDao.getRechargeCount(0,1,2);
			
			int pCardOverload = paymentCardDao.getOverload();
			int pChannelOverload = paymentChannelDao.getOverload();
			json.accumulate("bRegchargeCount", bRegchargeCount);
			json.accumulate("uOnlineCount", uOnlineCount);
			json.accumulate("uWithdrawCount", uWithdrawCount);
			json.accumulate("aBindCount", aBindCount);
			json.accumulate("aRechargeCount", aRechargeCount);
			json.accumulate("pCardOverload", pCardOverload);
			json.accumulate("pChannelOverload", pChannelOverload);
			json.accumulate("isUnlockedWithdrawPwd", isUnlockedWithdrawPwd(session));
			json.set(0, "0-3");
		} else {
			json.set(2, "2-6");
		}
		HttpUtil.write(response, json.toString(), HttpUtil.json);
	}

	@RequestMapping(value = WUC.ADMIN_GLOBAL_CONFIG, method = { RequestMethod.POST })
	@ResponseBody
	public void ADMIN_GLOBAL_CONFIG(HttpSession session, HttpServletRequest request, HttpServletResponse response) {
		WebJSONObject json = new WebJSONObject(super.getAdminDataFactory());
		AdminUser uEntity = super.getCurrUser(session, request, response);
		if (uEntity != null) {
			json.accumulate("adminGlobalConfig", dataFactory.getAdminGlobalConfig());
			json.set(0, "0-3");
		} else {
			json.set(2, "2-6");
		}
		HttpUtil.write(response, json.toString(), HttpUtil.json);
	}

	@RequestMapping(value = WUC.HIGH_PRIZE_UNPROCESS_COUNT, method = { RequestMethod.POST })
	@ResponseBody
	public void HIGH_PRIZE_UNPROCESS_COUNT(HttpSession session, HttpServletRequest request, HttpServletResponse response) {
		WebJSONObject json = new WebJSONObject(super.getAdminDataFactory());
		AdminUser uEntity = super.getCurrUser(session, request, response);
		if (uEntity != null) {
			int unProcessCount = highPrizeService.getUnProcessCount();
			json.accumulate("unProcessCount", unProcessCount);
			json.set(0, "0-3");
		} else {
			json.set(2, "2-6");
		}
		HttpUtil.write(response, json.toString(), HttpUtil.json);
	}

}