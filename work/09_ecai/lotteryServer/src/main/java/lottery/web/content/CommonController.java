package lottery.web.content;

import javautils.StringUtil;
import javautils.date.Moment;
import javautils.http.HttpUtil;
import lottery.domains.content.entity.Lottery;
import lottery.domains.content.entity.PaymentBank;
import lottery.domains.content.entity.UserBetsHitRanking;
import lottery.domains.content.vo.lottery.LotteryOpenCodeVO;
import lottery.domains.content.vo.lottery.LotteryVO;
import lottery.domains.pool.DataFactory;
import lottery.domains.utils.open.LotteryOpenUtil;
import lottery.domains.utils.open.OpenTime;
import lottery.web.WUC;
import lottery.web.WebJSON;
import lottery.web.helper.AbstractActionController;
import lottery.web.helper.session.SessionUser;
import lottery.web.websocket.WebSocketInfo;
import lottery.web.websocket.WebSocketSessionUserHolder;
import net.sf.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.*;

@Controller
public class CommonController extends AbstractActionController {
	private static final Logger log = LoggerFactory.getLogger(CommonController.class);

	/**
	 * Service
	 */

	/**
	 * Validate
	 */

	/**
	 * Util
	 */
	@Autowired
	private LotteryOpenUtil lotteryOpenUtil;

	/**
	 * DataFactory
	 */
	@Autowired
	private DataFactory dataFactory;
	
	@RequestMapping(value = WUC.LOTTERY_GET, method = { RequestMethod.POST })
	@ResponseBody
	public JSONObject LOTTERY_GET(HttpSession session,HttpServletRequest request) {
		JSONObject json = new JSONObject();
		WebJSON webJSON = new WebJSON(dataFactory);
		SessionUser sessionUser = super.getSessionUser(webJSON, session, request);
		if (sessionUser == null) {
			json.accumulate("data", "");
			return json;
		}
		
		String name = HttpUtil.getStringParameterTrim(request, "name");
		Integer id = HttpUtil.getIntParameter(request, "id");
		if(StringUtil.isNotNull(name)) {
			Lottery lottery = dataFactory.getLottery(name);
			json.accumulate("data", lottery == null ? null : new LotteryVO(lottery));
		} else if(id != null) {
			Lottery lottery = dataFactory.getLottery(id.intValue());
			json.accumulate("data", lottery == null ? null : new LotteryVO(lottery));
		} else {
			List<Lottery> list = dataFactory.listLottery();
			List<LotteryVO> voList = new LinkedList<>();
			for (Lottery lottery : list) {
				voList.add(new LotteryVO(lottery));
			}
			json.accumulate("data", voList);
		}
		return json;
	}

	@RequestMapping(value = WUC.LOTTERY_OPEN_TIME_CODE, method = { RequestMethod.POST })
	@ResponseBody
	public JSONObject LOTTERY_OPEN_TIME_CODE(HttpSession session,HttpServletRequest request) {
		try {
			JSONObject json = new JSONObject();
			WebJSON webJSON = new WebJSON(dataFactory);
			SessionUser sessionUser = super.getSessionUser(webJSON, session, request);
			if (sessionUser == null) {
                json.accumulate("opentime", "");
                json.accumulate("sTime", "");
                json.accumulate("openCode", "");
                return json;
            }

			int lotteryId = HttpUtil.getIntParameter(request, "lotteryId");
			OpenTime opentime = lotteryOpenUtil.getCurrOpenTime(lotteryId);
			List<LotteryOpenCodeVO> openCodeVOs = dataFactory.listLotteryOpenCode(lotteryId, 1, null);
			String sTime = new Moment().toSimpleTime();
			json.accumulate("opentime", opentime);
			json.accumulate("sTime", sTime);
			json.accumulate("openCode", openCodeVOs == null ? null : openCodeVOs.get(0));
			return json;
		} catch (Exception e) {
			e.printStackTrace();
			JSONObject json = new JSONObject();
			json.accumulate("opentime", "");
			json.accumulate("sTime", "");
			json.accumulate("openCode", "");
			return json;
		}
	}

	@RequestMapping(value = WUC.LOTTERY_OPEN_TIME, method = { RequestMethod.POST })
	@ResponseBody
	public JSONObject LOTTERY_OPEN_TIME(HttpSession session,HttpServletRequest request) {
		JSONObject json = new JSONObject();
		WebJSON webJSON = new WebJSON(dataFactory);
		SessionUser sessionUser = super.getSessionUser(webJSON, session, request);
		if (sessionUser == null) {
			json.accumulate("opentime", "");
			json.accumulate("sTime", "");
			return json;
		}

		int lotteryId = HttpUtil.getIntParameter(request, "lotteryId");
		OpenTime opentime = lotteryOpenUtil.getCurrOpenTime(lotteryId);
		String sTime = new Moment().toSimpleTime();
		json.accumulate("opentime", opentime);
		json.accumulate("sTime", sTime);
		return json;
	}

