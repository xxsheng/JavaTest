package lottery.domains.content.dao.read.impl;

import javautils.array.ArrayUtils;
import javautils.jdbc.hibernate.HibernateSuperReadDao;
import lottery.domains.content.dao.read.UserLotteryReportReadDao;
import lottery.domains.content.entity.UserLotteryReport;
import lottery.domains.content.vo.bill.UserLotteryReportRichVO;
import lottery.domains.content.vo.user.UserLotteryReportTeamStatisticsVO;
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
public class UserLotteryReportReadDaoImpl implements UserLotteryReportReadDao {
	
	private final String tab = UserLotteryReport.class.getSimpleName();
	
	@Autowired
	private HibernateSuperReadDao<UserLotteryReport> superDao;

	@Override
	public List<?> listAmount(int[] ids, String sTime, String eTime) {
		String hql = "select time, sum(billingOrder) from " + tab + " where userId in(" + ArrayUtils.transInIds(ids) + ") and time >= ?0 and time < ?1 and id > 0 group by time order by time asc";
		Object[] values = {sTime, eTime};
		return superDao.listObject(hql, values);
	}

	@Override
	public List<?> listAmountGroupByUserIds(Integer[] ids, String sTime, String eTime) {
		String hql = "select userId, sum(billingOrder) from " + tab + " where userId in(" + ArrayUtils.transInIds(ids) + ") and time >= ?0 and time < ?1 and id > 0 group by userId";
		Object[] values = {sTime, eTime};
		return superDao.listObject(hql, values);
	}

	@Override
	public List<UserLotteryReport> find(List<Criterion> criterions,
			List<Order> orders) {
		return superDao.findByCriteria(UserLotteryReport.class, criterions, orders);
	}

	@Override
	public List<UserLotteryReport> getDayListAll(String sDate, String eDate) {
		StringBuffer sql = new StringBuffer("select ulr.time,sum(ulr.trans_in),sum(ulr.trans_out),sum(ulr.prize),sum(ulr.spend_return),sum(ulr.proxy_return),sum(ulr.activity),sum(ulr.billing_order),sum(ulr.recharge_fee) from user_lottery_report ulr where ulr.time >= :sTime and ulr.time < :eTime and ulr.id > 0 group by ulr.time");

		Map<String, Object> params = new HashMap<>();
		params.put("sTime", sDate);
		params.put("eTime", eDate);

		List<?> results = superDao.listBySql(sql.toString(), params);
		if (CollectionUtils.isEmpty(results)) {
			return new ArrayList<>();
		}

		List<UserLotteryReport> reports = new ArrayList<>(results.size());
		for (Object result : results) {
			Object[] resultArr = (Object[]) result;

			int index = 0;

			String time = resultArr[index] == null ? null : resultArr[index].toString();index++;
			double transIn = resultArr[index] == null ? 0 : ((BigDecimal) resultArr[index]).doubleValue();index++;
			double transOut = resultArr[index] == null ? 0 : ((BigDecimal) resultArr[index]).doubleValue();index++;
			double prize = resultArr[index] == null ? 0 : ((BigDecimal) resultArr[index]).doubleValue();index++;
			double spendReturn = resultArr[index] == null ? 0 : ((BigDecimal) resultArr[index]).doubleValue();index++;
			double proxyReturn = resultArr[index] == null ? 0 : ((BigDecimal) resultArr[index]).doubleValue();index++;
			double activity = resultArr[index] == null ? 0 : ((BigDecimal) resultArr[index]).doubleValue();index++;
			double billingOrder = resultArr[index] == null ? 0 : ((BigDecimal) resultArr[index]).doubleValue();index++;
			double rechargeFee = resultArr[index] == null ? 0 : ((BigDecimal) resultArr[index]).doubleValue();index++;

			UserLotteryReport report = new UserLotteryReport();
			report.setTime(time);
			report.setTransIn(transIn);
			report.setTransOut(transOut);
			report.setPrize(prize);
			report.setSpendReturn(spendReturn);
			report.setProxyReturn(proxyReturn);
			report.setActivity(activity);
			report.setBillingOrder(billingOrder);
			report.setRechargeFee(rechargeFee);

			reports.add(report);
		}

		return reports;
	}

	@Override
	public List<UserLotteryReport> getDayListByTeam(int userId, String sDate, String eDate) {
		return getDayListByTeam(new int[]{userId}, sDate, eDate);
	}

