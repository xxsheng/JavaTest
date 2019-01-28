package lottery.web.content;

import admin.domains.content.entity.AdminUser;
import admin.domains.jobs.AdminUserActionLogJob;
import admin.web.WUC;
import admin.web.WebJSONObject;
import admin.web.helper.AbstractActionController;
import javautils.http.HttpUtil;
import lottery.domains.content.biz.LotteryStatService;
import lottery.domains.content.vo.chart.ChartLineVO;
import lottery.domains.content.vo.chart.RechargeWithdrawTotal;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Nick on 2017-04-24.
 */
@Controller
public class RechargeWithdrawController extends AbstractActionController {
    @Autowired
    private AdminUserActionLogJob adminUserActionLogJob;

    @Autowired
    private LotteryStatService lotteryStatService;

    @RequestMapping(value = WUC.RECHARGE_WITHDRAW_COMPLEX, method = { RequestMethod.POST })
    @ResponseBody
    public void RECHARGE_WITHDRAW_COMPLEX(HttpSession session, HttpServletRequest request, HttpServletResponse response) {
        String actionKey = WUC.RECHARGE_WITHDRAW_COMPLEX;
        long t1 = System.currentTimeMillis();
        WebJSONObject json = new WebJSONObject(super.getAdminDataFactory());
        AdminUser uEntity = super.getCurrUser(session, request, response);
        if (uEntity != null) {
            if (super.hasAccess(uEntity, actionKey)) {
                String sDate = request.getParameter("sDate");
                String eDate = request.getParameter("eDate");
                Integer type = HttpUtil.getIntParameter(request, "type");
                Integer subtype = HttpUtil.getIntParameter(request, "subtype");

                if (StringUtils.isNotEmpty(sDate) && StringUtils.isNotEmpty(eDate)) {
                    RechargeWithdrawTotal totalRechargeWithdrawData = lotteryStatService.getTotalRechargeWithdrawData(sDate, eDate, type, subtype);
                    List<ChartLineVO> rechargeWithdrawDataChart = lotteryStatService.getRechargeWithdrawDataChart(sDate, eDate, type, subtype);
                    Map<String, Object> data = new HashMap<>();
                    data.put("total", totalRechargeWithdrawData);
                    data.put("charts", rechargeWithdrawDataChart);
                    json.accumulate("data", data);
                    json.set(0, "0-3");
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
