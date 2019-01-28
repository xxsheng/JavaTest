// package lottery.web.content;
//
// import java.util.HashMap;
// import java.util.Map;
//
// import javautils.StringUtil;
// import javautils.date.Moment;
// import javautils.http.HttpUtil;
//
// import javax.servlet.http.HttpServletRequest;
// import javax.servlet.http.HttpSession;
//
// import lottery.domains.content.biz.VipBirthdayGiftsService;
// import lottery.domains.content.biz.VipFreeChipsService;
// import lottery.domains.content.biz.VipIntegralExchangeService;
// import lottery.domains.content.biz.VipUpgradeGiftsService;
// import lottery.domains.content.dao.VipBirthdayGiftsDao;
// import lottery.domains.content.dao.VipFreeChipsDao;
// import lottery.domains.content.dao.VipIntegralExchangeDao;
// import lottery.domains.content.dao.VipUpgradeGiftsDao;
// import lottery.domains.content.entity.User;
// import lottery.domains.content.entity.VipBirthdayGifts;
// import lottery.domains.content.entity.VipFreeChips;
// import lottery.domains.content.entity.VipUpgradeGifts;
// import lottery.domains.content.vo.config.VipConfig;
// import lottery.domains.pool.DataFactory;
// import lottery.web.WUC;
// import lottery.web.WebJSON;
// import lottery.web.helper.AbstractActionController;
//
// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.stereotype.Controller;
// import org.springframework.web.bind.annotation.RequestMapping;
// import org.springframework.web.bind.annotation.RequestMethod;
// import org.springframework.web.bind.annotation.ResponseBody;
//
// @Controller
// public class VipController extends AbstractActionController {
//
// 	/**
// 	 * DAO
// 	 */
// 	@Autowired
// 	private VipIntegralExchangeDao vIntegralExchangeDao;
//
// 	@Autowired
// 	private VipUpgradeGiftsDao vUpgradeGiftsDao;
//
// 	@Autowired
// 	private VipBirthdayGiftsDao vBirthdayGiftsDao;
//
// 	@Autowired
// 	private VipFreeChipsDao vFreeChipsDao;
//
// 	/**
// 	 * SERVICE
// 	 */
// 	@Autowired
// 	private VipIntegralExchangeService vIntegralExchangeService;
//
// 	@Autowired
// 	private VipUpgradeGiftsService vUpgradeGiftsService;
//
// 	@Autowired
// 	private VipBirthdayGiftsService vBirthdayGiftsService;
//
// 	@Autowired
// 	private VipFreeChipsService vFreeChipsService;
//
// 	/**
// 	 * DataFactory
// 	 */
// 	@Autowired
// 	private DataFactory dataFactory;
//
// 	@RequestMapping(value = WUC.VIP_INTEGRAL_EXCHANGE_LOAD, method = { RequestMethod.POST })
// 	@ResponseBody
// 	public Map<String, Object> VIP_INTEGRAL_EXCHANGE_LOAD(HttpSession session, HttpServletRequest request) {
// 		WebJSON json = new WebJSON(dataFactory);
// 		User uEntity = super.getCurrUser(session, request);
// 		if(uEntity != null) {
// 			double lotteryMoney = uEntity.getLotteryMoney();
// 			double integral = uEntity.getIntegral();
//
// 			VipConfig vipConfig = dataFactory.getVipConfig();
// 			int exchangeRate = vipConfig.getExchangeRate();
// 			int maxExchangeMultiple = vipConfig.getMaxExchangeMultiple();
// 			int maxExchangeTimes = vipConfig.getMaxExchangeTimes();
//
// 			String date = new Moment().toSimpleDate();
// 			int todayUsedCount = vIntegralExchangeDao.getDateCount(uEntity.getId(), date);
//
// 			Map<String, Object> data = new HashMap<>();
// 			data.put("lotteryMoney", lotteryMoney);
// 			data.put("integral", integral);
// 			data.put("exchangeRate", exchangeRate);
// 			data.put("maxExchangeMultiple", maxExchangeMultiple);
// 			data.put("maxExchangeTimes", maxExchangeTimes);
// 			data.put("todayUsedCount", todayUsedCount);
// 			json.data("data", data);
// 			json.set(0, "0-1");
// 		} else {
// 			String message = (String) request.getAttribute("message");
// 			message = StringUtil.isNotNull(message) ? message : "2-1006";
// 			json.set(2, message);
// 		}
// 		return json.toJson();
// 	}
//
// 	@RequestMapping(value = WUC.VIP_INTEGRAL_EXCHANGE_DO, method = { RequestMethod.POST })
// 	@ResponseBody
// 	public Map<String, Object> VIP_INTEGRAL_EXCHANGE_DO(HttpSession session, HttpServletRequest request) {
// 		WebJSON json = new WebJSON(dataFactory);
// 		User uEntity = super.getCurrUser(session, request);
// 		if(uEntity != null) {
// 			int integral = HttpUtil.getIntParameter(request, "integral");
// 			boolean result = vIntegralExchangeService.doExchange(json, uEntity.getId(), integral);
// 			if(result) {
// 				json.set(0, "0-1");
// 			} else {
// 				if(json.getError() == 0) {
// 					json.set(1, "1-1");
// 				}
// 			}
// 		} else {
// 			String message = (String) request.getAttribute("message");
// 			message = StringUtil.isNotNull(message) ? message : "2-1006";
// 			json.set(2, message);
// 		}
// 		return json.toJson();
// 	}
//
// 	@RequestMapping(value = WUC.VIP_UPGRADE_GIFTS_GET, method = { RequestMethod.POST })
// 	@ResponseBody
// 	public Map<String, Object> VIP_UPGRADE_GIFTS_GET(HttpSession session, HttpServletRequest request) {
// 		WebJSON json = new WebJSON(dataFactory);
// 		User uEntity = super.getCurrUser(session, request);
// 		if(uEntity != null) {
// 			VipUpgradeGifts result = vUpgradeGiftsDao.getByUserId(uEntity.getId());
// 			json.data("data", result);
// 			json.set(0, "0-1");
// 		} else {
// 			String message = (String) request.getAttribute("message");
// 			message = StringUtil.isNotNull(message) ? message : "2-1006";
// 			json.set(2, message);
// 		}
// 		return json.toJson();
// 	}
//
// 	@RequestMapping(value = WUC.VIP_UPGRADE_GIFTS_RECEIVE, method = { RequestMethod.POST })
// 	@ResponseBody
// 	public Map<String, Object> VIP_UPGRADE_GIFTS_RECEIVE(HttpSession session, HttpServletRequest request) {
// 		WebJSON json = new WebJSON(dataFactory);
// 		User uEntity = super.getCurrUser(session, request);
// 		if(uEntity != null) {
// 			boolean result = vUpgradeGiftsService.received(uEntity.getId());
// 			if(result) {
// 				json.set(0, "0-1");
// 			} else {
// 				json.set(1, "0-1");
// 			}
// 		} else {
// 			String message = (String) request.getAttribute("message");
// 			message = StringUtil.isNotNull(message) ? message : "2-1006";
// 			json.set(2, message);
// 		}
// 		return json.toJson();
// 	}
//
// 	@RequestMapping(value = WUC.VIP_BIRTHDAY_GIFTS_GET, method = { RequestMethod.POST })
// 	@ResponseBody
// 	public Map<String, Object> VIP_BIRTHDAY_GIFTS_GET(HttpSession session, HttpServletRequest request) {
// 		WebJSON json = new WebJSON(dataFactory);
// 		User uEntity = super.getCurrUser(session, request);
// 		if(uEntity != null) {
// 			VipBirthdayGifts result = vBirthdayGiftsDao.getByUserId(uEntity.getId());
// 			json.data("data", result);
// 			json.set(0, "0-1");
// 		} else {
// 			String message = (String) request.getAttribute("message");
// 			message = StringUtil.isNotNull(message) ? message : "2-1006";
// 			json.set(2, message);
// 		}
// 		return json.toJson();
// 	}
//
// 	@RequestMapping(value = WUC.VIP_BIRTHDAY_GIFTS_RECEIVE, method = { RequestMethod.POST })
// 	@ResponseBody
// 	public Map<String, Object> VIP_BIRTHDAY_GIFTS_RECEIVE(HttpSession session, HttpServletRequest request) {
// 		WebJSON json = new WebJSON(dataFactory);
// 		User uEntity = super.getCurrUser(session, request);
// 		if(uEntity != null) {
// 			boolean result = vBirthdayGiftsService.received(uEntity.getId());
// 			if(result) {
// 				json.set(0, "0-1");
// 			} else {
// 				json.set(1, "0-1");
// 			}
// 		} else {
// 			String message = (String) request.getAttribute("message");
// 			message = StringUtil.isNotNull(message) ? message : "2-1006";
// 			json.set(2, message);
// 		}
// 		return json.toJson();
// 	}
//
// 	@RequestMapping(value = WUC.VIP_FREE_CHIPS_GET, method = { RequestMethod.POST })
// 	@ResponseBody
// 	public Map<String, Object> VIP_FREE_CHIPS_GET(HttpSession session, HttpServletRequest request) {
// 		WebJSON json = new WebJSON(dataFactory);
// 		User uEntity = super.getCurrUser(session, request);
// 		if(uEntity != null) {
// 			VipFreeChips result = vFreeChipsDao.getByUserId(uEntity.getId());
// 			json.data("data", result);
// 			json.set(0, "0-1");
// 		} else {
// 			String message = (String) request.getAttribute("message");
// 			message = StringUtil.isNotNull(message) ? message : "2-1006";
// 			json.set(2, message);
// 		}
// 		return json.toJson();
// 	}
//
// 	@RequestMapping(value = WUC.VIP_FREE_CHIPS_RECEIVE, method = { RequestMethod.POST })
// 	@ResponseBody
// 	public Map<String, Object> VIP_FREE_CHIPS_RECEIVE(HttpSession session, HttpServletRequest request) {
// 		WebJSON json = new WebJSON(dataFactory);
// 		User uEntity = super.getCurrUser(session, request);
// 		if(uEntity != null) {
// 			boolean result = vFreeChipsService.received(uEntity.getId());
// 			if(result) {
// 				json.set(0, "0-1");
// 			} else {
// 				json.set(1, "0-1");
// 			}
// 		} else {
// 			String message = (String) request.getAttribute("message");
// 			message = StringUtil.isNotNull(message) ? message : "2-1006";
// 			json.set(2, message);
// 		}
// 		return json.toJson();
// 	}
//
// }