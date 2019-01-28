package lottery.domains.content.biz.read.impl;

import javautils.StringUtil;
import javautils.jdbc.PageList;
import lottery.domains.content.biz.read.UserDividendBillReadService;
import lottery.domains.content.dao.read.UserDividendBillReadDao;
import lottery.domains.content.entity.UserDividendBill;
import lottery.domains.content.global.Global;
import lottery.domains.content.vo.user.UserDividendBillVO;
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
public class UserDividendBillReadServiceImpl implements UserDividendBillReadService {
    @Autowired
    private UserDividendBillReadDao uDividendBillReadDao;

    @Autowired
    private DataFactory dataFactory;

    @Override
    public PageList searchAll(String sTime, String eTime, Integer status, int start, int limit) {
        start = start < 0 ? 0 : start;
        limit = limit < 0 ? 0 : limit;
        limit = limit > 10 ? 10 : limit;

        List<Criterion> criterions = new ArrayList<>();
        if (StringUtil.isNotNull(sTime)) {
            criterions.add(Restrictions.ge("indicateStartDate", sTime));
        }
        if (StringUtil.isNotNull(eTime)) {
            criterions.add(Restrictions.le("indicateStartDate", eTime));
        }
        if (status != null) {
            criterions.add(Restrictions.eq("status", status.intValue()));
        }
        criterions.add(Restrictions.ne("status", Global.DIVIDEND_BILL_UNAPPROVE));

        List<Order> orders = new ArrayList<>();
        orders.add(Order.desc("settleTime"));
        PageList pList = uDividendBillReadDao.search(criterions, orders, start, limit);
        pList = convertPageList(pList);
        return pList;
    }

    @Override
    public PageList searchByTeam(int[] userIds, String sTime, String eTime, Integer status, int start, int limit) {
        start = start < 0 ? 0 : start;
        limit = limit < 0 ? 0 : limit;
        limit = limit > 10 ? 10 : limit;

        PageList pList = uDividendBillReadDao.searchByTeam(userIds, sTime, eTime, status, start, limit);
        pList = convertPageList(pList);
        return pList;
    }

    @Override
    public PageList searchByTeam(int userId, String sTime, String eTime, Integer status, int start, int limit) {
        start = start < 0 ? 0 : start;
        limit = limit < 0 ? 0 : limit;
        limit = limit > 10 ? 10 : limit;

        PageList pList = uDividendBillReadDao.searchByTeam(userId, sTime, eTime, status, start, limit);
        pList = convertPageList(pList);
        return pList;
    }

    @Override
    public PageList searchByUserId(int userId, String sTime, String eTime, Integer status, int start, int limit) {
        start = start < 0 ? 0 : start;
        limit = limit < 0 ? 0 : limit;
        limit = limit > 10 ? 10 : limit;

        PageList pList = uDividendBillReadDao.searchByUserId(userId, sTime, eTime, status, start, limit);
        pList = convertPageList(pList);
        return pList;
    }

    // @Override
    // @Transactional(readOnly = true)
    // public PageList searchByZhuGuans(List<Integer> userIds, String sTime, String eTime, int start, int limit) {
    //     start = start < 0 ? 0 : start;
    //     limit = limit < 0 ? 0 : limit;
    //     limit = limit > 10 ? 10 : limit;
    //
    //     PageList pList = uDividendBillReadDao.searchByZhuGuans(userIds, sTime, eTime, start, limit);
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
    //     PageList pList = uDividendBillReadDao.searchByDirectLowers(upUserId, sTime, eTime, start, limit);
    //     pList = convertPageList(pList);
    //     return pList;
    // }

    private PageList convertPageList(PageList pList) {
        List<UserDividendBillVO> convertList = new ArrayList<>();
        if(pList != null && pList.getList() != null){
            for (Object tmpBean : pList.getList()) {
                convertList.add(new UserDividendBillVO((UserDividendBill) tmpBean, dataFactory));
            }
        }
        pList.setList(convertList);
        return pList;
    }
}
