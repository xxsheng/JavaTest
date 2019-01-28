package lottery.domains.content.biz.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javautils.ObjectUtil;
import javautils.StringUtil;
import lottery.domains.content.biz.UserLotteryReportService;
import lottery.domains.content.biz.UserMainReportService;
import lottery.domains.content.dao.UserBetsDao;
import lottery.domains.content.dao.UserDao;
import lottery.domains.content.dao.UserLotteryReportDao;
import lottery.domains.content.entity.HistoryUserLotteryReport;
import lottery.domains.content.entity.Lottery;
import lottery.domains.content.entity.User;
import lottery.domains.content.entity.UserLotteryReport;
import lottery.domains.content.global.Global;
import lottery.domains.content.vo.bill.HistoryUserLotteryReportVO;
import lottery.domains.content.vo.bill.UserBetsReportVO;
import lottery.domains.content.vo.bill.UserLotteryReportVO;
import lottery.domains.content.vo.bill.UserMainReportVO;
import lottery.domains.content.vo.bill.UserProfitRankingVO;
import lottery.domains.content.vo.user.UserVO;
import lottery.domains.pool.LotteryDataFactory;

@Service
public class UserLotteryReportServiceImpl implements UserLotteryReportService {

	@Autowired
	private UserDao uDao;

	@Autowired
	private UserBetsDao uBetsDao;

	@Autowired
	private UserLotteryReportDao uLotteryReportDao;

	@Autowired
	private LotteryDataFactory lotteryDataFactory;
	
	@Autowired
	private UserMainReportService userMainReportService;

	@Override
	public boolean update(int userId, int type, double amount, String time) {
		UserLotteryReport entity = new UserLotteryReport();
		switch (type) {
		case Global.BILL_TYPE_TRANS_IN:
			entity.setTransIn(amount);
			break;
		case Global.BILL_TYPE_TRANS_OUT:
			entity.setTransOut(amount);
			break;
		case Global.BILL_TYPE_SPEND:
			entity.setSpend(amount);
			break;
		case Global.BILL_TYPE_PRIZE:
			entity.setPrize(amount);
			break;
		case Global.BILL_TYPE_SPEND_RETURN:
			entity.setSpendReturn(amount);
			break;
		case Global.BILL_TYPE_PROXY_RETURN:
			entity.setProxyReturn(amount);
			break;
		case Global.BILL_TYPE_CANCEL_ORDER:
			entity.setCancelOrder(amount);
			break;
		case Global.BILL_TYPE_DIVIDEND:
			entity.setDividend(amount);
			break;
		case Global.BILL_TYPE_DAILY_SETTLE:
		case Global.BILL_TYPE_ACTIVITY:
		case Global.BILL_TYPE_INTEGRAL:
			entity.setActivity(amount);
			break;
		default:
			return false;
		}
		UserLotteryReport bean = uLotteryReportDao.get(userId, time);
		if (bean != null) {
			entity.setId(bean.getId());
			return uLotteryReportDao.update(entity);
		} else {
			entity.setUserId(userId);
			entity.setTime(time);
			return uLotteryReportDao.add(entity);
		}
	}

	@Override
	public boolean updateRechargeFee(int userId, double amount, String time) {
		UserLotteryReport entity = new UserLotteryReport();
		entity.setRechargeFee(amount);
		UserLotteryReport bean = uLotteryReportDao.get(userId, time);
		if (bean != null) {
			entity.setId(bean.getId());
			return uLotteryReportDao.update(entity);
		} else {
			entity.setUserId(userId);
			entity.setTime(time);
			return uLotteryReportDao.add(entity);
		}
	}

	@Override
	public List<UserLotteryReportVO> report(String sTime, String eTime) {
		// 列出所有总账号ID
		List<Integer> managerIds = uDao.getUserDirectLowerId(0);

		// 统计每个总账号
		List<UserLotteryReportVO> userReports = new ArrayList<>(managerIds.size());
		for (Integer managerId : managerIds) {
			UserLotteryReportVO reportVO = uLotteryReportDao.sumLowersAndSelf(managerId, sTime, eTime);
			if (reportVO.getTransIn() <= 0 && reportVO.getTransOut() <= 0 && reportVO.getPrize() <= 0
					&& reportVO.getSpendReturn() <= 0 && reportVO.getProxyReturn() <= 0 && reportVO.getActivity() <= 0
					&& reportVO.getDividend() <= 0 && reportVO.getBillingOrder() <= 0
					&& reportVO.getRechargeFee() <= 0) {
				// 如果下级用户没有数据，那不计入这条数据
				continue;
			}

			UserVO user = lotteryDataFactory.getUser(managerId);
			if (user != null) {
				reportVO.setHasMore(true);
				reportVO.setName(user.getUsername());
				userReports.add(reportVO);
			}
		}

		// 计算总计
		List<UserLotteryReportVO> result = new ArrayList<>(userReports.size() + 1);
		UserLotteryReportVO tBean = new UserLotteryReportVO("总计");
		for (UserLotteryReportVO userReport : userReports) {
			tBean.addBean(userReport);
		}
		result.add(tBean);
		result.addAll(userReports);
		
		List<UserMainReportVO> mainReport = userMainReportService.report(sTime, eTime);
		Map<String,UserMainReportVO> mapReport = new HashMap<>();
		for (UserMainReportVO userMainReport : mainReport) {
			mapReport.put(userMainReport.getName(), userMainReport);
		}
		
		for (UserLotteryReportVO vo : result) {
			if(mapReport.containsKey(vo.getName())){
				UserMainReportVO mvo = mapReport.get(vo.getName());
				vo.addCash(mvo.getRecharge(), mvo.getWithdrawals());
			}
		}
		return result;
	}

