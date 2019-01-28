package lottery.domains.content.dao.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Order;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javautils.StringUtil;
import javautils.jdbc.PageList;
import javautils.jdbc.hibernate.HibernateSuperDao;
import lottery.domains.content.dao.UserBetsOriginalDao;
import lottery.domains.content.entity.User;
import lottery.domains.content.entity.UserBetsOriginal;

@Repository
public class UserBetsOriginalDaoImpl implements UserBetsOriginalDao {
	
	private final String tab = UserBetsOriginal.class.getSimpleName();
	private final String utab = User.class.getSimpleName();
	

	@Autowired
	private HibernateSuperDao<UserBetsOriginal> superDao;

	@Override
	public UserBetsOriginal getById(int id) {
		String hql = "from " + tab + " where id = ?0";
		Object[] values = {id};
		return (UserBetsOriginal) superDao.unique(hql, values);
	}

	@Override
	public PageList find(List<Criterion> criterions, List<Order> orders, int start, int limit) {
		String propertyName = "id";
		return superDao.findPageList(UserBetsOriginal.class, propertyName, criterions, orders, start, limit);
	}

	@Override
	public double[] getTotalMoney(String keyword, Integer userId,  Integer utype,Integer type, Integer lotteryId, String expect, Integer ruleId, String minTime,
								  String maxTime, String minPrizeTime, String maxPrizeTime, Double minMoney, Double maxMoney, Integer minMultiple,
								  Integer maxMultiple, Double minPrizeMoney, Double maxPrizeMoney, Integer status) {
		String hql = "select sum(b.money), sum(b.prizeMoney) from " + tab + " b , " + utab + "  u  where b.userId = u.id ";


		Map<String, Object> params = new HashMap<>();
		if (StringUtil.isNotNull(keyword)) {
			if (StringUtil.isInteger(keyword)) {
				hql += " and (b.id = :id  or b.billno like :billno or b.chaseBillno like :chaseBillno)";
				params.put("b.id", Integer.valueOf(keyword));
				params.put("b.billno", "%" + keyword + "%");
				params.put("b.chaseBillno", "%" + keyword + "%");
			}
			else {
				hql += " and (b.billno like :billno or b.chaseBillno like :chaseBillno)";
				params.put("billno", "%" + keyword + "%");
				params.put("chaseBillno", "%" + keyword + "%");
			}
		}
		if (userId != null) {
			hql += " and b.userId = :userId";
			params.put("b.userId", userId);
		}
		if(type != null) {
			hql += " and b.type = :type";
			params.put("type", type);
		}
		
		if(utype != null){
			hql +="  and u.type ="+utype;
		}else{
			hql+="  and u.upid != 0";
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
	public PageList find(String sql, int start, int limit) {
		String hsql = "select b.* from user_bets_original b, user u where b.user_id = u.id  ";
		PageList pageList = superDao.findPageEntityList(hsql+sql, UserBetsOriginal.class,new HashMap<String, Object>(), start, limit);
		return pageList;
	
	}
}