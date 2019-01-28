package lottery.domains.content.biz.read.impl;

import javautils.jdbc.PageList;
import lottery.domains.content.biz.read.UserDailySettleReadService;
import lottery.domains.content.dao.read.UserDailySettleReadDao;
import lottery.domains.content.entity.UserDailySettle;
import lottery.domains.content.vo.user.UserDailySettleVO;
import lottery.domains.pool.DataFactory;
import org.hibernate.criterion.Order;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * 契约日结Service
 * Created by Nick on 2016/10/31.
 */
@Service
@Transactional(readOnly = true)
public class UserDailySettleReadServiceImpl implements UserDailySettleReadService {
    @Autowired
    private UserDailySettleReadDao uDailySettleReadDao;
    @Autowired
    private DataFactory dataFactory;

    @Override
    public Long getCountUser(int userId) {
        return uDailySettleReadDao.getCountUser(userId);
    }
    
    @Override
    public PageList searchAll(int start, int limit) {
        start = start < 0 ? 0 : start;
        limit = limit < 0 ? 0 : limit;
        limit = limit > 10 ? 10 : limit;

        List<Order> orders = new ArrayList<>();
        orders.add(Order.desc("createTime"));
        PageList pList = uDailySettleReadDao.search(null, orders, start, limit);
        pList = convertPageList(pList);
        return pList;
    }

    @Override
    public PageList searchByTeam(int[] userIds, int start, int limit) {
        start = start < 0 ? 0 : start;
        limit = limit < 0 ? 0 : limit;
        limit = limit > 10 ? 10 : limit;

        PageList pList = uDailySettleReadDao.searchByTeam(userIds, start, limit);
        pList = convertPageList(pList);
        return pList;
    }

    @Override
    public PageList searchByTeam(int userId, int start, int limit) {
        start = start < 0 ? 0 : start;
        limit = limit < 0 ? 0 : limit;
        limit = limit > 10 ? 10 : limit;

        PageList pList = uDailySettleReadDao.searchByTeam(userId, start, limit);
        pList = convertPageList(pList);
        return pList;
    }

    @Override
    public PageList searchByUserId(int userId, int start, int limit) {
        start = start < 0 ? 0 : start;
        limit = limit < 0 ? 0 : limit;
        limit = limit > 10 ? 10 : limit;

        PageList pList = uDailySettleReadDao.searchByUserId(userId, start, limit);
        pList = convertPageList(pList);
        return pList;
    }

    private PageList convertPageList(PageList pList) {
        List<UserDailySettleVO> convertList = new ArrayList<>();
        if(pList != null && pList.getList() != null){
            for (Object tmpBean : pList.getList()) {
                convertList.add(new UserDailySettleVO((UserDailySettle) tmpBean, dataFactory));
            }
        }
        pList.setList(convertList);
        return pList;
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserDailySettle> findByUserIds(List<Integer> userIds) {
        return uDailySettleReadDao.findByUserIds(userIds);
    }

    // @Override
    // @Transactional(readOnly = true)
    // public PageList search(List<Integer> userIds, int start, int limit) {
    //     start = start < 0 ? 0 : start;
    //     limit = limit < 0 ? 0 : limit;
    //     limit = limit > 10 ? 10 : limit;
    //     // 查询条件
    //     List<Criterion> criterions = new ArrayList<>();
    //
    //     criterions.add(Restrictions.in("userId", userIds));
    //
    //     // 排序条件
    //     List<Order> orders = new ArrayList<>();
    //     orders.add(Order.desc("createTime"));
    //     PageList pList = uDailySettleReadDao.search(criterions, orders, start, limit);
    //     pList = convertPageList(pList);
    //     return pList;
    // }
    //
    // @Override
    // @Transactional(readOnly = true)
    // public PageList searchByDirectLowers(int upUserId, int start, int limit) {
    //     start = start < 0 ? 0 : start;
    //     limit = limit < 0 ? 0 : limit;
    //     limit = limit > 10 ? 10 : limit;
    //
    //     PageList pList = uDailySettleReadDao.searchByDirectLowers(upUserId, start, limit);
    //     pList = convertPageList(pList);
    //     return pList;
    // }
    //
    // @Override
    // @Transactional(readOnly = true)
    // public List<UserDailySettle> findByDirectLowers(int upUserId) {
    //     return uDailySettleReadDao.findByDirectLowers(upUserId);
    // }
}
