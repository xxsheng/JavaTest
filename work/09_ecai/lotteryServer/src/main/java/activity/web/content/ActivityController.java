package activity.web.content;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import activity.domains.content.biz.ActivityRechargeLoopService;
import activity.domains.content.biz.ActivitySignService;
import activity.domains.content.entity.ActivityRebate;
import lottery.domains.content.biz.ActivityRebateService;
import lottery.domains.content.biz.ActivityRebateWheelService;
import lottery.domains.content.biz.ActivityRedPacketRainBillService;
import lottery.domains.content.biz.UserLotteryReportService;
import lottery.domains.content.biz.read.UserLotteryReportReadService;
import lottery.domains.content.global.Global;
import lottery.domains.pool.DataFactory;
import lottery.web.WUC;
import lottery.web.WebJSON;
import lottery.web.helper.AbstractActionController;
import lottery.web.helper.session.SessionUser;

/**
 * 活动控制服务类
* <p>Title: ActivityController</p>  
* <p>Description: </p>  
* @author James  
* @date 2018年2月3日
 */
@Controller
public class ActivityController extends AbstractActionController {


	/* @Autowired
	 private ActivitySalaryService aSalaryService;
*/
	 @Autowired
	 private ActivityRechargeLoopService aRechargeLoopService;

	@Autowired
	private ActivitySignService aSignService;

	@Autowired
	private ActivityRebateWheelService wheelService;

	@Autowired
	private UserLotteryReportService lotteryReportService;

	//
	// @Autowired
	// private ActivityPacketService packetService;

	// @Autowired
	// private UserSecurityService uSecurityService;

	// @Autowired
	// private ActivityCostService activityCostService;

	//返点活动服务类
	@Autowired
	private ActivityRebateService  activityRebateService;
	
	

	@Autowired
	private ActivityRedPacketRainBillService rainBillService;

	@Autowired
	private UserLotteryReportReadService uLotteryReportReadService;


