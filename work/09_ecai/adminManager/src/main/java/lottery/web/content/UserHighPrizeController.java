package lottery.web.content;

import admin.domains.content.entity.AdminUser;
import admin.domains.jobs.AdminUserActionLogJob;
import admin.domains.jobs.AdminUserLogJob;
import admin.web.WUC;
import admin.web.WebJSONObject;
import admin.web.helper.AbstractActionController;
import javautils.http.HttpUtil;
import javautils.jdbc.PageList;
import lottery.domains.content.biz.UserHighPrizeService;
import lottery.web.content.utils.UserCodePointUtil;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * Created by Nick on 2016/12/24.
 */
@Controller
public class UserHighPrizeController extends AbstractActionController {
    @Autowired
    private UserHighPrizeService highPrizeService;

    @Autowired
    private AdminUserActionLogJob adminUserActionLogJob;

    @Autowired
    private AdminUserLogJob adminUserLogJob;

    @Autowired
    private UserCodePointUtil uCodePointUtil;

    @RequestMapping(value = WUC.USER_HIGH_PRIZE_LIST, method = { RequestMethod.POST })
    @ResponseBody
    public void USER_HIGH_PRIZE_LIST(HttpSession session, HttpServletRequest request, HttpServletResponse response) {
        String actionKey = WUC.USER_HIGH_PRIZE_LIST;
        long t1 = System.currentTimeMillis();
        WebJSONObject json = new WebJSONObject(super.getAdminDataFactory());
        AdminUser uEntity = super.getCurrUser(session, request, response);
        if (uEntity != null) {
            if (super.hasAccess(uEntity, actionKey)) {
                String username = request.getParameter("username");
                Integer platform = HttpUtil.getIntParameter(request, "platform");
                String nameId = request.getParameter("nameId");
                String subName = request.getParameter("subName");
                String refId = request.getParameter("refId");
                Integer type = HttpUtil.getIntParameter(request, "type");
                Double minMoney = HttpUtil.getDoubleParameter(request, "minMoney");
                Double maxMoney = HttpUtil.getDoubleParameter(request, "maxMoney");
                Double minPrizeMoney = HttpUtil.getDoubleParameter(request, "minPrizeMoney");
                Double maxPrizeMoney = HttpUtil.getDoubleParameter(request, "maxPrizeMoney");
                Double minTimes = HttpUtil.getDoubleParameter(request, "minTimes");
                Double maxTimes = HttpUtil.getDoubleParameter(request, "maxTimes");
                String minTime = request.getParameter("minTime");
                String maxTime = request.getParameter("maxTime");
                if (StringUtils.isNotEmpty(minTime)) {
                    minTime += " 00:00:00";
                }
                if (StringUtils.isNotEmpty(maxTime)) {
                    maxTime += " 23:59:59";
                }
                Integer status = HttpUtil.getIntParameter(request, "status");
                String confirmUsername = request.getParameter("confirmUsername");
                int start = HttpUtil.getIntParameter(request, "start");
                int limit = HttpUtil.getIntParameter(request, "limit");
                PageList pList = highPrizeService.search(type,username, platform, nameId, subName, refId, minMoney, maxMoney, minPrizeMoney, maxPrizeMoney,
                        minTimes, maxTimes, minTime, maxTime, status, confirmUsername, start, limit);
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

    @RequestMapping(value = WUC.USER_HIGH_PRIZE_LOCK, method = { RequestMethod.POST })
    @ResponseBody
    public void USER_HIGH_PRIZE_LOCK(HttpSession session, HttpServletRequest request, HttpServletResponse response) {
        String actionKey = WUC.USER_HIGH_PRIZE_LOCK;
        long t1 = System.currentTimeMillis();
        WebJSONObject json = new WebJSONObject(super.getAdminDataFactory());
        AdminUser uEntity = super.getCurrUser(session, request, response);
        if (uEntity != null) {
            if (super.hasAccess(uEntity, actionKey)) {
                int id = HttpUtil.getIntParameter(request, "id");

                boolean result = highPrizeService.lock(id, uEntity.getUsername());
                if(result) {
                    adminUserLogJob.logLockHighPrize(uEntity, request, id);
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

    @RequestMapping(value = WUC.USER_HIGH_PRIZE_UNLOCK, method = { RequestMethod.POST })
    @ResponseBody
    public void USER_HIGH_PRIZE_UNLOCK(HttpSession session, HttpServletRequest request, HttpServletResponse response) {
        String actionKey = WUC.USER_HIGH_PRIZE_UNLOCK;
        long t1 = System.currentTimeMillis();
        WebJSONObject json = new WebJSONObject(super.getAdminDataFactory());
        AdminUser uEntity = super.getCurrUser(session, request, response);
        if (uEntity != null) {
            if (super.hasAccess(uEntity, actionKey)) {
                int id = HttpUtil.getIntParameter(request, "id");

                boolean result = highPrizeService.unlock(id, uEntity.getUsername());
                if(result) {
                    adminUserLogJob.logUnLockHighPrize(uEntity, request, id);
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

    @RequestMapping(value = WUC.USER_HIGH_PRIZE_CONFIRM, method = { RequestMethod.POST })
    @ResponseBody
    public void USER_HIGH_PRIZE_CONFIRM(HttpSession session, HttpServletRequest request, HttpServletResponse response) {
        String actionKey = WUC.USER_HIGH_PRIZE_CONFIRM;
        long t1 = System.currentTimeMillis();
        WebJSONObject json = new WebJSONObject(super.getAdminDataFactory());
        AdminUser uEntity = super.getCurrUser(session, request, response);
        if (uEntity != null) {
            if (super.hasAccess(uEntity, actionKey)) {
                int id = HttpUtil.getIntParameter(request, "id");

                boolean result = highPrizeService.confirm(id, uEntity.getUsername());
                if(result) {
                    adminUserLogJob.logConfirmHighPrize(uEntity, request, id);
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
