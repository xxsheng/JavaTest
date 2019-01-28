package lottery.domains.content.dao;

import java.util.List;

import javautils.jdbc.PageList;

import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Order;

import lottery.domains.content.entity.SysNotice;

public interface SysNoticeDao {

	SysNotice getById(int id);
	
	boolean add(SysNotice entity);
	
	boolean update(SysNotice entity);
	
	boolean delete(int id);
	
	PageList find(List<Criterion> criterions, List<Order> orders, int start, int limit);
	
}