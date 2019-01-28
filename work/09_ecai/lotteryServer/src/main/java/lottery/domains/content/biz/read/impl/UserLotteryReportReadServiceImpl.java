package lottery.domains.content.biz.read.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javautils.StringUtil;
import javautils.array.ArrayUtils;
import javautils.date.DateRangeUtil;
import lottery.domains.content.biz.read.UserLotteryReportReadService;
import lottery.domains.content.biz.read.UserMainReportReadService;
import lottery.domains.content.dao.read.UserLotteryReportReadDao;
import lottery.domains.content.entity.User;
import lottery.domains.content.entity.UserLotteryReport;
import lottery.domains.content.global.Global;
import lottery.domains.content.vo.bill.UserLotteryReportRichVO;
import lottery.domains.content.vo.bill.UserLotteryReportVO;
import lottery.domains.content.vo.bill.UserMainReportVO;
import lottery.domains.content.vo.user.TeamStatisticsDailyVO;
import lottery.domains.content.vo.user.UserLotteryReportTeamStatisticsVO;
import lottery.domains.content.vo.user.UserVO;
import lottery.domains.pool.DataFactory;

@Service
@Transactional(readOnly = true)
public class UserLotteryReportReadServiceImpl implements UserLotteryReportReadService {
	@Autowired
	private UserLotteryReportReadDao uLotteryReportReadDao;

	@Autowired
	private UserMainReportReadService userMainReportReadService;
	
	@Autowired
	private DataFactory dataFactory;

