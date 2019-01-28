package lottery.domains.content.dao.impl;

import java.util.List;

import javautils.jdbc.PageList;
import javautils.jdbc.hibernate.HibernateSuperDao;

import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Order;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import lottery.domains.content.dao.UserTransfersDao;
import lottery.domains.content.entity.UserTransfers;

@Repository
public class UserTransfersDaoImpl implements UserTransfersDao {
	
	private final String tab = UserTransfers.class.getSimpleName();

	@Autowired
	private HibernateSuperDao<UserTransfers> superDao;

	@Override
	public boolean add(UserTransfers entity) {
		return superDao.save(entity);
	}
	
	@Override
	public PageList search(List<Criterion> criterions, List<Order> orders, int start, int limit) {
		return superDao.findPageList(UserTransfers.class, criterions, orders, start, limit);
	}
	
	@Override
	public double getTotalTransfers(String sTime, String eTime, int type) {
		String hql = "select sum(money) from " + tab + " where time >= ?0 and time < ?1 and type = ?2";
		Object[] values = {sTime, eTime, type};
		Object result = superDao.unique(hql, values);
		return result != null ? ((Number) result).doubleValue() : 0;
	}
	
}