package lottery.domains.content.dao.read.impl;

import javautils.array.ArrayUtils;
import javautils.jdbc.PageList;
import javautils.jdbc.hibernate.HibernateSuperReadDao;
import lottery.domains.content.dao.read.UserDividendBillReadDao;
import lottery.domains.content.entity.UserDividendBill;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
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
public class UserDividendBillReadDaoImpl implements UserDividendBillReadDao {
	private final String tab = UserDividendBill.class.getSimpleName();
	private static final String SQL_FIELDS = "udb.id, udb.user_id, udb.indicate_start_date, udb.indicate_end_date, udb.min_valid_user, udb.valid_user, udb.scale, udb.billing_order, udb.this_loss, udb.last_loss, udb.total_loss, udb.available_amount, udb.total_received, udb.lower_total_amount, udb.lower_paid_amount, udb.user_amount, udb.issue_type, udb.settle_time, udb.collect_time, udb.`status`, udb.remarks";

	@Autowired
	private HibernateSuperReadDao<UserDividendBill> superDao;

	@Override
	public PageList search(List<Criterion> criterions, List<Order> orders, int start, int limit) {
		String propertyName = "id";
		return superDao.findPageList(UserDividendBill.class, propertyName, criterions, orders, start, limit);
	}

	@Override
	public PageList searchByTeam(int[] userIds, String sTime, String eTime, Integer status, int start, int limit) {
		StringBuffer countSql = new StringBuffer("select count(udb.id) from user_dividend_bill udb,user u where udb.user_id = u.id and (u.id in ("+ ArrayUtils.transInIds(userIds)+") or ");
		StringBuffer querySql = new StringBuffer("select ").append(SQL_FIELDS).append(" from user_dividend_bill udb,user u where udb.user_id = u.id and (u.id in("+ArrayUtils.transInIds(userIds)+") or ");

		Map<String, Object> params = new HashMap<>();

		for (int i = 0; i < userIds.length; i++) {
			countSql.append("u.upids like :upid").append(i);
			querySql.append("u.upids like :upid").append(i);
			params.put("upid" + i, "%["+userIds[i]+"]%");

			if (i < userIds.length - 1) {
				countSql.append(" or ");
				querySql.append(" or ");
			}
		}

		if (StringUtils.isNotEmpty(sTime)) {
			countSql.append(" and udb.indicate_start_date>=:sTime");
			querySql.append(" and udb.indicate_start_date>=:sTime");
			params.put("sTime", sTime);
		}
		if (StringUtils.isNotEmpty(eTime)) {
			countSql.append(" and udb.indicate_start_date<=:eTime");
			querySql.append(" and udb.indicate_start_date<=:eTime");
			params.put("eTime", eTime);
		}
		if (status != null) {
			countSql.append(" and udb.status = :status");
			querySql.append(" and udb.status = :status");
			params.put("status", status);
		}

		countSql.append(" and udb.status <> 2");
		querySql.append(" and udb.status <> 2");

		countSql.append(")");
		querySql.append(") order by udb.settle_time desc,udb.id desc");

		PageList pageList = superDao.findPageList(countSql.toString(), querySql.toString(), params, start, limit);

		if (pageList == null) {
			return new PageList();
		}
		List<?> list = pageList.getList();
		List<UserDividendBill> results = convertSqlResults(list);
		pageList.setList(results);
		return pageList;
	}

	@Override
	public PageList searchByTeam(int userId, String sTime, String eTime, Integer status, int start, int limit) {
		StringBuffer countSql = new StringBuffer("select count(udb.id) from user_dividend_bill udb, user u where udb.user_id = u.id and (u.id = :userId or u.upids like :upid)");
		StringBuffer querySql = new StringBuffer("select ").append(SQL_FIELDS).append(" from user_dividend_bill udb, user u where udb.user_id = u.id and (u.id = :userId or u.upids like :upid)");

		Map<String, Object> params = new HashMap<>();
		params.put("userId", userId);
		params.put("upid", "%["+userId+"]%");

		if (StringUtils.isNotEmpty(sTime)) {
			countSql.append(" and udb.indicate_start_date>=:sTime");
			querySql.append(" and udb.indicate_start_date>=:sTime");
			params.put("sTime", sTime);
		}
		if (StringUtils.isNotEmpty(eTime)) {
			countSql.append(" and udb.indicate_start_date<=:eTime");
			querySql.append(" and udb.indicate_start_date<=:eTime");
			params.put("eTime", eTime);
		}
		if (status != null) {
			countSql.append(" and udb.status = :status");
			querySql.append(" and udb.status = :status");
			params.put("status", status);
		}

		countSql.append(" and udb.status <> 2");
		querySql.append(" and udb.status <> 2");

		querySql.append(" order by udb.settle_time desc,udb.id desc");

		PageList pageList = superDao.findPageList(countSql.toString(), querySql.toString(), params, start, limit);

		if (pageList == null) {
			return new PageList();
		}
		List<?> list = pageList.getList();
		List<UserDividendBill> results = convertSqlResults(list);
		pageList.setList(results);
		return pageList;
	}

