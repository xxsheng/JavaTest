package lottery.domains.content.dao;

import lottery.domains.content.entity.UserDividend;

public interface UserDividendDao {
	UserDividend getByUserId(int userId);

	UserDividend getById(int id);

	/**
	 * 新增契约分红
	 */
	void add(UserDividend entity);

	void updateStatus(int id, int status, int beforeStatus, int userId);

	void updateSomeFields(int id, String scaleLevel, String lossLevel, String salesLevel, int minValidUser, int fixed, double minScale, double maxScale, int status);

	/**
	 * 根据用户ID删除日结
	 */
	void deleteByUser(int userId);

	/**
	 * 根据上级用户ID删除日结，包含所有下级
	 */
	void deleteByTeam(int upUserId);

	/**
	 * 删除所有下级的分红配置
	 */
	void deleteLowers(int upUserId);
}