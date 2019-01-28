package lottery.web.content;

import java.util.List;

import javautils.http.HttpUtil;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import lottery.domains.content.biz.SysConfigService;
import lottery.domains.content.entity.SysConfig;
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
public class SysConfigController extends AbstractActionController {

	@Autowired
	private AdminUserActionLogJob adminUserActionLogJob;

	@Autowired
	private SysConfigService sysConfigService;

	@RequestMapping(value = WUC.LOTTERY_SYS_CONFIG_GET, method = { RequestMethod.POST })
	@ResponseBody
	public void LOTTERY_SYS_CONFIG_GET(HttpSession session,
			HttpServletRequest request, HttpServletResponse response) {
		String group = request.getParameter("group");
		String key = request.getParameter("key");
		SysConfig bean = sysConfigService.get(group, key);
		JSONObject json = JSONObject.fromObject(bean);
		HttpUtil.write(response, json.toString(), HttpUtil.json);
	}

	@RequestMapping(value = WUC.LOTTERY_SYS_CONFIG_LIST, method = { RequestMethod.POST })
	@ResponseBody
	public void LOTTERY_SYS_CONFIG_LIST(HttpSession session,
			HttpServletRequest request, HttpServletResponse response) {
		String actionKey = WUC.LOTTERY_SYS_CONFIG_LIST;
		long t1 = System.currentTimeMillis();
		WebJSONObject json = new WebJSONObject(super.getAdminDataFactory());
		AdminUser uEntity = super.getCurrUser(session, request, response);
		if (uEntity != null) {
			if (super.hasAccess(uEntity, actionKey)) {
				List<SysConfig> slist = sysConfigService.listAll();
				json.accumulate("slist", slist);
				List<String> alist = super.listSysConfigKey(uEntity);
				json.accumulate("alist", alist);
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

	@RequestMapping(value = WUC.LOTTERY_SYS_CONFIG_UPDATE, method = { RequestMethod.POST })
	@ResponseBody
	public void LOTTERY_SYS_CONFIG_UPDATE(HttpSession session,
			HttpServletRequest request, HttpServletResponse response) {
		String actionKey = WUC.LOTTERY_SYS_CONFIG_UPDATE;
		long t1 = System.currentTimeMillis();
		WebJSONObject json = new WebJSONObject(super.getAdminDataFactory());
		AdminUser uEntity = super.getCurrUser(session, request, response);
		if (uEntity != null) {
			if (super.hasAccess(uEntity, actionKey)) {
				String key = request.getParameter("key");
				String group = request.getParameter("group");
				String value = request.getParameter("value");
				boolean result = sysConfigService.update(group, key, value);
				if (result) {
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

}