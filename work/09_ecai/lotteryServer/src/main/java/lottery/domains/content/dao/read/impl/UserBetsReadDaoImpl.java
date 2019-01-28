package lottery.domains.content.dao.read.impl;


import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javautils.array.ArrayUtils;
import javautils.jdbc.PageList;
import javautils.jdbc.hibernate.HibernateSuperReadDao;
import lottery.domains.content.dao.read.UserBetsReadDao;
import lottery.domains.content.entity.User;
import lottery.domains.content.entity.UserBets;

@Repository
public class UserBetsReadDaoImpl implements UserBetsReadDao {
	
	private final String tab = UserBets.class.getSimpleName();
	private final String utab = User.class.getSimpleName();
	private static final String NO_CODES_CONSTRUCT = "new UserBets(ub.id, ub.billno, ub.userId, ub.type, ub.lotteryId, ub.expect, ub.ruleId, ub.nums, ub.model, ub.multiple, ub.code, ub.point, ub.money, ub.time, ub.stopTime, ub.openTime, ub.status, ub.openCode, ub.prizeMoney, ub.prizeTime, ub.chaseBillno, ub.chaseStop, ub.planBillno, ub.rewardMoney, ub.compressed)";
	private static final String NO_CODES_SQL_CONSTRUCT = "ub.id, ub.billno, ub.user_id, ub.type, ub.lottery_id, ub.expect, ub.rule_id, ub.nums, ub.model, ub.multiple, ub.code, ub.point, ub.money, ub.time, ub.stop_time, ub.open_time, ub.status, ub.open_code, ub.prize_money, ub.prize_time, ub.chase_billno, ub.chase_stop, ub.plan_billno, ub.reward_money, ub.compressed";

	@Autowired
	private HibernateSuperReadDao<UserBets> superDao;

	@Override
	public UserBets getByIdWithCodes(int id) {
		String hql = "from " + tab + " where id = ?0";
		Object[] values = {id};
		return (UserBets) superDao.unique(hql, values);
	}

	@Override
	public UserBets getByIdWithoutCodes(int id) {
		String hql = "select " + NO_CODES_CONSTRUCT + " from " + tab + " ub where ub.id = ?0";
		Object[] values = {id};
		return (UserBets) superDao.unique(hql, values);
	}

	@Override
	public UserBets getByIdWithCodes(int id, int userId) {
		String hql = "from " + tab + " where id = ?0 and userId = ?1";
		Object[] values = {id, userId};
		return (UserBets) superDao.unique(hql, values);
	}

	@Override
	public UserBets getByIdWithoutCodes(int id, int userId) {
		String hql = "select " + NO_CODES_CONSTRUCT + " from " + tab + " ub where ub.id = ?0 and ub.userId = ?1";
		Object[] values = {id, userId};
		return (UserBets) superDao.unique(hql, values);
	}

	@Override
	public double getBillingOrder(int userId, String startTime, String endTime) {
		String hql = "select sum(money) from " + tab + " where userId = ?0 and prizeTime >= ?1 and prizeTime < ?2 and status > 0 and id > 0";
		Object[] values = {userId, startTime, endTime};
		Object result = superDao.unique(hql, values);
		return result != null ? ((Number) result).doubleValue() : 0;
	}

	@Override
	public double getUnSettleMoney(int userId, String startTime, String endTime) {
		String hql = "select sum(money) from " + tab + " where userId = ?0 and time >= ?1 and time < ?2 and status = 0 and id > 0";
		Object[] values = {userId, startTime, endTime};
		Object result = superDao.unique(hql, values);
		return result != null ? ((Number) result).doubleValue() : 0;
	}

	@Override
	public List<UserBets> searchByIds(List<Integer> ids) {
		String hql = "select " + NO_CODES_CONSTRUCT + " from " + tab + " ub where ub.id in ("+ArrayUtils.transInIds(ids)+") order by ub.time desc,ub.id desc";
		return superDao.list(hql);
	}

