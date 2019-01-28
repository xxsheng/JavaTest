package lottery.domains.content.dao.impl;

import java.util.List;

import javautils.jdbc.PageList;
import javautils.jdbc.hibernate.HibernateSuperDao;

import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Order;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import lottery.domains.content.dao.UserActionLogDao;
import lottery.domains.content.entity.UserActionLog;

@Repository
public class UserActionLogDaoImpl implements UserActionLogDao {
	
	@Autowired
	private HibernateSuperDao<UserActionLog> superDao;

	@Override
	public PageList find(List<Criterion> criterions, List<Order> orders,
			int start, int limit) {
		String propertyName = "id";
		return superDao.findPageList(UserActionLog.class, propertyName, criterions, orders, start, limit);
	}

}