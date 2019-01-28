package lottery.domains.content.dao;

import lottery.domains.content.entity.UserLotteryReport;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Order;

import java.util.List;

public interface UserLotteryReportDao {

	UserLotteryReport get(int userId, String time);

	boolean update(UserLotteryReport entity);
	
	boolean add(UserLotteryReport entity);

	List<UserLotteryReport> find(List<Criterion> criterions, List<Order> orders);
}