	@Override
	public PageList searchAll(Integer type, Integer lotteryId, String expect, Integer status, String sTime, String eTime, int start, int limit) {
		StringBuffer countHql = new StringBuffer("select count(ub.id) from ").append(tab).append("  ub ,").append(utab).append(" u  where ub.userId=u.id and u.upid >0  and  ub.id > 0");
		StringBuffer queryHql = new StringBuffer("select ").append(NO_CODES_CONSTRUCT).append(" from ").append(tab).append("  ub ,").append(utab).append(" u  where ub.userId=u.id and u.upid >0  and  ub.id > 0");

		List<Object> params = new LinkedList<>();

		int paramIndex = 0;
		if (type != null) {
			countHql.append(" and ub.type=?").append(paramIndex);
			queryHql.append(" and ub.type=?").append(paramIndex++);
			params.add(type);
		}
		if (lotteryId != null) {
			countHql.append(" and ub.lotteryId=?").append(paramIndex);
			queryHql.append(" and ub.lotteryId=?").append(paramIndex++);
			params.add(lotteryId);
		}
		if (StringUtils.isNotEmpty(expect)) {
			countHql.append(" and ub.expect=?").append(paramIndex);
			queryHql.append(" and ub.expect=?").append(paramIndex++);
			params.add(expect);
		}
		if (status != null) {
			countHql.append(" and ub.status=?").append(paramIndex);
			queryHql.append(" and ub.status=?").append(paramIndex++);
			params.add(status);
		}
		if (StringUtils.isNotEmpty(sTime)) {
			countHql.append(" and ub.time>=?").append(paramIndex);
			queryHql.append(" and ub.time>=?").append(paramIndex++);
			params.add(sTime);
		}
		if (StringUtils.isNotEmpty(eTime)) {
			countHql.append(" and ub.time<?").append(paramIndex);
			queryHql.append(" and ub.time<?").append(paramIndex++);
			params.add(eTime);
		}

		queryHql.append(" order by ub.time desc,ub.id desc");

		Object[] values = params.toArray();
		return superDao.findPageList(queryHql.toString(), countHql.toString(), values, start, limit);
	}

	@Override
	public PageList searchByUserId(int userId, Integer type, Integer lotteryId, String expect,
								   Integer status, String sTime, String eTime, int start, int limit) {
		StringBuffer countHql = new StringBuffer("select count(ub.id) from ").append(tab).append(" ub where ub.userId = ?0 and ub.id > 0");
		StringBuffer queryHql = new StringBuffer("select ").append(NO_CODES_CONSTRUCT).append(" from ").append(tab).append(" ub where ub.userId = ?0 and ub.id > 0");

		List<Object> params = new LinkedList<>();
		params.add(userId);

		int paramIndex = 1;
		if (type != null) {
			countHql.append(" and ub.type=?").append(paramIndex);
			queryHql.append(" and ub.type=?").append(paramIndex++);
			params.add(type);
		}
		if (lotteryId != null) {
			countHql.append(" and ub.lotteryId=?").append(paramIndex);
			queryHql.append(" and ub.lotteryId=?").append(paramIndex++);
			params.add(lotteryId);
		}
		if (StringUtils.isNotEmpty(expect)) {
			countHql.append(" and ub.expect=?").append(paramIndex);
			queryHql.append(" and ub.expect=?").append(paramIndex++);
			params.add(expect);
		}
		if (status != null) {
			countHql.append(" and ub.status=?").append(paramIndex);
			queryHql.append(" and ub.status=?").append(paramIndex++);
			params.add(status);
		}
		if (StringUtils.isNotEmpty(sTime)) {
			countHql.append(" and ub.time>=?").append(paramIndex);
			queryHql.append(" and ub.time>=?").append(paramIndex++);
			params.add(sTime);
		}
		if (StringUtils.isNotEmpty(eTime)) {
			countHql.append(" and ub.time<?").append(paramIndex);
			queryHql.append(" and ub.time<?").append(paramIndex++);
			params.add(eTime);
		}

		queryHql.append(" order by ub.time desc,ub.id desc");

		Object[] values = params.toArray();
		return superDao.findPageList(queryHql.toString(), countHql.toString(), values, start, limit);
	}

