package lottery.domains.content.dao.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Order;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javautils.StringUtil;
import javautils.array.ArrayUtils;
import javautils.jdbc.PageList;
import javautils.jdbc.hibernate.HibernateSuperDao;
import lottery.domains.content.dao.UserBetsDao;
import lottery.domains.content.entity.HistoryUserBets;
import lottery.domains.content.entity.HistoryUserBetsNoCode;
import lottery.domains.content.entity.User;
import lottery.domains.content.entity.UserBets;
import lottery.domains.content.entity.UserBetsNoCode;

@Repository
public class UserBetsDaoImpl implements UserBetsDao {
	
	private final String tab = UserBets.class.getSimpleName();
	private final String utab = User.class.getSimpleName();
	private final String historyTab = HistoryUserBets.class.getSimpleName();
	
	private final String noCodeTab = UserBetsNoCode.class.getSimpleName();
	private final String historyNoCodeTab = HistoryUserBetsNoCode.class.getSimpleName();
	@Autowired
	private HibernateSuperDao<UserBets> superDao;
	
	@Autowired
	private HibernateSuperDao<UserBetsNoCode> noCodeSuperDao;
	
	@Autowired
	private HibernateSuperDao<HistoryUserBets> historySuperDao;
	
	@Autowired
	private HibernateSuperDao<HistoryUserBetsNoCode> historyNoCodeSuperDao;
	
	@Override
	public boolean updateStatus(int id, int status, String codes, String openCode, double prizeMoney, String prizeTime) {
		String hql = "update " + tab + " set status = ?1, codes = ?2, openCode = ?3, prizeMoney = ?4, prizeTime = ?5, locked = 0 where id = ?0";
		Object[] values = {id, status, codes, openCode , prizeMoney, prizeTime};
		return superDao.update(hql, values);
	}
	
	@Override
	public boolean updateLocked(int id, int locked) {
		String hql = "update " + tab + " set locked = ?1 where id = ?0";
		Object[] values = {id, locked};
		return superDao.update(hql, values);
	}
	
	@Override
	public UserBets getById(int id) {
		String hql = "from " + tab + " where id = ?0";
		Object[] values = {id};
		return (UserBets) superDao.unique(hql, values);
	}
	@Override
	public HistoryUserBets getHistoryById(int id) {
		String hql = "from " + historyTab + " where id = ?0";
		Object[] values = {id};
		return (HistoryUserBets) historySuperDao.unique(hql, values);
	}
	@Override
	public List<UserBets> getByBillno(String billno, boolean withCodes) {
		String targetTab = withCodes ? tab : noCodeTab;
		String hql = "from " + targetTab + " where billno = ?0";
		Object[] values = {billno};
		if(withCodes) {
			return superDao.list(hql, values);
		} else {
			List<UserBetsNoCode> noCodeList = noCodeSuperDao.list(hql, values);
			List<UserBets> list = new ArrayList<>();
			for (UserBetsNoCode tmpBean : noCodeList) {
				list.add(tmpBean.formatBean());
			}
			return list;
		}
	}
	@Override
	public List<HistoryUserBets> getHistoryByBillno(String billno, boolean withCodes) {
		String targetTab = withCodes ? historyTab : historyNoCodeTab;
		String hql = "from " + targetTab + " where billno = ?0";
		Object[] values = {billno};
		if(withCodes) {
			return historySuperDao.list(hql, values);
		} else {
			List<HistoryUserBetsNoCode> noCodeList = historyNoCodeSuperDao.list(hql, values);
			List<HistoryUserBets> list = new ArrayList<>();
			for (HistoryUserBetsNoCode tmpBean : noCodeList) {
				list.add(tmpBean.formatBean());
			}
			return list;
		}
	}
	@Override
	public UserBets getBybillno(int userId, String billno) {
		String hql = "from " + tab + " where userId = ?0 and billno = ?1";
		Object[] values = {userId, billno};
		return (UserBets) superDao.unique(hql, values);
	}
	
	@Override
	public boolean cancel(int id) {
		String hql = "update " + tab + " set status = -1 where id = ?0 and status = 0";
		Object[] values = {id};
		return superDao.update(hql, values);
	}
	
	@Override
	public boolean delete(int userId) {
		String hql = "delete from " + tab + " where userId = ?0";
		Object[] values = {userId};
		return superDao.delete(hql, values);
	}
	
