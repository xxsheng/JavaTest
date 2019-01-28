package lottery.domains.content.dao.read.impl;

import javautils.jdbc.PageList;
import javautils.jdbc.hibernate.HibernateSuperReadDao;
import lottery.domains.content.dao.read.UserSysMessageReadDao;
import lottery.domains.content.entity.UserSysMessage;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Order;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class UserSysMessageReadDaoImpl implements UserSysMessageReadDao {
	
	private final String tab = UserSysMessage.class.getSimpleName();
	
	@Autowired
	private HibernateSuperReadDao<UserSysMessage> superDao;

	@Override
	public PageList search(List<Criterion> criterions, List<Order> orders, int start, int limit) {
		String propertyName = "id";
		return superDao.findPageList(UserSysMessage.class, propertyName, criterions, orders, start, limit);
	}

	@Override
	public int getUnreadCount(int userId) {
		String hql = "select count(id) from " + tab + " where userId = ?0 and status = 0";
		Object[] values = {userId};
		Object result = superDao.unique(hql, values);
		return result != null ? ((Number) result).intValue() : 0;
	}
}