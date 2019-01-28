package lottery.domains.content.dao.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Order;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javautils.StringUtil;
import javautils.array.ArrayUtils;
import javautils.jdbc.PageList;
import javautils.jdbc.hibernate.HibernateSuperDao;
import lottery.domains.content.dao.UserRechargeDao;
import lottery.domains.content.entity.HistoryUserRecharge;
import lottery.domains.content.entity.User;
import lottery.domains.content.entity.UserRecharge;

@Repository
public class UserRechargeDaoImpl implements UserRechargeDao {
	
	private final String user = User.class.getSimpleName();
	
	private final String tab = UserRecharge.class.getSimpleName();
	
	private final String historyTab = HistoryUserRecharge.class.getSimpleName();

	@Autowired
	private HibernateSuperDao<UserRecharge> superDao;
	
	@Autowired
	private HibernateSuperDao<HistoryUserRecharge> historySuperDao;
	
	@Override
	public boolean add(UserRecharge entity) {
		return superDao.save(entity);
	}
	
	@Override
	public boolean update(UserRecharge entity) {
		return superDao.update(entity);
	}

	@Override
	public boolean updateSuccess(int id, double beforeMoney, double afterMoney, double recMoney, String payTime, String payBillno) {
		String hql = "update " + tab + " set beforeMoney = ?0,afterMoney = ?1,recMoney=?2,status = 1, payTime=?3, payBillno=?4 where id = ?5";
		Object[] values = {beforeMoney, afterMoney, recMoney, payTime, payBillno, id};
		return superDao.update(hql, values);
	}

	@Override
	public UserRecharge getById(int id) {
		String hql = "from " + tab + " where id = ?0";
		Object[] values = {id};
		return (UserRecharge) superDao.unique(hql, values);
	}
	
	@Override
	public HistoryUserRecharge getHistoryById(int id) {
		String hql = "from " + historyTab + " where id = ?0";
		Object[] values = {id};
		return (HistoryUserRecharge) historySuperDao.unique(hql, values);
	}
	
	@Override
	public UserRecharge getByBillno(String billno) {
		String hql = "from " + tab + " where billno = ?0";
		Object[] values = {billno};
		return (UserRecharge) superDao.unique(hql, values);
	}
	
	@Override
	public boolean isRecharge(int userId) {
		String hql = "select count(id) from " + tab + " where userId = ?0 and status = 1";
		Object[] values = {userId};
		Object result = superDao.unique(hql, values);
		return result != null ? ((Number) result).intValue() > 0 : false;
	}
	
	@Override
	public List<UserRecharge> getLatest(int userId, int status, int count) {
		String hql = "from " + tab + " where userId = ?0 and status = ?1 order by id desc";
		Object[] values = {userId, status};
		return superDao.list(hql, values, 0, count);
	}

	@Override
	public List<UserRecharge> list(List<Criterion> criterions, List<Order> orders) {
		return superDao.findByCriteria(UserRecharge.class, criterions, orders);
	}

/*	@Override
	public PageList find(List<Criterion> criterions, List<Order> orders, int start, int limit) {
		String propertyName = "id";
 		return superDao.findPageList(UserRecharge.class, propertyName, criterions, orders, start, limit);
	}*/

	@Override
	public PageList findHistory(List<Criterion> criterions, List<Order> orders, int start, int limit) {
		String propertyName = "id";
		return historySuperDao.findPageList(HistoryUserRecharge.class, propertyName, criterions, orders, start, limit);
	}
	
	@Override
	public List<?> getDayRecharge(String sTime, String eTime) {
		String hql = "select substring(payTime, 1, 10), sum(recMoney) from " + tab + " where status = 1 and payTime >= ?0 and payTime < ?1 group by substring(payTime, 1, 10)";
		Object[] values = {sTime, eTime};
		return superDao.listObject(hql, values);
	}

