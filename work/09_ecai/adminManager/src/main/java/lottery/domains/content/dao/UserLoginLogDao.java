package lottery.domains.content.dao;

import java.util.List;

import javautils.jdbc.PageList;

import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Order;

import lottery.domains.content.entity.UserLoginLog;

public interface UserLoginLogDao {
	
	List<UserLoginLog> getByUserId(int userId);
	
	UserLoginLog getLastLogin(int userId);
	
	PageList find(List<Criterion> criterions, List<Order> orders, int start, int limit);
	//历史登录日志
	PageList findHistory(List<Criterion> criterions, List<Order> orders, int start, int limit);

	List<?> getDayUserLogin(String sTime, String eTime);

	PageList searchSameIp(Integer userId, String ip, int start, int limit);
}