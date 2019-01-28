package lottery.domains.content.dao.impl;

import javautils.jdbc.PageList;
import javautils.jdbc.hibernate.HibernateSuperDao;
import lottery.domains.content.dao.UserDividendBillDao;
import lottery.domains.content.entity.UserDividendBill;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Order;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class UserDividendBillDaoImpl implements UserDividendBillDao  {
	private final String tab = UserDividendBill.class.getSimpleName();

	@Autowired
	private HibernateSuperDao<UserDividendBill> superDao;

	@Override
	public PageList search(List<Criterion> criterions, List<Order> orders, int start, int limit) {
		String propertyName = "id";
		return superDao.findPageList(UserDividendBill.class, propertyName, criterions, orders, start, limit);
	}

	@Override
	public double[] sumUserAmount(List<Integer> userIds, String sTime, String eTime, Double minUserAmount, Double maxUserAmount) {
		String hql = "select sum(case when issueType = 1 and status in (1,2,3,6) and totalLoss < 0 then totalLoss else 0 end), sum(case when issueType = 1 and status in (1,2,3,6) then calAmount else 0 end),sum(case when issueType = 2 and status in (1,2,3,6,7) then calAmount else 0 end) from " + tab + " where 1=1";

		Map<String, Object> params = new HashMap<>();
		if (CollectionUtils.isNotEmpty(userIds)) {
			hql += " and userId in :userId";
			params.put("userId", userIds);
		}
		if (StringUtils.isNotEmpty(sTime)) {
			hql += " and indicateStartDate >= :sTime";
			params.put("sTime", sTime);
		}
		if (StringUtils.isNotEmpty(eTime)) {
			hql += " and indicateEndDate <= :eTime";
			params.put("eTime", eTime);
		}
		if (minUserAmount != null) {
			hql += " and userAmount >= :minUserAmount";
			params.put("minUserAmount", minUserAmount);
		}
		if (maxUserAmount != null) {
			hql += " and userAmount <= :maxUserAmount";
			params.put("maxUserAmount", maxUserAmount);
		}
		Object result = superDao.uniqueWithParams(hql, params);
		if (result == null) {
			return new double[]{0, 0};
		}
		Object[] results = (Object[]) result;
		int index = 0;
		double totalLoss = results[index] == null ? 0 : (Double) results[index]; index++;
		double platformTotalUserAmount = results[index] == null ? 0 : (Double) results[index]; index++;
		double upperTotalUserAmount = results[index] == null ? 0 : (Double) results[index]; index++;
		return new double[]{totalLoss, platformTotalUserAmount, upperTotalUserAmount};
	}

	@Override
	public List<UserDividendBill> findByCriteria(List<Criterion> criterions, List<Order> orders) {
		return superDao.findByCriteria(UserDividendBill.class, criterions, orders);
	}

	@Override
	public boolean updateAllExpire() {
		String hql = "update " + tab + " set status = 8 where status in (3, 6, 7)";
		Object[] values = {};
		return superDao.update(hql, values);
	}

	@Override
	public UserDividendBill getById(int id) {
		String hql = "from " + tab + " where id = ?0";
		Object[] values = {id};
		return (UserDividendBill) superDao.unique(hql, values);
	}

	@Override
	public UserDividendBill getByUserId(int userId, String indicateStartDate, String indicateEndDate) {
		String hql = "from " + tab + " where userId = ?0 and indicateStartDate = ?1 and indicateEndDate = ?2";
		Object[] values = {userId, indicateStartDate, indicateEndDate};
		return (UserDividendBill) superDao.unique(hql, values);
	}

	@Override
	public boolean add(UserDividendBill dividendBill) {
		return superDao.save(dividendBill);
	}

	@Override
	public boolean addAvailableMoney(int id, double money) {
		String hql = "update " + tab + " set availableAmount = availableAmount+?1 where id = ?0";
		Object[] values = {id, money};
		return superDao.update(hql, values);
	}

	@Override
	public boolean addUserAmount(int id, double money) {
		String hql = "update " + tab + " set userAmount = userAmount+?1 where id = ?0";
		Object[] values = {id, money};
		return superDao.update(hql, values);
	}

	@Override
	public boolean setAvailableMoney(int id, double money) {
		String hql = "update " + tab + " set availableAmount = ?1 where id = ?0";
		Object[] values = {id, money};
		return superDao.update(hql, values);
	}

	@Override
	public boolean addTotalReceived(int id, double money) {
		String hql = "update " + tab + " set totalReceived = totalReceived+?1 where id = ?0";
		Object[] values = {id, money};
		return superDao.update(hql, values);
	}

	@Override
	public boolean addLowerPaidAmount(int id, double money) {
		String hql = "update " + tab + " set lowerPaidAmount = lowerPaidAmount+?1 where id = ?0";
		Object[] values = {id, money};
		return superDao.update(hql, values);
	}

	@Override
	public boolean update(UserDividendBill dividendBill) {
		return superDao.update(dividendBill);
	}

	@Override
	public boolean update(int id, int status, String remarks) {
		String hql = "update " + tab + " set status = ?1, remarks = ?2 where id = ?0";
		Object[] values = {id, status, remarks};
		return superDao.update(hql, values);
	}

	@Override
	public boolean update(int id, int status, double availableAmount, String remarks) {
		String hql = "update " + tab + " set status = ?1, availableAmount = ?2, remarks = ?3 where id = ?0";
		Object[] values = {id, status, availableAmount, remarks};
		return superDao.update(hql, values);
	}

	@Override
	public boolean del(int id) {
		String hql = "delete from " + tab + " where id = ?0";
		Object[] values = {id};
		return superDao.delete(hql, values);
	}

	@Override
	public boolean updateStatus(int id, int status) {
		String hql = "update " + tab + " set status = ?1 where id = ?0";
		Object[] values = {id, status};
		return superDao.update(hql, values);
	}

	@Override
	public boolean updateStatus(int id, int status, String remarks) {
		String hql = "update " + tab + " set status = ?1, remarks = ?2 where id = ?0";
		Object[] values = {id, status, remarks};
		return superDao.update(hql, values);
	}
}