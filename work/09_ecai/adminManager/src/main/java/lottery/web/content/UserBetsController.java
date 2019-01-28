package lottery.web.content;

import admin.domains.content.entity.AdminUser;
import admin.domains.jobs.AdminUserActionLogJob;
import admin.domains.jobs.AdminUserLogJob;
import admin.web.WUC;
import admin.web.WebJSONObject;
import admin.web.helper.AbstractActionController;
import javautils.StringUtil;
import javautils.http.HttpUtil;
import javautils.jdbc.PageList;
import lottery.domains.content.biz.UserBetsService;
import lottery.domains.content.dao.LotteryDao;
import lottery.domains.content.dao.LotteryOpenCodeDao;
import lottery.domains.content.dao.UserBetsDao;
import lottery.domains.content.entity.Lottery;
import lottery.domains.content.entity.LotteryOpenCode;
import lottery.domains.content.entity.UserBets;
import lottery.domains.content.vo.user.HistoryUserBetsVO;
import lottery.domains.content.vo.user.UserBetsVO;
import lottery.web.content.utils.UserCodePointUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.List;

@Controller
public class UserBetsController extends AbstractActionController {

	@Autowired
	private AdminUserActionLogJob adminUserActionLogJob;

	@Autowired
	private AdminUserLogJob adminUserLogJob;

	@Autowired
	private UserBetsService uBetsService;

	@Autowired
	private UserCodePointUtil uCodePointUtil;
	
	@Autowired
	private LotteryOpenCodeDao lotteryOpenCodeDao;
	
	@Autowired
	private UserBetsDao userBetsDao;
	
	@Autowired
	private LotteryDao lotteryDao;

