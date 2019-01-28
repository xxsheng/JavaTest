package lottery.domains.content.dao.impl;

import javautils.date.Moment;
import javautils.jdbc.hibernate.HibernateSuperDao;
import lottery.domains.content.dao.UserDailySettleDao;
import lottery.domains.content.entity.UserDailySettle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

/**
 * 契约日结DAO
 * Created by Nick on 2016/10/31.
 */
@Repository
public class UserDailySettleDaoImpl implements UserDailySettleDao {
    private final String tab = UserDailySettle.class.getSimpleName();

    @Autowired
    private HibernateSuperDao<UserDailySettle> superDao;

    @Override
    public UserDailySettle getByUserId(int userId) {
        String hql = "from " + tab + " where userId = ?0";
        Object[] values = {userId};
        return (UserDailySettle) superDao.unique(hql, values);
    }

    @Override
    public UserDailySettle getById(int id) {
        String hql = "from " + tab + " where id = ?0";
        Object[] values = {id};
        return (UserDailySettle) superDao.unique(hql, values);
    }

    @Override
    public void add(UserDailySettle entity) {
        superDao.save(entity);
    }

    @Override
    public void updateStatus(int id, int status, int beforeStatus) {
        String hql = "update " + tab + " set status = ?0, agreeTime = ?1 where id = ?2 and status=?3";
        Object[] values = {status, new Moment().toSimpleTime(), id, beforeStatus};
        superDao.update(hql, values);
    }

    @Override
    public boolean updateSomeFields(int id, double scale, int minValidUser, int status, int fixed, double minScale, double maxScale) {
        String hql = "update " + tab + " set scale = ?0, minValidUser = ?1, status = ?2, fixed = ?3, minScale = ?4, maxScale = ?5 where id = ?6";
        Object[] values = {scale, minValidUser, status, fixed, minScale, maxScale, id};
        return superDao.update(hql, values);
    }

    @Override
    public void deleteByUser(int userId) {
        String hql = "delete from " + tab + " where userId = ?0";
        Object[] values = {userId};
        superDao.delete(hql, values);
    }

    @Override
    public void deleteByTeam(int upUserId) {
        String hql = "delete from " + tab + " where userId in(select id from User where id = ?0 or upids like ?1)";
        Object[] values = {upUserId, "%["+upUserId+"]%"};
        superDao.delete(hql, values);
    }

    @Override
    public void deleteLowers(int upUserId) {
        String hql = "delete from " + tab + " where userId in(select id from User where upids like ?0)";
        Object[] values = {"%["+upUserId+"]%"};
        superDao.delete(hql, values);
    }
}
