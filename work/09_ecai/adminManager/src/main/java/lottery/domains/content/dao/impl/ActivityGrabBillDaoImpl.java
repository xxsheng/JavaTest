package lottery.domains.content.dao.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javautils.StringUtil;
import javautils.jdbc.PageList;
import javautils.jdbc.hibernate.HibernateSuperDao;

import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Order;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import lottery.domains.content.dao.ActivityGrabBillDao;
import lottery.domains.content.entity.ActivityGrabBill;

@Repository
public class ActivityGrabBillDaoImpl implements ActivityGrabBillDao {
	
	private final String tab = ActivityGrabBill.class.getSimpleName();
	
	@Autowired
	private HibernateSuperDao<ActivityGrabBill> superDao;

	@Override
	public boolean add(ActivityGrabBill entity) {
		return superDao.save(entity);
	}

	/**
	 * 查询当天记录
	 */
	@Override
	public ActivityGrabBill get(int userId) {
		String hql = "from " + tab + " where userId = ?0 and TO_DAYS(time) = TO_DAYS(current_timestamp())";
		Object[] values = {userId};
		List<ActivityGrabBill> list = superDao.list(hql, values);
		if(list != null && list.size() > 0) {
			return list.get(0);
		}
		return null;
	}
	
	/**
	 * 根据红包金额和时间分组
	 */
	@SuppressWarnings("unchecked")
	@Override
	public Map<String,Integer> getByPackageGroup() {
		String sql = "select  package, count(*) from activity_grab_bill  where TO_DAYS(time) = TO_DAYS(current_timestamp()) GROUP BY package";
		List<Object []> result = (List<Object []>) superDao.findWithSql(sql);
		Map<String,Integer> data = new HashMap<String, Integer>();
		for (Object[] objects : result) {
			data.put(objects[0].toString(), Integer.parseInt(objects[1].toString()));
		}
		return data;
	}

	@Override
	public PageList find(List<Criterion> criterions, List<Order> orders,
			int start, int limit) {
		return superDao.findPageList(ActivityGrabBill.class, criterions, orders, start, limit);
	}

	@Override
	public Double getOutAmount(String date) {
		String hql = "select SUM(packageMoney) from " + tab;
		if(StringUtil.isNotNull(date)){
			hql += " where TO_DAYS('"+date+"') = TO_DAYS(time)";
		}
		Object result = superDao.unique(hql);
		return  result != null ? Double.parseDouble(result.toString()) : null;
	}

	@Override
	public double getGrabTotal(String sTime, String eTime) {
		String hql = "select sum(packageMoney) from " + tab + " where time >= ?0 and time < ?1";
		Object[] values = {sTime, eTime};
		Object result = superDao.unique(hql, values);
		return result != null ? ((Number) result).doubleValue() : 0;
	}
}