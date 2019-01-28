package lottery.domains.content.biz.impl;

import javautils.jdbc.PageList;
import lottery.domains.content.biz.LotteryOpenTimeService;
import lottery.domains.content.dao.LotteryOpenTimeDao;
import lottery.domains.content.entity.LotteryOpenTime;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Order;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Created by Nick on 2017-06-01.
 */
@Service
public class LotteryOpenTimeServiceImpl implements LotteryOpenTimeService {
    @Autowired
    private LotteryOpenTimeDao lotteryOpenTimeDao;

    @Override
    @Transactional(readOnly = true)
    public List<LotteryOpenTime> listAll() {
        return lotteryOpenTimeDao.listAll();
    }

    @Override
    @Transactional(readOnly = true)
    public PageList find(List<Criterion> criterions, List<Order> orders, int start, int limit) {
        return lotteryOpenTimeDao.find(criterions, orders, start, limit);
    }
}
