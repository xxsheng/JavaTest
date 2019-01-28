package lottery.domains.content.dao.read;

import javautils.jdbc.PageList;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Order;

import java.util.List;

public interface UserDividendBillReadDao {
	PageList search(List<Criterion> criterions, List<Order> orders, int start, int limit);

	PageList searchByTeam(int[] userIds, String sTime, String eTime, Integer status, int start, int limit);

	PageList searchByTeam(int userId, String sTime, String eTime, Integer status, int start, int limit);

	PageList searchByUserId(int userId, String sTime, String eTime, Integer status, int start, int limit);

	// PageList search(List<Criterion> criterions, List<Order> orders, int start, int limit);
    //
	// PageList searchByTeam(int[] upUserIds, String sTime, String eTime, int start, int limit);
    //
	// PageList searchByTeam(int upUserId, String sTime, String eTime, int start, int limit);

	// PageList searchByZhuGuans(List<Integer> userIds, String sTime, String eTime, int start, int limit);
    //
	// PageList searchByDirectLowers(int upUserId, String sTime, String eTime, int start, int limit);
}