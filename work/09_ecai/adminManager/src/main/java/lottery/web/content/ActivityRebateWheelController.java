package lottery.web.content;

import admin.domains.content.entity.AdminUser;
import admin.domains.jobs.AdminUserActionLogJob;
import admin.web.WUC;
import admin.web.WebJSONObject;
import admin.web.helper.AbstractActionController;
import com.alibaba.fastjson.JSON;
import javautils.http.HttpUtil;
import javautils.jdbc.PageList;
import lottery.domains.content.biz.ActivityRebateService;
import lottery.domains.content.biz.ActivityRebateWheelBillService;
import lottery.domains.content.entity.ActivityRebate;
import lottery.domains.content.entity.activity.RebateRulesWheel;
import lottery.domains.content.global.Global;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * Created by Nick on 2017/11/27.
 */
@Controller
public class ActivityRebateWheelController extends AbstractActionController {
    @Autowired
    private ActivityRebateWheelBillService billService;

    @Autowired
    private ActivityRebateService activityRebateService;

    @Autowired
    private AdminUserActionLogJob adminUserActionLogJob;

    @RequestMapping(value = WUC.ACTIVITY_REBATE_WHEEL_LIST, method = { RequestMethod.POST })
    @ResponseBody
    public void ACTIVITY_REBATE_WHEEL_LIST(HttpSession session, HttpServletRequest request, HttpServletResponse response) {
        String actionKey = WUC.ACTIVITY_REBATE_WHEEL_LIST;
        long t1 = System.currentTimeMillis();
        WebJSONObject json = new WebJSONObject(super.getAdminDataFactory());
        AdminUser uEntity = super.getCurrUser(session, request, response);
        if (uEntity != null) {
            if (super.hasAccess(uEntity, actionKey)) {
                ActivityRebate bean = activityRebateService.getByType(Global.ACTIVITY_REBATE_WHEEL);
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

    @RequestMapping(value = WUC.ACTIVITY_REBATE_WHEEL_EDIT, method = { RequestMethod.POST })
    @ResponseBody
    public void ACTIVITY_REBATE_WHEEL_EDIT(HttpSession session, HttpServletRequest request, HttpServletResponse response) {
        String actionKey = WUC.ACTIVITY_REBATE_SIGN_EDIT;
        long t1 = System.currentTimeMillis();
        WebJSONObject json = new WebJSONObject(super.getAdminDataFactory());
        AdminUser uEntity = super.getCurrUser(session, request, response);
        if (uEntity != null) {
            if (super.hasAccess(uEntity, actionKey)) {
                String rule = request.getParameter("rule");
                RebateRulesWheel rulesWheel = JSON.parseObject(rule, RebateRulesWheel.class);
                if(rulesWheel != null) {
                    boolean result = activityRebateService.edit(Global.ACTIVITY_REBATE_WHEEL, rule, null, null);
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

    @RequestMapping(value = WUC.ACTIVITY_REBATE_WHEEL_UPDATE_STATUS, method = { RequestMethod.POST })
    @ResponseBody
    public void ACTIVITY_REBATE_WHEEL_UPDATE_STATUS(HttpSession session, HttpServletRequest request, HttpServletResponse response) {
        String actionKey = WUC.ACTIVITY_REBATE_SIGN_UPDATE_STATUS;
        long t1 = System.currentTimeMillis();
        WebJSONObject json = new WebJSONObject(super.getAdminDataFactory());
        AdminUser uEntity = super.getCurrUser(session, request, response);
        if (uEntity != null) {
            if (super.hasAccess(uEntity, actionKey)) {
                int status = HttpUtil.getIntParameter(request, "status");
                boolean result = activityRebateService.updateStatus(Global.ACTIVITY_REBATE_WHEEL, status);
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

    @RequestMapping(value = WUC.ACTIVITY_REBATE_WHEEL_BILL, method = { RequestMethod.POST })
    @ResponseBody
    public void ACTIVITY_REBATE_WHEEL_BILL(HttpSession session, HttpServletRequest request, HttpServletResponse response) {
        String actionKey = WUC.ACTIVITY_REBATE_WHEEL_BILL;
        long t1 = System.currentTimeMillis();
        WebJSONObject json = new WebJSONObject(super.getAdminDataFactory());
        AdminUser uEntity = super.getCurrUser(session, request, response);
        if (uEntity != null) {
            if (super.hasAccess(uEntity, actionKey)) {
                String username = HttpUtil.getStringParameterTrim(request, "username");
                String minTime = HttpUtil.getStringParameterTrim(request, "minTime");
                String maxTime = HttpUtil.getStringParameterTrim(request, "maxTime");
                String ip = HttpUtil.getStringParameterTrim(request, "ip");
                int start = HttpUtil.getIntParameter(request, "start");
                int limit = HttpUtil.getIntParameter(request, "limit");
                PageList pList = billService.find(username, minTime, maxTime, ip, start, limit);
                if(pList != null) {
                    double totalAmount = billService.sumAmount(username, minTime, maxTime, ip);
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
