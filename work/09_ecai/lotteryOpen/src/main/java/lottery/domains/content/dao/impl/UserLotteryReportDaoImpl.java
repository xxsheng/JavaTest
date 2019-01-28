package lottery.domains.content.dao.impl;

import javautils.jdbc.hibernate.HibernateSuperDao;
import lottery.domains.content.dao.UserLotteryReportDao;
import lottery.domains.content.entity.UserLotteryReport;
import lottery.domains.content.entity.UserLotteryReport;
import org.apache.commons.collections.CollectionUtils;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Repository
public class UserLotteryReportDaoImpl implements UserLotteryReportDao {
	
	private final String tab = UserLotteryReport.class.getSimpleName();
	
	@Autowired
	private HibernateSuperDao<UserLotteryReport> superDao;

	@Override
	public UserLotteryReport get(int userId, String time) {
		String hql = "from " + tab + " where userId = ?0 and time = ?1";
		Object[] values = {userId, time};
		return (UserLotteryReport) superDao.unique(hql, values);
	}
	@Override
	public boolean update(UserLotteryReport entity) {
		String hql = "update " + tab + " set transIn = transIn + ?1, transOut = transOut + ?2, spend = spend + ?3, prize = prize + ?4, spendReturn = spendReturn + ?5, proxyReturn = proxyReturn + ?6, cancelOrder = cancelOrder + ?7, activity = activity + ?8, billingOrder = billingOrder + ?9, packet = packet + ?10 where id = ?0";
		Object[] values = {entity.getId(), entity.getTransIn(), entity.getTransOut(), entity.getSpend(), entity.getPrize(), entity.getSpendReturn(), entity.getProxyReturn(), entity.getCancelOrder(), entity.getActivity(), entity.getBillingOrder(), entity.getPacket()};
		return superDao.update(hql, values);
	}
	
	@Override
	public boolean add(UserLotteryReport entity) {
		return superDao.save(entity);
	}

	@Override
	public List<UserLotteryReport> find(List<Criterion> criterions,
										List<Order> orders) {
		return superDao.findByCriteria(UserLotteryReport.class, criterions, orders);
	}
}
