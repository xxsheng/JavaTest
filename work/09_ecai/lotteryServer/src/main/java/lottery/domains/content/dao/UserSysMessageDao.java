package lottery.domains.content.dao;

import java.util.List;

import javautils.jdbc.PageList;
import lottery.domains.content.entity.UserSysMessage;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Order;

public interface UserSysMessageDao {

	boolean add(UserSysMessage entity);

	PageList search(List<Criterion> criterions, List<Order> orders, int start, int limit);

	boolean updateUnread(int userId, int[] ids);

	int getUnreadCount(int userId);

	boolean deleteMsg(int userId, int[] ids);

}