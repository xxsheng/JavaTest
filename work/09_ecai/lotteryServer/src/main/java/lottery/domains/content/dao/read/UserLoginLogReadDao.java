package lottery.domains.content.dao.read;

import javautils.jdbc.PageList;
import lottery.domains.content.entity.UserLoginLog;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Order;

import java.util.List;

public interface UserLoginLogReadDao {
	UserLoginLog getLastLogin(int userId, int start);

	List<UserLoginLog> getLoginLog(int userId, int start, int limit);

	PageList getLoginLogList(List<Criterion> criterions, List<Order> orders, int start, int limit);
}