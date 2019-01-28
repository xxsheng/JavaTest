package lottery.domains.content.dao;

import javautils.jdbc.PageList;
import lottery.domains.content.entity.UserDividend;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Order;

import java.util.List;

public interface UserDividendDao {

	PageList search(List<Criterion> criterions, List<Order> orders, int start, int limit);

	List<UserDividend> findByUserIds(List<Integer> userIds);

	UserDividend getByUserId(int userId);

	UserDividend getById(int id);

	/**
	 * 新增契约分红
	 */
	void add(UserDividend entity);

	void updateStatus(int id, int status);

	boolean updateSomeFields(int id, String scaleLevel, String lossLevel, String salesLevel, int minValidUser, double minScale, double maxScale, String userLevel);

	boolean updateSomeFields(int id, String scaleLevel, String lossLevel, String salesLevel, int minValidUser, int fixed, double minScale, double maxScale, int status);

	/**
	 * 根据用户ID删除日结
	 */
	void deleteByUser(int userId);

	/**
	 * 删除团队分红
	 */
	void deleteByTeam(int upUserId);

	/**
	 * 删除所有下级的分红配置
	 */
	void deleteLowers(int upUserId);
}