	@RequestMapping(value = WUC.LOTTERY_LAST_OPEN_TIME, method = { RequestMethod.POST })
	@ResponseBody
	public JSONObject LOTTERY_LAST_OPEN_TIME(HttpSession session,HttpServletRequest request) {
		JSONObject json = new JSONObject();
		WebJSON webJSON = new WebJSON(dataFactory);
		SessionUser sessionUser = super.getSessionUser(webJSON, session, request);
		if (sessionUser == null) {
			json.accumulate("opentime", "");
			return json;
		}

		int lotteryId = HttpUtil.getIntParameter(request, "lotteryId");
		OpenTime opentime = lotteryOpenUtil.getLastOpenTime(lotteryId);
		json.accumulate("opentime", opentime);
		return json;
	}

	@RequestMapping(value = WUC.LOTTERY_LAST_EXPECT, method = { RequestMethod.POST })
	@ResponseBody
	public JSONObject LOTTERY_LAST_EXPECT(HttpSession session,HttpServletRequest request) {
		JSONObject json = new JSONObject();
		WebJSON webJSON = new WebJSON(dataFactory);
		SessionUser sessionUser = super.getSessionUser(webJSON, session, request);
		if (sessionUser == null) {
			json.accumulate("expect", null);
			return json;
		}

		String expect = null;
		int lotteryId = HttpUtil.getIntParameter(request, "lotteryId");
		OpenTime currOpenTime = lotteryOpenUtil.getCurrOpenTime(lotteryId);
		if (currOpenTime != null) {
			expect = lotteryOpenUtil.subtractExpect(lotteryId, currOpenTime.getExpect());
		}

		json.accumulate("expect", expect);
		return json;
	}

	@RequestMapping(value = WUC.LOTTERY_CHASE_TIME, method = { RequestMethod.POST })
	@ResponseBody
	public JSONObject LOTTERY_CHASE_TIME(HttpSession session,HttpServletRequest request) {
		JSONObject json = new JSONObject();

		WebJSON webJSON = new WebJSON(dataFactory);
		SessionUser sessionUser = super.getSessionUser(webJSON, session, request);
		if (sessionUser == null) {
			json.accumulate("data","");
			return json;
		}

		int lotteryId = HttpUtil.getIntParameter(request, "lotteryId");
		int count = HttpUtil.getIntParameter(request, "count");
		Lottery lottery = dataFactory.getLottery(lotteryId);

		if(lottery != null) {
			if(lottery.getType() == 4) {
				List<OpenTime> list = lotteryOpenUtil.getOpenTimeList(lotteryId, count);
				json.accumulate("data", list);
			}
			else {

				if (lottery.getSelf() == 1 || lottery.getId() == 122) {
					List<OpenTime> list = lotteryOpenUtil.getOpenTimeList(lotteryId, 100);
					json.accumulate("data", list);
				}
				else {
					String date = new Moment().toSimpleDate();
					List<OpenTime> dList = lotteryOpenUtil.getOpenDateList(lotteryId, date);
					OpenTime cTime = lotteryOpenUtil.getCurrOpenTime(lotteryId);
					List<OpenTime> list = new ArrayList<>();
					for (OpenTime oTime : dList) {
						if(oTime.getExpect().compareTo(cTime.getExpect()) >= 0) {
							list.add(oTime);
						}
					}
					json.accumulate("data", list);
				}


			}
		}
		return json;
	}
	
	@RequestMapping(value = WUC.LOTTERY_OPENCODE, method = { RequestMethod.POST })
	@ResponseBody
	public List<LotteryOpenCodeVO> LOTTERY_OPENCODE(HttpSession session,HttpServletRequest request) {
		WebJSON webJSON = new WebJSON(dataFactory);
		SessionUser sessionUser = super.getSessionUser(webJSON, session, request);
		if(sessionUser == null) {
			return new ArrayList<>();
		}

		int lotteryId = HttpUtil.getIntParameter(request, "lotteryId");
		Integer count = HttpUtil.getIntParameter(request, "count");

		if (count == null) {
			count = 5;
		}
		else {
			if (count >= 50) {
				count = 50;
			}
		}

		List<LotteryOpenCodeVO> openCodeVOs = dataFactory.listLotteryOpenCode(lotteryId, count, sessionUser.getId());
		if (openCodeVOs == null) {
			return new ArrayList<>();
		}
		return openCodeVOs;
	}

