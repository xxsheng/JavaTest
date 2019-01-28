package lottery.web.content;

import javautils.http.HttpUtil;
import lottery.domains.content.biz.UserBetsService;
import lottery.domains.content.biz.UserService;
import lottery.domains.content.entity.User;
import lottery.domains.pool.DataFactory;
import lottery.web.WUC;
import lottery.web.WebJSON;
import lottery.web.content.validate.UserBetsValidate;
import lottery.web.helper.AbstractActionController;
import lottery.web.helper.session.SessionUser;
import net.sf.json.JSONArray;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.Map;

@Controller
public class UserBetsController extends AbstractActionController {
	private static final Logger log = LoggerFactory.getLogger("lottery.bets");
	/**
	 * Service
	 */
	@Autowired
	private UserBetsService uBetsService;

	@Autowired
	private UserBetsValidate uBetsValidate;

	@Autowired
	private UserService uService;

	/**
	 * Validate
	 */

	/**
	 * Util
	 */

	/**
	 * DataFactory
	 */
	@Autowired
	private DataFactory dataFactory;
	
	@RequestMapping(value = WUC.USER_BETS_GENERAL, method = { RequestMethod.POST })
	@ResponseBody
	public Map<String, Object> USER_BETS_GENERAL(HttpSession session, HttpServletRequest request) {
		try {
			WebJSON json = new WebJSON(dataFactory);
			SessionUser sessionUser = super.getSessionUser(json, session, request);
			if (sessionUser == null) {
                return json.toJson();
            }

			String ip = HttpUtil.getRealIp(json, request);
			if (ip == null) {
				return json.toJson();
			}


			User uEntity = uService.getById(sessionUser.getId());
			if (uEntity == null) {
                super.logOut(session, request);
                json.set(2, "2-10"); // 您的账户信息发生变更，请重新登录
                return json.toJson();
            }

			String blist = request.getParameter("blist");
			JSONArray bjson = JSONArray.fromObject(blist);
			boolean isUserAllow = uBetsValidate.isUserAllow(json, uEntity, session);
			if(isUserAllow) {
                boolean result = uBetsService.general(json, uEntity, bjson, ip);
                if(result) {
                    json.set(0, "0-5");
                }
            }
			return json.toJson();
		} catch (Exception e) {
			log.error("普通投注发生异常", e);
			WebJSON json = new WebJSON(dataFactory);
			json.set(2, "1-1");
			return json.toJson();
		}
	}

	@RequestMapping(value = WUC.USER_BETS_REBET, method = { RequestMethod.POST })
	@ResponseBody
	public Map<String, Object> USER_BETS_REBET(HttpSession session, HttpServletRequest request) {
		try {
			WebJSON json = new WebJSON(dataFactory);
			SessionUser sessionUser = super.getSessionUser(json, session, request);
			if (sessionUser == null) {
                return json.toJson();
            }

			String ip = HttpUtil.getRealIp(json, request);
			if (ip == null) {
				return json.toJson();
			}

			User uEntity = uService.getById(sessionUser.getId());
			if (uEntity == null) {
                super.logOut(session, request);
                json.set(2, "2-10"); // 您的账户信息发生变更，请重新登录
                return json.toJson();
            }

			int id = HttpUtil.getIntParameter(request, "id");
			boolean isUserAllow = uBetsValidate.isUserAllow(json, uEntity, session);
			if(isUserAllow) {
                boolean result = uBetsService.reBet(json, uEntity, id, uEntity.getId(), ip);
                if(result) {
                    json.set(0, "0-5");
                }
            }
			return json.toJson();
		} catch (Exception e) {
			log.error("普通投注发生异常", e);
			WebJSON json = new WebJSON(dataFactory);
			json.set(2, "1-1");
			return json.toJson();
		}
	}