	@Override
	public boolean isCost(int userId) {
		String hql = "select count(id) from " + tab + " where userId = ?0 and status > 0";
		Object[] values = {userId};
		Object result = superDao.unique(hql, values);
		return result != null ? ((Number) result).intValue() > 0 : false;
	}
	
	@Override
	public List<UserBets> getSuspiciousOrder(int userId, int multiple, boolean withCodes) {
		String targetTab = withCodes ? tab : noCodeTab;
		String hql = "from " + targetTab + " where userId = ?0 and status > 0 and prizeMoney >= money * ?1";
		Object[] values = {userId, multiple * 1.0};
		if(withCodes) {
			return superDao.list(hql, values);
		} else {
			List<UserBetsNoCode> noCodeList = noCodeSuperDao.list(hql, values);
			List<UserBets> list = new ArrayList<>();
			for (UserBetsNoCode tmpBean : noCodeList) {
				list.add(tmpBean.formatBean());
			}
			return list;
		}
	}
	
	@Override
	public List<UserBets> getByFollowBillno(String followBillno, boolean withCodes) {
		String targetTab = withCodes ? tab : noCodeTab;
		String hql = "from " + targetTab + " where type = 0 and status > 0 and planBillno = ?0";
		Object[] values = {followBillno};
		if(withCodes) {
			return superDao.list(hql, values);
		} else {
			List<UserBetsNoCode> noCodeList = noCodeSuperDao.list(hql, values);
			List<UserBets> list = new ArrayList<>();
			for (UserBetsNoCode tmpBean : noCodeList) {
				list.add(tmpBean.formatBean());
			}
			return list;
		}
	}
	
	@Override
	@Transactional(readOnly = true)
	public PageList find(String sql , int start, int limit, boolean withCodes) {
		String hsql = "select b.* from user_bets b, user u where b.user_id = u.id  ";
		if(withCodes) {
			PageList pageList = superDao.findPageEntityList(hsql+sql, UserBets.class,new HashMap<String, Object>(), start, limit);
			return pageList;
		} else {
			PageList pageList = noCodeSuperDao.findPageEntityList(hsql+sql, UserBetsNoCode.class,new HashMap<String, Object>(), start, limit);
			List<UserBets> list = new ArrayList<>();
			for (Object o : pageList.getList()) {
				
				UserBetsNoCode entity = (UserBetsNoCode) o;
				list.add(entity.formatBean());
			}
			pageList.setList(list);
			return pageList;
		}
	}
	
	@Override
	public List<UserBets> find(List<Criterion> criterions, List<Order> orders, boolean withCodes) {
		if(withCodes) {
			return superDao.findByCriteria(UserBets.class, criterions, orders);
		} else {
			List<UserBetsNoCode> noCodeList = noCodeSuperDao.findByCriteria(UserBetsNoCode.class, criterions, orders);
			List<UserBets> list = new ArrayList<>();
			for (UserBetsNoCode tmpBean : noCodeList) {
				list.add(tmpBean.formatBean());
			}
			return list;
		}
	}

	@Override
	public long getTotalBetsMoney(String sTime, String eTime) {
		String hql = "select sum(b.money) from " + tab + "   b  ," + utab + "   u   where  u.id = b.userId   and  b.status >= 0 and b.time >= ?0 and b.time < ?1 and u.upid !=?2";
		Object[] values = {sTime, eTime,0};
		Object result = superDao.unique(hql, values);
		if(result != null) {
			return ((Number) result).longValue();
		}
		return 0;
	}
	
	@Override
	public int getTotalOrderCount(String sTime, String eTime) {
		String hql = "select count(b.id) from  " + tab + "   b  ," + utab + "   u   where  u.id = b.userId and b.status >= 0 and b.time >= ?0 and b.time < ?1 and u.upid !=?2";
		Object[] values = {sTime, eTime,0};
		Object result = superDao.unique(hql, values);
		if(result != null) {
			return ((Number) result).intValue();
		}
		return 0;
	}
	
	@Override
	public List<?> getDayUserBets(int[] lids, String sTime, String eTime) {
		String hql = "select substring(b.time, 1, 10), count(b.id) from " + tab + "   b  ," + utab + "   u   where  u.id = b.userId  and  b.status >= 0 and b.time >= ?0 and b.time < ?1 and u.upid !=?2";
		if(lids != null && lids.length > 0) {
			String ids = ArrayUtils.transInIds(lids);
			hql += " and b.lotteryId in (" + ids + ")";
		}
		hql += " group by substring(b.time, 1, 10)";
		Object[] values = {sTime, eTime,0};
		return superDao.listObject(hql, values);
	}

