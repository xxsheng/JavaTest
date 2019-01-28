package lottery.domains.content.dao.impl;

import javautils.jdbc.PageList;
import javautils.jdbc.hibernate.HibernateSuperDao;
import lottery.domains.content.dao.LotteryOpenCodeDao;
import lottery.domains.content.entity.LotteryOpenCode;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
public class LotteryOpenCodeDaoImpl implements LotteryOpenCodeDao {
	
	private final String tab = LotteryOpenCode.class.getSimpleName();
	
	@Autowired
	private HibernateSuperDao<LotteryOpenCode> superDao;

	@Override
	public PageList find(List<Criterion> criterions, List<Order> orders, int start, int limit) {
		return superDao.findPageList(LotteryOpenCode.class, criterions, orders, start, limit);
	}
	
	@Override
	public LotteryOpenCode get(String lottery, String expect) {
		String hql = "from " + tab + " where lottery = ?0 and expect = ?1";
		Object[] values = {lottery, expect};
		List<LotteryOpenCode> list = superDao.list(hql, values);
		if (list == null || list.size() <= 0) {
			return null;
		}
		return list.get(0);
	}
	
	@Override
	public boolean add(LotteryOpenCode entity) {
		return superDao.save(entity);
	}
	
	@Override
	public List<LotteryOpenCode> list(String lottery, String[] expects) {
		List<Criterion> criterions = new ArrayList<>();
		criterions.add(Restrictions.eq("lottery", lottery));
		criterions.add(Restrictions.in("expect", expects));
		List<Order> orders = new ArrayList<>();
		orders.add(Order.asc("expect"));
		return superDao.findByCriteria(LotteryOpenCode.class, criterions, orders);
	}
	
	@Override
	public boolean update(LotteryOpenCode entity) {
		return superDao.update(entity);
	}

	@Override
	public boolean delete(LotteryOpenCode entity) {
		String hql ="delete " + tab +" where lottery =?0 and expect=?1";
		Object[] values = {entity.getLottery(), entity.getExpect()};
		return superDao.delete(hql, values);
	}

	@Override
	public int countByInterfaceTime(String lottery, String startTime, String endTime) {
		String hql = "select count(id) from " + tab + " where interfaceTime >= ?0 and interfaceTime < ?1 and lottery=?2";
		Object[] values = {startTime, endTime, lottery};
		Object result = superDao.unique(hql, values);
		return result != null ? ((Number)result).intValue() : 0;
	}

	@Override
	public LotteryOpenCode getFirstExpectByInterfaceTime(String lottery, String startTime, String endTime) {
		String hql = "from " + tab + " where interfaceTime >= ?0 and interfaceTime < ?1 and lottery=?2 order by expect asc";
		Object[] values = {startTime, endTime, lottery};
		List<LotteryOpenCode> list = superDao.list(hql, values, 0, 1);
		if (list != null && list.size() > 0) {
			return list.get(0);
		}

		return null;
	}

	@Override
	public List<LotteryOpenCode> getLatest(String lottery, int count) {
		String hql = "from " + tab + " where lottery=?0 order by expect desc";
		Object[] values = {lottery};
		List<LotteryOpenCode> list = superDao.list(hql, values, 0, count);
		return list;
	}
}