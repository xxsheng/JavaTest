package activity.domains.content.dao.impl;

import java.util.List;

import activity.domains.content.dao.ActivityRechargeLoopBillDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import activity.domains.content.entity.ActivityRechargeLoopBill;
import javautils.jdbc.hibernate.HibernateSuperDao;

@Repository
public class ActivityRechargeLoopBillDaoImpl implements ActivityRechargeLoopBillDao {
	
	private final String tab = ActivityRechargeLoopBill.class.getSimpleName();
	
	@Autowired
	private HibernateSuperDao<ActivityRechargeLoopBill> superDao;

	@Override
	public boolean add(ActivityRechargeLoopBill entity) {
		return superDao.save(entity);
	}

	@Override
	public List<ActivityRechargeLoopBill> getByDate(int userId, String date) {
		String hql = "from " + tab + " where userId = ?0 and time like ?1";
		Object[] values = {userId, "%" + date + "%"};
		return superDao.list(hql, values);
	}

}
