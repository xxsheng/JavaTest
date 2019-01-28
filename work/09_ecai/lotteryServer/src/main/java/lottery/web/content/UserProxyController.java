package lottery.web.content;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.RandomUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javautils.StringUtil;
import javautils.array.ArrayUtils;
import javautils.date.Moment;
import javautils.encrypt.PasswordUtil;
import javautils.http.HttpUtil;
import javautils.jdbc.PageList;
import lottery.domains.content.biz.UserActionLogService;
import lottery.domains.content.biz.UserBetsService;
import lottery.domains.content.biz.UserCodeQuotaService;
import lottery.domains.content.biz.UserDailySettleService;
import lottery.domains.content.biz.UserDividendBillService;
import lottery.domains.content.biz.UserDividendService;
import lottery.domains.content.biz.UserGameDividendBillService;
import lottery.domains.content.biz.UserGameWaterBillReadService;
import lottery.domains.content.biz.UserRegistLinkService;
import lottery.domains.content.biz.UserSecurityService;
import lottery.domains.content.biz.UserService;
import lottery.domains.content.biz.UserStatService;
import lottery.domains.content.biz.UserTransfersService;
import lottery.domains.content.biz.read.GameBetsReadService;
import lottery.domains.content.biz.read.UserBetsReadService;
import lottery.domains.content.biz.read.UserBillReadService;
import lottery.domains.content.biz.read.UserDailySettleBillReadService;
import lottery.domains.content.biz.read.UserDailySettleReadService;
import lottery.domains.content.biz.read.UserDividendBillReadService;
import lottery.domains.content.biz.read.UserDividendReadService;
import lottery.domains.content.biz.read.UserGameDividendBillReadService;
import lottery.domains.content.biz.read.UserGameReportReadService;
import lottery.domains.content.biz.read.UserLotteryReportReadService;
import lottery.domains.content.biz.read.UserMainReportReadService;
import lottery.domains.content.biz.read.UserReadService;
import lottery.domains.content.entity.User;
import lottery.domains.content.entity.UserDailySettle;
import lottery.domains.content.entity.UserDailySettleBill;
import lottery.domains.content.entity.UserDividend;
import lottery.domains.content.entity.UserDividendBill;
import lottery.domains.content.entity.UserGameDividendBill;
import lottery.domains.content.global.Global;
import lottery.domains.content.vo.bets.UserBetsVO;
import lottery.domains.content.vo.user.TeamStatisticsDailyVO;
import lottery.domains.content.vo.user.TeamStatisticsVO;
import lottery.domains.content.vo.user.UserBaseVO;
import lottery.domains.content.vo.user.UserCodeQuotaVO;
import lottery.domains.content.vo.user.UserCodeRangeVO;
import lottery.domains.content.vo.user.UserDailySettleBillVO;
import lottery.domains.content.vo.user.UserDividendBillVO;
import lottery.domains.content.vo.user.UserGameDividendBillVO;
import lottery.domains.content.vo.user.UserGameReportTeamStatisticsVO;
import lottery.domains.content.vo.user.UserLotteryReportTeamStatisticsVO;
import lottery.domains.content.vo.user.UserMainReportTeamStatisticsVO;
import lottery.domains.content.vo.user.UserSecurityVO;
import lottery.domains.content.vo.user.UserTeamStatisticsVO;
import lottery.domains.content.vo.user.UserVO;
import lottery.domains.pool.DataFactory;
import lottery.web.WUC;
import lottery.web.WebJSON;
import lottery.web.content.utils.UserCodePointUtil;
import lottery.web.content.validate.UserDailySettleValidate;
import lottery.web.content.validate.UserDividendValidate;
import lottery.web.content.validate.UserProxyValidate;
import lottery.web.content.validate.UserValidate;
import lottery.web.content.validate.UserWithdrawValidate;
import lottery.web.helper.AbstractActionController;
import lottery.web.helper.session.SessionUser;

@Controller
public class UserProxyController extends AbstractActionController {
	/**
	 * Service
	 */
	@Autowired
	private UserService uService;
	@Autowired
	private UserReadService uReadService;

	@Autowired
	private UserStatService uStatService;

	@Autowired
	private UserCodeQuotaService uCodeQuotaService;

	@Autowired
	private UserSecurityService uSecurityService;

	@Autowired
	private UserMainReportReadService uMainReportReadService;

	@Autowired
	private UserLotteryReportReadService uLotteryReportReadService;

	@Autowired
	private UserGameReportReadService uGameReportReadService;

	@Autowired
	private UserBetsService uBetsService;

	@Autowired
	private UserBetsReadService uBetsReadService;

	@Autowired
	private UserBillReadService uBillReadService;

	@Autowired
	private UserTransfersService uTransfersService;

	@Autowired
	private UserRegistLinkService uRegistLinkService;

	@Autowired
	private UserActionLogService uActionLogService;

	@Autowired
	private UserDailySettleService uDailySettleService;

	@Autowired
	private UserDailySettleReadService uDailySettleReadService;

	@Autowired
	private UserDailySettleBillReadService uDailySettleBillReadService;

	@Autowired
	private UserDividendService uDividendService;

	@Autowired
	private UserDividendReadService uDividendReadService;

	@Autowired
	private UserDividendBillService uDividendBillService;

	@Autowired
	private UserDividendBillReadService uDividendBillReadService;

	@Autowired
	private UserGameDividendBillService uGameDividendBillService;

	@Autowired
	private UserGameDividendBillReadService uGameDividendBillReadService;

	@Autowired
	private UserGameWaterBillReadService uGameWaterBillService;

	@Autowired
	private GameBetsReadService gameBetsReadService;
	
	@Autowired
	private UserDailySettleReadService userDailySettleReadService;
	@Autowired
	private UserDividendReadService UserDividendReadService;

	/**
	 * Validate
	 */
	@Autowired
	private UserValidate uValidate;

	@Autowired
	private UserProxyValidate uProxyValidate;

	@Autowired
	private UserWithdrawValidate uWithdrawValidate;

	@Autowired
	private UserDailySettleValidate uDailySettleValidate;

	@Autowired
	private UserDividendValidate uDividendValidate;

	/**
	 * Util
	 */
	@Autowired
	private UserCodePointUtil uCodePointUtil;

	/**
	 * DataFactory
	 */
	@Autowired
	private DataFactory dataFactory;

	@RequestMapping(value = WUC.PROXY_INDEX_LOAD, method = { RequestMethod.POST })
	@ResponseBody
	public Map<String, Object> PROXY_INDEX_LOAD(HttpSession session, HttpServletRequest request) {
		WebJSON json = new WebJSON(dataFactory);
		SessionUser sessionUser = super.getSessionUser(json, session, request);
		if (sessionUser == null) {
			return json.toJson();
		}

		String sDate = HttpUtil.getStringParameterTrim(request, "sDate");
		String eDate = HttpUtil.getStringParameterTrim(request, "eDate");
		String dailyType = HttpUtil.getStringParameterTrim(request, "dailyType");
		if (StringUtils.isEmpty(sDate) || StringUtils.isEmpty(eDate)) {
			json.set(2, "2-14"); // 请选择时间范围！
			return json.toJson();
		}

		UserTeamStatisticsVO uTeamStatisticsVO = new UserTeamStatisticsVO();
		UserMainReportTeamStatisticsVO uMainReportTeamStatisticsVO = new UserMainReportTeamStatisticsVO();
		UserLotteryReportTeamStatisticsVO uLotteryReportTeamStatisticsVO = new UserLotteryReportTeamStatisticsVO();
		UserGameReportTeamStatisticsVO uGameReportTeamStatisticsVO = new UserGameReportTeamStatisticsVO();
		List<TeamStatisticsDailyVO> dailyVOs = new LinkedList<>();
		if (sessionUser.getId() == Global.USER_TOP_ID) {
			// 总账统计

			// 团队余额/团队人数/注册人数/在线人数/
			uTeamStatisticsVO = uReadService.statisticsAll(sDate, eDate);

			// 团队充值/充值人数/团队取款
			uMainReportTeamStatisticsVO = uMainReportReadService.statisticsAll(sDate, eDate);

			// 彩票投注总额/彩票中奖总额/团队撤单总额/团队彩票返点/团队活动总额/团队彩票盈亏/充值手续费
			uLotteryReportTeamStatisticsVO = uLotteryReportReadService.statisticsAll(sDate, eDate);

			// 游戏投注总额/游戏团队盈亏/游戏团队返点
			uGameReportTeamStatisticsVO = uGameReportReadService.statisticsAll(sDate, eDate);
			// 综合盈亏(团队彩票盈亏+游戏团队盈亏)

			// 统计每天数据
			// 每天彩票投注总额/每天彩票中奖总额/每天彩票返点
			if ("lottery".equals(dailyType)) {
				dailyVOs = uLotteryReportReadService.statisticsAllDaily(sDate, eDate);
			}
			// 每天注册人数)
			else if ("register".equals(dailyType)) {
				dailyVOs = uReadService.statisticsAllDaily(sDate, eDate);
			}
			// 每天充值/每天取款
			else if ("main".equals(dailyType)) {
				dailyVOs = uMainReportReadService.statisticsAllDaily(sDate, eDate);
			}
		}
		else {
			User uEntity = uReadService.getByIdFromRead(sessionUser.getId());
			if (uEntity == null) {
				super.logOut(session, request);
				json.set(2, "2-10"); // 您的账户信息发生变更，请重新登录
				return json.toJson();
			}

			if (uEntity.getType() == Global.USER_TYPE_RELATED) {

				int[] relatedUserIds = ArrayUtils.transGetIds(uEntity.getRelatedUsers());
				if (relatedUserIds != null && relatedUserIds.length > 0) {
					// 关联账号统计

					// 团队余额/团队人数/注册人数/在线人数/
					uTeamStatisticsVO = uReadService.statisticsByTeam(relatedUserIds, sDate, eDate);

					// 团队充值/充值人数/团队取款
					uMainReportTeamStatisticsVO = uMainReportReadService.statisticsByTeam(relatedUserIds, sDate, eDate);

					// 彩票投注总额/彩票中奖总额/团队撤单总额/团队彩票返点/团队活动总额/团队彩票盈亏/充值手续费
					uLotteryReportTeamStatisticsVO = uLotteryReportReadService.statisticsByTeam(relatedUserIds, sDate, eDate);

					// 游戏投注总额/游戏团队盈亏/游戏团队返点
					uGameReportTeamStatisticsVO = uGameReportReadService.statisticsByTeam(relatedUserIds, sDate, eDate);
					// 综合盈亏(团队彩票盈亏+游戏团队盈亏)

					// 统计每天数据
					// 每天彩票投注总额/每天彩票中奖总额/每天彩票返点
					if ("lottery".equals(dailyType)) {
						dailyVOs = uLotteryReportReadService.statisticsByTeamDaily(relatedUserIds, sDate, eDate);
					}
					// 每天注册人数)
					else if ("register".equals(dailyType)) {
						dailyVOs = uReadService.statisticsByTeamDaily(relatedUserIds, sDate, eDate);
					}
					// 每天充值/每天取款
					else if ("main".equals(dailyType)) {
						dailyVOs = uMainReportReadService.statisticsByTeamDaily(relatedUserIds, sDate, eDate);
					}
				}
			}
			else {
				// 普通用户统计

				// 团队余额/团队人数/注册人数/在线人数/
				uTeamStatisticsVO = uReadService.statisticsByTeam(sessionUser.getId(), sDate, eDate);

				// 团队充值/充值人数/团队取款
				uMainReportTeamStatisticsVO = uMainReportReadService.statisticsByTeam(sessionUser.getId(), sDate, eDate);

				// 彩票投注总额/彩票中奖总额/团队撤单总额/团队彩票返点/团队活动总额/团队彩票盈亏/充值手续费
				uLotteryReportTeamStatisticsVO = uLotteryReportReadService.statisticsByTeam(sessionUser.getId(), sDate, eDate);

				// 游戏投注总额/游戏团队盈亏/游戏团队返点
				uGameReportTeamStatisticsVO = uGameReportReadService.statisticsByTeam(sessionUser.getId(), sDate, eDate);
				// 综合盈亏(团队彩票盈亏+游戏团队盈亏)

				// 统计每天数据
				// 每天彩票投注总额/每天彩票中奖总额/每天彩票返点
				if ("lottery".equals(dailyType)) {
					dailyVOs = uLotteryReportReadService.statisticsByTeamDaily(sessionUser.getId(), sDate, eDate);
				}
				// 每天注册人数)
				else if ("register".equals(dailyType)) {
					dailyVOs = uReadService.statisticsByTeamDaily(sessionUser.getId(), sDate, eDate);
				}
				// 每天充值/每天取款
				else if ("main".equals(dailyType)) {
					dailyVOs = uMainReportReadService.statisticsByTeamDaily(sessionUser.getId(), sDate, eDate);
				}
			}
		}

		TeamStatisticsVO teamStatisticsVO = new TeamStatisticsVO(uTeamStatisticsVO, uMainReportTeamStatisticsVO, uLotteryReportTeamStatisticsVO, uGameReportTeamStatisticsVO);
		teamStatisticsVO.setDailyData(dailyVOs);

		json.data("data", teamStatisticsVO);
		json.set(0, "0-1");
		return json.toJson();
	}