	@Override
	public List<UserLotteryReport> getDayListByTeam(int[] userIds, String sDate, String eDate) {
		StringBuffer sql = new StringBuffer("select ulr.time,sum(ulr.trans_in),sum(ulr.trans_out),sum(ulr.prize),sum(ulr.spend_return),sum(ulr.proxy_return),sum(ulr.activity),sum(ulr.billing_order),sum(ulr.recharge_fee) from user_lottery_report ulr,user u where ulr.user_id = u.id and (u.id in(:userIds)");

		Map<String, Object> params = new HashMap<>();
		for (int i = 0; i < userIds.length; i++) {
			sql.append(" or u.upids like :upid").append(i);
			params.put("upid" + i, "%[" + userIds[i] + "]%");
		}

		sql.append(") and ulr.time >= :sTime and ulr.time < :eTime and ulr.id > 0 group by ulr.time");

		params.put("userIds", ArrayUtils.transInIds(userIds));
		params.put("sTime", sDate);
		params.put("eTime", eDate);

		List<?> results = superDao.listBySql(sql.toString(), params);
		if (CollectionUtils.isEmpty(results)) {
			return new ArrayList<>();
		}

		List<UserLotteryReport> reports = new ArrayList<>(results.size());
		for (Object result : results) {
			Object[] resultArr = (Object[]) result;

			int index = 0;

			String time = resultArr[index] == null ? null : resultArr[index].toString();index++;
			double transIn = resultArr[index] == null ? 0 : ((BigDecimal) resultArr[index]).doubleValue();index++;
			double transOut = resultArr[index] == null ? 0 : ((BigDecimal) resultArr[index]).doubleValue();index++;
			double prize = resultArr[index] == null ? 0 : ((BigDecimal) resultArr[index]).doubleValue();index++;
			double spendReturn = resultArr[index] == null ? 0 : ((BigDecimal) resultArr[index]).doubleValue();index++;
			double proxyReturn = resultArr[index] == null ? 0 : ((BigDecimal) resultArr[index]).doubleValue();index++;
			double activity = resultArr[index] == null ? 0 : ((BigDecimal) resultArr[index]).doubleValue();index++;
			double billingOrder = resultArr[index] == null ? 0 : ((BigDecimal) resultArr[index]).doubleValue();index++;
			double rechargeFee = resultArr[index] == null ? 0 : ((BigDecimal) resultArr[index]).doubleValue();index++;

			UserLotteryReport report = new UserLotteryReport();
			report.setTime(time);
			report.setTransIn(transIn);
			report.setTransOut(transOut);
			report.setPrize(prize);
			report.setSpendReturn(spendReturn);
			report.setProxyReturn(proxyReturn);
			report.setActivity(activity);
			report.setBillingOrder(billingOrder);
			report.setRechargeFee(rechargeFee);

			reports.add(report);
		}

		return reports;
	}

