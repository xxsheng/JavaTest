package lottery.domains.content.dao.impl;

import javautils.array.ArrayUtils;
import javautils.jdbc.PageList;
import javautils.jdbc.hibernate.HibernateSuperDao;
import lottery.domains.content.dao.UserDailySettleDao;
import lottery.domains.content.entity.UserDailySettle;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Order;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

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
    public PageList search(List<Criterion> criterions, List<Order> orders, int start, int limit) {
        String propertyName = "id";
        return superDao.findPageList(UserDailySettle.class, propertyName, criterions, orders, start, limit);
    }

    @Override
    public List<UserDailySettle> list(List<Criterion> criterions, List<Order> orders) {
        return superDao.findByCriteria(UserDailySettle.class, criterions, orders);
    }

    @Override
    public List<UserDailySettle> findByUserIds(List<Integer> userIds) {
        String hql = "from " + tab + " where userId in( " +  ArrayUtils.transInIds(userIds) + ")";
        return superDao.list(hql);
    }

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

    // @Override
    // public void deleteByUserId(int userId) {
    //     String hql = "delete from " + tab + " where userId = ?0";
    //     Object[] values = {userId};
    //     superDao.delete(hql, values);
    // }
    //
    // @Override
    // public void deleteByUserIds(List<Integer> userIds) {
    //     String hql = "delete from " + tab + " where userId in(" +  ArrayUtils.transInIds(userIds) + ")";
    //     superDao.delete(hql);
    // }

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

    @Override
    public boolean updateStatus(int id, int status) {
        String hql = "update " + tab + " set status = ?1 where id = ?0";
        Object[] values = {id, status};
        return superDao.update(hql, values);
    }

    @Override
    public boolean updateSomeFields(int id, String scaleLevel, String lossLevel, String salesLevel, int minValidUser, String userLevel) {
        String hql = "update " + tab + " set scaleLevel = ?0,lossLevel=?1,salesLevel=?2, minValidUser = ?3, userLevel = ?4, minScale=?5,maxScale=?6 where id = ?7";
        String[] scaleArr = scaleLevel.split(",");
        Object[] values = {scaleLevel, lossLevel, salesLevel, minValidUser, userLevel, Double.valueOf(scaleArr[0]), Double.valueOf(scaleArr[scaleArr.length -1]), id};
        return superDao.update(hql, values);
    }

    @Override
    public boolean updateSomeFields(int id, double scale, int minValidUser, int status, int fixed, double minScale, double maxScale) {
        String hql = "update " + tab + " set scale = ?0, minValidUser = ?1, status = ?2, fixed = ?3, minScale = ?4, maxScale = ?5 where id = ?6";
        Object[] values = {scale, minValidUser, status, fixed, minScale, maxScale, id};
        return superDao.update(hql, values);
    }

    @Override
    public boolean updateTotalAmount(int userId, double amount) {
        String hql = "update " + tab + " set totalAmount = totalAmount + ?1 where userId = ?0";
        Object[] values = {userId, amount};
        return superDao.update(hql, values);
    }
}