	@Override
	public List<?> getDayRecharge2(String sTime, String eTime, Integer type, Integer subtype) {
		String hql = "select substring(b.payTime, 1, 10), count(b.id), sum(b.recMoney), sum(b.receiveFeeMoney) from " + tab + " as b , " + user + "  as u   where u.id = b.userId  and u.upid != 0 and  b.status = 1 and b.payTime >= ?0 and b.payTime < ?1";
		Object[] values = null;
		if (type != null && subtype != null) {
			hql += " and b.type=?2 and b.subtype=?3";
			values = new Object[]{sTime, eTime, type, subtype};
		}
		else if (type != null && subtype == null) {
			hql += " and b.type=?2";
			values = new Object[]{sTime, eTime, type};
		}
		else if (type == null && subtype != null) {
			hql += " and b.subtype=?2";
			values = new Object[]{sTime, eTime, subtype};
		}
		else {
			values = new Object[]{sTime, eTime};
		}
		hql += " group by substring(b.payTime, 1, 10)";
		return superDao.listObject(hql, values);
	}

	@Override
	public double getTotalFee(String sTime, String eTime) {
		String hql = "select sum(receiveFeeMoney) from " + tab + " where status = 1 and payTime >= ?0 and payTime < ?1";
		Object[] values = {sTime, eTime};
		Object result = superDao.unique(hql, values);
		return result != null ? ((Number) result).doubleValue() : 0;
	}

	@Override
	public double getThirdTotalRecharge(String sTime, String eTime) {
		String hql = "select sum(recMoney) from " + tab + " where status = 1 and payTime >= ?0 and payTime < ?1 and channelId is not null and channelId > 0";

		Object[] values = {sTime, eTime};
		Object result = superDao.unique(hql, values);
		return result != null ? ((Number) result).doubleValue() : 0;
	}

	@Override
	public double getTotalRecharge(String sTime, String eTime, int[] type, int[] subtype, Integer payBankId) {
		String hql = "select sum(recMoney) from " + tab + " where status = 1 and payTime >= ?0 and payTime < ?1";
		if(type != null && type.length > 0) {
			hql += " and type in (" + ArrayUtils.transInIds(type) + ")";
		}
		if(subtype != null && subtype.length > 0) {
			hql += " and subtype in (" + ArrayUtils.transInIds(subtype) + ")";
		}
		if (payBankId != null) {
			hql += " and payBankId = " + payBankId;
		}
		Object[] values = {sTime, eTime};
		Object result = superDao.unique(hql, values);
		return result != null ? ((Number) result).doubleValue() : 0;
	}

	@Override
	public Object[] getTotalRechargeData(String sTime, String eTime, Integer type, Integer subtype) {
		String hql = "select count(b.id), sum(b.recMoney), sum(b.receiveFeeMoney) from " + tab + " as b , " + user + " as u  where b.userId = u.id and u.upid != 0 and b.status = 1 and b.payTime >= :sTime and b.payTime < :eTime";
		Map<String, Object> params = new HashMap<>();
		params.put("sTime", sTime);
		params.put("eTime", eTime);
		if(type != null) {
			hql += " and b.type = :type";
			params.put("type", type);
		}
		if(subtype != null) {
			hql += " and b.subtype in :subtype";
			params.put("subtype", subtype);
		}
		// 总充值订单数/总充值金额/总充值第三方手续费
		Object result = superDao.uniqueWithParams(hql, params);
		if (result == null) {
			return new Object[]{0, 0.0, 0.0};
		}
		Object[] results = (Object[]) result;

		Object result1 = results[0] == null ? 0 : results[0];
		Object result2 = results[1] == null ? 0.00 : results[1];
		Object result3 = results[2] == null ? 0.00 : results[2];

		return new Object[]{result1, result2, result3};
	}