	@RequestMapping(value = WUC.LOTTERY_CODE_TREND, method = { RequestMethod.POST })
	@ResponseBody
	public JSONObject LOTTERY_CODE_TREND(HttpSession session,HttpServletRequest request) {
		JSONObject json = new JSONObject();
		WebJSON webJSON = new WebJSON(dataFactory);
		SessionUser sessionUser = super.getSessionUser(webJSON, session, request);
		if(sessionUser == null) {
			json.accumulate("lottery", "");
			json.accumulate("list", "");
			return json;
		}
		
		int lotteryId = HttpUtil.getIntParameter(request, "lotteryId");
		String command = HttpUtil.getStringParameterTrim(request, "command");
		Lottery lottery = dataFactory.getLottery(lotteryId);
		Integer userId = lotteryId == 117 ? sessionUser.getId() : null;
		if(lottery != null) {
			json.accumulate("lottery", lottery == null ? null : new LotteryVO(lottery));
			if("latest-30".equals(command)) {
				List<LotteryOpenCodeVO> list = dataFactory.listLotteryOpenCode(lotteryId, 30, userId);
				if (list == null) {
					list = new ArrayList<>();
				}
				json.accumulate("list", list);
			}
			else if("latest-50".equals(command)) {
				List<LotteryOpenCodeVO> list = dataFactory.listLotteryOpenCode(lotteryId, 50, userId);
				if (list == null) {
					list = new ArrayList<>();
				}
				json.accumulate("list", list);
			}
			else if("latest-30-desc".equals(command)) {
				List<LotteryOpenCodeVO> list = dataFactory.listLotteryOpenCode(lotteryId, 30, userId);
				if (list == null) {
					list = new ArrayList<>();
				}
				else {
					Collections.reverse(list);
				}
				json.accumulate("list", list);
			}
			else if("latest-50-desc".equals(command)) {
				List<LotteryOpenCodeVO> list = dataFactory.listLotteryOpenCode(lotteryId, 50, userId);
				if (list == null) {
					list = new ArrayList<>();
				}
				else {
					Collections.reverse(list);
				}
				json.accumulate("list", list);
			}
		}
		return json;
	}

//	@RequestMapping(value = WUC.LOTTERY_PLAY_RULES, method = { RequestMethod.POST })
//	@ResponseBody
//	public Map<String, List<LotteryPlayRulesGroupVO> LOTTERY_PLAY_RULES(HttpSession session, HttpServletRequest request) {
//		WebJSON webJSON = new WebJSON(dataFactory);
//		SessionUser sessionUser = super.getSessionUser(webJSON, session, request);
//		if(sessionUser == null) {
//			List<LotteryPlayRulesGroupVO> list = new ArrayList<>();
//			return list;
//		}
//
//		int lotteryId = HttpUtil.getIntParameter(request, "lotteryId");
//		return dataFactory.listLotteryPlayRulesVOs(lotteryId);
//	}

	@RequestMapping(value = WUC.USER_BETS_HIT_RANKING, method = { RequestMethod.POST })
	@ResponseBody
	public List<UserBetsHitRanking> LOTTERY_HIT_RANKING(HttpSession session, HttpServletRequest request) {
		WebJSON webJSON = new WebJSON(dataFactory);
		SessionUser sessionUser = super.getSessionUser(webJSON, session, request);
		if(sessionUser == null) {
			List<UserBetsHitRanking> list = new ArrayList<>();
			return list;
		}
		
		return dataFactory.listUserBetsHitRanking();
	}

	@RequestMapping(value = WUC.SERVICE_KEFU, method = { RequestMethod.POST })
	@ResponseBody
	public String SERVICE_KEFU(HttpSession session,HttpServletRequest request) {
		WebJSON webJSON = new WebJSON(dataFactory);
		SessionUser sessionUser = super.getSessionUser(webJSON, session, request);
		if(sessionUser == null) {
			return "";
		}
		
		return dataFactory.getServiceConfig().getUrl();
	}
	
	@RequestMapping(value = WUC.PAYMENT_BANK_LIST, method = { RequestMethod.POST })
	@ResponseBody
	public List<PaymentBank> PAYMENT_BANK_LIST(HttpSession session,HttpServletRequest request) {
		WebJSON webJSON = new WebJSON(dataFactory);
		SessionUser sessionUser = super.getSessionUser(webJSON, session, request);
		if(sessionUser == null) {
			 List<PaymentBank> list = new ArrayList<PaymentBank>();
			return list;
		}
		
		return dataFactory.listPaymentBank();
	}

	/**
	 * 在使用web socket之前，使用该接口获取当前用户的token
	 */
	@RequestMapping(value = WUC.WEB_SOCKET_TOKEN, method = { RequestMethod.POST })
	@ResponseBody
	public JSONObject WEB_SOCKET_TOKEN(HttpSession session, HttpServletRequest request) {
		JSONObject json = new JSONObject();
		try {
			WebJSON webJSON = new WebJSON(dataFactory);
			SessionUser sessionUser = super.getSessionUser(webJSON, session, request);
			if (sessionUser == null) {
                json.accumulate("token", null);
                return json;
            }

			WebSocketInfo info = getWebSocketToken(session, request);

			if (info == null) {
                info = WebSocketSessionUserHolder.generateToken(request, sessionUser);
                setWebSocketToken(session, info);
            }

			json.accumulate("token", info.getToken());
			return json;
		} catch (Exception e) {
			log.error("获取Web Socket Token时出错", e);
		}

		return json;
	}

	/**
	 * 获取session关联的一次性token，每请求一次，覆盖上次的
	 */
	@RequestMapping(value = WUC.DISPOSABLE_TOKEN, method = { RequestMethod.POST })
	@ResponseBody
	public Map<String, Object> DISPOSABLE_TOKEN(HttpSession session, HttpServletRequest request) {
		String tokenStr = super.generateDisposableToken(session, request);
		WebJSON json = new WebJSON(dataFactory);
		json.data("token", tokenStr);
		json.set(0, "0-1");
		return json.toJson();
	}


}