package lottery.domains.content.biz.read.impl;

import javautils.date.DateRangeUtil;
import javautils.date.Moment;
import javautils.jdbc.PageList;
import lottery.domains.content.biz.read.UserReadService;
import lottery.domains.content.dao.read.UserReadDao;
import lottery.domains.content.entity.User;
import lottery.domains.content.global.Global;
import lottery.domains.content.vo.user.TeamStatisticsDailyVO;
import lottery.domains.content.vo.user.UserOnlineVO;
import lottery.domains.content.vo.user.UserTeamStatisticsVO;
import lottery.domains.content.vo.user.UserTeamVO;
import lottery.domains.pool.DataFactory;
import lottery.web.content.validate.UserProxyValidate;
import org.apache.commons.collections.CollectionUtils;
import org.hibernate.criterion.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@Transactional(readOnly = true)
public class UserReadServiceImpl implements UserReadService {
	@Autowired
	private UserReadDao uReadDao;
	@Autowired
	private UserProxyValidate uProxyValidate;
	@Autowired
	private DataFactory dataFactory;

	@Override
	@Transactional(readOnly = true)
	public User getByIdFromRead(int id) {
		return uReadDao.getByIdFromRead(id);
	}

	@Override
	@Transactional(readOnly = true)
	public User getByUsernameFromRead(String username) {
		return uReadDao.getByUsernameFromRead(username);
	}

	@Override
	@Transactional(readOnly = true)
	public List<User> listAllFromRead() {
		return uReadDao.listAllFromRead();
	}

	@Override
	@Transactional(readOnly = true)
	public List<User> getUserDirectLowerFromRead(int id) {
		return uReadDao.getUserDirectLowerFromRead(id);
	}

	@Override
	public List<User> getUserDirectLowerFromRead(int id, int code) {
		return uReadDao.getUserDirectLowerFromRead(id, code);
	}

	@Override
	public List<User> getUserDirectLowerFromRead(int id, int[] codes) {
		return uReadDao.getUserDirectLowerFromRead(id, codes);
	}

	@Override
	@Transactional(readOnly = true)
	public PageList searchAll(Double minMoney, Double maxMoney, int start, int limit) {
		List<Criterion> criterions = new ArrayList<>();
		List<Order> orders = new ArrayList<>();
		criterions.add(Restrictions.gt("id", 0));

		if(minMoney != null) criterions.add(Restrictions.ge("lotteryMoney", minMoney));
		if(maxMoney != null) criterions.add(Restrictions.lt("lotteryMoney", maxMoney));

		PageList pList = uReadDao.search(criterions, orders, start, limit);
		List<UserTeamVO> list = setUserTeamVO(pList);
		pList.setList(list);
		return pList;
	}

	@Override
	@Transactional(readOnly = true)
	public PageList searchByUserId(int userId) {
		List<Criterion> criterions = new ArrayList<>();
		List<Order> orders = new ArrayList<>();
		criterions.add(Restrictions.eq("id", userId));

		PageList pList = uReadDao.search(criterions, orders, 0, 1);
		List<UserTeamVO> list = setUserTeamVO(pList);
		pList.setList(list);
		return pList;
	}

	@Override
	@Transactional(readOnly = true)
	public PageList searchByUserIds(Integer[] userIds, Double minMoney, Double maxMoney, int start, int limit) {
		List<Criterion> criterions = new ArrayList<>();
		List<Order> orders = new ArrayList<>();

		criterions.add(Restrictions.in("id", Arrays.asList(userIds)));

		if(minMoney != null) criterions.add(Restrictions.ge("lotteryMoney", minMoney));
		if(maxMoney != null) criterions.add(Restrictions.lt("lotteryMoney", maxMoney));

		PageList pList = uReadDao.search(criterions, orders, start, limit);
		List<UserTeamVO> list = setUserTeamVO(pList);
		pList.setList(list);
		return pList;
	}

	@Override
	@Transactional(readOnly = true)
	public PageList searchByTeam(int userId, Double minMoney, Double maxMoney, int start, int limit) {
		List<Criterion> criterions = new ArrayList<>();
		List<Order> orders = new ArrayList<>();

		criterions.add(Restrictions.like("upids", "[" + userId + "]", MatchMode.ANYWHERE));

		if(minMoney != null) criterions.add(Restrictions.ge("lotteryMoney", minMoney));
		if(maxMoney != null) criterions.add(Restrictions.lt("lotteryMoney", maxMoney));

		PageList pList = uReadDao.search(criterions, orders, start, limit);
		List<UserTeamVO> list = setUserTeamVO(pList);
		pList.setList(list);
		return pList;
	}

