package lottery.domains.content.dao.impl;

import java.util.List;

import javautils.array.ArrayUtils;
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
	public boolean add(UserMessage entity) {
		return superDao.save(entity);
	}
	
	@Override
	public int getUnreadCount(int userId) {
		String hql = "select count(id) from " + tab + " where toUid = ?0 and toStatus = 0";
		Object[] values = {userId};
		Object result = superDao.unique(hql, values);
		return result != null ? ((Number) result).intValue() : 0;
	}
	
	@Override
	public boolean updateInboxMessage(int userId, int[] ids, int status) {
		if(ids.length > 0) {
			String hql = "update " + tab + " set toStatus = ?1 where toUid = ?0 and id in (" + ArrayUtils.transInIds(ids) + ")";
			Object[] values = {userId, status};
			return superDao.update(hql, values);
		}
		return false;
	}
	
	@Override
	public boolean updateOutboxMessage(int userId, int[] ids, int status) {
		if(ids.length > 0) {
			String hql = "update " + tab + " set fromStatus = ?1 where fromUid = ?0 and id in (" + ArrayUtils.transInIds(ids) + ")";
			Object[] values = {userId, status};
			return superDao.update(hql, values);
		}
		return false;
	}
	
	@Override
	public PageList search(List<Criterion> criterions, List<Order> orders, int start, int limit) {
		String propertyName = "id";
		return superDao.findPageList(UserMessage.class, propertyName, criterions, orders, start, limit);
	}

}