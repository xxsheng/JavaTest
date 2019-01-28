package lottery.domains.content.biz.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javautils.StringUtil;

import lottery.domains.content.vo.bill.UserLotteryReportVO;
import lottery.domains.content.vo.user.UserVO;
import lottery.domains.pool.LotteryDataFactory;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import lottery.domains.content.biz.UserMainReportService;
import lottery.domains.content.dao.UserDao;
import lottery.domains.content.dao.UserMainReportDao;
import lottery.domains.content.entity.User;
import lottery.domains.content.entity.UserMainReport;
import lottery.domains.content.global.Global;
import lottery.domains.content.vo.bill.UserMainReportVO;

@Service
public class UserMainReportServiceImpl implements UserMainReportService {
	
	@Autowired
	private UserDao uDao;

	@Autowired
	private UserMainReportDao uMainReportDao;

	@Autowired
	private LotteryDataFactory lotteryDataFactory;

	@Override
	public boolean update(int userId, int type, double amount, String time) {
		UserMainReport entity = new UserMainReport();
		switch (type) {
		case Global.BILL_TYPE_RECHARGE:
			entity.setRecharge(amount);
			break;
		case Global.BILL_TYPE_WITHDRAWALS:
			entity.setWithdrawals(amount);
			break;
		case Global.BILL_TYPE_TRANS_IN:
			entity.setTransIn(amount);
			break;
		case Global.BILL_TYPE_TRANS_OUT:
			entity.setTransOut(amount);
			break;
		case Global.BILL_TYPE_ACTIVITY:
			entity.setActivity(amount);
			break;
		default:
			return false;
		}
		UserMainReport bean = uMainReportDao.get(userId, time);
		if(bean != null) {
			entity.setId(bean.getId());
			return uMainReportDao.update(entity);
		} else {
			entity.setUserId(userId);
			entity.setTime(time);
			return uMainReportDao.add(entity);
		}
	}
	
