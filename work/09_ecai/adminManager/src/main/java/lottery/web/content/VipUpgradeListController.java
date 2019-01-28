// package lottery.web.content;
//
// import javautils.http.HttpUtil;
// import javautils.jdbc.PageList;
// import lottery.domains.content.biz.UserService;
// import lottery.domains.content.biz.VipUpgradeListService;
// import lottery.domains.content.jobs.VipUpgradeJob;
//
// import javax.servlet.http.HttpServletRequest;
// import javax.servlet.http.HttpServletResponse;
// import javax.servlet.http.HttpSession;
//
// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.stereotype.Controller;
// import org.springframework.web.bind.annotation.RequestMapping;
// import org.springframework.web.bind.annotation.RequestMethod;
// import org.springframework.web.bind.annotation.ResponseBody;
//
// import admin.domains.content.entity.AdminUser;
// import admin.domains.jobs.AdminUserActionLogJob;
// import admin.web.WUC;
// import admin.web.WebJSONObject;
// import admin.web.helper.AbstractActionController;
//
// @Controller
// public class VipUpgradeListController extends AbstractActionController {
//
// 	@Autowired
// 	private VipUpgradeJob vUpgradeJob;
//
// 	@Autowired
// 	private AdminUserActionLogJob adminUserActionLogJob;
//
// 	@Autowired
// 	private VipUpgradeListService vUpgradeListService;
//
// 	@RequestMapping(value = WUC.VIP_UPGRADE_LIST_LIST, method = { RequestMethod.POST })
// 	@ResponseBody
// 	public void VIP_UPGRADE_LIST_LIST(HttpSession session, HttpServletRequest request, HttpServletResponse response) {
// 		String actionKey = WUC.VIP_UPGRADE_LIST_LIST;
// 		long t1 = System.currentTimeMillis();
// 		WebJSONObject json = new WebJSONObject(super.getAdminDataFactory());
// 		AdminUser uEntity = super.getCurrUser(session, request, response);
// 		if (uEntity != null) {
// 			if (super.hasAccess(uEntity, actionKey)) {
// 				String username = request.getParameter("username");
// 				String month = request.getParameter("month");
// 				int start = HttpUtil.getIntParameter(request, "start");
// 				int limit = HttpUtil.getIntParameter(request, "limit");
// 				PageList pList = vUpgradeListService.search(username, month, start, limit);
// 				if(pList != null) {
// 					json.accumulate("totalCount", pList.getCount());
// 					json.accumulate("data", pList.getList());
// 				} else {
// 					json.accumulate("totalCount", 0);
// 					json.accumulate("data", "[]");
// 				}
// 				json.set(0, "0-3");
// 			} else {
// 				json.set(2, "2-4");
// 			}
// 		} else {
// 			json.set(2, "2-6");
// 		}
// 		long t2 = System.currentTimeMillis();
// 		if (uEntity != null) {
// 			adminUserActionLogJob.add(request, actionKey, uEntity, json, t2 - t1);
// 		}
// 		HttpUtil.write(response, json.toString(), HttpUtil.json);
// 	}
//
// 	@RequestMapping(value = WUC.VIP_UPGRADE_LIST_CALCULATE, method = { RequestMethod.POST })
// 	@ResponseBody
// 	public void VIP_UPGRADE_LIST_CALCULATE(HttpSession session, HttpServletRequest request, HttpServletResponse response) {
// 		String actionKey = WUC.VIP_UPGRADE_LIST_CALCULATE;
// 		long t1 = System.currentTimeMillis();
// 		WebJSONObject json = new WebJSONObject(super.getAdminDataFactory());
// 		AdminUser uEntity = super.getCurrUser(session, request, response);
// 		if (uEntity != null) {
// 			if (super.hasAccess(uEntity, actionKey)) {
// 				vUpgradeJob.start();
// 				json.set(0, "0-3");
// 			} else {
// 				json.set(2, "2-4");
// 			}
// 		} else {
// 			json.set(2, "2-6");
// 		}
// 		long t2 = System.currentTimeMillis();
// 		if (uEntity != null) {
// 			adminUserActionLogJob.add(request, actionKey, uEntity, json, t2 - t1);
// 		}
// 		HttpUtil.write(response, json.toString(), HttpUtil.json);
// 	}
//
// 	@Autowired
// 	private UserService mUserService;
//
// 	@RequestMapping(value = WUC.VIP_UPGRADE_TEYAO, method = { RequestMethod.POST })
// 	@ResponseBody
// 	public void VIP_UPGRADE_TEYAO(HttpSession session, HttpServletRequest request, HttpServletResponse response) {
// 		String actionKey = WUC.VIP_UPGRADE_LIST_CALCULATE;
// 		long t1 = System.currentTimeMillis();
// 		WebJSONObject json = new WebJSONObject(super.getAdminDataFactory());
// 		AdminUser uEntity = super.getCurrUser(session, request, response);
// 		if (uEntity != null) {
// 			if (super.hasAccess(uEntity, actionKey)) {
// 				// TODO
// 				String username = request.getParameter("username");
// 				Integer vipLevel = HttpUtil.getIntParameter(request, "vipLevel");
// 				boolean modifyUserVipLevel = mUserService.modifyUserVipLevel(username, vipLevel);
// 				if(modifyUserVipLevel){
// 					json.set(0, "0-3");
// 				}else {
// 					json.set(0, "1-5");
// 				}
// 			} else {
// 				json.set(2, "2-4");
// 			}
// 		} else {
// 			json.set(2, "2-6");
// 		}
// 		long t2 = System.currentTimeMillis();
// 		if (uEntity != null) {
// 			adminUserActionLogJob.add(request, actionKey, uEntity, json, t2 - t1);
// 		}
// 		HttpUtil.write(response, json.toString(), HttpUtil.json);
// 	}
//
// }