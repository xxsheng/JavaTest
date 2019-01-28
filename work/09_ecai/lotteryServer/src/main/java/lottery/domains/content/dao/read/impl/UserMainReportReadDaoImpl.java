package lottery.domains.content.dao.read.impl;

import javautils.array.ArrayUtils;
import javautils.date.Moment;
import javautils.jdbc.hibernate.HibernateSuperReadDao;
import lottery.domains.content.dao.read.UserMainReportReadDao;
import lottery.domains.content.entity.UserLotteryReport;
import lottery.domains.content.entity.UserMainReport;
import lottery.domains.content.vo.bill.UserMainReportRichVO;
import lottery.domains.content.vo.user.UserMainReportTeamStatisticsVO;
import org.apache.commons.collections.CollectionUtils;
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
public class UserMainReportReadDaoImpl implements UserMainReportReadDao {
	
	private final String tab = UserMainReport.class.getSimpleName();
	
	@Autowired
	private HibernateSuperReadDao<UserMainReport> superDao;

	@Override
	public List<UserMainReport> find(List<Criterion> criterions,
			List<Order> orders) {
		return superDao.findByCriteria(UserMainReport.class, criterions, orders);
	}

	@Override
	public double getTotalRecharge(int userId) {
		String date = new Moment().subtract(3, "months").toSimpleDate(); // 查询近3个月的数据，这里只是为了使用索引
		String hql = "select sum(recharge) from " + tab + " where userId = ?0 and time >= ?1";
		Object[] values = {userId, date};
		Object result = superDao.unique(hql, values);
		return result != null ? (Double) result : 0;
	}

	@Override
	public List<UserMainReport> getDayListAll(String sDate, String eDate) {
		StringBuffer sql = new StringBuffer("select umr.time,sum(umr.recharge),sum(umr.withdrawals),sum(umr.trans_in),sum(umr.trans_out),sum(umr.account_in),sum(umr.account_out) from user_main_report umr ,user u  where umr.user_id = u.id and u.upid !=0   and umr.time >= :sTime and umr.time < :eTime and umr.id > 0 group by umr.time");

		Map<String, Object> params = new HashMap<>();
		params.put("sTime", sDate);
		params.put("eTime", eDate);

		List<?> results = superDao.listBySql(sql.toString(), params);
		if (CollectionUtils.isEmpty(results)) {
			return new ArrayList<>();
		}

		List<UserMainReport> reports = new ArrayList<>(results.size());
		for (Object result : results) {
			Object[] resultArr = (Object[]) result;

			int index = 0;
			String time = resultArr[index] == null ? null : resultArr[index].toString();index++;
			double recharge = resultArr[index] == null ? 0 : ((BigDecimal) resultArr[index]).doubleValue();index++;
			double withdrawals = resultArr[index] == null ? 0 : ((BigDecimal) resultArr[index]).doubleValue();index++;
			double transIn = resultArr[index] == null ? 0 : ((BigDecimal) resultArr[index]).doubleValue();index++;
			double transOut = resultArr[index] == null ? 0 : ((BigDecimal) resultArr[index]).doubleValue();index++;
			double accountIn = resultArr[index] == null ? 0 : ((BigDecimal) resultArr[index]).doubleValue();index++;
			double accountOut = resultArr[index] == null ? 0 : ((BigDecimal) resultArr[index]).doubleValue();index++;

			UserMainReport report = new UserMainReport();
			report.setTime(time);
			report.setRecharge(recharge);
			report.setWithdrawals(withdrawals);
			report.setTransIn(transIn);
			report.setTransOut(transOut);
			report.setAccountIn(accountIn);
			report.setAccountOut(accountOut);

			reports.add(report);
		}

		return reports;
	}

	@Override
	public List<UserMainReport> getDayListByTeam(int userId, String sDate, String eDate) {
		return getDayListByTeam(new int[]{userId}, sDate, eDate);
	}

