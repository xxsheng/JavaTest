package lottery.domains.content.biz.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import javautils.StringUtil;
import javautils.jdbc.PageList;

import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Disjunction;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import lottery.domains.content.biz.UserActionLogService;
import lottery.domains.content.dao.UserActionLogDao;
import lottery.domains.content.dao.UserDao;
import lottery.domains.content.entity.User;
import lottery.domains.content.entity.UserActionLog;
import lottery.domains.content.vo.user.UserActionLogVO;
import lottery.domains.pool.LotteryDataFactory;

@Service
public class UserActionLogServiceImpl implements UserActionLogService {
	
	@Autowired
	private UserDao uDao;
	
	@Autowired
	private UserActionLogDao uActionLogDao;
	
	@Autowired
	private LotteryDataFactory lotteryDataFactory;

	@Override
	public PageList search(String username, String ip, String keyword, String date, int start, int limit) {
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
		if(StringUtil.isNotNull(keyword)) {
			StringTokenizer token = new StringTokenizer(keyword);
			Disjunction disjunction = Restrictions.or();
			while (token.hasMoreElements()) {
				String value = (String) token.nextElement();
				disjunction.add(Restrictions.like("action", value, MatchMode.ANYWHERE));
			}
			criterions.add(disjunction);
		}
		if(StringUtil.isNotNull(date)) {
			criterions.add(Restrictions.like("time", date, MatchMode.ANYWHERE));
		}
		orders.add(Order.desc("id"));
		if(isSearch) {
			List<UserActionLogVO> list = new ArrayList<>();
			PageList pList = uActionLogDao.find(criterions, orders, start, limit);
			for (Object tmpBean : pList.getList()) {
				list.add(new UserActionLogVO((UserActionLog) tmpBean, lotteryDataFactory));
			}
			pList.setList(list);
			return pList;
		}
		return null;
	}
	
}