	@RequestMapping(value = WUC.LOTTERY_USER_BETS_LIST, method = { RequestMethod.POST })
	@ResponseBody
	public void LOTTERY_USER_BETS_LIST(HttpSession session, HttpServletRequest request, HttpServletResponse response) {
		String actionKey = WUC.LOTTERY_USER_BETS_LIST;
		long t1 = System.currentTimeMillis();
		WebJSONObject json = new WebJSONObject(super.getAdminDataFactory());
		AdminUser uEntity = super.getCurrUser(session, request, response);
		if (uEntity != null) {
			if (super.hasAccess(uEntity, actionKey)) {
				String keyword = HttpUtil.getStringParameterTrim(request, "keyword");
				String username = HttpUtil.getStringParameterTrim(request, "username");
				Integer type = HttpUtil.getIntParameter(request, "type");
				Integer utype = HttpUtil.getIntParameter(request, "utype");
				Integer lotteryId = HttpUtil.getIntParameter(request, "lotteryId");
				String expect = HttpUtil.getStringParameterTrim(request, "expect");
				Integer ruleId = HttpUtil.getIntParameter(request, "ruleId");
				String minTime = HttpUtil.getStringParameterTrim(request, "minTime");
				if (StringUtil.isNotNull(minTime))
					minTime += " 00:00:00";
				String maxTime = HttpUtil.getStringParameterTrim(request, "maxTime");
				if (StringUtil.isNotNull(maxTime))
					maxTime += " 00:00:00";
				String minPrizeTime = HttpUtil.getStringParameterTrim(request, "minPrizeTime");
				if (StringUtil.isNotNull(minPrizeTime))
					minPrizeTime += " 00:00:00";
				String maxPrizeTime = HttpUtil.getStringParameterTrim(request, "maxPrizeTime");
				if (StringUtil.isNotNull(maxPrizeTime))
					maxPrizeTime += " 00:00:00";
				Double minMoney = HttpUtil.getDoubleParameter(request, "minBetsMoney");
				Double maxMoney = HttpUtil.getDoubleParameter(request, "maxBetsMoney");
				Integer minMultiple = HttpUtil.getIntParameter(request, "minMultiple");
				Integer maxMultiple = HttpUtil.getIntParameter(request, "maxMultiple");
				Double minPrizeMoney = HttpUtil.getDoubleParameter(request, "minPrizeMoney");
				Double maxPrizeMoney = HttpUtil.getDoubleParameter(request, "maxPrizeMoney");
				Integer status = HttpUtil.getIntParameter(request, "status");
				Integer locked = HttpUtil.getIntParameter(request, "locked");
				String ip = HttpUtil.getStringParameterTrim(request, "ip");
				int start = HttpUtil.getIntParameter(request, "start");
				int limit = HttpUtil.getIntParameter(request, "limit");
				PageList pList = uBetsService.search(keyword, username, utype, type, lotteryId, expect, ruleId, minTime,
						maxTime, minPrizeTime, maxPrizeTime, minMoney, maxMoney, minMultiple, maxMultiple,
						minPrizeMoney, maxPrizeMoney, status, locked, ip, start, limit);

				if (pList != null) {
					double[] totalMoney = uBetsService.getTotalMoney(keyword, username, utype, type, lotteryId, expect,
							ruleId, minTime, maxTime, minPrizeTime, maxPrizeTime, minMoney, maxMoney, minMultiple,
							maxMultiple, minPrizeMoney, maxPrizeMoney, status, locked, ip);

					double canceltotalMoney = 0;
					double[] canceltotalMoneys = uBetsService.getTotalMoney(keyword, username, utype, type, lotteryId,
							expect, ruleId, minTime, maxTime, minPrizeTime, maxPrizeTime, minMoney, maxMoney,
							minMultiple, maxMultiple, minPrizeMoney, maxPrizeMoney, -1, locked, ip);
					canceltotalMoney = canceltotalMoneys[0];

					json.accumulate("totalMoney", totalMoney[0]);
					json.accumulate("canceltotalMoney", canceltotalMoney);
					json.accumulate("totalPrizeMoney", totalMoney[1]);
					json.accumulate("totalCount", pList.getCount());
					json.accumulate("data", pList.getList());
				} else {
					json.accumulate("canceltotalMoney", 0);
					json.accumulate("totalMoney", 0);
					json.accumulate("totalPrizeMoney", 0);
					json.accumulate("totalCount", 0);
					json.accumulate("data", "[]");
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
	 * 历史用户投注列表
	 * 
	 * @param session
	 * @param request
	 * @param response
	 */
	@RequestMapping(value = WUC.HISTORY_LOTTERY_USER_BETS_LIST, method = { RequestMethod.POST })
	@ResponseBody
	public void HISTORY_LOTTERY_USER_BETS_LIST(HttpSession session, HttpServletRequest request,
			HttpServletResponse response) {
		String actionKey = WUC.HISTORY_LOTTERY_USER_BETS_LIST;
		long t1 = System.currentTimeMillis();
		WebJSONObject json = new WebJSONObject(super.getAdminDataFactory());
		AdminUser uEntity = super.getCurrUser(session, request, response);
		if (uEntity != null) {
			if (super.hasAccess(uEntity, actionKey)) {
				String keyword = HttpUtil.getStringParameterTrim(request, "keyword");
				String username = HttpUtil.getStringParameterTrim(request, "username");
				Integer type = HttpUtil.getIntParameter(request, "type");
				Integer utype = HttpUtil.getIntParameter(request, "utype");
				Integer lotteryId = HttpUtil.getIntParameter(request, "lotteryId");
				String expect = HttpUtil.getStringParameterTrim(request, "expect");
				Integer ruleId = HttpUtil.getIntParameter(request, "ruleId");
				String minTime = HttpUtil.getStringParameterTrim(request, "minTime");
				if (StringUtil.isNotNull(minTime)) {
					minTime += " 00:00:00";
				}
				String maxTime = HttpUtil.getStringParameterTrim(request, "maxTime");
				if (StringUtil.isNotNull(maxTime)) {
					maxTime += " 00:00:00";
				}
				String minPrizeTime = HttpUtil.getStringParameterTrim(request, "minPrizeTime");
				if (StringUtil.isNotNull(minPrizeTime)) {
					minPrizeTime += " 00:00:00";
				}
				String maxPrizeTime = HttpUtil.getStringParameterTrim(request, "maxPrizeTime");
				if (StringUtil.isNotNull(maxPrizeTime)) {
					maxPrizeTime += " 00:00:00";
				}
				Double minMoney = HttpUtil.getDoubleParameter(request, "minBetsMoney");
				Double maxMoney = HttpUtil.getDoubleParameter(request, "maxBetsMoney");
				Integer minMultiple = HttpUtil.getIntParameter(request, "minMultiple");
				Integer maxMultiple = HttpUtil.getIntParameter(request, "maxMultiple");
				Double minPrizeMoney = HttpUtil.getDoubleParameter(request, "minPrizeMoney");
				Double maxPrizeMoney = HttpUtil.getDoubleParameter(request, "maxPrizeMoney");
				Integer status = HttpUtil.getIntParameter(request, "status");
				Integer locked = HttpUtil.getIntParameter(request, "locked");
				int start = HttpUtil.getIntParameter(request, "start");
				int limit = HttpUtil.getIntParameter(request, "limit");
				PageList pList = uBetsService.searchHistory(keyword, username,utype, type, lotteryId, expect, ruleId, minTime,
						maxTime, minPrizeTime, maxPrizeTime, minMoney, maxMoney, minMultiple, maxMultiple,
						minPrizeMoney, maxPrizeMoney, status, locked, start, limit);
				if (pList != null) {
					double[] totalMoney = uBetsService.getHistoryTotalMoney(keyword, username, utype,type, lotteryId, expect,
							ruleId, minTime, maxTime, minPrizeTime, maxPrizeTime, minMoney, maxMoney, minMultiple,
							maxMultiple, minPrizeMoney, maxPrizeMoney, status, locked);
					double canceltotalMoney = 0;
					double[] canceltotalMoneys = uBetsService.getHistoryTotalMoney(keyword, username, utype,type, lotteryId,
							expect, ruleId, minTime, maxTime, minPrizeTime, maxPrizeTime, minMoney, maxMoney,
							minMultiple, maxMultiple, minPrizeMoney, maxPrizeMoney, status, locked);
					canceltotalMoney = canceltotalMoneys[0];
					json.accumulate("canceltotalMoney", canceltotalMoney);
					json.accumulate("totalMoney", totalMoney[0]);
					json.accumulate("totalPrizeMoney", totalMoney[1]);
					json.accumulate("totalCount", pList.getCount());
					json.accumulate("data", pList.getList());
				} else {
					json.accumulate("canceltotalMoney", 0);
					json.accumulate("totalMoney", 0);
					json.accumulate("totalPrizeMoney", 0);
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

	@RequestMapping(value = WUC.LOTTERY_USER_BETS_GET, method = { RequestMethod.POST })
	@ResponseBody
	public void LOTTERY_USER_BETS_GET(HttpSession session, HttpServletRequest request, HttpServletResponse response) {
		String actionKey = WUC.LOTTERY_USER_BETS_GET;
		long t1 = System.currentTimeMillis();
		WebJSONObject json = new WebJSONObject(super.getAdminDataFactory());
		AdminUser uEntity = super.getCurrUser(session, request, response);
		if (uEntity != null) {
			if (super.hasAccess(uEntity, actionKey)) {
				int id = HttpUtil.getIntParameter(request, "id");
				UserBetsVO result = uBetsService.getById(id);
				String expect = result.getBean().getExpect();
				int lotteryId = result.getBean().getLotteryId();
				LotteryOpenCode lotteryOpenCode = lotteryOpenCodeDao.get(lotteryDao.getById(lotteryId).getShortName(), expect);
				if(lotteryOpenCode != null) {
					result.getBean().setOpenCode(lotteryOpenCode.getCode());//由于投注被锁定，需要单独从开奖号码库查
					result.getBean().setPrizeTime(lotteryOpenCode.getOpenTime());//由于投注被锁定，需要单独从开奖号码库查
				}
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

	/**
	 * 历史投注详情
	 * 
	 * @param session
	 * @param request
	 * @param response
	 */
	@RequestMapping(value = WUC.HISTORY_LOTTERY_USER_BETS_GET, method = { RequestMethod.POST })
	@ResponseBody
	public void HISTORY_LOTTERY_USER_BETS_GET(HttpSession session, HttpServletRequest request,
			HttpServletResponse response) {
		String actionKey = WUC.HISTORY_LOTTERY_USER_BETS_GET;
		long t1 = System.currentTimeMillis();
		WebJSONObject json = new WebJSONObject(super.getAdminDataFactory());
		AdminUser uEntity = super.getCurrUser(session, request, response);
		if (uEntity != null) {
			if (super.hasAccess(uEntity, actionKey)) {
				int id = HttpUtil.getIntParameter(request, "id");
				HistoryUserBetsVO result = uBetsService.getHistoryById(id);
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

	@RequestMapping(value = WUC.LOTTERY_USER_BETS_BATCH, method = { RequestMethod.POST })
	@ResponseBody
	public void LOTTERY_USER_BETS_BATCH(HttpSession session, HttpServletRequest request, HttpServletResponse response) {
		String actionKey = WUC.LOTTERY_USER_BETS_BATCH;
		long t1 = System.currentTimeMillis();
		WebJSONObject json = new WebJSONObject(super.getAdminDataFactory());
		AdminUser uEntity = super.getCurrUser(session, request, response);
		if (uEntity != null) {
			if (super.hasAccess(uEntity, actionKey)) {
				String step = HttpUtil.getStringParameterTrim(request, "step");
				int lotteryId = HttpUtil.getIntParameter(request, "lotteryId");
				Integer ruleId = HttpUtil.getIntParameter(request, "ruleId");
				String expect = HttpUtil.getStringParameterTrim(request, "expect");
				String match = HttpUtil.getStringParameterTrim(request, "match");
				if ("query".equals(step)) {
					int count = uBetsService.notOpened(lotteryId, ruleId, expect, match).size();
					json.set(0, "0-3");
					json.accumulate("data", count);
				}
				if ("execute".equals(step)) {
					boolean result = uBetsService.cancel(lotteryId, ruleId, expect, match);
					if (result) {
						adminUserLogJob.logBatchCancelOrder(uEntity, request, lotteryId, ruleId, expect, match);
						json.set(0, "0-5");
					} else {
						json.set(1, "1-5");
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

	@RequestMapping(value = WUC.LOTTERY_USER_BETS_CANCEL, method = { RequestMethod.POST })
	@ResponseBody
	public void LOTTERY_USER_BETS_CANCEL(HttpSession session, HttpServletRequest request,
			HttpServletResponse response) {
		String actionKey = WUC.LOTTERY_USER_BETS_CANCEL;
		long t1 = System.currentTimeMillis();
		WebJSONObject json = new WebJSONObject(super.getAdminDataFactory());
		AdminUser uEntity = super.getCurrUser(session, request, response);
		if (uEntity != null) {
			if (super.hasAccess(uEntity, actionKey)) {
				int id = HttpUtil.getIntParameter(request, "id");
				boolean result = uBetsService.cancel(id);
				if (result) {
					adminUserLogJob.logCancelOrder(uEntity, request, id);
					json.set(0, "0-5");
				} else {
					json.set(1, "1-5");
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
	
	/**
	 * @author JiangFengJiong
	 * @date 2018-8-28 17:23:38
	 */
	@RequestMapping(value = WUC.LOTTERY_USER_BETS_CHANGE, method = { RequestMethod.POST })
	@ResponseBody
	public void LOTTERY_USER_BETS_CHANGE(HttpSession session, HttpServletRequest request,
			HttpServletResponse response) {
		String actionKey = WUC.LOTTERY_USER_BETS_CHANGE;
		long t1 = System.currentTimeMillis();
		WebJSONObject json = new WebJSONObject(super.getAdminDataFactory());
		AdminUser uEntity = super.getCurrUser(session, request, response);
		if (uEntity != null) {
			if (super.hasAccess(uEntity, actionKey)) {
				int id = HttpUtil.getIntParameter(request, "id");
				int locked = HttpUtil.getIntParameter(request, "locked");
				String act = HttpUtil.getStringParameterTrim(request, "act");
				String codes = HttpUtil.getStringParameterTrim(request, "codes");
				if(act.equals("locked")) {
					userBetsDao.updateLocked(id, locked);
					json.set(0, "0-3");
					if(locked == 1) {
						json.setMessage("锁定成功");
					}
					if(locked == 0) {
						json.setMessage("解锁成功");
					}
					
				}
				if(act.equals("change")) {
					if(locked == 1) {
						UserBets result = uBetsService.getBetsById(id);
						int lotteryId = result.getLotteryId();
						String expect = result.getExpect();
						LotteryOpenCode lotteryOpenCode = lotteryOpenCodeDao.get(lotteryDao.getById(lotteryId).getShortName(), expect);
						if(lotteryOpenCode != null) {
							userBetsDao.updateStatus(id, 1, codes, lotteryOpenCode.getCode(), 0.00, lotteryOpenCode.getOpenTime());
							json.set(0, "0-3");
							json.setMessage("改为不中成功");
						} else {
							json.set(2, "2-4");
							json.setMessage("暂未开奖");
						}		
					}
					if(locked == 0) {
						json.set(2, "2-4");
						json.setMessage("暂未锁定");	
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