	@Override
	public PageList searchByTeam(int userId, Integer type, Integer lotteryId, String expect,
								 Integer status, String sTime, String eTime, int start, int limit) {
		StringBuffer countSql = new StringBuffer("select count(ub.id) from user_bets ub, user u where ub.user_id = u.id and (u.id = :userId or u.upids like :upid)");
		StringBuffer querySql = new StringBuffer("select ").append(NO_CODES_SQL_CONSTRUCT).append(" from user_bets ub, user u where ub.user_id = u.id and (u.id = :userId or u.upids like :upid)");

		Map<String, Object> params = new HashMap<>();
		params.put("userId", userId);
		params.put("upid", "%["+userId+"]%");

		if (type != null) {
			countSql.append(" and ub.type=:type");
			querySql.append(" and ub.type=:type");
			params.put("type", type);
		}
		if (lotteryId != null) {
			countSql.append(" and ub.lottery_id=:lotteryId");
			querySql.append(" and ub.lottery_id=:lotteryId");
			params.put("lotteryId", lotteryId);
		}
		if (StringUtils.isNotEmpty(expect)) {
			countSql.append(" and ub.expect=:expect");
			querySql.append(" and ub.expect=:expect");
			params.put("expect", expect);
		}
		if (status != null) {
			countSql.append(" and ub.status=:status");
			querySql.append(" and ub.status=:status");
			params.put("status", status);
		}
		if (StringUtils.isNotEmpty(sTime)) {
			countSql.append(" and ub.time>=:sTime");
			querySql.append(" and ub.time>=:sTime");
			params.put("sTime", sTime);
		}
		if (StringUtils.isNotEmpty(eTime)) {
			countSql.append(" and ub.time<:eTime");
			querySql.append(" and ub.time<:eTime");
			params.put("eTime", eTime);
		}

		querySql.append(" order by ub.time desc,ub.id desc");

		PageList pageList = superDao.findPageList(countSql.toString(), querySql.toString(), params, start, limit);

		if (pageList == null) {
			return new PageList();
		}
		List<?> list = pageList.getList();

		List<UserBets> results = new ArrayList<>(list.size());
		for (Object o : list) {
			Object[] objs = (Object[]) o;

			int index = 0;
			int _id = objs[index] != null ? Integer.valueOf(objs[index].toString()) : 0;index++;
			String _billno = objs[index] != null ? objs[index].toString() : "";index++;
			int _userId = objs[index] != null ? Integer.valueOf(objs[index].toString()) : 0;index++;
			int _type = objs[index] != null ? Integer.valueOf(objs[index].toString()) : 0;index++;
			int _lotteryId = objs[index] != null ? Integer.valueOf(objs[index].toString()) : 0;index++;
			String _expect = objs[index] != null ? objs[index].toString() : "";index++;
			int _ruleId = objs[index] != null ? Integer.valueOf(objs[index].toString()) : 0;index++;
			int _nums = objs[index] != null ? Integer.valueOf(objs[index].toString()) : 0;index++;
			String _model = objs[index] != null ? objs[index].toString() : "";index++;
			int _multiple = objs[index] != null ? Integer.valueOf(objs[index].toString()) : 0;index++;
			int _code = objs[index] != null ? Integer.valueOf(objs[index].toString()) : 0;index++;
			double _point = objs[index] != null ? ((BigDecimal) objs[index]).doubleValue() : 0;index++;
			double _money = objs[index] != null ? ((BigDecimal) objs[index]).doubleValue() : 0;index++;
			String _time = objs[index] != null ? objs[index].toString() : "";index++;
			String _stopTime = objs[index] != null ? objs[index].toString() : "";index++;
			String _openTime = objs[index] != null ? objs[index].toString() : "";index++;
			int _status = objs[index] != null ? Integer.valueOf(objs[index].toString()) : 0;index++;
			String _openCode = objs[index] != null ? objs[index].toString() : "";index++;
			double _prizeMoney = objs[index] != null ? ((BigDecimal) objs[index]).doubleValue() : 0;index++;
			String _prizeTime = objs[index] != null ? objs[index].toString() : "";index++;
			String _chaseBillno = objs[index] != null ? objs[index].toString() : "";index++;
			int _chaseStop = objs[index] != null ? Integer.valueOf(objs[index].toString()) : 0;index++;
			String _planBillno = objs[index] != null ? objs[index].toString() : "";index++;
			double _rewardMoney = objs[index] != null ? ((BigDecimal) objs[index]).doubleValue() : 0;index++;
			int _compressed = objs[index] != null ? Integer.valueOf(Boolean.valueOf(objs[index].toString())?1:0) : 0;index++;

			UserBets result = new UserBets();
			result.setId(_id);
			result.setBillno(_billno);
			result.setUserId(_userId);
			result.setType(_type);
			result.setLotteryId(_lotteryId);
			result.setExpect(_expect);
			result.setRuleId(_ruleId);
			result.setNums(_nums);
			result.setModel(_model);
			result.setMultiple(_multiple);
			result.setCode(_code);
			result.setPoint(_point);
			result.setMoney(_money);
			result.setTime(_time);
			result.setStopTime(_stopTime);
			result.setOpenTime(_openTime);
			result.setStatus(_status);
			result.setOpenCode(_openCode);
			result.setPrizeMoney(_prizeMoney);
			result.setPrizeTime(_prizeTime);
			result.setChaseBillno(_chaseBillno);
			result.setChaseStop(_chaseStop);
			result.setPlanBillno(_planBillno);
			result.setRewardMoney(_rewardMoney);
			result.setCompressed(_compressed);

			results.add(result);
		}
		pageList.setList(results);
		return pageList;
	}

