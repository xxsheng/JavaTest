package lottery.domains.content.dao.read.impl;

import javautils.array.ArrayUtils;
import javautils.jdbc.PageList;
import javautils.jdbc.hibernate.HibernateSuperReadDao;
import lottery.domains.content.dao.read.UserDividendReadDao;
import lottery.domains.content.entity.UserDividend;
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
public class UserDividendReadDaoImpl implements UserDividendReadDao {
	private final String tab = UserDividend.class.getSimpleName();
	private static final String SQL_FIELDS = "ud.id, ud.user_id, ud.scale_level, ud.loss_level, ud.sales_level,ud.min_valid_user, ud.create_time, ud.agree_time, ud.start_date, ud.end_date, ud.total_amount, ud.last_amount, ud.`status`, ud.fixed, ud.min_scale, ud.max_scale, ud.remarks, ud.user_level";

	@Autowired
	private HibernateSuperReadDao<UserDividend> superDao;

	@Override
	public PageList search(List<Criterion> criterions, List<Order> orders, int start, int limit) {
		String propertyName = "id";
		return superDao.findPageList(UserDividend.class, propertyName, criterions, orders, start, limit);
	}

    @Override
    public Long getCountUser(int userId) {
        String hql = "select count(id) from " + tab + " udsb where userId = ?0";
        Object[] values = {userId};
        return (Long) superDao.unique(hql, values);
    }
	
	@Override
	public PageList searchByTeam(int[] userIs, int start, int limit) {
		StringBuffer countSql = new StringBuffer("select count(ud.id) from user_dividend ud,user u where ud.user_id = u.id and (u.id in ("+ArrayUtils.transInIds(userIs)+") or ");
		StringBuffer querySql = new StringBuffer("select ").append(SQL_FIELDS).append(" from user_dividend ud,user u where ud.user_id = u.id and (u.id in("+ArrayUtils.transInIds(userIs)+") or ");

		Map<String, Object> params = new HashMap<>();

		for (int i = 0; i < userIs.length; i++) {
			countSql.append("u.upids like :upid").append(i);
			querySql.append("u.upids like :upid").append(i);
			params.put("upid" + i, "%["+userIs[i]+"]%");

			if (i < userIs.length - 1) {
				countSql.append(" or ");
				querySql.append(" or ");
			}
		}

		countSql.append(")");
		querySql.append(") order by ud.create_time desc,ud.id desc");

		PageList pageList = superDao.findPageList(countSql.toString(), querySql.toString(), params, start, limit);

		if (pageList == null) {
			return new PageList();
		}
		List<?> list = pageList.getList();
		List<UserDividend> results = convertSqlResults(list);
		pageList.setList(results);
		return pageList;
	}

	@Override
	public PageList searchByTeam(int userId, int start, int limit) {
		StringBuffer countSql = new StringBuffer("select count(ud.id) from user_dividend ud, user u where ud.user_id = u.id and (u.id = :userId or u.upids like :upid)");
		StringBuffer querySql = new StringBuffer("select ").append(SQL_FIELDS).append(" from user_dividend ud, user u where ud.user_id = u.id and (u.id = :userId or u.upids like :upid)");

		Map<String, Object> params = new HashMap<>();
		params.put("userId", userId);
		params.put("upid", "%["+userId+"]%");

		querySql.append(" order by ud.create_time desc,ud.id desc");

		PageList pageList = superDao.findPageList(countSql.toString(), querySql.toString(), params, start, limit);

		if (pageList == null) {
			return new PageList();
		}
		List<?> list = pageList.getList();

		List<UserDividend> results = convertSqlResults(list);

		pageList.setList(results);
		return pageList;
	}

	@Override
	public PageList searchByUserId(int userId, int start, int limit) {
		StringBuffer countSql = new StringBuffer("select count(ud.id) from user_dividend ud where ud.user_id = :userId");
		StringBuffer querySql = new StringBuffer("select ").append(SQL_FIELDS).append(" from user_dividend ud where ud.user_id = :userId");

		Map<String, Object> params = new HashMap<>();
		params.put("userId", userId);

		querySql.append(" order by ud.create_time desc,ud.id desc");

		PageList pageList = superDao.findPageList(countSql.toString(), querySql.toString(), params, start, limit);

		if (pageList == null) {
			return new PageList();
		}
		List<?> list = pageList.getList();

		List<UserDividend> results = convertSqlResults(list);

		pageList.setList(results);
		return pageList;
	}

