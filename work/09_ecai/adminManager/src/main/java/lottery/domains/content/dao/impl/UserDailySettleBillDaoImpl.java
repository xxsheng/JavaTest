package lottery.domains.content.dao.impl;

import javautils.jdbc.PageList;
import javautils.jdbc.hibernate.HibernateSuperDao;
import lottery.domains.content.dao.UserDailySettleBillDao;
import lottery.domains.content.entity.UserDailySettleBill;
import lottery.domains.content.entity.UserDividendBill;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Order;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 契约日结账单DAO
 * Created by Nick on 2016/10/31.
 */
@Repository
public class UserDailySettleBillDaoImpl implements UserDailySettleBillDao {
    private final String tab = UserDailySettleBill.class.getSimpleName();

    @Autowired
    private HibernateSuperDao<UserDailySettleBill> superDao;

    @Override
    public PageList search(List<Criterion> criterions, List<Order> orders, int start, int limit) {
        String propertyName = "id";
        return superDao.findPageList(UserDailySettleBill.class, propertyName, criterions, orders, start, limit);
    }

    @Override
    public List<UserDailySettleBill> findByCriteria(List<Criterion> criterions, List<Order> orders) {
        return superDao.findByCriteria(UserDailySettleBill.class, criterions, orders);
    }

    @Override
    public boolean add(UserDailySettleBill settleBill) {
        return superDao.save(settleBill);
    }

    @Override
    public UserDailySettleBill getById(int id) {
        String hql = "from " + tab + " where id = ?0";
        Object[] values = {id};
        return (UserDailySettleBill) superDao.unique(hql, values);
    }

    @Override
    public boolean update(UserDailySettleBill settleBill) {
        return superDao.update(settleBill);
    }
}
