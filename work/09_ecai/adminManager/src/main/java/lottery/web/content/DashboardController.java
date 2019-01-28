package lottery.web.content;

import javautils.date.Moment;
import javautils.http.HttpUtil;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import lottery.domains.content.biz.LotteryStatService;
import lottery.domains.content.vo.chart.ChartLineVO;
import lottery.domains.content.vo.chart.ChartPieVO;
import net.sf.json.JSONObject;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import admin.web.WUC;
import admin.web.helper.AbstractActionController;

@Controller
public class DashboardController extends AbstractActionController {

	@Autowired
	private LotteryStatService lotteryStatService;
	
	@RequestMapping(value = WUC.DASHBOARD_TOTAL_INFO, method = { RequestMethod.POST })
	@ResponseBody
	public void DASHBOARD_TOTAL_INFO(HttpSession session, HttpServletRequest request, HttpServletResponse response) {
		JSONObject json = new JSONObject();
		String sDate = request.getParameter("sDate");
		String eDate = request.getParameter("eDate");
		
		String sTime = new Moment().fromDate(sDate).toSimpleDate();
		String eTime = new Moment().fromDate(eDate).add(1, "days").toSimpleDate();
		// 统计新用户
		int totalUserRegist = lotteryStatService.getTotalUserRegist(sTime, eTime);
		// 统计投注总额
		long totalBetsMoney = lotteryStatService.getTotalBetsMoney(sTime, eTime);
		// 统计总订单树龄
		int totalOrderCount = lotteryStatService.getTotalOrderCount(sTime, eTime);
		// 统计总盈亏情况
		double totalProfitMoney = lotteryStatService.getTotalProfitMoney(sTime, eTime);
		json.accumulate("totalUserRegist", totalUserRegist);
		json.accumulate("totalBetsMoney", totalBetsMoney);
		json.accumulate("totalOrderCount", totalOrderCount);
		json.accumulate("totalProfitMoney", totalProfitMoney);
		HttpUtil.write(response, json.toString(), HttpUtil.json);
	}
	
	@RequestMapping(value = WUC.DASHBOARD_CHART_USER_REGIST, method = { RequestMethod.POST })
	@ResponseBody
	public void DASHBOARD_CHART_USER_REGIST(HttpSession session, HttpServletRequest request, HttpServletResponse response) {
		String sDate = request.getParameter("sDate");
		String eDate = request.getParameter("eDate");
		ChartLineVO lineVO = lotteryStatService.getUserRegistChart(sDate, eDate);
		JSONObject json = JSONObject.fromObject(lineVO);
		HttpUtil.write(response, json.toString(), HttpUtil.json);
	}
	
	@RequestMapping(value = WUC.DASHBOARD_CHART_USER_LOGIN, method = { RequestMethod.POST })
	@ResponseBody
	public void DASHBOARD_CHART_USER_LOGIN(HttpSession session, HttpServletRequest request, HttpServletResponse response) {
		String sDate = request.getParameter("sDate");
		String eDate = request.getParameter("eDate");
		ChartLineVO lineVO = lotteryStatService.getUserLoginChart(sDate, eDate);
		JSONObject json = JSONObject.fromObject(lineVO);
		HttpUtil.write(response, json.toString(), HttpUtil.json);
	}
	
	@RequestMapping(value = WUC.DASHBOARD_CHART_USER_BETS, method = { RequestMethod.POST })
	@ResponseBody
	public void DASHBOARD_CHART_USER_BETS(HttpSession session, HttpServletRequest request, HttpServletResponse response) {
		String sDate = request.getParameter("sDate");
		String eDate = request.getParameter("eDate");
		Integer type = HttpUtil.getIntParameter(request, "type");
		Integer id = HttpUtil.getIntParameter(request, "id");
		ChartLineVO lineVO = lotteryStatService.getUserBetsChart(type, id, sDate, eDate);
		JSONObject json = JSONObject.fromObject(lineVO);
		HttpUtil.write(response, json.toString(), HttpUtil.json);
	}
	
	@RequestMapping(value = WUC.DASHBOARD_CHART_USER_CASH, method = { RequestMethod.POST })
	@ResponseBody
	public void DASHBOARD_CHART_USER_CASH(HttpSession session, HttpServletRequest request, HttpServletResponse response) {
		String sDate = request.getParameter("sDate");
		String eDate = request.getParameter("eDate");
		ChartLineVO lineVO = lotteryStatService.getUserCashChart(sDate, eDate);
		JSONObject json = JSONObject.fromObject(lineVO);
		HttpUtil.write(response, json.toString(), HttpUtil.json);
	}
	
	@RequestMapping(value = WUC.DASHBOARD_CHART_USER_COMPLEX, method = { RequestMethod.POST })
	@ResponseBody
	public void DASHBOARD_CHART_USER_COMPLEX(HttpSession session, HttpServletRequest request, HttpServletResponse response) {
		String sDate = request.getParameter("sDate");
		String eDate = request.getParameter("eDate");
		Integer type = HttpUtil.getIntParameter(request, "type");
		Integer id = HttpUtil.getIntParameter(request, "id");
		ChartLineVO lineVO = lotteryStatService.getUserComplexChart(type, id, sDate, eDate);
		JSONObject json = JSONObject.fromObject(lineVO);
		HttpUtil.write(response, json.toString(), HttpUtil.json);
	}
	
	@RequestMapping(value = WUC.DASHBOARD_CHART_LOTTERY_HOT, method = { RequestMethod.POST })
	@ResponseBody
	public void DASHBOARD_CHART_LOTTERY_HOT(HttpSession session, HttpServletRequest request, HttpServletResponse response) {
		String sDate = request.getParameter("sDate");
		String eDate = request.getParameter("eDate");
		
		String sTime = new Moment().fromDate(sDate).toSimpleDate();
		String eTime = new Moment().fromDate(eDate).add(1, "days").toSimpleDate();
		
		Integer type = HttpUtil.getIntParameter(request, "type");
		ChartPieVO pieVO = lotteryStatService.getLotteryHotChart(type, sTime, eTime);
		JSONObject json = JSONObject.fromObject(pieVO);
		HttpUtil.write(response, json.toString(), HttpUtil.json);
	}
	
}