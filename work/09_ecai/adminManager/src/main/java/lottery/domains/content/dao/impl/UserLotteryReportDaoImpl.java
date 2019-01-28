package lottery.domains.content.dao.impl;

import javautils.array.ArrayUtils;
import javautils.date.Moment;
import javautils.jdbc.hibernate.HibernateSuperDao;
import lottery.domains.content.dao.UserLotteryReportDao;
import lottery.domains.content.entity.HistoryUserLotteryReport;
import lottery.domains.content.entity.UserLotteryReport;
import lottery.domains.content.global.Database;
import lottery.domains.content.vo.bill.HistoryUserLotteryReportVO;
import lottery.domains.content.vo.bill.UserLotteryReportVO;
import lottery.domains.content.vo.bill.UserProfitRankingVO;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Order;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class UserLotteryReportDaoImpl implements UserLotteryReportDao {
	
	private final String tab = UserLotteryReport.class.getSimpleName();
	
	@Autowired
	private HibernateSuperDao<UserLotteryReport> superDao;


	@Autowired
	private HibernateSuperDao<HistoryUserLotteryReport> historySuperDao;
	
    private final String hsInstance =  Database.name;
    private final String hsbackupInstance=Database.backup;
	
	
	@Override
	public boolean add(UserLotteryReport entity) {
		return superDao.save(entity);
	}

	@Override
	public UserLotteryReport get(int userId, String time) {
		String hql = "from " + tab + " where userId = ?0 and time = ?1";
		Object[] values = {userId, time};
		return (UserLotteryReport) superDao.unique(hql, values);
	}
	
	@Override
	public List<UserLotteryReport> list(int userId, String sTime, String eTime) {
		String hql = "from " + tab + " where userId = ?0 and time >= ?1 and time < ?2 order by time asc";
		Object[] values = {userId, sTime, eTime};
		return superDao.list(hql, values);
	}

	@Override
	public boolean update(UserLotteryReport entity) {
		String hql = "update " + tab + " set transIn = transIn + ?1, transOut = transOut + ?2, spend = spend + ?3, prize = prize + ?4, spendReturn = spendReturn + ?5, proxyReturn = proxyReturn + ?6, cancelOrder = cancelOrder + ?7, activity = activity + ?8, billingOrder = billingOrder + ?9,rechargeFee = rechargeFee + ?10 where id = ?0";
		Object[] values = {entity.getId(), entity.getTransIn(), entity.getTransOut(), entity.getSpend(), entity.getPrize(), entity.getSpendReturn(), entity.getProxyReturn(), entity.getCancelOrder(), entity.getActivity(), entity.getBillingOrder(), entity.getRechargeFee()};
		return superDao.update(hql, values);
	}
	
	@Override
	public List<UserLotteryReport> find(List<Criterion> criterions,
			List<Order> orders) {
		return superDao.findByCriteria(UserLotteryReport.class, criterions, orders);
	}
	
	@Override
	public List<HistoryUserLotteryReport> findHistory(List<Criterion> criterions,
			List<Order> orders) {
		return historySuperDao.findByCriteria(HistoryUserLotteryReport.class, criterions, orders);
	}
	
	@Override
	public List<UserLotteryReport> getDayList(int[] ids, String sDate, String eDate) {
		if(ids.length > 0) {
			String hql = "from " + tab + " where userId in (" + ArrayUtils.transInIds(ids) +  ") and time >= ?0 and time < ?1";
			Object[] values = {sDate, eDate};
			return superDao.list(hql, values);
		}
		return new ArrayList<>();
	}

	@Override
	public UserLotteryReportVO sumLowersAndSelf(int userId, String sTime, String eTime) {
		String sql = "select sum(ulr.trans_in) transIn,sum(ulr.trans_out) transOut,sum(ulr.prize) prize,sum(ulr.spend_return) spendReturn,sum(ulr.proxy_return) proxyReturn,sum(ulr.activity) activity,sum(ulr.dividend) dividend,sum(ulr.billing_order) billingOrder,sum(ulr.recharge_fee) rechargeFee from user_lottery_report ulr left join user u on ulr.user_id = u.id where ulr.time >= :sTime and ulr.time < :eTime and (u.upids like :upid or ulr.user_id = :userId)";
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
		double transIn = results[0] == null ? 0 : ((BigDecimal) results[0]).doubleValue();
		double transOut = results[1] == null ? 0 : ((BigDecimal) results[1]).doubleValue();
		double prize = results[2] == null ? 0 : ((BigDecimal) results[2]).doubleValue();
		double spendReturn = results[3] == null ? 0 : ((BigDecimal) results[3]).doubleValue();
		double proxyReturn = results[4] == null ? 0 : ((BigDecimal) results[4]).doubleValue();
		double activity = results[5] == null ? 0 : ((BigDecimal) results[5]).doubleValue();
		double dividend = results[6] == null ? 0 : ((BigDecimal) results[6]).doubleValue();
		double billingOrder = results[7] == null ? 0 : ((BigDecimal) results[7]).doubleValue();
		double rechargeFee = results[8] == null ? 0 : ((BigDecimal) results[8]).doubleValue();

		UserLotteryReportVO report = new UserLotteryReportVO();
		report.setTransIn(transIn);
		report.setTransOut(transOut);
		report.setPrize(prize);
		report.setSpendReturn(spendReturn);
		report.setProxyReturn(proxyReturn);
		report.setActivity(activity);
		report.setDividend(dividend);
		report.setBillingOrder(billingOrder);
		report.setRechargeFee(rechargeFee);
		return report;
	}

	@Override
	public HistoryUserLotteryReportVO historySumLowersAndSelf(int userId, String sTime, String eTime) {
		String sql = "select sum(ulr.trans_in) transIn,sum(ulr.trans_out) transOut,sum(ulr.prize) prize,sum(ulr.spend_return) spendReturn,sum(ulr.proxy_return) proxyReturn,sum(ulr.activity) activity,sum(ulr.dividend) dividend,sum(ulr.billing_order) billingOrder,sum(ulr.recharge_fee) rechargeFee from "+hsbackupInstance+".user_lottery_report ulr left join "+hsInstance+".user u on ulr.user_id = u.id where ulr.time >= :sTime and ulr.time < :eTime and (u.upids like :upid or ulr.user_id = :userId)";
		Map<String, Object> params = new HashMap<>();
		params.put("sTime", sTime);
		params.put("eTime", eTime);
		params.put("upid", "%[" + userId + "]%");
		params.put("userId", userId);

		Object result = historySuperDao.uniqueSqlWithParams(sql, params);
		if (result == null) {
			return null;
		}
		Object[] results = (Object[]) result;
		double transIn = results[0] == null ? 0 : ((BigDecimal) results[0]).doubleValue();
		double transOut = results[1] == null ? 0 : ((BigDecimal) results[1]).doubleValue();
		double prize = results[2] == null ? 0 : ((BigDecimal) results[2]).doubleValue();
		double spendReturn = results[3] == null ? 0 : ((BigDecimal) results[3]).doubleValue();
		double proxyReturn = results[4] == null ? 0 : ((BigDecimal) results[4]).doubleValue();
		double activity = results[5] == null ? 0 : ((BigDecimal) results[5]).doubleValue();
		double dividend = results[6] == null ? 0 : ((BigDecimal) results[6]).doubleValue();
		double billingOrder = results[7] == null ? 0 : ((BigDecimal) results[7]).doubleValue();
		double rechargeFee = results[8] == null ? 0 : ((BigDecimal) results[8]).doubleValue();

		HistoryUserLotteryReportVO report = new HistoryUserLotteryReportVO();
		report.setTransIn(transIn);
		report.setTransOut(transOut);
		report.setPrize(prize);
		report.setSpendReturn(spendReturn);
		report.setProxyReturn(proxyReturn);
		report.setActivity(activity);
		report.setDividend(dividend);
		report.setBillingOrder(billingOrder);
		report.setRechargeFee(rechargeFee);
		return report;
	}
	
	@Override
	public UserLotteryReportVO sum(int userId, String sTime, String eTime) {
		String sql = "select sum(ulr.trans_in) transIn,sum(ulr.trans_out) transOut,sum(ulr.prize) prize,sum(ulr.spend_return) spendReturn,sum(ulr.proxy_return) proxyReturn,sum(ulr.activity) activity,sum(ulr.dividend) dividend,sum(ulr.billing_order) billingOrder,sum(ulr.recharge_fee) recharge_fee from user_lottery_report ulr left join user u on ulr.user_id = u.id where ulr.time >= :sTime and ulr.time < :eTime and ulr.user_id = :userId";
		Map<String, Object> params = new HashMap<>();
		params.put("sTime", sTime);
		params.put("eTime", eTime);
		params.put("userId", userId);
		Object result = superDao.uniqueSqlWithParams(sql, params);
		if (result == null) {
			return null;
		}
		Object[] results = (Object[]) result;
		double transIn = results[0] == null ? 0 : ((BigDecimal) results[0]).doubleValue();
		double transOut = results[1] == null ? 0 : ((BigDecimal) results[1]).doubleValue();
		double prize = results[2] == null ? 0 : ((BigDecimal) results[2]).doubleValue();
		double spendReturn = results[3] == null ? 0 : ((BigDecimal) results[3]).doubleValue();
		double proxyReturn = results[4] == null ? 0 : ((BigDecimal) results[4]).doubleValue();
		double activity = results[5] == null ? 0 : ((BigDecimal) results[5]).doubleValue();
		double dividend = results[6] == null ? 0 : ((BigDecimal) results[6]).doubleValue();
		double billingOrder = results[7] == null ? 0 : ((BigDecimal) results[7]).doubleValue();
		double rechargeFee = results[8] == null ? 0 : ((BigDecimal) results[8]).doubleValue();

		UserLotteryReportVO report = new UserLotteryReportVO();
		report.setTransIn(transIn);
		report.setTransOut(transOut);
		report.setPrize(prize);
		report.setSpendReturn(spendReturn);
		report.setProxyReturn(proxyReturn);
		report.setActivity(activity);
		report.setDividend(dividend);
		report.setBillingOrder(billingOrder);
		report.setRechargeFee(rechargeFee);
		return report;
	}

	@Override
	public List<UserProfitRankingVO> listUserProfitRanking(String sTime, String eTime, int start, int limit) {
		String sql = "SELECT r.user_id as user_id , (sum(r.prize) + sum(r.spend_return) + sum(r.proxy_return) + sum(r.activity) + sum(r.packet) + sum(r.recharge_fee) - sum(r.billing_order)) profit FROM user_lottery_report r , user u where  r.user_id = u.id  and r.time >= :sTime and r.time < :eTime and r.id > 0 and u.upid != :upid group by r.user_id order by profit desc";

		Map<String, Object> params = new HashMap<>();
		params.put("sTime", sTime);
		params.put("eTime", eTime);
		params.put("upid", 0);

		List<?> results = superDao.listBySql(sql, params, start, limit);
		if (results == null || results.size() < 0) {
			return null;
		}
		List<UserProfitRankingVO> rankingVOs = new ArrayList<>(results.size());
		for (Object result : results) {
			Object[] resultArr = (Object[]) result;
			int _userId = ((Integer) resultArr[0]);
			double _profit = resultArr[1] == null ? 0 : ((BigDecimal) resultArr[1]).doubleValue();
			UserProfitRankingVO rankingVO = new UserProfitRankingVO(_userId, sTime, eTime, _profit);
			rankingVOs.add(rankingVO);
		}

		return rankingVOs;
	}

	@Override
	public List<UserProfitRankingVO> listUserProfitRankingByDate(int userId, String sTime, String eTime, int start, int limit) {
		String sql = "SELECT user_id, time, (sum(prize) + sum(spend_return) + sum(proxy_return) + sum(activity) + sum(packet) + sum(recharge_fee) - sum(billing_order)) profit FROM user_lottery_report where time >= :sTime and time < :eTime and id > 0 and user_id=:userId group by user_id,time order by time desc";

		Map<String, Object> params = new HashMap<>();
		params.put("sTime", sTime);
		params.put("eTime", eTime);
		params.put("userId", userId);

		List<?> results = superDao.listBySql(sql, params, start, limit);
		if (results == null || results.size() < 0) {
			return null;
		}
		List<UserProfitRankingVO> rankingVOs = new ArrayList<>(results.size());
		for (Object result : results) {
			Object[] resultArr = (Object[]) result;
			int _userId = ((Integer) resultArr[0]);
			String _sTime = resultArr[1].toString();
			String _eTime = new Moment().fromDate(_sTime).add(1, "days").toSimpleDate();
			double _profit = resultArr[2] == null ? 0 : ((BigDecimal) resultArr[2]).doubleValue();
			UserProfitRankingVO rankingVO = new UserProfitRankingVO(_userId, _sTime, _eTime, _profit);
			rankingVOs.add(rankingVO);
		}

		return rankingVOs;
	}
}
