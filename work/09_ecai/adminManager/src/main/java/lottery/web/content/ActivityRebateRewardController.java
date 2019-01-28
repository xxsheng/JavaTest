package lottery.web.content;

import java.util.List;

import javautils.http.HttpUtil;
import javautils.jdbc.PageList;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import lottery.domains.content.biz.ActivityRebateService;
import lottery.domains.content.biz.ActivityRewardService;
import lottery.domains.content.dao.ActivityRebateDao;
import lottery.domains.content.entity.ActivityRebate;
import lottery.domains.content.entity.activity.RebateRulesReward;
import lottery.domains.content.global.Global;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import admin.domains.content.entity.AdminUser;
import admin.domains.jobs.AdminUserActionLogJob;
import admin.web.WUC;
import admin.web.WebJSONObject;
import admin.web.helper.AbstractActionController;

@Controller
public class ActivityRebateRewardController extends AbstractActionController {

	@Autowired
	private AdminUserActionLogJob adminUserActionLogJob;
	
	@Autowired
	private ActivityRebateDao activityRebateDao;
	
	@Autowired
	private ActivityRebateService activityRebateService;
	
	@Autowired
	private ActivityRewardService activityRewardService;
	
	@RequestMapping(value = WUC.ACTIVITY_REBATE_REWARD_LIST, method = { RequestMethod.POST })
	@ResponseBody
	public void ACTIVITY_REBATE_REWARD_LIST(HttpSession session, HttpServletRequest request, HttpServletResponse response) {
		String actionKey = WUC.ACTIVITY_REBATE_REWARD_LIST;
		long t1 = System.currentTimeMillis();
		WebJSONObject json = new WebJSONObject(super.getAdminDataFactory());
		AdminUser uEntity = super.getCurrUser(session, request, response);
		if (uEntity != null) {
			if (super.hasAccess(uEntity, actionKey)) {
				String type = request.getParameter("type");
				if("xf".equals(type)) {
					ActivityRebate bean = activityRebateDao.getByType(Global.ACTIVITY_REBATE_REWARD_XIAOFEI);
					json.accumulate("data", bean);
				}
				if("yk".equals(type)) {
					ActivityRebate bean = activityRebateDao.getByType(Global.ACTIVITY_REBATE_REWARD_YINGKUI);
					json.accumulate("data", bean);
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
	
	@RequestMapping(value = WUC.ACTIVITY_REBATE_REWARD_EDIT, method = { RequestMethod.POST })
	@ResponseBody
	public synchronized void ACTIVITY_REBATE_REWARD_EDIT(HttpSession session, HttpServletRequest request, HttpServletResponse response) {
		String actionKey = WUC.ACTIVITY_REBATE_REWARD_EDIT;
		long t1 = System.currentTimeMillis();
		WebJSONObject json = new WebJSONObject(super.getAdminDataFactory());
		AdminUser uEntity = super.getCurrUser(session, request, response);
		if (uEntity != null) {
			if (super.hasAccess(uEntity, actionKey)) {
				int id = HttpUtil.getIntParameter(request, "id");
				String rules = request.getParameter("rules");
				// 进行规则转换并验证
				@SuppressWarnings("unchecked")
				List<RebateRulesReward> rewardRules = (List<RebateRulesReward>) JSONArray.toCollection(JSONArray.fromObject(rules), RebateRulesReward.class);
				if(rewardRules != null && rewardRules.size() > 0) {
					boolean result = activityRebateService.edit(id, rules, null, null);
					if(result) {
						json.set(0, "0-5");
					} else {
						json.set(1, "1-5");
					}
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
	
	@RequestMapping(value = WUC.ACTIVITY_REBATE_REWARD_UPDATE_STATUS, method = { RequestMethod.POST })
	@ResponseBody
	public synchronized void ACTIVITY_REBATE_REWARD_UPDATE_STATUS(HttpSession session, HttpServletRequest request, HttpServletResponse response) {
		String actionKey = WUC.ACTIVITY_REBATE_REWARD_UPDATE_STATUS;
		long t1 = System.currentTimeMillis();
		WebJSONObject json = new WebJSONObject(super.getAdminDataFactory());
		AdminUser uEntity = super.getCurrUser(session, request, response);
		if (uEntity != null) {
			if (super.hasAccess(uEntity, actionKey)) {
				int id = HttpUtil.getIntParameter(request, "id");
				int status = HttpUtil.getIntParameter(request, "status");
				boolean result = activityRebateService.updateStatus(id, status);
				if(result) {
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
	
	@RequestMapping(value = WUC.ACTIVITY_REBATE_REWARD_BILL_LIST, method = { RequestMethod.POST,RequestMethod.GET })
	@ResponseBody
	public void ACTIVITY_REBATE_REWARD_BILL_LIST(HttpSession session, HttpServletRequest request, HttpServletResponse response) {
		String actionKey = WUC.ACTIVITY_REBATE_REWARD_BILL_LIST;
		long t1 = System.currentTimeMillis();
		WebJSONObject json = new WebJSONObject(super.getAdminDataFactory());
		AdminUser uEntity = super.getCurrUser(session, request, response);
		if (uEntity != null) {
			if (super.hasAccess(uEntity, actionKey)) {
				String username = request.getParameter("username");
				String date = request.getParameter("date");
				Integer type = HttpUtil.getIntParameter(request, "type");
				Integer status = HttpUtil.getIntParameter(request, "status");
				int start = HttpUtil.getIntParameter(request, "start");
				int limit = HttpUtil.getIntParameter(request, "limit");
				PageList pList = activityRewardService.search(username, date, type, status, start, limit);
				if(pList != null) {
					json.accumulate("totalCount", pList.getCount());
					json.accumulate("data", pList.getList());
				} else {
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
	
	@RequestMapping(value = WUC.ACTIVITY_REBATE_REWARD_BILL_CALCULATE, method = { RequestMethod.POST,RequestMethod.GET })
	@ResponseBody
	public void ACTIVITY_REBATE_REWARD_BILL_CALCULATE(HttpSession session, HttpServletRequest request, HttpServletResponse response) {
		String actionKey = WUC.ACTIVITY_REBATE_REWARD_BILL_CALCULATE;
		long t1 = System.currentTimeMillis();
		WebJSONObject json = new WebJSONObject(super.getAdminDataFactory());
		AdminUser uEntity = super.getCurrUser(session, request, response);
		if (uEntity != null) {
			if (super.hasAccess(uEntity, actionKey)) {
				String date = request.getParameter("date");
				boolean xfResult = false;
				try {
					xfResult = activityRewardService.calculate(Global.ACTIVITY_REBATE_REWARD_XIAOFEI, date);
				} catch (Exception e) {
					System.out.println("消费佣金计算失败。");
				}
				boolean ykResult = false;
				try {
					ykResult = activityRewardService.calculate(Global.ACTIVITY_REBATE_REWARD_YINGKUI, date);
				} catch (Exception e) {
					System.out.println("盈亏佣金计算失败。");
				}
				json.accumulate("xfResult", xfResult);
				json.accumulate("ykResult", ykResult);
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
	
	@RequestMapping(value = WUC.ACTIVITY_REBATE_REWARD_BILL_CONFIRM, method = { RequestMethod.POST,RequestMethod.GET })
	@ResponseBody
	public void ACTIVITY_REBATE_REWARD_BILL_CONFIRM(HttpSession session, HttpServletRequest request, HttpServletResponse response) {
		String actionKey = WUC.ACTIVITY_REBATE_REWARD_BILL_CONFIRM;
		long t1 = System.currentTimeMillis();
		WebJSONObject json = new WebJSONObject(super.getAdminDataFactory());
		AdminUser uEntity = super.getCurrUser(session, request, response);
		if (uEntity != null) {
			if (super.hasAccess(uEntity, actionKey)) {
				String date = request.getParameter("date");
				boolean result = activityRewardService.agreeAll(date);
				if(result) {
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
	
	@RequestMapping(value = WUC.ACTIVITY_REBATE_REWARD_GET, method = { RequestMethod.POST })
	@ResponseBody
	public void ACTIVITY_REBATE_REWARD_GET(HttpSession session, HttpServletRequest request, HttpServletResponse response) {
		Integer id = HttpUtil.getIntParameter(request, "id");
		ActivityRebate bean = activityRebateDao.getById(id);
		JSONObject json = JSONObject.fromObject(bean);
		HttpUtil.write(response, json.toString(), HttpUtil.json);
	}
	
}