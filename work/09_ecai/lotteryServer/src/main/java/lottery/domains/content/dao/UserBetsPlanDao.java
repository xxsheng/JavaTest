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
	
	
	List<UserBetsPlan> listUnsettled();

	PageList search(List<Criterion> criterions, List<Order> orders, int start, int limit);

	boolean updateStatus(int id, int status, double prizeMoney);

	boolean updateFollow(int id, int count, double money, int multiple);
	
}