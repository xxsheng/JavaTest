package lottery.domains.content.dao;

import java.util.List;

import javautils.jdbc.PageList;

import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Order;

import lottery.domains.content.entity.UserBill;

public interface UserBillDao {
	
	boolean add(UserBill entity);
	
	UserBill getById(int id);
	
	double getTotalMoney(String sTime, String eTime, int type, int[] refType);
	
	List<UserBill> getLatest(int userId, int type, int count);
	
	List<UserBill> listByDateAndType(String date, int type, int[] refType);
	
	PageList find(List<Criterion> criterions, List<Order> orders, int start, int limit);
	
	PageList findNoDemoUserBill(String sql, int start, int limit);
	
	PageList findHistory(List<Criterion> criterions, List<Order> orders, int start, int limit);
	
	
}