	@Override
	public List<UserMainReport> getDayListByTeam(int[] userIds, String sDate, String eDate) {
		StringBuffer sql = new StringBuffer("select umr.time,sum(umr.recharge),sum(umr.withdrawals),sum(umr.trans_in),sum(umr.trans_out),sum(umr.account_in),sum(umr.account_out) from user_main_report umr,user u where umr.user_id = u.id and (u.id in(:userIds)");

		Map<String, Object> params = new HashMap<>();
		for (int i = 0; i < userIds.length; i++) {
			sql.append(" or u.upids like :upid").append(i);
			params.put("upid" + i, "%[" + userIds[i] + "]%");
		}

		sql.append(") and umr.time >= :sTime and umr.time < :eTime and umr.id > 0 group by umr.time");

		params.put("userIds", ArrayUtils.transInIds(userIds));
		params.put("sTime", sDate);
		params.put("eTime", eDate);

		List<?> results = superDao.listBySql(sql.toString(), params);
		if (CollectionUtils.isEmpty(results)) {
			return new ArrayList<>();
		}

		List<UserMainReport> reports = new ArrayList<>(results.size());
		for (Object result : results) {
			Object[] resultArr = (Object[]) result;

			int index = 0;
			String time = resultArr[index] == null ? null : resultArr[index].toString();index++;
			double recharge = resultArr[index] == null ? 0 : ((BigDecimal) resultArr[index]).doubleValue();index++;
			double withdrawals = resultArr[index] == null ? 0 : ((BigDecimal) resultArr[index]).doubleValue();index++;
			double transIn = resultArr[index] == null ? 0 : ((BigDecimal) resultArr[index]).doubleValue();index++;
			double transOut = resultArr[index] == null ? 0 : ((BigDecimal) resultArr[index]).doubleValue();index++;
			double accountIn = resultArr[index] == null ? 0 : ((BigDecimal) resultArr[index]).doubleValue();index++;
			double accountOut = resultArr[index] == null ? 0 : ((BigDecimal) resultArr[index]).doubleValue();index++;

			UserMainReport report = new UserMainReport();
			report.setTime(time);
			report.setRecharge(recharge);
			report.setWithdrawals(withdrawals);
			report.setTransIn(transIn);
			report.setTransOut(transOut);
			report.setAccountIn(accountIn);
			report.setAccountOut(accountOut);

			reports.add(report);
		}

		return reports;
	}

	@Override
	public List<UserMainReportRichVO> searchAll(String sTime, String eTime) {
		String sql = "select umr.user_id,sum(umr.recharge),sum(umr.receive_fee_money),sum(umr.withdrawals),sum(umr.trans_in),sum(umr.trans_out),sum(umr.account_in),sum(umr.account_out),sum(umr.activity),u.username,u.upid,u.upids from user_main_report umr,user u where umr.user_id = u.id and umr.time >= :sTime and umr.time < :eTime and u.upid !=0  and umr.id > 0 group by umr.user_id,u.username,u.upid,u.upids";

		Map<String, Object> params = new HashMap<>();
		params.put("sTime", sTime);
		params.put("eTime", eTime);

		List<?> results = superDao.listBySql(sql, params);
		if (CollectionUtils.isEmpty(results)) {
			return new ArrayList<>();
		}

		List<UserMainReportRichVO> reports = new ArrayList<>(results.size());
		for (Object result : results) {
			Object[] resultArr = (Object[]) result;

			int index = 0;

			int _userId = resultArr[index] == null ? 0 : ((Integer) resultArr[index++]);
			double recharge = resultArr[index] == null ? 0 : ((BigDecimal) resultArr[index++]).doubleValue();
			double receiveFeeMoney = resultArr[index] == null ? 0 : ((BigDecimal) resultArr[index++]).doubleValue();
			double withdrawals = resultArr[index] == null ? 0 : ((BigDecimal) resultArr[index++]).doubleValue();
			double transIn = resultArr[index] == null ? 0 : ((BigDecimal) resultArr[index++]).doubleValue();
			double transOut = resultArr[index] == null ? 0 : ((BigDecimal) resultArr[index++]).doubleValue();
			double accountIn = resultArr[index] == null ? 0 : ((BigDecimal) resultArr[index++]).doubleValue();
			double accountOut = resultArr[index] == null ? 0 : ((BigDecimal) resultArr[index++]).doubleValue();
			double activity = resultArr[index] == null ? 0 : ((BigDecimal) resultArr[index++]).doubleValue();
			String username = resultArr[index] == null ? null : resultArr[index++].toString();
			int upid = resultArr[index] == null ? null : ((Integer) resultArr[index++]);
			String upids = resultArr[index] == null ? null : resultArr[index++].toString();

			UserMainReportRichVO report = new UserMainReportRichVO();
			report.setUserId(_userId);
			report.setRecharge(recharge);
			report.setReceiveFeeMoney(receiveFeeMoney);
			report.setWithdrawals(withdrawals);
			report.setTransIn(transIn);
			report.setTransOut(transOut);
			report.setAccountIn(accountIn);
			report.setAccountOut(accountOut);
			report.setActivity(activity);
			report.setUsername(username);
			report.setUpid(upid);
			report.setUpids(upids);

			reports.add(report);
		}

		return reports;
	}

