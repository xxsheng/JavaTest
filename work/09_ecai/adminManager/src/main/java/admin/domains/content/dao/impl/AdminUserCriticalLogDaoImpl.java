package admin.domains.content.dao.impl;

import java.util.ArrayList;
import java.util.List;

import javautils.jdbc.PageList;
import javautils.jdbc.hibernate.HibernateSuperDao;

import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Order;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import admin.domains.content.dao.AdminUserCriticalLogDao;
import admin.domains.content.entity.AdminUser;
import admin.domains.content.entity.AdminUserCriticalLog;

@Repository
public class AdminUserCriticalLogDaoImpl implements AdminUserCriticalLogDao {

	@Autowired
	private HibernateSuperDao<AdminUserCriticalLog> superDao;
	
	private final String tab = AdminUserCriticalLog.class.getSimpleName();
	@Override
	public boolean save(final List<AdminUserCriticalLog> list) {
		String sql = "insert into `admin_user_critical_log` (`admin_user_id`,`user_id`,`action_id`, `ip`, `address`, `action`, `time`, `user_agent`) values (?,?, ?, ?, ?, ?, ?, ?)";
		List<Object[]> params = new ArrayList<Object[]>();
		for (AdminUserCriticalLog tmpBean : list) {
			try {
				Object[] param = {tmpBean.getAdminUserId(),tmpBean.getUserId(),tmpBean.getActionId(), tmpBean.getIp(), tmpBean.getAddress(), tmpBean.getAction(), tmpBean.getTime(), tmpBean.getUserAgent()};
				params.add(param);
			} catch (Exception e) {}
		}
		return superDao.doWork(sql, params);
	}

	
	@Override
	public PageList find(List<Criterion> criterions, List<Order> orders, int start, int limit) {
		String propertyName = "id";
		return superDao.findPageList(AdminUserCriticalLog.class, propertyName, criterions, orders, start, limit);
	}


	@Override
	public List<AdminUserCriticalLog> findAction() {
		String hql = "from " + tab + " where 1 = 1 group by actionId";
		return superDao.list(hql);
//		return superDao.findByCriteria(AdminUserCriticalLog.class, criterions, orders);
	}
}