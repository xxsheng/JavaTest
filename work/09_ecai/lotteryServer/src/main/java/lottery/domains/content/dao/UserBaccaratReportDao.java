package lottery.domains.content.dao;

import java.util.List;

import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Order;

import lottery.domains.content.entity.UserBaccaratReport;

public interface UserBaccaratReportDao {
	
	boolean add(UserBaccaratReport entity);
	
	UserBaccaratReport get(int userId, String time);
	
	boolean update(UserBaccaratReport entity);
	
	List<UserBaccaratReport> find(List<Criterion> criterions, List<Order> orders);
	
}