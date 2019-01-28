package lottery.web.content;

import admin.domains.content.entity.AdminUser;
import admin.domains.jobs.AdminUserActionLogJob;
import admin.domains.jobs.AdminUserLogJob;
import admin.web.WUC;
import admin.web.WebJSONObject;
import admin.web.helper.AbstractActionController;
import javautils.http.HttpUtil;
import javautils.jdbc.PageList;
import lottery.domains.content.biz.ActivityFirstRechargeBillService;
import lottery.domains.content.biz.ActivityFirstRechargeConfigService;
import lottery.domains.content.entity.ActivityFirstRechargeConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * Created by Nick on 2017/3/17.
 */
@Controller
public class ActivityFirstRechargeController extends AbstractActionController {
    @Autowired
    private ActivityFirstRechargeConfigService configService;
    @Autowired
    private ActivityFirstRechargeBillService billService;

    @Autowired
    private AdminUserActionLogJob adminUserActionLogJob;

    @Autowired
    private AdminUserLogJob adminUserLogJob;

    @RequestMapping(value = WUC.ACTIVITY_FIRST_RECHARGE_LIST, method = { RequestMethod.POST })
    @ResponseBody
    public void ACTIVITY_FIRST_RECHARGE_LIST(HttpSession session, HttpServletRequest request, HttpServletResponse response) {
        String actionKey = WUC.ACTIVITY_FIRST_RECHARGE_LIST;
        long t1 = System.currentTimeMillis();
        WebJSONObject json = new WebJSONObject(super.getAdminDataFactory());
        AdminUser uEntity = super.getCurrUser(session, request, response);
        if (uEntity != null) {
            if (super.hasAccess(uEntity, actionKey)) {
                ActivityFirstRechargeConfig config = configService.getConfig();
                if(config != null) {
                    json.accumulate("data", config);
                } else {
                    json.accumulate("data", "{}");
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

    @RequestMapping(value = WUC.ACTIVITY_FIRST_RECHARGE_EDIT, method = { RequestMethod.POST })
    @ResponseBody
    public void ACTIVITY_FIRST_RECHARGE_EDIT(HttpSession session, HttpServletRequest request, HttpServletResponse response) {
        String actionKey = WUC.ACTIVITY_FIRST_RECHARGE_EDIT;
        long t1 = System.currentTimeMillis();
        WebJSONObject json = new WebJSONObject(super.getAdminDataFactory());
        AdminUser uEntity = super.getCurrUser(session, request, response);
        if (uEntity != null) {
            if (super.hasAccess(uEntity, actionKey)) {
                int id = HttpUtil.getIntParameter(request, "id");
                String rules = request.getParameter("rules");

                boolean result = configService.updateConfig(id, rules);
                if(result) {
                    adminUserLogJob.logEditFirstRechargeConfig(uEntity, request, rules);
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

    @RequestMapping(value = WUC.ACTIVITY_FIRST_RECHARGE_UPDATE_STATUS, method = { RequestMethod.POST })
    @ResponseBody
    public void ACTIVITY_FIRST_RECHARGE_UPDATE_STATUS(HttpSession session, HttpServletRequest request, HttpServletResponse response) {
        String actionKey = WUC.ACTIVITY_FIRST_RECHARGE_UPDATE_STATUS;
        long t1 = System.currentTimeMillis();
        WebJSONObject json = new WebJSONObject(super.getAdminDataFactory());
        AdminUser uEntity = super.getCurrUser(session, request, response);
        if (uEntity != null) {
            if (super.hasAccess(uEntity, actionKey)) {
                int id = HttpUtil.getIntParameter(request, "id");
                int status = HttpUtil.getIntParameter(request, "status");

                boolean result = configService.updateStatus(id, status);
                if(result) {
                    adminUserLogJob.logUpdateStatusFirstRecharge(uEntity, request, status);
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

    @RequestMapping(value = WUC.ACTIVITY_FIRST_RECHARGE_BILL, method = { RequestMethod.POST })
    @ResponseBody
    public void ACTIVITY_FIRST_RECHARGE_BILL(HttpSession session, HttpServletRequest request, HttpServletResponse response) {
        String actionKey = WUC.ACTIVITY_FIRST_RECHARGE_BILL;
        long t1 = System.currentTimeMillis();
        WebJSONObject json = new WebJSONObject(super.getAdminDataFactory());
        AdminUser uEntity = super.getCurrUser(session, request, response);
        if (uEntity != null) {
            if (super.hasAccess(uEntity, actionKey)) {
                String username = HttpUtil.getStringParameterTrim(request, "username");
                String sDate = HttpUtil.getStringParameterTrim(request, "sDate");
                String eDate = HttpUtil.getStringParameterTrim(request, "eDate");
                String ip = HttpUtil.getStringParameterTrim(request, "ip");
                int start = HttpUtil.getIntParameter(request, "start");
                int limit = HttpUtil.getIntParameter(request, "limit");
                PageList pList = billService.find(username, sDate, eDate, ip, start, limit);
                if(pList != null) {
                    double totalAmount = billService.sumAmount(username, sDate, eDate, ip);
                    json.accumulate("totalAmount", totalAmount);
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
