package lottery.domains.content.dao.impl;

import javautils.jdbc.PageList;
import javautils.jdbc.hibernate.HibernateSuperDao;
import lottery.domains.content.dao.LotteryOpenTimeDao;
import lottery.domains.content.entity.LotteryOpenTime;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Order;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

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
	public PageList find(List<Criterion> criterions, List<Order> orders, int start, int limit) {
		return superDao.findPageList(LotteryOpenTime.class, criterions, orders, start, limit);
	}
}