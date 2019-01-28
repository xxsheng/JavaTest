package lottery.domains.content.dao.impl;

import java.util.List;

import javautils.jdbc.PageList;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Order;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javautils.array.ArrayUtils;
import javautils.jdbc.hibernate.HibernateSuperDao;
import lottery.domains.content.dao.UserSysMessageDao;
import lottery.domains.content.entity.UserSysMessage;

@Repository
public class UserSysMessageDaoImpl implements UserSysMessageDao {

	private final String tab = UserSysMessage.class.getSimpleName();

	@Autowired
	private HibernateSuperDao<UserSysMessage> superDao;

	@Override
	public boolean add(UserSysMessage entity) {
		return superDao.save(entity);
	}

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

	@Override
	public boolean updateUnread(int userId, int[] ids) {
		if(ids.length > 0) {
			String hql = "update " + tab + " set status = 1 where userId = ?0 and id in (" + ArrayUtils.transInIds(ids) + ")";
			Object[] values = {userId};
			return superDao.update(hql, values);
		}
		return false;
	}

	@Override
	public boolean deleteMsg(int userId, int[] ids) {
		String hql = "delete from " + tab + " where userId = ?0 and id in (" + ArrayUtils.transInIds(ids) + ")";
		Object[] values = {userId};
		return superDao.delete(hql, values);
	}
}