	@RequestMapping(value = WUC.PROXY_USER_ADD, method = { RequestMethod.POST })
	@ResponseBody
	public Map<String, Object> PROXY_USER_ADD(HttpSession session, HttpServletRequest request) {
		WebJSON json = new WebJSON(dataFactory);
		SessionUser sessionUser = super.getSessionUser(json, session, request);
		if (sessionUser == null) {
			return json.toJson();
		}

		String ip = HttpUtil.getRealIp(json, request);
		if (ip == null) {
			return json.toJson();
		}

		User uEntity = uReadService.getByIdFromRead(sessionUser.getId());
		if (uEntity == null) {
			super.logOut(session, request);
			json.set(2, "2-10"); // 您的账户信息发生变更，请重新登录
			return json.toJson();
		}

		if (uEntity.getType() == Global.USER_TYPE_RELATED) {
			json.set(2, "2-13"); // 您的账号无法使用该功能！
			return json.toJson();
		}

		int type = HttpUtil.getIntParameter(request, "type");
		String username = HttpUtil.getStringParameterTrim(request, "username");
		String password = PasswordUtil.generatePasswordByPlainString("a123456"); // 默认密码
		int code = HttpUtil.getIntParameter(request, "code");
		double locatePoint = uCodePointUtil.getLocatePoint(code);
		double notLocatePoint = uCodePointUtil.getNotLocatePoint(code);
		if (StringUtils.isEmpty(username) || StringUtils.isEmpty(password)) {
			json.set(2, "2-12");
			return json.toJson();
		}

		String nickname = username; // 自动注册会员昵称与用户名相同

		// 不允许创建1990等级
//		if (code > 1990) {
//			json.set(2, "2-2022");
//			return json.toJson();
//		}
		
		if (code > dataFactory.getCodeConfig().getSysCode()) {
			json.set(2, "2-2022");
			return json.toJson();
		}

		if(uValidate.testUser(json, username)) {
			if(uProxyValidate.testLowerPoint(json, uEntity, type, locatePoint)) {
				// 配额验证
				if(!uCodePointUtil.hasQuota(uEntity, code)) {
					json.set(2, "2-2011");
					return json.toJson();
				}

				Integer userId = uService.addLowerUser(uEntity, username, nickname, password,
						type, code, locatePoint, notLocatePoint);
				if(userId != null) {
					uActionLogService.addNewUser(uEntity.getId(), ip, username, type, locatePoint);
					json.set(0, "0-1");
				} else {
					json.set(1, "1-1");
				}
			}
		}
		return json.toJson();
		// WebJSON json = new WebJSON(dataFactory);
		// SessionUser sessionUser = super.getSessionUser(json, session, request);
		// if (sessionUser == null) {
		// 	return json.toJson();
		// }
		//
		// User uEntity = uReadService.getByIdFromRead(sessionUser.getId());
		// if (uEntity == null) {
		// 	super.logOut(session, request);
		// 	json.set(2, "2-10"); // 您的账户信息发生变更，请重新登录
		// 	return json.toJson();
		// }
		//
		// int type = HttpUtil.getIntParameter(request, "type");
		// String username = HttpUtil.getStringParameterTrim(request, "username");
		// String password = PasswordUtil.generatePasswordByPlainString("a123456"); // 默认密码
		// int code = HttpUtil.getIntParameter(request, "code");
		// double locatePoint = uCodePointUtil.getLocatePoint(code);
		// double notLocatePoint = uCodePointUtil.getNotLocatePoint(code);
		// if (StringUtils.isEmpty(username) || StringUtils.isEmpty(password)) {
		// 	json.set(2, "2-12");
		// 	return json.toJson();
		// }
		//
		// String nickname = username; // 自动注册会员昵称与用户名相同
		//
		// // 不允许创建1958等级
		// if (code == 1958) {
		// 	json.set(2, "2-2022");
		// 	return json.toJson();
		// }
		// if (code > dataFactory.getCodeConfig().getSysCode()) {
		// 	json.set(2, "2-2022");
		// 	return json.toJson();
		// }
		//
		// if(uValidate.testUser(json, username)) {
		// 	if(uProxyValidate.testLowerPoint(json, uEntity, type, locatePoint)) {
		// 		List<UserCodeQuotaVO> surplusQuota = uCodePointUtil.listSurplusQuota(uEntity);
		// 		for (UserCodeQuotaVO tmpQuota : surplusQuota) {
		// 			if(locatePoint >= tmpQuota.getMinPoint() && locatePoint <= tmpQuota.getMaxPoint()) {
		// 				// 配额不足
		// 				if(tmpQuota.getSurplusCount() < 1) {
		// 					json.set(2, "2-2011");
		// 					return json.toJson();
		// 				}
		// 			}
		// 		}
		// 		Integer userId = uService.addLowerUser(uEntity, username, nickname, password,
		// 				type, code, locatePoint, notLocatePoint);
		// 		if(userId != null) {
		// 			String ip = HttpUtil.getRealIp(request);
		// 			uActionLogService.addNewUser(uEntity.getId(), ip, username, type, locatePoint);
		// 			json.set(0, "0-1");
		// 		} else {
		// 			json.set(1, "1-1");
		// 		}
		// 	}
		// }
		// return json.toJson();
	}

	@RequestMapping(value = WUC.PROXY_LINK_ADD, method = { RequestMethod.POST })
	@ResponseBody
	public Map<String, Object> PROXY_LINK_ADD(HttpSession session, HttpServletRequest request) {
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

		if (uEntity.getType() == Global.USER_TYPE_RELATED) {
			json.set(2, "2-13"); // 您的账号无法使用该功能！
			return json.toJson();
		}

		int type = HttpUtil.getIntParameter(request, "type");
		int days = HttpUtil.getIntParameter(request, "days");
		// 衔接有效期
		String expTime = days > 0 ? new Moment().add(days, "days").toSimpleTime() : null;
		int amount = HttpUtil.getIntParameter(request, "amount");
		int code = HttpUtil.getIntParameter(request, "code");
		Integer deviceType = HttpUtil.getIntParameter(request, "deviceType");

		// 不允许创建1990以上等级
//		if (code > 1990) {
//			json.set(2, "2-2022");
//			return json.toJson();
//		}

		if (code > dataFactory.getCodeConfig().getSysCode()) {
			json.set(2, "2-2022");
			return json.toJson();
		}
		if (code < 1800) {
			json.set(2, "2-2022");
			return json.toJson();
		}

		double locatePoint = uCodePointUtil.getLocatePoint(code);

		if(uProxyValidate.testLowerPoint(json, uEntity, type, locatePoint)) {
			// 配额验证
			if(!uCodePointUtil.hasQuota(uEntity, code)) {
				json.set(2, "2-2011");
				return json.toJson();
			}

			String linkCode = RandomStringUtils.random(20, true, true);
			// String linkCode = OrderUtil.createString(12);
			String result = uRegistLinkService.add(json, uEntity.getId(), type, linkCode, locatePoint, expTime, amount, deviceType);
			if(result != null) {
				if (deviceType == Global.REGISTER_DEVICE_TYPE_MOBILE || deviceType == Global.REGISTER_DEVICE_TYPE_WECHAT) {
					json.data("qrCode", result);
				}
				else if(deviceType == Global.REGISTER_DEVICE_TYPE_WEB) {
					json.data("domain", result);
				}
				json.data("linkCode", linkCode);
				json.set(0, "0-1");
			} else {
				if (json.getCode() == null) {
					json.set(1, "1-1");
				}
			}
		}
		return json.toJson();
		// WebJSON json = new WebJSON(dataFactory);
		// SessionUser sessionUser = super.getSessionUser(json, session, request);
		// if (sessionUser == null) {
		// 	return json.toJson();
		// }
		//
		// User uEntity = uReadService.getByIdFromRead(sessionUser.getId());
		// if (uEntity == null) {
		// 	super.logOut(session, request);
		// 	json.set(2, "2-10"); // 您的账户信息发生变更，请重新登录
		// 	return json.toJson();
		// }
		//
		// int type = HttpUtil.getIntParameter(request, "type");
		// int days = HttpUtil.getIntParameter(request, "days");
		// // 衔接有效期
		// String expTime = days > 0 ? new Moment().add(days, "days").toSimpleTime() : null;
		// int amount = HttpUtil.getIntParameter(request, "amount");
		// int code = HttpUtil.getIntParameter(request, "code");
		//
		// // 不允许创建1958等级
		// if (code == 1958) {
		// 	json.set(2, "2-2022");
		// 	return json.toJson();
		// }
		//
		// if (code > dataFactory.getCodeConfig().getSysCode()) {
		// 	json.set(2, "2-2022");
		// 	return json.toJson();
		// }
		//
		// double locatePoint = uCodePointUtil.getLocatePoint(code);
		//
		// if(uProxyValidate.testLowerPoint(json, uEntity, type, locatePoint)) {
		// 	// 配额验证
		// 	List<UserCodeQuotaVO> surplusQuota = uCodePointUtil.listSurplusQuota(uEntity);
		// 	for (UserCodeQuotaVO tmpQuota : surplusQuota) {
		// 		if(locatePoint >= tmpQuota.getMinPoint() && locatePoint <= tmpQuota.getMaxPoint()) {
		// 			// 配额不足
		// 			if(tmpQuota.getSurplusCount() < 1) {
		// 				json.set(2, "2-2011");
		// 				return json.toJson();
		// 			}
		// 		}
		// 	}
		// 	// String linkCode = OrderUtil.getBillno()RandomStringUtils.random(12, true, true);
		// 	String linkCode = OrderUtil.createString(12);
		// 	boolean result = uRegistLinkService.add(uEntity.getId(), type, linkCode, locatePoint, expTime, amount);
		// 	if(result) {
		// 		json.data("data", linkCode);
		// 		json.set(0, "0-1");
		// 	} else {
		// 		json.set(1, "1-1");
		// 	}
		// }
		// return json.toJson();
	}

	@RequestMapping(value = WUC.PROXY_LINK_LIST, method = { RequestMethod.POST })
	@ResponseBody
	public Map<String, Object> PROXY_LINK_LIST(HttpSession session, HttpServletRequest request) {
		WebJSON json = new WebJSON(dataFactory);
		SessionUser sessionUser = super.getSessionUser(json, session, request);
		if (sessionUser == null) {
			return json.toJson();
		}



		int start = HttpUtil.getIntParameter(request, "start");
		int limit = HttpUtil.getIntParameter(request, "limit");
		Integer deviceType = HttpUtil.getIntParameter(request, "deviceType");
		PageList pList = uRegistLinkService.search(sessionUser.getId(), deviceType, start, limit);

		List<String> urls = dataFactory.getDoMainConfig().getUrls();
		if (CollectionUtils.isNotEmpty(urls)) {
			int randomIndex = RandomUtils.nextInt(urls.size());
			String domain = urls.get(randomIndex);
			json.data("domain", domain);
		}
		else {
			json.data("domain", HttpUtil.getReferer(request));
		}

		json.data("data", pList.getList());
		json.data("totalCount", pList.getCount());
		json.set(0, "0-1");
		return json.toJson();
	}

	@RequestMapping(value = WUC.PROXY_LINK_DEL, method = { RequestMethod.POST })
	@ResponseBody
	public Map<String, Object> PROXY_LINK_DEL(HttpSession session, HttpServletRequest request) {
		WebJSON json = new WebJSON(dataFactory);
		SessionUser sessionUser = super.getSessionUser(json, session, request);
		if (sessionUser == null) {
			return json.toJson();
		}

		int id = HttpUtil.getIntParameter(request, "id");
		boolean result = uRegistLinkService.delete(id, sessionUser.getId());
		if(result) {
			json.set(0, "0-1");
		} else {
			json.set(1, "1-1");
		}
		return json.toJson();
	}

	@RequestMapping(value = WUC.PROXY_QUOTA_INFO, method = { RequestMethod.POST })
	@ResponseBody
	public Map<String, Object> PROXY_QUOTA_INFO(HttpSession session, HttpServletRequest request) {
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

		List<UserCodeQuotaVO> uQuota = uCodePointUtil.listUserQuota(uEntity);
		UserCodeRangeVO uCode = uCodePointUtil.getUserCodeRange(uEntity);
		UserBaseVO uBean = new UserBaseVO(uEntity);
		Map<String, Object> data = new HashMap<>();
//		if(uCode.getCode() > 1990){
//			uCode.setCode(1990);
//			uCode.setMaxLocatePoint(9.5);
//			uCode.setMaxLocatePointCode(1990);
//			uCode.setMaxNotLocatePoint(9.5);
//		}
		data.put("uQuota", uQuota);
		data.put("uCode", uCode);
		data.put("uBean", uBean);
		json.data("data", data);
		json.set(0, "0-1");
		return json.toJson();
	}

