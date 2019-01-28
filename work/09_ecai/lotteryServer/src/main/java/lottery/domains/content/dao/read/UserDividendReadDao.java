package lottery.domains.content.dao.read;

import javautils.jdbc.PageList;
import lottery.domains.content.entity.UserDividend;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Order;

import java.util.List;

public interface UserDividendReadDao {
	PageList search(List<Criterion> criterions, List<Order> orders, int start, int limit);

	PageList searchByTeam(int[] userIds, int start, int limit);

	PageList searchByTeam(int userId, int start, int limit);

	PageList searchByUserId(int userId, int start, int limit);

	// PageList searchByZhuGuans(List<Integer> userIds, int start, int limit);
    //
	// PageList searchByDirectLowers(int upUserId, int start, int limit);

	List<UserDividend> findByUserIds(List<Integer> userIds);
	
	Long getCountUser(int userId);
}