	@Override
	public List<HistoryUserLotteryReportVO> historyReport(String sTime, String eTime) {
		// 列出所有总账号ID
		List<Integer> managerIds = uDao.getUserDirectLowerId(0);

		// 统计每个总账号
		List<HistoryUserLotteryReportVO> userReports = new ArrayList<>(managerIds.size());
		for (Integer managerId : managerIds) {
			HistoryUserLotteryReportVO reportVO = uLotteryReportDao.historySumLowersAndSelf(managerId, sTime, eTime);
			if (reportVO.getTransIn() <= 0 && reportVO.getTransOut() <= 0 && reportVO.getPrize() <= 0
					&& reportVO.getSpendReturn() <= 0 && reportVO.getProxyReturn() <= 0 && reportVO.getActivity() <= 0
					&& reportVO.getDividend() <= 0 && reportVO.getBillingOrder() <= 0
					&& reportVO.getRechargeFee() <= 0) {
				// 如果下级用户没有数据，那不计入这条数据
				continue;
			}

			UserVO user = lotteryDataFactory.getUser(managerId);
			if (user != null) {
				reportVO.setHasMore(true);
				reportVO.setName(user.getUsername());
				userReports.add(reportVO);
			}
		}

		// 计算总计
		List<HistoryUserLotteryReportVO> result = new ArrayList<>(userReports.size() + 1);
		HistoryUserLotteryReportVO tBean = new HistoryUserLotteryReportVO("总计");
		for (HistoryUserLotteryReportVO userReport : userReports) {
			tBean.addBean(userReport);
		}
		result.add(tBean);
		result.addAll(userReports);
		return result;
	}

	@Override
	public List<UserLotteryReportVO> report(int userId, String sTime, String eTime) {
		User targetUser = uDao.getById(userId);

		if (targetUser != null) {
			// 首先列出来用户下级以及所有下级
			Map<Integer, User> lowerUsersMap = new HashMap<>();
			List<User> lowerUserList = uDao.getUserLower(targetUser.getId());
			List<User> directUserList = uDao.getUserDirectLower(targetUser.getId());
			// 查询条件
			List<Criterion> criterions = new ArrayList<>();
			List<Order> orders = new ArrayList<>();
			List<Integer> toUids = new ArrayList<>();
			toUids.add(targetUser.getId());
			for (User tmpUser : lowerUserList) {
				toUids.add(tmpUser.getId());
				lowerUsersMap.put(tmpUser.getId(), tmpUser);
			}
			if (StringUtil.isNotNull(sTime)) {
				criterions.add(Restrictions.ge("time", sTime));
			}
			if (StringUtil.isNotNull(eTime)) {
				criterions.add(Restrictions.lt("time", eTime));
			}
			criterions.add(Restrictions.in("userId", toUids));
			List<UserLotteryReport> result = uLotteryReportDao.find(criterions, orders);
			Map<Integer, UserLotteryReportVO> resultMap = new HashMap<>();
			UserLotteryReportVO tBean = new UserLotteryReportVO("总计");
			// 汇总处理
			for (UserLotteryReport tmpBean : result) {
				if (tmpBean.getUserId() == targetUser.getId()) { // 自己
					if (!resultMap.containsKey(targetUser.getId())) {
						resultMap.put(targetUser.getId(), new UserLotteryReportVO(targetUser.getUsername()));
					}
					resultMap.get(targetUser.getId()).addBean(tmpBean);
				} else {
					User thisUser = lowerUsersMap.get(tmpBean.getUserId());
					if (thisUser.getUpid() == targetUser.getId()) { // 直属下级
						if (!resultMap.containsKey(thisUser.getId())) {
							resultMap.put(thisUser.getId(), new UserLotteryReportVO(thisUser.getUsername()));
						}
						resultMap.get(thisUser.getId()).addBean(tmpBean);
					} else { // 下级的下级
						for (User tmpUser : directUserList) { // 查出具体是哪个下级的会员
							if (thisUser.getUpids().indexOf("[" + tmpUser.getId() + "]") != -1) {
								if (!resultMap.containsKey(tmpUser.getId())) {
									resultMap.put(tmpUser.getId(), new UserLotteryReportVO(tmpUser.getUsername()));
								}
								resultMap.get(tmpUser.getId()).addBean(tmpBean);
							}
						}
					}
				}
				tBean.addBean(tmpBean);
			}

			// 看所有用户是否有更多查看
			for (Integer lowerUserId : resultMap.keySet()) {
				UserLotteryReportVO reportVO = resultMap.get(lowerUserId);

				for (UserLotteryReport report : result) {
					User lowerUser = lowerUsersMap.get(report.getUserId());
					if (lowerUser != null && lowerUser.getUpid() == lowerUserId) {
						reportVO.setHasMore(true);
						break;
					}
				}
			}

			List<UserLotteryReportVO> list = new ArrayList<>();
			list.add(tBean);
			Object[] keys = resultMap.keySet().toArray();
			Arrays.sort(keys);
			for (Object o : keys) {
				list.add(resultMap.get(o));
			}
			resetFirstPosition(targetUser, list);

			
			List<UserMainReportVO> mainReport = userMainReportService.report(userId, sTime, eTime);
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
		}
		return null;
	}

