package activity.domains.content.dao.impl;

import java.util.List;

import activity.domains.content.dao.ActivitySalaryBillDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import activity.domains.content.entity.ActivitySalaryBill;
import javautils.jdbc.hibernate.HibernateSuperDao;

@Repository
public class ActivitySalaryBillDaoImpl implements ActivitySalaryBillDao {
	
	private final String tab = ActivitySalaryBill.class.getSimpleName();
	
	@Autowired
	private HibernateSuperDao<ActivitySalaryBill> superDao;

	@Override
	public boolean add(ActivitySalaryBill entity) {
		return superDao.save(entity);
	}

	@Override
	public boolean hasRecord(int userId, int type, String date) {
		String hql = "from " + tab + " where userId = ?0 and type = ?1 and date = ?2";
		Object[] values = {userId, type, date};
		List<ActivitySalaryBill> list = superDao.list(hql, values);
		if(list != null && list.size() > 0) {
			return true;
		}
		return false;
	}
	
}