	@Override
	@Transactional(readOnly = true)
	public PageList searchByTeam(int[] userIds, Double minMoney, Double maxMoney, int start, int limit) {
		List<Criterion> criterions = new ArrayList<>();
		List<Order> orders = new ArrayList<>();

		Disjunction or = Restrictions.or();
		for (int userId : userIds) {
			or.add(Restrictions.like("upids", "[" + userId + "]", MatchMode.ANYWHERE));
		}
		criterions.add(or);

		if(minMoney != null) criterions.add(Restrictions.ge("lotteryMoney", minMoney));
		if(maxMoney != null) criterions.add(Restrictions.lt("lotteryMoney", maxMoney));

		PageList pList = uReadDao.search(criterions, orders, start, limit);
		List<UserTeamVO> list = setUserTeamVO(pList);
		pList.setList(list);
		return pList;
	}

	@Override
	public PageList searchByDirectTeam(int userId, Double minMoney, Double maxMoney, int start, int limit) {
		List<Criterion> criterions = new ArrayList<>();
		List<Order> orders = new ArrayList<>();

		criterions.add(Restrictions.eq("upid", userId));

		if(minMoney != null) criterions.add(Restrictions.ge("lotteryMoney", minMoney));
		if(maxMoney != null) criterions.add(Restrictions.lt("lotteryMoney", maxMoney));

		PageList pList = uReadDao.search(criterions, orders, start, limit);
		List<UserTeamVO> list = setUserTeamVO(pList);
		pList.setList(list);
		return pList;
	}

	private List<UserTeamVO> setUserTeamVO(PageList pList) {
		List<UserTeamVO> list = new ArrayList<>();

		String sDate = new Moment().toSimpleDate();
		String eDate = new Moment().toSimpleDate();
		for (Object o : pList.getList()) {
			User user = (User) o;

			UserTeamStatisticsVO teamStatisticsVO;
			if (user.getId() == Global.USER_TOP_ID) {
				teamStatisticsVO = statisticsAll(sDate, eDate);
			}
			else {
				teamStatisticsVO = statisticsByTeam(user.getId(), sDate, eDate);
			}

			UserTeamVO userTeamVO = new UserTeamVO(user);
			userTeamVO.setTeamTotalBalance(teamStatisticsVO.getTotalBalance());
			userTeamVO.setTeamLotteryBalance(teamStatisticsVO.getLotteryBalance());
			userTeamVO.setTotalUser(teamStatisticsVO.getTotalUser());
			userTeamVO.setOnlineUser(teamStatisticsVO.getOnlineUser());

			list.add(userTeamVO);
		}

		return list;
	}

	@Override
	@Transactional(readOnly = true)
	public PageList teamOnlineList(int userId, int start, int limit) {
		List<Criterion> criterions = new ArrayList<>();
		if (userId != Global.USER_TOP_ID) {
			Disjunction or = Restrictions.or();
			or.add(Restrictions.eq("id", userId));
			or.add(Restrictions.like("upids", "[" + userId + "]", MatchMode.ANYWHERE));
			criterions.add(or);
		}

		criterions.add(Restrictions.eq("onlineStatus", 1));
		criterions.add(Restrictions.gt("id", 0));
		List<Order> orders = new ArrayList<>();
		PageList pList = uReadDao.search(criterions, orders, start, limit);
		List<UserOnlineVO> list = new ArrayList<>();
		for (Object o : pList.getList()) {
			list.add(new UserOnlineVO((User) o, userId, dataFactory));
		}

		pList.setList(list);
		return pList;
	}

	@Override
	public PageList teamOnlineList(Integer[] userIds, int start, int limit) {
		List<Criterion> criterions = new ArrayList<>();

		Disjunction or = Restrictions.or();
		or.add(Restrictions.in("id", Arrays.asList(userIds)));

		for (int userId : userIds) {
			or.add(Restrictions.like("upids", "[" + userId + "]", MatchMode.ANYWHERE));
		}
		criterions.add(or);

		criterions.add(Restrictions.eq("onlineStatus", 1));
		criterions.add(Restrictions.gt("id", 0));

		List<Order> orders = new ArrayList<>();

		PageList pList = uReadDao.search(criterions, orders, start, limit);

		List<UserOnlineVO> list = new ArrayList<>();
		for (Object o : pList.getList()) {
			User user = (User) o;

			for (Integer userId : userIds) {
				if (user.getId() == userId || user.getUpids().indexOf("["+userId+"]") > -1) {
					list.add(new UserOnlineVO(user, userId, dataFactory));
					break;
				}
			}

		}

		pList.setList(list);
		return pList;
	}

	@Override
	@Transactional(readOnly = true)
	public UserTeamStatisticsVO statisticsByTeam(int userId, String sDate, String eDate) {
		return uReadDao.statisticsByTeam(userId, sDate, eDate);
	}

