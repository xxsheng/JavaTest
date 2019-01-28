package lottery.domains.content.biz.impl;

import javautils.date.DateRangeUtil;
import lottery.domains.content.biz.UserStatService;
import lottery.domains.content.dao.read.UserGameAccountReadDao;
import lottery.domains.content.dao.read.UserReadDao;
import lottery.domains.content.global.Global;
import lottery.domains.content.vo.bill.UserGameReportVO;
import lottery.domains.content.vo.bill.UserLotteryReportVO;
import lottery.domains.content.vo.chart.ChartLineVO;
import lottery.web.helper.session.SessionUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class UserStatServiceImpl implements UserStatService {

	/**
	 * DAO
	 */
	@Autowired
	private UserReadDao uReadDao;

	@Autowired
	private UserGameAccountReadDao uGameAccountReadDao;
	
	@Override
	@Transactional(readOnly = true)
	public ChartLineVO getUserRegistChart(SessionUser user, String sDate, String eDate) {
		String[] xAxis = DateRangeUtil.listDate(sDate, eDate);
		ChartLineVO lineVO = new ChartLineVO();
		lineVO.setxAxis(xAxis);
		// 获取数据
		List<?> list;
		if (user.getId() == Global.USER_TOP_ID) {
			list = uReadDao.getDayRegistAll(sDate, eDate);
		}
		else {
			list = uReadDao.getDayRegistByTeam(user.getId(), sDate, eDate);
		}

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
	public ChartLineVO getProfitChart(List<UserLotteryReportVO> list, String sDate, String eDate) {
		String[] xAxis = DateRangeUtil.listDate(sDate, eDate);
		ChartLineVO lineVO = new ChartLineVO();
		lineVO.setxAxis(xAxis);
		Map<String, Number> tmpMap = new HashMap<>();
		if(list != null) {
			for (UserLotteryReportVO tmpBean : list) {
				double profit = tmpBean.getPrize() + tmpBean.getSpendReturn() + tmpBean.getProxyReturn() + tmpBean.getActivity() + tmpBean.getRechargeFee() - tmpBean.getBillingOrder();
				tmpMap.put(tmpBean.getName(), profit);
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
	public ChartLineVO getActivityChart(List<UserLotteryReportVO> list, String sDate, String eDate) {
		String[] xAxis = DateRangeUtil.listDate(sDate, eDate);
		ChartLineVO lineVO = new ChartLineVO();
		lineVO.setxAxis(xAxis);
		Map<String, Number> tmpMap = new HashMap<>();
		if(list != null) {
			for (UserLotteryReportVO tmpBean : list) {
				tmpMap.put(tmpBean.getName(), tmpBean.getActivity() + tmpBean.getRechargeFee());
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
	public ChartLineVO getUserCostChart(List<UserLotteryReportVO> list, String sDate, String eDate) {
		String[] xAxis = DateRangeUtil.listDate(sDate, eDate);
		ChartLineVO lineVO = new ChartLineVO();
		lineVO.setxAxis(xAxis);
		Map<String, Number> tmpMap = new HashMap<>();
		if(list != null) {
			for (UserLotteryReportVO tmpBean : list) {
				tmpMap.put(tmpBean.getName(), tmpBean.getBillingOrder());
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
	public ChartLineVO getUserPrizeChart(List<UserLotteryReportVO> list, String sDate, String eDate) {
		String[] xAxis = DateRangeUtil.listDate(sDate, eDate);
		ChartLineVO lineVO = new ChartLineVO();
		lineVO.setxAxis(xAxis);
		Map<String, Number> tmpMap = new HashMap<>();
		if(list != null) {
			for (UserLotteryReportVO tmpBean : list) {
				tmpMap.put(tmpBean.getName(), tmpBean.getPrize());
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
	public ChartLineVO getUserReturnChart(List<UserLotteryReportVO> list, String sDate, String eDate) {
		String[] xAxis = DateRangeUtil.listDate(sDate, eDate);
		ChartLineVO lineVO = new ChartLineVO();
		lineVO.setxAxis(xAxis);
		// 获取数据
		Map<String, Number> tmpMap = new HashMap<>();
		if(list != null) {
			for (UserLotteryReportVO tmpBean : list) {
				tmpMap.put(tmpBean.getName(), tmpBean.getProxyReturn() + tmpBean.getSpendReturn());
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
	public ChartLineVO getUserTransInChart(List<UserLotteryReportVO> list, String sDate, String eDate) {
		String[] xAxis = DateRangeUtil.listDate(sDate, eDate);
		ChartLineVO lineVO = new ChartLineVO();
		lineVO.setxAxis(xAxis);
		try {
			Map<String, Number> tmpMap = new HashMap<>();
			if(list != null) {
				for (UserLotteryReportVO tmpBean : list) {
					tmpMap.put(tmpBean.getName(), tmpBean.getTransIn());
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
	public ChartLineVO getUserTransOutChart(List<UserLotteryReportVO> list, String sDate, String eDate) {
		String[] xAxis = DateRangeUtil.listDate(sDate, eDate);
		ChartLineVO lineVO = new ChartLineVO();
		lineVO.setxAxis(xAxis);
		try {
			Map<String, Number> tmpMap = new HashMap<>();
			if(list != null) {
				for (UserLotteryReportVO tmpBean : list) {
					tmpMap.put(tmpBean.getName(), tmpBean.getTransOut());
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
	@Transactional(readOnly = true)
	public ChartLineVO getGameRegistChart(SessionUser user, String sDate, String eDate) {
		String[] xAxis = DateRangeUtil.listDate(sDate, eDate);
		ChartLineVO lineVO = new ChartLineVO();
		lineVO.setxAxis(xAxis);
		// 获取数据
		List<?> list;
		if (user.getId() == Global.USER_TOP_ID) {
			list = uGameAccountReadDao.getDayRegistAll(sDate, eDate);
		}
		else {
			list = uGameAccountReadDao.getDayRegistByTeam(user.getId(), sDate, eDate);
		}

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
	public ChartLineVO getGameCostChart(List<UserGameReportVO> list, String sDate, String eDate) {
		String[] xAxis = DateRangeUtil.listDate(sDate, eDate);
		ChartLineVO lineVO = new ChartLineVO();
		lineVO.setxAxis(xAxis);
		Map<String, Number> tmpMap = new HashMap<>();
		if(list != null) {
			for (UserGameReportVO tmpBean : list) {
				tmpMap.put(tmpBean.getName(), tmpBean.getBillingOrder());
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
	public ChartLineVO getGamePrizeChart(List<UserGameReportVO> list, String sDate, String eDate) {
		String[] xAxis = DateRangeUtil.listDate(sDate, eDate);
		ChartLineVO lineVO = new ChartLineVO();
		lineVO.setxAxis(xAxis);
		Map<String, Number> tmpMap = new HashMap<>();
		if(list != null) {
			for (UserGameReportVO tmpBean : list) {
				tmpMap.put(tmpBean.getName(), tmpBean.getPrize());
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
	public ChartLineVO getGameReturnChart(List<UserGameReportVO> list, String sDate, String eDate) {
		String[] xAxis = DateRangeUtil.listDate(sDate, eDate);
		ChartLineVO lineVO = new ChartLineVO();
		lineVO.setxAxis(xAxis);
		// 获取数据
		Map<String, Number> tmpMap = new HashMap<>();
		if(list != null) {
			for (UserGameReportVO tmpBean : list) {
				tmpMap.put(tmpBean.getName(), tmpBean.getProxyReturn() + tmpBean.getWaterReturn());
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
	public ChartLineVO getGameTransInChart(List<UserGameReportVO> list, String sDate, String eDate) {
		String[] xAxis = DateRangeUtil.listDate(sDate, eDate);
		ChartLineVO lineVO = new ChartLineVO();
		lineVO.setxAxis(xAxis);
		try {
			Map<String, Number> tmpMap = new HashMap<>();
			if(list != null) {
				for (UserGameReportVO tmpBean : list) {
					tmpMap.put(tmpBean.getName(), tmpBean.getTransIn());
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
	public ChartLineVO getGameTransOutChart(List<UserGameReportVO> list, String sDate, String eDate) {
		String[] xAxis = DateRangeUtil.listDate(sDate, eDate);
		ChartLineVO lineVO = new ChartLineVO();
		lineVO.setxAxis(xAxis);
		try {
			Map<String, Number> tmpMap = new HashMap<>();
			if(list != null) {
				for (UserGameReportVO tmpBean : list) {
					tmpMap.put(tmpBean.getName(), tmpBean.getTransOut());
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
}