	@Override
	public List<UserMainReportVO> report(String sTime, String eTime) {
		// 列出所有总账号ID
		List<Integer> managerIds = uDao.getUserDirectLowerId(0);

		// 统计每个总账号
		List<UserMainReportVO> userReports = new ArrayList<>(managerIds.size());
		for (Integer managerId : managerIds) {
			UserMainReportVO reportVO = uMainReportDao.sumLowersAndSelf(managerId, sTime, eTime);
			if (reportVO.getRecharge() <= 0 && reportVO.getWithdrawals() <= 0 && reportVO.getTransIn() <= 0
					&& reportVO.getTransOut() <= 0 && reportVO.getAccountIn() <= 0
					&& reportVO.getAccountOut() <= 0 && reportVO.getActivity() <= 0) {
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
		List<UserMainReportVO> result = new ArrayList<>(userReports.size() + 1);
		UserMainReportVO tBean = new UserMainReportVO("总计");
		for (UserMainReportVO userReport : userReports) {
			tBean.addBean(userReport);
		}
		result.add(tBean);
		result.addAll(userReports);
		return result;

		// // 首先列出来用户下级以及所有下级
		// Map<Integer, User> lowerUsersMap = new HashMap<>();
		// List<User> lowerUserList = uDao.listAll();
		// List<User> directUserList  = uDao.getUserDirectLower(0);
		// // 查询条件
		// List<Criterion> criterions = new ArrayList<>();
		// List<Order> orders = new ArrayList<>();
		// for (User tmpUser : lowerUserList) {
		// 	lowerUsersMap.put(tmpUser.getId(), tmpUser);
		// }
		// if(StringUtil.isNotNull(sTime)) {
		// 	criterions.add(Restrictions.ge("time", sTime));
		// }
		// if(StringUtil.isNotNull(eTime)) {
		// 	criterions.add(Restrictions.lt("time", eTime));
		// }
		// List<UserMainReport> result = uMainReportDao.find(criterions, orders);
		// Map<Integer, UserMainReportVO> resultMap = new HashMap<>();
		// UserMainReportVO tBean = new UserMainReportVO("总计");
		// // 汇总处理
		// for (UserMainReport tmpBean : result) {
		// 	User thisUser = lowerUsersMap.get(tmpBean.getUserId());
		// 	if(thisUser.getUpid() == 0) { // 直属下级
		// 		if(!resultMap.containsKey(thisUser.getId())) {
		// 			resultMap.put(thisUser.getId(), new UserMainReportVO(thisUser.getUsername()));
		// 		}
		// 		resultMap.get(thisUser.getId()).addBean(tmpBean);
		// 	} else { // 下级的下级
		// 		for (User tmpUser : directUserList) { // 查出具体是哪个下级的会员
		// 			if(thisUser.getUpids().indexOf("[" + tmpUser.getId() + "]") != -1) {
		// 				if(!resultMap.containsKey(tmpUser.getId())) {
		// 					resultMap.put(tmpUser.getId(), new UserMainReportVO(tmpUser.getUsername()));
		// 				}
		// 				resultMap.get(tmpUser.getId()).addBean(tmpBean);
		// 			}
		// 		}
		// 	}
		// 	tBean.addBean(tmpBean);
		// }
		// List<UserMainReportVO> list = new ArrayList<>();
		// list.add(tBean);
		// Object[] keys = resultMap.keySet().toArray();
		// Arrays.sort(keys);
		// for (Object o : keys) {
		// 	list.add(resultMap.get(o));
		// }
		// return list;
	}
	
	@Override
	public List<UserMainReportVO> report(int userId, String sTime, String eTime) {
		User targetUser = uDao.getById(userId);
		if(targetUser != null) {
			// 首先列出来用户下级以及所有下级
			Map<Integer, User> lowerUsersMap = new HashMap<>();
			List<User> lowerUserList = uDao.getUserLower(targetUser.getId());
			List<User> directUserList  = uDao.getUserDirectLower(targetUser.getId());
			// 查询条件
			List<Criterion> criterions = new ArrayList<>();
			List<Order> orders = new ArrayList<>();
			List<Integer> toUids = new ArrayList<>();
			toUids.add(targetUser.getId());
			for (User tmpUser : lowerUserList) {
				toUids.add(tmpUser.getId());
				lowerUsersMap.put(tmpUser.getId(), tmpUser);
			}
			if(StringUtil.isNotNull(sTime)) {
				criterions.add(Restrictions.ge("time", sTime));
			}
			if(StringUtil.isNotNull(eTime)) {
				criterions.add(Restrictions.lt("time", eTime));
			}
			criterions.add(Restrictions.in("userId", toUids));
			List<UserMainReport> result = uMainReportDao.find(criterions, orders);
			Map<Integer, UserMainReportVO> resultMap = new HashMap<>();
			UserMainReportVO tBean = new UserMainReportVO("总计");
			// 汇总处理
			for (UserMainReport tmpBean : result) {
				if(tmpBean.getUserId() == targetUser.getId()) { // 自己
					if(!resultMap.containsKey(targetUser.getId())) {
						resultMap.put(targetUser.getId(), new UserMainReportVO(targetUser.getUsername()));
					}
					resultMap.get(targetUser.getId()).addBean(tmpBean);
				} else {
					User thisUser = lowerUsersMap.get(tmpBean.getUserId());
					if(thisUser.getUpid() == targetUser.getId()) { // 直属下级
						if(!resultMap.containsKey(thisUser.getId())) {
							resultMap.put(thisUser.getId(), new UserMainReportVO(thisUser.getUsername()));
						}
						resultMap.get(thisUser.getId()).addBean(tmpBean);
					} else { // 下级的下级
						for (User tmpUser : directUserList) { // 查出具体是哪个下级的会员
							if(thisUser.getUpids().indexOf("[" + tmpUser.getId() + "]") != -1) {
								if(!resultMap.containsKey(tmpUser.getId())) {
									resultMap.put(tmpUser.getId(), new UserMainReportVO(tmpUser.getUsername()));
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
				UserMainReportVO reportVO = resultMap.get(lowerUserId);

				for (UserMainReport report : result) {
					User lowerUser = lowerUsersMap.get(report.getUserId());
					if (lowerUser != null && lowerUser.getUpid() == lowerUserId) {
						reportVO.setHasMore(true);
						break;
					}
				}
			}

			List<UserMainReportVO> list = new ArrayList<>();
			list.add(tBean);
			Object[] keys = resultMap.keySet().toArray();
			Arrays.sort(keys);
			for (Object o : keys) {
				list.add(resultMap.get(o));
			}
			return list;
		}
		return null;
	}
	
}