	@RequestMapping(value = WUC.USER_BETS_CHASE, method = { RequestMethod.POST })
	@ResponseBody
	public Map<String, Object> USER_BETS_CHASE(HttpSession session, HttpServletRequest request) {
		try {
			WebJSON json = new WebJSON(dataFactory);
			SessionUser sessionUser = super.getSessionUser(json, session, request);
			if (sessionUser == null) {
                return json.toJson();
            }

			String ip = HttpUtil.getRealIp(json, request);
			if (ip == null) {
				return json.toJson();
			}

			User uEntity = uService.getById(sessionUser.getId());
			if (uEntity == null) {
                super.logOut(session, request);
                json.set(2, "2-10"); // 您的账户信息发生变更，请重新登录
                return json.toJson();
            }

			int lotteryId = HttpUtil.getIntParameter(request, "lotteryId");
			String blist = HttpUtil.getStringParameterTrim(request, "blist");
			String clist = HttpUtil.getStringParameterTrim(request, "clist");
			String isStop = HttpUtil.getStringParameterTrim(request, "isStop");
			JSONArray bjson = JSONArray.fromObject(blist);
			JSONArray cjson = JSONArray.fromObject(clist);
			boolean isUserAllow = uBetsValidate.isUserAllow(json, uEntity, session);
			if(isUserAllow) {
                boolean result = uBetsService.chase(json, uEntity, lotteryId, bjson, cjson, isStop, ip);
                if(result) {
                    json.set(0, "0-5");
                }
            }
			return json.toJson();
		} catch (Exception e) {
			log.error("追号投注发生异常", e);
			WebJSON json = new WebJSON(dataFactory);
			json.set(2, "1-1");
			return json.toJson();
		}
	}
	
	@RequestMapping(value = WUC.USER_BETS_CANCEL, method = { RequestMethod.POST })
	@ResponseBody
	public Map<String, Object> USER_BETS_CANCEL(HttpSession session, HttpServletRequest request) {
		try {
			WebJSON json = new WebJSON(dataFactory);
			SessionUser sessionUser = super.getSessionUser(json, session, request);
			if (sessionUser == null) {
                return json.toJson();
            }

			User uEntity = uService.getById(sessionUser.getId());
			if (uEntity == null) {
                super.logOut(session, request);
                json.set(2, "2-10"); // 您的账户信息发生变更，请重新登录
                return json.toJson();
            }

			String type = HttpUtil.getStringParameterTrim(request, "type");
			boolean result = false;
			// 撤销普通订单
			if("general".equals(type)) {
                int id = HttpUtil.getIntParameter(request, "id");
                result = uBetsService.cancelGeneral(id, uEntity.getId(), uEntity);
            }
			// 撤销追号订单
			if("chase".equals(type)) {
                String chaseBillno = HttpUtil.getStringParameterTrim(request, "chaseBillno");
                result = uBetsService.cancelChase(chaseBillno, uEntity.getId(), uEntity);
            }
			if(result) {
                json.set(0, "0-1");
            } else {
                json.set(1, "1-1");
            }
			return json.toJson();
		} catch (Exception e) {
			log.error("撤销投注发生异常", e);
			WebJSON json = new WebJSON(dataFactory);
			json.set(2, "1-1");
			return json.toJson();
		}
	}
	//
	// @RequestMapping(value = WUC.USER_BETS_PLAN_PUBLISH, method = { RequestMethod.POST })
	// @ResponseBody
	// public Map<String, Object> USER_BETS_PLAN_PUBLISH(HttpSession session, HttpServletRequest request) {
	// 	WebJSON json = new WebJSON(dataFactory);
	// 	User uEntity = getCurrUser(session, request);
	// 	if(uEntity != null) {
	// 		int orderId = HttpUtil.getIntParameter(request, "orderId");
	// 		int tid = HttpUtil.getIntParameter(request, "tid");
	// 		// 检查标题
	// 		int rate = HttpUtil.getIntParameter(request, "rate");
	// 		String billno = OrderUtil.createString(8);
	// 		boolean result = uBetsPlanService.publish(json, billno, orderId, uEntity, tid, rate);
	// 		if(result) {
	// 			json.data("billno", billno);
	// 			json.set(0, "0-1");
	// 		}
	// 	} else {
	// 		String message = (String) request.getAttribute("message");
	// 		message = StringUtil.isNotNull(message) ? message : "2-1006";
	// 		json.set(2, message);
	// 	}
	// 	return json.toJson();
	// }
	//
	// @RequestMapping(value = WUC.USER_BETS_PLAN_HALL_SEARCH, method = { RequestMethod.POST })
	// @ResponseBody
	// public Map<String, Object> USER_BETS_PLAN_HALL_SEARCH(HttpSession session, HttpServletRequest request) {
	// 	WebJSON json = new WebJSON(dataFactory);
	// 	User uEntity = getCurrUser(session, request);
	// 	if(uEntity != null) {
	// 		String billno = request.getParameter("billno");
	// 		int lotteryId = HttpUtil.getIntParameter(request, "lotteryId");
	// 		String method = request.getParameter("method");
	// 		int start = HttpUtil.getIntParameter(request, "start");
	// 		int limit = HttpUtil.getIntParameter(request, "limit");
	//
	// 		// 获取当前期号
	// 		OpenTime oTime = lOpenUtil.getCurrOpenTime(lotteryId);
	// 		String expect = oTime.getExpect();
	//
	// 		PageList pList = uBetsPlanService.search(billno, lotteryId, expect, method, start, limit);
	// 		json.data("totalCount", pList.getCount());
	// 		json.data("data", pList.getList());
	// 		json.set(0, "0-1");
	// 	} else {
	// 		String message = (String) request.getAttribute("message");
	// 		message = StringUtil.isNotNull(message) ? message : "2-1006";
	// 		json.set(2, message);
	// 	}
	// 	return json.toJson();
	// }
	//
	// @RequestMapping(value = WUC.USER_BETS_PLAN_FOLLOW, method = { RequestMethod.POST })
	// @ResponseBody
	// public Map<String, Object> USER_BETS_PLAN_FOLLOW(HttpSession session, HttpServletRequest request) {
	// 	WebJSON json = new WebJSON(dataFactory);
	// 	User uEntity = getCurrUser(session, request);
	// 	if(uEntity != null) {
	// 		int id = HttpUtil.getIntParameter(request, "id");
	// 		int multiple = HttpUtil.getIntParameter(request, "multiple");
	// 		boolean result = uBetsService.follow(json, uEntity, id, multiple);
	// 		if(result) {
	// 			json.set(0, "0-5");
	// 		}
	// 	} else {
	// 		String message = (String) request.getAttribute("message");
	// 		message = StringUtil.isNotNull(message) ? message : "2-1006";
	// 		json.set(2, message);
	// 	}
	// 	return json.toJson();
	// }
	
