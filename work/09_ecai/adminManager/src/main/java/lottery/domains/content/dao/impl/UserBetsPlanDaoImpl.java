package lottery.domains.content.dao.impl;

import java.util.List;

import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Order;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javautils.jdbc.PageList;
import javautils.jdbc.hibernate.HibernateSuperDao;
import lottery.domains.content.dao.UserBetsPlanDao;
import lottery.domains.content.entity.UserBetsPlan;

@Repository
public class UserBetsPlanDaoImpl implements UserBetsPlanDao {
	
	private final String tab = UserBetsPlan.class.getSimpleName();
	
	@Autowired
	private HibernateSuperDao<UserBetsPlan> superDao;

	@Override
	public boolean add(UserBetsPlan entity) {
		return superDao.save(entity);
	}
	
	@Override
	public UserBetsPlan get(int id) {
		String hql = "from " + tab + " where id = ?0";
		Object[] values = {id};
		return (UserBetsPlan) superDao.unique(hql, values);
	}

	@Override
	public UserBetsPlan get(int id, int userId) {
		String hql = "from " + tab + " where id = ?0 and userId = ?1";
		Object[] values = {id, userId};
		return (UserBetsPlan) superDao.unique(hql, values);
	}

	@Override
	public boolean hasRecord(int userId, int lotteryId, String expect) {
		String hql = "from " + tab + " where userId = ?0 and lotteryId = ?1 and expect = ?2";
		Object[] values = {userId, lotteryId, expect};
		List<UserBetsPlan> list = superDao.list(hql, values);
		if(list != null && list.size() > 0) {
			return true;
		}
		return false;
	}
	
	@Override
	public boolean updateCount(int id, int count) {
		String hql = "update " + tab + " set count = count + ?1 where id = ?0";
		Object[] values = {id, count};
		return superDao.update(hql, values);
	}
	
	@Override
	public boolean updateStatus(int id, int status) {
		String hql = "update " + tab + " set status = ?1 where id = ?0 and status = 0";
		Object[] values = {id, status};
		return superDao.update(hql, values);
	}
	
	@Override
	public boolean updateStatus(int id, int status, double prizeMoney) {
		String hql = "update " + tab + " set status = ?1, prizeMoney = ?2 where id = ?0 and status = 0";
		Object[] values = {id, status, prizeMoney};
		return superDao.update(hql, values);
	}
	
	@Override
	public List<UserBetsPlan> listUnsettled() {
		String hql = "from " + tab + " where status = 0";
		return superDao.list(hql);
	}
	
	@Override
	public List<UserBetsPlan> list(int lotteryId, String expect) {
		String hql = "from " + tab + " where lotteryId = ?0 and expect = ?1";
		Object[] values = {lotteryId, expect};
		return superDao.list(hql, values);
	}
	
	@Override
	public PageList search(List<Criterion> criterions, List<Order> orders,
			int start, int limit) {
		String propertyName = "id";
		return superDao.findPageList(UserBetsPlan.class, propertyName, criterions, orders, start, limit);
	}

}