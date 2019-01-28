package lottery.domains.content.dao.impl;

import java.util.List;

import javautils.jdbc.hibernate.HibernateSuperDao;

import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Order;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import lottery.domains.content.dao.UserBaccaratReportDao;
import lottery.domains.content.entity.UserBaccaratReport;

@Repository
public class UserBaccaratReportDaoImpl implements UserBaccaratReportDao {
	
	private final String tab = UserBaccaratReport.class.getSimpleName();
	
	@Autowired
	private HibernateSuperDao<UserBaccaratReport> superDao;

	@Override
	public boolean add(UserBaccaratReport entity) {
		return superDao.save(entity);
	}

	@Override
	public UserBaccaratReport get(int userId, String time) {
		String hql = "from " + tab + " where userId = ?0 and time = ?1";
		Object[] values = {userId, time};
		return (UserBaccaratReport) superDao.unique(hql, values);
	}

	@Override
	public boolean update(UserBaccaratReport entity) {
		String hql = "update " + tab + " set transIn = transIn + ?1, transOut = transOut + ?2, spend = spend + ?3, prize = prize + ?4, waterReturn = waterReturn + ?5, proxyReturn = proxyReturn + ?6, cancelOrder = cancelOrder + ?7, activity = activity + ?8, billingOrder = billingOrder + ?9 where id = ?0";
		Object[] values = {entity.getId(), entity.getTransIn(), entity.getTransOut(), entity.getSpend(), entity.getPrize(), entity.getWaterReturn(), entity.getProxyReturn(), entity.getCancelOrder(), entity.getActivity(), entity.getBillingOrder()};
		return superDao.update(hql, values);
	}

	@Override
	public List<UserBaccaratReport> find(List<Criterion> criterions,
			List<Order> orders) {
		return superDao.findByCriteria(UserBaccaratReport.class, criterions, orders);
	}

}
