package lottery.web.content;

import java.util.List;

import javautils.http.HttpUtil;
import javautils.jdbc.PageList;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import lottery.domains.content.biz.ActivityCostService;
import lottery.domains.content.biz.ActivityRebateService;
import lottery.domains.content.dao.ActivityRebateDao;
import lottery.domains.content.entity.ActivityRebate;
import lottery.domains.content.entity.activity.RebateRulesCost;
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
public class ActivityRebateCostController extends AbstractActionController {

	@Autowired
	private AdminUserActionLogJob adminUserActionLogJob;
	
	@Autowired
	private ActivityRebateDao activityRebateDao;
	
	@Autowired
	private ActivityRebateService activityRebateService;
	@Autowired
	private ActivityCostService activityCostService;
	
	@RequestMapping(value = WUC.ACTIVITY_REBATE_COST_LIST, method = { RequestMethod.POST })
	@ResponseBody
	public void ACTIVITY_REBATE_COST_LIST(HttpSession session, HttpServletRequest request, HttpServletResponse response) {
		String actionKey = WUC.ACTIVITY_REBATE_COST_LIST;
		long t1 = System.currentTimeMillis();
		WebJSONObject json = new WebJSONObject(super.getAdminDataFactory());
		AdminUser uEntity = super.getCurrUser(session, request, response);
		if (uEntity != null) {
			if (super.hasAccess(uEntity, actionKey)) {
				ActivityRebate bean = activityRebateDao.getByType(Global.ACTIVITY_REBATE_COST);
				json.accumulate("data", bean);
				json.set(0, "0-3");
			} else {
				ActivityRebate bean = new ActivityRebate();
				bean.setId(Global.ACTIVITY_REBATE_COST);
				bean.setType(Global.ACTIVITY_REBATE_COST);
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
	
	@RequestMapping(value = WUC.ACTIVITY_REBATE_COST_GET, method = { RequestMethod.POST })
	@ResponseBody
	public void ACTIVITY_REBATE_COST_GET(HttpSession session, HttpServletRequest request, HttpServletResponse response) {
		Integer id = HttpUtil.getIntParameter(request, "id");
		ActivityRebate bean = activityRebateDao.getById(id);
		JSONObject json = JSONObject.fromObject(bean);
		HttpUtil.write(response, json.toString(), HttpUtil.json);
	}
	
	
	@RequestMapping(value = WUC.ACTIVITY_REBATE_COST_EDIT, method = { RequestMethod.POST })
	@ResponseBody
	public void ACTIVITY_REBATE_COST_EDIT(HttpSession session, HttpServletRequest request, HttpServletResponse response) {
		String actionKey = WUC.ACTIVITY_REBATE_COST_EDIT;
		long t1 = System.currentTimeMillis();
		WebJSONObject json = new WebJSONObject(super.getAdminDataFactory());
		AdminUser uEntity = super.getCurrUser(session, request, response);
		if (uEntity != null) {
			if (super.hasAccess(uEntity, actionKey)) {
				int id = HttpUtil.getIntParameter(request, "id");
				String rules = request.getParameter("rules");
				// 进行规则转换并验证
				@SuppressWarnings("unchecked")
				List<RebateRulesCost> rewardRules = (List<RebateRulesCost>) JSONArray.toCollection(JSONArray.fromObject(rules), RebateRulesCost.class);
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
	
	
	

	@RequestMapping(value = WUC.ACTIVITY_REBATE_COST_UPDATE_STATUS, method = { RequestMethod.POST })
	@ResponseBody
	public void ACTIVITY_REBATE_COST_UPDATE_STATUS(HttpSession session, HttpServletRequest request, HttpServletResponse response) {
		String actionKey = WUC.ACTIVITY_REBATE_COST_UPDATE_STATUS;
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
	
	@RequestMapping(value = WUC.ACTIVITY_REBATE_COST_BILL, method = { RequestMethod.POST })
	@ResponseBody
	public void ACTIVITY_REBATE_COST_BILL(HttpSession session, HttpServletRequest request, HttpServletResponse response) {
		String actionKey = WUC.ACTIVITY_REBATE_COST_BILL;
		long t1 = System.currentTimeMillis();
		WebJSONObject json = new WebJSONObject(super.getAdminDataFactory());
		AdminUser uEntity = super.getCurrUser(session, request, response);
		if (uEntity != null) {
			if (super.hasAccess(uEntity, actionKey)) {
				String username = request.getParameter("username");
				String date = request.getParameter("date");
				int start = HttpUtil.getIntParameter(request, "start");
				int limit = HttpUtil.getIntParameter(request, "limit");
				PageList pList = activityCostService.searchBill(username, date, start, limit);
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
	
	
}