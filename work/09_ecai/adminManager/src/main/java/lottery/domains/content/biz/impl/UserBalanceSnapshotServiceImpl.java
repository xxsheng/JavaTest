package lottery.domains.content.biz.impl;

import javautils.StringUtil;
import javautils.jdbc.PageList;
import lottery.domains.content.biz.UserBalanceSnapshotService;
import lottery.domains.content.dao.UserBalanceSnapshotDao;
import lottery.domains.content.entity.UserBalanceSnapshot;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Nick on 2017-07-03.
 */
@Service
public class UserBalanceSnapshotServiceImpl implements UserBalanceSnapshotService {
    @Autowired
    private UserBalanceSnapshotDao uBalanceSnapshotDao;

    @Override
    @Transactional(readOnly = true)
    public PageList search(String sDate, String eDate, int start, int limit) {
        List<Criterion> criterions = new ArrayList<>();
        List<Order> orders = new ArrayList<>();
        if(StringUtil.isNotNull(sDate)) {
            criterions.add(Restrictions.ge("time", sDate));
        }
        if(StringUtil.isNotNull(eDate)) {
            criterions.add(Restrictions.le("time", eDate));
        }
        orders.add(Order.desc("time"));
        return uBalanceSnapshotDao.find(criterions, orders, start, limit);
    }

    @Override
    public boolean add(UserBalanceSnapshot entity) {
        return uBalanceSnapshotDao.add(entity);
    }
}
