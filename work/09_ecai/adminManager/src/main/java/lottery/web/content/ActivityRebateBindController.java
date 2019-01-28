package lottery.web.content;

import javautils.StringUtil;
import javautils.http.HttpUtil;
import javautils.jdbc.PageList;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import lottery.domains.content.biz.ActivityBindService;
import lottery.domains.content.biz.ActivityRebateService;
import lottery.domains.content.dao.ActivityRebateDao;
import lottery.domains.content.entity.ActivityRebate;
import lottery.domains.content.global.Global;
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
public class ActivityRebateBindController extends AbstractActionController {

	@Autowired
	private AdminUserActionLogJob adminUserActionLogJob;
	
	@Autowired
	private ActivityRebateDao activityRebateDao;
	
	@Autowired
	private ActivityRebateService activityRebateService;
	
	@Autowired
	private ActivityBindService activityBindService;
	
	@RequestMapping(value = WUC.ACTIVITY_REBATE_BIND_LIST, method = { RequestMethod.POST })
	@ResponseBody
	public void ACTIVITY_REBATE_SALARY_LIST(HttpSession session, HttpServletRequest request, HttpServletResponse response) {
		String actionKey = WUC.ACTIVITY_REBATE_BIND_LIST;
		long t1 = System.currentTimeMillis();
		WebJSONObject json = new WebJSONObject(super.getAdminDataFactory());
		AdminUser uEntity = super.getCurrUser(session, request, response);
		if (uEntity != null) {
			if (super.hasAccess(uEntity, actionKey)) {
				ActivityRebate bean = activityRebateDao.getByType(Global.ACTIVITY_REBATE_BIND);
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
	
	@RequestMapping(value = WUC.ACTIVITY_REBATE_BIND_EDIT, method = { RequestMethod.POST })
	@ResponseBody
	public synchronized void ACTIVITY_REBATE_BIND_EDIT(HttpSession session, HttpServletRequest request, HttpServletResponse response) {
		String actionKey = WUC.ACTIVITY_REBATE_BIND_EDIT;
		long t1 = System.currentTimeMillis();
		WebJSONObject json = new WebJSONObject(super.getAdminDataFactory());
		AdminUser uEntity = super.getCurrUser(session, request, response);
		if (uEntity != null) {
			if (super.hasAccess(uEntity, actionKey)) {
				int id = HttpUtil.getIntParameter(request, "id");
				String rules = request.getParameter("rules");
				if(StringUtil.isDouble(rules)) {
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
	
	@RequestMapping(value = WUC.ACTIVITY_REBATE_BIND_UPDATE_STATUS, method = { RequestMethod.POST })
	@ResponseBody
	public synchronized void ACTIVITY_REBATE_BIND_UPDATE_STATUS(HttpSession session, HttpServletRequest request, HttpServletResponse response) {
		String actionKey = WUC.ACTIVITY_REBATE_BIND_UPDATE_STATUS;
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
	
	@RequestMapping(value = WUC.ACTIVITY_REBATE_BIND_BILL_LIST, method = { RequestMethod.POST,RequestMethod.GET })
	@ResponseBody
	public void ACTIVITY_REBATE_BIND_BILL_LIST(HttpSession session, HttpServletRequest request, HttpServletResponse response) {
		String actionKey = WUC.ACTIVITY_REBATE_BIND_BILL_LIST;
		long t1 = System.currentTimeMillis();
		WebJSONObject json = new WebJSONObject(super.getAdminDataFactory());
		AdminUser uEntity = super.getCurrUser(session, request, response);
		if (uEntity != null) {
			if (super.hasAccess(uEntity, actionKey)) {
				String username = request.getParameter("username");
				String date = request.getParameter("date");
				String upperUser = request.getParameter("upperUser");
				String keyword = request.getParameter("keyword");
				Integer status = HttpUtil.getIntParameter(request, "status");
				int start = HttpUtil.getIntParameter(request, "start");
				int limit = HttpUtil.getIntParameter(request, "limit");
				PageList pList = activityBindService.search(username, upperUser, date, keyword, status, start, limit);
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
	
	
	
	@RequestMapping(value = WUC.ACTIVITY_REBATE_BIND_BILL_CONFIRM, method = { RequestMethod.POST,RequestMethod.GET })
	@ResponseBody
	public void ACTIVITY_REBATE_BIND_BILL_CONFIRM(HttpSession session, HttpServletRequest request, HttpServletResponse response) {
		String actionKey = WUC.ACTIVITY_REBATE_BIND_BILL_CONFIRM;
		long t1 = System.currentTimeMillis();
		WebJSONObject json = new WebJSONObject(super.getAdminDataFactory());
		AdminUser uEntity = super.getCurrUser(session, request, response);
		if (uEntity != null) {
			if (super.hasAccess(uEntity, actionKey)) {
				int id = HttpUtil.getIntParameter(request, "id");
				String confirm = request.getParameter("confirm");
				boolean flag = false;
				if("y".equalsIgnoreCase(confirm)) {
					flag = true;
				} else {
					boolean result = activityBindService.check(id);
					if(!result) {
						flag = true;
					} else {
						json.set(2, "2-2022");
					}
				}
				if(flag) {
//					boolean result = activityBindService.agree(id);
					boolean result = false;//"取消发放";
					if(result) {
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
	
	@RequestMapping(value = WUC.ACTIVITY_REBATE_BIND_BILL_REFUSE, method = { RequestMethod.POST,RequestMethod.GET })
	@ResponseBody
	public void ACTIVITY_REBATE_BIND_BILL_REFUSE(HttpSession session, HttpServletRequest request, HttpServletResponse response) {
		String actionKey = WUC.ACTIVITY_REBATE_BIND_BILL_CONFIRM;
		long t1 = System.currentTimeMillis();
		WebJSONObject json = new WebJSONObject(super.getAdminDataFactory());
		AdminUser uEntity = super.getCurrUser(session, request, response);
		if (uEntity != null) {
			if (super.hasAccess(uEntity, actionKey)) {
				String strs = request.getParameter("ids");
				int[] ids = StringUtil.transStringToIntArray(strs, ",");
				for (int id : ids) {
					activityBindService.refuse(id);
				}
				json.set(0, "0-5");
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
	
	@RequestMapping(value = WUC.ACTIVITY_REBATE_BIND_GET, method = { RequestMethod.POST })
	@ResponseBody
	public void ACTIVITY_REBATE_BIND_GET(HttpSession session, HttpServletRequest request, HttpServletResponse response) {
		Integer id = HttpUtil.getIntParameter(request, "id");
		ActivityRebate bean = activityRebateDao.getById(id);
		JSONObject json = JSONObject.fromObject(bean);
		HttpUtil.write(response, json.toString(), HttpUtil.json);
	}
	
}