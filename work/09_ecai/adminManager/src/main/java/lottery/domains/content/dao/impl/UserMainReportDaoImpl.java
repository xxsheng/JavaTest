package lottery.domains.content.dao.impl;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javautils.jdbc.hibernate.HibernateSuperDao;

import lottery.domains.content.vo.bill.UserLotteryReportVO;
import lottery.domains.content.vo.bill.UserMainReportVO;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Order;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import lottery.domains.content.dao.UserMainReportDao;
import lottery.domains.content.entity.UserMainReport;

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
	public List<UserMainReport> list(int userId, String sTime, String eTime) {
		String hql = "from " + tab + " where userId = ?0 and time >= ?1 and time < ?2";
		Object[] values = {userId, sTime, eTime};
		return superDao.list(hql, values);
	}

	@Override
	public boolean update(UserMainReport entity) {
		String hql = "update " + tab + " set recharge = recharge + ?1, withdrawals = withdrawals + ?2, transIn = transIn + ?3, transOut = transOut + ?4, accountIn = accountIn + ?5, accountOut = accountOut + ?6, activity = activity + ?7 where id = ?0";
		Object[] values = {entity.getId(), entity.getRecharge(), entity.getWithdrawals(), entity.getTransIn(), entity.getTransOut(), entity.getAccountIn(), entity.getAccountOut(), entity.getActivity()};
		return superDao.update(hql, values);
	}

	@Override
	public List<UserMainReport> find(List<Criterion> criterions,
			List<Order> orders) {
		return superDao.findByCriteria(UserMainReport.class, criterions, orders);
	}



	@Override
	public UserMainReportVO sumLowersAndSelf(int userId, String sTime, String eTime) {
		String sql = "select sum(umr.recharge) recharge,sum(umr.withdrawals) withdrawals,sum(umr.trans_in) transIn,sum(umr.trans_out) transOut,sum(umr.account_in) accountIn,sum(umr.account_out) accountOut,sum(umr.activity) activity from user_main_report umr left join user u on umr.user_id = u.id where umr.time >= :sTime and umr.time < :eTime and (u.upids like :upid or umr.user_id = :userId)";
		Map<String, Object> params = new HashMap<>();
		params.put("sTime", sTime);
		params.put("eTime", eTime);
		params.put("upid", "%[" + userId + "]%");
		params.put("userId", userId);

		Object result = superDao.uniqueSqlWithParams(sql, params);
		if (result == null) {
			return null;
		}
		Object[] results = (Object[]) result;
		double recharge = results[0] == null ? 0 : ((BigDecimal) results[0]).doubleValue();
		double withdrawals = results[1] == null ? 0 : ((BigDecimal) results[1]).doubleValue();
		double transIn = results[2] == null ? 0 : ((BigDecimal) results[2]).doubleValue();
		double transOut = results[3] == null ? 0 : ((BigDecimal) results[3]).doubleValue();
		double accountIn = results[4] == null ? 0 : ((BigDecimal) results[4]).doubleValue();
		double accountOut = results[5] == null ? 0 : ((BigDecimal) results[5]).doubleValue();
		double activity = results[6] == null ? 0 : ((BigDecimal) results[6]).doubleValue();

		UserMainReportVO report = new UserMainReportVO();
		report.setRecharge(recharge);
		report.setWithdrawals(withdrawals);
		report.setTransIn(transIn);
		report.setTransOut(transOut);
		report.setAccountIn(accountIn);
		report.setAccountOut(accountOut);
		report.setActivity(activity);
		return report;
	}
}