	@Override
	public List<?> getDayBetsMoney(int[] lids, String sTime, String eTime) {
		String hql = "select substring(b.time, 1, 10), sum(b.money) from " + tab + "   b  ," + utab + "   u   where  u.id = b.userId  and b.status >= 0 and b.time >= ?0 and b.time < ?1  and u.upid !=?2";
		if(lids != null && lids.length > 0) {
			String ids = ArrayUtils.transInIds(lids);
			hql += " and b.lotteryId in (" + ids + ")";
		}
		hql += " group by substring(b.time, 1, 10)";
		Object[] values = {sTime, eTime,0};
		return superDao.listObject(hql, values);
	}

	@Override
	public List<?> getDayPrizeMoney(int[] lids, String sTime, String eTime) {
		String hql = "select substring(b.time, 1, 10), sum(b.prizeMoney) from " + tab + "   b  ," + utab + "   u   where  u.id = b.userId  and  b.status = 2 and b.time >= ?0 and b.time < ?1 and u.upid !=?2";
		if(lids != null && lids.length > 0) {
			String ids = ArrayUtils.transInIds(lids);
			hql += " and b.lotteryId in (" + ids + ")";
		}
		hql += " group by substring(b.time, 1, 10)";
		Object[] values = {sTime, eTime,0};
		return superDao.listObject(hql, values);
	}
	
	@Override
	public List<?> getLotteryHot(int[] lids, String sTime, String eTime) {
		String hql = "select lotteryId, count(id) from " + tab + " where time >= ?0 and time < ?1";
		if(lids != null && lids.length > 0) {
			String ids = ArrayUtils.transInIds(lids);
			hql += " and lotteryId in (" + ids + ")";
		}
		hql += " group by lotteryId";
		Object[] values = {sTime, eTime};
		return superDao.listObject(hql, values);
	}
	
	@Override
	public List<?> report(List<Integer> lids, Integer ruleId, String sTime, String eTime) {
		String hql = "select substring(b.prizeTime, 1, 10), sum(b.money), sum(((13.0-(b.point*20+b.code-1700)/20))*b.money)/100, sum(b.prizeMoney) from " + tab + "    b  ," + utab + "   u   where  u.id = b.userId where b.status > 0 and u.upid !=?2";
		if(lids != null && lids.size() > 0) {
			hql += " and b.lotteryId in (" + ArrayUtils.transInIds(lids) + ")";
		}
		if(ruleId != null) {
			hql += " and b.ruleId = '" + ruleId + "'";
		}
		hql += " and b.prizeTime >= ?0 and b.prizeTime < ?1 group by substring(b.prizeTime, 1, 10)";
		Object[] values = {sTime, eTime,0};
		return superDao.listObject(hql, values);
	}
	

	@Override
	public int countUserOnline(String time) {
		String hql = "select count(*) as ucount from (select id from  user_bets where time > '"+time+"'  GROUP BY user_id ) as ubet;";
		Object result =  superDao.uniqueWithSqlCount(hql);
		return result != null ? ((Number) result).intValue() : 0;
	}

