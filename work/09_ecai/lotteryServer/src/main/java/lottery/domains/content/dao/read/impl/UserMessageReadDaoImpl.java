package lottery.domains.content.dao.read.impl;

import javautils.jdbc.PageList;
import javautils.jdbc.hibernate.HibernateSuperReadDao;
import lottery.domains.content.dao.read.UserMessageReadDao;
import lottery.domains.content.entity.UserMessage;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Order;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class UserMessageReadDaoImpl implements UserMessageReadDao {

	private final String tab = UserMessage.class.getSimpleName();
	
	@Autowired
	private HibernateSuperReadDao<UserMessage> superDao;

	@Override
	public int getUnreadCount(int userId) {
		String hql = "select count(id) from " + tab + " where toUid = ?0 and toStatus = 0 and id > 0";
		Object[] values = {userId};
		Object result = superDao.unique(hql, values);
		return result != null ? ((Number) result).intValue() : 0;
	}

	@Override
	public PageList search(List<Criterion> criterions, List<Order> orders, int start, int limit) {
		String propertyName = "id";
		return superDao.findPageList(UserMessage.class, propertyName, criterions, orders, start, limit);
	}

}