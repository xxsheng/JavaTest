package lottery.web.content;

import admin.domains.content.entity.AdminUser;
import admin.domains.jobs.AdminUserActionLogJob;
import admin.web.WUC;
import admin.web.WebJSONObject;
import admin.web.helper.AbstractActionController;
import javautils.StringUtil;
import javautils.http.HttpUtil;
import lottery.domains.content.biz.UserGameReportService;
import lottery.domains.content.biz.UserLotteryDetailsReportService;
import lottery.domains.content.biz.UserLotteryReportService;
import lottery.domains.content.biz.UserMainReportService;
import lottery.domains.content.dao.UserDao;
import lottery.domains.content.entity.User;
import lottery.domains.content.global.Global;
import lottery.domains.content.vo.bill.*;
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
public class ComplexReportController extends AbstractActionController {

	@Autowired
	private AdminUserActionLogJob adminUserActionLogJob;
	
	@Autowired
	private UserDao uDao;
	
	@Autowired
	private UserMainReportService uMainReportService;
	
	@Autowired
	private UserLotteryReportService uLotteryReportService;
	
	@Autowired
	private UserLotteryDetailsReportService uLotteryDetailsReportService;
	
	@Autowired
	private UserGameReportService uGameReportService;

	@Autowired
	private UserCodePointUtil uCodePointUtil;