	@Override
	public double[] getTotalMoney(String keyword, Integer userId, Integer utype,Integer type, Integer lotteryId, String expect, Integer ruleId, String minTime,
								  String maxTime, String minPrizeTime, String maxPrizeTime, Double minMoney, Double maxMoney, Integer minMultiple,
								  Integer maxMultiple, Double minPrizeMoney, Double maxPrizeMoney, Integer status, Integer locked, String ip) {
		String hql = "select sum(b.money), sum(b.prizeMoney) from " + tab + " b , " + utab + "  u  where b.userId = u.id ";

		Map<String, Object> params = new HashMap<>();
		if (StringUtil.isNotNull(keyword)) {
			if (StringUtil.isInteger(keyword)) {
				hql += " and (b.id = :id  or b.billno like :billno or b.chaseBillno like :chaseBillno)";
				params.put("id", Integer.valueOf(keyword));
				params.put("billno", "%" + keyword + "%");
				params.put("chaseBillno", "%" + keyword + "%");
			}
			else {
				hql += " and (b.billno like :billno or b.chaseBillno like :chaseBillno)";
				params.put("billno", "%" + keyword + "%");
				params.put("chaseBillno", "%" + keyword + "%");
			}
		}
		if (userId != null) {
			hql += " and b.userId = :userId";
			params.put("userId", userId);
		}
		if(type != null) {
			hql += " and b.type = :type";
			params.put("type", type);
		}
		
		if(utype != null){
			hql += " and u.type = :utype";
			params.put("utype", utype);
		}else{
			hql += " and u.upid != 0";
		}
		
		if(lotteryId != null) {
			hql += " and b.lotteryId = :lotteryId";
			params.put("lotteryId", lotteryId);
		}
		if(StringUtil.isNotNull(expect)) {
			hql += " and b.expect like :expect";
			params.put("expect", expect);
		}
		if(ruleId != null) {
			hql += " and b.ruleId = :ruleId";
			params.put("ruleId", ruleId);
		}
		if(StringUtil.isNotNull(minTime)) {
			hql += " and b.time > :minTime";
			params.put("minTime", minTime);
		}
		if(StringUtil.isNotNull(maxTime)) {
			hql += " and b. time < :maxTime";
			params.put("maxTime", maxTime);
		}
		if(StringUtil.isNotNull(minPrizeTime)) {
			hql += " and b.prizeTime > :minPrizeTime";
			params.put("minPrizeTime", minPrizeTime);
		}
		if(StringUtil.isNotNull(maxPrizeTime)) {
			hql += " and b.prizeTime < :maxPrizeTime";
			params.put("maxPrizeTime", maxPrizeTime);
		}
		if(minMoney != null) {
			hql += " and b.money >= :minMoney";
			params.put("minMoney", minMoney);
		}
		if(maxMoney != null) {
			hql += " and b.money <= :maxMoney";
			params.put("maxMoney", maxMoney);
		}
		if(minMultiple  != null) {
			hql += " and b.multiple >= :minMultiple";
			params.put("minMultiple", minMultiple);
		}
		if(maxMultiple  != null) {
			hql += " and b.multiple <= :maxMultiple";
			params.put("maxMultiple", maxMultiple);
		}
		if(minPrizeMoney != null) {
			hql += " and b.prizeMoney >= :minPrizeMoney";
			params.put("minPrizeMoney", minPrizeMoney);
		}
		if(maxPrizeMoney != null) {
			hql += " and b.prizeMoney <= :maxPrizeMoney";
			params.put("maxPrizeMoney", maxPrizeMoney);
		}
		if(status != null) {
			hql += " and b.status = :status";
			params.put("status", status);
		}
		if(locked != null) {
			hql += " and b.locked = :locked";
			params.put("locked", locked);
		}
		if(StringUtils.isNotEmpty(ip)) {
			hql += " and b.ip = :ip";
			params.put("ip", ip);
		}
		Object result = superDao.uniqueWithParams(hql, params);
		if (result == null) {
			return new double[]{0, 0};
		}
		Object[] results = (Object[]) result;
		double totalMoney = results[0] == null ? 0 : (Double) results[0];
		double totalPrizeMoney = results[1] == null ? 0 : (Double) results[1];
		return new double[]{totalMoney, totalPrizeMoney};
	}
	
