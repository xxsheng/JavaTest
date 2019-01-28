package lottery.web.content;

import admin.domains.content.entity.AdminUser;
import admin.domains.jobs.AdminUserActionLogJob;
import admin.web.WUC;
import admin.web.WebJSONObject;
import admin.web.helper.AbstractActionController;
import javautils.http.HttpUtil;
import javautils.jdbc.PageList;
import lottery.domains.content.biz.UserBalanceSnapshotService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * Created by Nick on 2017-07-03.
 */
@Controller
public class UserBalanceSnapshotController extends AbstractActionController {
    @Autowired
    private UserBalanceSnapshotService uBalanceSnapshotService;

    @Autowired
    private AdminUserActionLogJob adminUserActionLogJob;

    @RequestMapping(value = WUC.USER_BALANCE_SNAPSHOT_LIST, method = { RequestMethod.POST })
    @ResponseBody
    public void USER_BALANCE_SNAPSHOT_LIST(HttpSession session, HttpServletRequest request, HttpServletResponse response) {
        String actionKey = WUC.USER_BALANCE_SNAPSHOT_LIST;
        long t1 = System.currentTimeMillis();
        WebJSONObject json = new WebJSONObject(super.getAdminDataFactory());
        AdminUser uEntity = super.getCurrUser(session, request, response);
        if (uEntity != null) {
            if (super.hasAccess(uEntity, actionKey)) {
                String sDate = HttpUtil.getStringParameterTrim(request, "sDate");
                String eDate = HttpUtil.getStringParameterTrim(request, "eDate");
                int start = HttpUtil.getIntParameter(request, "start");
                int limit = HttpUtil.getIntParameter(request, "limit");
                PageList pList = uBalanceSnapshotService.search(sDate, eDate, start, limit);
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
