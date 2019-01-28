package lottery.domains.content.dao.impl;

import javautils.jdbc.PageList;
import javautils.jdbc.hibernate.HibernateSuperDao;
import lottery.domains.content.dao.UserBetsHitRankingDao;
import lottery.domains.content.entity.UserBetsHitRanking;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Order;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class UserBetsHitRankingDaoImpl implements UserBetsHitRankingDao {

	private final String tab = UserBetsHitRanking.class.getSimpleName();

	@Autowired
	private HibernateSuperDao<UserBetsHitRanking> superDao;

	@Override
	public UserBetsHitRanking getById(int id) {
		String hql = "from " + tab + " where id = ?0";
		Object[] values = { id };
		return (UserBetsHitRanking) superDao.unique(hql, values);
	}

	@Override
	public boolean add(UserBetsHitRanking entity) {
		return superDao.save(entity);
	}

	@Override
	public boolean update(UserBetsHitRanking entity) {
		return superDao.update(entity);
	}

	@Override
	public boolean delete(int id) {
		String hql = "delete from " + tab + " where id = ?0";
		Object[] values = { id };
		return superDao.delete(hql, values);
	}

	@Override
	public PageList find(List<Criterion> criterions, List<Order> orders, int start, int limit) {
		return superDao.findPageList(UserBetsHitRanking.class, criterions, orders, start, limit);
	}

}