	@Override
	@Transactional(readOnly = true)
	public List<UserLotteryReportVO> reportByUser(User user, String sTime, String eTime) {
		List<UserLotteryReportRichVO> results;
		if (user.getType() == Global.USER_TYPE_RELATED) {
			int[] relatedUserIds = ArrayUtils.transGetIds(user.getRelatedUsers());
			results = uLotteryReportReadDao.searchByTeam(relatedUserIds, sTime, eTime);
		}
		else if (user.getId() == Global.USER_TOP_ID) {
			results = uLotteryReportReadDao.searchAll(sTime, eTime);
		}
		else {
			results = uLotteryReportReadDao.searchByTeam(user.getId(), sTime, eTime);
		}
		Map<Integer, UserLotteryReportVO> resultMap = new HashMap<>();
		UserLotteryReportVO tBean = new UserLotteryReportVO("总计");

		for (UserLotteryReportRichVO tmpBean : results) {
			if(tmpBean.getUserId() == user.getId()) { // 自己
				if(!resultMap.containsKey(user.getId())) {
					resultMap.put(user.getId(), new UserLotteryReportVO(user.getUsername()));
				}
				resultMap.get(user.getId()).addBean(tmpBean);
			} else {
				// 直属下级或关联直属下级
				if(tmpBean.getUpid() == user.getId() ||
						(user.getType() == Global.USER_TYPE_RELATED &&
								user.getRelatedUsers().indexOf("[" + tmpBean.getUserId()+ "]") > -1)) {
					if(!resultMap.containsKey(tmpBean.getUserId())) {
						resultMap.put(tmpBean.getUserId(), new UserLotteryReportVO(tmpBean.getUsername()).addBean(tmpBean));
					}
					else {
						resultMap.get(tmpBean.getUserId()).addBean(tmpBean);
					}
				}
				else {
					// 下级的下级。或关联账号的下级

					String upids;
					if (user.getType() == Global.USER_TYPE_RELATED) {
						// 关联账号的下级,关联账号只能关联到主管账号，这里拿出主管ID就可以了
						upids = StringUtils.substringBeforeLast(tmpBean.getUpids(), "["+Global.USER_TOP_ID+"]");
					}
					else {
						// 直属下级的下级
						upids = StringUtils.substringBeforeLast(tmpBean.getUpids(), "["+user.getId()+"]");
					}

					if (StringUtils.isNotEmpty(upids)) {
						if (upids.endsWith(",")) {
							upids = upids.substring(0, upids.length() - 1);
						}

						int[] upidsArr = ArrayUtils.transGetIds(upids);
						int directId = upidsArr[upidsArr.length - 1];

						if(!resultMap.containsKey(directId)) {
							UserVO directUser = dataFactory.getUser(directId);
							if (directUser != null) {
								UserLotteryReportVO reportVO = new UserLotteryReportVO(directUser.getUsername()).addBean(tmpBean);
								reportVO.setHasMore(true);
								resultMap.put(directUser.getId(), reportVO);
							}
						}
						else {
							resultMap.get(directId).addBean(tmpBean).setHasMore(true);
						}
					}
				}
			}

			// 汇总总计
			tBean.addBean(tmpBean);
		}

		List<UserLotteryReportVO> list = new ArrayList<>();
		list.add(tBean);
		Object[] keys = resultMap.keySet().toArray();
		Arrays.sort(keys);
		for (Object o : keys) {
			UserLotteryReportVO vo = resultMap.get(o);

			if (vo.getTransIn() > 0
					|| vo.getTransOut() > 0
					|| vo.getPrize() > 0
					|| vo.getSpendReturn() > 0
					|| vo.getProxyReturn() > 0
					|| vo.getActivity() > 0
					|| vo.getBillingOrder() > 0
					|| vo.getRechargeFee() > 0) {
				list.add(vo);
			}

		}
		
		List<UserMainReportVO> mainReport = userMainReportReadService.report(user, sTime, eTime);
		Map<String,UserMainReportVO> mapReport = new HashMap<>();
		for (UserMainReportVO userMainReport : mainReport) {
			mapReport.put(userMainReport.getName(), userMainReport);
		}
		
		for (UserLotteryReportVO vo : list) {
			if(mapReport.containsKey(vo.getName())){
				UserMainReportVO mvo = mapReport.get(vo.getName());
				vo.addCash(mvo.getRecharge(), mvo.getWithdrawals());
			}
		}
		return list;

		// List<UserLotteryReportRichVO> results;
		// if (user.getId() == Global.USER_TOP_ID) {
		// 	results = uLotteryReportReadDao.searchAll(sTime, eTime);
		// }
		// else {
		// 	results = uLotteryReportReadDao.searchByTeam(user.getId(), sTime, eTime);
		// }
		// Map<Integer, UserLotteryReportVO> resultMap = new HashMap<>();
		// UserLotteryReportVO tBean = new UserLotteryReportVO("总计");
        //
		// for (UserLotteryReportRichVO tmpBean : results) {
		// 	if(tmpBean.getUserId() == user.getId()) { // 自己
		// 		if(!resultMap.containsKey(user.getId())) {
		// 			resultMap.put(user.getId(), new UserLotteryReportVO(user.getUsername()));
		// 		}
		// 		resultMap.get(user.getId()).addBean(tmpBean);
		// 	} else {
		// 		// 直属下级或关联直属下级
		// 		if(tmpBean.getUpid() == user.getId()) {
		// 			if(!resultMap.containsKey(tmpBean.getUserId())) {
		// 				resultMap.put(tmpBean.getUserId(), new UserLotteryReportVO(tmpBean.getUsername()).addBean(tmpBean));
		// 			}
		// 			else {
		// 				resultMap.get(tmpBean.getUserId()).addBean(tmpBean);
		// 			}
		// 		}
		// 		else {
		// 			// 下级的下级。或关联账号的下级
        //
		// 			String upids = StringUtils.substringBeforeLast(tmpBean.getUpids(), "["+user.getId()+"]");
        //
		// 			if (StringUtils.isNotEmpty(upids)) {
		// 				if (upids.endsWith(",")) {
		// 					upids = upids.substring(0, upids.length() - 1);
		// 				}
        //
		// 				int[] upidsArr = ArrayUtils.transGetIds(upids);
		// 				int directId = upidsArr[upidsArr.length - 1];
        //
		// 				if(!resultMap.containsKey(directId)) {
		// 					UserVO directUser = dataFactory.getUser(directId);
		// 					if (directUser != null) {
		// 						UserLotteryReportVO reportVO = new UserLotteryReportVO(directUser.getUsername()).addBean(tmpBean);
		// 						reportVO.setHasMore(true);
		// 						resultMap.put(directUser.getId(), reportVO);
		// 					}
		// 				}
		// 				else {
		// 					resultMap.get(directId).addBean(tmpBean).setHasMore(true);
		// 				}
		// 			}
		// 		}
		// 	}
        //
		// 	// 汇总总计
		// 	tBean.addBean(tmpBean);
		// }
        //
		// List<UserLotteryReportVO> list = new ArrayList<>();
		// list.add(tBean);
		// Object[] keys = resultMap.keySet().toArray();
		// Arrays.sort(keys);
		// for (Object o : keys) {
		// 	list.add(resultMap.get(o));
		// }
		// return list;
	}

