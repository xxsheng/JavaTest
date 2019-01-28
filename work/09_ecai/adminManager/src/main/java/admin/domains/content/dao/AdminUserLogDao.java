package admin.domains.content.dao;

import java.util.List;

import javautils.jdbc.PageList;

import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Order;

import admin.domains.content.entity.AdminUserLog;

public interface AdminUserLogDao {
	
	/**
	 * 批量保存行为日志
	 * @param list
	 * @return
	 */
	boolean save(List<AdminUserLog> list);
	
	/**
	 * 查询行为日志
	 * @return
	 */
	PageList find(List<Criterion> criterions, List<Order> orders, int start, int limit);
	
}