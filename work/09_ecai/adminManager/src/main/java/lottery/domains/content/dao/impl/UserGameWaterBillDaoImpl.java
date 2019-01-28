package lottery.domains.content.dao.impl;

import javautils.jdbc.PageList;
import javautils.jdbc.hibernate.HibernateSuperDao;
import lottery.domains.content.dao.UserDailySettleBillDao;
import lottery.domains.content.dao.UserGameWaterBillDao;
import lottery.domains.content.entity.UserDailySettleBill;
import lottery.domains.content.entity.UserGameWaterBill;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Order;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 游戏返水账单DAO
 * Created by Nick on 2017/02/04.
 */
@Repository
public class UserGameWaterBillDaoImpl implements UserGameWaterBillDao {
    private final String tab = UserGameWaterBill.class.getSimpleName();

    @Autowired
    private HibernateSuperDao<UserGameWaterBill> superDao;

    @Override
    public PageList search(List<Criterion> criterions, List<Order> orders, int start, int limit) {
        String propertyName = "id";
        return superDao.findPageList(UserGameWaterBill.class, propertyName, criterions, orders, start, limit);
    }

    @Override
    public boolean add(UserGameWaterBill bill) {
        return superDao.save(bill);
    }
}
