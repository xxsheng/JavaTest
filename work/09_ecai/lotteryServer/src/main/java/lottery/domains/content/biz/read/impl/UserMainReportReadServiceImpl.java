package lottery.domains.content.biz.read.impl;

import javautils.StringUtil;
import javautils.array.ArrayUtils;
import javautils.date.DateRangeUtil;
import lottery.domains.content.biz.read.UserMainReportReadService;
import lottery.domains.content.dao.read.UserMainReportReadDao;
import lottery.domains.content.entity.User;
import lottery.domains.content.entity.UserMainReport;
import lottery.domains.content.global.Global;
import lottery.domains.content.vo.bill.UserMainReportRichVO;
import lottery.domains.content.vo.bill.UserMainReportVO;
import lottery.domains.content.vo.user.TeamStatisticsDailyVO;
import lottery.domains.content.vo.user.UserMainReportTeamStatisticsVO;
import lottery.domains.content.vo.user.UserVO;
import lottery.domains.pool.DataFactory;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@Transactional(readOnly = true)
public class UserMainReportReadServiceImpl implements UserMainReportReadService {

	@Autowired
	private UserMainReportReadDao uMainReportReadDao;

	@Autowired
	private DataFactory dataFactory;

	@Override
	@Transactional(readOnly = true)
	public List<UserMainReportVO> report(User user, String sTime, String eTime) {
		List<UserMainReportRichVO> results;
		if (user.getType() == Global.USER_TYPE_RELATED) {
			int[] relatedUserIds = ArrayUtils.transGetIds(user.getRelatedUsers());
			results = uMainReportReadDao.searchByTeam(relatedUserIds, sTime, eTime);
		}
		else if (user.getId() == Global.USER_TOP_ID) {
			results = uMainReportReadDao.searchAll(sTime, eTime);
		}
		else {
			results = uMainReportReadDao.searchByTeam(user.getId(), sTime, eTime);
		}
		Map<Integer, UserMainReportVO> resultMap = new HashMap<>();
		UserMainReportVO tBean = new UserMainReportVO("总计");

		for (UserMainReportRichVO tmpBean : results) {
			if(tmpBean.getUserId() == user.getId()) { // 自己
				if(!resultMap.containsKey(user.getId())) {
					resultMap.put(user.getId(), new UserMainReportVO(user.getUsername()));
				}
				resultMap.get(user.getId()).addBean(tmpBean);
			} else {
				// 直属下级或关联直属下级
				if(tmpBean.getUpid() == user.getId() ||
						(user.getType() == Global.USER_TYPE_RELATED &&
								user.getRelatedUsers().indexOf("[" + tmpBean.getUserId()+ "]") > -1)) {
					if(!resultMap.containsKey(tmpBean.getUserId())) {
						resultMap.put(tmpBean.getUserId(), new UserMainReportVO(tmpBean.getUsername()).addBean(tmpBean));
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
								UserMainReportVO reportVO = new UserMainReportVO(directUser.getUsername()).addBean(tmpBean);
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

		List<UserMainReportVO> list = new ArrayList<>();
		list.add(tBean);
		Object[] keys = resultMap.keySet().toArray();
		Arrays.sort(keys);
		for (Object o : keys) {
			UserMainReportVO reportVO = resultMap.get(o);

			if (reportVO.getRecharge() > 0 || reportVO.getWithdrawals() > 0 || reportVO.getTransIn() > 0 || reportVO.getTransOut() > 0 || reportVO.getAccountIn() > 0
					|| reportVO.getAccountOut() > 0 || reportVO.getActivity() > 0) {
				list.add(resultMap.get(o));
			}
		}
		return list;

		// List<UserMainReportRichVO> results;
		// if (user.getId() == Global.USER_TOP_ID) {
		// 	results = uMainReportReadDao.searchAll(sTime, eTime);
		// }
		// else if (user.getType() == Global.USER_TYPE_RELATED) {
		// 	// 关联账号查询
		// 	int[] relatedUserIds = ArrayUtils.transGetIds(user.getRelatedUsers());
		// 	if (relatedUserIds != null && relatedUserIds.length > 0) {
		// 		results = uMainReportReadDao.searchByTeam(relatedUserIds, sTime, eTime);
		// 	}
		// 	else {
		// 		results = new ArrayList<>();
		// 	}
		// }
		// else {
		// 	results = uMainReportReadDao.searchByTeam(user.getId(), sTime, eTime);
		// }
		// Map<Integer, UserMainReportVO> resultMap = new HashMap<>();
		// UserMainReportVO tBean = new UserMainReportVO("总计");
        //
		// for (UserMainReportRichVO tmpBean : results) {
		// 	if(tmpBean.getUserId() == user.getId()) { // 自己
		// 		if(!resultMap.containsKey(user.getId())) {
		// 			resultMap.put(user.getId(), new UserMainReportVO(user.getUsername()));
		// 		}
		// 		resultMap.get(user.getId()).addBean(tmpBean);
		// 	} else {
		// 		// 直属下级
		// 		if(tmpBean.getUpid() == user.getId()) {
		// 			if(!resultMap.containsKey(tmpBean.getUserId())) {
		// 				resultMap.put(tmpBean.getUserId(), new UserMainReportVO(tmpBean.getUsername()).addBean(tmpBean));
		// 			}
		// 			else {
		// 				resultMap.get(tmpBean.getUserId()).addBean(tmpBean);
		// 			}
		// 		}
		// 		else {
		// 			// 下级的下级
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
		// 						UserMainReportVO reportVO = new UserMainReportVO(directUser.getUsername()).addBean(tmpBean);
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
		// List<UserMainReportVO> list = new ArrayList<>();
		// list.add(tBean);
		// Object[] keys = resultMap.keySet().toArray();
		// Arrays.sort(keys);
		// for (Object o : keys) {
		// 	UserMainReportVO reportVO = resultMap.get(o);
        //
		// 	if (reportVO.getRecharge() > 0 || reportVO.getWithdrawals() > 0 || reportVO.getTransIn() > 0 || reportVO.getTransOut() > 0 || reportVO.getAccountIn() > 0
		// 			|| reportVO.getAccountOut() > 0 || reportVO.getActivity() > 0) {
		// 		list.add(resultMap.get(o));
		// 	}
		// }
		// return list;
	}

	@Override
	@Transactional(readOnly = true)
	public List<UserMainReport> getSelfReportByTime(int userId, String sDate, String eDate) {
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
		List<UserMainReport> reports = uMainReportReadDao.find(criterions, orders);
		if (CollectionUtils.isEmpty(reports)) {
			reports = new ArrayList<>();
		}

		// 补充数据
		String[] dates = DateRangeUtil.listDate(sDate, eDate);
		List<UserMainReport> dateReports = new LinkedList<>();
		for (int i = dates.length - 1; i >= 0; i--) {
			String date = dates[i];

			boolean has = false;
			for (UserMainReport report : reports) {
				if (report.getTime().equals(date)) {
					dateReports.add(report);
					has = true;
					break;
				}
			}
			if (!has) {
				UserMainReport report = new UserMainReport();
				report.setTime(date);
				report.setUserId(userId);
				dateReports.add(report);
			}
		}

		return dateReports;
	}

	/**
	 * 获取用户自己的报表并总计
	 */
	@Override
	@Transactional(readOnly = true)
	public UserMainReportVO getSelfReport(int userId, String sTime, String eTime) {
		List<Criterion> criterions = new ArrayList<>();
		criterions.add(Restrictions.eq("userId", userId));
		if(StringUtil.isNotNull(sTime)) {
			criterions.add(Restrictions.ge("time", sTime));
		}
		if(StringUtil.isNotNull(eTime)) {
			criterions.add(Restrictions.lt("time", eTime));
		}
		List<Order> orders = new ArrayList<>();

		List<UserMainReport> reports = uMainReportReadDao.find(criterions, orders);

		UserMainReportVO reportVO = new UserMainReportVO("");

		if (CollectionUtils.isEmpty(reports)) {
			return reportVO;
		}

		for (UserMainReport report : reports) {
			reportVO.addBean(report);
		}

		return reportVO;
	}

	@Override
	@Transactional(readOnly = true)
	public UserMainReportTeamStatisticsVO statisticsByTeam(int userId, String sDate, String eDate) {
		return uMainReportReadDao.statisticsByTeam(userId, sDate, eDate);
	}

	@Override
	public List<TeamStatisticsDailyVO> statisticsByTeamDaily(int userId, String sDate, String eDate) {
		List<UserMainReport> dayListByTeam = uMainReportReadDao.getDayListByTeam(userId, sDate, eDate);
		if (CollectionUtils.isEmpty(dayListByTeam)) {
			dayListByTeam = new ArrayList<>();
		}

		List<TeamStatisticsDailyVO> dailyVOs = new ArrayList<>();
		for (UserMainReport report : dayListByTeam) {

			TeamStatisticsDailyVO dailyVO = new TeamStatisticsDailyVO();
			dailyVO.setDate(report.getTime());
			dailyVO.setRecharge(report.getRecharge());
			dailyVO.setWithdraw(report.getWithdrawals());
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
	public UserMainReportTeamStatisticsVO statisticsByTeam(int[] userIds, String sDate, String eDate) {
		return uMainReportReadDao.statisticsByTeam(userIds, sDate, eDate);
	}

	@Override
	public List<TeamStatisticsDailyVO> statisticsByTeamDaily(int[] userIds, String sDate, String eDate) {
		List<UserMainReport> dayListByTeam = uMainReportReadDao.getDayListByTeam(userIds, sDate, eDate);
		if (CollectionUtils.isEmpty(dayListByTeam)) {
			dayListByTeam = new ArrayList<>();
		}

		List<TeamStatisticsDailyVO> dailyVOs = new ArrayList<>();
		for (UserMainReport report : dayListByTeam) {

			TeamStatisticsDailyVO dailyVO = new TeamStatisticsDailyVO();
			dailyVO.setDate(report.getTime());
			dailyVO.setRecharge(report.getRecharge());
			dailyVO.setWithdraw(report.getWithdrawals());
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
	public UserMainReportTeamStatisticsVO statisticsAll(String sDate, String eDate) {
		return uMainReportReadDao.statisticsAll(sDate, eDate);
	}

	@Override
	public List<TeamStatisticsDailyVO> statisticsAllDaily(String sDate, String eDate) {
		List<UserMainReport> dayListByTeam = uMainReportReadDao.getDayListAll(sDate, eDate);
		if (CollectionUtils.isEmpty(dayListByTeam)) {
			dayListByTeam = new ArrayList<>();
		}

		List<TeamStatisticsDailyVO> dailyVOs = new ArrayList<>();
		for (UserMainReport report : dayListByTeam) {

			TeamStatisticsDailyVO dailyVO = new TeamStatisticsDailyVO();
			dailyVO.setDate(report.getTime());
			dailyVO.setRecharge(report.getRecharge());
			dailyVO.setWithdraw(report.getWithdrawals());
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