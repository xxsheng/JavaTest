package lottery.domains.content.dao.impl;

import java.util.List;

import javautils.jdbc.PageList;
import javautils.jdbc.hibernate.HibernateSuperDao;

import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Order;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import lottery.domains.content.dao.LotteryOpenTimeDao;
import lottery.domains.content.entity.LotteryOpenTime;

@Repository
public class LotteryOpenTimeDaoImpl implements LotteryOpenTimeDao {

	private final String tab = LotteryOpenTime.class.getSimpleName();
	
	@Autowired
	private HibernateSuperDao<LotteryOpenTime> superDao;
	
	@Override
	public List<LotteryOpenTime> listAll() {
		String hql = "from " + tab + " order by expect asc";
		return superDao.list(hql);
	}
	
	@Override
	public List<LotteryOpenTime> list(String lottery) {
		String hql = "from " + tab + " where lottery = ?0";
		Object[] values = {lottery};
		return superDao.list(hql, values);
	}

	@Override
	public PageList find(List<Criterion> criterions, List<Order> orders, int start, int limit) {
		return superDao.findPageList(LotteryOpenTime.class, criterions, orders, start, limit);
	}

	@Override
	public LotteryOpenTime getById(int id) {
		String hql = "from " + tab + " where id = ?0";
		Object[] values = {id};
		return (LotteryOpenTime) superDao.unique(hql, values);
	}

	@Override
	public LotteryOpenTime getByLottery(String lottery) {
		String hql = "from " + tab + " where lottery = ?0";
		Object[] values = {lottery};
		return (LotteryOpenTime) superDao.unique(hql, values);
	}

	@Override
	public boolean update(LotteryOpenTime entity) {
		return superDao.update(entity);
	}

	@Override
	public boolean save(LotteryOpenTime entity) {
		return superDao.save(entity);
	}
}