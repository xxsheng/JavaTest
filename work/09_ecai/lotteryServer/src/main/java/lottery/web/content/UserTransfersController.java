package lottery.web.content;

import javautils.http.HttpUtil;
import javautils.jdbc.PageList;
import javautils.math.MathUtil;
import lottery.domains.content.api.ag.AGAPI;
import lottery.domains.content.api.im.IMAPI;
import lottery.domains.content.api.pt.PTAPI;
import lottery.domains.content.api.sb.Win88SBAPI;
import lottery.domains.content.biz.UserGameAccountService;
import lottery.domains.content.biz.UserTransfersService;
import lottery.domains.content.biz.read.UserReadService;
import lottery.domains.content.biz.read.UserTransfersReadService;
import lottery.domains.content.entity.SysPlatform;
import lottery.domains.content.entity.User;
import lottery.domains.content.entity.UserGameAccount;
import lottery.domains.content.global.Global;
import lottery.domains.pool.DataFactory;
import lottery.web.WUC;
import lottery.web.WebJSON;
import lottery.web.content.validate.UserValidate;
import lottery.web.helper.AbstractActionController;
import lottery.web.helper.session.SessionUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class UserTransfersController extends AbstractActionController {
	/**
	 * Service
	 */
	@Autowired
	private UserTransfersService uTransfersService;
	@Autowired
	private UserTransfersReadService uTransfersReadService;

	@Autowired
	private UserGameAccountService uGameAccountService;

	@Autowired
	private UserReadService uReadService;

	@Autowired
	private PTAPI ptAPI;

	@Autowired
	private AGAPI agAPI;

	@Autowired
	private Win88SBAPI win88SBAPI;

	@Autowired
	private IMAPI imApi;

	/**
	 * Validate
	 */
	@Autowired
	private UserValidate uValidate;

	/**
	 * Util
	 */

	/**
	 * DataFactory
	 */
	@Autowired
	private DataFactory dataFactory;

	@RequestMapping(value = WUC.USER_TRANSFERS_LOAD, method = { RequestMethod.POST })
	@ResponseBody
	public Map<String, Object> USER_TRANSFERS_LOAD(HttpSession session, HttpServletRequest request) {
		WebJSON json = new WebJSON(dataFactory);
		SessionUser sessionUser = super.getSessionUser(json, session, request);
		if (sessionUser == null) {
			return json.toJson();
		}

		User uEntity = uReadService.getByIdFromRead(sessionUser.getId());
		if (uEntity == null) {
			super.logOut(session, request);
			json.set(2, "2-10"); // 您的账户信息发生变更，请重新登录
			return json.toJson();
		}

		List<SysPlatform> listPlatform = dataFactory.listSysPlatform();
		// 主账户
		Map<String, Object> data = new HashMap<>();
		data.put("listPlatform", listPlatform);
		data.put("totalMoney", uEntity.getTotalMoney());
		data.put("lotteryMoney", uEntity.getLotteryMoney());
		json.data("data", data);
		json.set(0, "0-1");
		return json.toJson();
	}
	
	@RequestMapping(value = WUC.USER_TRANSFERS_SELF, method = { RequestMethod.POST })
	@ResponseBody
	public Map<String, Object> USER_TRANSFERS_SELF(HttpSession session, HttpServletRequest request) {
		WebJSON json = new WebJSON(dataFactory);
		SessionUser sessionUser = super.getSessionUser(json, session, request);
		if (sessionUser == null) {
			return json.toJson();
		}

		//先增加ip黑名单验证
		String ip = HttpUtil.getRealIp(json, request);
		if (ip == null) {
			return json.toJson();
		}
		boolean blackList = uValidate.testIpBlackList(ip);
		if(blackList){
			json.set(2, "2-1073", ip);
			return json.toJson();
		}
		//验证是否可以平台转账
		User user = uReadService.getByIdFromRead(sessionUser.getId());
		
		if (user.getType() == Global.USER_TYPE_PLAYER &&user.getNickname().equals("试玩用户")) {
			json.set(2, "2-13"); // 您的账号无法使用该功能！
			return json.toJson();
		}
	/*	if (user.getType() == Global.USER_TYPE_FICTITIOUS ) {
			json.set(2, "2-13"); // 您的账号无法使用该功能！
			return json.toJson();
		}*/
		
		if (user.getAllowPlatformTransfers() !=1) {
			json.set(2, "2-1090");
			return json.toJson();
		}
		
		
		int toAccount = HttpUtil.getIntParameter(request, "toAccount");
		int fromAccount = HttpUtil.getIntParameter(request, "fromAccount");
		if(toAccount == fromAccount){
			json.set(2, "2-4017");
		}else{
			String amountChar = request.getParameter("amount");
//				double amount =  new BigDecimal(amountChar).doubleValue();
			double amount = MathUtil.StringFormat(amountChar, 2);
			if(amount > 0){
				boolean result = uTransfersService.transfersToSelf(json, sessionUser.getId(), toAccount, fromAccount, amount);
				if(result) {
					json.set(0, "0-1");
				}
			}else{
				json.set(2, "2-4018",amount);
			}
		}
		return json.toJson();
	}

	@RequestMapping(value = WUC.USER_TRANSFERS_SELF_ALL, method = { RequestMethod.POST })
	@ResponseBody
	public Map<String, Object> USER_TRANSFERS_SELF_ALL(HttpSession session, HttpServletRequest request) {
		WebJSON json = new WebJSON(dataFactory);
		SessionUser sessionUser = super.getSessionUser(json, session, request);
		if (sessionUser == null) {
			return json.toJson();
		}

		//先增加ip黑名单验证
		String ip = HttpUtil.getRealIp(json, request);
		if (ip == null) {
			return json.toJson();
		}
		boolean blackList = uValidate.testIpBlackList(ip);
		if(blackList){
			json.set(2, "2-1073", ip);
			return json.toJson();
		}
		//验证是否可以平台转账
		User user = uReadService.getByIdFromRead(sessionUser.getId());
		if (user.getType() == Global.USER_TYPE_PLAYER &&user.getNickname().equals("试玩用户")) {
			json.set(2, "2-13"); // 您的账号无法使用该功能！
			return json.toJson();
		}
		
		if (user.getType() == Global.USER_TYPE_FICTITIOUS ) {
			json.set(2, "2-13"); // 您的账号无法使用该功能！
			return json.toJson();
		}
		
		if (user.getAllowPlatformTransfers() !=1) {
			json.set(2, "2-1090");
			return json.toJson();
		}

		boolean result = uTransfersService.transferAll(json, sessionUser.getId());
		if(result) {
			json.set(0, "0-1");
		}
		return json.toJson();
	}

	@RequestMapping(value = WUC.USER_TRANSFERS_SEARCH, method = { RequestMethod.POST })
	@ResponseBody
	public Map<String, Object> USER_TRANSFERS_SEARCH(HttpSession session, HttpServletRequest request) {
		WebJSON json = new WebJSON(dataFactory);
		SessionUser sessionUser = super.getSessionUser(json, session, request);
		if (sessionUser == null) {
			return json.toJson();
		}

		String billno = request.getParameter("billno");
		Integer type = HttpUtil.getIntParameter(request, "type");
		String sTime = request.getParameter("sTime");
		String eTime = request.getParameter("eTime");
		int start = HttpUtil.getIntParameter(request, "start");
		int limit = HttpUtil.getIntParameter(request, "limit");
		PageList pList = uTransfersReadService.search(sessionUser.getId(), billno, type, sTime, eTime, start, limit);
		json.data("totalCount", pList.getCount());
		json.data("data", pList.getList());
		json.set(0, "0-1");
		return json.toJson();
	}

	@RequestMapping(value = WUC.ACCOUNT_BALANCE, method = { RequestMethod.POST })
	@ResponseBody
	public Map<String, Object> ACCOUNT_BALANCE(HttpSession session, HttpServletRequest request) {
		WebJSON json = new WebJSON(dataFactory);
		SessionUser sessionUser = super.getSessionUser(json, session, request);
		if (sessionUser == null) {
			return json.toJson();
		}

		int platformId = HttpUtil.getIntParameter(request, "platformId");
		SysPlatform sysPlatform = dataFactory.getSysPlatform(platformId);
		if (sysPlatform == null) {
			json.set(2, "2-7007");
			return json.toJson();
		}

		Map<String, Object> data = new HashMap<>();
		if (sysPlatform.getStatus() != 0) {
			data.put("balance", 0);
			json.data("data", data);
			json.set(0, "0-1");
			return json.toJson();
		}

		User uEntity = uReadService.getByIdFromRead(sessionUser.getId());
		if (uEntity == null) {
			super.logOut(session, request);
			json.set(2, "2-10"); // 您的账户信息发生变更，请重新登录
			return json.toJson();
		}

		if (platformId == 1) { // 主账户
			data.put("balance", uEntity.getTotalMoney());
			json.data("data", data);
			json.set(0, "0-1");
		}
		else if (platformId == 2) { // 彩票账户
			data.put("balance", uEntity.getLotteryMoney());
			json.data("data", data);
			json.set(0, "0-1");
		}
		else if (platformId == 11) { // PT
			UserGameAccount account = dataFactory.getGameAccount(uEntity.getId(), platformId, 1);
			if (account != null) {
				Double balance = ptAPI.playerBalance(json, account.getUsername());
				data.put("balance", balance == null ? 0 : balance);
				json.data("data", data);
				json.set(0, "0-1");
			}
			else {
				data.put("balance", 0);
				json.data("data", data);
				json.set(0, "0-1");
			}
		}
		else if (platformId == 4) { // AG
			Double balance = agAPI.getBalanceNew(json, uEntity.getUsername());
			if (balance != null) {
				data.put("balance", balance == null ? 0 : balance);
				json.data("data", data);
				json.set(0, "0-1");
			}else{
				data.put("balance", 0);
				json.data("data", data);
				json.set(0, "0-1");
			}

//			UserGameAccount account = dataFactory.getGameAccount(uEntity.getId(), platformId, 1);
//			if (account != null) {
//				Double balance = agAPI.getBalance(json, account.getUsername(), uGameAccountService.decryptPwd(account.getPassword()));
//				data.put("balance", balance == null ? 0 : balance);
//				json.data("data", data);
//				json.set(0, "0-1");
//			}
//			else {
//				data.put("balance", 0);
//				json.data("data", data);
//				json.set(0, "0-1");
//			}
		}
		else if (platformId == Global.BILL_ACCOUNT_IM) { // IM
			UserGameAccount account = dataFactory.getGameAccount(uEntity.getId(), platformId, 1);
			if (account != null) {
				Double balance = imApi.balance(json, account.getUsername());
				data.put("balance", balance == null ? 0 : balance);
				json.data("data", data);
				json.set(0, "0-1");
			}
			else {
				data.put("balance", 0);
				json.data("data", data);
				json.set(0, "0-1");
			}
		}
		else if (platformId == Global.BILL_ACCOUNT_SB) { // SB
			UserGameAccount account = dataFactory.getGameAccount(uEntity.getId(), platformId, 1);
			if (account != null) {
				Double balance = win88SBAPI.checkUserBalance(json, account.getUsername());
				data.put("balance", balance == null ? 0 : balance);
				json.data("data", data);
				json.set(0, "0-1");
			}
			else {
				data.put("balance", 0);
				json.data("data", data);
				json.set(0, "0-1");
			}
		}
		else {
			data.put("balance", 0);
			json.data("data", data);
			json.set(0, "0-1");
		}
		return json.toJson();
	}

}