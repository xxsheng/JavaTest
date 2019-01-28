package lottery.domains.content.biz.impl;

import javautils.StringUtil;
import javautils.jdbc.PageList;
import lottery.domains.content.biz.UserGameWaterBillReadService;
import lottery.domains.content.dao.read.UserGameWaterBillReadDao;
import lottery.domains.content.entity.UserGameWaterBill;
import lottery.domains.content.vo.bill.UserGameWaterBillVO;
import lottery.domains.pool.DataFactory;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * 老虎机真人体育返水账单Service
 * Created by Nick on 2017/02/04
 */
@Service
public class UserGameWaterBillReadServiceImpl implements UserGameWaterBillReadService {
    @Autowired
    private UserGameWaterBillReadDao uGameWaterBillReadDao;
    @Autowired
    private DataFactory dataFactory;

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
        PageList pList = uGameWaterBillReadDao.search(criterions, orders, start, limit);
        pList = convertPageList(pList);
        return pList;
    }

    @Override
    public PageList searchByTeam(int[] userIds, String sTime, String eTime, int start, int limit) {
        start = start < 0 ? 0 : start;
        limit = limit < 0 ? 0 : limit;
        limit = limit > 10 ? 10 : limit;

        PageList pList = uGameWaterBillReadDao.searchByTeam(userIds, sTime, eTime, start, limit);
        pList = convertPageList(pList);
        return pList;
    }

    @Override
    public PageList searchByTeam(int userId, String sTime, String eTime, int start, int limit) {
        start = start < 0 ? 0 : start;
        limit = limit < 0 ? 0 : limit;
        limit = limit > 10 ? 10 : limit;

        PageList pList = uGameWaterBillReadDao.searchByTeam(userId, sTime, eTime, start, limit);
        pList = convertPageList(pList);
        return pList;
    }

    @Override
    public PageList searchByUserId(int userId, String sTime, String eTime, int start, int limit) {
        start = start < 0 ? 0 : start;
        limit = limit < 0 ? 0 : limit;
        limit = limit > 10 ? 10 : limit;

        PageList pList = uGameWaterBillReadDao.searchByUserId(userId, sTime, eTime, start, limit);
        pList = convertPageList(pList);
        return pList;
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
    //     PageList pList = uGameWaterBillReadDao.search(criterions, orders, start, limit);
    //     pList = convertPageList(pList);
    //     return pList;
    // }
    //
    // @Override
    // @Transactional(readOnly = true)
    // public PageList searchByDirectLowers(int upUserId, String sTime, String eTime, int start, int limit) {
    //     PageList pList = uGameWaterBillReadDao.searchByDirectLowers(upUserId, sTime, eTime, start, limit);
    //     pList = convertPageList(pList);
    //     return pList;
    // }

    private PageList convertPageList(PageList pList) {
        List<UserGameWaterBillVO> convertList = new ArrayList<>();
        if(pList != null && pList.getList() != null){
            for (Object tmpBean : pList.getList()) {
                convertList.add(new UserGameWaterBillVO((UserGameWaterBill) tmpBean, dataFactory));
            }
        }
        pList.setList(convertList);
        return pList;
    }
}
