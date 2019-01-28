package admin.domains.content.dao.impl;

import java.util.ArrayList;
import java.util.List;

import javautils.jdbc.PageList;
import javautils.jdbc.hibernate.HibernateSuperDao;

import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Order;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import admin.domains.content.dao.AdminUserActionLogDao;
import admin.domains.content.entity.AdminUserActionLog;

@Repository
public class AdminUserActionLogDaoImpl implements AdminUserActionLogDao {

	@Autowired
	private HibernateSuperDao<AdminUserActionLog> superDao;
	
	@Override
	public boolean save(AdminUserActionLog entity) {
		return superDao.save(entity);
	}

	@Override
	public boolean save(final List<AdminUserActionLog> list) {
		String sql = "insert into `admin_user_action_log`(`user_id`, `action_id`, `data`, `millisecond`, `error`, `message`, `time`, `user_agent`) values(?,?,?,?,?,?,?,?)";
		List<Object[]> params = new ArrayList<Object[]>();
		for (AdminUserActionLog tmp : list) {
			try {
				Object[] param = {tmp.getUserId(), tmp.getActionId(), tmp.getData(), tmp.getMillisecond(), tmp.getError(), tmp.getMessage(), tmp.getTime(), tmp.getUserAgent()};
				params.add(param);
			} catch (Exception e) {}
		}
		return superDao.doWork(sql, params);
	}

	@Override
	public PageList find(List<Criterion> criterions, List<Order> orders, int start, int limit) {
		String propertyName = "id";
		return superDao.findPageList(AdminUserActionLog.class, propertyName, criterions, orders, start, limit);
	}

}