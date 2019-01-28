package lottery.domains.content.dao;

import lottery.domains.content.entity.UserBetsSameIpLog;

public interface UserBetsSameIpLogDao {

	UserBetsSameIpLog getById(int id);

	UserBetsSameIpLog getByIP(String ip);

	boolean add(UserBetsSameIpLog log);

	boolean update(UserBetsSameIpLog log);
}