package admin.web.content;

import java.util.List;

import javautils.StringUtil;
import javautils.http.HttpUtil;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import net.sf.json.JSONObject;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import admin.domains.content.biz.AdminUserRoleService;
import admin.domains.content.entity.AdminUser;
import admin.domains.content.entity.AdminUserRole;
import admin.domains.content.vo.AdminUserRoleVO;
import admin.domains.jobs.AdminUserActionLogJob;
import admin.web.WUC;
import admin.web.WebJSONObject;
import admin.web.helper.AbstractActionController;

@Controller
public class AdminUserRoleController extends AbstractActionController {

	@Autowired
	private AdminUserRoleService adminUserRoleService;
	
	@Autowired
	private AdminUserActionLogJob adminUserActionLogJob;

	@RequestMapping(value = WUC.ADMIN_USER_ROLE_LIST, method = { RequestMethod.POST })
	@ResponseBody
	public void ADMIN_USER_ROLE_LIST(HttpSession session, HttpServletRequest request, HttpServletResponse response) {
		String actionKey = WUC.ADMIN_USER_ROLE_LIST;
		long t1 = System.currentTimeMillis();
		WebJSONObject json = new WebJSONObject(super.getAdminDataFactory());
		AdminUser uEntity = super.getCurrUser(session, request, response);
		if (uEntity != null) {
			if (super.hasAccess(uEntity, actionKey)) {
				List<AdminUserRole> list = adminUserRoleService.listAll(uEntity.getRoleId());
				json.accumulate("list", list);
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
	
	@RequestMapping(value = WUC.ADMIN_USER_ROLE_TREE_LIST, method = { RequestMethod.POST })
	@ResponseBody
	public void ADMIN_USER_ROLE_TREE_LIST(HttpSession session, HttpServletRequest request, HttpServletResponse response) {
		WebJSONObject json = new WebJSONObject(super.getAdminDataFactory());
		AdminUser uEntity = super.getCurrUser(session, request, response);
		if (uEntity != null) {
			List<AdminUserRole> list = adminUserRoleService.listTree(uEntity.getRoleId());
			json.accumulate("list", list);
			json.set(0, "0-3");
		} else {
			json.set(2, "2-6");
		}
		HttpUtil.write(response, json.toString(), HttpUtil.json);
	}
	
	@RequestMapping(value = WUC.ADMIN_USER_ROLE_CHECK_EXIST, method = { RequestMethod.POST })
	@ResponseBody
	public void ADMIN_USER_ROLE_CHECK_EXIST(HttpSession session, HttpServletRequest request, HttpServletResponse response) {
		String id = request.getParameter("id");
		String name = request.getParameter("name");
		AdminUserRoleVO bean = adminUserRoleService.getByName(name);
		String isExist = "false";
		if(bean != null) {
			if(StringUtil.isNotNull(id) && StringUtil.isInteger(id)) {
				if(bean.getBean().getId() == Integer.parseInt(id)) {
					isExist = "true";
				}
			}
		} else {
			isExist = "true";
		}
		HttpUtil.write(response, isExist, HttpUtil.json);
	}
	
	@RequestMapping(value = WUC.ADMIN_USER_ROLE_GET, method = { RequestMethod.POST })
	@ResponseBody
	public void ADMIN_USER_ROLE_GET(HttpSession session, HttpServletRequest request, HttpServletResponse response) {
		int id = HttpUtil.getIntParameter(request, "id");
		AdminUserRoleVO bean = adminUserRoleService.getById(id);
		JSONObject json = JSONObject.fromObject(bean);
		HttpUtil.write(response, json.toString(), HttpUtil.json);
	}
	
	@RequestMapping(value = WUC.ADMIN_USER_ROLE_ADD, method = { RequestMethod.POST })
	@ResponseBody
	public void ADMIN_USER_ROLE_ADD(HttpSession session, HttpServletRequest request, HttpServletResponse response) {
		String actionKey = WUC.ADMIN_USER_ROLE_ADD;
		long t1 = System.currentTimeMillis();
		WebJSONObject json = new WebJSONObject(super.getAdminDataFactory());
		AdminUser uEntity = super.getCurrUser(session, request, response);
		if (uEntity != null) {
			if (super.hasAccess(uEntity, actionKey)) {
				String name = request.getParameter("name");
				int upid = HttpUtil.getIntParameter(request, "upid");
				String description = request.getParameter("description");
				int sort = HttpUtil.getIntParameter(request, "sort");
				boolean result = adminUserRoleService.add(name, upid, description, sort);
				if(result) {
					json.set(0, "0-6");
				} else {
					json.set(1, "1-6");
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
	
	@RequestMapping(value = WUC.ADMIN_USER_ROLE_EDIT, method = { RequestMethod.POST })
	@ResponseBody
	public void ADMIN_USER_ROLE_EDIT(HttpSession session, HttpServletRequest request, HttpServletResponse response) {
		String actionKey = WUC.ADMIN_USER_ROLE_EDIT;
		long t1 = System.currentTimeMillis();
		WebJSONObject json = new WebJSONObject(super.getAdminDataFactory());
		AdminUser uEntity = super.getCurrUser(session, request, response);
		if (uEntity != null) {
			if (super.hasAccess(uEntity, actionKey)) {
				int id = HttpUtil.getIntParameter(request, "id");
				String name = request.getParameter("name");
				int upid = HttpUtil.getIntParameter(request, "upid");
				String description = request.getParameter("description");
				int sort = HttpUtil.getIntParameter(request, "sort");
				boolean result = adminUserRoleService.edit(id, name, upid, description, sort);
				if(result) {
					json.set(0, "0-6");
				} else {
					json.set(1, "1-6");
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
	
	@RequestMapping(value = WUC.ADMIN_USER_ROLE_UPDATE_STATUS, method = { RequestMethod.POST })
	@ResponseBody
	public void ADMIN_USER_ROLE_UPDATE_STATUS(HttpSession session, HttpServletRequest request, HttpServletResponse response) {
		String actionKey = WUC.ADMIN_USER_ROLE_UPDATE_STATUS;
		long t1 = System.currentTimeMillis();
		WebJSONObject json = new WebJSONObject(super.getAdminDataFactory());
		AdminUser uEntity = super.getCurrUser(session, request, response);
		if (uEntity != null) {
			if (super.hasAccess(uEntity, actionKey)) {
				int id = HttpUtil.getIntParameter(request, "id");
				int status = HttpUtil.getIntParameter(request, "status");
				boolean result = adminUserRoleService.updateStatus(id, status);
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
	
	@RequestMapping(value = WUC.ADMIN_USER_ROLE_SAVE_ACCESS, method = { RequestMethod.POST })
	@ResponseBody
	public void ADMIN_USER_ROLE_SAVE_ACCESS(HttpSession session, HttpServletRequest request, HttpServletResponse response) {
		String actionKey = WUC.ADMIN_USER_ROLE_SAVE_ACCESS;
		long t1 = System.currentTimeMillis();
		WebJSONObject json = new WebJSONObject(super.getAdminDataFactory());
		AdminUser uEntity = super.getCurrUser(session, request, response);
		if (uEntity != null) {
			if (super.hasAccess(uEntity, actionKey)) {
				int id = HttpUtil.getIntParameter(request, "id");
				String ids = request.getParameter("ids");
				boolean result = adminUserRoleService.saveAccess(id, ids);
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
	
}