	/**
	 * DataFactory
	 */
	@Autowired
	private DataFactory dataFactory;
//
// 	@RequestMapping(value = WUC.ACTIVITY_SALARY_ZS_LOAD, method = { RequestMethod.POST })
// 	@ResponseBody
// 	public Map<String, Object> ACTIVITY_SALARY_ZS_LOAD(HttpSession session, HttpServletRequest request) {
// 		WebJSON json = new WebJSON(dataFactory);
// 		SessionUser sessionUser = super.getSessionUser(json, session, request);
// 		if (sessionUser == null) {
// 			return json.toJson();
// 		}
//
// 		User uEntity = uService.getById(sessionUser.getId());
// 		if (uEntity == null) {
// 			super.logOut(session, request);
// 			json.set(2, "2-10"); // 您的账户信息发生变更，请重新登录
// 			return json.toJson();
// 		}
//
// 		if(uEntity.getType() == Global.USER_TYPE_PROXY) {
// //				System.out.println(uCodePointUtil.isZhiShu(uEntity));
// 			if(uCodePointUtil.isZhiShu(uEntity)) {
// 				int type = 1;
// 				ActivitySalaryVO bean = aSalaryService.get(uEntity.getId(), type);
// 				if(bean != null) {
// 					json.data("data", bean);
// 					json.set(0, "0-1");
// 				} else {
// 					json.set(2, "2-4001");
// 				}
// 			} else {
// 				json.set(2, "2-4003");
// 			}
// 		}
// 		return json.toJson();
// 	}
//
// 	@RequestMapping(value = WUC.ACTIVITY_SALARY_ZS_RECEIVE, method = { RequestMethod.POST })
// 	@ResponseBody
// 	public synchronized Map<String, Object> ACTIVITY_SALARY_ZS_RECEIVE(HttpSession session, HttpServletRequest request) {
// 		WebJSON json = new WebJSON(dataFactory);
// 		SessionUser sessionUser = super.getSessionUser(json, session, request);
// 		if (sessionUser == null) {
// 			return json.toJson();
// 		}
//
// 		User uEntity = uService.getById(sessionUser.getId());
// 		if (uEntity == null) {
// 			super.logOut(session, request);
// 			json.set(2, "2-10"); // 您的账户信息发生变更，请重新登录
// 			return json.toJson();
// 		}
//
// 		if(uEntity.getType() == Global.USER_TYPE_PROXY) {
// 			System.out.println(uCodePointUtil.isZhiShu(uEntity));
// 			if(uCodePointUtil.isZhiShu(uEntity)) {
// 				int type = 1;
// 				boolean flag = aSalaryService.receive(json, uEntity.getId(), type);
// 				if(flag) {
// 					json.set(0, "0-1");
// 				} else {
// 					if(!StringUtil.isNotNull(json.getMessage())) {
// 						json.set(1, "1-1");
// 					}
// 				}
// 			} else {
// 				json.set(2, "2-4003");
// 			}
// 		}
// 		return json.toJson();
// 	}
//
// 	@RequestMapping(value = WUC.ACTIVITY_SALARY_ZD_LOAD, method = { RequestMethod.POST })
// 	@ResponseBody
// 	public Map<String, Object> ACTIVITY_SALARY_ZD_LOAD(HttpSession session, HttpServletRequest request) {
// 		WebJSON json = new WebJSON(dataFactory);
// 		SessionUser sessionUser = super.getSessionUser(json, session, request);
// 		if (sessionUser == null) {
// 			return json.toJson();
// 		}
//
// 		User uEntity = uService.getById(sessionUser.getId());
// 		if (uEntity == null) {
// 			super.logOut(session, request);
// 			json.set(2, "2-10"); // 您的账户信息发生变更，请重新登录
// 			return json.toJson();
// 		}
//
// 		if(uEntity.getType() == Global.USER_TYPE_PROXY) {
// 			if(uCodePointUtil.isSpecialZhiShu(uEntity)) {
// 				int type = 2;
// 				ActivitySalaryVO bean = aSalaryService.get(uEntity.getId(), type);
// 				if(bean != null) {
// 					json.data("data", bean);
// 					json.set(0, "0-1");
// 				} else {
// 					json.set(2, "2-4001");
// 				}
// 			} else {
// 				json.set(2, "2-4004");
// 			}
// 		}
// 		return json.toJson();
// 	}
//
// 	@RequestMapping(value = WUC.ACTIVITY_SALARY_ZD_RECEIVE, method = { RequestMethod.POST })
// 	@ResponseBody
// 	public synchronized Map<String, Object> ACTIVITY_SALARY_ZD_RECEIVE(HttpSession session, HttpServletRequest request) {
// 		WebJSON json = new WebJSON(dataFactory);
// 		SessionUser sessionUser = super.getSessionUser(json, session, request);
// 		if (sessionUser == null) {
// 			return json.toJson();
// 		}
//
// 		User uEntity = uService.getById(sessionUser.getId());
// 		if (uEntity == null) {
// 			super.logOut(session, request);
// 			json.set(2, "2-10"); // 您的账户信息发生变更，请重新登录
// 			return json.toJson();
// 		}
//
// 		if(uEntity.getType() == Global.USER_TYPE_PROXY) {
// 			if(uCodePointUtil.isSpecialZhiShu(uEntity)) {
// 				int type = 2;
// 				boolean flag = aSalaryService.receive(json, uEntity.getId(), type);
// 				if(flag) {
// 					json.set(0, "0-1");
// 				} else {
// 					if(!StringUtil.isNotNull(json.getMessage())) {
// 						json.set(1, "1-1");
// 					}
// 				}
// 			} else {
// 				json.set(2, "2-4004");
// 			}
// 		}
// 		return json.toJson();
// 	}
//
// 	@RequestMapping(value = WUC.ACTIVITY_RECHARGE_LOOP_LOAD, method = { RequestMethod.POST })
// 	@ResponseBody
// 	public Map<String, Object> ACTIVITY_RECHARGE_LOOP_LOAD(HttpSession session, HttpServletRequest request) {
// 		WebJSON json = new WebJSON(dataFactory);
// 		SessionUser sessionUser = super.getSessionUser(json, session, request);
// 		if (sessionUser == null) {
// 			return json.toJson();
// 		}
//
// 		ActivityRechargeLoopVO bean = aRechargeLoopService.get(sessionUser.getId());
// 		if(bean != null) {
// 			json.data("data", bean);
// 			json.set(0, "0-1");
// 		} else {
// 			json.set(2, "2-4001");
// 		}
// 		return json.toJson();
// 	}
//
// 	@RequestMapping(value = WUC.ACTIVITY_RECHARGE_LOOP_RECEIVE, method = { RequestMethod.POST })
// 	@ResponseBody
// 	public Map<String, Object> ACTIVITY_RECHARGE_LOOP_RECEIVE(HttpSession session, HttpServletRequest request) {
// 		WebJSON json = new WebJSON(dataFactory);
// 		SessionUser sessionUser = super.getSessionUser(json, session, request);
// 		if (sessionUser == null) {
// 			return json.toJson();
// 		}
//
// 		String ip = HttpUtil.getRealIp(request);
// 		int step = HttpUtil.getIntParameter(request, "step");
// 		boolean flag = aRechargeLoopService.receive(json, sessionUser.getId(), ip, step);
// 		if(flag) {
// 			json.set(0, "0-1");
// 		} else {
// 			if(!StringUtil.isNotNull(json.getMessage())) {
// 				json.set(1, "1-1");
// 			}
// 		}
// 		return json.toJson();
// 	}



