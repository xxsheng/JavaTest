package lottery.domains.content.biz;

import lottery.domains.content.vo.chart.ChartLineVO;
import lottery.domains.content.vo.chart.ChartPieVO;
import lottery.domains.content.vo.chart.RechargeWithdrawTotal;

import java.util.List;

public interface LotteryStatService {
	
	// 统计新用户数
	int getTotalUserRegist(String sTime, String eTime);
	
	// 统计投注总额，这里不包含撤单
	long getTotalBetsMoney(String sTime, String eTime);
	
	// 统计总订单数量，这里不包含撤单
	int getTotalOrderCount(String sTime, String eTime);
	
	// 获取总计盈亏情况
	long getTotalProfitMoney(String sTime, String eTime);

	// 获取充提综合数据，总充值订单数/总充值金额/总充值第三方手续费/总提款订单数/总提款金额/实际收款/充提差(扣充值手续费)
	RechargeWithdrawTotal getTotalRechargeWithdrawData(String sTime, String eTime, Integer type, Integer subtype);
	// 获取充提图形数据,1:充值第三方手续费 2:充提差(扣充值手续费) 3:实际收款 4: 充值金额 5: 提款金额 6:充值订单数 7:提款订单数
	List<ChartLineVO> getRechargeWithdrawDataChart(String sDate, String eDate, Integer type, Integer subtype);

	// 获取用户注册图形数据
	ChartLineVO getUserRegistChart(String sDate, String eDate);
	
	// 获取用户登录图形数据
	ChartLineVO getUserLoginChart(String sDate, String eDate);
	
	// 获取订单数量图形数据
	ChartLineVO getUserBetsChart(Integer type, Integer id, String sDate, String eDate);
	
	// 获取用户充值取现图形数据
	ChartLineVO getUserCashChart(String sDate, String eDate);

	// 获取用户综合投注图形数据
	ChartLineVO getUserComplexChart(Integer type, Integer id, String sDate, String eDate);
	
	// 获取热门彩票图形数据
	ChartPieVO getLotteryHotChart(Integer type, String sTime, String eTime);
	
}