	@RequestMapping(value = WUC.PROXY_USER_SEARCH, method = { RequestMethod.POST })
	@ResponseBody
	public Map<String, Object> PROXY_USER_SEARCH(HttpSession session, HttpServletRequest request) {
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

		String username = HttpUtil.getStringParameterTrim(request, "username");
		Double minMoney = HttpUtil.getDoubleParameter(request, "minMoney");
		Double maxMoney = HttpUtil.getDoubleParameter(request, "maxMoney");
		int start = HttpUtil.getIntParameter(request, "start");
		int limit = HttpUtil.getIntParameter(request, "limit");
		Integer scope = HttpUtil.getIntParameter(request, "scope"); // 1：会员；2：团队；会员搜索会员本身的，团队搜索用户团队的数据
		if (scope == null) {
			scope = 1;
		}

		User user; // 目标用户
		if (StringUtils.isNotEmpty(username) && !StringUtils.equals(username, uEntity.getUsername())) {
			user = uReadService.getByUsernameFromRead(username); // 查询指定用户
			if (uProxyValidate.isLowerUser(uEntity, user) || uProxyValidate.isRelated(uEntity, user)) {
				// 合法用户
			}
			else {
				user = null;
			}
		}
		else {
			user = uEntity; // 查询自己
		}

		// 层级用户
		List<String> userLevels = uCodePointUtil.getUserLevels(uEntity, user);
		json.data("userLevels", userLevels);

		if (user == null) { // 目标用户为空
			json.data("data", new ArrayList<>());
			json.data("totalCount", 0);
		}
		else {
			PageList pList = new PageList();
			if (user.getUsername().equals(uEntity.getUsername())) {
				if (uEntity.getType() == Global.USER_TYPE_RELATED) {
					// 关联账号查询
					Integer[] relatedUserIds = ArrayUtils.transGetIdsToInteger(uEntity.getRelatedUsers());
					if (relatedUserIds != null && relatedUserIds.length > 0) {
						pList = uReadService.searchByUserIds(relatedUserIds, minMoney, maxMoney, start, limit);
					}
				}
				else {
					// 查询用户直属团队数据
					pList = uReadService.searchByDirectTeam(user.getId(), minMoney, maxMoney, start, limit);
				}
			}
			else {
				if (scope == 1) {
					// 查询指定用户的数据
					pList = uReadService.searchByUserId(user.getId());
				}
				else if (scope == 2) {
					// 查询用户团队数据
					pList = uReadService.searchByTeam(user.getId(), minMoney, maxMoney, start, limit);
				}
				else {
					// 查询用户直属团队数据
					pList = uReadService.searchByDirectTeam(user.getId(), minMoney, maxMoney, start, limit);
				}
			}

			json.data("data", pList.getList());
			json.data("totalCount", pList.getCount());
		}
		json.set(0, "0-1");
		return json.toJson();

		// WebJSON json = new WebJSON(dataFactory);
		// SessionUser sessionUser = super.getSessionUser(json, session, request);
		// if (sessionUser == null) {
		// 	return json.toJson();
		// }
		//
		// User uEntity = uReadService.getByIdFromRead(sessionUser.getId());
		// if (uEntity == null) {
		// 	super.logOut(session, request);
		// 	json.set(2, "2-10"); // 您的账户信息发生变更，请重新登录
		// 	return json.toJson();
		// }
		//
		// String username = HttpUtil.getStringParameterTrim(request, "username");
		// Double minMoney = HttpUtil.getDoubleParameter(request, "minMoney");
		// Double maxMoney = HttpUtil.getDoubleParameter(request, "maxMoney");
		// int start = HttpUtil.getIntParameter(request, "start");
		// int limit = HttpUtil.getIntParameter(request, "limit");
		// Integer scope = HttpUtil.getIntParameter(request, "scope"); // 1：会员；2：团队；会员搜索会员本身的，团队搜索用户团队的数据
		// PageList pList = new PageList();
		//
		// List<String> userLevels = new LinkedList<>();
		//
		// // 如果没有输入用户名或输入的用户名是会员自己，那么查询其直属团队
		// if(StringUtil.isNotNull(username)) {
		// 	if(username.equals(uEntity.getUsername())){
		// 		pList = uReadService.teamDirectSearch(uEntity.getId(), minMoney, maxMoney, start, limit);
		// 		userLevels = uCodePointUtil.getUserLevels(uEntity, uEntity);
		// 	}else{
		// 		User targetUser = uReadService.getByUsernameFromRead(username);
		// 		if(targetUser != null) {
		// 			if(uProxyValidate.isLowerUser(uEntity, targetUser)) {
		// 				pList = uReadService.teamDirectSearch(targetUser.getId(),  minMoney, maxMoney, start, limit);
		// 				userLevels = uCodePointUtil.getUserLevels(uEntity, targetUser);
		// 			}
		// 			else if(uProxyValidate.isRelatedUser(uEntity.getId(), targetUser)) {
		// 				pList = uReadService.teamDirectSearch(targetUser.getId(),  minMoney, maxMoney, start, limit);
		// 				userLevels = uCodePointUtil.getUserLevels(uEntity, targetUser);
		// 			}
		// 			// if(uProxyValidate.isLowerUser(uEntity, targetUser) || uProxyValidate.isRelatedUser(uEntity.getId(), targetUser)) {
		// 			// 	pList = uReadService.teamDirectSearch(targetUser.getId(),  minMoney, maxMoney, start, limit);
		// 			// 	userLevels = uCodePointUtil.getUserLevels(uEntity, targetUser);
		// 			// }
		// 		}
		// 	}
		// }
		// else {
		// 	pList = uReadService.teamDirectSearch(uEntity.getId(), minMoney, maxMoney, start, limit);
		// 	userLevels = uCodePointUtil.getUserLevels(uEntity, uEntity);
		// }
		//
		// json.data("userLevels", userLevels);
		// json.data("data", pList.getList());
		// json.data("totalCount", pList.getCount());
		// json.set(0, "0-1");
		// return json.toJson();
	}

	@RequestMapping(value = WUC.PROXY_ONLINE_LIST, method = { RequestMethod.POST })
	@ResponseBody
	public Map<String, Object> PROXY_ONLINE_LIST(HttpSession session, HttpServletRequest request) {
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

		int start = HttpUtil.getIntParameter(request, "start");
		int limit = HttpUtil.getIntParameter(request, "limit");
		PageList pList = new PageList();

		if (uEntity.getType() == Global.USER_TYPE_RELATED) {
			// 关联账号查询
			Integer[] relatedUserIds = ArrayUtils.transGetIdsToInteger(uEntity.getRelatedUsers());
			if (relatedUserIds != null && relatedUserIds.length > 0) {
				pList = uReadService.teamOnlineList(sessionUser.getId(), start, limit);
			}
		}
		else {
			pList = uReadService.teamOnlineList(sessionUser.getId(), start, limit);
		}

		json.data("data", pList.getList());
		json.data("totalCount", pList.getCount());
		json.set(0, "0-1");
		return json.toJson();
	}

	@RequestMapping(value = WUC.PROXY_ORDER_SEARCH, method = { RequestMethod.POST })
	@ResponseBody
	public Map<String, Object> PROXY_ORDER_SEARCH(HttpSession session, HttpServletRequest request) {
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

		Integer lotteryId = HttpUtil.getIntParameter(request, "lotteryId");
		Integer status = HttpUtil.getIntParameter(request, "status");
		String expect = HttpUtil.getStringParameterTrim(request, "expect");
		String sTime = HttpUtil.getStringParameterTrim(request, "sTime");
		String eTime = HttpUtil.getStringParameterTrim(request, "eTime");
		int start = HttpUtil.getIntParameter(request, "start");
		int limit = HttpUtil.getIntParameter(request, "limit");
		Integer scope = HttpUtil.getIntParameter(request, "scope"); // 1：会员；2：团队；会员搜索会员本身的，团队搜索用户团队的数据
		String username = HttpUtil.getStringParameterTrim(request, "username");
		if (scope == null) {
			scope = 1;
		}

		User user; // 目标用户
		if (StringUtils.isNotEmpty(username) && !StringUtils.equals(username, uEntity.getUsername())) {
			user = uReadService.getByUsernameFromRead(username); // 查询指定用户
			if (uProxyValidate.isLowerUser(uEntity, user) || uProxyValidate.isRelated(uEntity, user)) {
				// 合法用户
			}
			else {
				user = null;
			}
		}
		else {
			user = uEntity; // 查询自己
		}

		if (scope == null) {
			scope = 1;
		}

		// 层级用户
		List<String> userLevels = uCodePointUtil.getUserLevels(uEntity, user);
		json.data("userLevels", userLevels);

		if (user == null) { // 目标用户为空
			json.data("data", new ArrayList<>());
			json.data("totalCount", 0);
		}
		else {
			PageList pList = new PageList();
			if (user.getUsername().equals(uEntity.getUsername())) {
				if (user.getId() == Global.USER_TOP_ID) {
					// 总账查询所有数据
					pList = uBetsReadService.searchAll(null, lotteryId, expect, status, sTime, eTime, start, limit);
				}
				else {
					if (uEntity.getType() == Global.USER_TYPE_RELATED) {
						// 关联账号查询
						Integer[] relatedUserIds = ArrayUtils.transGetIdsToInteger(uEntity.getRelatedUsers());
						if (relatedUserIds != null && relatedUserIds.length > 0) {
							pList = uBetsReadService.searchByTeam(relatedUserIds, null, lotteryId, expect, status, sTime, eTime, start, limit);
						}
					}
					else {
						// 查询用户团队数据
						pList = uBetsReadService.searchByTeam(user.getId(), null, lotteryId, expect, status, sTime, eTime, start, limit);
					}
				}
			}
			else {
				if (scope == 1) {
					// 查询指定用户的数据
					pList = uBetsReadService.searchByUserId(user.getId(), null, lotteryId, expect, status, sTime, eTime, start, limit);
				}
				else {
					// 查询用户团队数据
					pList = uBetsReadService.searchByTeam(user.getId(), null, lotteryId, expect, status, sTime, eTime, start, limit);
				}
			}

			json.data("data", pList.getList());
			json.data("totalCount", pList.getCount());
		}
		json.set(0, "0-1");
		return json.toJson();
	}

	@RequestMapping(value = WUC.PROXY_GAME_ORDER_SEARCH, method = { RequestMethod.POST })
	@ResponseBody
	public Map<String, Object> PROXY_GAME_ORDER_SEARCH(HttpSession session, HttpServletRequest request) {
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

		Integer platformId = HttpUtil.getIntParameter(request, "platformId");
		String sTime = HttpUtil.getStringParameterTrim(request, "sTime");
		String eTime = HttpUtil.getStringParameterTrim(request, "eTime");
		int start = HttpUtil.getIntParameter(request, "start");
		int limit = HttpUtil.getIntParameter(request, "limit");
		Integer scope = HttpUtil.getIntParameter(request, "scope"); // 1：会员；2：团队；会员搜索会员本身的，团队搜索用户团队的数据
		String username = HttpUtil.getStringParameterTrim(request, "username");
		if (scope == null) {
			scope = 1;
		}

		User user; // 目标用户
		if (StringUtils.isNotEmpty(username) && !StringUtils.equals(username, uEntity.getUsername())) {
			user = uReadService.getByUsernameFromRead(username); // 查询指定用户
			if (uProxyValidate.isLowerUser(uEntity, user) || uProxyValidate.isRelated(uEntity, user)) {
				// 合法用户
			}
			else {
				user = null;
			}
		}
		else {
			user = uEntity; // 查询自己
		}

		// type1:会员本身数据,type2：会员团队数据
		if (scope == null || (scope != 1 && scope != 2)) {
			scope = 1;
		}

		// 层级用户
		List<String> userLevels = uCodePointUtil.getUserLevels(uEntity, user);
		json.data("userLevels", userLevels);

		if (user == null) { // 目标用户为空
			json.data("data", new ArrayList<>());
			json.data("totalCount", 0);
		}
		else {
			PageList pList = new PageList();
			if (user.getUsername().equals(uEntity.getUsername())) {
				if (user.getId() == Global.USER_TOP_ID) {
					// 查询用户团队数据
					pList = gameBetsReadService.searchAll(platformId, sTime, eTime, start, limit);
				}
				else {
					if (uEntity.getType() == Global.USER_TYPE_RELATED) {
						// 关联账号查询
						Integer[] relatedUserIds = ArrayUtils.transGetIdsToInteger(uEntity.getRelatedUsers());
						if (relatedUserIds != null && relatedUserIds.length > 0) {
							pList = gameBetsReadService.searchByTeam(relatedUserIds, platformId, sTime, eTime, start, limit);
						}
					}
					else {
						// 查询用户团队数据
						pList = gameBetsReadService.searchByTeam(user.getId(), platformId, sTime, eTime, start, limit);
					}
				}
			}
			else {
				if (scope == 1) {
					pList = gameBetsReadService.searchByUserId(user.getId(), platformId, sTime, eTime, start, limit);
				}
				else {
					pList = gameBetsReadService.searchByTeam(user.getId(), platformId, sTime, eTime, start, limit);
				}
			}
			json.data("data", pList.getList());
			json.data("totalCount", pList.getCount());
		}
		json.set(0, "0-1");
		return json.toJson();
	}

	@RequestMapping(value = WUC.PROXY_BILL_SEARCH, method = { RequestMethod.POST })
	@ResponseBody
	public Map<String, Object> PROXY_BILL_SEARCH(HttpSession session, HttpServletRequest request) {
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

		Integer account = HttpUtil.getIntParameter(request, "account");
		Integer type = HttpUtil.getIntParameter(request, "type");
		String sTime = HttpUtil.getStringParameterTrim(request, "sTime");
		String eTime = HttpUtil.getStringParameterTrim(request, "eTime");
		int start = HttpUtil.getIntParameter(request, "start");
		int limit = HttpUtil.getIntParameter(request, "limit");
		Integer scope = HttpUtil.getIntParameter(request, "scope");
		String username = HttpUtil.getStringParameterTrim(request, "username");

		User user; // 目标用户
		if (StringUtils.isNotEmpty(username) && !StringUtils.equals(username, uEntity.getUsername())) {
			user = uReadService.getByUsernameFromRead(username); // 查询指定用户
			if (uProxyValidate.isLowerUser(uEntity, user) || uProxyValidate.isRelated(uEntity, user)) {
				// 合法用户
			}
			else {
				user = null;
			}
		}
		else {
			user = uEntity; // 查询自己
		}

		// type1:会员本身数据,type2：会员团队数据
		if (scope == null || (scope != 1 && scope != 2)) {
			scope = 1;
		}

		// 层级用户
		List<String> userLevels = uCodePointUtil.getUserLevels(uEntity, user);
		json.data("userLevels", userLevels);

		if (user == null) { // 目标用户为空
			json.data("data", new ArrayList<>());
			json.data("totalCount", 0);
		}
		else {
			PageList pList = new PageList();
			if (user.getUsername().equals(uEntity.getUsername())) {
				if (user.getId() == Global.USER_TOP_ID) {
					// 查询用户团队数据
					pList = uBillReadService.searchAll(account, type, sTime, eTime, start, limit);
				}
				else {
					if (uEntity.getType() == Global.USER_TYPE_RELATED) {
						// 关联账号查询
						Integer[] relatedUserIds = ArrayUtils.transGetIdsToInteger(uEntity.getRelatedUsers());
						if (relatedUserIds != null && relatedUserIds.length > 0) {
							pList = uBillReadService.searchByTeam(relatedUserIds, account, type, sTime, eTime, start, limit);
						}
					}
					else {
						// 查询用户团队数据
						pList = uBillReadService.searchByTeam(user.getId(), account, type, sTime, eTime, start, limit);
					}

					// // 查询用户团队数据
					// pList = uBillReadService.searchByTeam(user.getId(), account, type, sTime, eTime, start, limit);
				}
			}
			else {
				if (scope == 1) {
					pList = uBillReadService.searchByUserId(user.getId(), account, type, sTime, eTime, start, limit);
				}
				else {
					pList = uBillReadService.searchByTeam(user.getId(), account, type, sTime, eTime, start, limit);
				}
			}
			json.data("data", pList.getList());
			json.data("totalCount", pList.getCount());
		}
		json.set(0, "0-1");
		return json.toJson();
	}