	@Override
	public PageList searchByUserId(int userId, String sTime, String eTime, Integer status, int start, int limit) {
		StringBuffer countSql = new StringBuffer("select count(udb.id) from user_dividend_bill udb where udb.user_id = :userId");
		StringBuffer querySql = new StringBuffer("select ").append(SQL_FIELDS).append(" from user_dividend_bill udb where udb.user_id = :userId");

		Map<String, Object> params = new HashMap<>();
		params.put("userId", userId);

		if (StringUtils.isNotEmpty(sTime)) {
			countSql.append(" and udb.indicate_start_date>=:sTime");
			querySql.append(" and udb.indicate_start_date>=:sTime");
			params.put("sTime", sTime);
		}
		if (StringUtils.isNotEmpty(eTime)) {
			countSql.append(" and udb.indicate_start_date<=:eTime");
			querySql.append(" and udb.indicate_start_date<=:eTime");
			params.put("eTime", eTime);
		}
		if (status != null) {
			countSql.append(" and udb.status = :status");
			querySql.append(" and udb.status = :status");
			params.put("status", status);
		}
		countSql.append(" and udb.status <> 2");
		querySql.append(" and udb.status <> 2");

		querySql.append(" order by udb.settle_time desc,udb.id desc");

		PageList pageList = superDao.findPageList(countSql.toString(), querySql.toString(), params, start, limit);

		if (pageList == null) {
			return new PageList();
		}
		List<?> list = pageList.getList();
		List<UserDividendBill> results = convertSqlResults(list);
		pageList.setList(results);
		return pageList;
	}

	private List<UserDividendBill> convertSqlResults(List<?> list) {
		if (CollectionUtils.isEmpty(list)) {
			return new ArrayList<>();
		}
		List<UserDividendBill> results = new ArrayList<>(list.size());
		for (Object o : list) {
			Object[] objs = (Object[]) o;

			int index = 0;
			int _id = objs[index] != null ? Integer.valueOf(objs[index].toString()) : 0; index++;
			int _userId = objs[index] != null ? Integer.valueOf(objs[index].toString()) : 0; index++;
			String _indicateStartDate = objs[index] != null ? objs[index].toString() : ""; index++;
			String _indicateEndDate = objs[index] != null ? objs[index].toString() : ""; index++;
			int _minValidUser = objs[index] != null ? Integer.valueOf(objs[index].toString()) : 0; index++;
			int _validUser = objs[index] != null ? Integer.valueOf(objs[index].toString()) : 0; index++;
			double _scale = objs[index] != null ? ((BigDecimal) objs[index]).doubleValue() : 0; index++;
			double _billingOrder = objs[index] != null ? ((BigDecimal) objs[index]).doubleValue() : 0; index++;
			double _thisLoss = objs[index] != null ? ((BigDecimal) objs[index]).doubleValue() : 0; index++;
			double _lastLoss = objs[index] != null ? ((BigDecimal) objs[index]).doubleValue() : 0; index++;
			double _totalLoss = objs[index] != null ? ((BigDecimal) objs[index]).doubleValue() : 0; index++;
			double _availableAmount = objs[index] != null ? ((BigDecimal) objs[index]).doubleValue() : 0; index++;
			double _totalReceived = objs[index] != null ? ((BigDecimal) objs[index]).doubleValue() : 0; index++;
			double _lowerTotalAmount = objs[index] != null ? ((BigDecimal) objs[index]).doubleValue() : 0; index++;
			double _lowerPaidAmount = objs[index] != null ? ((BigDecimal) objs[index]).doubleValue() : 0; index++;
			double _userAmount = objs[index] != null ? ((BigDecimal) objs[index]).doubleValue() : 0; index++;
			int _issueType = objs[index] != null ? Integer.valueOf(objs[index].toString()) : 0; index++;
			String _settleTime = objs[index] != null ? objs[index].toString() : ""; index++;
			String _collectTime = objs[index] != null ? objs[index].toString() : ""; index++;
			int _status = objs[index] != null ? Integer.valueOf(objs[index].toString()) : 0; index++;
			String _remarks = objs[index] != null ? objs[index].toString() : ""; index++;

			UserDividendBill result = new UserDividendBill();
			result.setId(_id);
			result.setUserId(_userId);
			result.setIndicateStartDate(_indicateStartDate);
			result.setIndicateEndDate(_indicateEndDate);
			result.setMinValidUser(_minValidUser);
			result.setValidUser(_validUser);
			result.setScale(_scale);
			result.setBillingOrder(_billingOrder);
			result.setThisLoss(_thisLoss);
			result.setLastLoss(_lastLoss);
			result.setTotalLoss(_totalLoss);
			result.setAvailableAmount(_availableAmount);
			result.setTotalReceived(_totalReceived);
			result.setLowerTotalAmount(_lowerTotalAmount);
			result.setLowerPaidAmount(_lowerPaidAmount);
			result.setUserAmount(_userAmount);
			result.setIssueType(_issueType);
			result.setSettleTime(_settleTime);
			result.setCollectTime(_collectTime);
			result.setStatus(_status);
			result.setRemarks(_remarks);
			results.add(result);
		}
		return results;
	}

