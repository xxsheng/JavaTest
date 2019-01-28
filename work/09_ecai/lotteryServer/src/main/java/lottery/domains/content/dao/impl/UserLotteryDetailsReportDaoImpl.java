package lottery.domains.content.dao.impl;

import javautils.jdbc.hibernate.HibernateSuperDao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import lottery.domains.content.dao.UserLotteryDetailsReportDao;
import lottery.domains.content.entity.UserLotteryDetailsReport;

@Repository
public class UserLotteryDetailsReportDaoImpl implements UserLotteryDetailsReportDao {
	
	private final String tab = UserLotteryDetailsReport.class.getSimpleName();
	
	@Autowired
	private HibernateSuperDao<UserLotteryDetailsReport> superDao;

	@Override
	public boolean add(UserLotteryDetailsReport entity) {
		return superDao.save(entity);
	}

	@Override
	public UserLotteryDetailsReport get(int userId, int lotteryId, int ruleId, String time) {
		String hql = "from " + tab + " where userId = ?0 and lotteryId = ?1 and ruleId = ?2 and time = ?3";
		Object[] values = {userId, lotteryId, ruleId, time};
		return (UserLotteryDetailsReport) superDao.unique(hql, values);
	}

	@Override
	public boolean update(UserLotteryDetailsReport entity) {
		String hql = "update " + tab + " set spend = spend + ?1, prize = prize + ?2, spendReturn = spendReturn + ?3, proxyReturn = proxyReturn + ?4, cancelOrder = cancelOrder + ?5, billingOrder = billingOrder + ?6 where id = ?0";
		Object[] values = {entity.getId(), entity.getSpend(), entity.getPrize(), entity.getSpendReturn(), entity.getProxyReturn(), entity.getCancelOrder(), entity.getBillingOrder()};
		return superDao.update(hql, values);
	}

}