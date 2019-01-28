package lottery.domains.content.dao;

import lottery.domains.content.entity.UserRecharge;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Order;

import java.util.List;

public interface UserRechargeDao {
	
	boolean add(UserRecharge entity);
	
	boolean update(UserRecharge entity);
	
	double getTotalRechargeMoney(int userId);
	
	UserRecharge getByBillno(String billno);
	
	List<UserRecharge> listByDate(int userId, String date);

	List<UserRecharge> list(List<Criterion> criterions, List<Order> orders);

	double listByTypeAndDateTotal(int userId,String date,int type);

	int countTotal(int userId, int status, String sPayDate, String ePayDate);
}