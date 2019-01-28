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
import lottery.domains.content.dao.UserWithdrawDao;
import lottery.domains.content.entity.HistoryUserWithdraw;
import lottery.domains.content.entity.User;
import lottery.domains.content.entity.UserWithdraw;

@Repository
public class UserWithdrawDaoImpl implements UserWithdrawDao {
	
	private final String tab = UserWithdraw.class.getSimpleName();
	
	private final String user= User.class.getSimpleName();
	
	private final String historyTab = HistoryUserWithdraw.class.getSimpleName();
	
	@Autowired
	private HibernateSuperDao<UserWithdraw> superDao;
	
	@Autowired
	private HibernateSuperDao<HistoryUserWithdraw> historySuperDao;
	
	@Override
	public boolean update(UserWithdraw entity) {
		return superDao.update(entity);
	}

	@Override
	public int getWaitTodo() {
		String hql = "select count(id) from " + tab + " where status = 0";
		Object result = superDao.unique(hql);
		return result != null ? ((Number) result).intValue() : 0;
	}
	
	@Override
	public UserWithdraw getById(int id) {
		String hql = "from " + tab + " where id = ?0";
		Object[] values = {id};
		return (UserWithdraw) superDao.unique(hql, values);
	}
	
	@Override
	public HistoryUserWithdraw getHistoryById(int id) {
		String hql = "from " + historyTab + " where id = ?0";
		Object[] values = {id};
		return (HistoryUserWithdraw) historySuperDao.unique(hql, values);
	}

	@Override
	public UserWithdraw getByBillno(String billno) {
		String hql = "from " + tab + " where billno = ?0";
		Object[] values = {billno};
		return (UserWithdraw) superDao.unique(hql, values);
	}
	
	@Override
	public List<UserWithdraw> listByOperatorTime(String sDate, String eDate) {
		String hql = "from " + tab + " where status = 1 and operatorTime >= ?0 and operatorTime < ?1 order by operatorTime desc,id desc";
		Object[] values = {sDate, eDate};
		return superDao.list(hql, values);
	}

	@Override
	public List<UserWithdraw> listByRemitStatus(int[] remitStatuses, boolean third, String sTime, String eTime) {
		String hql = "from " + tab + " where remitStatus in ("+ ArrayUtils.transInIds(remitStatuses)+") and operatorTime >= ?0 and operatorTime < ?1";
		if (third) {
			hql += " and paymentChannelId is not null and paymentChannelId > 0";
		}
		Object[] values = {sTime, eTime};
		return superDao.list(hql, values);
	}
	
	@Override
	public List<UserWithdraw> getLatest(int userId, int status, int count) {
		String hql = "from " + tab + " where userId = ?0 and status = ?1 order by id desc";
		Object[] values = {userId, status};
		return superDao.list(hql, values, 0, count);
	}
	
	public PageList find(String sql,int start, int limit) {
		String hsql = "select b.* from user_withdraw b, user u where b.user_id = u.id  ";
		return superDao.findPageEntityList(hsql+sql, UserWithdraw.class, new HashMap<String, Object>(), start, limit);
	}

	public PageList find(List<Criterion> criterions, List<Order> orders,
			int start, int limit) {
		String propertyName = "id";
		return superDao.findPageList(UserWithdraw.class, propertyName, criterions, orders, start, limit);
	}
	
	@Override
	public PageList findHistory(List<Criterion> criterions, List<Order> orders,
			int start, int limit) {
		String propertyName = "id";
		return historySuperDao.findPageList(HistoryUserWithdraw.class, propertyName, criterions, orders, start, limit);
	}
	
	@Override
	public double getTotalWithdraw(String sTime, String eTime) {
		String hql = "select sum(money) from " + tab + " where status = 1 and operatorTime >= ?0 and operatorTime < ?1";
		Object[] values = {sTime, eTime};
		Object result = superDao.unique(hql, values);
		return result != null ? ((Number) result).doubleValue() : 0;
	}

	@Override
	public Object[] getTotalWithdrawData(String sTime, String eTime) {
		String hql = "select count(b.id), sum(b.money) from " + tab + " as b  , "+user+" as u  where u.id = b.userId and u.upid !=0 and  b.status = 1 and b.operatorTime >= :sTime and b.operatorTime < :eTime";
		Map<String, Object> params = new HashMap<>();
		params.put("sTime", sTime);
		params.put("eTime", eTime);
		// 总提款订单数/总提款金额
		Object result = superDao.uniqueWithParams(hql, params);
		if (result == null) {
			return new Object[]{0, 0.0};
		}
		Object[] results = (Object[]) result;

		Object result1 = results[0] == null ? 0 : results[0];
		Object result2 = results[1] == null ? 0.00 : results[1];

		return new Object[]{result1, result2};
	}

