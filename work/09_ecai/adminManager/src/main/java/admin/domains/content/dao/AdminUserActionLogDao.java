package admin.domains.content.dao;

import java.util.List;

import javautils.jdbc.PageList;

import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Order;

import admin.domains.content.entity.AdminUserActionLog;

public interface AdminUserActionLogDao {
	
	/**
	 * 保存行为日志
	 * @param entity
	 * @return
	 */
	boolean save(AdminUserActionLog entity);
	
	/**
	 * 批量保存行为日志
	 * @param list
	 * @return
	 */
	boolean save(List<AdminUserActionLog> list);
	
	/**
	 * 查询行为日志
	 * @return
	 */
	PageList find(List<Criterion> criterions, List<Order> orders, int start, int limit);
	
}