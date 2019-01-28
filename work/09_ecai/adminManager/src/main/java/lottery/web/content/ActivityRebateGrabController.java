package lottery.web.content;

import java.util.List;
import java.util.Map;

import javautils.http.HttpUtil;
import javautils.jdbc.PageList;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import lottery.domains.content.biz.ActivityGrabService;
import lottery.domains.content.biz.ActivityRebateService;
import lottery.domains.content.dao.ActivityRebateDao;
import lottery.domains.content.entity.ActivityRebate;
import lottery.domains.content.entity.activity.RebateRulesGrab;
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
public class ActivityRebateGrabController extends AbstractActionController {

	@Autowired
	private AdminUserActionLogJob adminUserActionLogJob;
	
	@Autowired
	private ActivityRebateDao activityRebateDao;
	
	@Autowired
	private ActivityRebateService activityRebateService;
	
	@Autowired
	private ActivityGrabService activityGrabService;
	
	@RequestMapping(value = WUC.ACTIVITY_REBATE_GRAB_PACKAGE_BILL, method = { RequestMethod.POST })
	@ResponseBody
	public void ACTIVITY_REBATE_GRAB_PACKAGE_BILL(HttpSession session, HttpServletRequest request, HttpServletResponse response) {
		String actionKey = WUC.ACTIVITY_REBATE_GRAB_PACKAGE_BILL;
		long t1 = System.currentTimeMillis();
		WebJSONObject json = new WebJSONObject(super.getAdminDataFactory());
		AdminUser uEntity = super.getCurrUser(session, request, response);
		if (uEntity != null) {
			if (super.hasAccess(uEntity, actionKey)) {
				String username = request.getParameter("username");
				String date = request.getParameter("date");
				int start = HttpUtil.getIntParameter(request, "start");
				int limit = HttpUtil.getIntParameter(request, "limit");
				PageList pList = activityGrabService.searchBill(username, date, start, limit);
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
	
	
	@RequestMapping(value = WUC.ACTIVITY_REBATE_GRAB_PACKAGE_LIST, method = { RequestMethod.POST })
	@ResponseBody
	public void ACTIVITY_REBATE_GRAB_PACKAGE_LIST(HttpSession session, HttpServletRequest request, HttpServletResponse response) {
		String actionKey = WUC.ACTIVITY_REBATE_GRAB_PACKAGE_LIST;
		long t1 = System.currentTimeMillis();
		WebJSONObject json = new WebJSONObject(super.getAdminDataFactory());
		AdminUser uEntity = super.getCurrUser(session, request, response);
		if (uEntity != null) {
			if (super.hasAccess(uEntity, actionKey)) {
				ActivityRebate bean = activityRebateDao.getByType(Global.ACTIVITY_REBATE_GRAB);
				json.accumulate("data", bean);
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
	
	@SuppressWarnings("unchecked")
	@RequestMapping(value = WUC.ACTIVITY_REBATE_GRAB_PACKAGE_EDIT, method = { RequestMethod.POST })
	@ResponseBody
	public void ACTIVITY_REBATE_GRAB_PACKAGE_EDIT(HttpSession session, HttpServletRequest request, HttpServletResponse response) {
		String actionKey = WUC.ACTIVITY_REBATE_GRAB_PACKAGE_EDIT;
		long t1 = System.currentTimeMillis();
		WebJSONObject json = new WebJSONObject(super.getAdminDataFactory());
		AdminUser uEntity = super.getCurrUser(session, request, response);
		if (uEntity != null) {
			if (super.hasAccess(uEntity, actionKey)) {
				int id = HttpUtil.getIntParameter(request, "id");
				String rules = request.getParameter("rules");
				// 进行规则转换并验证
				List<RebateRulesGrab> rewardRules = (List<RebateRulesGrab>) JSONArray.toCollection(JSONArray.fromObject(rules), RebateRulesGrab.class);
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
	
	@RequestMapping(value = WUC.ACTIVITY_REBATE_GRAB_PACKAGE_STATUS, method = { RequestMethod.POST })
	@ResponseBody
	public void ACTIVITY_REBATE_GRAB_PACKAGE_STATUS(HttpSession session, HttpServletRequest request, HttpServletResponse response) {
		String actionKey = WUC.ACTIVITY_REBATE_GRAB_PACKAGE_STATUS;
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
	
	@RequestMapping(value = WUC.ACTIVITY_REBATE_GRAB_PACKAGE_GET, method = { RequestMethod.POST })
	@ResponseBody
	public void ACTIVITY_REBATE_GRAB_PACKAGE_GET(HttpSession session, HttpServletRequest request, HttpServletResponse response) {
		Integer id = HttpUtil.getIntParameter(request, "id");
		ActivityRebate bean = activityRebateDao.getById(id);
		JSONObject json = JSONObject.fromObject(bean);
		HttpUtil.write(response, json.toString(), HttpUtil.json);
	}
	
	//获取已发放红包金额新信息
	@RequestMapping(value = WUC.ACTIVITY_REBATE_GRAB_PACKAGE_OUTTOTAL, method = { RequestMethod.POST })
	@ResponseBody
	public void ACTIVITY_REBATE_GRAB_PACKAGE_OUTTOTAL(HttpSession session, HttpServletRequest request, HttpServletResponse response) {
		WebJSONObject json = new WebJSONObject(super.getAdminDataFactory());
		Map<String, Double> outTotal = activityGrabService.getOutTotalInfo();
		json.accumulate("outTotal", outTotal);
		HttpUtil.write(response, json.toString(), HttpUtil.json);
	}
	
}