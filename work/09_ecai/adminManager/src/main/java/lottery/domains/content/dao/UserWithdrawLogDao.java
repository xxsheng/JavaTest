package lottery.domains.content.dao;

import java.util.List;

import javautils.jdbc.PageList;
import lottery.domains.content.entity.UserWithdrawLog;

import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Order;


public interface UserWithdrawLogDao {
	
	boolean add(UserWithdrawLog entity);
	
	List<UserWithdrawLog> getByUserId(int userId,int tpye);
	
	PageList find(List<Criterion> criterions, List<Order> orders, int start, int limit);

}