package lottery.domains.content.biz.impl;

import javautils.date.DateRangeUtil;
import javautils.date.Moment;
import javautils.math.MathUtil;
import lottery.domains.content.biz.LotteryStatService;
import lottery.domains.content.biz.UserLotteryReportService;
import lottery.domains.content.dao.*;
import lottery.domains.content.entity.Lottery;
import lottery.domains.content.vo.bill.UserLotteryReportVO;
import lottery.domains.content.vo.chart.ChartLineVO;
import lottery.domains.content.vo.chart.ChartPieVO;
import lottery.domains.content.vo.chart.ChartPieVO.PieValue;
import lottery.domains.content.vo.chart.RechargeWithdrawTotal;
import lottery.domains.pool.LotteryDataFactory;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class LotteryStatServiceImpl implements LotteryStatService {
	private static final Logger log = LoggerFactory.getLogger(LotteryStatServiceImpl.class);

	/**
	 * DAO
	 */
	@Autowired
	private UserDao uDao;
	
	@Autowired
	private UserBetsDao uBetsDao;
	
	@Autowired
	private UserRechargeDao uRechargeDao;
	
	@Autowired
	private UserWithdrawDao uWithdrawDao;
	
	@Autowired
	private UserLoginLogDao uLoginLogDao;
	
	/**
	 * SERVICE
	 */
	@Autowired
	private UserLotteryReportService uLotteryReportService;
	
	/**
	 * DataFactory
	 */
	@Autowired
	private LotteryDataFactory lotteryDataFactory;
	
	@Override
	public int getTotalUserRegist(String sTime, String eTime) {
		return uDao.getTotalUserRegist(sTime, eTime);
	}

	@Override
	public long getTotalBetsMoney(String sTime, String eTime) {
		return uBetsDao.getTotalBetsMoney(sTime, eTime);
	}

	@Override
	public int getTotalOrderCount(String sTime, String eTime) {
		return uBetsDao.getTotalOrderCount(sTime, eTime);
	}

	@Override
	public long getTotalProfitMoney(String sTime, String eTime) {
		UserLotteryReportVO rBean = uLotteryReportService.report(sTime, eTime).get(0);
		return (long) (rBean.getPrize() + rBean.getSpendReturn() + rBean.getProxyReturn() + rBean.getActivity() - rBean.getBillingOrder());
	}

	@Override
	public RechargeWithdrawTotal getTotalRechargeWithdrawData(String sDate, String eDate, Integer type, Integer subtype) {
		String sTime = new Moment().fromDate(sDate).toSimpleDate();
		String eTime = new Moment().fromDate(eDate).add(1, "days").toSimpleDate();

		Object[] rechargeData = uRechargeDao.getTotalRechargeData(sTime, eTime, type, subtype);
		// 总充值订单数
		int totalRechargeCount = rechargeData == null ? 0 : Integer.valueOf(rechargeData[0].toString());
		// 总充值金额
		double totalRechargeMoney = rechargeData == null ? 0.0 : (Double)(rechargeData[1]);
		// 总充值第三方手续费
		double totalReceiveFeeMoney = rechargeData == null ? 0.0 : (Double)(rechargeData[2]);

		Object[] withdrawData = uWithdrawDao.getTotalWithdrawData(sTime, eTime);
		// 总提款订单数
		int totalWithdrawCount = withdrawData == null ? 0 : Integer.valueOf(withdrawData[0].toString());
		// 总提款金额
		double totalWithdrawMoney = withdrawData == null ? 0.0 : (Double)(withdrawData[1]);

		// 总实际收款
		double totalActualReceiveMoney = MathUtil.subtract(totalRechargeMoney, totalReceiveFeeMoney);
		// 总充提差(扣充值手续费)
		double totalRechargeWithdrawDiff = MathUtil.subtract(totalWithdrawMoney, totalActualReceiveMoney);

		return new RechargeWithdrawTotal(totalRechargeCount, totalRechargeMoney, totalReceiveFeeMoney,
				totalWithdrawCount, totalWithdrawMoney, totalActualReceiveMoney, totalRechargeWithdrawDiff);
	}

	@Override
	public List<ChartLineVO> getRechargeWithdrawDataChart(String sDate, String eDate, Integer type, Integer subtype) {
		String sTime = new Moment().fromDate(sDate).toSimpleDate();
		String eTime = new Moment().fromDate(eDate).add(1, "days").toSimpleDate();

		// 获取充提图形数据,1:充值第三方手续费 2:充提差(扣充值手续费) 3:实际收款 4: 充值金额 5: 提款金额 6:充值订单数 7:提款订单数
		String[] dates = DateRangeUtil.listDate(sDate, eDate);
		List<ChartLineVO> lineVOs = new LinkedList<>();
		try {
			List<?> rechargeList = uRechargeDao.getDayRecharge2(sTime, eTime, type, subtype);
			List<?> withdrawList = uWithdrawDao.getDayWithdraw2(sTime, eTime);

			Map<String, Object[]> rechargeMap = new HashMap<>(); // 充值数据
			Map<String, Object[]> withdrawMap = new HashMap<>(); // 提款数据
			if(CollectionUtils.isNotEmpty(rechargeList)) {
				for (Object o : rechargeList) {
					Object[] arr = (Object[]) o;
					String date = (String) arr[0]; // 日期
					rechargeMap.put(date, arr); // 充值数据
				}
			}
			if(CollectionUtils.isNotEmpty(withdrawList)) {
				for (Object o : withdrawList) {
					Object[] arr = (Object[]) o;
					String date = (String) arr[0]; // 日期
					withdrawMap.put(date, arr); // 提款数据
				}
			}

			// 充值第三方手续费
			ChartLineVO receiveFeeMoneyLineVO = new ChartLineVO();
			receiveFeeMoneyLineVO.setxAxis(dates);
			Number[] receiveFeeMoneyYAxis = new Number[dates.length];
			// 充提差(扣充值手续费)
			ChartLineVO rechargeWithdrawDiffLineVO = new ChartLineVO();
			rechargeWithdrawDiffLineVO.setxAxis(dates);
			Number[] rechargeWithdrawDiffYAxis = new Number[dates.length];
			// 实际收款
			ChartLineVO actualReceiveMoneyLineVO = new ChartLineVO();
			actualReceiveMoneyLineVO.setxAxis(dates);
			Number[] actualReceiveMoneyYAxis = new Number[dates.length];
			// 充值金额
			ChartLineVO rechargeMoneyLineVO = new ChartLineVO();
			rechargeMoneyLineVO.setxAxis(dates);
			Number[] rechargeMoneyYAxis = new Number[dates.length];
			// 提款金额
			ChartLineVO withdrawMoneyLineVO = new ChartLineVO();
			withdrawMoneyLineVO.setxAxis(dates);
			Number[] withdrawMoneyYAxis = new Number[dates.length];
			// 充值订单数
			ChartLineVO rechargeCountLineVO = new ChartLineVO();
			rechargeCountLineVO.setxAxis(dates);
			Number[] rechargeCountYAxis = new Number[dates.length];
			// 提款订单数
			ChartLineVO withdrawCountLineVO = new ChartLineVO();
			withdrawCountLineVO.setxAxis(dates);
			Number[] withdrawCountYAxis = new Number[dates.length];

			for (int i = 0; i < dates.length; i++) {
				String date = dates[i];
				Number receiveFeeMoney = 0; // 充值第三方手续费
				Number rechargeWithdrawDiff = 0; // 充提差(扣充值手续费)
				Number actualReceiveMoney = 0; // 实际收款
				Number rechargeMoney = 0; // 充值金额
				Number withdrawMoney = 0; // 提款金额
				Number rechargeCount = 0; // 充值订单数
				Number withdrawCount = 0; // 提款订单数
				if(rechargeMap.containsKey(date)) {
					Object[] arr = rechargeMap.get(date);
					rechargeCount = ((Number) arr[1]).intValue(); // 充值订单数
					rechargeMoney = ((Number) arr[2]).intValue(); // 充值金额
					receiveFeeMoney = ((Number) arr[3]).intValue(); // 充值第三方手续费
					actualReceiveMoney = rechargeMoney.intValue() - receiveFeeMoney.intValue(); // 实际收款
					rechargeWithdrawDiff = -actualReceiveMoney.intValue(); // 充提差(扣充值手续费)
				}

				if(withdrawMap.containsKey(date)) {
					Object[] arr = withdrawMap.get(date);
					withdrawCount = ((Number) arr[1]).intValue(); // 提款订单数
					withdrawMoney = ((Number) arr[2]).intValue(); // 提款金额
					rechargeWithdrawDiff = withdrawMoney.intValue() + rechargeWithdrawDiff.intValue(); // 充提差(扣充值手续费)
				}

				receiveFeeMoneyYAxis[i] = receiveFeeMoney;
				rechargeWithdrawDiffYAxis[i] = rechargeWithdrawDiff;
				actualReceiveMoneyYAxis[i] = actualReceiveMoney;
				rechargeMoneyYAxis[i] = rechargeMoney;
				withdrawMoneyYAxis[i] = withdrawMoney;
				rechargeCountYAxis[i] = rechargeCount;
				withdrawCountYAxis[i] = withdrawCount;
			}
			receiveFeeMoneyLineVO.getyAxis().add(receiveFeeMoneyYAxis); // 充值第三方手续费
			rechargeWithdrawDiffLineVO.getyAxis().add(rechargeWithdrawDiffYAxis); // 充提差(扣充值手续费)
			actualReceiveMoneyLineVO.getyAxis().add(actualReceiveMoneyYAxis); // 总实际收款
			rechargeMoneyLineVO.getyAxis().add(rechargeMoneyYAxis); // 充值金额
			withdrawMoneyLineVO.getyAxis().add(withdrawMoneyYAxis); // 提款金额
			rechargeCountLineVO.getyAxis().add(rechargeCountYAxis); // 充值订单数
			withdrawCountLineVO.getyAxis().add(withdrawCountYAxis); // 提款订单数

			lineVOs.add(receiveFeeMoneyLineVO); // 充值第三方手续费
			lineVOs.add(rechargeWithdrawDiffLineVO); // 充提差(扣充值手续费)
			lineVOs.add(actualReceiveMoneyLineVO); // 总实际收款
			lineVOs.add(rechargeMoneyLineVO); // 充值金额
			lineVOs.add(withdrawMoneyLineVO); // 提款金额
			lineVOs.add(rechargeCountLineVO); // 充值订单数
			lineVOs.add(withdrawCountLineVO); // 提款订单数
		} catch (Exception e) {
			log.error("统计充提报表时出错", e);
		}
		return lineVOs;
	}

	@Override
	public ChartLineVO getUserRegistChart(String sDate, String eDate) {
		String sTime = new Moment().fromDate(sDate).toSimpleDate();
		String eTime = new Moment().fromDate(eDate).add(1, "days").toSimpleDate();
		String[] xAxis = DateRangeUtil.listDate(sDate, eDate);
		ChartLineVO lineVO = new ChartLineVO();
		lineVO.setxAxis(xAxis);
		// 获取数据
		List<?> list = uDao.getDayUserRegist(sTime, eTime);
		Map<String, Number> tmpMap = new HashMap<>();
		if(list != null) {
			for (Object o : list) {
				Object[] arr = (Object[]) o;
				String date = (String) arr[0];
				Number count = (Number) arr[1];
				tmpMap.put(date, count);
			}
		}
		Number[] yAxis = new Number[xAxis.length];
		for (int i = 0; i < xAxis.length; i++) {
			if(tmpMap.containsKey(xAxis[i])) {
				yAxis[i] = tmpMap.get(xAxis[i]);
			} else {
				yAxis[i] = 0;
			}
		}
		lineVO.getyAxis().add(yAxis);
		return lineVO;
	}
	
	@Override
	public ChartLineVO getUserLoginChart(String sDate, String eDate) {
		String sTime = new Moment().fromDate(sDate).toSimpleDate();
		String eTime = new Moment().fromDate(eDate).add(1, "days").toSimpleDate();
		String[] xAxis = DateRangeUtil.listDate(sDate, eDate);
		ChartLineVO lineVO = new ChartLineVO();
		lineVO.setxAxis(xAxis);
		// 获取数据
		List<?> list = uLoginLogDao.getDayUserLogin(sTime, eTime);
		Map<String, Number> tmpMap = new HashMap<>();
		if(list != null) {
			for (Object o : list) {
				Object[] arr = (Object[]) o;
				String date = (String) arr[0];
				Number count = (Number) arr[1];
				tmpMap.put(date, count);
			}
		}
		Number[] yAxis = new Number[xAxis.length];
		for (int i = 0; i < xAxis.length; i++) {
			if(tmpMap.containsKey(xAxis[i])) {
				yAxis[i] = tmpMap.get(xAxis[i]);
			} else {
				yAxis[i] = 0;
			}
		}
		lineVO.getyAxis().add(yAxis);
		return lineVO;
	}
	
	@Override
	public ChartLineVO getUserBetsChart(Integer type, Integer id, String sDate, String eDate) {
		String sTime = new Moment().fromDate(sDate).toSimpleDate();
		String eTime = new Moment().fromDate(eDate).add(1, "days").toSimpleDate();
		
		// 这里处理彩票类型的选择
		List<Lottery> lotteries = new ArrayList<>();
		if(id != null) { // 如果已经选中彩票
			Lottery tmpLottery = lotteryDataFactory.getLottery(id.intValue());
			if(tmpLottery != null) {
				lotteries.add(tmpLottery);
			}
		} else if(type != null) { // 如果只是选中了类型
			lotteries = lotteryDataFactory.listLottery(type.intValue());
		}
		int[] lids = new int[lotteries.size()];
		for (int i = 0; i < lotteries.size(); i++) {
			lids[i] = lotteries.get(i).getId();
		}
		
		String[] xAxis = DateRangeUtil.listDate(sDate, eDate);
		ChartLineVO lineVO = new ChartLineVO();
		lineVO.setxAxis(xAxis);
		// 获取数据
		List<?> list = uBetsDao.getDayUserBets(lids, sTime, eTime);
		Map<String, Number> tmpMap = new HashMap<>();
		if(list != null) {
			for (Object o : list) {
				Object[] arr = (Object[]) o;
				String date = (String) arr[0];
				Number count = (Number) arr[1];
				tmpMap.put(date, count);
			}
		}
		Number[] yAxis = new Number[xAxis.length];
		for (int i = 0; i < xAxis.length; i++) {
			if(tmpMap.containsKey(xAxis[i])) {
				yAxis[i] = tmpMap.get(xAxis[i]);
			} else {
				yAxis[i] = 0;
			}
		}
		lineVO.getyAxis().add(yAxis);
		return lineVO;
	}
	
	@Override
	public ChartLineVO getUserCashChart(String sDate, String eDate) {
		String sTime = new Moment().fromDate(sDate).toSimpleDate();
		String eTime = new Moment().fromDate(eDate).add(1, "days").toSimpleDate();
		String[] xAxis = DateRangeUtil.listDate(sDate, eDate);
		ChartLineVO lineVO = new ChartLineVO();
		lineVO.setxAxis(xAxis);
		// 获取充值数据
		try {
			List<?> list = uRechargeDao.getDayRecharge(sTime, eTime);
			Map<String, Number> tmpMap = new HashMap<>();

			Map<String, Number> receiveFeeMap = new HashMap<>();
			if(list != null) {
				for (Object o : list) {
					Object[] arr = (Object[]) o;
					String date = (String) arr[0];
					Number count = ((Number) arr[1]).intValue();
					Number receiveFee = ((Number) arr[2]).intValue();
					tmpMap.put(date, count);
					receiveFeeMap.put(date, receiveFee);
				}
			}
			// 充值
			Number[] yAxis = new Number[xAxis.length];
			for (int i = 0; i < xAxis.length; i++) {
				if(tmpMap.containsKey(xAxis[i])) {
					yAxis[i] = tmpMap.get(xAxis[i]);
				} else {
					yAxis[i] = 0;
				}
			}
			lineVO.getyAxis().add(yAxis);
		} catch (Exception e) {
			e.printStackTrace();
		}
		// 获取提现数据
		try {
			List<?> list = uWithdrawDao.getDayWithdraw(sTime, eTime);
			Map<String, Number> tmpMap = new HashMap<>();
			if(list != null) {
				for (Object o : list) {
					Object[] arr = (Object[]) o;
					String date = (String) arr[0];
					Number count = ((Number) arr[1]).intValue();
					tmpMap.put(date, count);
				}
			}
			Number[] yAxis = new Number[xAxis.length];
			for (int i = 0; i < xAxis.length; i++) {
				if(tmpMap.containsKey(xAxis[i])) {
					yAxis[i] = tmpMap.get(xAxis[i]);
				} else {
					yAxis[i] = 0;
				}
			}
			lineVO.getyAxis().add(yAxis);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return lineVO;
	}
	
	@Override
	public ChartLineVO getUserComplexChart(Integer type, Integer id, String sDate, String eDate) {
		String sTime = new Moment().fromDate(sDate).toSimpleDate();
		String eTime = new Moment().fromDate(eDate).add(1, "days").toSimpleDate();
		
		// 这里处理彩票类型的选择
		List<Lottery> lotteries = new ArrayList<>();
		if(id != null) { // 如果已经选中彩票
			Lottery tmpLottery = lotteryDataFactory.getLottery(id.intValue());
			if(tmpLottery != null) {
				lotteries.add(tmpLottery);
			}
		} else if(type != null) { // 如果只是选中了类型
			lotteries = lotteryDataFactory.listLottery(type.intValue());
		}
		int[] lids = new int[lotteries.size()];
		for (int i = 0; i < lotteries.size(); i++) {
			lids[i] = lotteries.get(i).getId();
		}
		
		String[] xAxis = DateRangeUtil.listDate(sDate, eDate);
		ChartLineVO lineVO = new ChartLineVO();
		lineVO.setxAxis(xAxis);
		// 获取投注金额数据
		try {
			List<?> list = uBetsDao.getDayBetsMoney(lids, sTime, eTime);
			Map<String, Number> tmpMap = new HashMap<>();
			if(list != null) {
				for (Object o : list) {
					Object[] arr = (Object[]) o;
					String date = (String) arr[0];
					Number count = ((Number) arr[1]).intValue();
					tmpMap.put(date, count);
				}
			}
			Number[] yAxis = new Number[xAxis.length];
			for (int i = 0; i < xAxis.length; i++) {
				if(tmpMap.containsKey(xAxis[i])) {
					yAxis[i] = tmpMap.get(xAxis[i]);
				} else {
					yAxis[i] = 0;
				}
			}
			lineVO.getyAxis().add(yAxis);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		// 获取中奖金额数据
		try {
			List<?> list = uBetsDao.getDayPrizeMoney(lids, sTime, eTime);
			Map<String, Number> tmpMap = new HashMap<>();
			if(list != null) {
				for (Object o : list) {
					Object[] arr = (Object[]) o;
					String date = (String) arr[0];
					Number count = arr[1] != null ? ((Number) arr[1]).intValue() : 0;
					tmpMap.put(date, count);
				}
			}
			Number[] yAxis = new Number[xAxis.length];
			for (int i = 0; i < xAxis.length; i++) {
				if(tmpMap.containsKey(xAxis[i])) {
					yAxis[i] = tmpMap.get(xAxis[i]);
				} else {
					yAxis[i] = 0;
				}
			}
			lineVO.getyAxis().add(yAxis);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return lineVO;
	}
	
	@Override
	public ChartPieVO getLotteryHotChart(Integer type, String sTime,
			String eTime) {
		ChartPieVO pieVO = new ChartPieVO();
		List<Lottery> lotteries = new ArrayList<>();
		List<?> list = new ArrayList<>();
		if(type != null) {
			lotteries = lotteryDataFactory.listLottery(type.intValue());
			int[] lids = new int[lotteries.size()];
			for (int i = 0; i < lotteries.size(); i++) {
				lids[i] = lotteries.get(i).getId();
			}
			list = uBetsDao.getLotteryHot(lids, sTime, eTime);
		} else {
			lotteries = lotteryDataFactory.listLottery();
			list = uBetsDao.getLotteryHot(null, sTime, eTime);
		}
		String[] legend = new String[lotteries.size()];
		for (int i = 0; i < lotteries.size(); i++) {
			legend[i] = lotteries.get(i).getShowName();
		}
		pieVO.setLegend(legend);
		Map<String, Number> tmpMap = new HashMap<>();
		if(list != null) {
			for (Object o : list) {
				Object[] arr = (Object[]) o;
				int lotteryId = ((Number) arr[0]).intValue();
				Number count = (Number) arr[1];
				Lottery tmpLottery = lotteryDataFactory.getLottery(lotteryId);
				if(tmpLottery != null) {
					tmpMap.put(tmpLottery.getShowName(), count);
				}
			}
		}
		PieValue[] series = new PieValue[legend.length];
		for (int i = 0; i < legend.length; i++) {
			if(tmpMap.containsKey(legend[i])) {
				series[i] = pieVO.new PieValue(legend[i], tmpMap.get(legend[i]));
			} else {
				series[i] = pieVO.new PieValue(legend[i], 0);
			}
		}
		pieVO.setSeries(series);
		return pieVO;
	}

}