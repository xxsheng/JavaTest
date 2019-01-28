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
import lottery.domains.content.dao.read.UserBillReadDao;
import lottery.domains.content.entity.User;
import lottery.domains.content.entity.UserBill;


@Repository
public class UserBillReadDaoImpl implements UserBillReadDao {
	
	private final String tab = UserBill.class.getSimpleName();
	
	private final String utab = User.class.getSimpleName();
	@Autowired
	private HibernateSuperReadDao<UserBill> superDao;

	@Override
	public UserBill getById(int id) {
		String hql = "from " + tab + " where id = ?0";
		Object[] values = {id};
		return (UserBill) superDao.unique(hql, values);
	}

	@Override
	public PageList searchByUserId(int userId, Integer account, Integer type, String sTime, String eTime, int start, int limit) {
		StringBuffer countHql = new StringBuffer("select count(ub.id) from ").append(tab).append(" ub where ub.userId = ?0 and ub.id > 0");
		StringBuffer queryHql = new StringBuffer("select ub from ").append(tab).append(" ub where ub.userId = ?0 and ub.id > 0");

		List<Object> params = new LinkedList<>();
		params.add(userId);

		int paramIndex = 1;
		if (account != null) {
			countHql.append(" and ub.account=?").append(paramIndex);
			queryHql.append(" and ub.account=?").append(paramIndex++);
			params.add(account);
		}
		if (type != null) {
			countHql.append(" and ub.type=?").append(paramIndex);
			queryHql.append(" and ub.type=?").append(paramIndex++);
			params.add(type);
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
	public PageList searchAll(Integer account, Integer type, String sTime, String eTime, int start, int limit) {
	//	StringBuffer countHql = new StringBuffer("select count(ub.id) from ").append(tab).append(" ub where ub.id > 0");
		//StringBuffer queryHql = new StringBuffer("select ub from ").append(tab).append(" ub where ub.id > 0");
		
		StringBuffer countHql =new StringBuffer("  select count(ub.id) from  "+tab+" ub ,"+utab+"  u  where ub.userId = u.id  and u.upid !=0   and ub.id > 0 "); 
		StringBuffer queryHql =new StringBuffer("  select ub  from  "+tab+" ub ,"+utab+"  u where ub.userId = u.id  and  u.upid !=0 and ub.id > 0 "); 

		List<Object> params = new LinkedList<>();

		int paramIndex = 0;
		if (account != null) {
			countHql.append(" and ub.account=?").append(paramIndex);
			queryHql.append(" and ub.account=?").append(paramIndex++);
			params.add(account);
		}
		if (type != null) {
			countHql.append(" and ub.type=?").append(paramIndex);
			queryHql.append(" and ub.type=?").append(paramIndex++);
			params.add(type);
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
	public PageList searchByTeam(int userId, Integer account, Integer type, String sTime, String eTime, int start, int limit) {
		StringBuffer countSql = new StringBuffer("select count(ub.id) from user_bill ub,user u where ub.user_id = u.id and (u.id = :userId or u.upids like :upid)");
		StringBuffer querySql = new StringBuffer("select ub.id, ub.billno, ub.user_id, ub.account, ub.`type`, ub.money, ub.before_money, ub.after_money, ub.ref_type, ub.ref_id, ub.time, ub.remarks from user_bill ub,user u where ub.user_id = u.id and (u.id = :userId or u.upids like :upid)");

		Map<String, Object> params = new HashMap<>();
		params.put("userId", userId);
		params.put("upid", "%["+userId+"]%");

		if (account != null) {
			countSql.append(" and ub.account=:account");
			querySql.append(" and ub.account=:account");
			params.put("account", account);
		}
		if (type != null) {
			countSql.append(" and ub.type=:type");
			querySql.append(" and ub.type=:type");
			params.put("type", type);
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

		List<UserBill> results = new ArrayList<>(list.size());
		for (Object o : list) {
			Object[] objs = (Object[]) o;

			int index = 0;
			int _id = objs[index] != null ? Integer.valueOf(objs[index++].toString()) : 0;
			String _billno = objs[index] != null ? objs[index++].toString() : "";
			int _userId = objs[index] != null ? Integer.valueOf(objs[index++].toString()) : 0;
			int _account = objs[index] != null ? Integer.valueOf(objs[index++].toString()) : 0;
			int _type = objs[index] != null ? Integer.valueOf(objs[index++].toString()) : 0;
			double _money = objs[index] != null ? ((BigDecimal) objs[index++]).doubleValue() : 0;
			double _beforeMoney = objs[index] != null ? ((BigDecimal) objs[index++]).doubleValue() : 0;
			double _afterMoney = objs[index] != null ? ((BigDecimal) objs[index++]).doubleValue() : 0;
			int _refType = objs[index] != null ? Integer.valueOf(objs[index++].toString()) : 0;
			String _refId = objs[index] != null ? objs[index++].toString() : "";
			String _time = objs[index] != null ? objs[index++].toString() : "";
			String _remarks = objs[index] != null ? objs[index++].toString() : "";

			UserBill result = new UserBill();
			result.setId(_id);
			result.setBillno(_billno);
			result.setUserId(_userId);
			result.setAccount(_account);
			result.setType(_type);
			result.setMoney(_money);
			result.setBeforeMoney(_beforeMoney);
			result.setAfterMoney(_afterMoney);
			result.setRefType(_refType);
			result.setRefId(_refId);
			result.setTime(_time);
			result.setRemarks(_remarks);
			results.add(result);
		}
		pageList.setList(results);
		return pageList;

		// StringBuffer countHql = new StringBuffer("select count(ub.id) from ").append(tab).append(" ub where ub.userId in (select u.id from User u where u.id = ?0 or u.upids like ?1)");
		// StringBuffer queryHql = new StringBuffer("select ub from ").append(tab).append(" ub where ub.userId in (select u.id from User u where u.id = ?0 or u.upids like ?1)");
        //
		// List<Object> params = new LinkedList<>();
		// params.add(userId);
		// params.add("%["+userId+"]%");
        //
		// int paramIndex = 2;
		// if (account != null) {
		// 	countHql.append(" and ub.account=?").append(paramIndex);
		// 	queryHql.append(" and ub.account=?").append(paramIndex++);
		// 	params.add(account);
		// }
		// if (type != null) {
		// 	countHql.append(" and ub.type=?").append(paramIndex);
		// 	queryHql.append(" and ub.type=?").append(paramIndex++);
		// 	params.add(type);
		// }
		// if (StringUtils.isNotEmpty(sTime)) {
		// 	countHql.append(" and ub.time>=?").append(paramIndex);
		// 	queryHql.append(" and ub.time>=?").append(paramIndex++);
		// 	params.add(sTime);
		// }
		// if (StringUtils.isNotEmpty(eTime)) {
		// 	countHql.append(" and ub.time<?").append(paramIndex);
		// 	queryHql.append(" and ub.time<?").append(paramIndex++);
		// 	params.add(eTime);
		// }
        //
		// queryHql.append(" order by ub.time desc,ub.id desc");
        //
		// Object[] values = params.toArray();
		// return superDao.findPageList(queryHql.toString(), countHql.toString(), values, start, limit);
	}

	@Override
	public PageList searchByDirectTeam(int userId, Integer account, Integer type, String sTime, String eTime, int start, int limit) {
		StringBuffer countSql = new StringBuffer("select count(ub.id) from user_bill ub,user u where ub.user_id = u.id and (u.id = :userId or u.upid = :upid)");
		StringBuffer querySql = new StringBuffer("select ub.id, ub.billno, ub.user_id, ub.account, ub.`type`, ub.money, ub.before_money, ub.after_money, ub.ref_type, ub.ref_id, ub.time, ub.remarks from user_bill ub,user u where ub.user_id = u.id and (u.id = :userId or u.upid = :upid)");

		Map<String, Object> params = new HashMap<>();
		params.put("userId", userId);
		params.put("upid", userId);

		if (account != null) {
			countSql.append(" and ub.account=:account");
			querySql.append(" and ub.account=:account");
			params.put("account", account);
		}
		if (type != null) {
			countSql.append(" and ub.type=:type");
			querySql.append(" and ub.type=:type");
			params.put("type", type);
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

		List<UserBill> results = new ArrayList<>(list.size());
		for (Object o : list) {
			Object[] objs = (Object[]) o;

			int index = 0;
			int _id = objs[index] != null ? Integer.valueOf(objs[index++].toString()) : 0;
			String _billno = objs[index] != null ? objs[index++].toString() : "";
			int _userId = objs[index] != null ? Integer.valueOf(objs[index++].toString()) : 0;
			int _account = objs[index] != null ? Integer.valueOf(objs[index++].toString()) : 0;
			int _type = objs[index] != null ? Integer.valueOf(objs[index++].toString()) : 0;
			double _money = objs[index] != null ? ((BigDecimal) objs[index++]).doubleValue() : 0;
			double _beforeMoney = objs[index] != null ? ((BigDecimal) objs[index++]).doubleValue() : 0;
			double _afterMoney = objs[index] != null ? ((BigDecimal) objs[index++]).doubleValue() : 0;
			int _refType = objs[index] != null ? Integer.valueOf(objs[index++].toString()) : 0;
			String _refId = objs[index] != null ? objs[index++].toString() : "";
			String _time = objs[index] != null ? objs[index++].toString() : "";
			String _remarks = objs[index] != null ? objs[index++].toString() : "";

			UserBill result = new UserBill();
			result.setId(_id);
			result.setBillno(_billno);
			result.setUserId(_userId);
			result.setAccount(_account);
			result.setType(_type);
			result.setMoney(_money);
			result.setBeforeMoney(_beforeMoney);
			result.setAfterMoney(_afterMoney);
			result.setRefType(_refType);
			result.setRefId(_refId);
			result.setTime(_time);
			result.setRemarks(_remarks);
			results.add(result);
		}
		pageList.setList(results);
		return pageList;

		// StringBuffer countHql = new StringBuffer("select count(ub.id) from ").append(tab).append(" ub where ub.userId in (select u.id from User u where u.id = ?0 or u.upids like ?1)");
		// StringBuffer queryHql = new StringBuffer("select ub from ").append(tab).append(" ub where ub.userId in (select u.id from User u where u.id = ?0 or u.upids like ?1)");
        //
		// List<Object> params = new LinkedList<>();
		// params.add(userId);
		// params.add("%["+userId+"]%");
        //
		// int paramIndex = 2;
		// if (account != null) {
		// 	countHql.append(" and ub.account=?").append(paramIndex);
		// 	queryHql.append(" and ub.account=?").append(paramIndex++);
		// 	params.add(account);
		// }
		// if (type != null) {
		// 	countHql.append(" and ub.type=?").append(paramIndex);
		// 	queryHql.append(" and ub.type=?").append(paramIndex++);
		// 	params.add(type);
		// }
		// if (StringUtils.isNotEmpty(sTime)) {
		// 	countHql.append(" and ub.time>=?").append(paramIndex);
		// 	queryHql.append(" and ub.time>=?").append(paramIndex++);
		// 	params.add(sTime);
		// }
		// if (StringUtils.isNotEmpty(eTime)) {
		// 	countHql.append(" and ub.time<?").append(paramIndex);
		// 	queryHql.append(" and ub.time<?").append(paramIndex++);
		// 	params.add(eTime);
		// }
        //
		// queryHql.append(" order by ub.time desc,ub.id desc");
        //
		// Object[] values = params.toArray();
		// return superDao.findPageList(queryHql.toString(), countHql.toString(), values, start, limit);
	}

	@Override
	public PageList searchByTeam(Integer[] userIds, Integer account, Integer type, String sTime, String eTime, int start, int limit) {
		StringBuffer countSql = new StringBuffer("select count(ub.id) from user_bill ub,user u where ub.user_id = u.id and (u.id in ("+ArrayUtils.transInIds(userIds)+") or ");
		StringBuffer querySql = new StringBuffer("select ub.id, ub.billno, ub.user_id, ub.account, ub.`type`, ub.money, ub.before_money, ub.after_money, ub.ref_type, ub.ref_id, ub.time, ub.remarks from user_bill ub,user u where ub.user_id = u.id and (u.id in("+ArrayUtils.transInIds(userIds)+") or ");

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

		if (account != null) {
			countSql.append(" and ub.account=:account");
			querySql.append(" and ub.account=:account");
			params.put("account", account);
		}
		if (type != null) {
			countSql.append(" and ub.type=:type");
			querySql.append(" and ub.type=:type");
			params.put("type", type);
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

		List<UserBill> results = new ArrayList<>(list.size());
		for (Object o : list) {
			Object[] objs = (Object[]) o;

			int index = 0;
			int _id = objs[index] != null ? Integer.valueOf(objs[index++].toString()) : 0;
			String _billno = objs[index] != null ? objs[index++].toString() : "";
			int _userId = objs[index] != null ? Integer.valueOf(objs[index++].toString()) : 0;
			int _account = objs[index] != null ? Integer.valueOf(objs[index++].toString()) : 0;
			int _type = objs[index] != null ? Integer.valueOf(objs[index++].toString()) : 0;
			double _money = objs[index] != null ? ((BigDecimal) objs[index++]).doubleValue() : 0;
			double _beforeMoney = objs[index] != null ? ((BigDecimal) objs[index++]).doubleValue() : 0;
			double _afterMoney = objs[index] != null ? ((BigDecimal) objs[index++]).doubleValue() : 0;
			int _refType = objs[index] != null ? Integer.valueOf(objs[index++].toString()) : 0;
			String _refId = objs[index] != null ? objs[index++].toString() : "";
			String _time = objs[index] != null ? objs[index++].toString() : "";
			String _remarks = objs[index] != null ? objs[index++].toString() : "";

			UserBill result = new UserBill();
			result.setId(_id);
			result.setBillno(_billno);
			result.setUserId(_userId);
			result.setAccount(_account);
			result.setType(_type);
			result.setMoney(_money);
			result.setBeforeMoney(_beforeMoney);
			result.setAfterMoney(_afterMoney);
			result.setRefType(_refType);
			result.setRefId(_refId);
			result.setTime(_time);
			result.setRemarks(_remarks);
			results.add(result);
		}
		pageList.setList(results);
		return pageList;

		// StringBuffer countHql = new StringBuffer("select count(ub.id) from ").append(tab).append(" ub where ub.userId in(select u.id from User u where u.id in("+ ArrayUtils.transInIds(userIds)+") or (");
		// StringBuffer queryHql = new StringBuffer("select ub from ").append(tab).append(" ub where ub.userId in(select u.id from User u where u.id in("+ ArrayUtils.transInIds(userIds)+") or (");
        //
		// List<Object> params = new LinkedList<>();
        //
		// int paramIndex = 0;
		// for (int i = 0; i < userIds.length; i++) {
		// 	countHql.append("u.upids like ?").append(paramIndex);
		// 	queryHql.append("u.upids like ?").append(paramIndex++);
        //
		// 	if (i < userIds.length - 1) {
		// 		countHql.append(" or ");
		// 		queryHql.append(" or ");
		// 	}
        //
		// 	params.add("%[" + userIds[i] + "]%");
		// }
		// countHql.append(")) and ub.id > 0");
		// queryHql.append(")) and ub.id > 0");
        //
		// if (account != null) {
		// 	countHql.append(" and ub.account=?").append(paramIndex);
		// 	queryHql.append(" and ub.account=?").append(paramIndex++);
		// 	params.add(account);
		// }
		// if (type != null) {
		// 	countHql.append(" and ub.type=?").append(paramIndex);
		// 	queryHql.append(" and ub.type=?").append(paramIndex++);
		// 	params.add(type);
		// }
		// if (StringUtils.isNotEmpty(sTime)) {
		// 	countHql.append(" and ub.time>=?").append(paramIndex);
		// 	queryHql.append(" and ub.time>=?").append(paramIndex++);
		// 	params.add(sTime);
		// }
		// if (StringUtils.isNotEmpty(eTime)) {
		// 	countHql.append(" and ub.time<?").append(paramIndex);
		// 	queryHql.append(" and ub.time<?").append(paramIndex++);
		// 	params.add(eTime);
		// }
        //
		// queryHql.append(" order by ub.time desc,ub.id desc");
        //
		// Object[] values = params.toArray();
		// return superDao.findPageList(queryHql.toString(), countHql.toString(), values, start, limit);
	}
}