	@RequestMapping(value = WUC.DAILY_SETTLE_BILL_DETAILS, method = { RequestMethod.POST })
	@ResponseBody
	public Map<String, Object> DAILY_SETTLE_BILL_DETAILS(HttpSession session, HttpServletRequest request) {
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
		UserDailySettleBill bill = uDailySettleBillReadService.getById(id);
		boolean isShow = false;
		if(bill != null) {
			// 自己的数据
			if(bill.getUserId() == sessionUser.getId()) {
				isShow = true;
			} else {
				User targetUser = uReadService.getByIdFromRead(bill.getUserId());
				// 下级的数据
				if(uProxyValidate.isLowerUser(uEntity, targetUser) || uProxyValidate.isRelated(uEntity, targetUser)) {
					isShow = true;
				}
			}
		}
		if(isShow) {
			UserDailySettleBillVO data = new UserDailySettleBillVO(bill, dataFactory);
			json.data("data", data);
			json.set(0, "0-1");
		} else {
			json.set(2, "2-1038");
		}
		return json.toJson();
	}

	@RequestMapping(value = WUC.PROXY_EDIT_QUOTA_LOAD, method = { RequestMethod.POST })
	@ResponseBody
	public Map<String, Object> PROXY_EDIT_QUOTA_LOAD(HttpSession session, HttpServletRequest request) {
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

		String username = HttpUtil.getStringParameterTrim(request, "username");
		User lowerUser = uReadService.getByUsernameFromRead(username);
		if(lowerUser != null) {
			if(uProxyValidate.isDirectLower(uEntity, lowerUser)) {
				// 我的信息
				List<UserCodeQuotaVO> uQuota = uCodePointUtil.listUserQuota(uEntity);
				// 下级信息
				List<UserCodeQuotaVO> lQuota = uCodePointUtil.listUserQuota(lowerUser);
				UserBaseVO lBean = new UserBaseVO(lowerUser);
				Map<String, Object> data = new HashMap<>();
				data.put("uQuota", uQuota);
				data.put("lQuota", lQuota);
				data.put("lBean", lBean);
				json.data("data", data);
				json.set(0, "0-1");
			} else {
				json.set(2, "2-1034");
			}
		} else {
			json.set(2, "2-1003");
		}
		return json.toJson();
	}

	@RequestMapping(value = WUC.PROXY_EDIT_USER_QUOTA, method = { RequestMethod.POST })
	@ResponseBody
	public Map<String, Object> PROXY_EDIT_USER_QUOTA(HttpSession session, HttpServletRequest request) {
		WebJSON json = new WebJSON(dataFactory);
		return json.toJson();
// 		WebJSON json = new WebJSON(dataFactory);
// 		SessionUser sessionUser = super.getSessionUser(json, session, request);
// 		if (sessionUser == null) {
// 			return json.toJson();
// 		}
//
// 		User uEntity = uReadService.getByIdFromRead(sessionUser.getId());
// 		if (uEntity == null) {
// 			super.logOut(session, request);
// 			json.set(2, "2-10"); // 您的账户信息发生变更，请重新登录
// 			return json.toJson();
// 		}
//
// 		String add1 = HttpUtil.getStringParameterTrim(request, "add1");
// 		if(StringUtils.isEmpty(add1)){
// 			add1 = "0";
// 		}
// 		String sub1 = HttpUtil.getStringParameterTrim(request, "sub1");
// 		if(StringUtils.isEmpty(sub1)){
// 			sub1 = "0";
// 		}
// 		String add2 = HttpUtil.getStringParameterTrim(request, "add2");
// 		if(StringUtils.isEmpty(add2)){
// 			add2 = "0";
// 		}
// 		String sub2 = HttpUtil.getStringParameterTrim(request, "sub2");
// 		if(StringUtils.isEmpty(sub2)){
// 			sub2 = "0";
// 		}
// 		String add3 = HttpUtil.getStringParameterTrim(request, "add3");
// 		if(StringUtils.isEmpty(add3)){
// 			add3 = "0";
// 		}
// 		String sub3 = HttpUtil.getStringParameterTrim(request, "sub3");
// 		if(StringUtils.isEmpty(sub3)){
// 			sub3 = "0";
// 		}
// 		int amount1 = Integer.parseInt(add1) - Integer.parseInt(sub1);
// 		int amount2 = Integer.parseInt(add2) - Integer.parseInt(sub2);
// 		int amount3 = Integer.parseInt(add3) - Integer.parseInt(sub3);
//
//
// 		String username = HttpUtil.getStringParameterTrim(request, "username");
// //			int amount1 = HttpUtil.getIntParameter(request, "amount1");
// //			int amount2 = HttpUtil.getIntParameter(request, "amount2");
// //			int amount3 = HttpUtil.getIntParameter(request, "amount3");
// 		User lowerUser = uReadService.getByUsernameFromRead(username);
// 		if(lowerUser != null) {
// 			if(uProxyValidate.isDirectLower(uEntity, lowerUser)) {
// 				int[] amount = new int [] {amount1, amount2, amount3};
// 				UserCodeQuota oldQuota = uCodeQuotaService.get(lowerUser.getId(), lowerUser.getCode());
// 				if(uProxyValidate.testEditLowerQuota(json, uEntity, lowerUser, amount)) {
// 					boolean result = uCodeQuotaService.updateAmount(lowerUser.getId(), amount1, amount2, amount3);
// 					if(result) {
// 						String ip = HttpUtil.getRealIp(request);
// 						uActionLogService.editUserQuota(uEntity.getId(), ip, lowerUser.getUsername(), oldQuota, amount1, amount2, amount3);
// 						json.set(0, "0-1");
// 					} else {
// 						json.set(1, "1-1");
// 					}
// 				}
// 			} else {
// 				json.set(2, "2-1034");
// 			}
// 		} else {
// 			json.set(2, "2-1003");
// 		}
// 		return json.toJson();
	}

	@RequestMapping(value = WUC.PROXY_EDIT_POINT_LOAD, method = { RequestMethod.POST })
	@ResponseBody
	public Map<String, Object> PROXY_EDIT_POINT_LOAD(HttpSession session, HttpServletRequest request) {
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

		String username = HttpUtil.getStringParameterTrim(request, "username");
		User lowerUser = uReadService.getByUsernameFromRead(username);
		if(lowerUser != null) {
			if(uProxyValidate.isDirectLower(uEntity, lowerUser)) {
				List<UserCodeQuotaVO> uQuota = uCodePointUtil.listUserQuota(uEntity);
				UserCodeRangeVO uCode = uCodePointUtil.getUserCodeRange(uEntity);
				UserBaseVO uBean = new UserBaseVO(uEntity);
				UserBaseVO lBean = new UserBaseVO(lowerUser);
				Map<String, Object> data = new HashMap<>();
//				if(uCode.getCode() > 1990){
//					uCode.setCode(1990);
//					uCode.setMaxLocatePoint(9.5);
//					uCode.setMaxLocatePointCode(1990);
//					uCode.setMaxNotLocatePoint(9.5);
//				}
				data.put("uQuota", uQuota);
				data.put("uCode", uCode);
				data.put("uBean", uBean);
				data.put("lBean", lBean);
				json.data("data", data);
				json.set(0, "0-1");
			} else {
				json.set(2, "2-1034");
			}
		} else {
			json.set(2, "2-1003");
		}
		return json.toJson();
	}

	@RequestMapping(value = WUC.PROXY_EDIT_USER_POINT, method = { RequestMethod.POST })
	@ResponseBody
	public Map<String, Object> PROXY_EDIT_USER_POINT(HttpSession session, HttpServletRequest request) {
		WebJSON json = new WebJSON(dataFactory);
		SessionUser sessionUser = super.getSessionUser(json, session, request);
		if (sessionUser == null) {
			return json.toJson();
		}

		String ip = HttpUtil.getRealIp(json, request);
		if (ip == null) {
			return json.toJson();
		}

		User uEntity = uReadService.getByIdFromRead(sessionUser.getId());
		if (uEntity == null) {
			super.logOut(session, request);
			json.set(2, "2-10"); // 您的账户信息发生变更，请重新登录
			return json.toJson();
		}

		String username = HttpUtil.getStringParameterTrim(request, "username");
		int code = HttpUtil.getIntParameter(request, "code");

		// 不允许创建1990以上等级
//		if (code > 1990) {
//			json.set(2, "2-2022");
//			return json.toJson();
//		}
		
		// 不允许创建单号等级
		if (code % 2 != 0) {
			json.set(2, "2-2022");
			return json.toJson();
		}
		if (code > dataFactory.getCodeConfig().getSysCode() || code < 1800) {
			json.set(2, "2-2022");
			return json.toJson();
		}

		double locatePoint = uCodePointUtil.getLocatePoint(code); // 一定要格式化成1位小数
		double notLocatePoint = uCodePointUtil.getNotLocatePoint(code);

		User lowerUser = uReadService.getByUsernameFromRead(username);
		if(lowerUser != null) {
			if(uProxyValidate.isDirectLower(uEntity, lowerUser)) {
				// 返点必须大于自身返点
				if(locatePoint > lowerUser.getLocatePoint()) {
					if(uProxyValidate.testLowerPoint(json, uEntity, lowerUser.getType(), locatePoint)) {

						// // 配额验证
						// if(!uCodePointUtil.hasQuota(uEntity, code)) {
						// 	json.set(2, "2-2011");
						// 	return json.toJson();
						// }

						// // 按量升点
						// if (!uCodePointUtil.checkCodeAmount(json, lowerUser.getCode(), code, lowerUser.getId())) {
						// 	return json.toJson();
						// }

						int BStatus = lowerUser.getBStatus();
						if(code <= dataFactory.getLotteryConfig().getNotBetPointAccount()){
							BStatus = 0;
						}else{
							BStatus = -1;
						}
						
						boolean result = uService.updateLotteryPoint(lowerUser.getId(), code, locatePoint, notLocatePoint, 0, BStatus);
						if(result) {
							// //只要返点出现变化，就重置代理的状态
							// int BStatus = 0;
							// int allowEqualCode = -1; // 默认不允许同级开号
							// if(uValidate.allowEqualCode(code)){
							// 	allowEqualCode = 1;
							// }
							// //修改用户投注和统计开号权限d
							// uService.updateBstatusAndAllowEqualCode(BStatus,allowEqualCode,lowerUser.getId());
							uActionLogService.editUserPoint(uEntity.getId(), ip, lowerUser.getUsername(), lowerUser.getLocatePoint(), locatePoint);
							json.set(0, "0-1");
						} else {
							json.set(1, "1-1");
						}
					}
				} else {
					json.set(2, "2-2020");
				}
			} else {
				json.set(2, "2-1034");
			}
		} else {
			json.set(2, "2-1003");
		}
		return json.toJson();
		// WebJSON json = new WebJSON(dataFactory);
		// SessionUser sessionUser = super.getSessionUser(json, session, request);
		// if (sessionUser == null) {
		// 	return json.toJson();
		// }
		//
		// User uEntity = uReadService.getByIdFromRead(sessionUser.getId());
		// if (uEntity == null) {
		// 	super.logOut(session, request);
		// 	json.set(2, "2-10"); // 您的账户信息发生变更，请重新登录
		// 	return json.toJson();
		// }
		//
		// String username = HttpUtil.getStringParameterTrim(request, "username");
		// int code = HttpUtil.getIntParameter(request, "code");
		//
		// // 不允许创建1958等级
		// if (code == 1958) {
		// 	json.set(2, "2-2022");
		// 	return json.toJson();
		// }
		// if (code > dataFactory.getCodeConfig().getSysCode()) {
		// 	json.set(2, "2-2022");
		// 	return json.toJson();
		// }
		//
		// double locatePoint = uCodePointUtil.getLocatePoint(code); // 一定要格式化成1位小数
		// double notLocatePoint = uCodePointUtil.getNotLocatePoint(code);
		//
		// User lowerUser = uReadService.getByUsernameFromRead(username);
		// if(lowerUser != null) {
		// 	if(uProxyValidate.isDirectLower(uEntity, lowerUser)) {
		// 		// 返点必须大于自身返点
		// 		if(locatePoint > lowerUser.getLocatePoint()) {
		// 			if(uProxyValidate.testLowerPoint(json, uEntity, lowerUser.getType(), locatePoint)) {
		//
		// 				List<UserCodeQuotaVO> surplusQuota = uCodePointUtil.listSurplusQuota(uEntity);
		// 				for (UserCodeQuotaVO tmpQuota : surplusQuota) {
		// 					if(locatePoint >= tmpQuota.getMinPoint() && locatePoint <= tmpQuota.getMaxPoint()) {
		// 						// 配额不足
		// 						if(tmpQuota.getSurplusCount() < 1) {
		// 							json.set(2, "2-2011");
		// 							return json.toJson();
		// 						}
		// 					}
		// 				}
		// 				boolean result = uService.updateLotteryPoint(lowerUser.getId(), code, locatePoint, notLocatePoint, 0);
		// 				if(result) {
		// 					//只要返点出现变化，就重置代理的状态
		// 					int BStatus = 0;
		// 					int allowEqualCode = -1; // 默认不允许同级开号
		// 					if(uValidate.allowEqualCode(code)){
		// 						allowEqualCode = 1;
		// 					}
		// 					//修改用户投注和统计开号权限d
		// 					uService.updateBstatusAndAllowEqualCode(BStatus,allowEqualCode,lowerUser.getId());
		// 					String ip = HttpUtil.getRealIp(request);
		// 					uActionLogService.editUserPoint(uEntity.getId(), ip, lowerUser.getUsername(), lowerUser.getLocatePoint(), locatePoint);
		// 					json.set(0, "0-1");
		// 				} else {
		// 					json.set(1, "1-1");
		// 				}
		// 			}
		// 		} else {
		// 			json.set(2, "2-2020");
		// 		}
		// 	} else {
		// 		json.set(2, "2-1034");
		// 	}
		// } else {
		// 	json.set(2, "2-1003");
		// }
		// return json.toJson();
	}