	@Override
	public List<UserLotteryReportRichVO> searchAll(String sTime, String eTime) {
		String sql = "select ulr.user_id,sum(ulr.trans_in),sum(ulr.trans_out),sum(ulr.prize),sum(ulr.spend_return),sum(ulr.proxy_return),sum(ulr.activity),sum(ulr.billing_order),sum(ulr.recharge_fee),u.username,u.upid,u.upids from user_lottery_report ulr,user u where ulr.user_id = u.id and u.upid != 0 and ulr.time >= :sTime and ulr.time < :eTime and ulr.id > 0 group by ulr.user_id,u.username,u.upid,u.upids";

		Map<String, Object> params = new HashMap<>();
		params.put("sTime", sTime);
		params.put("eTime", eTime);

		List<?> results = superDao.listBySql(sql, params);
		if (CollectionUtils.isEmpty(results)) {
			return new ArrayList<>();
		}

		List<UserLotteryReportRichVO> reports = new ArrayList<>(results.size());
		for (Object result : results) {
			Object[] resultArr = (Object[]) result;

			int index = 0;

			int _userId = resultArr[index] == null ? 0 : ((Integer) resultArr[index]);index++;
			double transIn = resultArr[index] == null ? 0 : ((BigDecimal) resultArr[index]).doubleValue();index++;
			double transOut = resultArr[index] == null ? 0 : ((BigDecimal) resultArr[index]).doubleValue();index++;
			double prize = resultArr[index] == null ? 0 : ((BigDecimal) resultArr[index]).doubleValue();index++;
			double spendReturn = resultArr[index] == null ? 0 : ((BigDecimal) resultArr[index]).doubleValue();index++;
			double proxyReturn = resultArr[index] == null ? 0 : ((BigDecimal) resultArr[index]).doubleValue();index++;
			double activity = resultArr[index] == null ? 0 : ((BigDecimal) resultArr[index]).doubleValue();index++;
			double billingOrder = resultArr[index] == null ? 0 : ((BigDecimal) resultArr[index]).doubleValue();index++;
			double rechargeFee = resultArr[index] == null ? 0 : ((BigDecimal) resultArr[index]).doubleValue();index++;
			String username = resultArr[index] == null ? null : resultArr[index].toString();index++;
			int upid = resultArr[index] == null ? null : ((Integer) resultArr[index]);index++;
			String upids = resultArr[index] == null ? null : resultArr[index].toString();index++;

			UserLotteryReportRichVO report = new UserLotteryReportRichVO();
			report.setUserId(_userId);
			report.setTransIn(transIn);
			report.setTransOut(transOut);
			report.setPrize(prize);
			report.setSpendReturn(spendReturn);
			report.setProxyReturn(proxyReturn);
			report.setActivity(activity);
			report.setBillingOrder(billingOrder);
			report.setRechargeFee(rechargeFee);
			report.setUsername(username);
			report.setUpid(upid);
			report.setUpids(upids);

			reports.add(report);
		}

		return reports;
	}

	@Override
	public List<UserLotteryReportRichVO> searchByTeam(int userId, String sTime, String eTime) {
		String sql = "select ulr.user_id,sum(ulr.trans_in),sum(ulr.trans_out),sum(ulr.prize),sum(ulr.spend_return),sum(ulr.proxy_return),sum(ulr.activity),sum(ulr.billing_order),sum(ulr.recharge_fee),u.username,u.upid,u.upids from user_lottery_report ulr,user u where ulr.user_id = u.id and (u.id = :userId or u.upids like :upid ) and ulr.time >= :sTime and ulr.time < :eTime and ulr.id > 0 group by ulr.user_id,u.username,u.upid,u.upids";

		Map<String, Object> params = new HashMap<>();
		params.put("userId", userId);
		params.put("sTime", sTime);
		params.put("eTime", eTime);
		params.put("upid", "%[" + userId + "]%");

		List<?> results = superDao.listBySql(sql, params);
		if (CollectionUtils.isEmpty(results)) {
			return new ArrayList<>();
		}

		List<UserLotteryReportRichVO> reports = new ArrayList<>(results.size());
		for (Object result : results) {
			Object[] resultArr = (Object[]) result;

			int index = 0;

			int _userId = resultArr[index] == null ? 0 : ((Integer) resultArr[index]);index++;
			double transIn = resultArr[index] == null ? 0 : ((BigDecimal) resultArr[index]).doubleValue();index++;
			double transOut = resultArr[index] == null ? 0 : ((BigDecimal) resultArr[index]).doubleValue();index++;
			double prize = resultArr[index] == null ? 0 : ((BigDecimal) resultArr[index]).doubleValue();index++;
			double spendReturn = resultArr[index] == null ? 0 : ((BigDecimal) resultArr[index]).doubleValue();index++;
			double proxyReturn = resultArr[index] == null ? 0 : ((BigDecimal) resultArr[index]).doubleValue();index++;
			double activity = resultArr[index] == null ? 0 : ((BigDecimal) resultArr[index]).doubleValue();index++;
			double billingOrder = resultArr[index] == null ? 0 : ((BigDecimal) resultArr[index]).doubleValue();index++;
			double rechargeFee = resultArr[index] == null ? 0 : ((BigDecimal) resultArr[index]).doubleValue();index++;
			String username = resultArr[index] == null ? null : resultArr[index].toString();index++;
			int upid = resultArr[index] == null ? null : ((Integer) resultArr[index]);index++;
			String upids = resultArr[index] == null ? null : resultArr[index].toString();index++;

			UserLotteryReportRichVO report = new UserLotteryReportRichVO();
			report.setUserId(_userId);
			report.setTransIn(transIn);
			report.setTransOut(transOut);
			report.setPrize(prize);
			report.setSpendReturn(spendReturn);
			report.setProxyReturn(proxyReturn);
			report.setActivity(activity);
			report.setBillingOrder(billingOrder);
			report.setRechargeFee(rechargeFee);
			report.setUsername(username);
			report.setUpid(upid);
			report.setUpids(upids);

			reports.add(report);
		}

		return reports;
	}