	@Override
	public List<UserMainReportRichVO> searchByTeam(int userId, String sTime, String eTime) {
		String sql = "select umr.user_id,sum(umr.recharge),sum(umr.receive_fee_money),sum(umr.withdrawals),sum(umr.trans_in),sum(umr.trans_out),sum(umr.account_in),sum(umr.account_out),sum(umr.activity),u.username,u.upid,u.upids from user_main_report umr,user u where umr.user_id = u.id and (u.id = :userId or u.upids like :upid ) and umr.time >= :sTime and umr.time < :eTime and umr.id > 0 group by umr.user_id,u.username,u.upid,u.upids";

		Map<String, Object> params = new HashMap<>();
		params.put("userId", userId);
		params.put("sTime", sTime);
		params.put("eTime", eTime);
		params.put("upid", "%[" + userId + "]%");

		List<?> results = superDao.listBySql(sql, params);
		if (CollectionUtils.isEmpty(results)) {
			return new ArrayList<>();
		}

		List<UserMainReportRichVO> reports = new ArrayList<>(results.size());
		for (Object result : results) {
			Object[] resultArr = (Object[]) result;

			int index = 0;

			int _userId = resultArr[index] == null ? 0 : ((Integer) resultArr[index++]);
			double recharge = resultArr[index] == null ? 0 : ((BigDecimal) resultArr[index++]).doubleValue();
			double receiveFeeMoney = resultArr[index] == null ? 0 : ((BigDecimal) resultArr[index++]).doubleValue();
			double withdrawals = resultArr[index] == null ? 0 : ((BigDecimal) resultArr[index++]).doubleValue();
			double transIn = resultArr[index] == null ? 0 : ((BigDecimal) resultArr[index++]).doubleValue();
			double transOut = resultArr[index] == null ? 0 : ((BigDecimal) resultArr[index++]).doubleValue();
			double accountIn = resultArr[index] == null ? 0 : ((BigDecimal) resultArr[index++]).doubleValue();
			double accountOut = resultArr[index] == null ? 0 : ((BigDecimal) resultArr[index++]).doubleValue();
			double activity = resultArr[index] == null ? 0 : ((BigDecimal) resultArr[index++]).doubleValue();
			String username = resultArr[index] == null ? null : resultArr[index++].toString();
			int upid = resultArr[index] == null ? null : ((Integer) resultArr[index++]);
			String upids = resultArr[index] == null ? null : resultArr[index++].toString();

			UserMainReportRichVO report = new UserMainReportRichVO();
			report.setUserId(_userId);
			report.setRecharge(recharge);
			report.setReceiveFeeMoney(receiveFeeMoney);
			report.setWithdrawals(withdrawals);
			report.setTransIn(transIn);
			report.setTransOut(transOut);
			report.setAccountIn(accountIn);
			report.setAccountOut(accountOut);
			report.setActivity(activity);
			report.setUsername(username);
			report.setUpid(upid);
			report.setUpids(upids);

			reports.add(report);
		}

		return reports;
	}

