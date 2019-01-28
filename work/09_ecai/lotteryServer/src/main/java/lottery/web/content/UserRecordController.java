package lottery.web.content;

import javautils.http.HttpUtil;
import javautils.jdbc.PageList;
import lottery.domains.content.biz.read.GameBetsReadService;
import lottery.domains.content.biz.read.UserBetsReadService;
import lottery.domains.content.biz.read.UserBillReadService;
import lottery.domains.content.biz.read.UserReadService;
import lottery.domains.content.entity.User;
import lottery.domains.content.global.Global;
import lottery.domains.content.vo.bets.UserBetsVO;
import lottery.domains.pool.DataFactory;
import lottery.web.WUC;
import lottery.web.WebJSON;
import lottery.web.content.validate.UserProxyValidate;
import lottery.web.helper.AbstractActionController;
import lottery.web.helper.session.SessionUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.List;
import java.util.Map;

@Controller
public class UserRecordController extends AbstractActionController {
	/**
	 * Service
	 */
	@Autowired
	private UserBetsReadService uBetsReadService;

	@Autowired
	private GameBetsReadService gameBetsReadService;

	@Autowired
	private UserBillReadService uBillReadService;

	@Autowired
	private UserReadService uReadService;

	/**
	 * Validate
	 */
	@Autowired
	private UserProxyValidate uProxyValidate;

	/**
	 * Util
	 */

	/**
	 * DataFactory
	 */
	@Autowired
	private DataFactory dataFactory;


	@RequestMapping(value = WUC.USER_BETS_GENERAL_SEARCH, method = { RequestMethod.POST })
	@ResponseBody
	public Map<String, Object> USER_BETS_GENERAL_SEARCH(HttpSession session, HttpServletRequest request) {
		WebJSON json = new WebJSON(dataFactory);
		SessionUser sessionUser = super.getSessionUser(json, session, request);
		if (sessionUser == null) {
			return json.toJson();
		}

		Integer lotteryId = HttpUtil.getIntParameter(request, "lotteryId");
		String expect = HttpUtil.getStringParameterTrim(request, "expect");
		Integer status = HttpUtil.getIntParameter(request, "status");
		String sTime = HttpUtil.getStringParameterTrim(request, "sTime");
		String eTime = HttpUtil.getStringParameterTrim(request, "eTime");
		int start = HttpUtil.getIntParameter(request, "start");
		int limit = HttpUtil.getIntParameter(request, "limit");

		PageList pList = uBetsReadService.searchByUserId(sessionUser.getId(), null, lotteryId, expect, status, sTime, eTime, start, limit);
		json.data("data", pList.getList());
		json.data("totalCount", pList.getCount());
		json.set(0, "0-1");
		return json.toJson();
	}

	@RequestMapping(value = WUC.USER_BETS_DETAILS, method = { RequestMethod.POST })
	@ResponseBody
	public Map<String, Object> USER_BETS_DETAILS(HttpSession session, HttpServletRequest request) {
		WebJSON json = new WebJSON(dataFactory);
		SessionUser sessionUser = super.getSessionUser(json, session, request);
		if (sessionUser == null) {
			return json.toJson();
		}

		int id = HttpUtil.getIntParameter(request, "id");
		UserBetsVO bean = uBetsReadService.getByIdWithCodes(id, sessionUser.getId());
		if(bean != null) {
			if(!bean.getUsername().equals(sessionUser.getUsername())){
				bean.setAllowCancel(false);
			}
			json.data("data", bean);
			json.set(0, "0-1");
		} else {
			json.set(2, "2-1028");
		}
		return json.toJson();
	}