	@Override
	public List<TeamStatisticsDailyVO> statisticsByTeamDaily(int userId, String sDate, String eDate) {
		List<?> dayRegistByTeam = uReadDao.getDayRegistByTeam(userId, sDate, eDate);
		if (CollectionUtils.isEmpty(dayRegistByTeam)) {
			dayRegistByTeam = new ArrayList<>();
		}

		List<TeamStatisticsDailyVO> dailyVOs = new ArrayList<>();
		for (Object result : dayRegistByTeam) {
			Object[] results = (Object[]) result;
			int index = 0;
			String date = results[index] == null ? null : results[index].toString(); index++;
			int register = results[index] == null ? 0 : Integer.valueOf(results[index].toString()); index++;

			TeamStatisticsDailyVO dailyVO = new TeamStatisticsDailyVO();
			dailyVO.setDate(date);
			dailyVO.setRegister(register);
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
	public UserTeamStatisticsVO statisticsByTeam(int[] userIds, String sDate, String eDate) {
		return uReadDao.statisticsByTeam(userIds, sDate, eDate);
	}

	@Override
	public List<TeamStatisticsDailyVO> statisticsByTeamDaily(int[] userIds, String sDate, String eDate) {
		List<?> dayRegistByTeam = uReadDao.getDayRegistByTeam(userIds, sDate, eDate);
		if (CollectionUtils.isEmpty(dayRegistByTeam)) {
			dayRegistByTeam = new ArrayList<>();
		}

		List<TeamStatisticsDailyVO> dailyVOs = new ArrayList<>();
		for (Object result : dayRegistByTeam) {
			Object[] results = (Object[]) result;
			int index = 0;
			String date = results[index] == null ? null : results[index].toString(); index++;
			int register = results[index] == null ? 0 : Integer.valueOf(results[index].toString()); index++;

			TeamStatisticsDailyVO dailyVO = new TeamStatisticsDailyVO();
			dailyVO.setDate(date);
			dailyVO.setRegister(register);
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
	public UserTeamStatisticsVO statisticsAll(String sDate, String eDate) {
		return uReadDao.statisticsAll(sDate, eDate);
	}

	@Override
	public List<TeamStatisticsDailyVO> statisticsAllDaily(String sDate, String eDate) {
		List<?> dayRegistByTeam = uReadDao.getDayRegistAll(sDate, eDate);
		if (CollectionUtils.isEmpty(dayRegistByTeam)) {
			dayRegistByTeam = new ArrayList<>();
		}

		List<TeamStatisticsDailyVO> dailyVOs = new ArrayList<>();
		for (Object result : dayRegistByTeam) {
			Object[] results = (Object[]) result;
			int index = 0;
			String date = results[index] == null ? null : results[index].toString(); index++;
			int register = results[index] == null ? 0 : Integer.valueOf(results[index].toString()); index++;

			TeamStatisticsDailyVO dailyVO = new TeamStatisticsDailyVO();
			dailyVO.setDate(date);
			dailyVO.setRegister(register);
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

	/**
	 * 查找所有主管号
	 */
	@Override
	@Transactional(readOnly = true)
	public List<User> findZhuGuans() {
		List<Criterion> criterions = new ArrayList<>();
		criterions.add(Restrictions.eq("code", 1997)); // 主管1997等级
		criterions.add(Restrictions.eq("upid", Global.USER_TOP_ID));// 上级是mick
		criterions.add(Restrictions.in("type", Arrays.asList(Integer.valueOf(Global.USER_TYPE_PROXY), Integer.valueOf(Global.USER_TYPE_PLAYER))));// 类型必须是用户或代理

		List<User> users = uReadDao.listFromRead(criterions, null);

		return users;
	}

	/**
	 * 查找所有招商号
	 */
	@Override
	@Transactional(readOnly = true)
	public List<User> findZhaoShangs(List<User> zhuGuans) {
		List<Integer> zhuGuanIds = new ArrayList<>();
		for (User zhuGuan : zhuGuans) {
			zhuGuanIds.add(zhuGuan.getId());
		}

		List<Criterion> criterions = new ArrayList<>();

		criterions.add(Restrictions.in("code", Arrays.asList(1996,1995,1994,1992))); // 招商号1996等级
		criterions.add(Restrictions.in("upid", zhuGuanIds));// 上级是主管
		criterions.add(Restrictions.in("type", Arrays.asList(Integer.valueOf(Global.USER_TYPE_PROXY), Integer.valueOf(Global.USER_TYPE_PLAYER)))); // 类型必须是用户或代理

		List<User> users = uReadDao.listFromRead(criterions, null);

		return users;
	}

	/**
	 * 查找所有直属号
	 */
	@Override
	@Transactional(readOnly = true)
	public List<User> findZhiShus(List<User> zhaoShang) {
		List<Integer> zhaoShangIds = new ArrayList<>();
		for (User zhaoShan : zhaoShang) {
			zhaoShangIds.add(zhaoShan.getId());
		}

		List<Criterion> criterions = new ArrayList<>();

		criterions.add(Restrictions.eq("code", 1994)); // 直属号1994等级
		criterions.add(Restrictions.in("upid", zhaoShangIds));// 上级是招商
		criterions.add(Restrictions.in("type", Arrays.asList(Integer.valueOf(Global.USER_TYPE_PROXY), Integer.valueOf(Global.USER_TYPE_PLAYER)))); // 类型必须是用户或代理

		List<User> users = uReadDao.listFromRead(criterions, null);

		return users;
	}
}