	@RequestMapping(value = WUC.PROXY_LOAD_RECHARGE, method = { RequestMethod.POST })
	@ResponseBody
	public Map<String, Object> PROXY_LOAD_RECHARGE(HttpSession session, HttpServletRequest request) {
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

		if (uEntity.getType() == Global.USER_TYPE_RELATED) {
			json.set(2, "2-13"); // 您的账号无法使用该功能！
			return json.toJson();
		}

		String username = HttpUtil.getStringParameterTrim(request, "username");
		User lowerUser = uReadService.getByUsernameFromRead(username);
		if(lowerUser != null) {
			if(uProxyValidate.isLowerUser(uEntity, lowerUser)) {

				UserSecurityVO sBean = uSecurityService.getRandomByUserId(uEntity.getId());
				boolean hasSecurity = sBean != null ? true : false;
				boolean hasWithdrawPwd = StringUtil.isNotNull(uEntity.getWithdrawPassword()) ? true : false;
				boolean allowTransfers = uEntity.getAllowTransfers() == 1;
				boolean hasBindGoogle = uEntity.getIsBindGoogle() == 1 && StringUtil.isNotNull(uEntity.getSecretKey());

				Map<String, Object> data = new HashMap<>();
				data.put("sBean", sBean);
				data.put("availableMoney", uEntity.getTotalMoney());
				data.put("hasSecurity", hasSecurity);
				data.put("hasWithdrawPwd", hasWithdrawPwd);
				data.put("allowTransfers", allowTransfers);
				data.put("hasBindGoogle", hasBindGoogle);
				json.data("data", data);
				json.set(0, "0-1");
			} else {
				json.set(2, "2-1035");
			}
		} else {
			json.set(2, "2-1003");
		}
		return json.toJson();
	}

	@RequestMapping(value = WUC.PROXY_RECHARGE_USER, method = { RequestMethod.POST })
	@ResponseBody
	public Map<String, Object> PROXY_RECHARGE_USER(HttpSession session, HttpServletRequest request) {
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

		User uEntity = uReadService.getByIdFromRead(sessionUser.getId());
		if (uEntity == null) {
			super.logOut(session, request);
			json.set(2, "2-10"); // 您的账户信息发生变更，请重新登录
			return json.toJson();
		}

		if (uEntity.getType() == Global.USER_TYPE_RELATED) {
			json.set(2, "2-13"); // 您的账号无法使用该功能！
			return json.toJson();
		}
		if (uEntity.getType() == Global.USER_TYPE_FICTITIOUS) {
			json.set(2, "2-13"); // 您的账号无法使用该功能！
			return json.toJson();
		}
		
		if (uEntity.getType() == Global.USER_TYPE_PLAYER && uEntity.getNickname().equals("试玩用户")) {
			json.set(2, "2-13"); // 您的账号无法使用该功能！
			return json.toJson();
		}
		
		
		if(uWithdrawValidate.validateLockTime(json, uEntity)) {
			String username = HttpUtil.getStringParameterTrim(request, "username");
			double amount = HttpUtil.getDoubleParameter(request, "amount");
			int sid = HttpUtil.getIntParameter(request, "sid");
			String answer = request.getParameter("answer"); // MD5大写(MD5大写(MD5大写(MD5大写(密码明文))) + token)
			String withdrawPwd = request.getParameter("withdrawPwd"); // MD5大写(MD5大写(MD5大写(MD5大写(密码明文))) + token)
			int transferType = HttpUtil.getIntParameter(request, "transferType");


			if (StringUtils.isEmpty(answer)) {
				json.set(2, "2-1010");
				return json.toJson();
			}

			String disposableToken = getDisposableToken(session, request); // 获取并删除一次性token
			if (StringUtils.isEmpty(disposableToken)) {
				json.set(2, "2-15"); // 您的请求已超时，请重新提交 一次性验证token没有了，说明session失效
				return json.toJson();
			}

			User lowerUser = uReadService.getByUsernameFromRead(username);
			if(lowerUser != null) {
				if(uProxyValidate.isLowerUser(uEntity, lowerUser)) {
					if(uWithdrawValidate.validateToUser(json, uEntity, amount)) {
						if(uSecurityService.validateSecurity(sid, uEntity.getId(), disposableToken, answer)) {
							if(PasswordUtil.validatePassword(uEntity.getWithdrawPassword(), disposableToken, withdrawPwd)) {
							
						/*			int verificationCode = HttpUtil.getIntParameter(request, "vCode");
									boolean authorize = uService.authoriseUser(uEntity.getId(), verificationCode);
									if (!authorize) {
										json.set(2, "2-4021");
									}
									else {*/
										// 验证是否有未发放的分红金额
										if (!uDividendValidate.validateUnIssue(json, uEntity.getId())) {
											return json.toJson();
										}
										// 验证是否有未发放的日结金额
										if (!uDailySettleValidate.validateUnIssue(json, uEntity.getId())) {
											return json.toJson();
										}

										boolean result = uTransfersService.transfersToUser(lowerUser.getId(), uEntity.getId(), amount,transferType);
										if(result) {
											uActionLogService.transToUser(uEntity.getId(), ip, lowerUser.getUsername(), amount);
											json.set(0, "0-1");
										} else {
											json.set(1, "1-1");
										}
									//}
							
							} else {
								json.set(2, "2-1009");
							}
						} else {
							json.set(2, "2-1010");
						}
					}
				} else {
					json.set(2, "2-1035");
				}
			} else {
				json.set(2, "2-1003");
			}
		}
		return json.toJson();
	}