	@RequestMapping(value = WUC.USER_BETS_CHASE_SEARCH, method = { RequestMethod.POST })
	@ResponseBody
	public Map<String, Object> USER_BETS_CHASE_SEARCH(HttpSession session, HttpServletRequest request) {
		WebJSON json = new WebJSON(dataFactory);
		SessionUser sessionUser = super.getSessionUser(json, session, request);
		if (sessionUser == null) {
			return json.toJson();
		}

		Integer lotteryId = HttpUtil.getIntParameter(request, "lotteryId");
		String expect =  HttpUtil.getStringParameterTrim(request, "expect");
		Integer status = HttpUtil.getIntParameter(request, "status");
		String sTime = HttpUtil.getStringParameterTrim(request, "sTime");
		String eTime = HttpUtil.getStringParameterTrim(request, "eTime");
		int start = HttpUtil.getIntParameter(request, "start");
		int limit = HttpUtil.getIntParameter(request, "limit");

		PageList pList = uBetsReadService.searchByUserId(sessionUser.getId(), Global.USER_BETS_TYPE_CHASE, lotteryId, expect, status, sTime, eTime, start, limit);
		json.data("data", pList.getList());
		json.data("totalCount", pList.getCount());
		json.set(0, "0-1");
		return json.toJson();
	}

	@RequestMapping(value = WUC.RECENT_USER_BETS_GENERAL_SEARCH, method = { RequestMethod.POST })
	@ResponseBody
	public Map<String, Object> RECENT_USER_BETS_GENERAL_SEARCH(HttpSession session, HttpServletRequest request) {
		WebJSON json = new WebJSON(dataFactory);
		SessionUser sessionUser = super.getSessionUser(json, session, request);
		if (sessionUser == null) {
			return json.toJson();
		}
		List<UserBetsVO> recentUserBets = uBetsReadService.getRecentUserBets(sessionUser.getId());
		json.data("data", recentUserBets);
		json.set(0, "0-1");
		return json.toJson();
	}

	@RequestMapping(value = WUC.RECENT_USER_BETS_CHASE_SEARCH, method = { RequestMethod.POST })
	@ResponseBody
	public Map<String, Object> RECENT_USER_BETS_CHASE_SEARCH(HttpSession session, HttpServletRequest request) {
		WebJSON json = new WebJSON(dataFactory);
		SessionUser sessionUser = super.getSessionUser(json, session, request);
		if (sessionUser == null) {
			return json.toJson();
		}
		List<UserBetsVO> recentUserBets = uBetsReadService.getRecentChaseUserBets(sessionUser.getId());
		json.data("data", recentUserBets);
		json.set(0, "0-1");
		return json.toJson();
	}


	@RequestMapping(value = WUC.RECENT_USER_BETS_UNOPEN_SEARCH, method = { RequestMethod.POST })
	@ResponseBody
	public Map<String, Object> RECENT_USER_BETS_UNOPEN_SEARCH(HttpSession session, HttpServletRequest request) {
		WebJSON json = new WebJSON(dataFactory);
		SessionUser sessionUser = super.getSessionUser(json, session, request);
		if (sessionUser == null) {
			return json.toJson();
		}
		List<UserBetsVO> recentUserBets = uBetsReadService.getRecentUserBetsUnOpen(sessionUser.getId());
		json.data("data", recentUserBets);
		json.set(0, "0-1");
		return json.toJson();
	}


	@RequestMapping(value = WUC.RECENT_USER_BETS_OPENED_SEARCH, method = { RequestMethod.POST })
	@ResponseBody
	public Map<String, Object> RECENT_USER_BETS_OPENED_SEARCH(HttpSession session, HttpServletRequest request) {
		WebJSON json = new WebJSON(dataFactory);
		SessionUser sessionUser = super.getSessionUser(json, session, request);
		if (sessionUser == null) {
			return json.toJson();
		}
		List<UserBetsVO> recentUserBets = uBetsReadService.getRecentUserBetsOpened(sessionUser.getId());
		json.data("data", recentUserBets);
		json.set(0, "0-1");
		return json.toJson();
	}

	@RequestMapping(value = WUC.USER_BILL_SEARCH, method = { RequestMethod.POST })
	@ResponseBody
	public Map<String, Object> USER_BILL_SEARCH(HttpSession session, HttpServletRequest request) {
		WebJSON json = new WebJSON(dataFactory);
		SessionUser sessionUser = super.getSessionUser(json, session, request);
		if (sessionUser == null) {
			return json.toJson();
		}

		Integer account = HttpUtil.getIntParameter(request, "account");
		Integer type = HttpUtil.getIntParameter(request, "type");
		String sTime = HttpUtil.getStringParameterTrim(request, "sTime");
		String eTime = HttpUtil.getStringParameterTrim(request, "eTime");
		int start = HttpUtil.getIntParameter(request, "start");
		int limit = HttpUtil.getIntParameter(request, "limit");

		PageList pList = uBillReadService.searchByUserId(sessionUser.getId(), account, type, sTime, eTime, start, limit);
		json.data("data", pList.getList());
		json.data("totalCount", pList.getCount());
		json.set(0, "0-1");
		return json.toJson();
	}