	@Override
	public List<UserLotteryReportRichVO> searchByTeam(int[] userIds, String sTime, String eTime) {

		StringBuffer sql = new StringBuffer("select ulr.user_id,sum(ulr.trans_in),sum(ulr.trans_out),sum(ulr.prize),sum(ulr.spend_return),sum(ulr.proxy_return),sum(ulr.activity),sum(ulr.billing_order),sum(ulr.recharge_fee),u.username,u.upid,u.upids from user_lottery_report ulr,user u where ulr.user_id = u.id and (u.id in(:userIds)");

		Map<String, Object> params = new HashMap<>();
		for (int i = 0; i < userIds.length; i++) {
			sql.append(" or u.upids like :upid").append(i);
			params.put("upid" + i, "%[" + userIds[i] + "]%");
		}

		sql.append(") and ulr.time >= :sTime and ulr.time < :eTime and ulr.id > 0 group by ulr.user_id,u.username,u.upid,u.upids");

		params.put("userIds", ArrayUtils.transInIds(userIds));
		params.put("sTime", sTime);
		params.put("eTime", eTime);

		List<?> results = superDao.listBySql(sql.toString(), params);
		if (CollectionUtils.isEmpty(results)) {
			return new ArrayList<>();
		}

		List<UserLotteryReportRichVO> reports = new ArrayList<>(results.size());
		for (Object result : results) {
			Object[] resultArr = (Object[]) result;

			int index = 0;

			int _userId = resultArr[index] == null ? 0 : ((Integer) resultArr[index]);index++;
			double transIn = resultArr[index] == null ? 0 : ((BigDecimal) resultArr[index]).doubleValue();index++;
			double transOut = resultArr[index] == null ? 0 : ((BigDecimal) resultArr[index]).doubleValue();index++;
			double prize = resultArr[index] == null ? 0 : ((BigDecimal) resultArr[index]).doubleValue();index++;
			double spendReturn = resultArr[index] == null ? 0 : ((BigDecimal) resultArr[index]).doubleValue();index++;
			double proxyReturn = resultArr[index] == null ? 0 : ((BigDecimal) resultArr[index]).doubleValue();index++;
			double activity = resultArr[index] == null ? 0 : ((BigDecimal) resultArr[index]).doubleValue();index++;
			double billingOrder = resultArr[index] == null ? 0 : ((BigDecimal) resultArr[index]).doubleValue();index++;
			double rechargeFee = resultArr[index] == null ? 0 : ((BigDecimal) resultArr[index]).doubleValue();index++;
			String username = resultArr[index] == null ? null : resultArr[index].toString();index++;
			int upid = resultArr[index] == null ? null : ((Integer) resultArr[index]);index++;
			String upids = resultArr[index] == null ? null : resultArr[index].toString();index++;

			UserLotteryReportRichVO report = new UserLotteryReportRichVO();
			report.setUserId(_userId);
			report.setTransIn(transIn);
			report.setTransOut(transOut);
			report.setPrize(prize);
			report.setSpendReturn(spendReturn);
			report.setProxyReturn(proxyReturn);
			report.setActivity(activity);
			report.setBillingOrder(billingOrder);
			report.setRechargeFee(rechargeFee);
			report.setUsername(username);
			report.setUpid(upid);
			report.setUpids(upids);

			reports.add(report);
		}

		return reports;
	}