	// @Override
	// public PageList searchByZhuGuans(List<Integer> userIds, String sTime, String eTime, int start, int limit) {
	// 	String countHql = "select count(udb.id) from " + tab + " udb,User u where udb.userId = u.id and (udb.userId in ("+ArrayUtils.transInIds(userIds)+") or u.upid in ("+ArrayUtils.transInIds(userIds)+")) and u.id > 0 and udb.id > 0";
	// 	String queryHql = "select udb from " + tab + " udb,User u where udb.userId = u.id and (udb.userId in ("+ArrayUtils.transInIds(userIds)+") or u.upid in ("+ArrayUtils.transInIds(userIds)+")) and u.id > 0 and udb.id > 0";
    //
	// 	List<Object> params = new LinkedList<>();
	// 	int paramIndex = 0;
	// 	if (StringUtils.isNotEmpty(sTime)) {
	// 		countHql += " and udb.indicateStartDate>=?" + paramIndex;
	// 		queryHql += " and udb.indicateStartDate>=?" + paramIndex++;
	// 		params.add(sTime);
	// 	}
	// 	if (StringUtils.isNotEmpty(eTime)) {
	// 		countHql += " and udb.indicateEndDate<=?" + paramIndex;
	// 		queryHql += " and udb.indicateEndDate<=?" + paramIndex++;
	// 		params.add(eTime);
	// 	}
    //
	// 	queryHql += " order by udb.settleTime desc,udb.id desc";
    //
	// 	Object[] values = params.toArray();
	// 	return superDao.findPageList(queryHql, countHql, values, start, limit);
	// }
    //
	// @Override
	// public PageList searchByDirectLowers(int upUserId, String sTime, String eTime, int start, int limit) {
	// 	String countHql = "select count(udb.id) from " + tab + " udb,User u where udb.userId = u.id and (udb.userId = ?0 or u.upid = ?1) and u.id > 0 and udb.id > 0";
	// 	String queryHql = "select udb from " + tab + " udb,User u where udb.userId = u.id and (udb.userId = ?0 or u.upid = ?1) and u.id > 0 and udb.id > 0";
    //
	// 	List<Object> params = new LinkedList<>();
	// 	params.add(upUserId);
	// 	params.add(upUserId);
    //
	// 	int paramIndex = 2;
	// 	if (StringUtils.isNotEmpty(sTime)) {
	// 		countHql += " and udb.indicateStartDate>=?" + paramIndex;
	// 		queryHql += " and udb.indicateStartDate>=?" + paramIndex++;
	// 		params.add(sTime);
	// 	}
	// 	if (StringUtils.isNotEmpty(eTime)) {
	// 		countHql += " and udb.indicateEndDate<=?" + paramIndex;
	// 		queryHql += " and udb.indicateEndDate<=?" + paramIndex++;
	// 		params.add(eTime);
	// 	}
    //
	// 	queryHql += " order by udb.settleTime desc,udb.id desc";
    //
	// 	Object[] values = params.toArray();
	// 	return superDao.findPageList(queryHql, countHql, values, start, limit);
	// }
}