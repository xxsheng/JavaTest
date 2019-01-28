package lottery.domains.content.dao;

import java.util.List;

import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Order;

import javautils.jdbc.PageList;
import lottery.domains.content.entity.VipBirthdayGifts;

public interface VipBirthdayGiftsDao {
	
	boolean add(VipBirthdayGifts entity);
	
	VipBirthdayGifts getById(int id);
	
	int getWaitTodo();
	
	boolean hasRecord(int userId, int year);
	
	boolean update(VipBirthdayGifts entity);
	
	PageList find(List<Criterion> criterions, List<Order> orders, int start, int limit);

}