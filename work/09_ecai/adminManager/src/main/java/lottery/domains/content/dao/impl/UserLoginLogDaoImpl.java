package lottery.domains.content.dao.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Order;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javautils.jdbc.PageList;
import javautils.jdbc.hibernate.HibernateSuperDao;
import lottery.domains.content.dao.UserLoginLogDao;
import lottery.domains.content.entity.HistoryUserLoginLog;
import lottery.domains.content.entity.User;
import lottery.domains.content.entity.UserLoginLog;

@Repository
public class UserLoginLogDaoImpl implements UserLoginLogDao {
	
	private final String tab = UserLoginLog.class.getSimpleName();
	private final String utab = User.class.getSimpleName();
	
	@Autowired
	private HibernateSuperDao<HistoryUserLoginLog> historySuperDao;
	
	@Autowired
	private HibernateSuperDao<UserLoginLog> superDao;
	
	@Override
	public List<UserLoginLog> getByUserId(int userId) {
		String hql = "from " + tab + " where userId = ?0";
		Object[] values = {userId};
		return superDao.list(hql, values);
	}
	
	@Override
	public UserLoginLog getLastLogin(int userId) {
		String hql = "from " + tab + " where userId = ?0 order by id desc";
		Object[] values = {userId};
		List<UserLoginLog> list = superDao.list(hql, values, 0, 1);
		if(list != null && list.size() > 0) {
			return list.get(0);
		}
		return null;
	}

	@Override
	public PageList find(List<Criterion> criterions,
			List<Order> orders, int start, int limit) {
		String propertyName = "id";
		return superDao.findPageList(UserLoginLog.class, propertyName, criterions, orders, start, limit);
	}
	@Override
	public PageList findHistory(List<Criterion> criterions,
			List<Order> orders, int start, int limit) {
		String propertyName = "id";
		return historySuperDao.findPageList(HistoryUserLoginLog.class, propertyName, criterions, orders, start, limit);
	}
	@Override
	public List<?> getDayUserLogin(String sTime, String eTime) {
		String hql = "select substring(l.time, 1, 10), count(distinct l.userId) from " + tab + "  l  , " + utab + " u  where l.userId = u.id and  l.time >= ?0 and l.time < ?1  and u.upid !=?2 group by substring(l.time, 1, 10)";
		Object[] values = {sTime, eTime,0};
		return superDao.listObject(hql, values);
	}

	@Override
	public PageList searchSameIp(Integer userId, String ip, int start, int limit) {
		String sql;
		Map<String, Object> params = new HashMap<>();
		// 无条件
		if (userId == null && StringUtils.isEmpty(ip)) {
			// sql = "select t1.ip,ull.address,group_concat(distinct u.username) from (select distinct ip from user_login_log where id > 0 limit 0,20) t1,user_login_log ull,user u where t1.ip = ull.ip and ull.user_id = u.id group by t1.ip,ull.address";
			return null;
		}
		// 用户
		else if (userId != null && StringUtils.isEmpty(ip)) {
			sql = "select t1.ip,ull.address,group_concat(distinct u.username) from (select distinct ip from user_login_log where user_id = :userId and id > 0 limit 0,1000) t1,user_login_log ull,user u where t1.ip = ull.ip and ull.user_id = u.id group by t1.ip,ull.address";
			params.put("userId", userId);
		}
		// IP
		else if (userId == null && StringUtils.isNotEmpty(ip)) {
			sql = "select t1.ip,ull.address,group_concat(distinct u.username) from (select distinct ip from user_login_log where ip = :ip and id > 0 limit 0,1000) t1,user_login_log ull,user u where t1.ip = ull.ip and ull.user_id = u.id group by t1.ip,ull.address";
			params.put("ip", ip);
		}
		// 用户+IP
		else {
			sql = "select t1.ip,ull.address,group_concat(distinct u.username) from (select distinct ip from user_login_log where user_id = :userId and ip = :ip and id > 0 limit 0,1000) t1,user_login_log ull,user u where t1.ip = ull.ip and ull.user_id = u.id group by t1.ip,ull.address";
			params.put("userId", userId);
			params.put("ip", ip);
		}

		return superDao.findPageList(sql, params, start, limit);
	}
}