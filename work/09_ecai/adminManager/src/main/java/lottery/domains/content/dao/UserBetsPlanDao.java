package lottery.domains.content.dao;

import java.util.List;

import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Order;

import javautils.jdbc.PageList;
import lottery.domains.content.entity.UserBetsPlan;

public interface UserBetsPlanDao {

	boolean add(UserBetsPlan entity);
	
	UserBetsPlan get(int id);
	
	UserBetsPlan get(int id, int userId);
	
	boolean hasRecord(int userId, int lotteryId, String expect);
	
	boolean updateCount(int id, int count);
	
	boolean updateStatus(int id, int status);
	
	boolean updateStatus(int id, int status, double prizeMoney);
	
	List<UserBetsPlan> listUnsettled();
	
	List<UserBetsPlan> list(int lotteryId, String expect);

	PageList search(List<Criterion> criterions, List<Order> orders, int start, int limit);
	
}