	@Override
	public double[] getTotalWithdraw(String billno, Integer userId, String minTime, String maxTime, String minOperatorTime, String maxOperatorTime,
									 Double minMoney, Double maxMoney, String keyword, Integer status, Integer checkStatus, Integer remitStatus, Integer paymentChannelId) {
		String hql = "select sum(recMoney), sum(feeMoney) from " + tab + " where 1=1";

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
		if (StringUtil.isNotNull(minOperatorTime)) {
			hql += " and operatorTime > :minOperatorTime";
			params.put("minOperatorTime", minOperatorTime);
		}
		if (StringUtil.isNotNull(maxOperatorTime)) {
			hql += " and operatorTime < :maxOperatorTime";
			params.put("maxOperatorTime", maxOperatorTime);
		}
		if (minMoney != null) {
			hql += " and money >= :minMoney";
			params.put("minMoney", minMoney);
		}
		if (maxMoney != null) {
			hql += " and money <= :maxMoney";
			params.put("maxMoney", maxMoney);
		}
		if (StringUtil.isNotNull(keyword)) {
			hql += " and (cardName like :cardName or cardId like :cardId)";
			params.put("cardName", "%" + keyword + "%");
			params.put("cardId", "%" + keyword + "%");
		}
		if (status != null) {
			hql += " and status = :status";
			params.put("status", status);
		}
		if (checkStatus != null) {
			hql += " and checkStatus = :checkStatus";
			params.put("checkStatus", checkStatus);
		}
		if (remitStatus != null) {
			hql += " and remitStatus = :remitStatus";
			params.put("remitStatus", remitStatus);
		}
		if (paymentChannelId != null) {
			hql += " and paymentChannelId = :paymentChannelId";
			params.put("paymentChannelId", paymentChannelId);
		}
		Object result = superDao.uniqueWithParams(hql, params);
		if (result == null) {
			return new double[]{0, 0};
		}
		Object[] results = (Object[]) result;
		double totalRecMoney = results[0] == null ? 0 : (Double) results[0];
		double totalFeeMoney = results[1] == null ? 0 : (Double) results[1];
		return new double[]{totalRecMoney, totalFeeMoney};
	}

	@Override
	public double[] getHistoryTotalWithdraw(String billno, Integer userId, String minTime, String maxTime, String minOperatorTime, String maxOperatorTime,
									 Double minMoney, Double maxMoney, String keyword, Integer status, Integer checkStatus, Integer remitStatus, Integer paymentChannelId) {
		String hql = "select sum(recMoney), sum(feeMoney) from " + historyTab + " where 1=1";

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
		if (StringUtil.isNotNull(minOperatorTime)) {
			hql += " and operatorTime > :minOperatorTime";
			params.put("minOperatorTime", minOperatorTime);
		}
		if (StringUtil.isNotNull(maxOperatorTime)) {
			hql += " and operatorTime < :maxOperatorTime";
			params.put("maxOperatorTime", maxOperatorTime);
		}
		if (minMoney != null) {
			hql += " and money >= :minMoney";
			params.put("minMoney", minMoney);
		}
		if (maxMoney != null) {
			hql += " and money <= :maxMoney";
			params.put("maxMoney", maxMoney);
		}
		if (StringUtil.isNotNull(keyword)) {
			hql += " and (cardName like :cardName or cardId like :cardId)";
			params.put("cardName", "%" + keyword + "%");
			params.put("cardId", "%" + keyword + "%");
		}
		if (status != null) {
			hql += " and status = :status";
			params.put("status", status);
		}
		if (checkStatus != null) {
			hql += " and checkStatus = :checkStatus";
			params.put("checkStatus", checkStatus);
		}
		if (remitStatus != null) {
			hql += " and remitStatus = :remitStatus";
			params.put("remitStatus", remitStatus);
		}
		if (paymentChannelId != null) {
			hql += " and paymentChannelId = :paymentChannelId";
			params.put("paymentChannelId", paymentChannelId);
		}
		Object result = superDao.uniqueWithParams(hql, params);
		if (result == null) {
			return new double[]{0, 0};
		}
		Object[] results = (Object[]) result;
		double totalRecMoney = results[0] == null ? 0 : (Double) results[0];
		double totalFeeMoney = results[1] == null ? 0 : (Double) results[1];
		return new double[]{totalRecMoney, totalFeeMoney};
	}
	
	@Override
	public double getTotalAutoRemit(String sTime, String eTime) {
		String hql = "select sum(money) from " + tab + " where status = 1 and remitStatus = 2 and operatorTime >= ?0 and operatorTime < ?1";
		Object[] values = {sTime, eTime};
		Object result = superDao.unique(hql, values);
		return result != null ? ((Number) result).doubleValue() : 0;
	}
	
	@Override
	public double getTotalFee(String sTime, String eTime) {
		String hql = "select sum(feeMoney) from " + tab + " where status = 1 and operatorTime >= ?0 and operatorTime < ?1";
		Object[] values = {sTime, eTime};
		Object result = superDao.unique(hql, values);
		return result != null ? ((Number) result).doubleValue() : 0;
	}
	
	@Override
	public List<?> getDayWithdraw(String sTime, String eTime) {
		String hql = "select substring(operatorTime, 1, 10), sum(money) from " + tab + " where status = 1 and operatorTime >= ?0 and operatorTime < ?1 group by substring(operatorTime, 1, 10)";
		Object[] values = {sTime, eTime};
		return superDao.listObject(hql, values);
	}

	@Override
	public List<?> getDayWithdraw2(String sTime, String eTime) {
		String hql = "select substring(b.operatorTime, 1, 10), count(b.id), sum(b.money) from " + tab + " as b , " + user + " as u  where  b.userId = u.id and u.upid != 0 and b.status = 1 and b.operatorTime >= ?0 and b.operatorTime < ?1 group by substring(b.operatorTime, 1, 10)";
		Object[] values = {sTime, eTime};
		return superDao.listObject(hql, values);
	}

	@Override
	public boolean lock(String billno, String operatorUser, String operatorTime) {
		String hql = "update " + tab + " set lockStatus = 1, operatorUser = ?1, operatorTime = ?2 where billno = ?0 and lockStatus = 0";
		Object[] values = {billno, operatorUser, operatorTime};
		return superDao.update(hql, values);
	}
	
	@Override
	public boolean unlock(String billno, String operatorUser) {
		String hql = "update " + tab + " set lockStatus = 0, operatorUser = null, operatorTime = null where billno = ?0 and operatorUser = ?1";
		Object[] values = {billno, operatorUser};
		return superDao.update(hql, values);
	}

}