	@Override
	public PageList searchByDirectTeam(int userId, Integer type, Integer lotteryId, String expect,
								 Integer status, String sTime, String eTime, int start, int limit) {
		StringBuffer countSql = new StringBuffer("select count(ub.id) from user_bets ub, user u where ub.user_id = u.id and (u.id = :userId or u.upid = :upid)");
		StringBuffer querySql = new StringBuffer("select ").append(NO_CODES_SQL_CONSTRUCT).append(" from user_bets ub, user u where ub.user_id = u.id and (u.id = :userId or u.upid = :upid)");

		Map<String, Object> params = new HashMap<>();
		params.put("userId", userId);
		params.put("upid", userId);

		if (type != null) {
			countSql.append(" and ub.type=:type");
			querySql.append(" and ub.type=:type");
			params.put("type", type);
		}
		if (lotteryId != null) {
			countSql.append(" and ub.lottery_id=:lotteryId");
			querySql.append(" and ub.lottery_id=:lotteryId");
			params.put("lotteryId", lotteryId);
		}
		if (StringUtils.isNotEmpty(expect)) {
			countSql.append(" and ub.expect=:expect");
			querySql.append(" and ub.expect=:expect");
			params.put("expect", expect);
		}
		if (status != null) {
			countSql.append(" and ub.status=:status");
			querySql.append(" and ub.status=:status");
			params.put("status", status);
		}
		if (StringUtils.isNotEmpty(sTime)) {
			countSql.append(" and ub.time>=:sTime");
			querySql.append(" and ub.time>=:sTime");
			params.put("sTime", sTime);
		}
		if (StringUtils.isNotEmpty(eTime)) {
			countSql.append(" and ub.time<:eTime");
			querySql.append(" and ub.time<:eTime");
			params.put("eTime", eTime);
		}

		querySql.append(" order by ub.time desc,ub.id desc");

		PageList pageList = superDao.findPageList(countSql.toString(), querySql.toString(), params, start, limit);

		if (pageList == null) {
			return new PageList();
		}
		List<?> list = pageList.getList();

		List<UserBets> results = new ArrayList<>(list.size());
		for (Object o : list) {
			Object[] objs = (Object[]) o;

			int index = 0;
			int _id = objs[index] != null ? Integer.valueOf(objs[index].toString()) : 0; index++;
			String _billno = objs[index] != null ? objs[index].toString() : ""; index++;
			int _userId = objs[index] != null ? Integer.valueOf(objs[index].toString()) : 0; index++;
			int _type = objs[index] != null ? Integer.valueOf(objs[index].toString()) : 0; index++;
			int _lotteryId = objs[index] != null ? Integer.valueOf(objs[index].toString()) : 0; index++;
			String _expect = objs[index] != null ? objs[index].toString() : ""; index++;
			int _ruleId = objs[index] != null ? Integer.valueOf(objs[index].toString()) : 0; index++;
			int _nums = objs[index] != null ? Integer.valueOf(objs[index].toString()) : 0; index++;
			String _model = objs[index] != null ? objs[index].toString() : ""; index++;
			int _multiple = objs[index] != null ? Integer.valueOf(objs[index].toString()) : 0; index++;
			int _code = objs[index] != null ? Integer.valueOf(objs[index].toString()) : 0; index++;
			double _point = objs[index] != null ? ((BigDecimal) objs[index]).doubleValue() : 0; index++;
			double _money = objs[index] != null ? ((BigDecimal) objs[index]).doubleValue() : 0; index++;
			String _time = objs[index] != null ? objs[index].toString() : ""; index++;
			String _stopTime = objs[index] != null ? objs[index].toString() : ""; index++;
			String _openTime = objs[index] != null ? objs[index].toString() : ""; index++;
			int _status = objs[index] != null ? Integer.valueOf(objs[index].toString()) : 0; index++;
			String _openCode = objs[index] != null ? objs[index].toString() : ""; index++;
			double _prizeMoney = objs[index] != null ? ((BigDecimal) objs[index]).doubleValue() : 0; index++;
			String _prizeTime = objs[index] != null ? objs[index].toString() : ""; index++;
			String _chaseBillno = objs[index] != null ? objs[index].toString() : ""; index++;
			int _chaseStop = objs[index] != null ? Integer.valueOf(objs[index].toString()) : 0; index++;
			String _planBillno = objs[index] != null ? objs[index].toString() : ""; index++;
			double _rewardMoney = objs[index] != null ? ((BigDecimal) objs[index]).doubleValue() : 0; index++;
			int _compressed = objs[index] != null ? Integer.valueOf(objs[index].toString()) : 0; index++;

			UserBets result = new UserBets();
			result.setId(_id);
			result.setBillno(_billno);
			result.setUserId(_userId);
			result.setType(_type);
			result.setLotteryId(_lotteryId);
			result.setExpect(_expect);
			result.setRuleId(_ruleId);
			result.setNums(_nums);
			result.setModel(_model);
			result.setMultiple(_multiple);
			result.setCode(_code);
			result.setPoint(_point);
			result.setMoney(_money);
			result.setTime(_time);
			result.setStopTime(_stopTime);
			result.setOpenTime(_openTime);
			result.setStatus(_status);
			result.setOpenCode(_openCode);
			result.setPrizeMoney(_prizeMoney);
			result.setPrizeTime(_prizeTime);
			result.setChaseBillno(_chaseBillno);
			result.setChaseStop(_chaseStop);
			result.setPlanBillno(_planBillno);
			result.setRewardMoney(_rewardMoney);
			result.setCompressed(_compressed);

			results.add(result);
		}
		pageList.setList(results);
		return pageList;
	}

