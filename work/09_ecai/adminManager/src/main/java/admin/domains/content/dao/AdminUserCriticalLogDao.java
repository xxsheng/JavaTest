package admin.domains.content.dao;

import java.util.List;

import javautils.jdbc.PageList;

import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Order;

import admin.domains.content.entity.AdminUserCriticalLog;

public interface AdminUserCriticalLogDao {
	
	/**
	 * 批量保存行为日志
	 * @param list
	 * @return
	 */
	boolean save(List<AdminUserCriticalLog> list);
	
	/**
	 * 查询关键行为日志
	 * @return
	 */
	PageList find(List<Criterion> criterions, List<Order> orders, int start, int limit);
	
	/**
	 * 查询权限列表
	 * @param criterions
	 * @return
	 */
	List<AdminUserCriticalLog> findAction();
	
}