	private List<UserDividend> convertSqlResults(List<?> list) {
		if (CollectionUtils.isEmpty(list)) {
			return new ArrayList<>();
		}

		List<UserDividend> results = new ArrayList<>(list.size());
		for (Object o : list) {
			Object[] objs = (Object[]) o;

			int _id = objs[0] != null ? Integer.valueOf(objs[0].toString()) : 0;
			int _userId = objs[1] != null ? Integer.valueOf(objs[1].toString()) : 0;
			String _scale = objs[2] != null ? objs[2].toString() : "";
			String _loss = objs[3] != null ? objs[3].toString() : "";
			String _sales = objs[4] != null ? objs[4].toString() : "";
			int _minValidUser = objs[5] != null ? Integer.valueOf(objs[5].toString()) : 0;
			String _createTime = objs[6] != null ? objs[6].toString() : "";
			String _agreeTime = objs[7] != null ? objs[7].toString() : "";
			String _startDate = objs[8] != null ? objs[8].toString() : "";
			String _endDate = objs[9] != null ? objs[9].toString() : "";
			double _totalAmount = objs[10] != null ? ((BigDecimal) objs[10]).doubleValue() : 0;
			double _lastAmount = objs[11] != null ? ((BigDecimal) objs[11]).doubleValue() : 0;
			int _status = objs[12] != null ? Integer.valueOf(objs[12].toString()) : 0;
			int _fixed = objs[13] != null ? Integer.valueOf(objs[13].toString()) : 0;
			double _minScale = objs[14] != null ? Double.valueOf(objs[14].toString()) : 0;
			double _maxScale = objs[15] != null ? Double.valueOf(objs[15].toString()) : 0;
			String _remarks = objs[16] != null ? objs[16].toString() : "";
			String _user = objs[17] != null ? objs[17].toString() : "";

			UserDividend result = new UserDividend();
			result.setId(_id);
			result.setUserId(_userId);
			result.setScaleLevel(_scale);
			result.setSalesLevel(_sales); 
			result.setLossLevel(_loss); 
			result.setUserLevel(_user);
			result.setMinValidUser(_minValidUser);
			result.setCreateTime(_createTime);
			result.setAgreeTime(_agreeTime);
			result.setStartDate(_startDate);
			result.setEndDate(_endDate);
			result.setTotalAmount(_totalAmount);
			result.setLastAmount(_lastAmount);
			result.setStatus(_status);
			result.setFixed(_fixed);
			result.setMinScale(_minScale);
			result.setMaxScale(_maxScale);
			result.setRemarks(_remarks);

			results.add(result);
		}
		return results;
	}

	// @Override
	// public PageList searchByZhuGuans(List<Integer> userIds, int start, int limit) {
	// 	String countHql = "select count(ud.id) from " + tab + " ud,User u where ud.userId = u.id and (ud.userId in ("+ArrayUtils.transInIds(userIds)+") or u.upid in ("+ArrayUtils.transInIds(userIds)+")) and u.id > 0 and ud.id > 0";
	// 	String queryHql = "select ud from " + tab + " ud,User u where ud.userId = u.id and (ud.userId in ("+ArrayUtils.transInIds(userIds)+") or u.upid in ("+ArrayUtils.transInIds(userIds)+")) and u.id > 0 and ud.id > 0 order by ud.createTime desc,ud.id desc";
    //
	// 	List<Object> params = new LinkedList<>();
    //
	// 	Object[] values = params.toArray();
	// 	return superDao.findPageList(queryHql, countHql, values, start, limit);
	// }
    //
	// @Override
	// public PageList searchByDirectLowers(int upUserId, int start, int limit) {
	// 	String countHql = "select count(ud.id) from " + tab + " ud,User u where ud.userId = u.id and (ud.userId = ?0 or u.upid = ?1) and u.id >0";
	// 	String queryHql = "select ud from " + tab + " ud,User u where ud.userId = u.id and (ud.userId = ?0 or u.upid = ?1) and u.id > 0 order by ud.createTime desc,ud.id desc";
	// 	Object[] values = {upUserId, upUserId};
	// 	return superDao.findPageList(queryHql, countHql, values, start, limit);
	// }

	@Override
	public List<UserDividend> findByUserIds(List<Integer> userIds) {
		String hql = "from " + tab + " where userId in( " +  ArrayUtils.transInIds(userIds) + ")";
		return superDao.list(hql);
	}
}