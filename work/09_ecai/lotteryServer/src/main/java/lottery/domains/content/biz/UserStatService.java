package lottery.domains.content.biz;

import lottery.domains.content.vo.bill.UserGameReportVO;
import lottery.domains.content.vo.bill.UserLotteryReportVO;
import lottery.domains.content.vo.chart.ChartLineVO;
import lottery.web.helper.session.SessionUser;

import java.util.List;

public interface UserStatService {
	
	// 获取用户注册图形数据
	ChartLineVO getUserRegistChart(SessionUser user, String sDate, String eDate);

	// 获取用户盈亏图形数据
	ChartLineVO getProfitChart(List<UserLotteryReportVO> list, String sDate, String eDate);

	// 获取用户盈亏图形数据
	ChartLineVO getActivityChart(List<UserLotteryReportVO> list, String sDate, String eDate);

	// 获取用户消费图形数据
	ChartLineVO getUserCostChart(List<UserLotteryReportVO> list, String sDate, String eDate);

	// 获取用户中奖图形数据
	ChartLineVO getUserPrizeChart(List<UserLotteryReportVO> list, String sDate, String eDate);

	// 获取用户返点图形数据
	ChartLineVO getUserReturnChart(List<UserLotteryReportVO> list, String sDate, String eDate);
	
	// 获取用户转入图形数据
	ChartLineVO getUserTransInChart(List<UserLotteryReportVO> list, String sDate, String eDate);
	
	// 获取用户转出图形数据
	ChartLineVO getUserTransOutChart(List<UserLotteryReportVO> list, String sDate, String eDate);

	// 获取用户游戏注册图形数据
	ChartLineVO getGameRegistChart(SessionUser user, String sDate, String eDate);

	// 获取用户游戏消费图形数据
	ChartLineVO getGameCostChart(List<UserGameReportVO> list, String sDate, String eDate);

	// 获取用户游戏中奖图形数据
	ChartLineVO getGamePrizeChart(List<UserGameReportVO> list, String sDate, String eDate);

	// 获取用户游戏返点图形数据
	ChartLineVO getGameReturnChart(List<UserGameReportVO> list, String sDate, String eDate);

	// 获取用户游戏转入图形数据
	ChartLineVO getGameTransInChart(List<UserGameReportVO> list, String sDate, String eDate);

	// 获取用户游戏转出图形数据
	ChartLineVO getGameTransOutChart(List<UserGameReportVO> list, String sDate, String eDate);

}