	/**
	 * 历史彩票综合记录
	 */
	@Override
	public List<HistoryUserLotteryReportVO> historyReport(int userId, String sTime, String eTime) {
		User targetUser = uDao.getById(userId);
		if (targetUser != null) {
			// 首先列出来用户下级以及所有下级
			Map<Integer, User> lowerUsersMap = new HashMap<>();
			List<User> lowerUserList = uDao.getUserLower(targetUser.getId());
			List<User> directUserList = uDao.getUserDirectLower(targetUser.getId());
			// 查询条件
			List<Criterion> criterions = new ArrayList<>();
			List<Order> orders = new ArrayList<>();
			List<Integer> toUids = new ArrayList<>();
			toUids.add(targetUser.getId());
			for (User tmpUser : lowerUserList) {
				toUids.add(tmpUser.getId());
				lowerUsersMap.put(tmpUser.getId(), tmpUser);
			}
			if (StringUtil.isNotNull(sTime)) {
				criterions.add(Restrictions.ge("time", sTime));
			}
			if (StringUtil.isNotNull(eTime)) {
				criterions.add(Restrictions.lt("time", eTime));
			}
			criterions.add(Restrictions.in("userId", toUids));
			List<HistoryUserLotteryReport> result = uLotteryReportDao.findHistory(criterions, orders);
			Map<Integer, HistoryUserLotteryReportVO> resultMap = new HashMap<>();
			HistoryUserLotteryReportVO tBean = new HistoryUserLotteryReportVO("总计");
			// 汇总处理
			for (HistoryUserLotteryReport tmpBean : result) {
				if (tmpBean.getUserId() == targetUser.getId()) { // 自己
					if (!resultMap.containsKey(targetUser.getId())) {
						resultMap.put(targetUser.getId(), new HistoryUserLotteryReportVO(targetUser.getUsername()));
					}
					resultMap.get(targetUser.getId()).addBean(tmpBean);
				} else {
					User thisUser = lowerUsersMap.get(tmpBean.getUserId());
					if (thisUser.getUpid() == targetUser.getId()) { // 直属下级
						if (!resultMap.containsKey(thisUser.getId())) {
							resultMap.put(thisUser.getId(), new HistoryUserLotteryReportVO(thisUser.getUsername()));
						}
						resultMap.get(thisUser.getId()).addBean(tmpBean);
					} else { // 下级的下级
						for (User tmpUser : directUserList) { // 查出具体是哪个下级的会员
							if (thisUser.getUpids().indexOf("[" + tmpUser.getId() + "]") != -1) {
								if (!resultMap.containsKey(tmpUser.getId())) {
									resultMap.put(tmpUser.getId(),
											new HistoryUserLotteryReportVO(tmpUser.getUsername()));
								}
								resultMap.get(tmpUser.getId()).addBean(tmpBean);
							}
						}
					}
				}
				tBean.addBean(tmpBean);
			}

			// 看所有用户是否有更多查看
			for (Integer lowerUserId : resultMap.keySet()) {
				HistoryUserLotteryReportVO reportVO = resultMap.get(lowerUserId);

				for (HistoryUserLotteryReport report : result) {
					User lowerUser = lowerUsersMap.get(report.getUserId());
					if (lowerUser != null && lowerUser.getUpid() == lowerUserId) {
						reportVO.setHasMore(true);
						break;
					}
				}
			}

			List<HistoryUserLotteryReportVO> list = new ArrayList<>();
			list.add(tBean);
			Object[] keys = resultMap.keySet().toArray();
			Arrays.sort(keys);
			for (Object o : keys) {
				list.add(resultMap.get(o));
			}
			historyResetFirstPosition(targetUser, list);

			return list;
		}
		return null;
	}

