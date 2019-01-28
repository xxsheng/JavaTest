package lottery.domains.content.dao;

import lottery.domains.content.entity.UserDailySettle;

/**
 * 契约日结DAO
 * Created by Nick on 2016/10/31.
 */
public interface UserDailySettleDao {
    UserDailySettle getByUserId(int userId);

    UserDailySettle getById(int id);

    /**
     * 新增日结
     */
    void add(UserDailySettle entity);

    void updateStatus(int id, int status, int beforeStatus);

    boolean updateSomeFields(int id, double scale, int minValidUser, int status, int fixed, double minScale, double maxScale);

    /**
     * 根据用户ID删除日结
     */
    void deleteByUser(int userId);

    /**
     * 根据上级用户ID删除日结，包含所有下级
     */
    void deleteByTeam(int upUserId);

    /**
     * 删除团队日结，但不包含自己
     */
    void deleteLowers(int upUserId);
}
