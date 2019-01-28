package lottery.domains.content.dao;

import lottery.domains.content.entity.UserGameReport;

/**
 * Created by Nick on 2016/12/24.
 */
public interface UserGameReportDao {
    boolean save(UserGameReport entity);

    UserGameReport get(int userId, int platformId, String time);

    boolean update(UserGameReport entity);
}