	/**
	 * 加载全部进行中的活动
	 * @param session
	 * @param request
	 * @return
	 */
	@RequestMapping(value = WUC.ACTIVITY_LIST_LOAD, method = { RequestMethod.GET,RequestMethod.POST })
	@ResponseBody
	public Map<String, Object> ACTIVITY_LIST_LOAD(HttpSession session, HttpServletRequest request) {
		WebJSON json = new WebJSON(dataFactory);
		SessionUser sessionUser = super.getSessionUser(json, session, request);
		if (sessionUser == null) {
			return json.toJson();
		}
		if (sessionUser.getUpid() != 0) {
		    List<ActivityRebate> rebateList = new ArrayList<ActivityRebate>();
		    json.data("rebateList", rebateList);
			return json.toJson();
		}
		
		if (sessionUser.getId() == Global.USER_TOP_ID) {
			json.set(2, "2-13");
			return json.toJson();
		}
		//查询所有返点活动集合
	    List<ActivityRebate> rebateList =  activityRebateService.getRebateActivityList();
	    
	    json.data("rebateList", rebateList);
		return json.toJson();
	}
	
	
/*	@RequestMapping(value = WUC.ACTIVITY_WHEEL_DRAW, method = { RequestMethod.POST })
	@ResponseBody
	public Map<String, Object> ACTIVITY_WHEEL_DRAW(HttpSession session, HttpServletRequest request) {
		WebJSON json = new WebJSON(dataFactory);
		SessionUser sessionUser = super.getSessionUser(json, session, request);
		if (sessionUser == null) {
			return json.toJson();
		}

		if (sessionUser.getId() == Global.USER_TOP_ID) {
			json.set(2, "2-13");
			return json.toJson();
		}

		String ip = HttpUtil.getRealIp(json, request);
		if (ip == null) {
			return json.toJson();
		}

		Double amount = wheelService.draw(json, sessionUser.getId(), ip);
		// amount == null || amount <= 0 活动参与失败并返回失败原因
		// amount > 0 活动参与成功并返回金额
		if(amount != null && amount > 0) {
			String amountStr = MathUtil.doubleUpTOString(amount);
			json.data("amount", amountStr);
			json.set(0, "0-1");
		}
		return json.toJson();
	}*/

/*    //
	// @RequestMapping(value = WUC.ACTIVITY_PACKET_LOAD, method = { RequestMethod.POST })
	// @ResponseBody
	// public Map<String, Object> ACTIVITY_PACKET_LOAD(HttpSession session, HttpServletRequest request) {
	// 	WebJSON json = new WebJSON(dataFactory);
	// 	User uEntity = super.getCurrUser(session, request);
	// 	String ip = HttpUtil.getRealIp(request);
	// 	if(uEntity != null) {
	// 		List<ActivityPacketInfo> list = packetService.getAvaliablePackets(uEntity.getId(), ip);
	// 		List<ActivityPacketInfoVO> data = new ArrayList<ActivityPacketInfoVO>();
	// 		for (ActivityPacketInfo activityPacketInfo : list) {
	// 			ActivityPacketInfoVO vo = new ActivityPacketInfoVO(activityPacketInfo, dataFactory, userInfoDao);
	// 			data.add(vo);
	// 		}
	// 		json.data("data", data);
	// 	}
	// 	json.set(0, "0-1");
	// 	return json.toJson();
	// }
*/
	// @RequestMapping(value = WUC.ACTIVITY_PACKET_SEND, method = { RequestMethod.POST })
	// @ResponseBody
	// public synchronized Map<String, Object> ACTIVITY_PACKET_SEND(HttpSession session, HttpServletRequest request) {
	// 	WebJSON json = new WebJSON(dataFactory);
	// 	User uEntity = super.getCurrUser(session, request);
	// 	if(uEntity != null) {
	// 		String loadType = request.getParameter("loadType");//loadSecurity加载密保问题 send发送红包
	// 		if(loadType.equals("loadSecurity")){
	// 			loadSecurityInfo(json, uEntity);
	// 		}else{
	// 			int count = Integer.parseInt(request.getParameter("count"));
	// 			double amount = Double.parseDouble(request.getParameter("amount"));
	// 			amount = Math.abs(amount);
	// 			String withdrawPwd = request.getParameter("withdrawPwd");
	// 			int questionId = Integer.parseInt(request.getParameter("questionId"));
	// 			String answer = request.getParameter("answer");
	// 			boolean validateSecurity = uSecurityService.validateSecurity(questionId, uEntity.getId(), answer);
	// 			boolean validateWithdrawPwd = CipherUtil.validatePassword(uEntity.getWithdrawPassword(), withdrawPwd);
	// 			if(!validateSecurity){
	// 				json.set(2, "2-1010");
	// 			}else if(!validateWithdrawPwd){
	// 				json.set(2, "2-1009");
	// 			}else if(uEntity.getLotteryMoney() < amount){
	// 				json.set(2, "2-1109");
	// 			}else{
	// 				packetService.doSendPacket(count, amount, uEntity.getId());
	// 				json.set(0, "0-1");
	// 			}
	// 		}
	// 	} else {
	// 		String message = (String) request.getAttribute("message");
	// 		message = StringUtil.isNotNull(message) ? message : "2-1006";
	// 		json.set(2, message);
	// 	}
	// 	return json.toJson();
	// }