	@Override
	@Transactional(readOnly = true)
	public List<UserLotteryReportVO> reportByTime(User user, String sDate, String eDate) {
		List<UserLotteryReport> results;
		if (user.getId() == Global.USER_TOP_ID) {
			results = uLotteryReportReadDao.getDayListAll(sDate, eDate);
		}
		else {
			results = uLotteryReportReadDao.getDayListByTeam(user.getId(), sDate, eDate);
		}

		if (results == null) {
			return new ArrayList<>();
		}

		List<UserLotteryReportVO> resultList = new ArrayList<>();
		Map<String, UserLotteryReportVO> resultMap = new HashMap<>();
		for (UserLotteryReport tmpBean : results) {
			if(!resultMap.containsKey(tmpBean.getTime())) {
				resultMap.put(tmpBean.getTime(), new UserLotteryReportVO(tmpBean.getTime()));
			}
			resultMap.get(tmpBean.getTime()).addBean(tmpBean);
		}
		for (Object o : resultMap.keySet().toArray()) {
			resultList.add(resultMap.get(o));
		}
		return resultList;
	}

	@Override
	@Transactional(readOnly = true)
	public List<?> listAmountGroupByUserIds(Integer[] ids, String sTime, String eTime) {
		return uLotteryReportReadDao.listAmountGroupByUserIds(ids, sTime, eTime);
	}

	@Override
	@Transactional(readOnly = true)
	public List<UserLotteryReport> getSelfReportByTime(int userId, String sDate, String eDate) {
		List<Criterion> criterions = new ArrayList<>();
		criterions.add(Restrictions.eq("userId", userId));
		if(StringUtil.isNotNull(sDate)) {
			criterions.add(Restrictions.ge("time", sDate));
		}
		if(StringUtil.isNotNull(eDate)) {
			criterions.add(Restrictions.lt("time", eDate));
		}
		List<Order> orders = new ArrayList<>();
		orders.add(Order.desc("time"));
		orders.add(Order.desc("id"));
		List<UserLotteryReport> reports = uLotteryReportReadDao.find(criterions, orders);
		if (CollectionUtils.isEmpty(reports)) {
			reports = new ArrayList<>();
		}

		// 补充数据
		String[] dates = DateRangeUtil.listDate(sDate, eDate);
		List<UserLotteryReport> dateReports = new LinkedList<>();
		for (int i = dates.length - 1; i >= 0; i--) {
			String date = dates[i];

			boolean has = false;
			for (UserLotteryReport report : reports) {
				if (report.getTime().equals(date)) {
					dateReports.add(report);
					has = true;
					break;
				}
			}
			if (!has) {
				UserLotteryReport report = new UserLotteryReport();
				report.setTime(date);
				report.setUserId(userId);
				dateReports.add(report);
			}
		}

		return dateReports;
	}

	@Override
	@Transactional(readOnly = true)
	public UserLotteryReportVO getSelfReport(int userId, String sTime, String eTime) {
		List<Criterion> criterions = new ArrayList<>();
		criterions.add(Restrictions.eq("userId", userId));
		if(StringUtil.isNotNull(sTime)) {
			criterions.add(Restrictions.ge("time", sTime));
		}
		if(StringUtil.isNotNull(eTime)) {
			criterions.add(Restrictions.lt("time", eTime));
		}
		List<Order> orders = new ArrayList<>();

		List<UserLotteryReport> reports = uLotteryReportReadDao.find(criterions, orders);

		UserLotteryReportVO reportVO = new UserLotteryReportVO("");

		if (CollectionUtils.isEmpty(reports)) {
			return reportVO;
		}

		for (UserLotteryReport report : reports) {
			reportVO.addBean(report);
		}

		return reportVO;
	}


	@Override
	@Transactional(readOnly = true)
	public UserLotteryReportTeamStatisticsVO statisticsByTeam(int userId, String sDate, String eDate) {
		return uLotteryReportReadDao.statisticsByTeam(userId, sDate, eDate);
	}

	@Override
	public List<TeamStatisticsDailyVO> statisticsByTeamDaily(int userId, String sDate, String eDate) {
		List<UserLotteryReport> dayListByTeam = uLotteryReportReadDao.getDayListByTeam(userId, sDate, eDate);
		if (CollectionUtils.isEmpty(dayListByTeam)) {
			dayListByTeam = new ArrayList<>();
		}

		List<TeamStatisticsDailyVO> dailyVOs = new ArrayList<>();
		for (UserLotteryReport report : dayListByTeam) {

			TeamStatisticsDailyVO dailyVO = new TeamStatisticsDailyVO();
			dailyVO.setDate(report.getTime());
			dailyVO.setLotteryBillingOrder(report.getBillingOrder());
			dailyVO.setLotteryPrize(report.getPrize());
			dailyVO.setLotterySpendReturn(report.getSpendReturn());
			dailyVO.setLotteryProxyReturn(report.getProxyReturn());
			dailyVOs.add(dailyVO);
		}

		Map<String, TeamStatisticsDailyVO> resultMap = new TreeMap<>();
		for (TeamStatisticsDailyVO tmpBean : dailyVOs) {
			resultMap.put(tmpBean.getDate(), tmpBean);
		}

		String[] dates = DateRangeUtil.listDate(sDate, eDate);

		for (String date : dates) {
			if (!resultMap.containsKey(date)) {
				TeamStatisticsDailyVO dailyVO = new TeamStatisticsDailyVO();
				dailyVO.setDate(date);
				resultMap.put(date, dailyVO);
			}
		}
		List<TeamStatisticsDailyVO> resultList = new LinkedList<>();
		for (Object o : resultMap.keySet().toArray()) {
			resultList.add(resultMap.get(o));
		}

		return resultList;
	}

