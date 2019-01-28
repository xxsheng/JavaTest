package activity.domains.content.dao.impl;

import java.util.List;

import activity.domains.content.dao.ActivitySignBillDao;
import javautils.jdbc.hibernate.HibernateSuperDao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import activity.domains.content.entity.ActivitySignBill;

@Repository
public class ActivitySignBillDaoImpl implements ActivitySignBillDao {
	
	private final String tab = ActivitySignBill.class.getSimpleName();
	
	@Autowired
	private HibernateSuperDao<ActivitySignBill> superDao;

	@Override
	public boolean add(ActivitySignBill entity) {
		return superDao.save(entity);
	}

	@Override
	public ActivitySignBill get(int userId, String time) {
		String hql = "from " + tab + " where userId = ?0 and time like ?1";
		Object[] values = {userId, "%" + time + "%"};
		List<ActivitySignBill> list = superDao.list(hql, values);
		if(list != null && list.size() > 0) {
			return list.get(0);
		}
		return null;
	}

}