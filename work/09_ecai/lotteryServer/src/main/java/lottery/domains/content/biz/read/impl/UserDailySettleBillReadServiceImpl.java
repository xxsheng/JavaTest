package lottery.domains.content.biz.read.impl;

import javautils.StringUtil;
import javautils.jdbc.PageList;
import lottery.domains.content.biz.read.UserDailySettleBillReadService;
import lottery.domains.content.dao.read.UserDailySettleBillReadDao;
import lottery.domains.content.entity.UserDailySettleBill;
import lottery.domains.content.vo.user.UserDailySettleBillVO;
import lottery.domains.pool.DataFactory;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * 契约日结账单Service
 * Created by Nick on 2016/10/31.
 */
@Service
@Transactional(readOnly = true)
public class UserDailySettleBillReadServiceImpl implements UserDailySettleBillReadService {
    @Autowired
    private UserDailySettleBillReadDao uDailySettleBillReadDao;
    @Autowired
    private DataFactory dataFactory;

    @Override
    public UserDailySettleBill getById(int id) {
        return uDailySettleBillReadDao.getById(id);
    }

    @Override
    public PageList searchAll(String sTime, String eTime, int start, int limit) {
        start = start < 0 ? 0 : start;
        limit = limit < 0 ? 0 : limit;
        limit = limit > 10 ? 10 : limit;

        List<Criterion> criterions = new ArrayList<>();
        if (StringUtil.isNotNull(sTime)) {
            criterions.add(Restrictions.ge("indicateDate", sTime));
        }
        if (StringUtil.isNotNull(eTime)) {
            criterions.add(Restrictions.lt("indicateDate", eTime));
        }

        List<Order> orders = new ArrayList<>();
        orders.add(Order.desc("settleTime"));
        PageList pList = uDailySettleBillReadDao.search(criterions, orders, start, limit);
        pList = convertPageList(pList);
        return pList;
    }

    @Override
    public PageList searchByTeam(int[] userIds, String sTime, String eTime, int start, int limit) {
        start = start < 0 ? 0 : start;
        limit = limit < 0 ? 0 : limit;
        limit = limit > 10 ? 10 : limit;

        PageList pList = uDailySettleBillReadDao.searchByTeam(userIds, sTime, eTime, start, limit);
        pList = convertPageList(pList);
        return pList;
    }

    @Override
    public PageList searchByTeam(int userId, String sTime, String eTime, int start, int limit) {
        start = start < 0 ? 0 : start;
        limit = limit < 0 ? 0 : limit;
        limit = limit > 10 ? 10 : limit;

        PageList pList = uDailySettleBillReadDao.searchByTeam(userId, sTime, eTime, start, limit);
        pList = convertPageList(pList);
        return pList;
    }

    @Override
    public PageList searchByUserId(int userId, String sTime, String eTime, int start, int limit) {
        start = start < 0 ? 0 : start;
        limit = limit < 0 ? 0 : limit;
        limit = limit > 10 ? 10 : limit;

        PageList pList = uDailySettleBillReadDao.searchByUserId(userId, sTime, eTime, start, limit);
        pList = convertPageList(pList);
        return pList;
    }

    @Override
    public double getTotalUnIssue(int userId) {
        return uDailySettleBillReadDao.getTotalUnIssue(userId);
    }

    // @Override
    // @Transactional(readOnly = true)
    // public PageList search(List<Integer> userIds, String sTime, String eTime, int start, int limit) {
    //     start = start < 0 ? 0 : start;
    //     limit = limit < 0 ? 0 : limit;
    //     limit = limit > 10 ? 10 : limit;
    //     // 查询条件
    //     List<Criterion> criterions = new ArrayList<>();
    //
    //     if (CollectionUtils.isNotEmpty(userIds)) {
    //         criterions.add(Restrictions.in("userId", userIds));
    //     }
    //
    //     if (StringUtil.isNotNull(sTime)) {
    //         criterions.add(Restrictions.ge("indicateDate", sTime));
    //     }
    //     if (StringUtil.isNotNull(eTime)) {
    //         criterions.add(Restrictions.lt("indicateDate", eTime));
    //     }
    //
    //     // 排序条件
    //     List<Order> orders = new ArrayList<>();
    //     orders.add(Order.desc("settleTime"));
    //     PageList pList = uDailySettleBillReadDao.search(criterions, orders, start, limit);
    //     pList = convertPageList(pList);
    //     return pList;
    // }
    //
    // @Override
    // @Transactional(readOnly = true)
    // public PageList searchByDirectLowers(int upUserId, String sTime, String eTime, int start, int limit) {
    //     start = start < 0 ? 0 : start;
    //     limit = limit < 0 ? 0 : limit;
    //     limit = limit > 10 ? 10 : limit;
    //
    //     PageList pList = uDailySettleBillReadDao.searchByDirectLowers(upUserId, sTime, eTime, start, limit);
    //     pList = convertPageList(pList);
    //     return pList;
    // }

    private PageList convertPageList(PageList pList) {
        List<UserDailySettleBillVO> convertList = new ArrayList<>();
        if(pList != null && pList.getList() != null){
            for (Object tmpBean : pList.getList()) {
                convertList.add(new UserDailySettleBillVO((UserDailySettleBill) tmpBean, dataFactory));
            }
        }
        pList.setList(convertList);
        return pList;
    }
}