	@Override
	public double getTotalRecharge(String billno, Integer userId, String minTime, String maxTime, String minPayTime, String maxPayTime, Double minMoney, Double maxMoney, Integer status, Integer channelId) {
		String hql = "select sum(recMoney) from " + tab + " where 1=1";

		Map<String, Object> params = new HashMap<>();
		if (StringUtil.isNotNull(billno)) {
			hql += " and billno like :billno";
			params.put("billno", "%" + billno + "%");
		}
		if (userId != null) {
			hql += " and userId = :userId";
			params.put("userId", userId);
		}
		if (StringUtil.isNotNull(minTime)) {
			hql += " and time > :minTime";
			params.put("minTime", minTime);
		}
		if (StringUtil.isNotNull(maxTime)) {
			hql += " and time < :maxTime";
			params.put("maxTime", maxTime);
		}
		if (StringUtil.isNotNull(minPayTime)) {
			hql += " and payTime > :minPayTime";
			params.put("minPayTime", minPayTime);
		}
		if (StringUtil.isNotNull(maxPayTime)) {
			hql += " and payTime < :maxPayTime";
			params.put("maxPayTime", maxPayTime);
		}
		if (minMoney != null) {
			hql += " and money >= :minMoney";
			params.put("minMoney", minMoney);
		}
		if (maxMoney != null) {
			hql += " and money <= :maxMoney";
			params.put("maxMoney", maxMoney);
		}
		if (status != null) {
			hql += " and status = :status";
			params.put("status", status);
		}
		if (channelId != null) {
			hql += " and channelId = :channelId";
			params.put("channelId", channelId);
		}
		Object result = superDao.uniqueWithParams(hql, params);
		return result != null ? ((Number) result).doubleValue() : 0;
	}
	
	@Override
	public double getHistoryTotalRecharge(String billno, Integer userId, String minTime, String maxTime, String minPayTime, String maxPayTime, Double minMoney, Double maxMoney, Integer status, Integer channelId) {
		String hql = "select sum(recMoney) from " + historyTab + " where 1=1";

		Map<String, Object> params = new HashMap<>();
		if (StringUtil.isNotNull(billno)) {
			hql += " and billno like :billno";
			params.put("billno", "%" + billno + "%");
		}
		if (userId != null) {
			hql += " and userId = :userId";
			params.put("userId", userId);
		}
		if (StringUtil.isNotNull(minTime)) {
			hql += " and time > :minTime";
			params.put("minTime", minTime);
		}
		if (StringUtil.isNotNull(maxTime)) {
			hql += " and time < :maxTime";
			params.put("maxTime", maxTime);
		}
		if (StringUtil.isNotNull(minPayTime)) {
			hql += " and payTime > :minPayTime";
			params.put("minPayTime", minPayTime);
		}
		if (StringUtil.isNotNull(maxPayTime)) {
			hql += " and payTime < :maxPayTime";
			params.put("maxPayTime", maxPayTime);
		}
		if (minMoney != null) {
			hql += " and money >= :minMoney";
			params.put("minMoney", minMoney);
		}
		if (maxMoney != null) {
			hql += " and money <= :maxMoney";
			params.put("maxMoney", maxMoney);
		}
		if (status != null) {
			hql += " and status = :status";
			params.put("status", status);
		}
		if (channelId != null) {
			hql += " and channelId = :channelId";
			params.put("channelId", channelId);
		}
		Object result = superDao.uniqueWithParams(hql, params);
		return result != null ? ((Number) result).doubleValue() : 0;
	}

	@Override
	public int getRechargeCount(int status, int type, int subType) {
		String hql = "select count(id) from " + tab + " where status = ?0 and type = ?1 and subtype = ?2";
		Object[] values = {status, type,subType};
		Object result = superDao.unique(hql,values);
		return result != null ? ((Number) result).intValue() : 0;
	}

	@Override
	public PageList find(String sql, int start, int limit) {
		String hsql = "select b.* from user_recharge b, user u where b.user_id = u.id  ";
			PageList pageList = superDao.findPageEntityList(hsql+sql, UserRecharge.class,new HashMap<String, Object>(), start, limit);
			return pageList;
	}


}