	@Override
	public List<UserMainReportRichVO> searchByTeam(int[] userIds, String sTime, String eTime) {
		StringBuffer sql = new StringBuffer("select umr.user_id,sum(umr.recharge),sum(umr.receive_fee_money),sum(umr.withdrawals),sum(umr.trans_in),sum(umr.trans_out),sum(umr.account_in),sum(umr.account_out),sum(umr.activity),u.username,u.upid,u.upids from user_main_report umr,user u where umr.user_id = u.id and (u.id in(:userIds)");

		Map<String, Object> params = new HashMap<>();
		for (int i = 0; i < userIds.length; i++) {
			sql.append(" or u.upids like :upid").append(i);
			params.put("upid" + i, "%[" + userIds[i] + "]%");
		}

		sql.append(") and umr.time >= :sTime and umr.time < :eTime and umr.id > 0 group by umr.user_id,u.username,u.upid,u.upids");

		params.put("userIds", ArrayUtils.transInIds(userIds));
		params.put("sTime", sTime);
		params.put("eTime", eTime);

		List<?> results = superDao.listBySql(sql.toString(), params);
		if (CollectionUtils.isEmpty(results)) {
			return new ArrayList<>();
		}

		List<UserMainReportRichVO> reports = new ArrayList<>(results.size());
		for (Object result : results) {
			Object[] resultArr = (Object[]) result;

			int index = 0;

			int _userId = resultArr[index] == null ? 0 : ((Integer) resultArr[index++]);
			double recharge = resultArr[index] == null ? 0 : ((BigDecimal) resultArr[index++]).doubleValue();
			double receiveFeeMoney = resultArr[index] == null ? 0 : ((BigDecimal) resultArr[index++]).doubleValue();
			double withdrawals = resultArr[index] == null ? 0 : ((BigDecimal) resultArr[index++]).doubleValue();
			double transIn = resultArr[index] == null ? 0 : ((BigDecimal) resultArr[index++]).doubleValue();
			double transOut = resultArr[index] == null ? 0 : ((BigDecimal) resultArr[index++]).doubleValue();
			double accountIn = resultArr[index] == null ? 0 : ((BigDecimal) resultArr[index++]).doubleValue();
			double accountOut = resultArr[index] == null ? 0 : ((BigDecimal) resultArr[index++]).doubleValue();
			double activity = resultArr[index] == null ? 0 : ((BigDecimal) resultArr[index++]).doubleValue();
			String username = resultArr[index] == null ? null : resultArr[index++].toString();
			int upid = resultArr[index] == null ? null : ((Integer) resultArr[index++]);
			String upids = resultArr[index] == null ? null : resultArr[index++].toString();

			UserMainReportRichVO report = new UserMainReportRichVO();
			report.setUserId(_userId);
			report.setRecharge(recharge);
			report.setReceiveFeeMoney(receiveFeeMoney);
			report.setWithdrawals(withdrawals);
			report.setTransIn(transIn);
			report.setTransOut(transOut);
			report.setAccountIn(accountIn);
			report.setAccountOut(accountOut);
			report.setActivity(activity);
			report.setUsername(username);
			report.setUpid(upid);
			report.setUpids(upids);

			reports.add(report);
		}

		return reports;
	}

	@Override
	public UserMainReportTeamStatisticsVO statisticsByTeam(int userId, String sDate, String eDate) {
		StringBuffer sql = new StringBuffer("select sum(umr.recharge),sum(case when umr.recharge > 0 then 1 else 0 end),sum(umr.withdrawals) from user_main_report umr,user u where umr.user_id = u.id and (u.id = :userId or u.upids like :upid)");

		sql.append(" and umr.time >= :sTime and umr.time < :eTime and umr.id > 0");

		Map<String, Object> params = new HashMap<>();
		params.put("userId", userId);
		params.put("upid", "%[" + userId + "]%");
		params.put("sTime", sDate);
		params.put("eTime", eDate);

		Object unique = superDao.uniqueSqlWithParams(sql.toString(), params);
		if (unique == null) {
			return new UserMainReportTeamStatisticsVO();
		}

		Object[] results = (Object[]) unique;
		int index = 0;
		double totalRecharge = results[index] == null ? 0 : ((BigDecimal) results[index]).doubleValue(); index++;
		int totalRechargePerson = results[index] == null ? 0 : Integer.valueOf(results[index].toString()); index++;
		double totalWithdraw = results[index] == null ? 0 : ((BigDecimal) results[index]).doubleValue(); index++;

		UserMainReportTeamStatisticsVO uTeamStatisticsVO = new UserMainReportTeamStatisticsVO();
		uTeamStatisticsVO.setTotalRecharge(totalRecharge);
		uTeamStatisticsVO.setTotalRechargePerson(totalRechargePerson);
		uTeamStatisticsVO.setTotalWithdraw(totalWithdraw);
		return uTeamStatisticsVO;
	}