	@RequestMapping(value = WUC.USER_BILL_DETAILS, method = { RequestMethod.POST })
	@ResponseBody
	public Map<String, Object> USER_BILL_DETAILS(HttpSession session, HttpServletRequest request) {
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

		int id = HttpUtil.getIntParameter(request, "id");
		UserBetsVO result = uBetsReadService.getByIdWithCodes(id);
		boolean isShow = false;
		if(result != null) {
			// 自己的订单
			if(result.getBean().getUserId() == sessionUser.getId()) {
				isShow = true;
			} else {
				User targetUser = uReadService.getByIdFromRead(result.getBean().getUserId());
				// 下级或关联账号的订单
				if(uProxyValidate.isLowerUser(sessionUser, targetUser) || uProxyValidate.isRelated(uEntity, targetUser)) {
					isShow = true;
				}
			}
		}
		if(isShow) {
			if(result!= null && !result.getUsername().equals(sessionUser.getUsername())){
				result.setAllowCancel(false);
			}
			json.data("data", result);
			json.set(0, "0-1");
		} else {
			json.set(2, "2-1028");
		}
		return json.toJson();
	}
    //
	// @RequestMapping(value = WUC.USER_BETS_PROFIT, method = { RequestMethod.POST })
	// @ResponseBody
	// public Map<String, Object> USER_BETS_PROFIT(HttpSession session, HttpServletRequest request) {
	// 	WebJSON json = new WebJSON(dataFactory);
	// 	SessionUser sessionUser = super.getSessionUser(json, session, request);
	// 	if (sessionUser == null) {
	// 		return json.toJson();
	// 	}
    //
	// 	int lotteryId = HttpUtil.getIntParameter(request, "lotteryId");
	// 	String expect = request.getParameter("expect");
	// 	Lottery lBean = dataFactory.getLottery(lotteryId);
	// 	UserBetsProfitVO bean = new UserBetsProfitVO();
	// 	if(lBean != null) {
	// 		Object[] result = uBetsReadService.getProfitMoney(sessionUser.getId(), lBean.getId(), expect);
	// 		bean.setLottery(lBean.getShowName());
	// 		bean.setExpect(expect);
	// 		bean.setTotalCount(ObjectUtil.toInt(result[0]));
	// 		bean.setTotalMoney(ObjectUtil.toDouble(result[1]));
	// 		bean.setTotalReturn(ObjectUtil.toDouble(result[2]));
	// 		bean.setTotalPrize(ObjectUtil.toDouble(result[3]));
	// 	}
	// 	json.data("data", bean);
	// 	json.set(0, "0-1");
	// 	return json.toJson();
	// }

	@RequestMapping(value = WUC.USER_GAME_BETS_SEARCH, method = { RequestMethod.POST })
	@ResponseBody
	public Map<String, Object> USER_GAME_BETS_SEARCH(HttpSession session, HttpServletRequest request) {
		WebJSON json = new WebJSON(dataFactory);
		SessionUser sessionUser = super.getSessionUser(json, session, request);
		if (sessionUser == null) {
			return json.toJson();
		}

		Integer platformId = HttpUtil.getIntParameter(request, "platformId");
		String sTime = HttpUtil.getStringParameterTrim(request, "sTime");
		String eTime = HttpUtil.getStringParameterTrim(request, "eTime");
		int start = HttpUtil.getIntParameter(request, "start");
		int limit = HttpUtil.getIntParameter(request, "limit");

		PageList pList = gameBetsReadService.searchByUserId(sessionUser.getId(), platformId, sTime, eTime, start, limit);
		json.data("data", pList.getList());
		json.data("totalCount", pList.getCount());
		json.set(0, "0-1");
		return json.toJson();
	}
}