	@RequestMapping(value = WUC.PROXY_ORDER_DETAILS, method = { RequestMethod.POST })
	@ResponseBody
	public Map<String, Object> PROXY_ORDER_DETAILS(HttpSession session, HttpServletRequest request) {
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
				// 下级的订单
				if(uProxyValidate.isLowerUser(uEntity, targetUser) || uProxyValidate.isRelated(uEntity, targetUser)) {
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




	@RequestMapping(value = WUC.DAILY_SETTLE_SEARCH, method = { RequestMethod.POST })
	@ResponseBody
	public Map<String, Object> DAILY_SETTLE_SEARCH(HttpSession session, HttpServletRequest request) {
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

		String username = HttpUtil.getStringParameterTrim(request, "username");
		if (username != null) {
			username = username.trim();
		}
		int start = HttpUtil.getIntParameter(request, "start");
		int limit = HttpUtil.getIntParameter(request, "limit");
		Integer scope = HttpUtil.getIntParameter(request, "scope"); // 1：会员；2：团队；会员搜索会员本身的，团队搜索用户团队的数据
		if (scope == null) {
			scope = 1;
		}

		User user; // 目标用户
		if (StringUtils.isNotEmpty(username) && !StringUtils.equals(username, uEntity.getUsername())) {
			user = uReadService.getByUsernameFromRead(username); // 查询指定用户
			if (uProxyValidate.isLowerUser(uEntity, user) || uProxyValidate.isRelated(uEntity, user)) {
				// 合法用户
			}
			else {
				user = null;
			}
		}
		else {
			user = uEntity; // 查询自己
		}

		// 层级用户
		List<String> userLevels = uCodePointUtil.getUserLevels(uEntity, user);
		json.data("userLevels", userLevels);

		if (user == null) { // 目标用户为空
			json.data("data", new ArrayList<>());
			json.data("username", uEntity.getUsername());
			json.data("totalCount", 0);
		}
		else {
			PageList pList = new PageList();
			if (user.getUsername().equals(uEntity.getUsername())) {
				if (user.getId() == Global.USER_TOP_ID) {
					// 总账查询所有数据
					pList = uDailySettleReadService.searchAll(start, limit);
				}
				else {
					if (uEntity.getType() == Global.USER_TYPE_RELATED) {
						// 关联账号查询
						int[] relatedUserIds = ArrayUtils.transGetIds(uEntity.getRelatedUsers());
						if (relatedUserIds != null && relatedUserIds.length > 0) {
							pList = uDailySettleReadService.searchByTeam(relatedUserIds, start, limit);
						}
					}
					else {
						// 查询用户团队数据
						pList = uDailySettleReadService.searchByTeam(user.getId(), start, limit);
					}

					// // 查询用户团队数据
					// pList = uDailySettleReadService.searchByTeam(user.getId(), start, limit);
				}
			}
			else {
				if (scope == 1) {
					// 查询指定用户的数据
					pList = uDailySettleReadService.searchByUserId(user.getId(), start, limit);
				}
				else {
					// 查询用户团队数据
					pList = uDailySettleReadService.searchByTeam(user.getId(), start, limit);
				}
			}
			json.data("data", pList.getList());
			json.data("username", uEntity.getUsername());
			json.data("totalCount", pList.getCount());

			// PageList pList;
			// // 如果是总账号来查看，并且没有输入用户名，那么查看所有数据
			// if (uEntity.getId() == Global.USER_TOP_ID && user.getId() == uEntity.getId()) {
			// 	pList = uDailySettleReadService.searchAll(start, limit);
			// }
			// // 否则就查询指定用户整个团队的
			// else {
			// 	pList = uDailySettleReadService.searchByTeam(user.getId(), start, limit);
			// }
			// json.data("data", pList.getList());
			// json.data("username", uEntity.getUsername());
			// json.data("totalCount", pList.getCount());
		}
		json.set(0, "0-1");
		return json.toJson();
	}

	@RequestMapping(value = WUC.DAILY_SETTLE_BILL_SEARCH, method = { RequestMethod.POST })
	@ResponseBody
	public Map<String, Object> DAILY_SETTLE_BILL_SEARCH(HttpSession session, HttpServletRequest request) {
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

		String username = HttpUtil.getStringParameterTrim(request, "username");
		int start = HttpUtil.getIntParameter(request, "start");
		int limit = HttpUtil.getIntParameter(request, "limit");
		String sTime = HttpUtil.getStringParameterTrim(request, "sTime");
		String eTime = HttpUtil.getStringParameterTrim(request, "eTime");
		Integer scope = HttpUtil.getIntParameter(request, "scope"); // 1：会员；2：团队；会员搜索会员本身的，团队搜索用户团队的数据
		if (scope == null) {
			scope = 1;
		}

		User user; // 目标用户
		if (StringUtils.isNotEmpty(username) && !StringUtils.equals(username, uEntity.getUsername())) {
			user = uReadService.getByUsernameFromRead(username); // 查询指定用户
			if (uProxyValidate.isLowerUser(uEntity, user) || uProxyValidate.isRelated(uEntity, user)) {
				// 合法用户
			}
			else {
				user = null;
			}
		}
		else {
			user = uEntity; // 查询自己
		}

		// 层级用户
		List<String> userLevels = uCodePointUtil.getUserLevels(uEntity, user);
		json.data("userLevels", userLevels);

		if (user == null) { // 目标用户为空
			json.data("data", new ArrayList<>());
			json.data("username", uEntity.getUsername());
			json.data("totalCount", 0);
		}
		else {
			PageList pList = new PageList();
			if (user.getUsername().equals(uEntity.getUsername())) {
				if (user.getId() == Global.USER_TOP_ID) {
					// 总账查询所有数据
					pList = uDailySettleBillReadService.searchAll(sTime, eTime, start, limit);
				}
				else {
					if (uEntity.getType() == Global.USER_TYPE_RELATED) {
						// 关联账号查询
						int[] relatedUserIds = ArrayUtils.transGetIds(uEntity.getRelatedUsers());
						if (relatedUserIds != null && relatedUserIds.length > 0) {
							pList = uDailySettleBillReadService.searchByTeam(relatedUserIds, sTime, eTime, start, limit);
						}
					}
					else {
						// 查询用户团队数据
						pList = uDailySettleBillReadService.searchByTeam(user.getId(), sTime, eTime, start, limit);
					}

					// // 查询用户团队数据
					// pList = uDailySettleBillReadService.searchByTeam(user.getId(), sTime, eTime, start, limit);
				}
			}
			else {
				if (scope == 1) {
					// 查询指定用户的数据
					pList = uDailySettleBillReadService.searchByUserId(user.getId(), sTime, eTime, start, limit);
				}
				else {
					// 查询用户团队数据
					pList = uDailySettleBillReadService.searchByTeam(user.getId(), sTime, eTime, start, limit);
				}
			}

			json.data("data", pList.getList());
			json.data("username", uEntity.getUsername());
			json.data("totalCount", pList.getCount());
		}
		json.set(0, "0-1");
		return json.toJson();
	}

	@RequestMapping(value = WUC.DAILY_SETTLE_LIST_LOWER, method = { RequestMethod.POST })
	@ResponseBody
	public Map<String, Object> DAILY_SETTLE_LIST_LOWER(HttpSession session, HttpServletRequest request) {
		WebJSON json = new WebJSON(dataFactory);
		SessionUser sessionUser = super.getSessionUser(json, session, request);
		if (sessionUser == null) {
			return json.toJson();
		}

		// 总账号不能发起
		if(sessionUser.getId() == Global.USER_TOP_ID) {
			json.set(2, "2-13"); // 您的账号无法使用该功能！
			return json.toJson();
		}
		if (!dataFactory.getDailySettleConfig().isEnable()) {
			json.set(2, "2-8"); // 该功能目前没有启用！
			return json.toJson();
		}
		User uEntity = uReadService.getByIdFromRead(sessionUser.getId());
		if (!uDailySettleValidate.allowRequestByUser(uEntity)) {
			// 过了这个判断表示发起人是可以发起的
			json.set(2, "2-13"); // 您的账号无法使用该功能！
			return json.toJson();
		}
		if(uEntity.getType() == Global.USER_TYPE_RELATED) {
			json.set(2, "2-13"); // 您的账号无法使用该功能！
			return json.toJson();
		}

		// 再判断发起人自己的契约状态
		UserDailySettle selfDailySettle = uDailySettleService.getByUserId(sessionUser.getId());
		if (selfDailySettle == null) {
			json.set(2, "2-5004"); // 您当前未拥有任何契约日结设置,无法发起契约日结！
			return json.toJson();
		}
		if (selfDailySettle.getStatus() == Global.DAILY_SETTLE_REQUESTED) {
			json.set(2, "2-5005"); // 请先同意您的契约日结！
			return json.toJson();
		}
		if (selfDailySettle.getStatus() != Global.DAILY_SETTLE_VALID) {
			json.set(2, "2-5006"); // 您当前拥有的契约日结设置无效,无法发起契约日结！
			return json.toJson();
		}

		/*int[] codes; // 列出直属下级的账号等级列表
		User requestUser = uEntity;

		if (uCodePointUtil.isLevel2Proxy(requestUser)) {
			// 招商只允许对直属发起
			codes = new int[]{requestUser.getCode()};
		}
		else {
			int minCode = uEntity.getCode() - 2;
			int maxCode = uEntity.getCode();
			if (minCode < 1800) {
				minCode = 1800;
			}
			if (minCode == maxCode) {
				codes = new int[]{minCode};
			}
			else {
				codes = new int[]{minCode, maxCode};
			}
		}

		// 所有下级代理
		if (codes == null || codes.length == 0) {
			// 不需要列出直属直级
			json.data("list", new ArrayList<>());
			json.set(0, "0-1");
			return json.toJson();
		}
*/
		// 查询所有直属下级
		List<User> tmpList = uReadService.getUserDirectLowerFromRead(sessionUser.getId());
		/*if (codes.length == 1) {
			tmpList = uReadService.getUserDirectLowerFromRead(sessionUser.getId(), codes[0]);
		}
		else {
			tmpList = uReadService.getUserDirectLowerFromRead(sessionUser.getId(), codes);
		}*/

		// 判断直属下级状态
		List<User> uList = new ArrayList<>();
		List<Integer> userLowerIds = new ArrayList<>();
		for (User tmpBean : tmpList) {
			if(tmpBean.getType() == Global.USER_TYPE_PROXY && tmpBean.getAStatus() == 0) {
				userLowerIds.add(tmpBean.getId());
				uList.add(tmpBean);
			}
		}

		// 移除已发起过或已生效的直属下级
		List<UserDailySettle> settleList = new ArrayList<>();
		if (CollectionUtils.isNotEmpty(userLowerIds)) {
			settleList = uDailySettleReadService.findByUserIds(userLowerIds);
		}

		List<UserVO> list = new ArrayList<>();
		for (User tmpBean : uList) {
			boolean contains = false;
			for (UserDailySettle userDailySettle : settleList) {
				if (tmpBean.getId() == userDailySettle.getUserId()
						&& (userDailySettle.getStatus() == Global.DAILY_SETTLE_REQUESTED
						|| userDailySettle.getStatus() == Global.DAILY_SETTLE_VALID)) {
					contains = true;
					break;
				}
			}

			if (!contains) {
				list.add(new UserVO(tmpBean.getId(), tmpBean.getUsername()));
			}
		}

		json.data("list", list);
		json.set(0, "0-1");
		return json.toJson();
	}

	@RequestMapping(value = WUC.DAILY_SETTLE_REQUEST, method = { RequestMethod.POST })
	@ResponseBody
	public Map<String, Object> DAILY_SETTLE_REQUEST(HttpSession session, HttpServletRequest request) {
		WebJSON json = new WebJSON(dataFactory);
		SessionUser sessionUser = super.getSessionUser(json, session, request);
		if (sessionUser == null) {
			return json.toJson();
		}

		String ip = HttpUtil.getRealIp(json, request);
		if (ip == null) {
			return json.toJson();
		}

		String username = HttpUtil.getStringParameterTrim(request, "username");
		String userLevel = HttpUtil.getStringParameterTrim(request, "userLevel");
		String scaleLevel = HttpUtil.getStringParameterTrim(request, "scaleLevel");
		String lossLevel = HttpUtil.getStringParameterTrim(request, "lossLevel");
		String salesLevel = HttpUtil.getStringParameterTrim(request, "salesLevel");
		scaleLevel = scaleLevel == null ? "0" : scaleLevel;
		lossLevel = lossLevel == null ? "0" : lossLevel;
		salesLevel = salesLevel == null ? "0" : salesLevel;
		userLevel = userLevel == null ? "0" : userLevel;
		
//		if (minValidUser == null || minValidUser < 0 || minValidUser > 1000) {
//			json.set(2, "2-5003"); // 最低有效人数不合法！
//			return json.toJson();
//		}

		if (StringUtils.isEmpty(username)) {
			json.set(2, "2-1000"); // 用户名不能不空
			return json.toJson();
		}

		if (!dataFactory.getDailySettleConfig().isEnable()) {
			json.set(2, "2-8"); // 该功能目前没有启用！
			return json.toJson();
		}

		User user = uReadService.getByUsernameFromRead(username);
		if (user == null) {
			json.set(2, "2-1003"); // 该用户不存在
			return json.toJson();
		}
		if (user.getAStatus() != 0) {
			json.set(2, "2-5001"); // 该用户状态为非正常
			return json.toJson();
		}
		
		String[] userArr = userLevel.split(",");
		boolean succeed = uDailySettleService.request(json, sessionUser.getId(), user.getId(), scaleLevel, lossLevel, salesLevel, Integer.valueOf(userArr[0]), userLevel);
		if(succeed) {
			uActionLogService.requestDailySettle(sessionUser.getId(), ip, user.getUsername(), scaleLevel, lossLevel, salesLevel, userLevel);
			json.set(0, "0-1");
		}

		return json.toJson();
	}

	@RequestMapping(value = WUC.DAILY_SETTLE_REQUEST_DATA, method = { RequestMethod.POST })
	@ResponseBody
	public Map<String, Object> DAILY_SETTLE_REQUEST_DATA(HttpSession session, HttpServletRequest request) {
		WebJSON json = new WebJSON(dataFactory);
		SessionUser sessionUser = super.getSessionUser(json, session, request);
		if (sessionUser == null) {
			return json.toJson();
		}
		// 总账号不能发起
		if(sessionUser.getId() == Global.USER_TOP_ID) {
			json.set(2, "2-13"); // 您的账号无法使用该功能！
			return json.toJson();
		}
		if (!dataFactory.getDailySettleConfig().isEnable()) {
			json.set(2, "2-8"); // 该功能目前没有启用！
			return json.toJson();
		}
		User requestUser = uReadService.getByIdFromRead(sessionUser.getId());
		if (!uDailySettleValidate.allowRequestByUser(requestUser)) {
			json.set(2, "2-13"); // 您的账号无法使用该功能！
			return json.toJson();
		}

		UserDailySettle settle = uDailySettleService.getByUserId(sessionUser.getId());
		if (settle == null) {
			json.set(2, "2-5004"); // 您当前未拥有任何契约日结设置，无法发起契约日结！
			return json.toJson();
		}
		if (settle.getStatus() == Global.DAILY_SETTLE_REQUESTED) {
			json.set(2, "2-5005"); // 请先同意您的契约！
			return json.toJson();
		}
		if (settle.getStatus() != Global.DAILY_SETTLE_VALID) {
			json.set(2, "2-5006"); // 您当前拥有的契约日结设置无效，无法发起契约分红！
			return json.toJson();
		}
		String username = HttpUtil.getStringParameterTrim(request, "username");
		User acceptUser;
		if (StringUtils.isNotEmpty(username)) {
			acceptUser = uReadService.getByUsernameFromRead(username);
			if (acceptUser == null) {
				json.set(2, "2-1003"); // 该用户不存在！
				return json.toJson();
			}

			if (!uProxyValidate.isDirectLower(sessionUser, acceptUser)) {
				json.set(2, "2-5013"); // 该用户不是您的下级！
				return json.toJson();
			}

			if (!uDailySettleValidate.allowAccept(requestUser, acceptUser)) {
				// 过了这一步表示发起人选择的接受人都是合法的,只需要生成比例即可
				json.set(2, "2-5019"); // 不允许对该账号发起契约日结！
				return json.toJson();
			}
		}
		else {
			// 刚进页面
			json.data("minScale", 0);
			json.data("maxScale", 0);
			json.data("minValidUser", 0);
			json.data("maxValidUser", 1000);
			json.set(0, "0-1");
			return json.toJson();
		}

		if (settle.getMaxScale() <= dataFactory.getDailySettleConfig().getLevelsScale()[0]) {
			json.set(2, "2-5016"); // 您的日结比例已经是最低了,无法发起契约日结！
			return json.toJson();
		}
		
		int minValidUser = settle.getMinValidUser();
		int maxValidUser = 1000;

		double[] minMaxScale = uDailySettleService.getMinMaxScale(acceptUser);
		double minScale = minMaxScale[0];
		double maxScale = minMaxScale[1];
		
		double[] minMaxSales = uDailySettleService.getMinMaxSales(acceptUser);
		double minSales = minMaxSales[0];
		double maxSales = minMaxSales[1];
		
		double[] minMaxLoss = uDailySettleService.getMinMaxLoss(acceptUser);
		double minLoss = minMaxLoss[0];
		double maxLoss = minMaxLoss[1];
		
		json.data("minScale", minScale);
		json.data("maxScale", maxScale);
		json.data("minSales", minSales);
		json.data("maxSales", maxSales);
		json.data("minLoss", minLoss);
		json.data("maxLoss", maxLoss);
		json.data("minValidUser", minValidUser);
		json.data("maxValidUser", maxValidUser);
		json.data("maxSignLevel", dataFactory.getDailySettleConfig().getMaxSignLevel());
		json.set(0, "0-1");
		return json.toJson();
	}

	@RequestMapping(value = WUC.DAILY_SETTLE_AGREE, method = { RequestMethod.POST })
	@ResponseBody
	public Map<String, Object> DAILY_SETTLE_AGREE(HttpSession session, HttpServletRequest request) {
		WebJSON json = new WebJSON(dataFactory);
		SessionUser sessionUser = super.getSessionUser(json, session, request);
		if (sessionUser == null) {
			return json.toJson();
		}

		String ip = HttpUtil.getRealIp(json, request);
		if (ip == null) {
			return json.toJson();
		}

		if (!dataFactory.getDailySettleConfig().isEnable()) {
			json.set(2, "2-8"); // 该功能目前没有启用！
		}
		else {
			int id = HttpUtil.getIntParameter(request, "id");

			boolean succeed = uDailySettleService.agree(json, sessionUser.getId(), id);
			if(succeed) {
				uActionLogService.agreeDailySettle(sessionUser.getId(), ip);
				json.set(0, "0-1");
			}
		}
		return json.toJson();
	}
	
	
	@RequestMapping(value = WUC.SEARCH_DIVIDEND_INFO, method = { RequestMethod.POST })
	@ResponseBody
	public Map<String, Object> SEARCH_DIVIDEND_INFO(HttpSession session, HttpServletRequest request) {
		WebJSON json = new WebJSON(dataFactory);
		SessionUser sessionUser = super.getSessionUser(json, session, request);
		if (sessionUser == null) {
			return json.toJson();
		}

		String ip = HttpUtil.getRealIp(json, request);
		if (ip == null) {
			return json.toJson();
		}
	
		UserDividend dividend = uDividendService.getByUserId(sessionUser.getId());
		
		if(dividend != null){
			json.data("dividend", true);
		}
		else{
			json.data("dividend", false);
		}
		
		UserDailySettle settle = uDailySettleService.getByUserId(sessionUser.getId());

		if(settle != null){
			json.data("settle", true);
		}else{
			json.data("settle", false);
		}
		return json.toJson();
	}

	@RequestMapping(value = WUC.DAILY_SETTLE_DENY, method = { RequestMethod.POST })
	@ResponseBody
	public Map<String, Object> DAILY_SETTLE_DENY(HttpSession session, HttpServletRequest request) {
		WebJSON json = new WebJSON(dataFactory);
		SessionUser sessionUser = super.getSessionUser(json, session, request);
		if (sessionUser == null) {
			return json.toJson();
		}

		String ip = HttpUtil.getRealIp(json, request);
		if (ip == null) {
			return json.toJson();
		}

		if (!dataFactory.getDailySettleConfig().isEnable()) {
			json.set(2, "2-8"); // 该功能目前没有启用！
		}
		else {
			int id = HttpUtil.getIntParameter(request, "id");

			boolean succeed = uDailySettleService.deny(json, sessionUser.getId(), id);
			if(succeed) {
				uActionLogService.denyDailySettle(sessionUser.getId(), ip);
				json.set(0, "0-1");
			}
		}
		return json.toJson();
	}





	@RequestMapping(value = WUC.DIVIDEND_SEARCH, method = { RequestMethod.POST })
	@ResponseBody
	public Map<String, Object> DIVIDEND_SEARCH(HttpSession session, HttpServletRequest request) {
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

		String username = HttpUtil.getStringParameterTrim(request, "username");
		if (username != null) {
			username = username.trim();
		}
		int start = HttpUtil.getIntParameter(request, "start");
		int limit = HttpUtil.getIntParameter(request, "limit");
		Integer scope = HttpUtil.getIntParameter(request, "scope"); // 1：会员；2：团队；会员搜索会员本身的，团队搜索用户团队的数据
		if (scope == null) {
			scope = 1;
		}

		User user; // 目标用户
		if (StringUtils.isNotEmpty(username) && !StringUtils.equals(username, uEntity.getUsername())) {
			user = uReadService.getByUsernameFromRead(username); // 查询指定用户
			if (uProxyValidate.isLowerUser(uEntity, user) || uProxyValidate.isRelated(uEntity, user)) {
				// 合法用户
			}
			else {
				user = null;
			}
		}
		else {
			user = uEntity; // 查询自己
		}

		// 层级用户
		List<String> userLevels = uCodePointUtil.getUserLevels(uEntity, user);
		json.data("userLevels", userLevels);

		if (user == null) { // 目标用户为空
			json.data("data", new ArrayList<>());
			json.data("username", uEntity.getUsername());
			json.data("totalCount", 0);
		}
		else {
			PageList pList = new PageList();
			if (user.getUsername().equals(uEntity.getUsername())) {
				if (user.getId() == Global.USER_TOP_ID) {
					// 总账查询所有数据
					pList = uDividendReadService.searchAll(start, limit);
				}
				else {
					if (uEntity.getType() == Global.USER_TYPE_RELATED) {
						// 关联账号查询
						int[] relatedUserIds = ArrayUtils.transGetIds(uEntity.getRelatedUsers());
						if (relatedUserIds != null && relatedUserIds.length > 0) {
							pList = uDividendReadService.searchByTeam(relatedUserIds, start, limit);
						}
					}
					else {
						// 查询用户团队数据
						pList = uDividendReadService.searchByTeam(user.getId(), start, limit);
					}
				}
			}
			else {
				if (scope == 1) {
					// 查询指定用户的数据
					pList = uDividendReadService.searchByUserId(user.getId(), start, limit);
				}
				else {
					// 查询用户团队数据
					pList = uDividendReadService.searchByTeam(user.getId(), start, limit);
				}
			}
			json.data("data", pList.getList());
			json.data("username", uEntity.getUsername());
			json.data("totalCount", pList.getCount());
		}
		json.set(0, "0-1");
		return json.toJson();
	}

	@RequestMapping(value = WUC.DIVIDEND_BILL_SEARCH, method = { RequestMethod.POST })
	@ResponseBody
	public Map<String, Object> DIVIDEND_BILL_SEARCH(HttpSession session, HttpServletRequest request) {
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

		String username = HttpUtil.getStringParameterTrim(request, "username");
		if (username != null) {
			username = username.trim();
		}
		int start = HttpUtil.getIntParameter(request, "start");
		int limit = HttpUtil.getIntParameter(request, "limit");
		String sTime = HttpUtil.getStringParameterTrim(request, "sTime");
		String eTime = HttpUtil.getStringParameterTrim(request, "eTime");
		Integer status = HttpUtil.getIntParameter(request, "status");
		Integer scope = HttpUtil.getIntParameter(request, "scope"); // 1：会员；2：团队；会员搜索会员本身的，团队搜索用户团队的数据
		if (scope == null) {
			scope = 1;
		}

		User user; // 目标用户
		if (StringUtils.isNotEmpty(username) && !StringUtils.equals(username, uEntity.getUsername())) {
			user = uReadService.getByUsernameFromRead(username); // 查询指定用户
			if (uProxyValidate.isLowerUser(uEntity, user) || uProxyValidate.isRelated(uEntity, user)) {
				// 合法用户
			}
			else {
				user = null;
			}
		}
		else {
			user = uEntity; // 查询自己
		}

		// 层级用户
		List<String> userLevels = uCodePointUtil.getUserLevels(uEntity, user);
		json.data("userLevels", userLevels);

		if (user == null) { // 目标用户为空
			json.data("data", new ArrayList<>());
			json.data("username", uEntity.getUsername());
			json.data("totalCount", 0);
		}
		else {
			PageList pList = new PageList();
			if (user.getUsername().equals(uEntity.getUsername())) {
				if (user.getId() == Global.USER_TOP_ID) {
					// 总账查询所有数据
					pList = uDividendBillReadService.searchAll(sTime, eTime, status, start, limit);
				}
				else {
					if (uEntity.getType() == Global.USER_TYPE_RELATED) {
						// 关联账号查询
						int[] relatedUserIds = ArrayUtils.transGetIds(uEntity.getRelatedUsers());
						if (relatedUserIds != null && relatedUserIds.length > 0) {
							pList = uDividendBillReadService.searchByTeam(relatedUserIds, sTime, eTime, status, start, limit);
						}
					}
					else {
						// 查询用户团队数据
						pList = uDividendBillReadService.searchByTeam(user.getId(), sTime, eTime, status, start, limit);
					}
				}
			}
			else {
				if (scope == 1) {
					// 查询指定用户的数据
					pList = uDividendBillReadService.searchByUserId(user.getId(), sTime, eTime, status, start, limit);
				}
				else {
					// 查询用户团队数据
					pList = uDividendBillReadService.searchByTeam(user.getId(), sTime, eTime, status, start, limit);
				}
			}
			json.data("data", pList.getList());
			json.data("username", uEntity.getUsername());
			json.data("totalCount", pList.getCount());
		}
		json.set(0, "0-1");
		return json.toJson();
	}

	@RequestMapping(value = WUC.DIVIDEND_GET, method = { RequestMethod.POST })
	@ResponseBody
	public Map<String, Object> DIVIDEND_GET(HttpSession session, HttpServletRequest request) {
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

		UserDividendBill bill = uDividendBillService.getById(id);
		// 分红账单必须是用户自己或者用户直属下级的
		if (bill == null) {
			json.set(0, "0-1");
			json.data("data", "{}");
			return json.toJson();
		}
		if (bill.getUserId() != sessionUser.getId()) {
			User lowerUser = uReadService.getByIdFromRead(bill.getUserId());
			if (lowerUser == null) {
				json.set(0, "0-1");
				json.data("data", "{}");
				return json.toJson();
			}
			if (!uProxyValidate.isLowerUser(uEntity, lowerUser)) {
				if (!uProxyValidate.isRelated(uEntity, lowerUser)) {
					json.set(0, "0-1");
					json.data("data", "{}");
					return json.toJson();
				}
			}
		}

		json.set(0, "0-1");
		json.data("data", new UserDividendBillVO(bill, dataFactory));
		return json.toJson();
	}

	@RequestMapping(value = WUC.DIVIDEND_LIST_LOWER, method = { RequestMethod.POST })
	@ResponseBody
	public Map<String, Object> DIVIDEND_LIST_LOWER(HttpSession session, HttpServletRequest request) {
		WebJSON json = new WebJSON(dataFactory);
		SessionUser sessionUser = super.getSessionUser(json, session, request);
		if (sessionUser == null) {
			return json.toJson();
		}

		// 总账号不能发起
		if(sessionUser.getId() == Global.USER_TOP_ID) {
			json.set(2, "2-13"); // 您的账号无法使用该功能！
			return json.toJson();
		}
		if (!dataFactory.getDividendConfig().isEnable()) {
			json.set(2, "2-8"); // 该功能目前没有启用！
			return json.toJson();
		}
		User uEntity = uReadService.getByIdFromRead(sessionUser.getId());
		if (!uDividendValidate.allowRequestByUser(uEntity)) {
			// 过了这个判断表示发起人是可以发起的
			json.set(2, "2-13"); // 您的账号无法使用该功能！
			return json.toJson();
		}
		if(uEntity.getType() == Global.USER_TYPE_RELATED) {
			json.set(2, "2-13"); // 您的账号无法使用该功能！
			return json.toJson();
		}

		// 再判断发起人自己的契约状态
		UserDividend selfDividend = uDividendService.getByUserId(sessionUser.getId());
		if (selfDividend == null) {
			json.set(2, "2-6004"); // 您当前未拥有任何契约分红设置,无法发起契约分红！
			return json.toJson();
		}
		if (selfDividend.getStatus() == Global.DIVIDEND_REQUESTED) {
			json.set(2, "2-6005"); // 请先同意您的契约分红！
			return json.toJson();
		}
		if (selfDividend.getStatus() != Global.DIVIDEND_VALID) {
			json.set(2, "2-6006"); // 您当前拥有的契约分红设置无效,无法发起契约分红！
			return json.toJson();
		}

//		User requestUser = uEntity;

//		int[] codes; // 列出直属下级的账号等级列表
//		if (uCodePointUtil.isLevel2Proxy(requestUser)) {
//			codes = new int[]{requestUser.getCode()};
//		}
//		else {
//			int minCode = uEntity.getCode() - 2;
//			int maxCode = uEntity.getCode();
//			if (minCode < 1800) {
//				minCode = 1800;
//			}
//			if (minCode == maxCode) {
//				codes = new int[]{minCode};
//			}
//			else {
//				codes = new int[]{minCode, maxCode};
//			}
//		}
//
//		// 所有下级代理
//		if (codes == null || codes.length == 0) {
//			// 不需要列出直属直级
//			json.data("list", new ArrayList<>());
//			json.set(0, "0-1");
//			return json.toJson();
//		}

		// 查询所有直属下级
		List<User> tmpList = uReadService.getUserDirectLowerFromRead(uEntity.getId());
//		if (codes.length == 1) {
//			tmpList = uReadService.getUserDirectLowerFromRead(sessionUser.getId(), codes[0]);
//		}
//		else {
//			tmpList = uReadService.getUserDirectLowerFromRead(sessionUser.getId(), codes);
//		}

		// 判断直属下级状态
		List<User> uList = new ArrayList<>();
		List<Integer> userLowerIds = new ArrayList<>();
		for (User tmpBean : tmpList) {
			if(tmpBean.getType() == Global.USER_TYPE_PROXY && tmpBean.getAStatus() == 0) {
				userLowerIds.add(tmpBean.getId());
				uList.add(tmpBean);
			}
		}

		// 移除已发起过或已生效的直属下级
		List<UserDividend> dividendList = new ArrayList<>();
		if (CollectionUtils.isNotEmpty(userLowerIds)) {
			dividendList = uDividendReadService.findByUserIds(userLowerIds);
		}

		List<UserVO> list = new ArrayList<>();
		for (User tmpBean : uList) {
			boolean contains = false;
			for (UserDividend dividend : dividendList) {
				if (tmpBean.getId() == dividend.getUserId()
						&& (dividend.getStatus() == Global.DIVIDEND_REQUESTED
						|| dividend.getStatus() == Global.DIVIDEND_VALID)) {
					contains = true;
					break;
				}
			}

			if (!contains) {
				list.add(new UserVO(tmpBean.getId(), tmpBean.getUsername()));
			}
		}

		json.data("list", list);
		json.set(0, "0-1");
		return json.toJson();
	}



	@RequestMapping(value = WUC.DIVIDEND_REQUEST, method = { RequestMethod.POST })
	@ResponseBody
	public Map<String, Object> DIVIDEND_REQUEST(HttpSession session, HttpServletRequest request) {
		WebJSON json = new WebJSON(dataFactory);
		SessionUser sessionUser = super.getSessionUser(json, session, request);
		if (sessionUser == null) {
			return json.toJson();
		}

		String ip = HttpUtil.getRealIp(json, request);
		if (ip == null) {
			return json.toJson();
		}

		String username = HttpUtil.getStringParameterTrim(request, "username");
		String scaleLevel = HttpUtil.getStringParameterTrim(request, "scaleLevel");
		String lossLevel = HttpUtil.getStringParameterTrim(request, "lossLevel");
		String salesLevel = HttpUtil.getStringParameterTrim(request, "salesLevel");
		String userLevel = HttpUtil.getStringParameterTrim(request, "userLevel");
		scaleLevel = scaleLevel == null ? "0" : scaleLevel;
		lossLevel = lossLevel == null ? "0" : lossLevel;
		salesLevel = salesLevel == null ? "0" : salesLevel;
		userLevel = userLevel == null ? "0" : userLevel;
		
		
//		if (minValidUser == null || minValidUser < 0 || minValidUser > 1000) {
//			json.set(2, "2-6003"); // 最低有效人数不合法！
//			return json.toJson();
//		}

		if (StringUtils.isEmpty(username)) {
			json.set(2, "2-1000"); // 用户名不能不空
			return json.toJson();
		}

		if (!dataFactory.getDividendConfig().isEnable()) {
			json.set(2, "2-8"); // 该功能目前没有启用！
			return json.toJson();
		}

		User user = uReadService.getByUsernameFromRead(username);
		if (user == null) {
			json.set(2, "2-1003"); // 该用户不存在
			return json.toJson();
		}
		if (user.getAStatus() != 0) {
			json.set(2, "2-6001"); // 该用户状态为非正常
			return json.toJson();
		}
		
		String[] userArr = userLevel.split(",");

		boolean succeed = uDividendService.request(json, sessionUser.getId(), user.getId(), scaleLevel, lossLevel, salesLevel, Integer.valueOf(userArr[0]), userLevel);
		if(succeed) {
			UserDividend dividend = uDividendService.getByUserId(user.getId());
			uActionLogService.requestDividend(sessionUser.getId(), ip, user.getUsername(), dividend.getScaleLevel(), dividend.getLossLevel(), dividend.getSalesLevel(), dividend.getUserLevel());
			json.set(0, "0-1");
		}
		return json.toJson();
	}

	@RequestMapping(value = WUC.DIVIDEND_REQUEST_DATA, method = { RequestMethod.POST })
	@ResponseBody
	public Map<String, Object> DIVIDEND_REQUEST_DATA(HttpSession session, HttpServletRequest request) {
		WebJSON json = new WebJSON(dataFactory);
		SessionUser sessionUser = super.getSessionUser(json, session, request);
		if (sessionUser == null) {
			return json.toJson();
		}
		// 总账号不能发起
		if(sessionUser.getId() == Global.USER_TOP_ID) {
			json.set(2, "2-13"); // 您的账号无法使用该功能！
			return json.toJson();
		}
		if (!dataFactory.getDividendConfig().isEnable()) {
			json.set(2, "2-8"); // 该功能目前没有启用！
			return json.toJson();
		}
		User uEntity = uReadService.getByIdFromRead(sessionUser.getId());
		if (!uDividendValidate.allowRequestByUser(uEntity)) {
			json.set(2, "2-13"); // 您的账号无法使用该功能！
			return json.toJson();
		}

		UserDividend dividend = uDividendService.getByUserId(sessionUser.getId());
		if (dividend == null) {
			json.set(2, "2-6004"); // 您当前未拥有任何契约分红设置,无法发起契约分红！
			return json.toJson();
		}
		if (dividend.getStatus() == Global.DIVIDEND_REQUESTED) {
			json.set(2, "2-6005"); // 请先同意您的契约分红！
			return json.toJson();
		}
		if (dividend.getStatus() != Global.DIVIDEND_VALID) {
			json.set(2, "2-6006"); // 您当前拥有的契约分红设置无效,无法发起契约分红！
			return json.toJson();
		}
		String username = HttpUtil.getStringParameterTrim(request, "username");
		User user;
		int minValidUser = dataFactory.getDividendConfig().getMinValidUserl();
		int maxValidUser = 1000;
		if (StringUtils.isNotEmpty(username)) {
			user = uReadService.getByUsernameFromRead(username);
			if (user == null) {
				json.set(2, "2-1003"); // 该用户不存在！
				return json.toJson();
			}

			if (!uProxyValidate.isDirectLower(sessionUser, user)) {
				json.set(2, "2-6013"); // 该用户不是您的下级！
				return json.toJson();
			}

			if (!uDividendValidate.allowAccept(uEntity, user)) {
				// 过了这一步表示发起人选择的接受人都是合法的,只需要生成比例即可
				json.set(2, "2-6026"); // 不允许对该账号发起契约分红！
				return json.toJson();
			}
		}
		else {
			// 刚进页面
			json.data("minScale", 0);
			json.data("maxScale", 0);
			json.data("minValidUser", minValidUser);
			json.data("maxValidUser", 1000);
			json.set(0, "0-1");
			return json.toJson();
		}

		double[] minMaxScale = uDividendService.getMinMaxScale(user);
		double minScale = minMaxScale[0];
		double maxScale = minMaxScale[1];
		
		double[] minMaxSales = uDividendService.getMinMaxSales(user);
		double minSales = minMaxSales[0];
		double maxSales = minMaxSales[1];
		
		double[] minMaxLoss = uDividendService.getMinMaxLoss(user);
		double minLoss = minMaxLoss[0];
		double maxLoss = minMaxLoss[1];
		
		json.data("minScale", minScale);
		json.data("maxScale", maxScale);
		json.data("minSales", minSales);
		json.data("maxSales", maxSales);
		json.data("minLoss", minLoss);
		json.data("maxLoss", maxLoss);
		json.data("minValidUser", minValidUser);
		json.data("maxValidUser", maxValidUser);
		json.data("maxSignLevel", dataFactory.getDividendConfig().getMaxSignLevel());
		json.set(0, "0-1");
		return json.toJson();
	}




	@RequestMapping(value = WUC.DIVIDEND_AGREE, method = { RequestMethod.POST })
	@ResponseBody
	public Map<String, Object> DIVIDEND_AGREE(HttpSession session, HttpServletRequest request) {
		WebJSON json = new WebJSON(dataFactory);
		SessionUser sessionUser = super.getSessionUser(json, session, request);
		if (sessionUser == null) {
			return json.toJson();
		}

		String ip = HttpUtil.getRealIp(json, request);
		if (ip == null) {
			return json.toJson();
		}

		if (!dataFactory.getDividendConfig().isEnable()) {
			json.set(2, "2-8"); // 该功能目前没有启用！
		}
		else {
			int id = HttpUtil.getIntParameter(request, "id");

			boolean succeed = uDividendService.agree(json, sessionUser.getId(), id);
			if(succeed) {
				uActionLogService.agreeDividend(sessionUser.getId(), ip);
				json.set(0, "0-1");
			}
		}
		return json.toJson();
	}

	@RequestMapping(value = WUC.DIVIDEND_DENY, method = { RequestMethod.POST })
	@ResponseBody
	public Map<String, Object> DIVIDEND_DENY(HttpSession session, HttpServletRequest request) {
		WebJSON json = new WebJSON(dataFactory);
		SessionUser sessionUser = super.getSessionUser(json, session, request);
		if (sessionUser == null) {
			return json.toJson();
		}

		String ip = HttpUtil.getRealIp(json, request);
		if (ip == null) {
			return json.toJson();
		}

		if (!dataFactory.getDividendConfig().isEnable()) {
			json.set(2, "2-8"); // 该功能目前没有启用！
		}
		else {
			int id = HttpUtil.getIntParameter(request, "id");

			boolean succeed = uDividendService.deny(json, sessionUser.getId(), id);
			if(succeed) {
				uActionLogService.denyDividend(sessionUser.getId(), ip);
				json.set(0, "0-1");
			}
		}
		return json.toJson();
	}

	@RequestMapping(value = WUC.DIVIDEND_COLLECT, method = { RequestMethod.POST })
	@ResponseBody
	public Map<String, Object> DIVIDEND_COLLECT(HttpSession session, HttpServletRequest request) {
		WebJSON json = new WebJSON(dataFactory);
		SessionUser sessionUser = super.getSessionUser(json, session, request);
		if (sessionUser == null) {
			return json.toJson();
		}

		String ip = HttpUtil.getRealIp(json, request);
		if (ip == null) {
			return json.toJson();
		}

		if (!dataFactory.getDividendConfig().isEnable()) {
			json.set(2, "2-8"); // 该功能目前没有启用！
			return json.toJson();
		}

		int id = HttpUtil.getIntParameter(request, "id");

		UserDividendBill bill = uDividendBillService.getById(id);
		boolean succeed = uDividendBillService.collect(json, sessionUser.getId(), id);
		if(succeed) {
			uActionLogService.collectDividend(sessionUser.getId(), ip,
					bill.getIndicateStartDate(), bill.getIndicateEndDate(), bill.getAvailableAmount());
			json.set(0, "0-1");
		}
		return json.toJson();
	}

//	@RequestMapping(value = WUC.DIVIDEND_ISSUE, method = { RequestMethod.POST })
//	@ResponseBody
//	public Map<String, Object> DIVIDEND_ISSUE(HttpSession session, HttpServletRequest request) {
//		WebJSON json = new WebJSON(dataFactory);
//		json.set(2, "2-6027"); // 功能关闭，改为后台进行
//		return json.toJson();
//	}

	@RequestMapping(value = WUC.GAME_DIVIDEND_BILL_SEARCH, method = { RequestMethod.POST })
	@ResponseBody
	public Map<String, Object> GAME_DIVIDEND_BILL_SEARCH(HttpSession session, HttpServletRequest request) {
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

		String username = HttpUtil.getStringParameterTrim(request, "username");
		if (username != null) {
			username = username.trim();
		}
		int start = HttpUtil.getIntParameter(request, "start");
		int limit = HttpUtil.getIntParameter(request, "limit");
		String sTime = HttpUtil.getStringParameterTrim(request, "sTime");
		String eTime = HttpUtil.getStringParameterTrim(request, "eTime");

		User user; // 目标用户
		if (StringUtils.isNotEmpty(username) && !StringUtils.equals(username, uEntity.getUsername())) {
			user = uReadService.getByUsernameFromRead(username); // 查询指定用户
			if (uProxyValidate.isLowerUser(uEntity, user) || uProxyValidate.isRelated(uEntity, user)) {
				// 合法用户
			}
			else {
				user = null;
			}
		}
		else {
			user = uEntity; // 查询自己
		}

		// 层级用户
		List<String> userLevels = uCodePointUtil.getUserLevels(uEntity, user);
		json.data("userLevels", userLevels);

		if (user == null) { // 目标用户为空
			json.data("data", new ArrayList<>());
			json.data("username", uEntity.getUsername());
			json.data("totalCount", 0);
		}
		else {
			PageList pList = uGameDividendBillReadService.searchByLowers(user.getId(), sTime, eTime, start, limit);
			json.data("data", pList.getList());
			json.data("username", uEntity.getUsername());
			json.data("totalCount", pList.getCount());
		}
		json.set(0, "0-1");
		return json.toJson();
	}

	@RequestMapping(value = WUC.GAME_DIVIDEND_BILL_GET, method = { RequestMethod.POST })
	@ResponseBody
	public Map<String, Object> GAME_DIVIDEND_BILL_GET(HttpSession session, HttpServletRequest request) {
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

		UserGameDividendBill bill = uGameDividendBillService.getById(id);
		// 分红账单必须是用户自己或者用户直属下级的
		if (bill == null) {
			json.set(0, "0-1");
			json.data("data", "{}");
			return json.toJson();
		}
		if (bill.getUserId() != sessionUser.getId()) {
			User lowerUser = uReadService.getByIdFromRead(bill.getUserId());
			if (lowerUser == null ) {
				json.set(0, "0-1");
				json.data("data", "{}");
				return json.toJson();
			}
			if (!uProxyValidate.isLowerUser(uEntity, lowerUser)) {
				if (!uProxyValidate.isRelated(uEntity, lowerUser)) {
					json.set(0, "0-1");
					json.data("data", "{}");
					return json.toJson();
				}
			}
		}

		json.set(0, "0-1");
		json.data("data", new UserGameDividendBillVO(bill, dataFactory));
		return json.toJson();
	}

	@RequestMapping(value = WUC.GAME_DIVIDEND_BILL_COLLECT, method = { RequestMethod.POST })
	@ResponseBody
	public Map<String, Object> GAME_DIVIDEND_BILL_COLLECT(HttpSession session, HttpServletRequest request) {
		WebJSON json = new WebJSON(dataFactory);
		SessionUser sessionUser = super.getSessionUser(json, session, request);
		if (sessionUser == null) {
			return json.toJson();
		}

		String ip = HttpUtil.getRealIp(json, request);
		if (ip == null) {
			return json.toJson();
		}

		int id = HttpUtil.getIntParameter(request, "id");

		UserGameDividendBill bill = uGameDividendBillService.getById(id);
		boolean succeed = uGameDividendBillService.collect(json, sessionUser.getId(), id);
		if(succeed) {
			uActionLogService.collectGameDividend(sessionUser.getId(), ip,
					bill.getIndicateStartDate(), bill.getIndicateEndDate(), bill.getUserAmount());
			json.set(0, "0-1");
		}
		return json.toJson();
	}

	@RequestMapping(value = WUC.GAME_WATER_BILL_SEARCH, method = { RequestMethod.POST })
	@ResponseBody
	public Map<String, Object> GAME_WATER_BILL_SEARCH(HttpSession session, HttpServletRequest request) {
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

		String username = HttpUtil.getStringParameterTrim(request, "username");
		int start = HttpUtil.getIntParameter(request, "start");
		int limit = HttpUtil.getIntParameter(request, "limit");
		String sTime = HttpUtil.getStringParameterTrim(request, "sTime");
		String eTime = HttpUtil.getStringParameterTrim(request, "eTime");
		Integer scope = HttpUtil.getIntParameter(request, "scope"); // 1：会员；2：团队；会员搜索会员本身的，团队搜索用户团队的数据
		if (scope == null) {
			scope = 1;
		}

		User user; // 目标用户
		if (StringUtils.isNotEmpty(username) && !StringUtils.equals(username, uEntity.getUsername())) {
			user = uReadService.getByUsernameFromRead(username); // 查询指定用户
			if (uProxyValidate.isLowerUser(uEntity, user) || uProxyValidate.isRelated(uEntity, user)) {
				// 合法用户
			}
			else {
				user = null;
			}
		}
		else {
			user = uEntity; // 查询自己
		}

		// 层级用户
		List<String> userLevels = uCodePointUtil.getUserLevels(uEntity, user);
		json.data("userLevels", userLevels);

		if (user == null) { // 目标用户为空
			json.data("data", new ArrayList<>());
			json.data("username", uEntity.getUsername());
			json.data("totalCount", 0);
		}
		else {
			PageList pList = new PageList();
			if (user.getUsername().equals(uEntity.getUsername())) {
				if (user.getId() == Global.USER_TOP_ID) {
					// 总账查询所有数据
					pList = uGameWaterBillService.searchAll(sTime, eTime, start, limit);
				}
				else {
					if (uEntity.getType() == Global.USER_TYPE_RELATED) {
						// 关联账号查询
						int[] relatedUserIds = ArrayUtils.transGetIds(uEntity.getRelatedUsers());
						if (relatedUserIds != null && relatedUserIds.length > 0) {
							pList = uGameWaterBillService.searchByTeam(relatedUserIds, sTime, eTime, start, limit);
						}
					}
					else {
						// 查询用户团队数据
						pList = uGameWaterBillService.searchByTeam(user.getId(), sTime, eTime, start, limit);
					}

					// // 查询用户团队数据
					// pList = uGameWaterBillService.searchByTeam(user.getId(), sTime, eTime, start, limit);
				}
			}
			else {
				if (scope == 1) {
					// 查询指定用户的数据
					pList = uGameWaterBillService.searchByUserId(user.getId(), sTime, eTime, start, limit);
				}
				else {
					// 查询用户团队数据
					pList = uGameWaterBillService.searchByTeam(user.getId(), sTime, eTime, start, limit);
				}
			}

			json.data("data", pList.getList());
			json.data("username", uEntity.getUsername());
			json.data("totalCount", pList.getCount());

			// PageList pList = uGameWaterBillService.searchByDirectLowers(user.getId(), sTime, eTime, start, limit);
			// json.data("data", pList.getList());
			// json.data("username", uEntity.getUsername());
			// json.data("totalCount", pList.getCount());
		}
		json.set(0, "0-1");
		return json.toJson();
	}

	@RequestMapping(value = WUC.SHOW_DAIYU, method = { RequestMethod.POST })
	@ResponseBody
	public Map<String, Object> SHOW_DAIYU(HttpSession session, HttpServletRequest request) {
		WebJSON json = new WebJSON(dataFactory);
		SessionUser sessionUser = super.getSessionUser(json, session, request);
		if (sessionUser == null) {
			return json.toJson();
		}

		boolean showDailySettle = false;
		boolean showDividend = false;
		if (sessionUser.getId() == Global.USER_TOP_ID) {
			showDailySettle = true;
			showDividend = true;
		}
		else {
			User user = uReadService.getByIdFromRead(sessionUser.getId());
			if (user != null) {
//				if (uCodePointUtil.isLevel1Proxy(user)) {
//					showDailySettle = true;
//					showDividend = true;
//				}
//				else {
					Long dailySettleReadCount = userDailySettleReadService.getCountUser(user.getId());
					Long dividendReadCount = UserDividendReadService.getCountUser(user.getId());
					showDailySettle = dailySettleReadCount > 0;
					showDividend = dividendReadCount > 0;
//				}
			}
		}

		json.data("showDailySettle", showDailySettle);
		json.data("showDividend", showDividend);
		return json.toJson();
	}

}