	@Override
	public double[] getHistoryTotalMoney(String keyword, Integer userId, Integer utype,Integer type, Integer lotteryId, String expect, Integer ruleId, String minTime,
								  String maxTime, String minPrizeTime, String maxPrizeTime, Double minMoney, Double maxMoney, Integer minMultiple,
								  Integer maxMultiple, Double minPrizeMoney, Double maxPrizeMoney, Integer status, Integer locked) {
		String hql = "select sum(b.money), sum(b.prizeMoney) from " + historyTab + " b , " + utab + "  u  where b.userId = u.id ";

		Map<String, Object> params = new HashMap<>();
		if (StringUtil.isNotNull(keyword)) {
			if (StringUtil.isInteger(keyword)) {
				hql += " and (b.id = :id  or b.billno like :billno or b.chaseBillno like :chaseBillno)";
				params.put("id", Integer.valueOf(keyword));
				params.put("billno", "%" + keyword + "%");
				params.put("chaseBillno", "%" + keyword + "%");
			}
			else {
				hql += " and (b.billno like :billno or b.chaseBillno like :chaseBillno)";
				params.put("billno", "%" + keyword + "%");
				params.put("chaseBillno", "%" + keyword + "%");
			}
		}
		if (userId != null) {
			hql += " and b.userId = :userId";
			params.put("userId", userId);
		}
		if(type != null) {
			hql += " and b.type = :type";
			params.put("type", type);
		}
		if(utype != null){
			hql += " and u.type = :utype";
			params.put("utype", utype);
		}else{
			hql += " and u.upid !=0";
		}
		
		if(lotteryId != null) {
			hql += " and b.lotteryId = :lotteryId";
			params.put("lotteryId", lotteryId);
		}
		if(StringUtil.isNotNull(expect)) {
			hql += " and b.expect like :expect";
			params.put("expect", expect);
		}
		if(ruleId != null) {
			hql += " and b.ruleId = :ruleId";
			params.put("ruleId", ruleId);
		}
		if(StringUtil.isNotNull(minTime)) {
			hql += " and b.time > :minTime";
			params.put("minTime", minTime);
		}
		if(StringUtil.isNotNull(maxTime)) {
			hql += " and b.time < :maxTime";
			params.put("maxTime", maxTime);
		}
		if(StringUtil.isNotNull(minPrizeTime)) {
			hql += " and b.prizeTime > :minPrizeTime";
			params.put("minPrizeTime", minPrizeTime);
		}
		if(StringUtil.isNotNull(maxPrizeTime)) {
			hql += " and b.prizeTime < :maxPrizeTime";
			params.put("maxPrizeTime", maxPrizeTime);
		}
		if(minMoney != null) {
			hql += " and b.money >= :minMoney";
			params.put("minMoney", minMoney);
		}
		if(maxMoney != null) {
			hql += " and b.money <= :maxMoney";
			params.put("maxMoney", maxMoney);
		}
		if(minMultiple  != null) {
			hql += " and b.multiple >= :minMultiple";
			params.put("minMultiple", minMultiple);
		}
		if(maxMultiple  != null) {
			hql += " and b.multiple <= :maxMultiple";
			params.put("maxMultiple", maxMultiple);
		}
		if(minPrizeMoney != null) {
			hql += " and b.prizeMoney >= :minPrizeMoney";
			params.put("minPrizeMoney", minPrizeMoney);
		}
		if(maxPrizeMoney != null) {
			hql += " and b.prizeMoney <= :maxPrizeMoney";
			params.put("maxPrizeMoney", maxPrizeMoney);
		}
		if(status != null) {
			hql += " and b.status = :status";
			params.put("status", status);
		}
		if(locked != null) {
			hql += " and b.locked = :locked";
			params.put("locked", locked);
		}
		Object result = historySuperDao.uniqueWithParams(hql, params);
		if (result == null) {
			return new double[]{0, 0};
		}
		Object[] results = (Object[]) result;
		double totalMoney = results[0] == null ? 0 : (Double) results[0];
		double totalPrizeMoney = results[1] == null ? 0 : (Double) results[1];
		return new double[]{totalMoney, totalPrizeMoney};
	}
	
	@Override
	public PageList findHistory(List<Criterion> criterions, List<Order> orders, int start, int limit,
			boolean withCodes) {
		String propertyName = "id";
		if(withCodes) {
			return historySuperDao.findPageList(HistoryUserBets.class, propertyName, criterions, orders, start, limit);
		} else {
			PageList pageList = historyNoCodeSuperDao.findPageList(HistoryUserBetsNoCode.class, propertyName, criterions, orders, start, limit);
			List<HistoryUserBets> list = new ArrayList<>();
			for (Object o : pageList.getList()) {
				HistoryUserBetsNoCode entity = (HistoryUserBetsNoCode) o;
				list.add(entity.formatBean());
			}
			pageList.setList(list);
			return pageList;
		}
	}
	
	@Override
	public double getBillingOrder(int userId, String startTime, String endTime) {
		String hql = "select sum(money) from " + tab + " where userId = ?0 and prizeTime >= ?1 and prizeTime < ?2 and status > 0 and id > 0";
		Object[] values = {userId, startTime, endTime};
		Object result = superDao.unique(hql, values);
		return result != null ? ((Number) result).doubleValue() : 0;
	}

}