	@Override
	@Transactional(readOnly = true)
	public UserLotteryReportTeamStatisticsVO statisticsByTeam(int[] userIds, String sDate, String eDate) {
		return uLotteryReportReadDao.statisticsByTeam(userIds, sDate, eDate);
	}

	@Override
	public List<TeamStatisticsDailyVO> statisticsByTeamDaily(int[] userIds, String sDate, String eDate) {
		List<UserLotteryReport> dayListByTeam = uLotteryReportReadDao.getDayListByTeam(userIds, sDate, eDate);
		if (CollectionUtils.isEmpty(dayListByTeam)) {
			dayListByTeam = new ArrayList<>();
		}

		List<TeamStatisticsDailyVO> dailyVOs = new ArrayList<>();
		for (UserLotteryReport report : dayListByTeam) {

			TeamStatisticsDailyVO dailyVO = new TeamStatisticsDailyVO();
			dailyVO.setDate(report.getTime());
			dailyVO.setLotteryBillingOrder(report.getBillingOrder());
			dailyVO.setLotteryPrize(report.getPrize());
			dailyVO.setLotterySpendReturn(report.getSpendReturn());
			dailyVO.setLotteryProxyReturn(report.getProxyReturn());
			dailyVOs.add(dailyVO);
		}

		Map<String, TeamStatisticsDailyVO> resultMap = new TreeMap<>();
		for (TeamStatisticsDailyVO tmpBean : dailyVOs) {
			resultMap.put(tmpBean.getDate(), tmpBean);
		}

		String[] dates = DateRangeUtil.listDate(sDate, eDate);

		for (String date : dates) {
			if (!resultMap.containsKey(date)) {
				TeamStatisticsDailyVO dailyVO = new TeamStatisticsDailyVO();
				dailyVO.setDate(date);
				resultMap.put(date, dailyVO);
			}
		}
		List<TeamStatisticsDailyVO> resultList = new LinkedList<>();
		for (Object o : resultMap.keySet().toArray()) {
			resultList.add(resultMap.get(o));
		}

		return resultList;
	}

	@Override
	@Transactional(readOnly = true)
	public UserLotteryReportTeamStatisticsVO statisticsAll(String sDate, String eDate) {
		return uLotteryReportReadDao.statisticsAll(sDate, eDate);
	}

	@Override
	public List<TeamStatisticsDailyVO> statisticsAllDaily(String sDate, String eDate) {
		List<UserLotteryReport> dayListByTeam = uLotteryReportReadDao.getDayListAll(sDate, eDate);
		if (CollectionUtils.isEmpty(dayListByTeam)) {
			dayListByTeam = new ArrayList<>();
		}

		List<TeamStatisticsDailyVO> dailyVOs = new ArrayList<>();
		for (UserLotteryReport report : dayListByTeam) {

			TeamStatisticsDailyVO dailyVO = new TeamStatisticsDailyVO();
			dailyVO.setDate(report.getTime());
			dailyVO.setLotteryBillingOrder(report.getBillingOrder());
			dailyVO.setLotteryPrize(report.getPrize());
			dailyVO.setLotterySpendReturn(report.getSpendReturn());
			dailyVO.setLotteryProxyReturn(report.getProxyReturn());
			dailyVOs.add(dailyVO);
		}

		Map<String, TeamStatisticsDailyVO> resultMap = new TreeMap<>();
		for (TeamStatisticsDailyVO tmpBean : dailyVOs) {
			resultMap.put(tmpBean.getDate(), tmpBean);
		}

		String[] dates = DateRangeUtil.listDate(sDate, eDate);

		for (String date : dates) {
			if (!resultMap.containsKey(date)) {
				TeamStatisticsDailyVO dailyVO = new TeamStatisticsDailyVO();
				dailyVO.setDate(date);
				resultMap.put(date, dailyVO);
			}
		}
		List<TeamStatisticsDailyVO> resultList = new LinkedList<>();
		for (Object o : resultMap.keySet().toArray()) {
			resultList.add(resultMap.get(o));
		}

		return resultList;
	}
}