package lottery.domains.content.dao.impl;

import java.util.List;

import javautils.jdbc.PageList;
import javautils.jdbc.hibernate.HibernateSuperDao;

import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Order;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import lottery.domains.content.dao.UserMessageDao;
import lottery.domains.content.entity.UserMessage;

@Repository
public class UserMessageDaoImpl implements UserMessageDao {

	private final String tab = UserMessage.class.getSimpleName();
	
	@Autowired
	private HibernateSuperDao<UserMessage> superDao;
	
	@Override
	public UserMessage getById(int id) {
		String hql = "from " + tab + " where id = ?0";
		Object[] values = {id};
		return (UserMessage) superDao.unique(hql, values);
	}
	
	@Override
	public boolean delete(int id) {
		String hql = "update " + tab + " set toStatus = -1, fromStatus = -1 where id = ?0";
		Object[] values = {id};
		return superDao.update(hql, values);
	}

	@Override
	public PageList search(List<Criterion> criterions, List<Order> orders, int start, int limit) {
		String propertyName = "id";
		return superDao.findPageList(UserMessage.class, propertyName, criterions, orders, start, limit);
	}

	@Override
	public boolean save(UserMessage userMessage) {
		return superDao.save(userMessage);
	}

	@Override
	public void update(UserMessage message) {
		superDao.update(message);
	}
}