package admin.domains.content.dao.impl;

import java.util.ArrayList;
import java.util.List;

import javautils.jdbc.PageList;
import javautils.jdbc.hibernate.HibernateSuperDao;

import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Order;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import admin.domains.content.dao.AdminUserLogDao;
import admin.domains.content.entity.AdminUserLog;

@Repository
public class AdminUserLogDaoImpl implements AdminUserLogDao {

	@Autowired
	private HibernateSuperDao<AdminUserLog> superDao;
	
	@Override
	public boolean save(final List<AdminUserLog> list) {
		String sql = "insert into `admin_user_log` (`user_id`, `ip`, `address`, `action`, `time`, `user_agent`) values (?, ?, ?, ?, ?, ?)";
		List<Object[]> params = new ArrayList<Object[]>();
		for (AdminUserLog tmpBean : list) {
			try {
				Object[] param = {tmpBean.getUserId(), tmpBean.getIp(), tmpBean.getAddress(), tmpBean.getAction(), tmpBean.getTime(), tmpBean.getUserAgent()};
				params.add(param);
			} catch (Exception e) {}
		}
		return superDao.doWork(sql, params);
	}

	@Override
	public PageList find(List<Criterion> criterions, List<Order> orders, int start, int limit) {
		String propertyName = "id";
		return superDao.findPageList(AdminUserLog.class, propertyName, criterions, orders, start, limit);
	}

}