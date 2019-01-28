package lottery.domains.content.dao.read.impl;

import javautils.date.Moment;
import javautils.jdbc.PageList;
import javautils.jdbc.hibernate.HibernateSuperReadDao;
import lottery.domains.content.dao.read.UserLoginLogReadDao;
import lottery.domains.content.entity.UserLoginLog;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Order;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class UserLoginLogReadDaoImpl implements UserLoginLogReadDao {
	
	private final String tab = UserLoginLog.class.getSimpleName();
	
	@Autowired
	private HibernateSuperReadDao<UserLoginLog> superDao;
	
	@Override
	public UserLoginLog getLastLogin(int userId, int start) {
		String hql = "from " + tab + " where userId = ?0 order by id desc";
		Object[] values = {userId};
		List<UserLoginLog> list = superDao.list(hql, values, start, 1);
		if(list != null && list.size() > 0) {
			return list.get(0);
		}
		return null;
	}


	@Override
	public List<UserLoginLog> getLoginLog(int userId, int start, int limit) {
		String hql = "from " + tab + " where userId = ?0 and time >?1 order by id desc";
		Object[] values = {userId, new Moment().subtract(1, "months").toSimpleTime()};
		List<UserLoginLog> list = superDao.list(hql, values, start, limit);
		return list;
	}

	@Override
	public PageList getLoginLogList(List<Criterion> criterions, List<Order> orders,
									int start, int limit) {
		String propertyName = "id";
		return superDao.findPageList(UserLoginLog.class, propertyName, criterions, orders, start, limit);
	}
}