	// private void loadSecurityInfo(WebJSON json, User uEntity) {
	// 	UserSecurityVO securityVO = uSecurityService.getRandomByUserId(uEntity.getId());
	// 	if(securityVO == null){
	// 		json.set(2, "2-1033");
	// 	}else if(!StringUtil.isNotNull(uEntity.getWithdrawPassword()) ){
	// 		json.set(2, "2-1072");
	// 	}else{
	// 		json.set(0, "0-1");
	// 		json.data("data", securityVO);
	// 	}
	// }

	// @RequestMapping(value = WUC.ACTIVITY_PACKET_RECEIVE, method = { RequestMethod.POST })
	// @ResponseBody
	// public synchronized Map<String, Object> ACTIVITY_PACKET_RECEIVE(HttpSession session, HttpServletRequest request) {
	// 	User uEntity = super.getCurrUser(session, request);
	// 	WebJSON json = new WebJSON(dataFactory);
	// 	String ip = HttpUtil.getRealIp(request);
	// 	if(uEntity != null) {
	// 		int id = Integer.parseInt(request.getParameter("id"));
	// 		List<ActivityPacketInfo> list = packetService.getAvaliablePackets(uEntity.getId(), ip);
	// 		if(list != null && !list.isEmpty()){
	// 			ActivityPacketInfo packetInfo = packetService.validateIp(id, ip, list);
	// 			if(packetInfo == null){
	// 				json.set(2, "2-4010");
	// 				return json.toJson();
	// 			}
	//
	// 			double minCost = packetService.validateCost(uEntity.getId());
	// 			if(minCost > -1){
	// 				json.set(2, "2-4009", minCost);
	// 				return json.toJson();
	// 			}
	//
	// 			 ActivityPacketBill packetBill = packetService.doGrabPacket(packetInfo, uEntity.getId(), ip);
	// 			 if(packetBill == null){
	// 				json.set(2, "2-4008");
	// 			 }else{
	// 				json.data("data", packetBill);
	// 				json.set(0, "0-1");
	// 			 }
	// 		}else{
	// 			//手慢了红包被抢
	// 			json.set(2, "2-4008");
	// 		}
	// 	} else {
	// 		String message = (String) request.getAttribute("message");
	// 		message = StringUtil.isNotNull(message) ? message : "2-1006";
	// 		json.set(2, message);
	// 	}
	// 	return json.toJson();
	// }


