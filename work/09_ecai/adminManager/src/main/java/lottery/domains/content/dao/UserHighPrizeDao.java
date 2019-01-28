package lottery.domains.content.dao;

import javautils.jdbc.PageList;
import lottery.domains.content.entity.UserHighPrize;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Order;

import java.util.List;

public interface UserHighPrizeDao {
	PageList find(List<Criterion> criterions, List<Order> orders, int start, int limit);
	
	PageList find(String sql , int start, int limit);

	UserHighPrize getById(int id);

	boolean add(UserHighPrize entity);

	boolean updateStatus(int id, int status);

	boolean updateStatusAndConfirmUsername(int id, int status, String confirmUsername);

	int getUnProcessCount();
}