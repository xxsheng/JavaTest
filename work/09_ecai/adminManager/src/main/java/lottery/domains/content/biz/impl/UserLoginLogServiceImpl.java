package lottery.domains.content.biz.impl;

import javautils.StringUtil;
import javautils.jdbc.PageList;
import lottery.domains.content.biz.UserLoginLogService;
import lottery.domains.content.dao.UserDao;
import lottery.domains.content.dao.UserLoginLogDao;
import lottery.domains.content.entity.HistoryUserLoginLog;
import lottery.domains.content.entity.User;
import lottery.domains.content.entity.UserLoginLog;
import lottery.domains.content.vo.user.HistoryUserLoginLogVO;
import lottery.domains.content.vo.user.UserLoginLogVO;
import lottery.domains.content.vo.user.UserLoginSameIpLog;
import lottery.domains.pool.LotteryDataFactory;
import org.apache.commons.lang.StringUtils;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class UserLoginLogServiceImpl implements UserLoginLogService {
	
	/**
	 * DAO
	 */
	@Autowired
	private UserDao uDao;
	
	@Autowired
	private UserLoginLogDao uLoginLogDao;

	/**
	 * DataFactory
	 */
	@Autowired
	private LotteryDataFactory lotteryDataFactory;
	
	@Override
	public PageList search(String username, String ip, String date,String loginLine, int start, int limit) {
		List<Criterion> criterions = new ArrayList<>();
		List<Order> orders = new ArrayList<>();
		boolean isSearch = true;
		if(StringUtil.isNotNull(username)) {
			User user = uDao.getByUsername(username);
			if(user != null) {
				criterions.add(Restrictions.eq("userId", user.getId()));
			} else {
				isSearch = false;
			}
		}
		if(StringUtil.isNotNull(ip)) {
			criterions.add(Restrictions.eq("ip", ip));
		}
		if(StringUtil.isNotNull(date)) {
			criterions.add(Restrictions.ge("time", date));
		}
		if(StringUtil.isNotNull(loginLine)) {
			criterions.add(Restrictions.eq("loginLine", loginLine));
		}
		
		orders.add(Order.desc("id"));
		if(isSearch) {
			List<UserLoginLogVO> list = new ArrayList<>();
			PageList pList = uLoginLogDao.find(criterions, orders, start, limit);
			for (Object tmpBean : pList.getList()) {
				list.add(new UserLoginLogVO((UserLoginLog) tmpBean, lotteryDataFactory));
			}
			pList.setList(list);
			return pList;
		}
		return null;
	}
	@Override
	public PageList searchHistory(String username, String ip, String date,String loginLine, int start, int limit) {
		List<Criterion> criterions = new ArrayList<>();
		List<Order> orders = new ArrayList<>();
		boolean isSearch = true;
		if(StringUtil.isNotNull(username)) {
			User user = uDao.getByUsername(username);
			if(user != null) {
				criterions.add(Restrictions.eq("userId", user.getId()));
			} else {
				isSearch = false;
			}
		}
		if(StringUtil.isNotNull(ip)) {
			criterions.add(Restrictions.eq("ip", ip));
		}
		if(StringUtil.isNotNull(date)) {
			criterions.add(Restrictions.like("time", date, MatchMode.ANYWHERE));
		}
		if(StringUtil.isNotNull(loginLine)) {
			criterions.add(Restrictions.like("loginLine", loginLine));
		}
		
		orders.add(Order.desc("id"));
		if(isSearch) {
			List<HistoryUserLoginLogVO> list = new ArrayList<>();
			PageList pList = uLoginLogDao.findHistory(criterions, orders, start, limit);
			for (Object tmpBean : pList.getList()) {
				list.add(new HistoryUserLoginLogVO((HistoryUserLoginLog) tmpBean, lotteryDataFactory));
			}
			pList.setList(list);
			return pList;
		}
		return null;
	}
	
	@Override
	public PageList searchSameIp(String username, String ip, int start, int limit) {
		if (StringUtils.isEmpty(username) && StringUtils.isEmpty(ip)) {
			return null;
		}


		Integer userId = null;
		if (StringUtils.isNotEmpty(username)) {
			User user = uDao.getByUsername(username);
			if (user != null) {
				userId = user.getId();
			}
			else {
				return null;
			}
		}

		PageList pageList = uLoginLogDao.searchSameIp(userId, ip, start, limit);
		List<?> list = pageList.getList();
		List<UserLoginSameIpLog> voList = new ArrayList<>(list.size());
		for (Object o : list) {
			Object[] objs = (Object[]) o;
			String _ip = objs[0] != null ? objs[0].toString() : "";
			String _address = objs[1] != null ? objs[1].toString() : "";
			String _users = objs[2] != null ? objs[2].toString() : "";
			UserLoginSameIpLog vo = new UserLoginSameIpLog(_ip, _address, _users);
			voList.add(vo);
		}
		pageList.setList(voList);
		return pageList;
	}
}