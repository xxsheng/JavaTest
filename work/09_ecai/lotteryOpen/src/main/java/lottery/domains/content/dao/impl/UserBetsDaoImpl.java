package lottery.domains.content.dao.impl;

import javautils.array.ArrayUtils;
import javautils.jdbc.PageList;
import javautils.jdbc.hibernate.HibernateSuperDao;
import lottery.domains.content.dao.UserBetsDao;
import lottery.domains.content.entity.UserBets;
import lottery.domains.content.entity.UserBetsNoCode;
import org.apache.commons.collections.CollectionUtils;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Order;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
public class UserBetsDaoImpl implements UserBetsDao {

	private final String tab = UserBets.class.getSimpleName();
	private final String noCodetab = UserBetsNoCode.class.getSimpleName();

	@Autowired
	private HibernateSuperDao<UserBets> superDao;

	@Autowired
	private HibernateSuperDao<UserBetsNoCode> noCodeSuperDao;

	@Override
	public boolean add(UserBets userBets) {
		return superDao.save(userBets);
	}

	@Override
	public UserBets getById(int id) {
		String hql = "from " + tab + " where id = ?0";
		Object[] values = {id};
		return (UserBets) superDao.unique(hql, values);
	}

	@Override
	public UserBets getById(int id, int userId) {
		String hql = "from " + tab + " where id = ?0 and userId = ?1";
		Object[] values = {id, userId};
		return (UserBets) superDao.unique(hql, values);
	}

	@Override
	public List<UserBets> getByChaseBillno(String chaseBillno, int userId, String winExpect) {
		String hql = "from " + noCodetab + " where chaseBillno = ?0 and userId = ?1 and status=0 and type = 1 and expect > ?2 and id>0";
		Object[] values = {chaseBillno, userId, winExpect};
		// return superDao.list(hql, values);

		List<UserBetsNoCode> noCodeList = noCodeSuperDao.list(hql, values);
		List<UserBets> list = new ArrayList<>();
		for (UserBetsNoCode tmpBean : noCodeList) {
			list.add(tmpBean.formatBean());
		}
		return list;
	}

	@Override
	public List<UserBets> getByFollowBillno(String followBillno) {
		String hql = "from " + tab + " where type = 0 and status > 0 and planBillno = ?0";
		Object[] values = {followBillno};
		return superDao.list(hql, values);
	}

	@Override
	public boolean updateToPlan(int id, int type, String planBillno) {
		String hql = "update " + tab + " set type = ?1, planBillno = ?2 where id = ?0";
		Object[] values = {id, type, planBillno};
		return superDao.update(hql, values);
	}

	@Override
	public List<UserBets> list(List<Criterion> criterions, List<Order> orders) {
		return superDao.findByCriteria(UserBets.class, criterions, orders);
	}

	@Override
	public PageList search(List<Criterion> criterions, List<Order> orders,
						   int start, int limit) {
		String propertyName = "id";
		return superDao.findPageList(UserBets.class, propertyName, criterions, orders, start, limit);
	}

	@Override
	public boolean updateStatus(int id, int status, String openCode, double prizeMoney, String prizeTime) {
		String hql = "update " + tab + " set status = ?1, openCode = ?2, prizeMoney = ?3, prizeTime = ?4 where id = ?0";
		Object[] values = {id, status, openCode , prizeMoney, prizeTime};
		return superDao.update(hql, values);
	}

	@Override
	public boolean cancel(int id) {
		String hql = "update " + tab + " set status = -1 where id = ?0 and status = 0";
		Object[] values = {id};
		return superDao.update(hql, values);
	}

	@Override
	public boolean cancelAndSetOpenCode(int id, String openCode) {
		String hql = "update " + tab + " set status = -1, openCode = ?1 where id = ?0 and status = 0";
		Object[] values = {id, openCode};
		return superDao.update(hql, values);
	}

	@Override
	public List<?> sumUserProfitGroupByUserId(int lotteryId, String startTime, String endTime, List<Integer> userIds) {
		String hql = "select userId, sum(money), sum(prizeMoney) from " + tab + " where status in (1,2) and time >= ?0 and time <= ?1 and lotteryId = ?2 and id > 0";
		if (CollectionUtils.isNotEmpty(userIds)) {
			hql += " and userId in ("+ArrayUtils.toString(userIds)+")";
		}
		hql += " group by userId";
		Object[] values = {startTime, endTime, lotteryId};
		return superDao.listObject(hql, values);
	}

	@Override
	public double[] sumProfit(int lotteryId, String startTime, String endTime) {
		String hql = "select sum(money), sum(prizeMoney) from " + tab + " where status in (1,2) and time >= ?0 and time <= ?1 and lotteryId = ?2";
		Object[] values = {startTime, endTime, lotteryId};
		Object result = superDao.unique(hql, values);
		if (result == null) {
			return new double[]{0, 0};
		}

		Object[] results = (Object[]) result;
		double money = results[0] == null ? 0 : (Double) results[0];
		double prizeMoney = results[1] == null ? 0 : (Double) results[1];
		return new double[]{money, prizeMoney};
	}

	@Override
	public List<UserBets> getLatest(int userId, int lotteryId, int count) {
		String hql = "from " + tab + " where userId=?0 and lotteryId=?1 and status in (1,2) and id > 0 order by expect desc";
		Object[] values = {userId, lotteryId};
		List<UserBets> list = superDao.list(hql, values, 0, count);
		return list;
	}

	@Override
	public List<UserBets> getByExpect(int lotteryId, String expect) {
		String hql = "from " + tab + " where lotteryId=?0 and expect=?1 and status in (1,2) and id > 0";
		Object[] values = {lotteryId, expect};
		List<UserBets> list = superDao.list(hql, values);
		return list;
	}

	@Override
	public List<UserBets> getByUserIdAndExpect(int userId, int lotteryId, String expect) {
		String hql = "from " + tab + " where userId = ?0 and lotteryId=?1 and expect=?2 and status in (1,2) and id > 0";
		Object[] values = {userId, lotteryId, expect};
		List<UserBets> list = superDao.list(hql, values);
		return list;
	}

	@Override
	public List<UserBets> getNoDemoUserBetsByExpect(String hsql, Object[] values) {
		List<UserBets> list = superDao.list(hsql, values);
		return list;
	}
}