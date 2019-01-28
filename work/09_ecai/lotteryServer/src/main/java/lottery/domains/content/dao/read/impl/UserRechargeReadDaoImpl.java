package lottery.domains.content.dao.read.impl;

import javautils.jdbc.PageList;
import javautils.jdbc.hibernate.HibernateSuperReadDao;
import lottery.domains.content.dao.read.UserRechargeReadDao;
import lottery.domains.content.entity.UserRecharge;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Order;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class UserRechargeReadDaoImpl implements UserRechargeReadDao {

	private final String tab = UserRecharge.class.getSimpleName();
	
	@Autowired
	private HibernateSuperReadDao<UserRecharge> superDao;
	
	@Override
	public PageList search(List<Criterion> criterions, List<Order> orders, int start, int limit) {
		String propertyName = "id";
		return superDao.findPageList(UserRecharge.class, propertyName, criterions, orders, start, limit);
	}

	@Override
	public long countTotal(int userId, int status) {
		String hql = "select count(id) from " + tab + " where userId = ?0 and status = ?1 and id > 0";
		Object[] values = {userId, status};
		Object result = superDao.unique(hql, values);
		return result != null ? (Long) result : 0;
	}
}