	// @RequestMapping(value = WUC.USER_BETS_PLAN_PREPARE, method = { RequestMethod.POST })
	// @ResponseBody
	// public Map<String, Object> USER_BETS_PLAN_PREPARE(HttpSession session, HttpServletRequest request) {
	// 	WebJSON json = new WebJSON(dataFactory);
	// 	User uEntity = getCurrUser(session, request);
	// 	if(uEntity != null) {
	// 		int id = HttpUtil.getIntParameter(request, "id");
	// 		UserBetsVO bean = uBetsService.get(id, uEntity.getId());
	// 		if(bean != null) {
	// 			PlanConfig planConfig = dataFactory.getPlanConfig();
	// 			LotteryConfig lotteryConfig = dataFactory.getLotteryConfig();
	// 			int lotteryId = bean.getBean().getLotteryId();
	// 			String method = bean.getBean().getMethod();
	// 			String model = bean.getBean().getModel();
	// 			int multiple = bean.getBean().getMultiple();
	// 			String billno = bean.getBean().getBillno();
	// 			String expect = bean.getBean().getExpect();
	// 			List<Integer> rateList = planConfig.getRate();
	// 			List<String> titleList = planConfig.getTitle();
	// 			double money = bean.getBean().getMoney();
	//
	// 			// 计算盈利金额
	// 			int bUnitMoney = lotteryConfig.getbUnitMoney();
	// 			// 彩票
	// 			Lottery lottery = dataFactory.getLottery(lotteryId);
	// 			LotteryPlayRules rule = dataFactory.getLotteryPlayRules(lottery.getType(), method);
	// 			double maxUnitPrize = PrizeUtils.getMoney(rule, model, bUnitMoney, uEntity.getCode());
	// 			double maxPrize = maxUnitPrize * multiple;
	//
	// 			UserPlanInfo uPlanInfo = uPlanInfoService.get(uEntity.getId());
	// 			Integer maxRate = rateList.get(uPlanInfo.getLevel());
	//
	// 			UserBetsPlanPrepareVO pBean = new UserBetsPlanPrepareVO(id, billno, bean.getLottery(), expect, money, maxPrize, maxRate, titleList);
	//
	// 			json.data("data", pBean);
	// 			json.set(0, "0-1");
	// 		} else {
	// 			json.set(2, "2-1028");
	// 		}
	// 	} else {
	// 		String message = (String) request.getAttribute("message");
	// 		message = StringUtil.isNotNull(message) ? message : "2-1006";
	// 		json.set(2, message);
	// 	}
	// 	return json.toJson();
	// }
	
}