	@Override
	public UserMainReportTeamStatisticsVO statisticsByTeam(int[] userIds, String sDate, String eDate) {
		StringBuffer sql = new StringBuffer("select sum(umr.recharge),sum(case when umr.recharge > 0 then 1 else 0 end),sum(umr.withdrawals) from user_main_report umr,user u where umr.user_id = u.id and (u.id in ("+ArrayUtils.transInIds(userIds)+") or ");

		Map<String, Object> params = new HashMap<>();
		params.put("sTime", sDate);
		params.put("eTime", eDate);

		for (int i = 0; i < userIds.length; i++) {
			sql.append("u.upids like :upid").append(i);
			params.put("upid" + i, "%["+userIds[i]+"]%");

			if (i < userIds.length - 1) {
				sql.append(" or ");
			}
		}

		sql.append(" ) and umr.time >= :sTime and umr.time < :eTime and umr.id > 0");

		Object unique = superDao.uniqueSqlWithParams(sql.toString(), params);
		if (unique == null) {
			return new UserMainReportTeamStatisticsVO();
		}

		Object[] results = (Object[]) unique;
		int index = 0;
		double totalRecharge = results[index] == null ? 0 : ((BigDecimal) results[index]).doubleValue(); index++;
		int totalRechargePerson = results[index] == null ? 0 : Integer.valueOf(results[index].toString()); index++;
		double totalWithdraw = results[index] == null ? 0 : ((BigDecimal) results[index]).doubleValue(); index++;

		UserMainReportTeamStatisticsVO uTeamStatisticsVO = new UserMainReportTeamStatisticsVO();
		uTeamStatisticsVO.setTotalRecharge(totalRecharge);
		uTeamStatisticsVO.setTotalRechargePerson(totalRechargePerson);
		uTeamStatisticsVO.setTotalWithdraw(totalWithdraw);
		return uTeamStatisticsVO;
	}

	@Override
	public UserMainReportTeamStatisticsVO statisticsAll(String sDate, String eDate) {
		StringBuffer sql = new StringBuffer("select sum(umr.recharge),sum(case when umr.recharge > 0 then 1 else 0 end),sum(umr.withdrawals) from user_main_report umr,user u where umr.user_id = u.id and umr.time >= :sTime and umr.time < :eTime and umr.id > 0 and u.upid != 0");

		Map<String, Object> params = new HashMap<>();
		params.put("sTime", sDate);
		params.put("eTime", eDate);

		Object unique = superDao.uniqueSqlWithParams(sql.toString(), params);
		if (unique == null) {
			return new UserMainReportTeamStatisticsVO();
		}

		Object[] results = (Object[]) unique;
		int resultsIndex = 0;
		double totalRecharge = results[resultsIndex] == null ? 0 : ((BigDecimal) results[resultsIndex++]).doubleValue();
		int totalRechargePerson = results[resultsIndex] == null ? 0 : Integer.valueOf(results[resultsIndex++].toString());
		double totalWithdraw = results[resultsIndex] == null ? 0 : ((BigDecimal) results[resultsIndex++]).doubleValue();

		UserMainReportTeamStatisticsVO uTeamStatisticsVO = new UserMainReportTeamStatisticsVO();
		uTeamStatisticsVO.setTotalRecharge(totalRecharge);
		uTeamStatisticsVO.setTotalRechargePerson(totalRechargePerson);
		uTeamStatisticsVO.setTotalWithdraw(totalWithdraw);
		return uTeamStatisticsVO;
	}
}
