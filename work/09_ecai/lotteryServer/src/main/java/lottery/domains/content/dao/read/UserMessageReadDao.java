package lottery.domains.content.dao.read;

import javautils.jdbc.PageList;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Order;

import java.util.List;

public interface UserMessageReadDao {
	int getUnreadCount(int userId);

	PageList search(List<Criterion> criterions, List<Order> orders, int start, int limit);
}