	@RequestMapping(value = WUC.MAIN_REPORT_COMPLEX, method = { RequestMethod.POST })
	@ResponseBody
	public void MAIN_REPORT_COMPLEX(HttpSession session, HttpServletRequest request, HttpServletResponse response) {
		String actionKey = WUC.MAIN_REPORT_COMPLEX;
		long t1 = System.currentTimeMillis();
		WebJSONObject json = new WebJSONObject(super.getAdminDataFactory());
		AdminUser uEntity = super.getCurrUser(session, request, response);
		if (uEntity != null) {
			if (super.hasAccess(uEntity, actionKey)) {
				String username = HttpUtil.getStringParameterTrim(request, "username");
				String sTime = HttpUtil.getStringParameterTrim(request, "sTime");
				String eTime = HttpUtil.getStringParameterTrim(request, "eTime");
				if(StringUtils.isNotEmpty(username)) {
					User targetUser = uDao.getByUsername(username);
					if (targetUser != null) {
						List<UserMainReportVO> result = uMainReportService.report(targetUser.getId(), sTime, eTime);
						json.accumulate("list", result);
					}
					else {
						json.accumulate("list", new ArrayList<>());
					}
				} else {
					List<UserMainReportVO> result = uMainReportService.report(sTime, eTime);
					json.accumulate("list", result);
				}

				// 层级用户
				List<String> userLevels = uCodePointUtil.getUserLevels(username);
				json.accumulate("userLevels", userLevels);

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
	
	@RequestMapping(value = WUC.LOTTERY_REPORT_COMPLEX, method = { RequestMethod.POST })
	@ResponseBody
	public void LOTTERY_REPORT_COMPLEX(HttpSession session, HttpServletRequest request, HttpServletResponse response) {
		String actionKey = WUC.LOTTERY_REPORT_COMPLEX;
		long t1 = System.currentTimeMillis();
		WebJSONObject json = new WebJSONObject(super.getAdminDataFactory());
		AdminUser uEntity = super.getCurrUser(session, request, response);
		if (uEntity != null) {
			if (super.hasAccess(uEntity, actionKey)) {
				String username = request.getParameter("username");
				String sTime = request.getParameter("sTime");
				String eTime = request.getParameter("eTime");
				Integer type = HttpUtil.getIntParameter(request, "type");
				if(StringUtil.isNotNull(username)) {
					User targetUser = uDao.getByUsername(username);
					if (targetUser != null) {
						List<UserLotteryReportVO> result = uLotteryReportService.report(targetUser.getId(), sTime, eTime);
						json.accumulate("list", result);
					}
					else {
						json.accumulate("list", new ArrayList<>());
					}
				} else if(type != null){
					List<UserLotteryReportVO> result = uLotteryReportService.reportByType(4,sTime, eTime);
					json.accumulate("list", result);
				}else {
					List<UserLotteryReportVO> result = uLotteryReportService.report(sTime, eTime);
					json.accumulate("list", result);
				}

				// 层级用户
				List<String> userLevels = uCodePointUtil.getUserLevels(username);
				json.accumulate("userLevels", userLevels);

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
	
	/**
	 * 历史彩票综合报表
	 * @param session
	 * @param request
	 * @param response
	 */
	@RequestMapping(value = WUC.HISTORY_LOTTERY_REPORT_COMPLEX, method = { RequestMethod.POST })
	@ResponseBody
	public void HISTORY_LOTTERY_REPORT_COMPLEX(HttpSession session, HttpServletRequest request, HttpServletResponse response) {
		String actionKey = WUC.HISTORY_LOTTERY_REPORT_COMPLEX;
		long t1 = System.currentTimeMillis();
		WebJSONObject json = new WebJSONObject(super.getAdminDataFactory());
		AdminUser uEntity = super.getCurrUser(session, request, response);
		if (uEntity != null) {
			if (super.hasAccess(uEntity, actionKey)) {
				String username = request.getParameter("username");
				String sTime = request.getParameter("sTime");
				String eTime = request.getParameter("eTime");
				if(StringUtil.isNotNull(username)) {
					User targetUser = uDao.getByUsername(username);
					if (targetUser != null) {
						List<HistoryUserLotteryReportVO> result = uLotteryReportService.historyReport(targetUser.getId(), sTime, eTime);
						json.accumulate("list", result);
					}
					else {
						json.accumulate("list", new ArrayList<>());
					}
				} else {
					List<HistoryUserLotteryReportVO> result = uLotteryReportService.historyReport(sTime, eTime);
					json.accumulate("list", result);
				}

				// 层级用户
				List<String> userLevels = uCodePointUtil.getUserLevels(username);
				json.accumulate("userLevels", userLevels);

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
	
	@RequestMapping(value = WUC.LOTTERY_REPORT_COMPLEX_DETAILS, method = { RequestMethod.POST })
	@ResponseBody
	public void LOTTERY_REPORT_COMPLEX_DETAILS(HttpSession session, HttpServletRequest request, HttpServletResponse response) {
		String actionKey = WUC.LOTTERY_REPORT_COMPLEX_DETAILS;
		long t1 = System.currentTimeMillis();
		WebJSONObject json = new WebJSONObject(super.getAdminDataFactory());
		AdminUser uEntity = super.getCurrUser(session, request, response);
		if (uEntity != null) {
			if (super.hasAccess(uEntity, actionKey)) {
				String username = request.getParameter("username");
				Integer lotteryId = HttpUtil.getIntParameter(request, "lotteryId");
				String sTime = request.getParameter("sTime");
				String eTime = request.getParameter("eTime");
				Boolean self = HttpUtil.getBooleanParameter(request, "self");
				if(StringUtil.isNotNull(username)) {
					User targetUser = uDao.getByUsername(username);

					List<UserLotteryDetailsReportVO> result;
					if (self != null && self == true) {
						result = uLotteryDetailsReportService.reportSelf(targetUser.getId(), lotteryId, sTime, eTime);
					}
					else {
						result = uLotteryDetailsReportService.reportLowersAndSelf(targetUser.getId(), lotteryId, sTime, eTime);
					}
					json.accumulate("list", result);
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
	
	@RequestMapping(value = WUC.HISTORY_LOTTERY_REPORT_COMPLEX_DETAILS, method = { RequestMethod.POST })
	@ResponseBody
	public void HISTORY_LOTTERY_REPORT_COMPLEX_DETAILS(HttpSession session, HttpServletRequest request, HttpServletResponse response) {
		String actionKey = WUC.HISTORY_LOTTERY_REPORT_COMPLEX_DETAILS;
		long t1 = System.currentTimeMillis();
		WebJSONObject json = new WebJSONObject(super.getAdminDataFactory());
		AdminUser uEntity = super.getCurrUser(session, request, response);
		if (uEntity != null) {
			if (super.hasAccess(uEntity, actionKey)) {
				String username = request.getParameter("username");
				Integer lotteryId = HttpUtil.getIntParameter(request, "lotteryId");
				String sTime = request.getParameter("sTime");
				String eTime = request.getParameter("eTime");
				Boolean self = HttpUtil.getBooleanParameter(request, "self");
				if(StringUtil.isNotNull(username)) {
					User targetUser = uDao.getByUsername(username);

					List<HistoryUserLotteryDetailsReportVO> result;
					if (self != null && self == true) {
						result = uLotteryDetailsReportService.historyReportSelf(targetUser.getId(), lotteryId, sTime, eTime);
					}
					else {
						result = uLotteryDetailsReportService.historyReportLowersAndSelf(targetUser.getId(), lotteryId, sTime, eTime);
					}
					json.accumulate("list", result);
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
	
	@RequestMapping(value = WUC.LOTTERY_REPORT_PROFIT, method = { RequestMethod.POST })
	@ResponseBody
	public void LOTTERY_REPORT_PROFIT(HttpSession session, HttpServletRequest request, HttpServletResponse response) {
		String actionKey = WUC.LOTTERY_REPORT_PROFIT;
		long t1 = System.currentTimeMillis();
		WebJSONObject json = new WebJSONObject(super.getAdminDataFactory());
		AdminUser uEntity = super.getCurrUser(session, request, response);
		if (uEntity != null) {
			if (super.hasAccess(uEntity, actionKey)) {
				Integer type = HttpUtil.getIntParameter(request, "type");
				Integer lottery = HttpUtil.getIntParameter(request, "lottery");
				Integer ruleId = HttpUtil.getIntParameter(request, "ruleId");
				String sTime = HttpUtil.getStringParameterTrim(request, "sTime");
				String eTime = HttpUtil.getStringParameterTrim(request, "eTime");
				// List<UserBetsReportVO> result = uLotteryReportService.bReport(type, lottery, method, sTime, eTime);
				List<UserBetsReportVO> result = uLotteryDetailsReportService.sumUserBets(type, lottery, ruleId, sTime, eTime);
				json.accumulate("list", result);
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
	
	@RequestMapping(value = WUC.GAME_REPORT_COMPLEX, method = { RequestMethod.POST })
	@ResponseBody
	public void GAME_REPORT_COMPLEX(HttpSession session, HttpServletRequest request, HttpServletResponse response) {
		String actionKey = WUC.GAME_REPORT_COMPLEX;
		long t1 = System.currentTimeMillis();
		WebJSONObject json = new WebJSONObject(super.getAdminDataFactory());
		AdminUser uEntity = super.getCurrUser(session, request, response);
		if (uEntity != null) {
			if (super.hasAccess(uEntity, actionKey)) {
				String username = request.getParameter("username");
				String sTime = request.getParameter("sTime");
				String eTime = request.getParameter("eTime");
				if(StringUtil.isNotNull(username)) {
					User targetUser = uDao.getByUsername(username);
					List<UserGameReportVO> result = uGameReportService.report(targetUser.getId(), sTime, eTime);
					json.accumulate("list", result);
				} else {
					List<UserGameReportVO> result = uGameReportService.report(sTime, eTime);
					json.accumulate("list", result);
				}

				// 层级用户
				List<String> userLevels = uCodePointUtil.getUserLevels(username);
				json.accumulate("userLevels", userLevels);

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


	@RequestMapping(value = WUC.HISTORY_GAME_REPORT_COMPLEX, method = { RequestMethod.POST })
	@ResponseBody
	public void HISTORY_GAME_REPORT_COMPLEX(HttpSession session, HttpServletRequest request, HttpServletResponse response) {
		String actionKey = WUC.HISTORY_GAME_REPORT_COMPLEX;
		long t1 = System.currentTimeMillis();
		WebJSONObject json = new WebJSONObject(super.getAdminDataFactory());
		AdminUser uEntity = super.getCurrUser(session, request, response);
		if (uEntity != null) {
			if (super.hasAccess(uEntity, actionKey)) {
				String username = request.getParameter("username");
				String sTime = request.getParameter("sTime");
				String eTime = request.getParameter("eTime");
				if(StringUtil.isNotNull(username)) {
					User targetUser = uDao.getByUsername(username);
					List<HistoryUserGameReportVO> result = uGameReportService.historyReport(targetUser.getId(), sTime, eTime);
					json.accumulate("list", result);
				} else {
					List<HistoryUserGameReportVO> result = uGameReportService.historyReport(sTime, eTime);
					json.accumulate("list", result);
				}

				// 层级用户
				List<String> userLevels = uCodePointUtil.getUserLevels(username);
				json.accumulate("userLevels", userLevels);

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
	
	@RequestMapping(value = WUC.LOTTERY_REPORT_USER_PROFIT_RANKING, method = { RequestMethod.POST })
	@ResponseBody
	public void LOTTERY_REPORT_USER_PROFIT_RANKING(HttpSession session, HttpServletRequest request, HttpServletResponse response) {
		String actionKey = WUC.LOTTERY_REPORT_USER_PROFIT_RANKING;
		long t1 = System.currentTimeMillis();
		WebJSONObject json = new WebJSONObject(super.getAdminDataFactory());
		AdminUser uEntity = super.getCurrUser(session, request, response);
		if (uEntity != null) {
			if (super.hasAccess(uEntity, actionKey)) {
				String sTime = request.getParameter("sTime");
				String eTime = request.getParameter("eTime");
				Integer userId = HttpUtil.getIntParameter(request, "userId");

				List<UserProfitRankingVO> result = uLotteryReportService.listUserProfitRanking(userId, sTime, eTime, 0, 20);
				json.accumulate("list", result);
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
}