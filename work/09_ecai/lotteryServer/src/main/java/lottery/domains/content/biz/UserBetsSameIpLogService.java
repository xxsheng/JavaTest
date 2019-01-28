package lottery.domains.content.biz;

import lottery.domains.content.entity.UserBetsSameIpLog;

/**
 * Created by Nick on 2017-10-16.
 */
public interface UserBetsSameIpLogService {
    UserBetsSameIpLog getById(int id);

    UserBetsSameIpLog getByIP(String ip);

    boolean add(UserBetsSameIpLog log);

    boolean update(UserBetsSameIpLog log);
}