	@Override
	public PageList searchByTeam(Integer[] userIds, Integer type, Integer lotteryId, String expect,
								 Integer status, String sTime, String eTime, int start, int limit) {
		StringBuffer countSql = new StringBuffer("select count(ub.id) from user_bets ub,user u where ub.user_id = u.id and (u.id in ("+ArrayUtils.transInIds(userIds)+") or ");
		StringBuffer querySql = new StringBuffer("select ").append(NO_CODES_SQL_CONSTRUCT).append(" from user_bets ub,user u where ub.user_id = u.id and (u.id in ("+ArrayUtils.transInIds(userIds)+") or ");

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

		countSql.append(")");
		querySql.append(")");

		if (type != null) {
			countSql.append(" and ub.type=:type");
			querySql.append(" and ub.type=:type");
			params.put("type", type);
		}
		if (lotteryId != null) {
			countSql.append(" and ub.lottery_id=:lotteryId");
			querySql.append(" and ub.lottery_id=:lotteryId");
			params.put("lotteryId", lotteryId);
		}
		if (StringUtils.isNotEmpty(expect)) {
			countSql.append(" and ub.expect=:expect");
			querySql.append(" and ub.expect=:expect");
			params.put("expect", expect);
		}
		if (status != null) {
			countSql.append(" and ub.status=:status");
			querySql.append(" and ub.status=:status");
			params.put("status", status);
		}
		if (StringUtils.isNotEmpty(sTime)) {
			countSql.append(" and ub.time>=:sTime");
			querySql.append(" and ub.time>=:sTime");
			params.put("sTime", sTime);
		}
		if (StringUtils.isNotEmpty(eTime)) {
			countSql.append(" and ub.time<:eTime");
			querySql.append(" and ub.time<:eTime");
			params.put("eTime", eTime);
		}

		querySql.append(" order by ub.time desc,ub.id desc");

		PageList pageList = superDao.findPageList(countSql.toString(), querySql.toString(), params, start, limit);

		if (pageList == null) {
			return new PageList();
		}
		List<?> list = pageList.getList();

		List<UserBets> results = new ArrayList<>(list.size());
		for (Object o : list) {
			Object[] objs = (Object[]) o;

			int index = 0;
			int _id = objs[index] != null ? Integer.valueOf(objs[index].toString()) : 0; index++;
			String _billno = objs[index] != null ? objs[index].toString() : ""; index++;
			int _userId = objs[index] != null ? Integer.valueOf(objs[index].toString()) : 0; index++;
			int _type = objs[index] != null ? Integer.valueOf(objs[index].toString()) : 0; index++;
			int _lotteryId = objs[index] != null ? Integer.valueOf(objs[index].toString()) : 0; index++;
			String _expect = objs[index] != null ? objs[index].toString() : ""; index++;
			int _ruleId = objs[index] != null ? Integer.valueOf(objs[index].toString()) : 0; index++;
			int _nums = objs[index] != null ? Integer.valueOf(objs[index].toString()) : 0; index++;
			String _model = objs[index] != null ? objs[index].toString() : ""; index++;
			int _multiple = objs[index] != null ? Integer.valueOf(objs[index].toString()) : 0; index++;
			int _code = objs[index] != null ? Integer.valueOf(objs[index].toString()) : 0; index++;
			double _point = objs[index] != null ? ((BigDecimal) objs[index]).doubleValue() : 0; index++;
			double _money = objs[index] != null ? ((BigDecimal) objs[index]).doubleValue() : 0; index++;
			String _time = objs[index] != null ? objs[index].toString() : ""; index++;
			String _stopTime = objs[index] != null ? objs[index].toString() : ""; index++;
			String _openTime = objs[index] != null ? objs[index].toString() : ""; index++;
			int _status = objs[index] != null ? Integer.valueOf(objs[index].toString()) : 0; index++;
			String _openCode = objs[index] != null ? objs[index].toString() : ""; index++;
			double _prizeMoney = objs[index] != null ? ((BigDecimal) objs[index]).doubleValue() : 0; index++;
			String _prizeTime = objs[index] != null ? objs[index].toString() : ""; index++;
			String _chaseBillno = objs[index] != null ? objs[index].toString() : ""; index++;
			int _chaseStop = objs[index] != null ? Integer.valueOf(objs[index].toString()) : 0; index++;
			String _planBillno = objs[index] != null ? objs[index].toString() : ""; index++;
			double _rewardMoney = objs[index] != null ? ((BigDecimal) objs[index]).doubleValue() : 0; index++;
			int _compressed = objs[index] != null ? Integer.valueOf(objs[index].toString()) : 0; index++;

			UserBets result = new UserBets();
			result.setId(_id);
			result.setBillno(_billno);
			result.setUserId(_userId);
			result.setType(_type);
			result.setLotteryId(_lotteryId);
			result.setExpect(_expect);
			result.setRuleId(_ruleId);
			result.setNums(_nums);
			result.setModel(_model);
			result.setMultiple(_multiple);
			result.setCode(_code);
			result.setPoint(_point);
			result.setMoney(_money);
			result.setTime(_time);
			result.setStopTime(_stopTime);
			result.setOpenTime(_openTime);
			result.setStatus(_status);
			result.setOpenCode(_openCode);
			result.setPrizeMoney(_prizeMoney);
			result.setPrizeTime(_prizeTime);
			result.setChaseBillno(_chaseBillno);
			result.setChaseStop(_chaseStop);
			result.setPlanBillno(_planBillno);
			result.setRewardMoney(_rewardMoney);
			result.setCompressed(_compressed);

			results.add(result);
		}
		pageList.setList(results);
		return pageList;
	}
}