	@Override
	public UserLotteryReportTeamStatisticsVO statisticsByTeam(int userId, String sDate, String eDate) {
		StringBuffer sql = new StringBuffer("select sum(ulr.prize),sum(ulr.spend_return),sum(ulr.proxy_return),sum(ulr.activity),sum(ulr.billing_order),sum(ulr.cancel_order),sum(ulr.recharge_fee),count(distinct user_id) from user_lottery_report ulr,user u where ulr.user_id = u.id  and (u.id = :userId or u.upids like :upid)");

		sql.append(" and ulr.time >= :sTime and ulr.time < :eTime and ulr.id > 0");

		Map<String, Object> params = new HashMap<>();
		params.put("userId", userId);
		params.put("upid", "%[" + userId + "]%");
		params.put("sTime", sDate);
		params.put("eTime", eDate);

		Object unique = superDao.uniqueSqlWithParams(sql.toString(), params);
		if (unique == null) {
			return new UserLotteryReportTeamStatisticsVO();
		}

		Object[] resultArr = (Object[]) unique;
		int index = 0;
		double totalLotteryPrize = resultArr[index] == null ? 0 : ((BigDecimal) resultArr[index]).doubleValue(); index++;
		double totalLotterySpendReturn = resultArr[index] == null ? 0 : ((BigDecimal) resultArr[index]).doubleValue(); index++;
		double totalLotteryProxyReturn = resultArr[index] == null ? 0 : ((BigDecimal) resultArr[index]).doubleValue(); index++;
		double totalLotteryActivity = resultArr[index] == null ? 0 : ((BigDecimal) resultArr[index]).doubleValue(); index++;
		double totalLotteryBillingOrder = resultArr[index] == null ? 0 : ((BigDecimal) resultArr[index]).doubleValue(); index++;
		double totalLotteryCancelOrder = resultArr[index] == null ? 0 : ((BigDecimal) resultArr[index]).doubleValue(); index++;
		double totalRechargeFee = resultArr[index] == null ? 0 : ((BigDecimal) resultArr[index]).doubleValue(); index++;
		double totalLotteryProfit = totalLotteryPrize + totalLotterySpendReturn + totalLotteryProxyReturn + totalLotteryActivity + totalRechargeFee - totalLotteryBillingOrder;

		UserLotteryReportTeamStatisticsVO uTeamStatisticsVO = new UserLotteryReportTeamStatisticsVO();
		uTeamStatisticsVO.setTotalLotteryPrize(totalLotteryPrize);
		uTeamStatisticsVO.setTotalLotterySpendReturn(totalLotterySpendReturn);
		uTeamStatisticsVO.setTotalLotteryProxyReturn(totalLotteryProxyReturn);
		uTeamStatisticsVO.setTotalLotteryActivity(totalLotteryActivity);
		uTeamStatisticsVO.setTotalLotteryBillingOrder(totalLotteryBillingOrder);
		uTeamStatisticsVO.setTotalLotteryCancelOrder(totalLotteryCancelOrder);
		uTeamStatisticsVO.setTotalRechargeFee(totalRechargeFee);
		uTeamStatisticsVO.setTotalLotteryProfit(totalLotteryProfit);
		return uTeamStatisticsVO;
	}

	@Override
	public UserLotteryReportTeamStatisticsVO statisticsByTeam(int[] userIds, String sDate, String eDate) {
		StringBuffer sql = new StringBuffer("select sum(ulr.prize),sum(ulr.spend_return),sum(ulr.proxy_return),sum(ulr.activity),sum(ulr.billing_order),sum(ulr.cancel_order),sum(ulr.recharge_fee),count(distinct user_id) from user_lottery_report ulr,user u where ulr.user_id = u.id and (u.id in ("+ArrayUtils.transInIds(userIds)+") or ");

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

		sql.append(" ) and ulr.time >= :sTime and ulr.time < :eTime and ulr.id > 0");

		Object unique = superDao.uniqueSqlWithParams(sql.toString(), params);
		if (unique == null) {
			return new UserLotteryReportTeamStatisticsVO();
		}

		Object[] resultArr = (Object[]) unique;
		int index = 0;
		double totalLotteryPrize = resultArr[index] == null ? 0 : ((BigDecimal) resultArr[index]).doubleValue(); index++;
		double totalLotterySpendReturn = resultArr[index] == null ? 0 : ((BigDecimal) resultArr[index]).doubleValue(); index++;
		double totalLotteryProxyReturn = resultArr[index] == null ? 0 : ((BigDecimal) resultArr[index]).doubleValue(); index++;
		double totalLotteryActivity = resultArr[index] == null ? 0 : ((BigDecimal) resultArr[index]).doubleValue(); index++;
		double totalLotteryBillingOrder = resultArr[index] == null ? 0 : ((BigDecimal) resultArr[index]).doubleValue(); index++;
		double totalLotteryCancelOrder = resultArr[index] == null ? 0 : ((BigDecimal) resultArr[index]).doubleValue(); index++;
		double totalRechargeFee = resultArr[index] == null ? 0 : ((BigDecimal) resultArr[index]).doubleValue(); index++;
		double totalLotteryProfit = totalLotteryPrize + totalLotterySpendReturn + totalLotteryProxyReturn + totalLotteryActivity + totalRechargeFee - totalLotteryBillingOrder;

		UserLotteryReportTeamStatisticsVO uTeamStatisticsVO = new UserLotteryReportTeamStatisticsVO();
		uTeamStatisticsVO.setTotalLotteryPrize(totalLotteryPrize);
		uTeamStatisticsVO.setTotalLotterySpendReturn(totalLotterySpendReturn);
		uTeamStatisticsVO.setTotalLotteryProxyReturn(totalLotteryProxyReturn);
		uTeamStatisticsVO.setTotalLotteryActivity(totalLotteryActivity);
		uTeamStatisticsVO.setTotalLotteryBillingOrder(totalLotteryBillingOrder);
		uTeamStatisticsVO.setTotalLotteryCancelOrder(totalLotteryCancelOrder);
		uTeamStatisticsVO.setTotalRechargeFee(totalRechargeFee);
		uTeamStatisticsVO.setTotalLotteryProfit(totalLotteryProfit);
		return uTeamStatisticsVO;
	}