	/**
	 * 始终保证 如果有上级记录，上级是排在第一位的
	 * 
	 * @param targetUser
	 * @param list
	 */
	private void resetFirstPosition(User targetUser, List<UserLotteryReportVO> list) {
		if (CollectionUtils.isEmpty(list) || list.size() <= 1) {
			return;
		}

		UserLotteryReportVO targetUserReport = list.get(1);// 当前用户
		if (targetUserReport.getName() != targetUser.getUsername()) {
			UserLotteryReportVO targetUserReportReset = null;
			for (int i = 0; i < list.size(); i++) {
				if (list.get(i).getName().equals(targetUser.getUsername())) {
					targetUserReportReset = list.get(i);
					list.set(i, targetUserReport);
					break;
				}
			}
			if (targetUserReportReset != null)
				list.set(1, targetUserReportReset);
		}
	}

	/**
	 * 始终保证 如果有上级记录，上级是排在第一位的
	 * 
	 * @param targetUser
	 * @param list
	 */
	private void historyResetFirstPosition(User targetUser, List<HistoryUserLotteryReportVO> list) {
		if (CollectionUtils.isEmpty(list) || list.size() <= 1) {
			return;
		}

		HistoryUserLotteryReportVO targetUserReport = list.get(1);// 当前用户
		if (targetUserReport.getName() != targetUser.getUsername()) {
			HistoryUserLotteryReportVO targetUserReportReset = null;
			for (int i = 0; i < list.size(); i++) {
				if (list.get(i).getName().equals(targetUser.getUsername())) {
					targetUserReportReset = list.get(i);
					list.set(i, targetUserReport);
					break;
				}
			}
			if (targetUserReportReset != null)
				list.set(1, targetUserReportReset);
		}
	}

	@Override
	public List<UserBetsReportVO> bReport(Integer type, Integer lottery, Integer ruleId, String sTime, String eTime) {
		List<Integer> lids = new ArrayList<>();
		if (lottery != null) {
			lids.add(lottery);
		} else {
			if (type != null) {
				List<Lottery> llist = lotteryDataFactory.listLottery(type);
				for (Lottery tmpBean : llist) {
					lids.add(tmpBean.getId());
				}
			}
		}
		List<UserBetsReportVO> list = new ArrayList<>();
		List<?> rlist = uBetsDao.report(lids, ruleId, sTime, eTime);
		for (Object o : rlist) {
			Object[] values = (Object[]) o;
			String field = values[0] != null ? (String) values[0] : null;
			double money = ObjectUtil.toDouble(values[1]);
			double returnMoney = ObjectUtil.toDouble(values[2]);
			double prizeMoney = ObjectUtil.toDouble(values[3]);
			UserBetsReportVO tmpBean = new UserBetsReportVO();
			tmpBean.setField(field);
			tmpBean.setMoney(money);
			tmpBean.setReturnMoney(returnMoney);
			tmpBean.setPrizeMoney(prizeMoney);
			list.add(tmpBean);
		}
		return list;
	}

	@Override
	public List<UserProfitRankingVO> listUserProfitRanking(Integer userId, String sTime, String eTime, int start,
			int limit) {
		List<UserProfitRankingVO> rankingVOs;
		if (userId != null) {
			rankingVOs = uLotteryReportDao.listUserProfitRankingByDate(userId, sTime, eTime, start, limit);
		} else {
			rankingVOs = uLotteryReportDao.listUserProfitRanking(sTime, eTime, start, limit);
		}
		if (rankingVOs != null && rankingVOs.size() > 0) {
			for (UserProfitRankingVO rankingVO : rankingVOs) {
				UserVO user = lotteryDataFactory.getUser(rankingVO.getUserId());
				if (user != null) {
					rankingVO.setName(user.getUsername());
				} else {
					rankingVO.setName("未知");
				}
			}
		}

		return rankingVOs;
	}

	@Override
	public List<UserLotteryReportVO> reportByType(Integer type, String sTime, String eTime) {
		List<UserLotteryReportVO> result  = new ArrayList<UserLotteryReportVO>();
		List<User> users = uDao.listAllByType(4);
		for(User user :users){
			result.addAll(this.report(user.getId(), sTime, eTime));
		}
		return result;
	}
}