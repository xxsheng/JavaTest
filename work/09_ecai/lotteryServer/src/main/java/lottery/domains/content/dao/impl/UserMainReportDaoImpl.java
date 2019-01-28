package lottery.domains.content.dao.impl;

import javautils.jdbc.hibernate.HibernateSuperDao;
import lottery.domains.content.dao.UserMainReportDao;
import lottery.domains.content.entity.UserMainReport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class UserMainReportDaoImpl implements UserMainReportDao {
	
	private final String tab = UserMainReport.class.getSimpleName();
	
	@Autowired
	private HibernateSuperDao<UserMainReport> superDao;

	@Override
	public boolean add(UserMainReport entity) {
		return superDao.save(entity);
	}

	@Override
	public UserMainReport get(int userId, String time) {
		String hql = "from " + tab + " where userId = ?0 and time = ?1";
		Object[] values = {userId, time};
		return (UserMainReport) superDao.unique(hql, values);
	}

	@Override
	public boolean update(UserMainReport entity) {
		String hql = "update " + tab + " set recharge = recharge + ?1, withdrawals = withdrawals + ?2, transIn = transIn + ?3, transOut = transOut + ?4, accountIn = accountIn + ?5, accountOut = accountOut + ?6, activity = activity + ?7, receiveFeeMoney = receiveFeeMoney + ?8 where id = ?0";
		Object[] values = {entity.getId(), entity.getRecharge(), entity.getWithdrawals(), entity.getTransIn(), entity.getTransOut(), entity.getAccountIn(), entity.getAccountOut(), entity.getActivity(), entity.getReceiveFeeMoney()};
		return superDao.update(hql, values);
	}
}