	@Override
	public UserLotteryReportTeamStatisticsVO statisticsAll(String sDate, String eDate) {
		StringBuffer sql = new StringBuffer("select sum(ulr.prize),sum(ulr.spend_return),sum(ulr.proxy_return),sum(ulr.activity),sum(ulr.billing_order),sum(ulr.cancel_order),sum(ulr.recharge_fee),count(distinct user_id) from user_lottery_report ulr,user u where ulr.user_id = u.id and ulr.time >= :sTime and ulr.time < :eTime and ulr.id > 0 and u.upid != 0");

		Map<String, Object> params = new HashMap<>();
		params.put("sTime", sDate);
		params.put("eTime", eDate);

		Object unique = superDao.uniqueSqlWithParams(sql.toString(), params);
		if (unique == null) {
			return new UserLotteryReportTeamStatisticsVO();
		}

		Object[] resultArr = (Object[]) unique;
		int index = 0;
		double totalLotteryPrize = resultArr[index] == null ? 0 : ((BigDecimal) resultArr[index]).doubleValue();index++;
		double totalLotterySpendReturn = resultArr[index] == null ? 0 : ((BigDecimal) resultArr[index]).doubleValue();index++;
		double totalLotteryProxyReturn = resultArr[index] == null ? 0 : ((BigDecimal) resultArr[index]).doubleValue();index++;
		double totalLotteryActivity = resultArr[index] == null ? 0 : ((BigDecimal) resultArr[index]).doubleValue();index++;
		double totalLotteryBillingOrder = resultArr[index] == null ? 0 : ((BigDecimal) resultArr[index]).doubleValue();index++;
		double totalLotteryCancelOrder = resultArr[index] == null ? 0 : ((BigDecimal) resultArr[index]).doubleValue();index++;
		double totalRechargeFee = resultArr[index] == null ? 0 : ((BigDecimal) resultArr[index]).doubleValue();index++;
		double totalLotteryProfit = totalLotteryPrize + totalLotterySpendReturn + totalLotteryProxyReturn + totalLotteryActivity + totalRechargeFee - totalLotteryBillingOrder;

		UserLotteryReportTeamStatisticsVO uTeamStatisticsVO = new UserLotteryReportTeamStatisticsVO();
		uTeamStatisticsVO.setTotalLotteryPrize(totalLotteryPrize);
		uTeamStatisticsVO.setTotalLotterySpendReturn(totalLotterySpendReturn);
		uTeamStatisticsVO.setTotalLotteryProxyReturn(totalLotteryProxyReturn);
		uTeamStatisticsVO.setTotalLotteryActivity(totalLotteryActivity);
		uTeamStatisticsVO.setTotalLotteryBillingOrder(totalLotteryBillingOrder);
		uTeamStatisticsVO.setTotalLotteryCancelOrder(totalLotteryCancelOrder);
		uTeamStatisticsVO.setTotalRechargeFee(totalRechargeFee);
		uTeamStatisticsVO.setTotalLotteryProfit(totalLotteryProfit);
		//uTeamStatisticsVO.setTotalUserCount(totalUserCount);
		return uTeamStatisticsVO;
	}
}