	// /**
	//  * 加载消费奖励活动详情
	//  * @param session
	//  * @param request
	//  * @return
	//  */
// 	@RequestMapping(value = WUC.ACTIVITY_COST_LOAD, method = { RequestMethod.POST })
// 	@ResponseBody
// 	public Map<String, Object> ACTIVITY_COST_LOAD(HttpSession session, HttpServletRequest request) {
// 		WebJSON json = new WebJSON(dataFactory);
// 		SessionUser sessionUser = super.getSessionUser(json, session, request);
// 		if (sessionUser == null) {
// 			return json.toJson();
// 		}
//
// 		ActivityRebateVo bean = activityCostService.getCostInfo(Global.ACTIVITY_REBATE_COST);
// 		if(bean != null) {
// 			json.data("data", bean);
// 			json.set(0, "0-1");
// 		} else {
// 			json.set(2, "2-4001");
// 		}
// 		return json.toJson();
// 	}
//
// 	/**
// 	 * 领取情况
// 	 * @param session
// 	 * @param request
// 	 * @return
// 	 */
// 	@RequestMapping(value = WUC.ACTIVITY_COST_CONSUMPTION, method = { RequestMethod.POST })
// 	@ResponseBody
// 	public Map<String, Object> ACTIVITY_COST_CONSUMPTION(HttpSession session, HttpServletRequest request) {
// 		WebJSON json = new WebJSON(dataFactory);
// 		SessionUser sessionUser = super.getSessionUser(json, session, request);
// 		if (sessionUser == null) {
// 			return json.toJson();
// 		}
//
// 		SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd");
// 		Date now = new Date();
// 		String sTime = fmt.format(now) +" 00:00:00";
// 		String eTime = fmt.format(now) +" 23:59:59";
// 		ActivityCostDrawInfoVo bean = activityCostService.getCostDarwInfo(sessionUser.getId(),sTime,eTime);
// 		if(bean != null) {
// 			json.data("data", bean);
// 			json.set(0, "0-1");
// 		} else {
// 			json.set(2, "2-4001");
// 		}
// 		return json.toJson();
// 	}
//
//
// 	/**
// 	 * 领取奖励
// 	 * @param session
// 	 * @param request
// 	 * @return
// 	 */
// 	@RequestMapping(value = WUC.ACTIVITY_COST_RECEIVE, method = { RequestMethod.POST })
// 	@ResponseBody
// 	public Map<String, Object> ACTIVITY_COST_RECEIVE(HttpSession session, HttpServletRequest request) {
// 		WebJSON json = new WebJSON(dataFactory);
// 		SessionUser sessionUser = super.getSessionUser(json, session, request);
// 		if (sessionUser == null) {
// 			return json.toJson();
// 		}
//
// 		ActivityRebate activityInfo = activityRebateDao.getByStatusAndType(Global.ACTIVITY_REBATE_COST,
// 				Global.ACTIVITY_STATUS_OPEN);
// 		//活动不存在
// 		if(activityInfo == null){
// 			json.set(2, "2-4001");
// 			return json.toJson();
// 		}
// 		String ip = HttpUtil.getRealIp(request).trim();
// 		ActivityCostBill lastCost  = activityCostService.getLastCostBillInfoByIp(ip);
//
// //			ActivityCostBill lastCost = activityCostService.getLastCostBillInfo(uEntity.getId());
// 		//和上次领取的IP不同
// 		if(lastCost !=null &&
// 				!String.valueOf(lastCost.getUserId()).equals(String.valueOf(sessionUser.getId()))){
// 			json.set(2, "2-4011");
// 			return json.toJson();
// 		}
// 		SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd");
// 		Date now = new Date();
// 		String sTime = fmt.format(now) +" 00:00:00";
// 		String eTime = fmt.format(now) +" 23:59:59";
// 		//判断今天是否领取
// 		ActivityCostBill bill = activityCostService.getToDayDrawInfo(sessionUser.getId(), sTime, eTime);
// 		if (bill != null) {
// 			json.set(2, "2-4012");
// 			return json.toJson();
// 		}
// 		double costMoney = userBetsReadDao.getBillingOrder(sessionUser.getId(), sTime, eTime);
// 		if(costMoney <=0){
// 			json.set(2, "2-4013");
// 			return json.toJson();
// 		}
// 		//根据活动配置得到对应的消费奖励
// 		double prizeMoney = PrizeUtils.costConfigPrize(activityInfo.getRules(),costMoney);
// 		if(prizeMoney > 0){
// 			//最大限制8888
// 			if(prizeMoney > 8888)
// 				prizeMoney = 8888;
// 			//领取奖励
// 			boolean result = activityCostService.darwCostAward(sessionUser.getId(),
// 					costMoney,prizeMoney, now, ip);
// 			if(result){
// 				ActivityCostDrawInfoVo drawInfo = new ActivityCostDrawInfoVo();
// 				drawInfo.setIsReceived(Global.ACTIVITY_COST_STATUS_Y);//已领取
// 				drawInfo.setMayDraw(prizeMoney);//已领金额
// 				drawInfo.setTodayCost(costMoney);//领取时消费的金额
// 				json.data("data", drawInfo);
// 				json.set(0, "0-1");
// 			}else{
// 				json.set(2, "2-4014");
// 			}
// 		}else{
// 			json.set(2, "2-4013");
// 		}
// 		return json.toJson();
// 	}
//
// 	/**
// 	 * 领取红包雨金额
// 	 */
// 	@RequestMapping(value = WUC.ACTIVITY_REDPACKET_COLLECT, method = { RequestMethod.POST })
// 	@ResponseBody
// 	public Map<String, Object> ACTIVITY_REDPACKET_COLLECT(HttpSession session, HttpServletRequest request) {
// 		WebJSON json = new WebJSON(dataFactory);
// 		SessionUser sessionUser = super.getSessionUser(json, session, request);
// 		if (sessionUser == null) {
// 			return json.toJson();
// 		}
//
// 		String ip = HttpUtil.getRealIp(json, request);
// 		if (ip == null) {
// 			return json.toJson();
// 		}
//
// 		Double amount = rainBillService.collect(json, sessionUser.getId(), ip);
// 		if(amount != null) {
// 			json.data("amount", amount);
// 			json.set(0, "0-1");
// 		}
// 		return json.toJson();
// 	}
//
// 	/**
// 	 * 获取红包雨时间
// 	 */
// 	@RequestMapping(value = WUC.ACTIVITY_REDPACKET_TIME, method = { RequestMethod.POST })
// 	@ResponseBody
// 	public Map<String, Object> ACTIVITY_REDPACKET_TIME(HttpSession session, HttpServletRequest request) {
// 		WebJSON json = new WebJSON(dataFactory);
// 		SessionUser sessionUser = super.getSessionUser(json, session, request);
// 		if (sessionUser == null) {
// 			return json.toJson();
// 		}
//
// 		// 当前状态
// 		ActivityRedPacketRainTime currentTime = dataFactory.getCurrentActivityRedPacketRainTime();
//
// 		boolean started = currentTime != null;
// 		boolean allowAttend = false; // 是否允许参与红包雨
// 		int nextSeconds = -1; // 下一轮的剩余秒数
// 		if (started) {
// 			String date = currentTime.getDate();
// 			String hour = currentTime.getHour();
// 			// 看用户本轮是否已经参与
// 			ActivityRedPacketRainBill bill = rainBillService.getByUserIdAndDateAndHour(sessionUser.getId(), date, hour);
// 			started = bill == null;
// 		}
//
// 		// 查看用户今天的消费情况，来看是否允许参与今天的红包雨
// 		if (started) {
// 			String sDate = new Moment().toSimpleDate();
// 			String eDate = new Moment().add(1, "days").toSimpleDate();
// 			List<?> userCosts = uLotteryReportReadService.listAmountGroupByUserIds(new Integer[]{sessionUser.getId()}, sDate, eDate);
// 			if (CollectionUtils.isNotEmpty(userCosts)) {
// 				double minCost = dataFactory.getActivityRedPacketRainConfig().getMinCost();
// 				Object[] userCostArr = (Object[]) userCosts.get(0);
// 				double todayCost = (Double) userCostArr[1];
// 				allowAttend = todayCost >= minCost;
// 			}
// 		}
//
// 		ActivityRedPacketRainTime nextTime = dataFactory.getNextActivityRedPacketRainTime(currentTime);
// 		if (nextTime != null) {
// 			Moment now = new Moment();
// 			Moment nextStartTime = new Moment().fromTime(nextTime.getStartTime());
// 			nextSeconds = nextStartTime.difference(now, "second");
// 		}
//
// 		int currentEnd = 0;
// 		if (currentTime != null) {
// 			Moment now = new Moment();
// 			Moment endTime = new Moment().fromTime(currentTime.getEndTime());
// 			currentEnd = endTime.difference(now, "second");
// 			if (currentEnd > 2) {
// 				currentEnd -= 2;
// 			}
// 		}
//
// 		Map<String, Object> data = new HashMap<>();
// 		data.put("currentStarted", started); // 本轮红包雨是否已经开启
// 		data.put("allowAttend", allowAttend); // 是否允许参与红包雨
// 		data.put("nextSeconds", nextSeconds); // 下一轮的剩余秒数
// 		data.put("currentStartTime", currentTime == null ? "" : currentTime.getStartTime()); // 本轮开始时间
// 		data.put("currentEndTime", currentTime == null ? "" : currentTime.getEndTime()); // 本轮结束时间
// 		data.put("currentEnd", currentEnd); // 本轮XX秒后结束
// 		json.data("data", data);
// 		json.set(0, "0-1");
// 		return json.toJson();
// 	}
}