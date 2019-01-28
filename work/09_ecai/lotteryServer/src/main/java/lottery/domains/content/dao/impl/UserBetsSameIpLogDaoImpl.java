package lottery.domains.content.dao.impl;

import javautils.jdbc.hibernate.HibernateSuperDao;
import lottery.domains.content.dao.UserBetsSameIpLogDao;
import lottery.domains.content.entity.UserBetsSameIpLog;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class UserBetsSameIpLogDaoImpl implements UserBetsSameIpLogDao {

	private final String tab = UserBetsSameIpLog.class.getSimpleName();

	@Autowired
	private HibernateSuperDao<UserBetsSameIpLog> superDao;

    @Override
    public UserBetsSameIpLog getById(int id) {
        String hql = "from " + tab + " where id = ?0";
        Object[] values = {id};
        return (UserBetsSameIpLog) superDao.unique(hql, values);
    }

    @Override
    public UserBetsSameIpLog getByIP(String ip) {
        String hql = "from " + tab + " where ip = ?0";
        Object[] values = {ip};
        return (UserBetsSameIpLog) superDao.unique(hql, values);
    }

    @Override
    public boolean add(UserBetsSameIpLog log) {
        return superDao.save(log);
    }

    @Override
    public boolean update(UserBetsSameIpLog log) {
        return superDao.update(log);
    }
}